/**
 * @CuisineQuiz.java
 *
 * The initial Quiz page that opens up after first sign in is in a Tinder swipe style activity to
 * rate cuisines. If users swipe left they dislike, and right for like. The results of this
 * quiz will allow us to set an initial preference for the user. After the user finishes swiping,
 * the results are recorded and saved to an Amazon web server where our MySQL database is located.
 *
 */

package fj.foodjunkies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import link.fls.swipestack.SwipeStack;

public class CuisineQuiz extends AppCompatActivity {

    private static final String defaultURL = "http://54.208.66.68:80/setdefaultRating.php";
    private static final String likeURL = "http://54.208.66.68:80/likeCuisine.php";
    private static final String dislikeURL = "http://54.208.66.68:80/dislikeCuisine.php";

    private RequestQueue requestQueue;
    private StringRequest request;
    private String userID;

    private ArrayList<String> cuisineStack;
    private ArrayList<Integer> images;
    private boolean [] preference;

    private ImageView like;
    private ImageView dislike;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cuisine_quiz);
        ActionBar actionBar = getSupportActionBar(); //Set back button on the title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Cuisine Quiz");

        like = (ImageView) findViewById(R.id.heart);
        dislike = (ImageView) findViewById(R.id.ximage);
        like.setVisibility(View.INVISIBLE);
        dislike.setVisibility(View.INVISIBLE);

        //Get current userID
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        //Set up volley
        requestQueue = Volley.newRequestQueue(this);

        //Run the function to set default ratings
        setDefaultRating();

        //Create a list of cuisines for the user to like/dislike
        cuisineStack = new ArrayList<String>();
        cuisineStack.add("Italian");
        cuisineStack.add("Spanish");
        cuisineStack.add("Chinese");
        cuisineStack.add("Indian");
        cuisineStack.add("Japanese");
        cuisineStack.add("Mediterranean");
        cuisineStack.add("Middle Eastern");
        cuisineStack.add("American");
        cuisineStack.add("Korean");
        cuisineStack.add("European");

        //Create an ArrayList to hold the resource id of the images to be loaded corresponding to the cuisines
        images = new ArrayList<Integer>();
        images.add(R.drawable.italian);
        images.add(R.drawable.spanish);
        images.add(R.drawable.chinese);
        images.add(R.drawable.indian);
        images.add(R.drawable.japanese);
        images.add(R.drawable.mediterranean);
        images.add(R.drawable.middleeastern);
        images.add(R.drawable.american);
        images.add(R.drawable.korean);
        images.add(R.drawable.european);

        //Create an array of booleans that stores if the user liked or disliked the cuisine
        preference = new boolean [10];

        //Initialize a SwipeStack object which is the set of cards
        SwipeStack swipeStack = (SwipeStack) findViewById(R.id.swipeStack);
        swipeStack.setAdapter(new SwipeStackAdapter(cuisineStack, images));

        //Create a listener to save if the user liked or disliked a cuisine
        SwipeStack.SwipeStackListener listener = new SwipeStack.SwipeStackListener() {
            @Override
            public void onViewSwipedToLeft(int position) {
                //Make the dislike image appear and disappear
                dislike.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        dislike.setVisibility(View.INVISIBLE);
                    }
                }, 600);
                //On left swipe, set cuisine to false to indicate dislike
                preference[position]=false;
            }

            @Override
            public void onViewSwipedToRight(int position) {
                //Make the like image appear and disappear
                like.setVisibility(View.VISIBLE);
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        like.setVisibility(View.INVISIBLE);
                    }
                }, 600);
                //On right swipe, set cuisine to true to indicate like
                preference[position]=true;
            }

            @Override
            public void onStackEmpty() {
                //Save the user's preferences into the MySQL server
                //Parse through the array of likes/dislikes and save them to the MySQL server
                for (int i=0; i<10; i++) {
                    if (preference[i]==true){ //If the user liked the cuisine
                        String likeCuisineID = String.valueOf(i+1); //The MySQL sever starts at an index of 1, so increment
                        setLikeRating(likeCuisineID);
                    }
                    else { //If the user disliked the cuisine
                        String dislikeCuisineID = String.valueOf(i+1);
                        setDislikeRating(dislikeCuisineID);
                    }
                }
                startActivity(new Intent(getApplicationContext(), fj.foodjunkies.Constraints.class));
            }
        };
        swipeStack.setListener(listener);
    }


    //Swipe Stack Adapter class to set the views for the cards
    public class SwipeStackAdapter extends BaseAdapter {

        private ArrayList<String> mData;
        private ArrayList<Integer> mImage;

        public SwipeStackAdapter(ArrayList<String> data, ArrayList<Integer> images) {
            this.mData = data;
            this.mImage = images;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public String getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            convertView = getLayoutInflater().inflate(R.layout.card, parent, false);
            TextView textViewCard = (TextView) convertView.findViewById(R.id.textViewCard);
            textViewCard.setText(mData.get(position));
            ImageView cuisineBackground = (ImageView) convertView.findViewById(R.id.cuisineBackground);
            cuisineBackground.setImageResource(mImage.get(position));

            return convertView;
        }
    }

    //If the user disliked a cuisine then send a POST request to the MySQL server to update values
    public void setDislikeRating(final String cusID){

        request = new StringRequest(Request.Method.POST, dislikeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("User_ID", userID);
                hashMap.put("Cus_ID", cusID);

                return hashMap;
            }
        };

        requestQueue.add(request);
    }

    //If the user liked a cuisine then send a POST request to the MySQL server to update values
    public void setLikeRating(final String cusID){

        request = new StringRequest(Request.Method.POST, likeURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }


            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("User_ID", userID);
                hashMap.put("Cus_ID", cusID);

                return hashMap;
            }
        };

        requestQueue.add(request);
    }

    //Set the default rating for the user in the MySQL server
    public void setDefaultRating(){

        request = new StringRequest(Request.Method.POST, defaultURL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();
                hashMap.put("User_ID", userID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    //Go back to the previous activity on back arrow press
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(CuisineQuiz.this, Welcome.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}

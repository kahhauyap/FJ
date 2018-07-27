/**
 * @Recommend.java
 *
 * This activity generates a dish recommendation based on the algorithm and user preference.
 * The initial values for the user is determined during the CuisineQuiz to gauge the user's preference.
 * Afterwards as the user continues to use the app the score of cuisines rises or drops depending based
 * on the user rating the experience. There is a button to dislike a recommended dish, request a new
 * recommendation, or to view restaurants serving the dish.
 *
 */

package fj.foodjunkies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ImageView;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


public class Recommend extends AppCompatActivity {

    RequestQueue requestQueue;

    //URLS to php scripts
    String getRatings = "http://54.208.66.68:80/getRatings.php";
    String getDishes = "http://54.208.66.68:80/getDishes.php";
    String getDishName = "http://54.208.66.68:80/getDishName.php";
    String dislike_Dish = "http://54.208.66.68:80/dislikeDish.php";
    String dislike_CuisineDish = "http://54.208.66.68:80/dislikeCuisineDish.php";

    Button dislike;
    Button seeRestaurants;
    Button reRoll;
    TextView showName;
    ImageView dishImage;
    private StringRequest request;
    private String dishURL;
    private String userID;
    //Stores dishes of picked cuisine
    private Integer[] dishID = new Integer[10];
    //Stores users ratings
    private Integer[] cusRatings = new Integer[10];
    private Random generator = new Random();

    //Specific Dish recommendation, Dish name
    String recName;

    //Following are stored as strings due to how volley sends parameters:

    //Specific Dish recommendation, Dish_ID
    String recommendationID;
    //Specific Cuisine recommendation, Cus_ID
    String cusID;

    private Context context = Recommend.this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommend);
        ActionBar actionBar = getSupportActionBar(); //Set back button on the title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Recommendations");

        dislike = (Button) findViewById(R.id.button8);
        showName = (TextView) findViewById(R.id.Namehere);
        reRoll = (Button) findViewById(R.id.button5);
        dishImage = findViewById(R.id.imageView5);
        seeRestaurants = (Button) findViewById(R.id.button7);

        dishImage.setVisibility(View.INVISIBLE);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        requestQueue = Volley.newRequestQueue(this);

        //Gets ratings from database, stores in cusRatings[]
        //Picks a cuisine and gets its dishes, puts dish IDs in dishID[]
        //Recommends dish
        //Puts Cus_ID in cusID and recommended Dish_ID in recommendationID
        initialRecommendation();


        System.out.println("Check cusRatings: " +cusRatings[1]);

        //Button, on click picks a cuisine and gets its dishes, puts dish IDs in dishID[]
        //Puts Cus_ID in cusID and recommended Dish_ID in recommendationID
        reRoll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                furtherRecommendation();

            }
        });


        //Button, on click goes to restaurants
        seeRestaurants.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //This goes to restaurants page yo
                Intent intent = new Intent(getApplicationContext(), SelectRestaurant.class);
                intent.putExtra("RECOMMEND", recName);
                startActivity(intent);
            }
        });

        //Button, on click dislikes Dish
        dislike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                dislikeDish();

            }
        });



    }

    //Algorithm for choosing a cuisine
    //Takes an array of user cuisine ratings
    //Returns cuisine ID
    public Integer pickCuisine(Integer[] x){
        Integer[] a = x;
        int sum = 0;
        for(int i: a)
            sum += i;
        Random r = new Random();
        int s = r.nextInt(sum);  //Get selection position (not array index)

        //Find position in the array:
        int prev_value = 0;
        int current_max_value = 0;
        int found_index = -1;
        for(int i=0; i< a.length; i++){ //walk through the array
            current_max_value = prev_value + a[i];
            //is between beginning and end of this array index?
            boolean found = (s >= prev_value && s < current_max_value)? true : false;
            if( found ){
                found_index = i+1;
                break;
            }
            prev_value = current_max_value;
        }

        return found_index;

    }

    //Gets cuisine ratings of current user
    //Results are stored in cusRatings array
    //Picks cuisine to be recommended, stores the cuisine ID in cusID
    //Makes a call to get specific dish recommendation
    public void initialRecommendation (){
        request = new StringRequest(Request.Method.POST, getRatings, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Creating JsonObject from response String
                    JSONObject jsonObject= new JSONObject(response.toString());

                    JSONArray items = jsonObject.getJSONArray("products");


                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);

                        String rating = item.getString("Rating");

                        //Change to int
                        Integer b = Integer.valueOf(rating);
                        //Store in cusRatings
                        cusRatings[i] = b;


                    }

                    furtherRecommendation();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("User_ID",userID);

                return hashMap;
            }
        };
        requestQueue.add(request);



    }

    //Gets dishes from a specified cuisine (cuisine ID is stored in class variable "cusID")
    //Results are stored in dishID array
    //Then selects a random item from this array
    //The result is the recommendation and its id is stored in recommendationID
    //Last, a call is made to get its name and display the dishes image
    public void getDishes(){

        request = new StringRequest(Request.Method.POST, getDishes, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Creating JsonObject from response String
                    JSONObject jsonObject= new JSONObject(response.toString());

                    JSONArray items = jsonObject.getJSONArray("products");

                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);

                        String dishIDHere = item.getString("Dish_ID");

                        //Change to int
                        Integer d = Integer.valueOf(dishIDHere);
                        //Store in dishID
                        dishID[i] = d;





                    }

                    System.out.println("Check if dishes loaded: " +dishID[1]);

                    Integer randomIndex = generator.nextInt(dishID.length);
                    System.out.println("Check randomIndex: " +randomIndex);
                    Integer temp = dishID[randomIndex];
                    String toString = String.valueOf(temp);
                    recommendationID = toString;


                    System.out.println("Check recommendationID: " +recommendationID);
                    getName();


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("Cus_ID",cusID);

                return hashMap;
            }
        };
        requestQueue.add(request);


    }


    public void getName(){
        request = new StringRequest(Request.Method.POST, getDishName, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Creating JsonObject from response String
                    JSONObject jsonObject= new JSONObject(response.toString());

                    JSONArray names = jsonObject.getJSONArray("products");

                    for (int i = 0; i < names.length(); i++) {
                        JSONObject name = names.getJSONObject(i);

                        String recommend = name.getString("Name");
                        String dishURLhere = name.getString("Url");

                        dishURL = dishURLhere;

                        recName = recommend;
                    }

                    System.out.println("Check recName: " +recName);
                    System.out.println("Check dishURL: " +dishURL);

                    showName.setText(recName);

                    Glide.with(context).load(dishURL).into(dishImage);
                    dishImage.setVisibility(View.VISIBLE);

                    SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();
                    editor.putString("cusID", cusID );
                    editor.putString("dishID", recommendationID);
                    editor.apply();

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) { }
        })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String,String> hashMap = new HashMap<String, String>();
                hashMap.put("Dish_ID",recommendationID);

                return hashMap;
            }
        };
        requestQueue.add(request);


    }


    public void dislikeDish(){

        request = new StringRequest(Request.Method.POST, dislike_Dish, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {
                        dislikeDishCuisine();
                        System.out.println("SUCCESS " + jsonObject.getString("success"));
                    }
                    if (jsonObject.names().get(0).equals("fail")) {
                        System.out.println("Fail " + jsonObject.getString("fail"));
                    }

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
                hashMap.put("Dish_ID", recommendationID);

                return hashMap;
            }
        };

        requestQueue.add(request);
    }

    public void dislikeDishCuisine(){

        request = new StringRequest(Request.Method.POST, dislike_CuisineDish, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.names().get(0).equals("success")) {
                        System.out.println("SUCCESS " + jsonObject.getString("success"));
                        furtherRecommendation();
                    }
                    if (jsonObject.names().get(0).equals("fail")) {
                        System.out.println("Fail " + jsonObject.getString("fail"));
                    }

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

    public void furtherRecommendation(){

        String test;
        Integer temp;
        temp = pickCuisine(cusRatings);
        test = String.valueOf(temp);
        System.out.println("Check cusID:" + test);
        cusID = test;

        getDishes();

    }

    //Go back to the previous activity on back arrow press
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(Recommend.this, Welcome.class);
        startActivityForResult(myIntent, 0);
        return true;
    }



}

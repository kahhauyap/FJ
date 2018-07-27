/**
 * @RateQuestions.java
 *
 * This page is opened upon selecting a dish to rate from the RatePage activity. This page will
 * display two questions: "Did you like the restaurant?", and "Did you like the dish?".
 * Upon pressing the like/dislike button, the data is saved into the MySQL server.
 * This information will be used by the algorithm to adjust the user preference.
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

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class RateQuestions extends AppCompatActivity {

    //Php urls to access the Amazon MySQL database
    String unsave_Rest = "http://54.208.66.68:80/deleteRest.php";
    String dislike_Rest = "http://54.208.66.68:80/dislikeRest.php";
    String like_Rest = "http://54.208.66.68:80/likeRest.php";
    String like_Dish = "http://54.208.66.68:80/likeDish.php";
    String dislike_Dish = "http://54.208.66.68:80/dislikeDish.php";
    String like_CuisineDish = "http://54.208.66.68:80/likeCuisineDish.php";
    String dislike_CuisineDish = "http://54.208.66.68:80/dislikeCuisineDish.php";

    private TextView restaurantText;
    private TextView dishText;
    private Button restaurantYes;
    private Button restaurantNo;
    private Button dishYes;
    private Button dishNo;
    private Button doneButton;

    private String userID;
    private String restaurantName;
    private String restaurantID;
    private String dishID;
    private String cusID;
    private String dishName;

    //Volley stuff
    private StringRequest request;
    RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate_questions);

        ActionBar actionBar = getSupportActionBar(); //Set back button on the title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Rate");

        requestQueue = Volley.newRequestQueue(this); //Initialize volley

        restaurantText = (TextView) findViewById(R.id.textRestaurant);
        dishText = (TextView) findViewById(R.id.textCuisine);
        restaurantYes = (Button) findViewById(R.id.yesButton1);
        restaurantNo = (Button) findViewById(R.id.noButton1);
        dishYes = (Button) findViewById(R.id.yesButton2);
        dishNo = (Button) findViewById(R.id.noButton2);
        doneButton = (Button) findViewById(R.id.doneButton);

        //Get the current user ID
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        //Get the information on the cuisine and restaurant passed in from the History List
        Intent intent = getIntent();
        restaurantName = intent.getStringExtra("restaurantName");
        dishID = intent.getStringExtra("dishID");
        cusID = intent.getStringExtra("cusID");
        restaurantID = intent.getStringExtra("restID");
        dishName = intent.getStringExtra("dishName");

        //Set the text for the restaurant
        restaurantText.setText(restaurantName);

        //Set the text for the dish
        dishText.setText(dishName);

        //Yes button if the user liked the restaurant
        restaurantYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeRest();
            }
        });
        //No button if the user disliked the restaurant
        restaurantNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dislikeRest();
            }
        });

        //Yes button if the user liked the dish
        dishYes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                likeDish(); //Raise the rating for the cuisine
            }
        });
        //No button if the user disliked the dish
        dishNo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dislikeDish(); //Lower the rating for the cuisine
            }
        });

        //After pressing the done button
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                unsaveRest(); //Un-bookmark the restaurant after rating it
                startActivity(new Intent(getApplicationContext(), fj.foodjunkies.Welcome.class)); //Go back to homepage
            }
        });
    }
    public void dislikeDish(){

        request = new StringRequest(Request.Method.POST, dislike_Dish, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {
                        dislikeCuisineDish();
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
                hashMap.put("Dish_ID", dishID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }
    public void dislikeCuisineDish(){

        request = new StringRequest(Request.Method.POST, dislike_CuisineDish, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.names().get(0).equals("success")) {
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
                hashMap.put("Cus_ID", cusID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public void likeDish(){

        request = new StringRequest(Request.Method.POST, like_Dish, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {
                        likeCuisineDish();
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
                hashMap.put("Dish_ID", dishID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public void likeCuisineDish(){

        request = new StringRequest(Request.Method.POST, like_CuisineDish, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.names().get(0).equals("success")) {
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
                hashMap.put("Cus_ID", cusID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public void unsaveRest(){

        request = new StringRequest(Request.Method.POST, unsave_Rest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.names().get(0).equals("success")) {
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
                hashMap.put("Rest_ID", restaurantID);
                hashMap.put("Dish_ID", dishID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public void likeRest(){

        request = new StringRequest(Request.Method.POST, like_Rest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {
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
                hashMap.put("Rest_ID", restaurantID);
                hashMap.put("Dish_ID", dishID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public void dislikeRest(){

        request = new StringRequest(Request.Method.POST, dislike_Rest, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {
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
                hashMap.put("Rest_ID", restaurantID);
                hashMap.put("Dish_ID", dishID);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    //Go back to the previous activity on back arrow press
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(RateQuestions.this, RatePage.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}

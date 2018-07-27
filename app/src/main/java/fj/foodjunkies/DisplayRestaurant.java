/**
 * @DisplayRestaurant.java
 *
 * This page is opened after the SelectRestaurant page with information on the selected restaurant
 * passed in. This page is for displaying more detailed information and an image of the restaurant.
 * After the user is finished there is a button to save the restaurant into the history of the user.
 * The restaurant is saved to the MySQL database.
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
import android.widget.ImageView;
import android.widget.TextView;

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

public class DisplayRestaurant extends AppCompatActivity {

    //Php scripts to query the MySQL database
    String get_RestID = "http://54.208.66.68:80/getRestID.php";
    String save_Rest = "http://54.208.66.68:80/saveRest.php";
    String add_Restaurant = "http://54.208.66.68:80/addRestaurant.php";

    String dishID, userID, Name, Address, cusID;

    private String restaurantName;
    private String address;
    private String phoneNumber;
    private String price;
    private String imageURL;
    private String food;
    private String distance;
    private String rating;
    private ImageView restaurantImage;

    private TextView textViewName;
    private TextView textViewAddress;
    private TextView textViewPhone;
    private TextView textViewPrice;
    private TextView textViewDistance;
    private TextView textViewRating;
    private Button buttonSave;

    //Volley stuff
    private StringRequest request;
    private RequestQueue requestQueue;
    private String restaurantID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_restaurant);
        ActionBar actionBar = getSupportActionBar(); //Set back button on the title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Additional details..."); //Set title to the restaurant name
        requestQueue = Volley.newRequestQueue(this);

        restaurantImage = (ImageView) findViewById(R.id.restaurantImage);

        //Get restaurant information from the previous SelectRestaurant activity by extracting extras from Intent
        Intent intent = getIntent();
        restaurantName = intent.getStringExtra("name");
        address = intent.getStringExtra("address");
        phoneNumber = intent.getStringExtra("phone");
        price = intent.getStringExtra("price");
        distance = intent.getStringExtra("distance"); //Convert the string values to doubles
        rating = intent.getStringExtra("rating");
        imageURL = intent.getStringExtra("imageURL");
        food = intent.getStringExtra("food");

        textViewName = (TextView) findViewById(R.id.textViewName);
        textViewName.setText(restaurantName);
        textViewAddress = (TextView) findViewById(R.id.textViewAddress);
        textViewAddress.append(address);
        textViewPhone = (TextView) findViewById(R.id.textViewPhone);
        textViewPhone.append(phoneNumber);
        textViewPrice = (TextView) findViewById(R.id.textViewPrice);
        textViewPrice.append(price);
        textViewDistance = (TextView) findViewById(R.id.textViewDistance);
        textViewDistance.append(distance + " miles");
        textViewRating = (TextView) findViewById(R.id.textViewRating);
        textViewRating.append(rating);

        //Upon pressing the save button, the restaurant will be saved to both history and ratings page
        buttonSave = (Button) findViewById(R.id.buttonSave);
        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //Get the relevant information from shared preference for saving
                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                dishID = sharedPref.getString("dishID", "");
                cusID = sharedPref.getString("cusID", "");
                userID = sharedPref.getString("userID", "");

                Address = address;
                Name = restaurantName;

                addRestaurant(); //Add the restaurant to the MySQL server to be saved as a bookmarked restaurant
                startActivity(new Intent(getApplicationContext(), Welcome.class)); //Open up the rating page
            }
        });

        Glide.with(this).load(imageURL).into(restaurantImage);
    }

    //Save the restaurant to the MySQL database
    public void saveRest(){

        request = new StringRequest(Request.Method.POST, save_Rest, new Response.Listener<String>() {
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
    public void getRestID(){

        request = new StringRequest(Request.Method.POST, get_RestID, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject= new JSONObject(response.toString());

                    JSONArray names = jsonObject.getJSONArray("products");

                    for (int i = 0; i < 1; i++) {
                        JSONObject name = names.getJSONObject(i);

                        String recommend = name.getString("Rest_ID");

                        restaurantID = recommend;

                    }

                    saveRest();


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
                hashMap.put("Name", Name);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    public void addRestaurant(){

        request = new StringRequest(Request.Method.POST, add_Restaurant, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);


                    if (jsonObject.names().get(0).equals("success")) {
                        getRestID();
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
                hashMap.put("Name", Name);
                hashMap.put("Address", Address);

                return hashMap;
            }
        };
        requestQueue.add(request);
    }

    //Go back to the previous activity on back arrow press
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(DisplayRestaurant.this, SelectRestaurant.class);
        myIntent.putExtra("RECOMMEND", food);
        startActivityForResult(myIntent, 0);
        return true;
    }
}

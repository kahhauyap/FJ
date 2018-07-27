/**
 * @History.java
 *
 * This page loads from the MySQL server the past restaurants that the user has been to, and displays
 * the restaurant name, along with the dish they ate in a ListView.
 *
 */

package fj.foodjunkies;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class History extends AppCompatActivity {

    private String get_History = "http://54.208.66.68:80/getHistory.php";

    private String userID; //Current user
    private ListView historyList;
    
    //Initialize the ArrayList to store information
    private ArrayList <String> displayList = new ArrayList<String>();

    //Volley stuff
    private StringRequest request;
    RequestQueue requestQueue;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        ActionBar actionBar = getSupportActionBar(); //Set back button on the title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("History");

        //Get the current user ID
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        requestQueue = Volley.newRequestQueue(this);

        historyList = (ListView) findViewById(R.id.rateList);

        getHistory();
    }

    public void getHistory (){
        request = new StringRequest(Request.Method.POST, get_History, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    //Creating JsonObject from response String
                    JSONObject jsonObject= new JSONObject(response.toString());

                    JSONArray items = jsonObject.getJSONArray("products");
                    System.out.println(items.length());
                    for (int i = 0; i < items.length(); i++) {
                        JSONObject item = items.getJSONObject(i);

                        String namesT = item.getString("Name");
                        String foodName = item.getString("DishName");

                        displayList.add(namesT + " - " + foodName); //Add the restaurant name and dish to the ArrayList for display
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }

                //Bind the ArrayList to an Array Adapter for display after populating the ArrayList, and create a custom view with white text
                ArrayAdapter<String> restaurantAdapter = new ArrayAdapter<String>(History.this, android.R.layout.simple_list_item_1, displayList) {
                    @Override //This code just makes the text of the ArrayAdapter white
                    public View getView(int position, View convertView, ViewGroup parent) {
                        View view = super.getView(position, convertView, parent); // Get the Item from ListView
                        TextView tv = (TextView) view.findViewById(android.R.id.text1); // Initialize a TextView for ListView each Item
                        tv.setTextColor(Color.WHITE);// Set the text color of TextView to white
                        return view;
                    }
                };
                historyList.setAdapter(restaurantAdapter); //Bind the ArrayList to the adapter for display

                //When an item on the ListView is pressed, it will return the position which we use to determine the item pressed
                historyList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    }
                });
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
    //Go back to the previous activity on back arrow press
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(History.this, Welcome.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}

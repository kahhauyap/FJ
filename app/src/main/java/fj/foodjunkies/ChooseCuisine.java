package fj.foodjunkies;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ListView;
import android.widget.Toast;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ChooseCuisine extends AppCompatActivity {

    private RequestQueue requestQueue;
    private static final String URL = "http://54.208.66.68:80/setdefaultRating.php";
    private StringRequest request;
    private String userID;

    private ListView listView;
    private ArrayAdapter<String> adapter;
    ArrayList<String> arrayListCuisines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_cuisine);

        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        //set up volley
        requestQueue = Volley.newRequestQueue(this);

        //Run the function to set default ratings
        setDefaultRating();

        listView = (ListView) findViewById(R.id.listView);
        arrayListCuisines = new ArrayList<String>();

        String[] cuisines = new String[] {
                "American", "Chinese", "European", "Indian", "Italian", "Japanese", "Korean", "Mediterranean", "Middle Eastern", "Spanish"
        };

        Collections.addAll(arrayListCuisines, cuisines);
        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayListCuisines);
        listView.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        //Create the options menu. From here we can select the mode
        menu.add("Select None");
        menu.add("Select Single");
        menu.add("Select Multiple");
        menu.add("Select All");

        return true;
    }

    //5. Perform the action that user wants to perform e.g. Select multiple, select single, delete etc.
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        String title = (String) item.getTitle();

        //6. Choose the appropriate method for our option menu. The list view supports a variety of choice modes.

        if (title.equals("Select None")) {
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, arrayListCuisines);
            listView.setAdapter(adapter);
            // Nothing can be selected
            listView.setChoiceMode(ListView.CHOICE_MODE_NONE);
        }

        else if (title.equals("Select Single")) {
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_single_choice, arrayListCuisines);
            listView.setAdapter(adapter);

            // 7. Set choice mode on the list to the appropriate value
            listView.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        }

        else if (title.equals("Select Multiple")) {
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_multiple_choice, arrayListCuisines);
            listView.setAdapter(adapter);
            // we can select multiple item
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        }

        else if (title.equals("Select All")) {
            // same as multiple choice
            adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_multiple_choice, arrayListCuisines);
            listView.setAdapter(adapter);
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

            // select all items
            for (int itemPosition = 0; itemPosition < listView.getAdapter().getCount(); itemPosition++) {
                listView.setItemChecked(itemPosition, true);
            }

            // create an alert dialog
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(
                    this);
            alertDialogBuilder.setTitle("Confirm");
            alertDialogBuilder
                    .setMessage("Delete All?")
                    .setPositiveButton("Delete",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            SparseBooleanArray checked = listView.getCheckedItemPositions();

                            for (int i = listView.getCount() - 1; i >= 0; i--) {
                                if (checked.get(i) == true)
                                    arrayListCuisines.remove(i);
                            }
                            adapter.notifyDataSetChanged();
                            listView.clearChoices();
                        }
                    })
                    .setNegativeButton("Cancel",new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            dialog.cancel();
                        }
                    });
            AlertDialog alertDialog = alertDialogBuilder.create();
            alertDialog.show();
        }

        return true;
    }

    public void setDefaultRating(){

        request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.names().get(0).equals("success")) {
                        Toast.makeText(getApplicationContext(), "SUCCESS " + jsonObject.getString("success"), Toast.LENGTH_LONG).show();
                    }
                    if (jsonObject.names().get(0).equals("fail")) {
                        Toast.makeText(getApplicationContext(), "Fail " + jsonObject.getString("fail"), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Error" + jsonObject.getString("error"), Toast.LENGTH_LONG).show();
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

                return hashMap;
            }
        };

        requestQueue.add(request);
    }

    public void doneButtonClick(View view) {
        // Start the profile activity
        finish();
        startActivity(new Intent(getApplicationContext(), fj.foodjunkies.Constraints.class));
    }
}

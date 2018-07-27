package fj.foodjunkies;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.content.Context;
import android.content.SharedPreferences;
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
import java.util.HashMap;
import java.util.Map;

public class quiz extends AppCompatActivity {


    private RequestQueue requestQueue;
    private static final String URL = "http://54.208.66.68:80/setdefaultRating.php";
    private StringRequest request;
    private String userID;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz);


        //Get user ID from shared prefs
        SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
        userID = sharedPref.getString("userID", "");

        //set up volley
        requestQueue = Volley.newRequestQueue(this);

        //Run the function to set default ratings
        setDefaultRating();

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


}

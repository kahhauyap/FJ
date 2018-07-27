/**
 * @LoginActivity.java
 *
 * Login screen for the app, allows user to register and sign in. This activity will signal if
 * this is the first time the app has been opened to lead to the Quiz and Constraints. Otherwise,
 * it will lead to the homepage.
 *
 */

package fj.foodjunkies;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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
import android.content.Context;
import android.content.SharedPreferences;
import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private static final String URL = "http://54.208.66.68:80/user_control2.php";

    private EditText Email,Password;
    private Button sign_in_register;
    private Button register;
    private RequestQueue requestQueue;

    private StringRequest request;

    private Boolean firstTime = null;

    @Override
    protected void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        Email = (EditText) findViewById(R.id.email);
        Password = (EditText) findViewById(R.id.password);
        sign_in_register = (Button) findViewById(R.id.sign_in_register);
        register = (Button) findViewById(R.id.button6);

        requestQueue = Volley.newRequestQueue(this);

        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.names().get(0).equals("success")){

                                String temp = jsonObject.getString("success");

                                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("userID", temp);
                                editor.apply();

                                startActivity(new Intent(getApplicationContext(),Welcome.class));

                            } if(jsonObject.names().get(0).equals("created")) {
                                Toast.makeText(getApplicationContext(),"SUCCESS "+jsonObject.getString("created"),Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error" +jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("Email",Email.getText().toString());
                        hashMap.put("Password",Password.getText().toString());

                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });

        sign_in_register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                request = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jsonObject = new JSONObject(response);

                            if(jsonObject.names().get(0).equals("success")){

                                String temp = jsonObject.getString("success");

                                SharedPreferences sharedPref = getSharedPreferences("userInfo", Context.MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.putString("userID", temp);
                                editor.apply();

                                if (isFirstTime()){ //If it is the user's first time, send them to the quiz and constraints page
                                    startActivity(new Intent(getApplicationContext(), CuisineQuiz.class));
                                }
                                else { //If it is an existing user, go to the homepage
                                    startActivity(new Intent(getApplicationContext(), fj.foodjunkies.Welcome.class));
                                }

                            } if(jsonObject.names().get(0).equals("created")) {
                                Toast.makeText(getApplicationContext(),"SUCCESS "+jsonObject.getString("created"),Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(getApplicationContext(), "Error" +jsonObject.getString("error"), Toast.LENGTH_LONG).show();
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        HashMap<String,String> hashMap = new HashMap<String, String>();
                        hashMap.put("Email",Email.getText().toString());
                        hashMap.put("Password",Password.getText().toString());

                        return hashMap;
                    }
                };
                requestQueue.add(request);
            }
        });
    }

    //Check if it is the first time the user is using the app
    private boolean isFirstTime() {
        if (firstTime == null) {
            SharedPreferences mPreferences = this.getSharedPreferences("first_time", Context.MODE_PRIVATE);
            firstTime = mPreferences.getBoolean("firstTime", true);
            if (firstTime) {
                SharedPreferences.Editor editor = mPreferences.edit();
                editor.putBoolean("firstTime", false);
                editor.commit();
            }
        }
        return firstTime;
    }
}
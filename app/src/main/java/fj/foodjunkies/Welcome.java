/**
 * @Welcome.java
 *
 * The home screen page that users will be greeted with upon signing in. This page acts as the hub
 * of the app, and allows the user to reach every other page. On this page the user can access the
 * Recommendation, Search, Quiz, Constraints, History, Rating, pages.
 *
 */

package fj.foodjunkies;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageView;
import android.widget.TextView;

public class Welcome extends Activity {

    private Button buttonQuiz;
    private Button buttonSearch;
    private Button buttonRecommend;
    private Button buttonLogout;
    private Button buttonHistory;
    private Button buttonRate;
    private Button buttonConstraints;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);

        buttonQuiz = (Button) findViewById(R.id.buttonQuiz);
        buttonQuiz.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), fj.foodjunkies.CuisineQuiz.class));
            }
        });

        buttonSearch = (Button) findViewById(R.id.buttonSearch);
        buttonSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), fj.foodjunkies.Search.class));

            }
        });

        buttonRecommend = (Button) findViewById(R.id.buttonRecommend);
        buttonRecommend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), fj.foodjunkies.Recommend.class));
            }
        });

        buttonLogout = (Button) findViewById(R.id.buttonLogout);
        buttonLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), fj.foodjunkies.LoginActivity.class));
            }
        });

        buttonConstraints = (Button) findViewById(R.id.buttonConstraints);
        buttonConstraints.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), Constraints.class));
            }
        });

        buttonHistory = (Button) findViewById(R.id.buttonHistory);
        buttonHistory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), History.class));
            }
        });

        buttonRate = (Button) findViewById(R.id.buttonRate);
        buttonRate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), RatePage.class));
            }
        });
    }
}
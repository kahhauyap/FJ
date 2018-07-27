/**
 * @Search.java
 *
 * This page allows the user to search for a food or restaurant of their choice, the search text
 * is then sent to the SelectRestaurant activity for a list of restaurants.
 *
 */

package fj.foodjunkies;

import android.content.Intent;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

public class Search extends AppCompatActivity {

    private ImageButton searchButton;
    private EditText searchBar;
    private String searchText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        ActionBar actionBar = getSupportActionBar(); //Set back button on the title bar
        actionBar.setDisplayHomeAsUpEnabled(true);
        setTitle("Search");

        searchButton = (ImageButton) findViewById(R.id.searchButton);
        searchBar = (EditText) findViewById(R.id.editSearch);

        //When the search button is pressed the text from the EditText search bar field should be passed to the SelectRestaurant Activity for searching
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchText = searchBar.getText().toString(); //Get the text
                //Start the SelectRestaurant activity to display restaurants and pass the search term in
                Intent intent = new Intent(getApplicationContext(), SelectRestaurant.class);
                intent.putExtra("RECOMMEND", searchText);
                startActivity(intent);
            }
        });
    }

    //Go back to the previous activity on back arrow press
    public boolean onOptionsItemSelected(MenuItem item){
        Intent myIntent = new Intent(Search.this, Welcome.class);
        startActivityForResult(myIntent, 0);
        return true;
    }
}

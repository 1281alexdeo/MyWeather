package com.londonappbrewery.climapm;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

public class ChangeCityController extends AppCompatActivity {

    EditText mEditTextField;
    ImageButton mbackButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_city_layout);
        mEditTextField = (EditText) findViewById(R.id.queryET);
        mbackButton = (ImageButton) findViewById(R.id.backButton);

        mbackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mEditTextField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                String newCity = mEditTextField.getText().toString();//getting the text from the edit text view
                //setting up an intent to go from changeCityController -> WeatherController class
                Intent newCityIntent = new Intent(ChangeCityController.this,WeatherController.class);
                //attaching the text from the EditTextView to the intent "extra" to pass the text to
                // the WeatherController class from the changeCityActivity.
               newCityIntent.putExtra("City",newCity);
               startActivity(newCityIntent);
                return false;
            }
        });

    }
}

package com.example.joe.nflcrime;

import android.content.DialogInterface;
import android.os.AsyncTask;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    Button mButton;
    TextView mTextView;
    EditText mEditText;
    ProgressBar mLoading;
    int from = 0;
    String objectString;

    private static final String TAG = "NFL Arrest";
    private final CharSequence[] choices = {"Top Position","Team","Top Player", "Top Crimes"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mLoading = (ProgressBar)findViewById(R.id.progress);
        mEditText = (EditText)findViewById(R.id.name_of_player);
        mTextView = (TextView)findViewById(R.id.show_results);
        mButton = (Button)findViewById(R.id.search_button);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle("Search For... ")
                        .setSingleChoiceItems(choices, -1, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if (choices[which] == "Top Position"){
                                    from = 1;
                                    objectString = "Position";
                                }else if (choices[which] == "Team"){
                                    from = 2;
                                    objectString = "Team";
                                }else if (choices[which] == "Top Player"){
                                    from = 3;
                                    objectString = "Name";
                                }else if (choices[which] == "Top Crimes"){
                                    from = 4;
                                    objectString = "Category";
                                }
                            }
                        }).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (from == 0){
                            Toast.makeText(MainActivity.this, "Select one choice only", Toast.LENGTH_SHORT).show();
                        }else if (from == 1){
                            getTopPositionCrime();
                        }else if (from == 2){
                            getTeamCrime();
                        }else if (from == 3){
                            getTopPlayerCrime();
                        }else if (from == 4){
                            getTopCrimes();
                        }
                    }
                }).setNegativeButton(android.R.string.cancel, null)
                        .create()
                        .show();
            }
        });

    }
    public class requestCrimeData extends AsyncTask<String, Void, JSONObject>{

        @Override
        protected JSONObject doInBackground(String... urls){
            try{
                URL url = new URL(urls[0]);
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                InputStream responseStream = connection.getInputStream();

                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(responseStream));

                StringBuilder builder = new StringBuilder();

                String line;
                while ((line = bufferedReader.readLine()) != null){
                    Log.d(TAG, "line= " + line);
                    builder.append(line);
                }
                String responseString = builder.toString();
                Log.d(TAG, responseString);

                JSONObject json = new JSONObject(responseString);

                return json;

            }catch (Exception e){
                Log.e(TAG, "Error fetching arrest data", e);
                return null;
            }

        }
        protected void onPostExecute(JSONObject json){
            if (json != null){
                mLoading.setVisibility(ProgressBar.INVISIBLE);

                try{
                    if(json.getJSONObject(objectString).has("error")){
                        Log.e(TAG, "Error in response from NFLArrestAPI" + json.getJSONObject("response")
                        .getJSONObject("error")
                        .getString("description"));
                        return;
                    }

                    JSONObject topCrime = json.getJSONObject(objectString);

                    mTextView.setText(topCrime.toString());

                }catch (JSONException je){
                    Log.e(TAG, "JSON parsing error", je);
                }
            }
        }
    }

    private void getTopPositionCrime(){
        String baseURL = "http://www.NflArrest.com/api/v1/crime/topPositions/";
        String search = mEditText.getText().toString();
        String url = String.format("%1s%2s",baseURL, search);

        requestCrimeData tempTask = new requestCrimeData();
        tempTask.execute(url);

        mLoading.setVisibility(ProgressBar.VISIBLE);

    }

    private void getTeamCrime(){
        String baseURL = "http://www.NflArrest.com/api/v1/team/topCrimes/";
        String editText = mEditText.getText().toString();
        String url = String.format("%1s%2s",baseURL, editText);

        requestCrimeData tempTask = new requestCrimeData();
        tempTask.execute(url);

        mLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void getTopPlayerCrime(){
        String baseURL = "http://www.NflArrest.com/api/v1/player/";
//        String editText = mEditText.getText().toString();
//        String url = String.format("%1s%2s",baseURL, editText);

        requestCrimeData tempTask = new requestCrimeData();
        tempTask.execute(baseURL);

        mLoading.setVisibility(ProgressBar.VISIBLE);
    }

    private void getTopCrimes(){
        String baseURL = "http://www.NflArrest.com/api/v1/crime/";

        requestCrimeData tempTask = new requestCrimeData();
        tempTask.execute(baseURL);

        mLoading.setVisibility(ProgressBar.VISIBLE);
    }
}

package edu.scu.cheryl.yelpxxx;

import android.content.Intent;
import android.net.Uri;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;

public class ReviewActivity extends AppCompatActivity {

    Button submit;
    EditText review;
    String getReview;
    int id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_review);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Pley");
        actionBar.setSubtitle("Better than ever!");
        //actionBar.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.action_bar_background));
        actionBar.setIcon(R.drawable.ic_action_name);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);

        submit = (Button)findViewById(R.id.submitButton);
        review = (EditText)findViewById(R.id.reviewText);

        id = getIntent().getIntExtra("id", 0);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getReview = "" + review.getText().toString();
                Intent i = new Intent(ReviewActivity.this, DetailActivity.class);
                i.putExtra("id", id);
                i.putExtra("name", getIntent().getStringExtra("name"));
                i.putExtra("address",getIntent().getStringExtra("address"));
                i.putExtra("phone",getIntent().getStringExtra("phone"));
                i.putExtra("img",getIntent().getStringExtra("img"));
                i.putExtra("review", "1");
                // send review to database with para restName
                RequestParams params = new RequestParams();

                params.put("restaurant_id", id);
                params.put("review", getReview);
                String baseURL = "http://52.196.7.97:8080/db_rest/rest";
                AsyncHttpClient client = new AsyncHttpClient();
                client.post(baseURL + "/review", params, new JsonHttpResponseHandler() {

                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        System.out.println("post review successfull with response:" + response);
                        try {

                        } catch (Exception e) {
                        }
                        super.onSuccess(statusCode, headers, response);
                    }

                });
                startActivity(i);
            }
        });
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_information:
                Intent intent2 = new Intent(ReviewActivity.this, AboutUs.class);
                startActivity(intent2);
                break;
            case R.id.action_delete:
                Intent intent3= new Intent(Intent.ACTION_DELETE);
                intent3.setData(Uri.parse("package:edu.scu.cheryl.yelpxxx"));
                startActivity(intent3);
                break;
            case R.id.home:
                finish();
                return true;
            default:
                toast("unknown action ...");
        }

        return true;
    }
    private void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}


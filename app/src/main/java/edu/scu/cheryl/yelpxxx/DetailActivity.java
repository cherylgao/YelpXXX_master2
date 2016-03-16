package edu.scu.cheryl.yelpxxx;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.NavUtils;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.yelp.clientlib.connection.YelpAPI;
import com.yelp.clientlib.connection.YelpAPIFactory;
import com.yelp.clientlib.entities.Business;
import com.yelp.clientlib.entities.SearchResponse;
import com.yelp.clientlib.entities.options.CoordinateOptions;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Scanner;

import cz.msebera.android.httpclient.Header;
import retrofit.Call;
import retrofit.Response;

public class DetailActivity extends AppCompatActivity {

    private static final String CONSUMER_KEY = "rEl2e37-TQueqIpLeFNEGA";
    private static final String CONSUMER_SECRET = "2tfEDEf3aX2b93BJj86IQHPe270";
    private static final String TOKEN = "JrmitrWu9XVqy0namjf6PGLFZ_370LNc";
    private static final String TOKEN_SECRET = "Pse21d6tQkRqinuQUYnADDs-s3c";

    Call<SearchResponse> call;
    TextView restName;
    TextView restAddr;
    TextView phoneNum;
    ImageView restImage;
    ListView dishList;
    ListView reviewList;
    List<Business> restaurants;
    Button writeReview;
    int restaurant_id = -1;
    String activity;
    String img;
    String name;
    String address;
    String phone;
    ArrayAdapter<String> arrayAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        restName = (TextView)findViewById(R.id.restName);
        restAddr = (TextView)findViewById(R.id.restAddress);
        phoneNum = (TextView)findViewById(R.id.phoneNum);
        restImage = (ImageView)findViewById(R.id.restImage);
        writeReview = (Button)findViewById(R.id.writeReview);
        dishList = (ListView)findViewById(R.id.dishList);
        reviewList = (ListView)findViewById(R.id.reviewList);
        restaurants = new ArrayList<>();
        Intent i = getIntent();
        activity = i.getStringExtra("activity");
        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(DetailActivity.this, ReviewActivity.class);
                intent.putExtra("id", restaurant_id);
                intent.putExtra("name", name);
                intent.putExtra("address", address);
                intent.putExtra("img", img);
                intent.putExtra("phone", phone);
                startActivity(intent);
            }
        });

        //dataStore = context.getSharedPreferences(SP_NAME, 0);

        getLucky(activity, i);

        getList(activity, i);

        dishList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            // will have bug if the dish name contains ()
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String dish = parent.getItemAtPosition(position).toString();

                String name = "";
                for (int i = 0; i < dish.length(); i++) {
                    if (dish.charAt(i) == '(') {
                        name = dish.substring(0, i - 3);
                    }
                }
                RequestParams params = new RequestParams();
                params.put("name", name);
                params.put("restaurant_id", restaurant_id);
                String baseURL = "http://52.196.7.97:8080/db_rest/rest";
                AsyncHttpClient client = new AsyncHttpClient();

                client.post(baseURL + "/dish/", params, new JsonHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                        try {
                            System.out.println("get dish response:" + response);
                            String baseURL = "http://52.196.7.97:8080/db_rest/rest";
                            AsyncHttpClient client = new AsyncHttpClient();
                            client.get(baseURL + "/dish/" + restaurant_id, null, new JsonHttpResponseHandler() {
                                @Override
                                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                                    try {
                                        ArrayAdapter<String> dishAdapter;
                                        System.out.println("get dish response:" + response);
                                        String[] dishes = new String[response.length()];
                                        ArrayList<Dishes> vote = new ArrayList<Dishes>();
                                        for (int i = 0; i < response.length(); i++) {
                                            JSONObject jsonObject = response.getJSONObject(i);
//
                                            vote.add(new Dishes(jsonObject.getString("name"), jsonObject.getInt("voting")));
                                            //dishes[i] = jsonObject.getString("name");
                                        }
                                        Collections.sort(vote, new Comparator<Dishes>() {
                                            @Override
                                            public int compare(Dishes lhs, Dishes rhs) {
                                                return rhs.getVote() - lhs.getVote();
                                            }
                                        });
                                        for (int i = 0; i < dishes.length; i++) {
                                            dishes[i] = vote.get(i).getName() + "   (" + vote.get(i).getVote() + ")";
                                        }
                                        dishAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, dishes) {
                                            public View getView(int position, View convertView, ViewGroup parent) {
                                                View view = super.getView(position, convertView, parent);
                                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                                text.setTextColor(Color.BLACK);
                                                return view;
                                            }
                                        };
                                        dishList.setAdapter(dishAdapter);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                    super.onSuccess(statusCode, headers, response);
                                }

                            });
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    ;
                });
            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle("Pley");
        actionBar.setSubtitle("Better than ever!");
        //actionBar.setBackgroundDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.action_bar_background));
        actionBar.setIcon(R.drawable.ic_action_name);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowHomeEnabled(true);
        actionBar.setHomeButtonEnabled(true);
    }

    @Override
    protected void onResume() {
        super.onResume();
        name = getIntent().getStringExtra("name");
        address = getIntent().getStringExtra("address");
        phone = getIntent().getStringExtra("phone");
        restName.setText(name);
        restAddr.setText(address);
        img = getIntent().getStringExtra("img");
        phoneNum.setText(phone);
        if (getIntent().getStringExtra("review") != null) {
            try {
                DownloadImageTask task = new DownloadImageTask(restImage);
                task.execute(img);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        String baseURL = "http://52.196.7.97:8080/db_rest/rest";
        AsyncHttpClient client = new AsyncHttpClient();
        int candidateId = getIntent().getIntExtra("id", -1);
        restaurant_id = candidateId != -1 ? candidateId : restaurant_id;
        if (restaurant_id <= 0) {
            System.out.println(restaurant_id);
            getDishReview(name, address);
        } else {
            System.out.println("222222");
            client.get(baseURL + "/review/" + restaurant_id, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        System.out.println("get review response:" + response);
                        String[] review = new String[response.length()];
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
                            System.out.println("get review by restaurant_id - id: " + jsonObject.getInt("id") +
                                    ", restaurant_id: " + jsonObject.getString("restaurant_id") +
                                    ", review: " + jsonObject.getString("review"));
                            review[i] = jsonObject.getString("review");
                        }
                        arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, review) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };
                        reviewList.setAdapter(arrayAdapter);

                    } catch (Exception e) {
                        e.printStackTrace();
                    }


                    super.onSuccess(statusCode, headers, response);
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                    //AsyncHttpClient.log.w(LOG_TAG, "onFailure(int, Header[], Throwable, JSONObject) was not overriden, but callback was received", throwable);
                }
            });

            // get dishes
            client.get(baseURL + "/dish/" + restaurant_id, null, new JsonHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                    try {
                        ArrayAdapter<String> dishAdapter;
                        System.out.println("get dish response:" + response);
                        String[] dishes = new String[response.length()];
                        ArrayList<Dishes> vote = new ArrayList<Dishes>();
                        for (int i = 0; i < response.length(); i++) {
                            JSONObject jsonObject = response.getJSONObject(i);
//
                            vote.add(new Dishes(jsonObject.getString("name"), jsonObject.getInt("voting")));
                            //dishes[i] = jsonObject.getString("name");
                        }
                        Collections.sort(vote, new Comparator<Dishes>() {
                            @Override
                            public int compare(Dishes lhs, Dishes rhs) {
                                return rhs.getVote() - lhs.getVote();
                            }
                        });
                        for (int i = 0; i < dishes.length; i++) {
                            dishes[i] = vote.get(i).getName() + "   (" + vote.get(i).getVote() + ")";
                        }
                        dishAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, dishes) {
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = super.getView(position, convertView, parent);
                                TextView text = (TextView) view.findViewById(android.R.id.text1);
                                text.setTextColor(Color.BLACK);
                                return view;
                            }
                        };
                        dishList.setAdapter(dishAdapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    super.onSuccess(statusCode, headers, response);
                }

            });
        }
    }

    private void showDetail(Business b){
        restName.setText(b.name());
        String addr1 = "";
        String add = "";
        for(String addr: b.location().displayAddress()){
            addr1 += addr+" \n";
            add += add+" ";
        }
        address = addr1;
        name = b.name();

        phone = b.displayPhone();
        restAddr.setText(address);
        phoneNum.setText(b.displayPhone());
        restImage = (ImageView)findViewById(R.id.restImage);
        String path = b.imageUrl();
        img = path;
        try {

            DownloadImageTask task=new DownloadImageTask(restImage);
            task.execute(path);
        }catch (Exception e){
            e.printStackTrace();
        }

        // connect to database to get review and dish rank

        getDishReview(b.name(), add);
    }

    // connect to db and get review dish rank(get)
    private void getDishReview(final String name,final String address){
        // get restaurant id from rest api
        RequestParams params = new RequestParams();
        params.put("name", name);
        params.put("address", address);
        String baseURL = "http://52.196.7.97:8080/db_rest/rest";
        AsyncHttpClient client = new AsyncHttpClient();
        client.post(baseURL + "/restaurant", params, new JsonHttpResponseHandler() {
            // return restaurant
            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                System.out.println("post restaurant successfull with response:" + response);
                try {
                    restaurant_id = response.getInt("id");

                    //get review
                    AsyncHttpClient client = new AsyncHttpClient();
                    String baseURL = "http://52.196.7.97:8080/db_rest/rest";
                    client.get(baseURL + "/review/" + restaurant_id, null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            try {
                                System.out.println("get review response:" + response);
                                String[] review = new String[response.length()];
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
                                    System.out.println("get review by restaurant_id - id: " + jsonObject.getInt("id") +
                                            ", restaurant_id: " + jsonObject.getString("restaurant_id") +
                                            ", review: " + jsonObject.getString("review"));
                                    review[i] = jsonObject.getString("review");
                                }
                                arrayAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, review){
                                    public View getView(int position, View convertView, ViewGroup parent){
                                        View view = super.getView(position, convertView, parent);
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                                reviewList.setAdapter(arrayAdapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            super.onSuccess(statusCode, headers, response);
                        }

                    });

                    // get dishes
                    client.get(baseURL + "/dish/" + restaurant_id, null, new JsonHttpResponseHandler() {
                        @Override
                        public void onSuccess(int statusCode, Header[] headers, JSONArray response) {
                            try {
                                ArrayAdapter<String> dishAdapter;
                                System.out.println("get dish response:" + response);
                                String[] dishes = new String[response.length()];
                                ArrayList<Dishes> vote = new ArrayList<Dishes>();
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject jsonObject = response.getJSONObject(i);
//
                                    vote.add(new Dishes(jsonObject.getString("name"), jsonObject.getInt("voting")));
                                    //dishes[i] = jsonObject.getString("name");
                                }
                                Collections.sort(vote, new Comparator<Dishes>() {
                                    @Override
                                    public int compare(Dishes lhs, Dishes rhs) {
                                        return rhs.getVote()-lhs.getVote();
                                    }
                                });
                                for(int i = 0; i<dishes.length;i++){
                                    dishes[i] = vote.get(i).getName() + "   ("+vote.get(i).getVote() +")";
                                }
                                dishAdapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, dishes){
                                    public View getView(int position, View convertView, ViewGroup parent){
                                        View view = super.getView(position, convertView, parent);
                                        TextView text = (TextView) view.findViewById(android.R.id.text1);
                                        text.setTextColor(Color.BLACK);
                                        return view;
                                    }
                                };
                                dishList.setAdapter(dishAdapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                            super.onSuccess(statusCode, headers, response);
                        }

                    });
                } catch (Exception e) {
                }
                super.onSuccess(statusCode, headers, response);
            }

        });

    }

    //from lucky
    //1.get intent
    //2.make yelp search according to intent data
    //3.randomly pick one and show result
    private void getLucky(String activity,Intent i){
        if(activity != null && activity.equals("lucky")){
            GetRestAsync g = new GetRestAsync(i);
            g.execute();
        }
    }

    private void getList(String activity, Intent i){
        if(activity != null && activity.equals("list")){
            name = getIntent().getStringExtra("name");
            img = getIntent().getStringExtra("img");
            address = getIntent().getStringExtra("address");
            phone = getIntent().getStringExtra("phone");
            restName.setText(name);
            restAddr.setText(address);
            phoneNum.setText(phone);

            try {
                DownloadImageTask task=new DownloadImageTask(restImage);
                task.execute(img);
                getDishReview(name, address);
            }catch (Exception e){
                e.printStackTrace();
            }
        }

    }

    public class GetRestAsync extends AsyncTask<Void,Void,String> {
        List<Business> rest;
        Intent i;
        public GetRestAsync(Intent i){
            this.rest = new ArrayList<>();
            this.i = i;
        }

        protected String doInBackground(Void... params) {
            String radius = i.getStringExtra("radius");
            String term = i.getStringExtra("term");
            double latitude=getIntent().getDoubleExtra("latitude", 0.0);
            double longitude=getIntent().getDoubleExtra("longitude", 0.0);
            Scanner readLine = new Scanner(term);
            //dataStore.edit().clear().commit();
            YelpAPIFactory apiFactory = new YelpAPIFactory(CONSUMER_KEY, CONSUMER_SECRET, TOKEN, TOKEN_SECRET);
            YelpAPI yelpAPI = apiFactory.createAPI();
            Map<String, String> para = new HashMap<>();
            para.put("radius_filter", radius);
            para.put("lang", "fr");
            para.put("limit", "5");

            while(readLine.hasNext()) {
                para.put("term", readLine.next() + " food");

                try {
                    CoordinateOptions coordinate = CoordinateOptions.builder()
                            .latitude(latitude).longitude(longitude).build();
                    call = yelpAPI.search(coordinate, para);
                    Response<SearchResponse> response = call.execute();
                    rest.addAll(response.body().businesses());
//                    for (Business b : response.body().businesses()) {
//                        rest.add(b);
//                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return "s";
        }

        @Override
        protected void onPostExecute(String s) {
            Random rand = new Random();
            int r = rand.nextInt(rest.size());
            if(rest.size()!=0) {
                showDetail(rest.get(r));
            }
        }
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
                Intent intent2 = new Intent(DetailActivity.this, AboutUs.class);
                startActivity(intent2);
                break;
            case R.id.action_delete:
                Intent intent3= new Intent(Intent.ACTION_DELETE);
                intent3.setData(Uri.parse("package:edu.scu.cheryl.yelpxxx"));
                startActivity(intent3);
                break;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
            default:
                //toast("unknown action ...");
                Intent intent = new Intent(DetailActivity.this, MainActivity.class);
                startActivity(intent);
                break;
        }

        return true;
    }
    private void toast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
    }
}

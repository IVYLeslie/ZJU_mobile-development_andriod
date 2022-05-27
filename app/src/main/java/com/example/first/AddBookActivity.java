package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.URL;

public class AddBookActivity extends AppCompatActivity {

    private String isbn;
    private String address = "https://api.jike.xyz/situ/book/isbn/";
    private String apikey = "?apikey=12718.2336c4d830e490842883539cad5a9668.d596f160ca12154d2fa00b1aef5c9045";
    private ImageView image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);
        TextView error=findViewById(R.id.error);
        Button button_addbook_find=findViewById(R.id.button_addbook_find);
        Button button_addbook_add=findViewById(R.id.button_addbook_add);
        EditText input_ISBN=findViewById(R.id.input_ISBN);
        TextView name=findViewById(R.id.text_bookname_result);
        TextView author=findViewById(R.id.text_author_result);
        TextView publish=findViewById(R.id.text_publish_result);
        TextView year=findViewById(R.id.text_publishyear_result);
        image = findViewById(R.id.image);
        image.setImageResource(R.drawable.ivy);

        button_addbook_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isbn=input_ISBN.getText().toString();
                System.out.println(isbn);
                //isbn="9787806767245";
                String url = address+isbn+apikey;
                System.out.println(url);
                RequestQueue queues = Volley.newRequestQueue(AddBookActivity.this);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET,
                        url, null, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("json data===" + response);
                        JSONObject context = null;
                        try {
                            context = response.getJSONObject("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            error.setText(response.getString("msg")+" !");
                            name.setText(context.getString("name"));
                            author.setText(context.getString("author"));
                            publish.setText(context.getString("publishing"));
                            year.setText(context.getString("published"));
                            button_addbook_add.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            new ImageDownloadTask().execute(context.getString("photoUrl"));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener(){
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("发生了一个错误！");
                        error.printStackTrace();
                    }
                });
                queues.add(jsonObjectRequest);

            }
        });
        button_addbook_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences.Editor editor = getSharedPreferences("data", MODE_PRIVATE).edit();
            }
        });

    }

    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            try {
                URL imageUrl = new URL(url);
                InputStream is = imageUrl.openConnection().getInputStream();
                Bitmap imageBitmap = BitmapFactory.decodeStream(is);
                return imageBitmap;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected void onPostExecute(Bitmap result) {
            if (result != null) {
                image.setImageBitmap(result);
            }
        }
    }


}
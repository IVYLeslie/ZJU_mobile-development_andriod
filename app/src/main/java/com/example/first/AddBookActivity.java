package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;

public class AddBookActivity extends AppCompatActivity {

    private String isbn;
    private String address = "https://api.jike.xyz/situ/book/isbn/";
    private String apikey = "?apikey=12718.2336c4d830e490842883539cad5a9668.d596f160ca12154d2fa00b1aef5c9045";
    private ImageView image;
    private Bitmap imageBitmap;
    private JSONObject result = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);
        TextView error=findViewById(R.id.error);
        Button button_addbook_find=findViewById(R.id.button_addbook_find);
        Button button_addbook_add=findViewById(R.id.button_addbook_add);
        Button add_to_list = findViewById(R.id.addbook_to_listbook);
        Button add_to_main = findViewById(R.id.add_to_main);
        EditText input_ISBN=findViewById(R.id.input_ISBN);
        TextView name=findViewById(R.id.text_bookname_result);
        TextView author=findViewById(R.id.text_author_result);
        TextView publish=findViewById(R.id.text_publish_result);
        TextView year=findViewById(R.id.text_publishyear_result);
        image = findViewById(R.id.image);
        image.setImageResource(R.drawable.ivy);
        add_to_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddBookActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
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

                        try {
                            result = response.getJSONObject("data");
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            error.setText(response.getString("msg")+" !");
                            name.setText(result.getString("name"));
                            author.setText(result.getString("author"));
                            publish.setText(result.getString("publishing"));
                            year.setText(result.getString("published"));
                            button_addbook_add.setEnabled(true);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            new ImageDownloadTask().execute(result.getString("photoUrl"));
                        } catch (JSONException e) {
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
                saveToLocal();
            }
        });
        add_to_list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(AddBookActivity.this,BookListActivity.class);
                startActivity(intent);
            }
        });

    }
    public String bitmapToString(Bitmap bitmap){
        //将Bitmap转换成字符串
        String string=null;
        ByteArrayOutputStream bStream=new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG,100,bStream);
        byte[]bytes=bStream.toByteArray();
        string= Base64.encodeToString(bytes,Base64.DEFAULT);
        return string;
    }
    public void saveToLocal() {
        FileOutputStream out = null;//openFileOutput () 方法返回的是一个FileOutputStream 对象
        BufferedWriter writer = null;
        JSONObject jsonObject = new JSONObject();
        String json="";
        try {
            jsonObject.put("author", result.getString("author"));
            jsonObject.put("name", result.getString("name"));
            jsonObject.put("publish", result.getString("publishing"));
            jsonObject.put("year", result.getString("published"));
            String imagebitmap = bitmapToString(imageBitmap);
            jsonObject.put("image",imagebitmap);
            json = jsonObject.toString();
            System.out.println("json  "+json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        try {
            out = openFileOutput(result.getString("name"), Context.MODE_PRIVATE);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write(json);
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try {
            out = openFileOutput("catalog", Context.MODE_APPEND);
            writer = new BufferedWriter(new OutputStreamWriter(out));
            writer.write("#"+result.getString("name"));
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    private class ImageDownloadTask extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... params) {
            String url = params[0];
            try {
                URL imageUrl = new URL(url);
                InputStream is = imageUrl.openConnection().getInputStream();
                imageBitmap = BitmapFactory.decodeStream(is);

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
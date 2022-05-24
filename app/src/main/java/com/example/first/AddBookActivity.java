package com.example.first;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONObject;

public class AddBookActivity extends AppCompatActivity {
    private Button button_addbook_find;
    private EditText input_ISBN;
    private String isbn;
    private String address = "https://api.jike.xyz/situ/book/isbn/";
    private String apikey = "?apikey=12718.2336c4d830e490842883539cad5a9668.d596f160ca12154d2fa00b1aef5c9045";
    private TextView name;
    private TextView author;
    private TextView publish;
    private TextView year;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_addbook);
        button_addbook_find=findViewById(R.id.button_addbook_find);
        input_ISBN=findViewById(R.id.input_ISBN);
        name=findViewById(R.id.text_bookname_result);
        author=findViewById(R.id.text_author_result);
        publish=findViewById(R.id.text_publish_result);
        year=findViewById(R.id.text_publishyear_result);
        button_addbook_find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isbn=input_ISBN.getText().toString();
                System.out.println(isbn);
                //isbn="9787806767245";
                String url = address+isbn+apikey;
                System.out.println(url);
                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest
                        (Request.Method.GET, url, null, new Response.Listener<JSONObject>() {

                            @Override
                            public void onResponse(JSONObject response) {
                                //textView.setText("Response: " + response.toString());
                                //name.setText(response.data.name);
                                System.out.println("Response: " + response.toString());
                            }
                        }, new Response.ErrorListener() {

                            @Override
                            public void onErrorResponse(VolleyError error) {
                                // TODO: Handle error
                                System.out.println("error");
                            }
                        });

            }
        });


        // Access the RequestQueue through your singleton class.
        //AddBookActivity.getInstance(this).addToRequestQueue(jsonObjectRequest);
    }



}
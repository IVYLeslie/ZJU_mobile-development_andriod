package com.example.first;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
//import com.alibaba.fastjson.JSONObject;



public class BookListActivity extends AppCompatActivity {
    RecyclerView mRecyclerView;
    private ArrayList<bookInfo> bookinfo = new ArrayList<>();
    private Button back_main;
    private Button list_to_add;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booklist);
        mRecyclerView = findViewById(R.id.recyclerview);
        back_main = findViewById(R.id.list_to_main);
        list_to_add = findViewById(R.id.list_to_add);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        String catalog = load("catalog");
        String[] books = catalog.split("#");
        for (int i =1;i<books.length;i++){
            try {
                String info = load(books[i]);
                JSONObject json = new JSONObject(info);
                if (json!=null) {
                    bookInfo tmp = new bookInfo(json.getString("name"),
                            json.getString("author"),
                            json.getString("publish"),
                            json.getString("year"),
                            json.getString("image"));
                    bookinfo.add(tmp);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        mRecyclerView.setAdapter(new BookAdapter(bookinfo));
        back_main.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookListActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        list_to_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(BookListActivity.this,AddBookActivity.class);
                startActivity(intent);
            }
        });
    }

    public String load(String path){
        FileInputStream in = null;
        BufferedReader reader = null;
        StringBuilder content = new StringBuilder();
        try {
            in = openFileInput(path);
            reader = new BufferedReader(new InputStreamReader(in));
            String line = "";
            while ((line = reader.readLine()) != null) {
                content.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return content.toString();
    }
}
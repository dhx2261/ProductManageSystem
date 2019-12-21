package com.rmit.cloudcomputing;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity {
    private Button submit;
    private EditText search;
    private Button create;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabAdapter tabAdapter;
    private EditText categoty;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DataHolder dataHolder=new DataHolder();
        tabLayout=findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.viewpager);
        tabLayout.setupWithViewPager(viewPager);
        tabAdapter=new TabAdapter(MainActivity.this.getSupportFragmentManager());
        viewPager.setAdapter(tabAdapter);
        submit=findViewById(R.id.submit);
        create=findViewById(R.id.createnew);
        if(!DataHolder.usertype.equals("admin")){
            create.setVisibility(View.GONE);
        }
        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(MainActivity.this,Activity_CreateProduct.class);
                intent.putExtra("from","create");
                startActivity(intent);
            }
        });
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(viewPager.getCurrentItem()==0) {
                    search=viewPager.getChildAt(0).findViewById(R.id.search);
                    categoty=viewPager.getChildAt(0).findViewById(R.id.category);
                    String name = search.getText().toString();
                    if(name.length()==0){
                        Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        AsyncTask_Search asyncTask_search = new AsyncTask_Search(MainActivity.this);
                        if(categoty.getText().length()>0){
                            asyncTask_search.execute("https://cc20192.appspot.com/product?keyword=" + name+"&category="+categoty.getText().toString());
                        }
                        else{
                            asyncTask_search.execute("https://cc20192.appspot.com/product?keyword=" + name);
                        }

                    }
                }
                else if(viewPager.getCurrentItem()==1){
                    search=viewPager.getChildAt(1).findViewById(R.id.search);
                    if(search.getText().length()==0){
                        Toast.makeText(MainActivity.this, "Invalid input", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        AsyncTask_Search asyncTask_search = new AsyncTask_Search(MainActivity.this);
                        asyncTask_search.execute("https://cc20192.appspot.com/product?id=" + search.getText().toString());
                    }
                }
            }
        });

    }

}

package com.example.marekwin10.calc;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;


public class Menu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
    }
    public void onClickListererActivity(View v){
        Button b = (Button) v;
        Intent i=new Intent(Menu.this, MainActivity.class);
        i.putExtra("layout",b.getText());
        startActivity(i);
    }
    public void onClickInfo(View v){
        Intent i=new Intent(Menu.this, InfoScreen.class);
        startActivity(i);
    }
    public void onClickExit(View v){
        finish();
    }
}

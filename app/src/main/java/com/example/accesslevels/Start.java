package com.example.accesslevels;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.util.Pair;
import android.view.View;
import android.widget.Button;

public class Start extends AppCompatActivity {
    private Button startBtnLogin,startRegisterBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);

        startBtnLogin=findViewById(R.id.startLoginBtn);
        startRegisterBtn=findViewById(R.id.startRegisterBtn);

    }

    public void CallLoginScreen(View view)
    {
        Intent intent=new Intent(Start.this,LogIn.class);
        startActivity(intent);
        finish();

        Pair[] pairs=new Pair[1];
        pairs[0]=new Pair<View,String>(startRegisterBtn,"transationName");
        ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(Start.this,pairs);
        //if the system requires an API greater than the current minimum API,hover over "makeSceneTransition
        // Animation" and press alt + enter to surround with the required API
        //do as follows:

        startActivity(intent,activityOptions.toBundle());

    }

    public void CallRegisterScreen(View view)
    {
        Intent intent=new Intent(Start.this,Register.class);
        startActivity(intent);
        finish();

        Pair[] pairs=new Pair[1];
        pairs[0]=new Pair<View,String>(startBtnLogin,"transitionScreen");
        ActivityOptions activityOptions=ActivityOptions.makeSceneTransitionAnimation(Start.this,pairs);
        //if the system requires an API greater than the current minimum API,hover over "makeSceneTransition
        // Animation" and press alt + enter to surround with the required API
        //do as follows:

        startActivity(intent,activityOptions.toBundle());
    }
}
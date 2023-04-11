package com.example.loginandsignup;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class HomeActivity extends AppCompatActivity {
    Button logout;
    Button toChat;
    Button toGenerateImage;
    Button toSpeech;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        logout =  (Button) findViewById(R.id.logout);
        toChat =  (Button) findViewById(R.id.chattobot);
        toGenerateImage =  (Button) findViewById(R.id.chattobotbyimage);
        toSpeech =  (Button) findViewById(R.id.speechtotext);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),LoginActivity.class);
                startActivity(intent);
            }
        });
        toGenerateImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),image_generator_gpt.class);
                startActivity(intent);
            }
        });
        toSpeech.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),SpeechToText.class);
                startActivity(intent);
            }
        });
        toChat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(),ChatActivity.class);
                startActivity(intent);
            }
        });


    }
}
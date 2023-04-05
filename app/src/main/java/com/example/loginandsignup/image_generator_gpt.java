package com.example.loginandsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class image_generator_gpt extends AppCompatActivity {

    EditText inputText;
    Button generateButton;
    ProgressBar progressBar;
    ImageView imageView;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_generator_gpt);

        inputText = findViewById(R.id.generate_image_edit_text);
        generateButton = findViewById(R.id.generate_image_button);
        progressBar = findViewById(R.id.generate_image_circular);
        imageView = findViewById(R.id.generate_image_view);

        generateButton.setOnClickListener((view -> {
            String text = inputText.getText().toString().trim();
            if(text.isEmpty()){
                inputText.setError("Please input a request");
            }
            callAPI(text);

        }));
    }

    void callAPI(String text){

        setInProgress(true);

        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.put("prompt",text);
            jsonObject.put("size","256x256");
        } catch(Exception e){
            e.printStackTrace();
        }

        RequestBody requestBody = RequestBody.create(jsonObject.toString(), JSON);
        Request request = new Request.Builder()
                .url("https://api.openai.com/v1/images/generations")
                .header("Authorization","Bearer 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")
                .post(requestBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Toast.makeText(getApplicationContext(), "Failed to generate requested image", Toast.LENGTH_LONG).show();
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                try {
                    JSONObject object = new JSONObject(response.body().string());
                    String imageUrl = object.getJSONArray("data").getJSONObject(0).getString("url");
                    loadImage(imageUrl);
                    setInProgress(false);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        });

    }

    void setInProgress(boolean inProgress){
        runOnUiThread(()->{
            if(inProgress){
                progressBar.setVisibility(View.VISIBLE);
                generateButton.setVisibility(View.GONE);
            } else{
                progressBar.setVisibility(View.GONE);
                generateButton.setVisibility(View.VISIBLE);
            }
        });

    }

    void loadImage(String url){
        runOnUiThread(()->{
            Picasso.get().load(url).into(imageView);
        });

    }
}
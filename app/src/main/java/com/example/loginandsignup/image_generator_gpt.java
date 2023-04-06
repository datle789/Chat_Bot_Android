package com.example.loginandsignup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class image_generator_gpt extends AppCompatActivity {

    private static int REQUEST_CODE = 100;

    OutputStream outputStream;

    EditText inputText;
    Button generateButton;

    Button saveButton;
    ProgressBar progressBar;
    ImageView imageView;

    String urlOfImaage;

    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_generator_gpt);

        inputText = findViewById(R.id.generate_image_edit_text);
        generateButton = findViewById(R.id.generate_image_button);
        saveButton = findViewById(R.id.generate_image_save_button);
        progressBar = findViewById(R.id.generate_image_circular);
        imageView = findViewById(R.id.generate_image_view);

        generateButton.setOnClickListener((view -> {
            String text = inputText.getText().toString().trim();
            if(text.isEmpty()){
                inputText.setError("Please input a request");
            }
            callAPI(text);

        }));

        saveButton.setOnClickListener((view -> {

            String fileName = inputText.getText() + ".jpg";
            Picasso.get().load(urlOfImaage).into(new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    String mimeType = "image/jpeg";
                    ContentValues values = new ContentValues();
                    values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName);
                    values.put(MediaStore.Images.Media.MIME_TYPE, mimeType);
                    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());

                    ContentResolver contentResolver = getContentResolver();
                    Uri imageUri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

                    OutputStream outputStream;
                    try {
                        outputStream = contentResolver.openOutputStream(imageUri);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.close();
                        Toast.makeText(getApplicationContext(), "Image saved to external storage", Toast.LENGTH_SHORT).show();
                    } catch (IOException e) {
                        e.printStackTrace();
                        Toast.makeText(getApplicationContext(), "Failed to save image to external storage", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    Toast.makeText(getApplicationContext(), "Failed to download image", Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            });
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
                    urlOfImaage = imageUrl;
                    loadImage(imageUrl);
                    //Log.i("response :", response.body().string());
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
                saveButton.setVisibility(View.GONE);
            } else{
                progressBar.setVisibility(View.GONE);
                generateButton.setVisibility(View.VISIBLE);
                saveButton.setVisibility(View.VISIBLE);

            }
        });

    }

    void loadImage(String url){
        runOnUiThread(()->{
            Picasso.get().load(url).into(imageView);
        });

    }
}

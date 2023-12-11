package com.example.bioauth;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Random;

public class MainActivity extends AppCompatActivity {
    ImageView ivFinger;
    TextView tvMessage;
    byte[] img;
    Bitmap bm;
    Button okay;
    File image_file;
    private static final int SCAN_FINGER = 0;
    private static final int REQUEST_WRITE_PERMISSION = 1;
    private static final int REQUEST_WRITE_PERMISSION2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvMessage = (TextView) findViewById(R.id.tvMessage);
        ivFinger = (ImageView) findViewById(R.id.ivFingerDisplay);
        okay = (Button) findViewById(R.id.okay);
        okay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_WRITE_PERMISSION);
                } else {
                    saveBitmapAsPNG();
                }
            }
        });
    }
    public static String generateRandomDigits(int numberOfDigits) {
        Random random = new Random();
        StringBuilder stringBuilder = new StringBuilder();

        for (int i = 0; i < numberOfDigits; i++) {
            int digit = random.nextInt(10); // Generates a random number between 0 and 9
            stringBuilder.append(digit);
        }

        return stringBuilder.toString();
    }
    private void createAndSaveTextFile() {
        String id = generateRandomDigits(10);
        String fileName = id+".txt";
        String imagepath = image_file.getAbsolutePath();
        String content =
                ""+imagepath
                +"\n"+id;

        File directory = new File(Environment.getExternalStorageDirectory(), "/CBIMS/criminals/");
        if (!directory.exists()) {
            directory.mkdirs();
        }
//
//        File file = new File(directory, fileName);

        // beginning of try

//            FileOutputStream fos = new FileOutputStream(file);
//            fos.write(content.getBytes());
//            fos.close();
//            handleIntent();
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("label", content);
        clipboardManager.setPrimaryClip(clipData);
        Toast.makeText(this, "Scan Complete"+clipboardManager.getPrimaryClip(), Toast.LENGTH_SHORT).show();
        finish();

        // end of try
    }

    private void handleIntent() {
        File image;
        String errorMessage = "empty";
//        int status = msg.getData().getInt("status");
        Intent intent = new Intent();
        intent.putExtra("status", "Fingerprint successfully captured");
        image = image_file;
        intent.putExtra("img", image);
        setResult(RESULT_OK, intent);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION2) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                saveBitmapAsPNG();
            }
        }
    }
    private void saveBitmapAsPNG() {
        Bitmap bitmap = bm; // Your Bitmap goes here

        String fileName = generateRandomDigits(10)+".png";

        File directory = new File(Environment.getExternalStorageDirectory(), "/CBIMS/fingerprint");
        if (!directory.exists()) {
            directory.mkdirs();
        }

        File file = new File(directory, fileName);

        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            image_file = file;
//            ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
//            ClipData clipData = ClipData.newPlainText("label", image_file.getAbsolutePath());
//            clipboardManager.setPrimaryClip(clipData);
            Toast.makeText(this, "Processing...", Toast.LENGTH_SHORT).show();
//            finish();
//            handleIntent();
            createAndSaveTextFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void startScan(View view) {
        Intent intent = new Intent(MainActivity.this, ScanActivity.class);
        startActivityForResult(intent, SCAN_FINGER);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        int status;
        String errorMesssage;
        switch (requestCode) {
            case (SCAN_FINGER): {
                if (resultCode == RESULT_OK) {
                    status = data.getIntExtra("status", Status.ERROR);
                    if (status == Status.SUCCESS) {
                        tvMessage.setText("FingerPrint Captured Successfully");
                        img = data.getByteArrayExtra("img");
                        bm = BitmapFactory.decodeByteArray(img, 0, img.length);
                        ivFinger.setImageBitmap(bm);
                    } else {
                        errorMesssage = data.getStringExtra("errorMessage");
                        tvMessage.setText("-- Error: " + errorMesssage + " --");
                    }
                }
                break;
            }
        }
    }
}
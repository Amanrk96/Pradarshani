package com.example.hp.pradarshani;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.util.Random;

/**
 * Created by HP on 10-Apr-18.
 */

public class Encryption extends AppCompatActivity {

    String path;
    EditText encrypt_message;
    Button encrypt;
    Bitmap bmp, container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_encrypt);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        encrypt_message = findViewById(R.id.message_encrypt);
        encrypt = findViewById(R.id.btn_encrypt);

        bmp = BitmapFactory.decodeFile(path);


        encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String message = encrypt_message.getText().toString();

                container = bmp.copy(Bitmap.Config.ARGB_8888, true);
                embedMessage(container, message);
                if (true) {
                    String root = Environment.getExternalStorageDirectory().toString();
                    File dir = new File(root + "/Pictures");
                    dir.mkdirs();

                    Random generator = new Random();
                    int n = 10000;
                    n = generator.nextInt(n);

                    String fname = "Image-" + n + ".png";
                    File file = new File(dir, fname);

                    try {
                        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                        container.compress(Bitmap.CompressFormat.PNG, 100, bytes);
                        FileOutputStream fo = new FileOutputStream(file);
                        fo.write(bytes.toByteArray());
                        fo.close();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Toast.makeText(Encryption.this, "Encrypted", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Encryption.this, MainActivity.class);
                    startActivity(i);

                    // Tell the media scanner about the new file so that it is
                    // immediately available to the user.
                    MediaScannerConnection.scanFile(Encryption.this, new String[]{file.toString()}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("ExternalStorage", "Scanned " + path + ":");
                                    Log.i("ExternalStorage", "-> uri=" + uri);
                                }
                            });
                } else {
                    Toast.makeText(Encryption.this, "Not Done!", Toast.LENGTH_SHORT).show();
                    Intent i = new Intent(Encryption.this, MainActivity.class);
                    startActivity(i);
                }
            }
        });
    }

    private void embedMessage(Bitmap img, String mess) {
        int messageLength = mess.length();

        int imageWidth = img.getWidth(), imageHeight = img.getHeight(),
                imageSize = imageWidth * imageHeight;
        if (messageLength * 8 + 32 > imageSize) {
            System.out.println("The text is too long. It cannot be hidden.");
            return;
        }
        embedInteger(img, messageLength, 0, 0);

        byte b[] = mess.getBytes();
        for (int i = 0; i < b.length; i++)
            embedByte(img, b[i], i * 8 + 32, 0);
    }

    private void embedInteger(Bitmap img, int n, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        for (int i = startX; i < maxX && count < 32; i++) {
            for (int j = startY; j < maxY && count < 32; j++) {
                int rgb = img.getPixel(i, j), bit = getBitValue(n, count);
                rgb = setBitValue(rgb, storageBit, bit);
                img.setPixel(i, j, rgb);
                count++;
            }
        }
    }

    private void embedByte(Bitmap img, byte b, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        for (int i = startX; i < maxX && count < 8; i++) {
            for (int j = startY; j < maxY && count < 8; j++) {
                int rgb = img.getPixel(i, j), bit = getBitValue(b, count);
                rgb = setBitValue(rgb, storageBit, bit);
                img.setPixel(i, j, rgb);
                count++;
            }
        }
    }

    private int getBitValue(int n, int location) {
        int v = n & (int) Math.round(Math.pow(2, location));
        return v == 0 ? 0 : 1;
    }

    private int setBitValue(int n, int location, int bit) {
        int toggle = (int) Math.pow(2, location), bv = getBitValue(n, location);
        if (bv == bit)
            return n;
        if (bv == 0 && bit == 1)
            n |= toggle;
        else if (bv == 1 && bit == 0)
            n ^= toggle;
        return n;
    }
}
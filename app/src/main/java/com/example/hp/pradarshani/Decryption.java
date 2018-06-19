package com.example.hp.pradarshani;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by HP on 11-Apr-18.
 */

public class Decryption extends AppCompatActivity {

    TextView decryptedmessage;
    Button decrypt;
    String path;
    Bitmap bmp;

    @Override
    protected void onCreate(Bundle savedinstanceState) {
        super.onCreate(savedinstanceState);
        setContentView(R.layout.activity_decryption);

        decryptedmessage = findViewById(R.id.message_decrypt);
        decrypt = findViewById(R.id.btn_decrypt);

        Intent intent = getIntent();
        path = intent.getStringExtra("path");

        bmp = BitmapFactory.decodeFile(path);

        decrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String hiddenmessage = decodeMessage(bmp);
                decryptedmessage.setText(hiddenmessage);

            }
        });
    }

    private String decodeMessage(Bitmap image) {
        String message;
        int len = extractInteger(image, 0, 0);
        byte b[] = new byte[len];
        for (int i = 0; i < len; i++)
            b[i] = extractByte(image, i * 8 + 32, 0);

        message = new String(b);
        return message;
    }

    private int extractInteger(Bitmap img, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        int length = 0;
        for (int i = startX; i < maxX && count < 32; i++) {
            for (int j = startY; j < maxY && count < 32; j++) {
                int rgb = img.getPixel(i, j), bit = getBitValue(rgb, storageBit);
                length = setBitValue(length, count, bit);
                count++;
            }
        }
        return length;
    }

    private byte extractByte(Bitmap img, int start, int storageBit) {
        int maxX = img.getWidth(), maxY = img.getHeight(),
                startX = start / maxY, startY = start - startX * maxY, count = 0;
        byte b = 0;
        for (int i = startX; i < maxX && count < 8; i++) {
            for (int j = startY; j < maxY && count < 8; j++) {
                int rgb = img.getPixel(i, j), bit = getBitValue(rgb, storageBit);
                b = (byte) setBitValue(b, count, bit);
                count++;
            }
        }
        return b;
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
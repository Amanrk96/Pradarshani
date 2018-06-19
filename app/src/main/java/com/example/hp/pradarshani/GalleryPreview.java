package com.example.hp.pradarshani;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.io.File;


public class GalleryPreview extends AppCompatActivity {

    ImageView GalleryPreviewImg;
    String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().show();
        setContentView(R.layout.gallery_preview);
        Intent intent = getIntent();
        path = intent.getStringExtra("path");
        GalleryPreviewImg = findViewById(R.id.GalleryPreviewImg);
        Glide.with(GalleryPreview.this)
                .load(new File(path)) // Uri of the picture
                .into(GalleryPreviewImg);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.main_activity_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_share:
                Uri imageUri = Uri.parse(path);
                Intent shareimage = new Intent();
                shareimage.setAction(Intent.ACTION_SEND);
                shareimage.putExtra(Intent.EXTRA_STREAM, imageUri);
                shareimage.setType("image/*");
                startActivity(Intent.createChooser(shareimage, getResources().getText(R.string.send_to)));
                this.finish();
                break;
            case R.id.action_encrypt:
                Intent encrypt = new Intent(this, Encryption.class);
                encrypt.putExtra("path", path);
                startActivity(encrypt);
                this.finish();
                break;
            case R.id.action_decrypt:
                Intent decrypt = new Intent(this, Decryption.class);
                decrypt.putExtra("path", path);
                startActivity(decrypt);
                this.finish();
                break;
            case R.id.action_delete:
                ContentResolver contentResolver = getContentResolver();
                contentResolver.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        MediaStore.Images.ImageColumns.DATA + "=?", new String[]{path});
                Intent intent = new Intent(GalleryPreview.this, MainActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
        return true;
    }
}
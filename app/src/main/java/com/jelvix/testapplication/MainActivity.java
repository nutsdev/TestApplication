package com.jelvix.testapplication;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_START_CAMERA = 0;

    private File photoFile;

    private ImageView photoImageView;


    /* lifecycle */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        photoImageView = (ImageView) findViewById(R.id.photoImageView);

        setupToolbar();
        setupFab();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        cleanPhotoDirectory();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_clean) {
            cleanPhotoDirectory();
            photoImageView.setImageDrawable(null);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_CODE_START_CAMERA:
                if (resultCode != RESULT_OK || photoFile == null)
                    return;

                Picasso.with(this).load(photoFile).fit().centerCrop().into(photoImageView);
                break;
            default:
                break;
        }
    }


    /* callbacks */

    private final View.OnClickListener clickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            try {
                photoFile = createCacheImageFile();
                startCamera(photoFile, REQUEST_CODE_START_CAMERA);
            } catch (Exception e) {
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.Error))
                        .setMessage(e.getMessage())
                        .show();
            }
        }
    };


    /* private methods */

    private void setupToolbar() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
    }

    private void setupFab() {
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        //noinspection ConstantConditions
        floatingActionButton.setOnClickListener(clickListener);
    }

    @SuppressLint("SimpleDateFormat")
    private File createCacheImageFile() throws Exception {
        final String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        final String fileName = getString(R.string.cached_photo).concat(timeStamp);
        return new File(getExternalFilesDir(null), fileName);
    }

    private void startCamera(File photoFile, int requestCode) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            if (photoFile != null) {
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                startActivityForResult(takePictureIntent, requestCode);
            }
        }
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    private void cleanPhotoDirectory() {
        final File externalFilesDir = getExternalFilesDir(null);

        if (externalFilesDir != null) {
            File[] files = externalFilesDir.listFiles();

            for (File file : files) {
                file.delete();
            }
        }
    }

}
package com.example.cillian.photointents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.Locale;

/**
 * @author Cillian Myles <mylsey4thewin@gmail.com> on 30/11/2017.
 */
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PHOTO_REQUEST_CODE = 0;

    private TextView mPhotoUrlTv;
    private ImageView mPhotoThumbnail;

    private String mPhotoAbsolutePath;
    private Uri mPhotoContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar lToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(lToolbar);

        mPhotoUrlTv = findViewById(R.id.photo_uri);
        mPhotoThumbnail = findViewById(R.id.photo_thumb);

        FloatingActionButton lFab = findViewById(R.id.fab);
        lFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera.
                launchCamera();
            }
        });
    }

    private void launchCamera() {
        Intent lTakePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (lTakePictureIntent.resolveActivity(getPackageManager()) != null) {
            File lPhotoFile = null;
            try {
                // Create the File where the photo should go.
                lPhotoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File.
            }
            // Continue only if the File was successfully created.
            if (lPhotoFile != null) {
                mPhotoContentUri = PhotoProvider.getUriForFile(
                        MainActivity.this,
                        PhotoProvider.AUTHORITY, // TODO: verify
                        lPhotoFile
                );
                Log.e(TAG, "mPhotoAbsolutePath: " + mPhotoAbsolutePath); // TODO: delete
                Log.e(TAG, "mPhotoContentUri: " + mPhotoContentUri); // TODO: delete
                Log.e(TAG, "int files (before): " + Arrays.toString(fileList())); // TODO: delete
                lTakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mPhotoContentUri);
                startActivityForResult(lTakePictureIntent, PHOTO_REQUEST_CODE);
            }
        } else {
            Toast.makeText(MainActivity.this,
                    "Please ensure you have an application to take images.",
                    Toast.LENGTH_LONG).show();
        }
    }

    private File createImageFile() throws IOException {
        // Create an image file name
        final String lTimeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.ENGLISH).format(new Date());
        final String lFileName = "IMG_" + lTimeStamp + "_";
        final String lSuffix = ".jpg";
        File lExternalDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File lInternalDir = getFilesDir();
        File lImage = File.createTempFile(
                lFileName,   /* prefix */
                lSuffix,     /* suffix */
                lInternalDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mPhotoAbsolutePath = lImage.getAbsolutePath();
        return lImage;
    }

    @SuppressWarnings("UnnecessaryLocalVariable")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e(TAG, "requestCode: " + requestCode); // TODO: delete
        Log.e(TAG, "resultCode: " + resultCode); // TODO: delete
        Log.e(TAG, "data: " + data); // TODO: delete
        Log.e(TAG, "uri: " + data.getData()); // TODO: delete
        Log.e(TAG, "int files (after): " + Arrays.toString(fileList())); // TODO: delete

        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {

            // Can use file or content uri.
            final Uri lFileUri = Uri.parse("file://" + mPhotoAbsolutePath);
            final Uri lContentUri = mPhotoContentUri;
            final Uri lUri = lContentUri;

            mPhotoUrlTv.setText(lUri != null ? lUri.toString() : "ERROR");
            mPhotoThumbnail.setImageURI(lUri);

            File lImageFile = new File(mPhotoAbsolutePath);
            if (lImageFile.exists()) {
                Log.e(TAG, "name: " + lImageFile.getName()); // TODO: delete
                Log.e(TAG, "path (reg): " + lImageFile.getPath()); // TODO: delete
                Log.e(TAG, "path (abs): " + lImageFile.getAbsolutePath()); // TODO: delete
                Log.e(TAG, "size (bytes): " + lImageFile.length()); // TODO: delete
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

package com.example.cillian.photointents;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
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
@SuppressWarnings("FieldCanBeLocal")
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private final static int PHOTO_REQUEST_CODE = 0;

    private TextView mPhotoUrlTv;
    private ImageView mPhotoThumbnail;

    private String mAbsolutePath;
    private Uri mFileUri;
    private Uri mContentUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar lToolbar = findViewById(R.id.toolbar);
        setSupportActionBar(lToolbar);

        mPhotoThumbnail = findViewById(R.id.photo_thumb);

        mPhotoUrlTv = findViewById(R.id.photo_uri);
        mPhotoUrlTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open with external app.
                openImageWithExternalApp();
            }
        });

        FloatingActionButton lFab = findViewById(R.id.fab);
        lFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Launch camera.
                launchCamera();
            }
        });
    }

    private void openImageWithExternalApp() {
        final String lPhotoUri = mPhotoUrlTv.getText().toString();
        if (!TextUtils.isEmpty(lPhotoUri)) {
            Intent lIntent = new Intent(Intent.ACTION_VIEW);
            lIntent.setDataAndType(Uri.parse(lPhotoUri), "image/jpeg");
            if (lIntent.resolveActivity(getPackageManager()) != null) {
                Log.e(TAG, "intent: " + lIntent);
                lIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                startActivity(lIntent);
            } else {
                Toast.makeText(MainActivity.this,
                        "Please ensure you have an application to view images.",
                        Toast.LENGTH_LONG).show();
            }
        }
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
                mFileUri = Uri.parse("file://" + mAbsolutePath);
                mContentUri = PhotoProvider.getUriForFile(
                        MainActivity.this,
                        PhotoProvider.AUTHORITY, // TODO: verify
                        lPhotoFile
                );

                lTakePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, mContentUri);
                startActivityForResult(lTakePictureIntent, PHOTO_REQUEST_CODE);

                Log.e(TAG, "absolute path: " + mAbsolutePath); // TODO: delete
                Log.e(TAG, "file uri: " + mFileUri); // TODO: delete
                Log.e(TAG, "content uri: " + mContentUri); // TODO: delete
                Log.e(TAG, "internal files (before): " + Arrays.toString(fileList())); // TODO: delete
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
                lExternalDir /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        mAbsolutePath = lImage.getAbsolutePath();
        return lImage;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PHOTO_REQUEST_CODE && resultCode == RESULT_OK) {

            // Can use file or content uri.
            //final Uri lUri = mFileUri;
            final Uri lUri = mContentUri;

            mPhotoUrlTv.setText(lUri != null ? lUri.toString() : "ERROR");
            mPhotoThumbnail.setImageURI(lUri);

            File lImageFile = new File(mAbsolutePath);
            if (lImageFile.exists()) {
                Log.e(TAG, "name: " + lImageFile.getName()); // TODO: delete
                Log.e(TAG, "path: " + lImageFile.getAbsolutePath()); // TODO: delete
                Log.e(TAG, "size (bytes): " + lImageFile.length()); // TODO: delete
                Log.e(TAG, "internal files (after): " + Arrays.toString(fileList())); // TODO: delete
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

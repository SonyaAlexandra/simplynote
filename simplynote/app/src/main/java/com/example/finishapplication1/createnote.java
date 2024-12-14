package com.example.finishapplication1;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;
import androidx.appcompat.widget.Toolbar;

import android.annotation.SuppressLint;
import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class createnote extends AppCompatActivity {

    private EditText mCreateTitleOfNote, mCreateContentOfNote;
    private FloatingActionButton mSaveNote;
    private ProgressBar mProgressBarOfCreateNote;
    private ImageView mImageView;
    private SwitchCompat themeSwitch;

    private FirebaseAuth firebaseAuth;
    private FirebaseUser firebaseUser;
    private FirebaseFirestore firebaseFirestore;

    private static final String PREFS_NAME = "theme_prefs";
    private static final String IS_DARK_MODE = "is_dark_mode";
    private static final int REQUEST_CODE = 99;
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Apply theme before setting content view
        applyTheme();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_createnote);

        // Initialize UI components
        mCreateContentOfNote = findViewById(R.id.createContentOfNote);
        mCreateTitleOfNote = findViewById(R.id.createTitleNote);
        mSaveNote = findViewById(R.id.saveNote);
        mProgressBarOfCreateNote = findViewById(R.id.progressBarOfCreateNote);
        mImageView = findViewById(R.id.imageView); // Add an ImageView for displaying the captured image
        themeSwitch = findViewById(R.id.theme_switch);

        Toolbar toolbar = findViewById(R.id.toolbarofcreatenote);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        firebaseAuth = FirebaseAuth.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseUser = firebaseAuth.getCurrentUser();

        // Theme toggle switch
        themeSwitch.setChecked(isDarkMode()); // Set initial state
        themeSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            setDarkMode(isChecked); // Save preference
            recreate(); // Recreate activity to apply the new theme
        });

        // Save note functionality
        mSaveNote.setOnClickListener(view -> {
            String title = mCreateTitleOfNote.getText().toString();
            String content = mCreateContentOfNote.getText().toString();

            if (title.isEmpty() || content.isEmpty()) {
                Toast.makeText(getApplicationContext(), "Both fields are required", Toast.LENGTH_SHORT).show();
            } else {
                mProgressBarOfCreateNote.setVisibility(View.VISIBLE);

                DocumentReference documentReference = firebaseFirestore.collection("notes")
                        .document(firebaseUser.getUid()).collection("myNotes").document();
                Map<String, Object> note = new HashMap<>();
                note.put("title", title);
                note.put("content", content);

                documentReference.set(note).addOnSuccessListener(unused -> {
                    Toast.makeText(getApplicationContext(), "Note Created Successfully", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(createnote.this, notesActivity.class));
                }).addOnFailureListener(e -> {
                    Toast.makeText(getApplicationContext(), "Failed To Create Note", Toast.LENGTH_SHORT).show();
                    mProgressBarOfCreateNote.setVisibility(View.INVISIBLE);
                });
            }
        });

        // Camera functionality
        Button fabCamera = findViewById(R.id.btncamera);
        fabCamera.setOnClickListener(view -> {
            // Check for camera permission
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                // Permission already granted, open camera
                openCamera();
            }
        });
        // Restore image state after theme change (if available)
        if (savedInstanceState != null) {
            Bitmap imageBitmap = savedInstanceState.getParcelable("captured_image");
            if (imageBitmap != null) {
                mImageView.setImageBitmap(imageBitmap);
            }
        }
    }
    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save the captured image before theme change
        if (mImageView.getDrawable() != null) {
            BitmapDrawable drawable = (BitmapDrawable) mImageView.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            outState.putParcelable("captured_image", bitmap);
        }
    }
    // Handle camera permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission granted, open camera
                openCamera();
            } else {
                // Permission denied, show a message
                Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
    // Open the camera
    private void openCamera() {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(cameraIntent, REQUEST_CODE);
    }
    // Handle result from camera activity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Bitmap picTaken = (Bitmap) data.getExtras().get("data");
            mImageView.setImageBitmap(picTaken); // Display the captured image
        } else {
            Toast.makeText(this, "Camera Canceled", Toast.LENGTH_SHORT).show();
        }
    }

    // Theme-related methods
    private void applyTheme() {
        if (isDarkMode()) {
            setTheme(R.style.AppTheme);
        } else {
            setTheme(R.style.AppTheme_Dark);
        }
    }

    private boolean isDarkMode() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        return prefs.getBoolean(IS_DARK_MODE, false);
    }

    private void setDarkMode(boolean isDarkMode) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(IS_DARK_MODE, isDarkMode);
        editor.apply();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}

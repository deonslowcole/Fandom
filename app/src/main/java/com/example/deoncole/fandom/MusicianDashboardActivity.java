package com.example.deoncole.fandom;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.Broadcast;
import com.example.deoncole.fandom.model.LiveBroadcast;
import com.example.deoncole.fandom.ui.view.broadcast.BroadcastsView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.firebase.messaging.FirebaseMessaging;

public class MusicianDashboardActivity extends AppCompatActivity {

    private static final String ARTIST_FIREBASE_UID_EXTRA = "ARTIST_FIREBASE_UID";
    private ImageView artistTitleImg;
    private TextView artistTitleNameTv;
    private ImageView imageToSendImgV;
    private EditText messageToSendEt;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private FirebaseAuth mAuth;
    @Nullable
    private String firebaseUserUid;

    private Uri photoURI;
    private String mCurrentFanPhotoPath;
    @Nullable
    private Bitmap broadcastBitmap;
    @Nullable
    private Artist artist;
    private BroadcastsView mBroadcastsView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_musician_dashboard);

        artistTitleImg = (ImageView) findViewById(R.id.artistTitleImg);
        artistTitleNameTv = (TextView) findViewById(R.id.artistTitleNameTv);
        imageToSendImgV = (ImageView) findViewById(R.id.image_to_send);
        ImageView messTakePicImgV = (ImageView) findViewById(R.id.pictureBtImgV);
        messageToSendEt = (EditText) findViewById(R.id.message_to_send);
        mBroadcastsView = (BroadcastsView) findViewById(R.id.broadcasts_view);
        mAuth = FirebaseAuth.getInstance();

        Toolbar musicianToolbar = (Toolbar) findViewById(R.id.musicianToolBar);
        setSupportActionBar(musicianToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        if (getIntent() != null) {
            firebaseUserUid = getIntent().getStringExtra(ARTIST_FIREBASE_UID_EXTRA);
        }

        fetchArtist();

        imageToSendImgV.setVisibility(View.GONE);
        messTakePicImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addPictureToSendIntent();
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_musician_dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_broadcast:
                showBroadcastDialog();
                break;
            case R.id.action_voice:
                VoiceRecordActivity.open(this, artist);
                Toast.makeText(getApplicationContext(), "Ready to record voice", Toast.LENGTH_SHORT)
                        .show();
                break;
            case R.id.action_posts:
                Intent postIntent = new Intent(getApplicationContext(), ArtistFeedActivity.class);
                postIntent.putExtra(ArtistFeedActivity.IS_MUSICIAN, true);
                startActivity(postIntent);
                break;
            case R.id.action_voice_notify:
                activityIntent(AudioMessageActivity.class);
                break;
            case R.id.action_logout:
                mAuth.signOut();
                activityIntent(MainActivity.class);
                break;
            case R.id.action_profile:
                activityIntent(ProfileActivity.class);
                break;

        }
        return super.onOptionsItemSelected(item);
    }

    public void fetchArtist() {
        final FirebaseUser user = mAuth.getCurrentUser();
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ("artists".equals(snapshot.getKey())) {
                        for (DataSnapshot shot : snapshot.getChildren()) {
                            Artist newArtist = shot.getValue(Artist.class);
                            if (user != null && newArtist != null &&
                                    user.getUid().equals(newArtist.getUserUid())) {
                                artist = newArtist;
                                artist.setId(shot.getKey());

                                Picasso.with(getApplicationContext()).load(artist.getImageUrl())
                                        .into(artistTitleImg);
                                artistTitleNameTv.setText(artist.getArtistName());

                                mBroadcastsView.displayedBroadcasts(artist);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void sendMessage(final View view) {
        if (firebaseUserUid == null) {
            Snackbar.make(view, "Can't send a message from a non artist screen.", Snackbar
                    .LENGTH_LONG).show();
        }

        final String message = messageToSendEt.getText().toString();

        if (TextUtils.isEmpty(message.trim())) {
            Snackbar.make(view, "No message to send.", Snackbar
                    .LENGTH_LONG).show();
            return;
        }

        //TODO: have a progress indicator while saving broadcast and image
        DatabaseReference broadcastRef = FirebaseDatabase.getInstance().getReference("broadcasts").push();
        final String broadcastKey = broadcastRef.getKey();

        broadcastRef.setValue(new Broadcast(artist.getId(), message,
                System.currentTimeMillis(), null))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        onBroadcastSaved(view);
                        imageToSendImgV.setImageBitmap(null);
                        imageToSendImgV.setVisibility(View.GONE);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Snackbar.make(view, "Message couldn't be sent", Snackbar.LENGTH_LONG).show();
                    }
                });

        saveBroadcastImage(message, broadcastKey);

    }

    private void saveBroadcastImage(final String message, final String broadcastKey) {
        if (broadcastBitmap == null) return;
        ((FandomApp) getApplicationContext()).getFireBaseProvider()
                .saveBroadcastImage(MusicianDashboardActivity.this,
                        broadcastKey,
                        broadcastBitmap,
                        new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                Log.d("TEST", "onSuccess save broadcast image ");
                                Broadcast broadcast = new Broadcast(artist.getId(), message,
                                        System.currentTimeMillis(), taskSnapshot.getDownloadUrl().toString());
                                FirebaseDatabase.getInstance().getReference("broadcasts")
                                        .child(broadcastKey)
                                        .setValue(broadcast);
                            }
                        });
    }

    private void onBroadcastSaved(View view) {
        Snackbar.make(view, "Message Sent", Snackbar.LENGTH_LONG).show();
        messageToSendEt.setText("");
        hideKeyboard();
    }

    public static Intent createIntent(Context context, String firebaseUserId) {
        Intent intent = new Intent(context, MusicianDashboardActivity.class);
        intent.putExtra(ARTIST_FIREBASE_UID_EXTRA, firebaseUserId);
        return intent;
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(
                Context.INPUT_METHOD_SERVICE);

        imm.hideSoftInputFromWindow(messageToSendEt.getWindowToken(), 0);
    }

    private void addPictureToSendIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(this.getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {

            }
            if (photoFile != null) {
                String fileProviderString = "com.example.deoncole.fileprovider";
                photoURI = FileProvider.getUriForFile(this, fileProviderString,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addImageToGallery(mCurrentFanPhotoPath, this);

            try {
                broadcastBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), photoURI);
                imageToSendImgV.setVisibility(View.VISIBLE);
                imageToSendImgV.setImageURI(photoURI);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = this.getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentFanPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static void addImageToGallery(final String filePath, final Context context) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private void showBroadcastDialog() {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
        alertBuilder.setTitle("Going Live")
                .setMessage("Touch Yes to start a live broadcast and chat with some of your fans.")
                .setNeutralButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Toast.makeText(getApplicationContext(), "Broadcast started", Toast
                                .LENGTH_SHORT)
                                .show();

                        DatabaseReference liveBroadcastRef = FirebaseDatabase.getInstance()
                                .getReference("live-broadcasts").push();

                        liveBroadcastRef.setValue(new LiveBroadcast(artist.getId(),
                                System.currentTimeMillis()))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        activityIntent(LiveStreamActivity.class);
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MusicianDashboardActivity.this,
                                                getString(R.string.error_starting_live_broadcast),
                                                Toast.LENGTH_LONG).show();
                                    }
                                });

                    }
                });

        AlertDialog dialog = alertBuilder.create();

        dialog.show();
    }

    private void activityIntent(Class intentClass) {
        Intent intent = new Intent(getApplicationContext(), intentClass);
        startActivity(intent);
    }
}

package com.example.deoncole.fandom;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.example.deoncole.fandom.model.Artist;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageMetadata;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;
import java.io.IOException;
import java.util.Random;

public class VoiceRecordActivity extends AppCompatActivity {

    public static final String ARTIST_EXTRA = "ARTIST_EXTRA";
    private TextView recordingTv;
    private Button recordBt, stopBt, playbackBt, sendToFansBt;
    private EditText audioNameEt;

    private FirebaseAuth mAuth;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;

    private Random random;
    private MediaRecorder mediaRecorder;
    private MediaPlayer mediaPlayer;
    private String voiceStoragePath;
    private Boolean isPlayback;
    private Artist artist;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_voice_record);

        recordingTv = (TextView) findViewById(R.id.recordingTv);
        recordBt = (Button) findViewById(R.id.recordButton);
        stopBt = (Button) findViewById(R.id.stopButton);
        playbackBt = (Button) findViewById(R.id.playButton);
        sendToFansBt = (Button) findViewById(R.id.sendVoiceButton);

        Toolbar vrToolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(vrToolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Voice Recorder");
        }

        if (!getIntent().hasExtra(ARTIST_EXTRA)) return;

        artist = getIntent().getParcelableExtra(ARTIST_EXTRA);

        mAuth = FirebaseAuth.getInstance();
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);
        createOutputFile();

        stopBt.setEnabled(false);
        playbackBt.setEnabled(false);
        sendToFansBt.setEnabled(false);

        isPlayback = false;
        random = new Random();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.actionbar_main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                break;
            case R.id.action_profile:
                activityIntent(ProfileActivity.class);
                break;
            case R.id.action_voice_notify:
                activityIntent(AudioMessageActivity.class);
                break;
            case R.id.action_logout:
                mAuth.signOut();
                activityIntent(MainActivity.class);
                break;
        }
        return true;
    }

    public void record(View v) {

        createOutputFile();

        voiceStoragePath = voiceStoragePath + File.separator + "voices/" + createFileName(6) + ".3gpp";
        System.out.println("Audio path : " + voiceStoragePath);

        MediaRecorderReady();

        try {
            mediaRecorder.prepare();
            mediaRecorder.start();
            mediaPlayer = null;
        } catch (IOException e) {
            e.printStackTrace();
        }

        stopBt.setEnabled(true);
        playbackBt.setEnabled(false);
        sendToFansBt.setEnabled(false);

        animateText(recordingTv);

    }

    public void stop(View view) {
        if (isPlayback && mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
            stopBt.setEnabled(false);
            recordBt.setEnabled(true);
            sendToFansBt.setEnabled(true);
            recordingTv.setText(R.string.ready_to_record);
            recordingTv.clearAnimation();
            mediaPlayer = null;
            MediaRecorderReady();

        } else {
            mediaRecorder.stop();
            playbackBt.setEnabled(true);
            recordingTv.clearAnimation();
            recordingTv.setText(R.string.ready_to_record);
            stopBt.setEnabled(false);
            sendToFansBt.setEnabled(true);
            MediaRecorderReady();
            Snackbar.make(view, "Recording Complete", Snackbar.LENGTH_SHORT).show();
        }
    }

    public void playBack(View view) {
        mediaPlayer = new MediaPlayer();
        isPlayback = true;
        recordingTv.setText(R.string.playing_audio);
        stopBt.setEnabled(true);
        recordBt.setEnabled(false);
        sendToFansBt.setEnabled(false);

        try {
            mediaPlayer.setDataSource(voiceStoragePath);
            mediaPlayer.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaPlayer.start();
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mediaPlayer.stop();
                recordBt.setEnabled(true);
                stopBt.setEnabled(false);
                sendToFansBt.setEnabled(true);
                recordingTv.setText(R.string.ready_to_record);
                MediaRecorderReady();
            }
        });
    }


    private String createFileName(int intString) {
        StringBuilder builder = new StringBuilder(intString);
        int i = 0;
        while (i < intString) {
            String audioFileName = "fandomVoiceX";
            builder.append(audioFileName.charAt(random.nextInt(audioFileName.length())));
            i++;
        }
        return builder.toString();
    }

    private void MediaRecorderReady() {
        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        mediaRecorder.setOutputFile(voiceStoragePath);
    }

    private boolean createOutputFile() {
        voiceStoragePath = Environment.getExternalStorageDirectory().getAbsolutePath();
        File audioVoice = new File(voiceStoragePath + File.separator + "voices");
        return !audioVoice.exists() && audioVoice.mkdir();
    }

    public void animateText(TextView myTextView) {
        myTextView.setText(R.string.recording);
        Animation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(125);
        anim.setStartOffset(20);
        anim.setRepeatMode(Animation.REVERSE);
        anim.setRepeatCount(Animation.INFINITE);
        myTextView.startAnimation(anim);
    }

    public void sendToFans(View view) {
        uploadDialog();
    }

    private void uploadDialog() {
        final View dialogView = getLayoutInflater().inflate(R.layout.dialog_audio_filename, null);
        audioNameEt = (EditText) dialogView.findViewById(R.id.audioNameEt);

        final FireBaseProvider fireBaseProvider = ((FandomApp) getApplicationContext()).getFireBaseProvider();

        AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Upload Audio");
        alertDialog.setMessage("Name your message and touch send");
        alertDialog.setView(dialogView);
        alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Send", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (TextUtils.isEmpty(audioNameEt.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Upload incomplete, please name your " +
                            "audio.", Toast.LENGTH_SHORT).show();
                } else {
                    FirebaseUser user = mAuth.getCurrentUser();

                    final String audioName = audioNameEt.getText().toString().trim();
                    mProgressDialog.setMessage("Uploading audio to fans...");
                    mProgressDialog.show();

                    StorageReference voiceFileStorage = mStorage.child("Artist Memo").child
                            (audioName);

                    Uri uri = Uri.fromFile(new File(voiceStoragePath));
                    StorageMetadata metadata = new StorageMetadata.Builder()
                            .setContentType("audio.3pg")
                            .setCustomMetadata("Author", user != null ? user.getDisplayName() : null)
                            .build();

                    voiceFileStorage.putFile(uri, metadata).addOnSuccessListener(new OnSuccessListener<UploadTask
                            .TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            String url = taskSnapshot.getDownloadUrl().toString();
                            fireBaseProvider.createAudioMessage(artist.getId(), audioNameEt.getText().toString(), url);
                            mProgressDialog.dismiss();
                        }
                    });

                    sendToFansBt.setEnabled(false);
                }
            }
        });

        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        alertDialog.show();
    }

    private void activityIntent(Class intentClass) {
        Intent intent = new Intent(getApplicationContext(), intentClass);
        startActivity(intent);
    }

    public static void open(Context context, Artist artist) {
        Intent voiceRecordIntent = new Intent(context, VoiceRecordActivity.class);
        voiceRecordIntent.putExtra(ARTIST_EXTRA, artist);
        context.startActivity(voiceRecordIntent);
    }
}

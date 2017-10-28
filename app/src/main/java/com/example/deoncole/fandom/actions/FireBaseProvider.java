package com.example.deoncole.fandom.actions;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.example.deoncole.fandom.model.Artist;
import com.example.deoncole.fandom.model.AudioMessage;
import com.example.deoncole.fandom.model.Connection;
import com.example.deoncole.fandom.model.Fan;
import com.example.deoncole.fandom.model.UserType;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;

import static com.example.deoncole.fandom.model.Artist.ARTISTS_REF;
import static com.example.deoncole.fandom.model.AudioMessage.AUDIO_MESSAGES_REF;
import static com.example.deoncole.fandom.model.Connection.CONNECTIONS_REF;

public class FireBaseProvider {

    private FirebaseAuth mAuth;
    private UserType mConnectedUserType;
    @Nullable private Artist mConnectedArtist;
    @Nullable private Fan mConnectedFan;

    public FireBaseProvider(FirebaseAuth mAuth) {
        this.mAuth = mAuth;
    }

    public void createFan(final String email, final String name, final String password,
                          @Nullable Bitmap userBitmap, Activity activity, UserCreationListener listener) {
        createAccount(email, name, password, userBitmap, activity, listener, UserType.FAN);
    }

    public void createArtist(final String email, final String name, final String password,
                             @Nullable Bitmap userBitmap, Activity activity, UserCreationListener listener) {
        createAccount(email, name, password, userBitmap, activity, listener, UserType
                .ARTIST);
    }

    private void createAccount(final String email, final String name,
                               final String password, final Bitmap userBitmap, final Activity activity,
                               final UserCreationListener listener, final UserType userType) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(activity,
                        new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    //TODO: check if fan or artist
                                    final String firebaseUserUid = task.getResult().getUser().getUid();
                                    if (userBitmap == null) {
                                        createUser(firebaseUserUid, name, userType, null, listener);
                                    } else {
                                        saveUserAccountImage(activity, firebaseUserUid, userBitmap, new
                                                OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                    @Override
                                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                                        createUser(firebaseUserUid, name, userType,
                                                                taskSnapshot.getDownloadUrl(),
                                                                listener);
                                                    }
                                                });
                                    }
                                } else {
                                    Exception exception = task.getException();
                                    displayFailure(exception, activity);
                                }
                            }
                        });
    }

    private void displayFailure(Exception exception, Context context) {
        Toast.makeText(context, exception.getMessage(),
                Toast.LENGTH_LONG).show();
    }

    private void createUser(String firebaseUserUid, String name, UserType userType,
                            @Nullable Uri imageUrl, UserCreationListener listener) {
        if (userType == UserType.FAN) {
            createFan(firebaseUserUid, name, imageUrl, listener);
        } else {
            createArtist(firebaseUserUid, name, imageUrl, listener);
        }
    }

    public void fetchCurrentUser(@NonNull final OnFetchCurrentUserListener onFetchCurrentUserListener) {
        final FirebaseUser firebaseUser = mAuth.getCurrentUser();
        final DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                onFetchCurrentUserListener.onSuccess();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    if ("fans".equals(snapshot.getKey())) {

                        if (snapshot.hasChild(Fan.FAN_NAME_COL)) {
                            Fan fan = snapshot.getValue(Fan.class);
                            onFetchCurrentUserListener.onNewFan(fan, firebaseUser);
                        } else {
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                Fan fan = shot.getValue(Fan.class);
                                onFetchCurrentUserListener.onNewFan(fan, firebaseUser);
                            }
                        }
                    } else if ("artists".equals(snapshot.getKey())) {
                        if (snapshot.hasChild(Artist.ARTIST_NAME_COL)) {
                            Artist artist = snapshot.getValue(Artist.class);
                            artist.setId(snapshot.getKey());
                            onFetchCurrentUserListener.onNewArtist(artist, firebaseUser);
                        } else {
                            for (DataSnapshot shot : snapshot.getChildren()) {
                                Artist artist = shot.getValue(Artist.class);
                                artist.setId(shot.getKey());
                                onFetchCurrentUserListener.onNewArtist(artist, firebaseUser);
                            }
                        }
                    }
                }
            }

            @Override
            public void onCancelled(final DatabaseError databaseError) {

            }
        });
    }

    public void setConnectedFan(final Fan fan, final UserType userType) {
        mConnectedFan = fan;
        mConnectedUserType = userType;
    }

    public void setConnectedArtist(final Artist artist, final UserType userType) {
        mConnectedArtist = artist;
        mConnectedUserType = userType;
    }

    public String getConnectedUserUid() {
        return mAuth.getCurrentUser().getUid();
    }

    public interface OnFetchCurrentUserListener {
        void onSuccess();
        void onError(DatabaseError error);
        void onNewFan(Fan fan, FirebaseUser firebaseUser);
        void onNewArtist(Artist artist, FirebaseUser firebaseUser);
    }

    public interface UserCreationListener {

        void onUserCreated(String firebaseUserId, String userUid);
    }

    private Task<Void> createFan(final String firebaseUserUid, String name, @Nullable Uri
            imageUrl, final UserCreationListener listener) {
        DatabaseReference fansDbRef = FirebaseDatabase.getInstance().getReference("fans").push();
        final String userUid = fansDbRef.getKey();
        Fan fan = new Fan(firebaseUserUid, name, imageUrl != null ? imageUrl.toString() : null);
        return fansDbRef.setValue(fan)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onUserCreated(firebaseUserUid, userUid);
                    }
                });
    }

    public void updateUser(String firebaseUserUid, String name, @Nullable Uri imageUrl,
                           final OnSuccessListener<Void> onSuccessListener) {
        if (mConnectedUserType == UserType.FAN) {
            updateFan(firebaseUserUid, name, imageUrl, onSuccessListener);
        } else {
            updateArtist(firebaseUserUid, name, imageUrl, onSuccessListener);
        }
    }

    private Task<Void> updateFan(final String firebaseUserUid, final String name, @Nullable final Uri
            imageUrl, final OnSuccessListener<Void> onSuccessListener) {
        DatabaseReference fansDbRef = FirebaseDatabase.getInstance().getReference("fans");
        Fan fan = new Fan(firebaseUserUid, name, imageUrl != null ? imageUrl.toString() : null);
        return fansDbRef.setValue(fan)
                .addOnSuccessListener(onSuccessListener)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void aVoid) {
                        if (mConnectedFan != null) {
                            mConnectedFan.setFanName(name);
                            mConnectedFan.setImageUrl(imageUrl.toString());
                        }
                    }
                });
    }

    private Task<Void> createArtist(final String firebaseUserUid, String artistName,
                                    @Nullable Uri imageUrl, final UserCreationListener listener) {
        DatabaseReference artistsDbRef = FirebaseDatabase.getInstance().getReference(ARTISTS_REF)
                .push();
        final String artistUid = artistsDbRef.getKey();
        return artistsDbRef
                .setValue(new Artist(firebaseUserUid, artistName, imageUrl != null ? imageUrl
                        .toString() : null))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        listener.onUserCreated(firebaseUserUid, artistUid);
                    }
                });
    }

    private Task<Void> updateArtist(final String firebaseUserUid, final String artistName,
                                    @Nullable final Uri imageUrl, final OnSuccessListener<Void> onSuccessListener) {
        DatabaseReference artistsDbRef = FirebaseDatabase.getInstance().getReference(ARTISTS_REF);
        return artistsDbRef
                .setValue(new Artist(firebaseUserUid, artistName, imageUrl != null ? imageUrl
                        .toString() : null))
                .addOnSuccessListener(onSuccessListener)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(final Void aVoid) {
                        if (mConnectedArtist != null) {
                            mConnectedArtist.setArtistName(artistName);
                            mConnectedArtist.setImageUrl(imageUrl.toString());
                        }
                    }
                });
    }

    public Task<Void> createConnection(String artistId, String connectedUserUid) {
        DatabaseReference connectionsDbRef = FirebaseDatabase.getInstance()
                .getReference("connections").push();
        final String connectionUid = connectionsDbRef.getKey();
        return connectionsDbRef
                .setValue(new Connection(artistId, connectedUserUid))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
    }

    public void removeConnection(final String artistId, final String connectedUserUid) {
        DatabaseReference connectionsDbRef = FirebaseDatabase.getInstance()
                .getReference(CONNECTIONS_REF);
        connectionsDbRef
                .orderByChild("userUid")
                .startAt(connectedUserUid).endAt(connectedUserUid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        for (DataSnapshot shot : dataSnapshot.getChildren()) {
                            Connection connection = shot.getValue(Connection.class);
                            if (connection.getArtistId().equals(artistId)) {
                                shot.getRef().setValue(null);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    public Task<Void> createAudioMessage(final String artistId, String title, String url) {
        DatabaseReference audioMessagesDbRef = FirebaseDatabase.getInstance()
                .getReference(AUDIO_MESSAGES_REF).push();
        final String audioMessageUid = audioMessagesDbRef.getKey();
        return audioMessagesDbRef
                .setValue(new AudioMessage(artistId, title, url))
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d("TEST", "FireBaseProvider . onSuccess: audio messages");
                    }
                });
    }

    private void saveUserAccountImage(final Context context, String userUid, @NonNull Bitmap bitmap,
                                      OnSuccessListener<UploadTask.TaskSnapshot>
                                              onSuccessListener) {
        saveImage(context, userUid + "-image", bitmap, onSuccessListener);
    }

    public void saveBroadcastImage(final Context context, String broadcastId, @NonNull Bitmap bitmap,
                                   OnSuccessListener<UploadTask.TaskSnapshot>
                                           onSuccessListener) {
        saveImage(context, broadcastId + "-broadcast", bitmap, onSuccessListener);
    }

    private void saveImage(final Context context, String imageName, @NonNull Bitmap bitmap,
                           OnSuccessListener<UploadTask.TaskSnapshot>
                                   onSuccessListener) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference imageRef = FirebaseStorage.getInstance()
                .getReference().child(imageName);

        UploadTask uploadTask = imageRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
                displayFailure(exception, context);
            }
        }).addOnSuccessListener(onSuccessListener);
    }

    public UserType getConnectedUserType() {
        return mConnectedUserType;
    }

    @Nullable
    public Artist getConnectedArtist() {
        return mConnectedArtist;
    }

    @Nullable
    public Fan getConnectedFan() {
        return mConnectedFan;
    }
}


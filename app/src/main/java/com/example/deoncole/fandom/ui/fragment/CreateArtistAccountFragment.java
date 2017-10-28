package com.example.deoncole.fandom.ui.fragment;


import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.deoncole.fandom.FandomApp;
import com.example.deoncole.fandom.MusicianDashboardActivity;
import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.actions.FireBaseProvider;
import com.google.firebase.auth.FirebaseAuth;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CreateArtistAccountFragment extends Fragment {

    private static final int PICK_PHOTO_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;
    private ImageView createArtistImg;
    private Button createArtistActBt;
    private EditText artistNameEt, artistEmailEt, artistPasswordEt;
    private boolean isFan;
    private Bitmap userBitmap;
    private ProgressDialog mProgressDialog;

    private String mCurrentArtistPhotoPath;
    private String fileProviderString = "com.example.deoncole.fileprovider";
    private FirebaseAuth mAuth;

    File photoFile;
    Uri photoURI;

    public static CreateArtistAccountFragment newInstance() {
        CreateArtistAccountFragment cmFrag = new CreateArtistAccountFragment();
        return cmFrag;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_musician_account, container, false);

        createArtistImg = (ImageView) v.findViewById(R.id.createArtistImg);
        createArtistActBt = (Button) v.findViewById(R.id.createMusicActBt);
        artistEmailEt = (EditText) v.findViewById(R.id.artistEmailEt);
        artistNameEt = (EditText) v.findViewById(R.id.artistNameEt);
        artistPasswordEt = (EditText) v.findViewById(R.id.artistPassEt);

        mProgressDialog = new ProgressDialog(getContext());

        setHasOptionsMenu(true);

        mAuth = FirebaseAuth.getInstance();

        createArtistActBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mProgressDialog.setMessage("Creating artist account...");
                mProgressDialog.show();
                isFan = false;
                if (TextUtils.isEmpty(artistNameEt.getText().toString()) ||
                        TextUtils.isEmpty(artistEmailEt.getText().toString()) ||
                        TextUtils.isEmpty(artistPasswordEt.getText().toString())) {

                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), "Please fill out all fields to continue", Toast
                            .LENGTH_SHORT).show();

                } else if (checkImage()) {

                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), "Please add a profile picture", Toast
                            .LENGTH_SHORT).show();

                } else {
                    String artistName = artistNameEt.getText().toString();
                    FireBaseProvider fireBaseProvider = ((FandomApp) getContext().getApplicationContext()).getFireBaseProvider();
                    fireBaseProvider.createArtist(artistEmailEt.getText().toString(), artistName,
                            artistPasswordEt.getText().toString(), userBitmap, getActivity(), new
                                    FireBaseProvider.UserCreationListener() {
                                        @Override
                                        public void onUserCreated(String firebaseUserId, String userUid) {
                                            startActivity(MusicianDashboardActivity
                                                    .createIntent(getActivity(), firebaseUserId));
                                            mProgressDialog.dismiss();
                                        }
                                    });
                }
            }
        });

        createArtistImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent galleryIntent = new Intent(Intent.ACTION_PICK,
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(galleryIntent, PICK_PHOTO_REQUEST_CODE);
            }
        });
        return v;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                break;
            case R.id.action_camera:
                dispatchTakePictureIntent();
                Toast.makeText(getActivity(), "Set Profile Picture", Toast.LENGTH_SHORT).show();
                break;
        }
        return true;
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {

            photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException exception) {

            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(getContext(), fileProviderString,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    //TODO: the common logic here for loading an image should be done in one place
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri contentURI = data.getData();
                try {
                    userBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                            contentURI);
                    Toast.makeText(getActivity(), "Image Loaded!", Toast.LENGTH_SHORT).show();
                    createArtistImg.setImageBitmap(userBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Could not load image", Toast.LENGTH_SHORT)
                            .show();
                }
            }

        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addImageToGallery(mCurrentArtistPhotoPath, getActivity());
            createArtistImg.setImageURI(photoURI);
        }
    }

    private File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);

        mCurrentArtistPhotoPath = image.getAbsolutePath();
        return image;
    }

    public static void addImageToGallery(final String filePath, final Context context) {
        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.MediaColumns.DATA, filePath);

        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    private boolean checkImage() {
        return createArtistImg.getDrawable().getConstantState().equals(getResources().getDrawable(R
                .drawable.profile).getConstantState());
    }

}


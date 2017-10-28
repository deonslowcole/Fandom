package com.example.deoncole.fandom.ui.fragment;

import android.annotation.SuppressLint;
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

import com.example.deoncole.fandom.ArtistFeedActivity;
import com.example.deoncole.fandom.FandomApp;
import com.example.deoncole.fandom.R;
import com.example.deoncole.fandom.actions.FireBaseProvider;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static android.app.Activity.RESULT_OK;

public class CreateFanAccountFragment extends Fragment {

    private static final int PICK_PHOTO_REQUEST_CODE = 1;
    private static final int REQUEST_IMAGE_CAPTURE = 2;

    private ImageView createUserImg;
    private EditText createUserEmailEt, createUserPassEt, createUserNameEt, createUserZipEt;
    private Bitmap userBitmap;
    private String mCurrentFanPhotoPath;

    private ProgressDialog mProgressDialog;
    private Uri photoURI;

    public static CreateFanAccountFragment newInstance() {
        return new CreateFanAccountFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_create_user_account, container, false);

        createUserImg = (ImageView) v.findViewById(R.id.createUserImg);
        createUserEmailEt = (EditText) v.findViewById(R.id.createUserEmailEt);
        createUserPassEt = (EditText) v.findViewById(R.id.createUserPassEt);
        createUserNameEt = (EditText) v.findViewById(R.id.createUserNameEt);
//        createUserZipEt = (EditText) v.findViewById(R.id.createUserZipCodeEt);
        final Button createActBt = (Button) v.findViewById(R.id.createActBt);

        mProgressDialog = new ProgressDialog(getContext());

        setHasOptionsMenu(true);

        createActBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = createUserEmailEt.getText().toString();
                String password = createUserPassEt.getText().toString();
                String name = createUserNameEt.getText().toString();
                System.out.println("THE NAME IS " + name);
                mProgressDialog.setMessage("Creating fan account...");
                mProgressDialog.show();

                if (TextUtils.isEmpty(email) ||
                        TextUtils.isEmpty(password) ||
                        TextUtils.isEmpty(createUserNameEt.getText().toString()) ||
                        TextUtils.isEmpty(createUserZipEt.getText().toString())) {

                    mProgressDialog.dismiss();

                    Toast.makeText(getContext(), "Please fill out all fields to continue", Toast
                            .LENGTH_SHORT).show();
                } else if (checkImage()) {

                    mProgressDialog.dismiss();
                    Toast.makeText(getContext(), "Please add a profile picture", Toast
                            .LENGTH_SHORT).show();
                } else {
                    FireBaseProvider fireBaseProvider = ((FandomApp) getContext().getApplicationContext()).getFireBaseProvider();
                    fireBaseProvider.createFan(email, name, password, userBitmap,
                            getActivity(), new FireBaseProvider.UserCreationListener() {
                                @Override
                                public void onUserCreated(String firebaseUserId, String userUid) {
                                    Intent feedIntent = new Intent(getContext(), ArtistFeedActivity.class);
                                    startActivity(feedIntent);
                                    mProgressDialog.dismiss();
                                }
                            });
                }
            }
        });

        createUserImg.setOnClickListener(new View.OnClickListener() {
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
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ignored) {

            }
            if (photoFile != null) {
                final String fileProviderString = "com.example.deoncole.fileprovider";
                photoURI = FileProvider.getUriForFile(getContext(), fileProviderString,
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_PHOTO_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Uri contentURI = data.getData();
                try {
                    userBitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(),
                            contentURI);
                    Toast.makeText(getActivity(), "Image Saved!", Toast.LENGTH_SHORT).show();
                    createUserImg.setImageBitmap(userBitmap);

                } catch (IOException e) {
                    e.printStackTrace();
                    Toast.makeText(getActivity(), "Could not load image", Toast.LENGTH_SHORT)
                            .show();
                }
            }
        }

        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            addImageToGallery(mCurrentFanPhotoPath, getActivity());

            createUserImg.setImageURI(photoURI);
        }
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
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

    private boolean checkImage() {
        return createUserImg.getDrawable().getConstantState() != null &&
                createUserImg.getDrawable().getConstantState().equals(getResources().getDrawable(R
                        .drawable.profile).getConstantState());
    }
}

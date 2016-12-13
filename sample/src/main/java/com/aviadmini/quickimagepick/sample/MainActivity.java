package com.aviadmini.quickimagepick.sample;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.aviadmini.quickimagepick.PickCallback;
import com.aviadmini.quickimagepick.PickSource;
import com.aviadmini.quickimagepick.PickTriggerResult;
import com.aviadmini.quickimagepick.QiPick;
import com.aviadmini.quickimagepick.UriUtils;
import com.bumptech.glide.Glide;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import permissions.dispatcher.NeedsPermission;
import permissions.dispatcher.RuntimePermissions;

@RuntimePermissions
public class MainActivity
        extends AppCompatActivity {

    private static final String TAG = "QIP Sample";

    private static final String QIP_DIR_NAME = "QuickImagePick Sample";

    private ImageView mImageView;

    final PickCallback mCallback = new PickCallback() {

        @Override
        public void onImagePicked(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final Uri pImageUri) {

            final Context context = getApplicationContext();

            final boolean exists = UriUtils.contentExists(context, pImageUri);

            if (!exists) {

                Toast.makeText(context, "Image does not exist. WTF!?", Toast.LENGTH_SHORT)
                     .show();

                return;
            }

            final String extension = UriUtils.getFileExtension(context, pImageUri);
            Log.i(TAG, "Picked: " + pImageUri.toString() + "\nMIME type: " + UriUtils.getMimeType(context,
                    pImageUri) + "\nFile extension: " + extension + "\nRequest type: " + pRequestType);

            // Do something with Uri, for example load image into an ImageView
            Glide.with(context)
                 .load(pImageUri)
                 .fitCenter()
                 .into(mImageView);

            try {

                final String ext = extension == null ? "" : "." + extension;
                final File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), QIP_DIR_NAME);
                final File file = new File(outDir, "qip_temp" + ext);

                //noinspection ResultOfMethodCallIgnored
                outDir.mkdirs();

                // DO NOT do this on main thread. This is only for reference
                UriUtils.saveContentToFile(context, pImageUri, file);

                Toast.makeText(context, "Save complete", Toast.LENGTH_SHORT)
                     .show();

            } catch (final IOException e) {
                Toast.makeText(context, "Save failed: " + e.getMessage(), Toast.LENGTH_SHORT)
                     .show();
            }

        }

        @Override
        public void onMultipleImagesPicked(final int pRequestType, @NonNull final List<Uri> pImageUris) {

            Log.i(TAG, "Picked a few images. Uris: " + Arrays.toString(pImageUris.toArray()));

            // meh whatever, just show first picked ;D
            this.onImagePicked(PickSource.DOCUMENTS, pRequestType, pImageUris.get(0));

        }

        @Override
        public void onError(@NonNull final PickSource pPickSource, final int pRequestType, @NonNull final String pErrorString) {
            Log.e(TAG, "Err: " + pErrorString);
        }

        @Override
        public void onCancel(@NonNull final PickSource pPickSource, final int pRequestType) {
            Log.d(TAG, "Cancel: " + pPickSource.name());
        }

    };

    @Override
    protected void onCreate(@Nullable final Bundle pSavedInstanceState) {
        super.onCreate(pSavedInstanceState);

        this.setContentView(R.layout.activity_main);

        this.mImageView = (ImageView) findViewById(R.id.main_iv);

        final View btn1 = this.findViewById(R.id.btn_pick_local_jpg_webp);
        final View btn2 = this.findViewById(R.id.btn_pick_png_jpg_camera_docs);
        final View btn3 = this.findViewById(R.id.btn_pick_cam_only);

        final View.OnClickListener listener = new View.OnClickListener() {
            @Override
            public void onClick(final View pView) {
                MainActivityPermissionsDispatcher.btnCLickWithCheck(MainActivity.this, pView);
            }
        };
        btn1.setOnClickListener(listener);
        btn2.setOnClickListener(listener);
        btn3.setOnClickListener(listener);

    }

    @Override
    protected void onActivityResult(final int pRequestCode, final int pResultCode, final Intent pData) {

        if (!QiPick.handleActivityResult(getApplicationContext(), pRequestCode, pResultCode, pData, this.mCallback)) {
            super.onActivityResult(pRequestCode, pResultCode, pData);
        }

    }

    @Override
    public void onRequestPermissionsResult(final int pRequestCode, @NonNull final String[] pPermissions, @NonNull final int[] pGrantResults) {
        super.onRequestPermissionsResult(pRequestCode, pPermissions, pGrantResults);

        MainActivityPermissionsDispatcher.onRequestPermissionsResult(MainActivity.this, pRequestCode, pGrantResults);

    }

    @NeedsPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
    public void btnCLick(final View pView) {

        final File outDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), QIP_DIR_NAME);
        Log.d(TAG, outDir.getAbsolutePath() + ", can write: " + outDir.canWrite());

        // If cannot write then PickRequest will attempt to create it

        switch (pView.getId()) {

            case R.id.btn_pick_local_jpg_webp: {

                // Code below is for reference only and will not work on API 9
                @PickTriggerResult final int triggerResult;
                triggerResult = QiPick.in(this)
                                      .allowOnlyLocalContent(true)
                                      .withAllowedMimeTypes(QiPick.MIME_TYPE_IMAGE_JPEG, QiPick.MIME_TYPE_IMAGE_WEBP)
                                      .withCameraPicsDirectory(outDir)
                                      .withRequestType(1)
                                      .fromMultipleSources("All sources", PickSource.DOCUMENTS, PickSource.CAMERA, PickSource.GALLERY);

                this.solveTriggerResult(triggerResult);

                break;
            }

            case R.id.btn_pick_png_jpg_camera_docs: {

                @PickTriggerResult final int triggerResult;
                triggerResult = QiPick.in(this)
                                      .allowOnlyLocalContent(false)
                                      .withAllowedMimeTypes(QiPick.MIME_TYPE_IMAGE_JPEG, QiPick.MIME_TYPE_IMAGE_PNG)
                                      .withRequestType(2)
                                      .fromDocuments(true);

                this.solveTriggerResult(triggerResult);

                break;
            }

            case R.id.btn_pick_cam_only: {

                @PickTriggerResult final int triggerResult;
                triggerResult = QiPick.in(this)
                                      .withAllImageMimeTypesAllowed()
                                      .withCameraPicsDirectory(outDir)
                                      .withRequestType(3)
                                      .fromCamera();

                this.solveTriggerResult(triggerResult);

                break;
            }

        }

    }

    private void solveTriggerResult(final @PickTriggerResult int pTriggerResult) {

        switch (pTriggerResult) {

            case PickTriggerResult.TRIGGER_PICK_ERR_CAM_FILE: {

                Toast.makeText(this, "Could not create file to save Camera image. Make sure camera pics dir is writable", Toast.LENGTH_SHORT)
                     .show();

                break;
            }

            case PickTriggerResult.TRIGGER_PICK_ERR_NO_ACTIVITY: {

                Toast.makeText(this, "There is no Activity that can pick requested file :(", Toast.LENGTH_SHORT)
                     .show();

                break;
            }

            case PickTriggerResult.TRIGGER_PICK_ERR_NO_PICK_SOURCES: {

                Toast.makeText(this, "Dear dev, multiple source request needs at least one source!", Toast.LENGTH_SHORT)
                     .show();

                break;
            }

            case PickTriggerResult.TRIGGER_PICK_OK: {
                break;// all good, do nothing
            }

        }

    }

}

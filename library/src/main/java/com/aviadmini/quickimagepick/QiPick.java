package com.aviadmini.quickimagepick;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;

import java.io.File;
import java.util.ArrayList;

/**
 * QuickImagePick entry point in v2.x
 * <br><br>
 * Create a {@link PickRequest} using one of {@code in(*)} methods and use chained calls to set up and launch the pick request
 *
 * @see <a href="https://github.com/aviadmini/quickimagepick">GitHub repo</a>
 * @since v2.0.0
 */
@SuppressWarnings("unused")
public class QiPick {

    static final String PREFS_REQUEST_TYPE              = "qip_req_type";
    static final String PREFS_LAST_CAMERA_URI           = "qip_last_cam_uri";
    static final String PREFS_CAMERA_DIR                = "qip_cam_dir";
    static final String PREFS_ALLOWED_MIME_TYPE         = "qip_allowed_mime_type";
    static final String PREFS_ALLOWED_MIME_TYPES_KITKAT = "qip_allowed_mime_types_kitkat";
    static final String PREFS_ALLOW_LOCAL_CONTENT_ONLY  = "qip_local_content_only";

    static final int REQ_CAMERA    = 46211;
    static final int REQ_GALLERY   = 46212;
    static final int REQ_DOCUMENTS = 46213;
    static final int REQ_MULTIPLE  = 46214;

    public static final String ERR_CAMERA_NULL_RESULT         = "Camera returned bad/null data";
    public static final String ERR_CAMERA_CANNOT_WRITE_OUTPUT = "App cannot write to specified camera output directory";
    public static final String ERR_GALLERY_NULL_RESULT        = "Gallery returned bad/null data";
    public static final String ERR_DOCS_NULL_RESULT           = "Documents returned bad/null data";

    public static final String MIME_TYPE_IMAGES_ALL = "image/*";
    public static final String MIME_TYPE_IMAGE_BMP  = "image/bmp";
    public static final String MIME_TYPE_IMAGE_GIF  = "image/gif";
    public static final String MIME_TYPE_IMAGE_JPEG = "image/jpeg";
    public static final String MIME_TYPE_IMAGE_PNG  = "image/png";
    public static final String MIME_TYPE_IMAGE_WEBP = "image/webp";

    static final boolean API_19 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;
    static final boolean API_23 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.M;
    static final boolean API_16 = Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN;

    private QiPick() {}

    /**
     * @param pActivity Activity to which pick result will be returned
     * @return PickRequest to set up pick request and trigger it
     */
    public static PickRequest in(@NonNull final Activity pActivity) {
        return new PickRequest(pActivity);
    }

    /**
     * @param pFragment Support fragment to which pick result will be returned
     * @return PickRequest to set up pick request and trigger it
     */
    public static PickRequest in(@NonNull final Fragment pFragment) {
        return new PickRequest(pFragment);
    }

    /**
     * @param pFragment Fragment to which pick result will be returned
     * @return PickRequest to set up pick request and trigger it
     */
    public static PickRequest in(@NonNull final android.app.Fragment pFragment) {
        return new PickRequest(pFragment);
    }

    // ==== RESULT HANDLING ==== //

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     *
     * @param pFragment    support fragment
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     */
    public static boolean handleActivityResult(@NonNull final Fragment pFragment, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final PickCallback pCallback) {
        return handleActivityResult(pFragment.getContext(), pRequestCode, pResultCode, pData, pCallback);
    }

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     *
     * @param pFragment    fragment
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     */
    @SuppressLint("NewApi")
    public static boolean handleActivityResult(@NonNull final android.app.Fragment pFragment, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final PickCallback pCallback) {
        return handleActivityResult(API_23 ? pFragment.getContext() : pFragment.getActivity(), pRequestCode, pResultCode, pData, pCallback);
    }

    /**
     * Use this in {@code onActivityResult(...)} of Activity or Fragment to retrieve image {@link Uri}
     * <br>
     * Note: for Fragments it's advised to use Fragment-specific overloads of this method
     *
     * @param pContext     app {@link Context}
     * @param pRequestCode request code from {@code onActivityResult(...)} parameter
     * @param pResultCode  result code from {@code onActivityResult(...)} parameter
     * @param pData        Intent from {@code onActivityResult(...)} parameter
     * @param pCallback    result callback
     * @return true if result was accepted by QuickImagePick
     * @see #handleActivityResult(Fragment, int, int, Intent, PickCallback)
     * @see #handleActivityResult(android.app.Fragment, int, int, Intent, PickCallback)
     */
    @SuppressLint("NewApi")
    public static boolean handleActivityResult(@NonNull final Context pContext, final int pRequestCode, final int pResultCode,
                                               @Nullable final Intent pData, @NonNull final PickCallback pCallback) {

        if (pRequestCode != REQ_CAMERA && pRequestCode != REQ_GALLERY && pRequestCode != REQ_DOCUMENTS && pRequestCode != REQ_MULTIPLE) {
            return false;
        }

        final int requestType = PreferenceManager.getDefaultSharedPreferences(pContext)
                                                 .getInt(PREFS_REQUEST_TYPE, 0);

        if (pResultCode == Activity.RESULT_OK) {

            boolean clipData = false;

            if (API_16 == true && pData != null) {
                clipData = pData.getClipData() != null;
            }

            if (pRequestCode == REQ_DOCUMENTS) {
                handleResultFromDocuments(requestType, pCallback, pData);
            } else if (pRequestCode == REQ_GALLERY) {
                handleResultFromGallery(requestType, pCallback, pData);
            } else if (pRequestCode == REQ_CAMERA) {
                handleResultFromCamera(pContext, requestType, pCallback, pData);
            } else if ((pData == null || pData.getData() == null) && clipData == false) {
                handleResultFromCamera(pContext, requestType, pCallback, pData);
            } else {
                handleResultFromDocuments(requestType, pCallback, pData);
            }

        } else {

            if (pRequestCode == REQ_DOCUMENTS) {
                pCallback.onCancel(PickSource.DOCUMENTS, requestType);
            } else if (pRequestCode == REQ_GALLERY) {
                pCallback.onCancel(PickSource.GALLERY, requestType);
            } else if (pRequestCode == REQ_CAMERA) {

                pCallback.onCancel(PickSource.CAMERA, requestType);

                deleteLastCameraPic(pContext);

            } else {

                if (pData == null || pData.getData() == null) {

                    pCallback.onCancel(PickSource.CAMERA, requestType);

                    deleteLastCameraPic(pContext);

                } else {
                    pCallback.onCancel(PickSource.DOCUMENTS, requestType);
                }

            }

        }

        return true;
    }

    private static void handleResultFromCamera(@NonNull final Context pContext, final int pRequestType, @NonNull final PickCallback pCallback,
                                               @Nullable final Intent pData) {

        final File cameraPicsDir = getCameraPicsDirectory(pContext);
        if (cameraPicsDir == null || !cameraPicsDir.canWrite()) {

            pCallback.onError(PickSource.CAMERA, pRequestType, ERR_CAMERA_CANNOT_WRITE_OUTPUT);

            return;
        }

        final Uri pictureUri = getLastCameraUri(pContext);

        if (pictureUri == null) {
            pCallback.onError(PickSource.CAMERA, pRequestType, ERR_CAMERA_NULL_RESULT);
        } else {
            pCallback.onImagePicked(PickSource.CAMERA, pRequestType, pictureUri);

        }

    }

    private static void handleResultFromGallery(final int pRequestType, @NonNull final PickCallback pCallback, @Nullable final Intent pData) {

        final Uri pictureUri = pData == null ? null : pData.getData();

        if (pictureUri == null) {
            pCallback.onError(PickSource.GALLERY, pRequestType, ERR_GALLERY_NULL_RESULT);
        } else {
            pCallback.onImagePicked(PickSource.GALLERY, pRequestType, pictureUri);
        }

    }

    private static void handleResultFromDocuments(final int pRequestType, @NonNull final PickCallback pCallback, @Nullable final Intent pData) {

        final Uri pictureUri = pData == null ? null : pData.getData();


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            if (pData.getClipData() != null) {
                ClipData clipData = pData.getClipData();
                ArrayList<Uri> mArrayUri = new ArrayList<Uri>();
                for (int i = 0; i < clipData.getItemCount(); i++) {
                    ClipData.Item item = clipData.getItemAt(i);
                    Uri uri = item.getUri();
                    mArrayUri.add(uri);
                }
                pCallback.onMultiImagePicked(PickSource.DOCUMENTS, pRequestType, mArrayUri);
                return;
            }
        }

        if (pictureUri == null) {
            pCallback.onError(PickSource.DOCUMENTS, pRequestType, ERR_DOCS_NULL_RESULT);
        } else {
            pCallback.onImagePicked(PickSource.DOCUMENTS, pRequestType, pictureUri);
        }

    }

    // ==== CAMERA DIR ==== //

    /**
     * @param pContext app {@link Context}
     * @return directory where pictures taken by camera apps will be saved or null if an error occurs.
     * By default it's a pictures directory on external storage.
     */
    public static File getCameraPicsDirectory(@NonNull final Context pContext) {

        final String camDirPath = PreferenceManager.getDefaultSharedPreferences(pContext)
                                                   .getString(QiPick.PREFS_CAMERA_DIR, null);

        final File dir = camDirPath == null ? pContext.getExternalFilesDir(Environment.DIRECTORY_PICTURES) : new File(camDirPath);

        if (dir != null) {
            //noinspection ConstantConditions,ResultOfMethodCallIgnored
            dir.mkdirs();
        }

        return dir;
    }

    // ==== LAST CAM PIC ==== //

    /**
     * @param pContext app {@link Context}
     * @return Uri of last camera pic. Not necessarily valid content
     */
    public static Uri getLastCameraUri(@NonNull final Context pContext) {

        final String uriString = PreferenceManager.getDefaultSharedPreferences(pContext)
                                                  .getString(QiPick.PREFS_LAST_CAMERA_URI, null);

        return uriString == null ? null : Uri.parse(uriString);
    }

    /**
     * @param pContext app {@link Context}
     * @return number of rows deleted from content provider
     */
    public static int deleteLastCameraPic(@NonNull final Context pContext) {

        final Uri uri = getLastCameraUri(pContext);
        if (uri != null) {
            return UriUtils.deleteContent(pContext, uri);
        }

        return 0;
    }

    // ==== //

}

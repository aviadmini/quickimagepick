package com.aviadmini.quickimagepick;

import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.annotation.NonNull;

public class FileUtils {

    @SuppressLint("NewApi")
    public static String getPath(@NonNull final Context pContext, @NonNull final Uri pUri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(pContext, pUri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(pUri)) {
                final String docId = DocumentsContract.getDocumentId(pUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(pUri)) {

                final String id = DocumentsContract.getDocumentId(pUri);
                final Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(pContext, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(pUri)) {
                final String docId = DocumentsContract.getDocumentId(pUri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{split[1]};

                return getDataColumn(pContext, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(pUri.getScheme())) {
            return getDataColumn(pContext, pUri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(pUri.getScheme())) {
            return pUri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param pContext       The context.
     * @param pUri           The Uri to query.
     * @param pSelection     (Optional) Filter used in the query.
     * @param pSelectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(@NonNull final Context pContext, @NonNull final Uri pUri, final String pSelection,
                                       final String[] pSelectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {column};

        try {
            cursor = pContext.getContentResolver()
                             .query(pUri, projection, pSelection, pSelectionArgs, null);
            if (cursor != null && cursor.moveToFirst()) {
                final int column_index = cursor.getColumnIndex(column);
                return column_index == -1 ? null : cursor.getString(column_index);
            }
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * @param pUri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(@NonNull final Uri pUri) {
        return "com.android.externalstorage.documents".equals(pUri.getAuthority());
    }

    /**
     * @param pUri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(@NonNull final Uri pUri) {
        return "com.android.providers.downloads.documents".equals(pUri.getAuthority());
    }

    /**
     * @param pUri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(@NonNull final Uri pUri) {
        return "com.android.providers.media.documents".equals(pUri.getAuthority());
    }

    private FileUtils() {
    }

}

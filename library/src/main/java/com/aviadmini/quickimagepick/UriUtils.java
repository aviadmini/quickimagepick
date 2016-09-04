package com.aviadmini.quickimagepick;

import android.content.ContentResolver;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;
import android.webkit.MimeTypeMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Class contains some useful methods to work with {@link Uri}. Mainly designed for image Uris returned by this library
 *
 * @since v2.0.0
 */
public class UriUtils {

    /**
     * @param pContext app {@link Context}
     * @param pUri     uri to get MIME type for
     * @return MIME type for {@link Uri} content or null if cannot determine
     */
    @Nullable
    public static String getMimeType(@NonNull final Context pContext, @NonNull final Uri pUri) {

        // first try to get it from content resolver
        final String contentResolverChoice = pContext.getContentResolver()
                                                     .getType(pUri);

        // if content resolver fails and it's a file Uri then try guessing by extension
        if (contentResolverChoice == null && ContentResolver.SCHEME_FILE.equals(pUri.getScheme())) {

            final String extension = MimeTypeMap.getFileExtensionFromUrl(pUri.toString());

            return extension == null ? null : MimeTypeMap.getSingleton()
                                                         .getMimeTypeFromExtension(extension);
        } else {
            return contentResolverChoice;
        }

    }

    /**
     * @param pContext app {@link Context}
     * @param pUri     uri to get MIME type for
     * @return most common file extension for {@link Uri} content or null if cannot determine
     */
    public static String getFileExtension(@NonNull final Context pContext, @NonNull final Uri pUri) {

        // first try to get extension from mime type
        final String mimeType = pContext.getContentResolver()
                                        .getType(pUri);

        if (mimeType != null) {
            return MimeTypeMap.getSingleton()
                              .getExtensionFromMimeType(mimeType);
        } else {
            // if content resolver fails then try to get it from url
            return MimeTypeMap.getFileExtensionFromUrl(pUri.toString());
        }

    }

    /**
     * Saves Uri content to a File. Strongly recommended to execute in background thread
     *
     * @param pContext app {@link Context}
     * @param pUri     Uri to get content from
     * @param pFile    File to which content will be saved. Caller should have permission to write to it
     * @throws IOException if the provided Uri could not be opened or
     *                     if the provided File could not be opened for writing or if writing operation failed
     */
    @WorkerThread
    public static void saveContentToFile(@NonNull final Context pContext, @NonNull final Uri pUri, @NonNull final File pFile)
            throws RuntimeException, IOException {

        FileOutputStream fos = null;
        InputStream is = null;

        try {

            is = pContext.getContentResolver()
                         .openInputStream(pUri);

            if (is != null) {

                fos = new FileOutputStream(pFile);

                final byte[] buffer = new byte[1024];
                int len;
                while ((len = is.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                }

            } else {
                throw new IOException("ContentResolver returned null InputStream for the File");
            }

        } finally {

            if (is != null) {

                try {
                    is.close();
                } catch (final IOException e) {
                    // silently ignore
                }

            }

            if (fos != null) {

                try {
                    fos.close();
                } catch (final IOException e) {
                    // silently ignore
                }

            }

        }

    }

    /**
     * @param pContext app {@link Context}
     * @param pUri     uri of content that will be deleted
     * @return number of rows deleted from content provider
     */
    public static int deleteContent(@NonNull final Context pContext, @NonNull final Uri pUri) {
        return pContext.getContentResolver()
                       .delete(pUri, null, null);
    }

    private UriUtils() {}

}

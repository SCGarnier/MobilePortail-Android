package com.briccsquad.mobileportail;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * A collection of utility functions that fill the void.
 */
public final class Utils {
    private static final int BYTE_BUFFER_SIZE = 1024;

    public static byte[] readStream(@NonNull InputStream inputStream) throws IOException {
        byte[] b = new byte[BYTE_BUFFER_SIZE];
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        int c;

        while ((c = inputStream.read(b)) != -1) {
            os.write(b, 0, c);
        }

        return os.toByteArray();
    }

    public static <T extends View> T fromTemplate(@NonNull AppCompatActivity activity, int layoutId) {
        LayoutInflater inflater = activity.getLayoutInflater();
        return (T) inflater.inflate(layoutId, null);
    }

    @NonNull
    public static String calculateMD5(byte[] arr) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            // Return a string representation of the hexadecimal value
            return new BigInteger(md.digest(arr)).toString(16);
        } catch (NoSuchAlgorithmException e) {
            // Seriously should never come to this
            return "";
        }
    }

    @NonNull
    public static String combinePaths(@NonNull String path1, @NonNull String path2) {
        File file1 = new File(path1);
        File file2 = new File(file1, path2);
        return file2.getPath();
    }

    public static void pipeStream(@NonNull InputStream input, @NonNull OutputStream output)
            throws IOException {
        byte[] buffer = new byte[BYTE_BUFFER_SIZE];
        int bytesRead;
        while ((bytesRead = input.read(buffer)) != -1) {
            output.write(buffer, 0, bytesRead);
        }
    }

    public static void recursiveDelete(File f) {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                recursiveDelete(c);
            }
        }

        f.delete();
    }
}

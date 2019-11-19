package com.example.encount_1110_ohno;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.File;

public class ImageGet {

    public static String getPath(Context context, Uri uri) {
        ContentResolver contentResolver = context.getContentResolver();
        String[] columns = { MediaStore.Images.Media.DATA };
        Cursor cursor = contentResolver.query(uri, columns, null, null, null);
        cursor.moveToFirst();
        String path = cursor.getString(0);
        cursor.close();
        return path;
    }

    /*public static String getPath2(Context context,Uri uri){
        String[] projection = {MediaStore.MediaColumns.DATA};
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            String path = null;
            if (cursor.moveToFirst()) {
                path = cursor.getString(0);
            }
            cursor.close();
            if (path != null) {
                File file = new File(path);
            }
        }
        return ;
    }*/

    //File myFile = new File(uri.toString());
    //myFile.getAbsolutePath()

}

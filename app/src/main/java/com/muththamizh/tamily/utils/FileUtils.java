package com.muththamizh.tamily.utils;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Toast;
import com.muththamizh.tamily.R;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Objects;

public class FileUtils {

    private static final String TAG = "TAG";

    static OutputStream outputStream ;

    public static void downloadFile(View view, Context context){
        try {
            view.setDrawingCacheEnabled(true);
            view.buildDrawingCache();
            Bitmap bm = view.getDrawingCache();

            File filePath = null;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q)
            {
                //filePath = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS) + "/" + context.getResources().getString(R.string.app_name));

                ContentResolver resolver = context.getContentResolver();
                ContentValues contentValues = new ContentValues();
                contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, System.currentTimeMillis()+".jpg");
                contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg");
                contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + File.separator + context.getResources().getString(R.string.app_name));
                contentValues.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000);
                contentValues.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                contentValues.put(MediaStore.MediaColumns.IS_PENDING, 1);
                Uri imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues);
                outputStream = resolver.openOutputStream(Objects.requireNonNull(imageUri));

                try {
                    bm.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.close();
                    Toast.makeText(context, "Image Saved", Toast.LENGTH_SHORT).show();

                } catch (Exception e) {
                    Toast.makeText(context, "Image not Saved:"+e.getMessage(), Toast.LENGTH_SHORT).show();

                    e.printStackTrace();
                } finally {
                    contentValues.clear();
                    contentValues.put(MediaStore.MediaColumns.IS_PENDING, 0);
                    resolver.update(imageUri, contentValues, null, null);
                }


            } else {
                filePath = new File(Environment.getExternalStorageDirectory() + File.separator + context.getResources().getString(R.string.app_name));

                //File filePath = new File(Environment.getExternalStorageDirectory()+File.separator+context.getResources().getString(R.string.app_name));
                // File file = new File(filePath+File.separator+System.currentTimeMillis()+".png");
                File file = null;
                if (!filePath.exists()) {
                    filePath.mkdirs();
                }
                file = new File(filePath, System.currentTimeMillis() + ".png");
                FileOutputStream fileOutputStream = new FileOutputStream(file);
                bm.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                fileOutputStream.close();
                view.invalidate();
                view.setDrawingCacheEnabled(false);
                refreshGallery(context, filePath);
                Toast.makeText(context, "Image Saved successfully...!!", Toast.LENGTH_SHORT).show();
            }
        }catch (Exception e){
            e.printStackTrace();
            Toast.makeText(context, "failed to store:"+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public static void refreshGallery(Context context, File f) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Intent mediaScanIntent = new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
            Uri fileUri = Uri.fromFile(f); //out is your output file
            mediaScanIntent.setData(fileUri);
            context.sendBroadcast(mediaScanIntent);
        } else {
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));
        }
    }

}

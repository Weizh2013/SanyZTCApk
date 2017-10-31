package sany.com.mmpapp.Util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.SyncStateContract;
import android.support.v4.content.FileProvider;

import java.io.File;
import java.io.FileNotFoundException;

/**
 * Created by sunj7 on 17-1-18.
 */
public class CutPictureUtils {
    private Activity activity;
    public final int CROP_ACTIVITY_RESULT=3;
    public CutPictureUtils(Activity activity){
        this.activity=activity;
    }
    File imageTemp=new File(Environment.getExternalStorageDirectory()+"/mmpapp/","temp.jpg");
    private Uri imageUri=Uri.parse("file:///sdcard/temp.jpg");
    Uri outPutUri = Uri.fromFile(imageTemp);
    /*
    * 剪切图片
    */
    public Uri crop(Uri uri) {


        // 裁剪图片

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 2);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 1000);
        intent.putExtra("outputY", 500);
        intent.putExtra("scale", true);
        intent.putExtra("return-data", false);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, outPutUri);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.putExtra("noFaceDetection", true); // no face detection
        activity.startActivityForResult(intent, CROP_ACTIVITY_RESULT);

        return outPutUri;
    }

    public Bitmap decodeUriAsBitmap(Uri uri){
        Bitmap bitmap = null;
        try {
            bitmap = BitmapFactory.decodeStream(activity.getContentResolver().openInputStream(uri));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
        return bitmap;
    }
}
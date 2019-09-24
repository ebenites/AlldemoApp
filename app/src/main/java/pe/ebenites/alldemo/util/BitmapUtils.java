package pe.ebenites.alldemo.util;

import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.util.Log;

public class BitmapUtils {

    private static final String TAG = BitmapUtils.class.getSimpleName();

    // Redimensionar una imagen bitmap
    public static Bitmap scaleBitmapDown(Bitmap bitmap, int maxDimension) {
        try{
            int originalWidth = bitmap.getWidth();
            int originalHeight = bitmap.getHeight();
            int resizedWidth = maxDimension;
            int resizedHeight = maxDimension;

            if (originalHeight > originalWidth) {
                resizedHeight = maxDimension;
                resizedWidth = (int) (resizedHeight * (float) originalWidth / (float) originalHeight);
            } else if (originalWidth > originalHeight) {
                resizedWidth = maxDimension;
                resizedHeight = (int) (resizedWidth * (float) originalHeight / (float) originalWidth);
            } else if (originalHeight == originalWidth) {
                resizedHeight = maxDimension;
                resizedWidth = maxDimension;
            }

            return Bitmap.createScaledBitmap(bitmap, resizedWidth, resizedHeight, false);

        }catch (Throwable t){
            Log.e(TAG, t.toString(), t);
            return bitmap;
        }
    }

    // Corregir la orientación a normal de una imagen bitmap: https://android-developers.googleblog.com/2016/12/introducing-the-exifinterface-support-library.html
    public static Bitmap fixBitmapOrientation(Bitmap bitmap, String pathName) {
        try {
            ExifInterface exif = new ExifInterface(pathName);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            int rotationDegrees = 0;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotationDegrees = 90;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotationDegrees = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotationDegrees = 270;
                    break;
            }
            Log.d(TAG, "rotationDegrees: " + rotationDegrees);

            Matrix matrix = new Matrix();
            matrix.postRotate(rotationDegrees);

            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);

        }catch (Throwable t){
            Log.e(TAG, t.toString(), t);
            return bitmap;
        }
    }

}

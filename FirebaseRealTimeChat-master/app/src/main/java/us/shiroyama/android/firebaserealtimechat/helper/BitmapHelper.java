package us.shiroyama.android.firebaserealtimechat.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

import javax.inject.Inject;

/**
 * @author Fumihiko Shiroyama (fu.shiroyama@gmail.com)
 */
public class BitmapHelper {
    private static final String TAG = BitmapHelper.class.getSimpleName();

    private static final int MAX_WIDTH = 320;
    private static final int MAX_HEIGHT = 320;

    private final Context context;
    private final BitmapResizer bitmapResizer;

    @Inject
    public BitmapHelper(Context context) {
        this.context = context;
        this.bitmapResizer = new BitmapResizer(context);
    }

    /**
     * @param fromPath
     * @return resizedFilePath
     */
    @Nullable
    public String resize(@NonNull String fromPath) {
        Uri uri = Uri.parse("file://" + fromPath);
        try {
            Bitmap bitmap = bitmapResizer.resize(uri, MAX_WIDTH, MAX_HEIGHT);
            File cacheDir = context.getCacheDir();
            File tempFile = File.createTempFile(UUID.randomUUID().toString(), "jpg", cacheDir);
            FileOutputStream fileOutputStream = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, fileOutputStream);
            fileOutputStream.close();
            return tempFile.getPath();
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return null;
    }

    /**
     * http://qiita.com/masahide318/items/e9afbbbf6747bb091f63
     */
    private static class BitmapResizer {
        private Context context;

        public BitmapResizer(Context context) {
            this.context = context.getApplicationContext();
        }

        public Bitmap resize(Uri uri, int mMaxWidth, int mMaxHeight) throws IOException {
            InputStream inputStream = context.getContentResolver().openInputStream(uri);

            Bitmap bitmap;
            BitmapFactory.Options decodeOptions = new BitmapFactory.Options();
            decodeOptions.inJustDecodeBounds = true;
            BitmapFactory.decodeStream(inputStream, null, decodeOptions);
            inputStream.close();
            int actualWidth = decodeOptions.outWidth;
            int actualHeight = decodeOptions.outHeight;

            int desiredWidth = getResizedDimension(mMaxWidth, mMaxHeight,
                    actualWidth, actualHeight);
            int desiredHeight = getResizedDimension(mMaxHeight, mMaxWidth,
                    actualHeight, actualWidth);

            decodeOptions.inJustDecodeBounds = false;
            decodeOptions.inSampleSize =
                    findBestSampleSize(actualWidth, actualHeight, desiredWidth, desiredHeight);

            inputStream = context.getContentResolver().openInputStream(uri);
            Bitmap tempBitmap = BitmapFactory.decodeStream(inputStream, null, decodeOptions);
            inputStream.close();

            if (tempBitmap != null && (tempBitmap.getWidth() > desiredWidth ||
                    tempBitmap.getHeight() > desiredHeight)) {
                bitmap = Bitmap.createScaledBitmap(tempBitmap,
                        desiredWidth, desiredHeight, true);
                tempBitmap.recycle();
            } else {
                bitmap = tempBitmap;
            }
            return bitmap;
        }

        private int getResizedDimension(int maxPrimary, int maxSecondary, int actualPrimary,
                                        int actualSecondary) {
            if ((maxPrimary == 0) && (maxSecondary == 0)) {
                return actualPrimary;
            }

            if (maxPrimary == 0) {
                double ratio = (double) maxSecondary / (double) actualSecondary;
                return (int) (actualPrimary * ratio);
            }

            if (maxSecondary == 0) {
                return maxPrimary;
            }

            double ratio = (double) actualSecondary / (double) actualPrimary;
            int resized = maxPrimary;

            if ((resized * ratio) < maxSecondary) {
                resized = (int) (maxSecondary / ratio);
            }
            return resized;
        }

        static int findBestSampleSize(
                int actualWidth, int actualHeight, int desiredWidth, int desiredHeight) {
            double wr = (double) actualWidth / desiredWidth;
            double hr = (double) actualHeight / desiredHeight;
            double ratio = Math.min(wr, hr);
            float n = 1.0f;
            while ((n * 2) <= ratio) {
                n *= 2;
            }
            return (int) n;
        }
    }
}

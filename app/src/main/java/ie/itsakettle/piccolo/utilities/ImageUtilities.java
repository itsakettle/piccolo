package ie.itsakettle.piccolo.utilities;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.view.View;

/**
 * Created by wtr on 01/02/15.
 */
public class ImageUtilities {

    public static Bitmap loadBitmapFromView(View v) {
        v.layout(0,0,100,100);
        Bitmap b = Bitmap.createBitmap( v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        Drawable bgDrawable =v.getBackground();
        if (bgDrawable!=null)
            bgDrawable.draw(c);
        else
            c.drawColor(Color.WHITE);
        v.draw(c);
        return b;
    }


}

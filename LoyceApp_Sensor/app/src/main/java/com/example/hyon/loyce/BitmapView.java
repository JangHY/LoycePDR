package com.example.hyon.loyce;

/**
 * Created by Hyon on 2016-04-13.
 */
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

public class BitmapView extends View {
    Bitmap bitmap;
    int width;
    int height;
    int X;
    int sw;
    int c;
    int dx;
    int dy;

    @SuppressWarnings("deprecation")

    public BitmapView( Context context, AttributeSet attrs ) {
        super( context, attrs );

        WindowManager wm = (WindowManager)context.getSystemService( Context.WINDOW_SERVICE );
        Display display = wm.getDefaultDisplay();
        sw = display.getWidth();

        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.current_marker);
        // 얘 뒤져보면 byte[] 에서 bitmap생성하는 것도 있심
        width = bitmap.getWidth();
        height = bitmap.getHeight();
        c = bitmap.getWidth() / 2;
        X = sw / 2; // 초기 X값을 구한다. (가운데)
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(
                bitmap, // 출력할 bitmap
                new Rect(0,0,width,height),   // 출력할 bitmap의 지정된 영역을 (sub bitmap)
                new Rect(dx,dy,dx+width,dy+height),  // 이 영역에 출력한다. (화면을 벗어나면 clipping됨)
                null);
        super.onDraw(canvas);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        switch(event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_UP:
                break;
            case MotionEvent.ACTION_MOVE:
// 주루룩 drag했을 경우 히스토리가 모두 기록되어서 전달됨
                int length=event.getHistorySize();
                float sx, sy, ex, ey;

                if (length != 0) {
                    sx = event.getHistoricalX(0);
                    sy = event.getHistoricalY(0);
                    ex = event.getHistoricalX(length-1);
                    ey = event.getHistoricalY(length-1);

                    dx += (int)(ex-sx);
                    dy += (int)(ey-sy);
                }
                invalidate();
                break;
        }

        return true;
    }
}
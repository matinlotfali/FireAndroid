package com.matinlotfali.fire;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {

    final int _gher = 3;
    final int _wind = 0;
    final int _size = 400;
    final int _flame = 15;

    final int _light = 255;
    final int _black = 0xFF << 24;
    final int _color1 = _black + (_light << 16) + (_light << 8);

    Bitmap bitmap;
    int[] colorPixels;
    ImageView imageView;

    final MyRandom random = new MyRandom(85533);
    final Timer timer = new Timer();
    final ArrayList<Long> fpsList = new ArrayList<Long>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    protected void onStart() {
        super.onStart();

        imageView = (ImageView) findViewById(R.id.imageView);
        imageView.post(new Runnable() {
                           @Override
                           public void run() {
                               int width = imageView.getWidth();
                               int height = imageView.getHeight();

                               final int w, h;
                               if (width <= _size && height <= _size) {
                                   w = width;
                                   h = height;
                               } else if (width > height) {
                                   w = _size;
                                   h = _size * height / width;
                               } else {
                                   h = _size;
                                   w = _size * width / height;
                               }

                               bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
                               colorPixels = new int[w * h];

                               bitmap.eraseColor(Color.BLACK);
                               for (int i = 0; i < w; i++) {
                                   bitmap.setPixel(i, h - 1, _color1);
                               }
                               bitmap.getPixels(colorPixels, 0, w, 0, 0, w, h);
                               imageView.setImageBitmap(bitmap);

                               timer.schedule(new TimerTask() {
                                   long t;

                                   @Override
                                   public void run() {
                                       while (true) {
                                           final long current = System.currentTimeMillis();
                                           final long duration = current - t;
                                           t = current;
                                           fpsList.add(1000 / duration);
                                           if (fpsList.size() > 500)
                                               fpsList.remove(0);
                                           long sum = 0;
                                           for (long f : fpsList)
                                               sum += f;
                                           final long fps = sum / fpsList.size();

                                           for (int i = _wind; i < w * (h - 1) - _gher + 1; i++) {
                                               int color = colorPixels[i + w];

                                               if (random.nextInt(100) < _flame)
                                                   color = NextColor(color);

                                               colorPixels[i + random.nextInt(_gher) - _wind] = color;
                                           }
                                           bitmap.setPixels(colorPixels, 0, w, 0, 0, w, h);

                                           runOnUiThread(new Runnable() {
                                               @Override
                                               public void run() {
                                                   imageView.setImageBitmap(bitmap);
                                                   setTitle("Fire   -   FPS: " + fps);
                                               }
                                           });
                                       }
                                   }
                               }, 0);
                           }
                       }
        );
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x,y;
        int w = imageView.getWidth();
        int h = imageView.getHeight();
        if(w < _size && h < _size)
        {
            x = event.getX();
            y = event.getY();
        }
        else if(w > h)
        {
            x = event.getX() * _size / w;
            y = event.getY() * _size / w;
        }
        else
        {
            x = event.getX() * _size / h;
            y = event.getY() * _size / h;
        }

        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        y-= 50;
        for(float i=x-10; i<x+10; i++)
            if(i>=0 && i<width)
                for(float j=y-10; j<y+10; j++)
                    if(j>=0 && j<height &&  Math.pow(i-x,2) + Math.pow(j-y,2) <= 100)
                        colorPixels[(int)j*width+(int)i] = _color1;
        bitmap.setPixels(colorPixels, 0, width, 0, 0, width, height);

        return super.onTouchEvent(event);
    }

    int NextColor(int color) {

        if(color == _black)
            return color;

        int b = color & 0xFF;
        if(b != 0)
            return color;

        int g = (color >> 8) & 0xFF;
        int r = (color >> 16) & 0xFF;

        if (r == _light && g > 0) {
            if (g >= _flame)
                g = g - _flame;
            else
                g = 0;
            return _black + (r << 16) + (g << 8);
        }
        if (r > 0 && g == 0) {
            if (r >= _flame)
                r = r - _flame;
            else
                r = 0;
            return _black + (r << 16);
        }

//        if(r == 255 && r != g) {
//            if(g + _flame <= 255)
//                g += _flame;
//            else
//                g = 255;
//            return (0xFF << 24) + (r << 16) + (g << 8);
//        }
//        else if(r == g) {
//            if(r - _flame>= 0) {
//                r -= _flame ;
//                g = r;
//            }
//            else
//                r = g = 0;
//            return (0xFF << 24) + (r << 16) + (g << 8);
//        }

        return color;
    }
}

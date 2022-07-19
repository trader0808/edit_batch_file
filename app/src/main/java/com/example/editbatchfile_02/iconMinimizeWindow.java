package com.example.editbatchfile_02;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.IBinder;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;

import androidx.annotation.Nullable;

import java.util.concurrent.BlockingDeque;

public class iconMinimizeWindow extends Service {

    private static final String TAG = "[iconMinimizeWindowGFG]";

    private ViewGroup floatView;
    private WindowManager.LayoutParams floatWindowLayoutParam;
    private WindowManager windowManager;
    private int LAYOUT_TYPE;
    private Button iconMaximizeBtn;
    private String focus_value;
    private String iso_value;
    private String shutter_value;
    private String count_value;
    private String weight_01_value;
    private String weight_02_value;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        DisplayMetrics metrics = getApplicationContext().getResources().getDisplayMetrics();
        int width = metrics.widthPixels;
        int height = metrics.heightPixels;

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        floatView = (ViewGroup) inflater.inflate(R.layout.floating_layout_02, null);

        iconMaximizeBtn = floatView.findViewById(R.id.iconMinimizeBtn2);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        } else {
            LAYOUT_TYPE = WindowManager.LayoutParams.TYPE_TOAST;
        }

        floatWindowLayoutParam = new WindowManager.LayoutParams(
                (int) (300),
                (int) (300),
                LAYOUT_TYPE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );

        floatWindowLayoutParam.gravity = Gravity.CENTER;
        floatWindowLayoutParam.x = 0;
        floatWindowLayoutParam.y = 0;

        windowManager.addView(floatView, floatWindowLayoutParam);

        iconMaximizeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stopSelf();
                //The window is removed from the screen
                windowManager.removeView(floatView);
                Intent intent = new Intent(iconMinimizeWindow.this, EditBatchFileFloatingService.class);
                startService(intent);
            }
        });

        //Another feature of the floating window is, the window is movable.
        //The window can be moved at any position on the screen.
        floatView.setOnTouchListener(new View.OnTouchListener() {

            final WindowManager.LayoutParams floatWindowLayoutUpdateParam = floatWindowLayoutParam;
            double x;
            double y;
            double px;
            double py;

            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {
                    //When the window will be touched, the x and y position of that position will be retrieved
                    case MotionEvent.ACTION_DOWN:
                        x = floatWindowLayoutUpdateParam.x;
                        y = floatWindowLayoutUpdateParam.y;
                        //returns the original raw X coordinate of this event
                        px = event.getRawX();
                        //returns the original raw Y coordinate of this event
                        py = event.getRawY();
                        break;
                    //When the window will be dragged around, it will update the x, y of the Window Layout Parameter
                    case MotionEvent.ACTION_MOVE:
                        floatWindowLayoutUpdateParam.x = (int) ((x + event.getRawX()) - px);
                        floatWindowLayoutUpdateParam.y = (int) ((y + event.getRawY()) - py);

                        //updated parameter is applied to the WindowManager
                        windowManager.updateViewLayout(floatView, floatWindowLayoutUpdateParam);
                        break;
                }

                return false;
            }
        });
    }

    //It is called when stopService() method is called in MainActivity
    @Override
    public void onDestroy() {

        super.onDestroy();
        stopSelf();
        //Window is removed from the screen
        windowManager.removeView(floatView);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return Service.START_STICKY;
        }else {
            focus_value = intent.getStringExtra("focus_value");
            iso_value = intent.getStringExtra("iso_value");
            shutter_value = intent.getStringExtra("shutter_value");
            count_value = intent.getStringExtra("count_value");
            weight_01_value = intent.getStringExtra("weight_01_value");
            weight_02_value = intent.getStringExtra("weight_02_value");
        }
        return START_REDELIVER_INTENT;
    }
}
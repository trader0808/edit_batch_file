package com.example.editbatchfile_02;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;

public class EditBatchFileFloatingService extends Service {

    private static final String TAG = "[Floating Tuner]";

    private double x;
    private double y;
    private double px;
    private double py;

    private ViewGroup tuningGeneralPopup;
    private WindowManager.LayoutParams tuningGeneralPopupLayoutParam;
    private WindowManager windowManager;

    private TextView previewFloatingTV;
    private EditText focusFloatingET;
    private EditText isoFloatingET;
    private EditText middleFloatingET;
    private EditText countFloatingET;
    private EditText weight01FloatingET;
    private EditText weight02FloatingET;
    private Button generateBatchFloatingBtn;
    private Button minimizeViewBtn;
    private Button closeViewBtn;

    private String previewTvContent = "";
    private ArrayList<ArrayList<Long>> ratioList = new ArrayList<ArrayList<Long>>();

    private ArrayList<Integer> ratioNum = new ArrayList<Integer>();
    private ArrayList<ArrayList<Integer>> customRatio = new ArrayList<ArrayList<Integer>>();

    private ArrayList<Integer> customRatio_02 = new ArrayList<Integer>();
    private double middleNumWeight01 = 1.6;
    private double middleNumWeight02 = 2.0;

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
    }

    @Override
    public void onCreate () {
        super.onCreate();

        Log.d (TAG, "Show General tuning activity");

        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);

        implementTuningGeneralPopup();
        implementTuningGeneralPopupLayoutParam();

        windowManager.addView(tuningGeneralPopup, tuningGeneralPopupLayoutParam);

        implementFloatingContents();
        implementEvent ();
        addRatioUserWant();
    }

    private void addRatioUserWant() {  // 최소, 중간, 최대 값이 비율이 일정하면 ratioNum에 할당. 그렇지 않으면 customRatio에 할당
        customRatio_02.add(1);
        customRatio_02.add(4);
        customRatio_02.add(8);
        customRatio_02.add(16);
        customRatio_02.add(32);
        customRatio_02.add(64);
        customRatio_02.add(128);
        customRatio_02.add(256);
    }

    private void implementTuningGeneralPopup () {
        LayoutInflater inflater = (LayoutInflater) getBaseContext().getSystemService(LAYOUT_INFLATER_SERVICE);
        tuningGeneralPopup = (ViewGroup) inflater.inflate(R.layout.floating_activity, null);
    }

    private void implementTuningGeneralPopupLayoutParam () {
        tuningGeneralPopupLayoutParam = new WindowManager.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT
        );
    }

    private void implementEvent () {
        implementEventForMovingPopup();
        implementEventForCloseButton();
        implementKeyboardPopEvent();
    }

    private void implementFloatingContents () {
        previewFloatingTV = tuningGeneralPopup.findViewById(R.id.preview_floating_tv);
        focusFloatingET = tuningGeneralPopup.findViewById(R.id.focus_floating_et);
        isoFloatingET = tuningGeneralPopup.findViewById(R.id.iso_floating_et);
        middleFloatingET = tuningGeneralPopup.findViewById(R.id.middle_floating_et);
        countFloatingET = tuningGeneralPopup.findViewById(R.id.count_floating_et);
        weight01FloatingET = tuningGeneralPopup.findViewById(R.id.weight01_floating_et);
        weight02FloatingET = tuningGeneralPopup.findViewById(R.id.weight02_floating_et);
        generateBatchFloatingBtn = tuningGeneralPopup.findViewById(R.id.generate_floating_btn);
        minimizeViewBtn = tuningGeneralPopup.findViewById(R.id.min_floating_btn);
        closeViewBtn = tuningGeneralPopup.findViewById(R.id.close_floating_btn);
    }

    private void implementEventForMovingPopup () {
        tuningGeneralPopup.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        x = tuningGeneralPopupLayoutParam.x;
                        y = tuningGeneralPopupLayoutParam.y;
                        px = event.getRawX();
                        py = event.getRawY();
                        break;
                    case MotionEvent.ACTION_MOVE:
                        tuningGeneralPopupLayoutParam.x = (int) ((x + event.getRawX()) - px);
                        tuningGeneralPopupLayoutParam.y = (int) ((y + event.getRawY()) - py);
                        windowManager.updateViewLayout(tuningGeneralPopup, tuningGeneralPopupLayoutParam);
                        break;
                }
                v.performClick();
                return false;
            }
        });
    }

    private void implementEventForCloseButton () {
        closeViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDestroy();
            }
        });
    }

    private void implementKeyboardPopEvent () {
        focusFloatingET.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                focusFloatingET.setCursorVisible(true);
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = tuningGeneralPopupLayoutParam;
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                windowManager.updateViewLayout(tuningGeneralPopup, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });

        isoFloatingET.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isoFloatingET.setCursorVisible(true);
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = tuningGeneralPopupLayoutParam;
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                windowManager.updateViewLayout(tuningGeneralPopup, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });

        middleFloatingET.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                middleFloatingET.setCursorVisible(true);
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = tuningGeneralPopupLayoutParam;
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                windowManager.updateViewLayout(tuningGeneralPopup, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });

        countFloatingET.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                countFloatingET.setCursorVisible(true);
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = tuningGeneralPopupLayoutParam;
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                windowManager.updateViewLayout(tuningGeneralPopup, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });

        weight01FloatingET.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                weight01FloatingET.setCursorVisible(true);
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = tuningGeneralPopupLayoutParam;
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                windowManager.updateViewLayout(tuningGeneralPopup, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });

        weight02FloatingET.setOnTouchListener(new View.OnTouchListener() {
            @SuppressLint("ClickableViewAccessibility")
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                weight02FloatingET.setCursorVisible(true);
                WindowManager.LayoutParams floatWindowLayoutParamUpdateFlag = tuningGeneralPopupLayoutParam;
                floatWindowLayoutParamUpdateFlag.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN;
                windowManager.updateViewLayout(tuningGeneralPopup, floatWindowLayoutParamUpdateFlag);
                return false;
            }
        });;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        stopSelf();
        if (windowManager != null) {
            windowManager.removeView(tuningGeneralPopup);
        }
    }

    private boolean checkIfFullET(EditText editText) {
        return !editText.getText().toString().equals("");
    }
}
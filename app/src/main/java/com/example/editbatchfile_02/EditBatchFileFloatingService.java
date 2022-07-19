package com.example.editbatchfile_02;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Environment;
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
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
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
    private double middleNumWeight01 = 1.1;
    private double middleNumWeight02 = 2.2;

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
        generateBatchText();
    }

    private void addRatioUserWant() {  // 최소, 중간, 최대 값이 비율이 일정하면 ratioNum에 할당. 그렇지 않으면 customRatio에 할당
        customRatio_02.add(1);
        customRatio_02.add(2);
        customRatio_02.add(4);
        customRatio_02.add(8);
        customRatio_02.add(16);
        customRatio_02.add(32);
        customRatio_02.add(64);
        customRatio_02.add(128);
        customRatio_02.add(256);
        customRatio_02.add(512);
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
        implementEventForMinimizeButton();
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

    private void implementEventForMinimizeButton() {
        minimizeViewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                stopSelf();
                windowManager.removeView(tuningGeneralPopup);
                Intent intent = new Intent(EditBatchFileFloatingService.this, iconMinimizeWindow.class);
                Log.d(TAG, "minimize button clicked");
                startService(intent);
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

    private void generateBatchText() {
        generateBatchFloatingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIfFullET(isoFloatingET) && checkIfFullET(focusFloatingET) && checkIfFullET(middleFloatingET)) {
                    calculateRatioList();
                    Log.d(TAG, "ratioList : " + ratioList);
                    makeValueOnPreviewTvContent();
                    previewFloatingTV.setText(previewTvContent);
                    Log.d(TAG, "previewTVcontent : " + previewTvContent);
                    makeBatchFile();
                    clearValueOnUsedParameters();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.validation_check_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateRatioList() {
        int middleValue = Integer.parseInt(middleFloatingET.getText().toString());

        if(ratioNum.size()!=0){
            for(int i = 0; i < ratioNum.size(); i++) {
                ratioList.add(MainActivity.Scaling.scaling_list(middleValue, ratioNum.get(i)));
            }
        }

        if(customRatio.size()!=0){
            for(int i = 0; i < customRatio.size(); i++) {
                ratioList.add(MainActivity.Scaling.custom_scaling_list(middleValue, customRatio.get(i)));
            }
        }

        if(customRatio_02.size()!=0){

            int middleIndex = (int)(customRatio_02.size()/2);
            int countNum;

            if(countFloatingET.getText().toString().equals("")) {
                countNum = 1;
            }else{
                countNum = Integer.parseInt(countFloatingET.getText().toString());
            }

            if(!weight01FloatingET.getText().toString().equals("")) {
                middleNumWeight01 = Double.parseDouble(weight01FloatingET.getText().toString());

                for(int j = 0; j < countNum; j++) {
                    for(int i = 0; i <customRatio_02.size();i++) {
                        ratioList.add(MainActivity.Scaling.custom_scaling_list_02((int)(middleValue * middleNumWeight01), customRatio_02.get(i), customRatio_02.get(middleIndex)));
                    }
                }
            }

            if(!weight02FloatingET.getText().toString().equals("")) {
                middleNumWeight02 = Double.parseDouble(weight02FloatingET.getText().toString());

                for(int j = 0; j < countNum; j++) {
                    for(int i = 0; i <customRatio_02.size();i++) {
                        ratioList.add(MainActivity.Scaling.custom_scaling_list_02((int)(middleValue * middleNumWeight02), customRatio_02.get(i), customRatio_02.get(middleIndex)));
                    }
                }
            }
        }
    }

    public static class Scaling {
        public static ArrayList<Long> scaling_list(int middleValue, int ratio) {
            ArrayList<Long> scale_list = new ArrayList<Long>();
            scale_list.add((long)(middleValue * 0.1));
            scale_list.add((long)(middleValue * 0.4));
            scale_list.add((long)(middleValue * 0.7));
            scale_list.add((long)(middleValue * 1));
            scale_list.add((long)(middleValue * 1.3));
            scale_list.add((long)(middleValue * 1.6));
            scale_list.add((long)(middleValue * 1.9));

            ArrayList<Long> scale_list_buffer = new ArrayList<Long>();
            for( int i = 0; i < scale_list.size(); i++) {
                long top = scale_list.get(i) * ratio;
                long bot = (long)(scale_list.get(i) / ratio);
                long middle = scale_list.get(i);

                scale_list_buffer.add(bot);
                scale_list_buffer.add(middle);
                scale_list_buffer.add(top);
            }

            return scale_list_buffer;
        }

        public static ArrayList<Long> custom_scaling_list(int middleValue, ArrayList<Integer> custom_list) {
            ArrayList<Long> scale_list = new ArrayList<Long>();
            scale_list.add((long)(middleValue * 0.1));
            scale_list.add((long)(middleValue * 0.4));
            scale_list.add((long)(middleValue * 0.7));
            scale_list.add((long)(middleValue * 1));
            scale_list.add((long)(middleValue * 1.3));
            scale_list.add((long)(middleValue * 1.6));
            scale_list.add((long)(middleValue * 1.9));

            ArrayList<Long> scale_list_buffer = new ArrayList<Long>();
            for( int i = 0; i < scale_list.size(); i++) {
                long top = scale_list.get(i) * (custom_list.get(2) / custom_list.get(1));
                long bot = (long)(scale_list.get(i) / (custom_list.get(1) / custom_list.get(0)));
                long middle = scale_list.get(i);

                scale_list_buffer.add(bot);
                scale_list_buffer.add(middle);
                scale_list_buffer.add(top);
            }

            return scale_list_buffer;
        }

        public static ArrayList<Long> custom_scaling_list_02(int middleValue, int custom_list_02_ratio, int custom_list_02_middle_ratio) {

            ArrayList<Long> scale_list_buffer = new ArrayList<Long>();
            scale_list_buffer.add((long)((long) middleValue * (custom_list_02_ratio) / custom_list_02_middle_ratio));

            return scale_list_buffer;
        }
    }

    private void makeValueOnPreviewTvContent() {
        String strFocus = focusFloatingET.getText().toString();
        String strISO = isoFloatingET.getText().toString();
        int count = 0;
        int one_element_len = ratioList.get(0).size();

        for (int i = 0; i < ratioList.size(); i++) {
            for (int j = 0; j < one_element_len; j++) {
                count += 1;
                previewTvContent += intToStr(count) + "\t" + strFocus + "\t" + strISO + "\t" + ratioList.get(i).get(j) + "\n";
            }
        }

        previewTvContent = previewTvContent.substring(0, previewTvContent.length()-1);
    }

    private String intToStr(int i) {
        return String.valueOf(i);
    }

    @SuppressLint("SetTextI18n")
    private void makeBatchFile() {
        File batchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "batch_created.txt");

        try {
            BufferedWriter buf = new BufferedWriter(new FileWriter(batchFile));
            buf.write("No\tFocus\tISO\tShutter\n" + previewTvContent );
            buf.newLine();
            buf.close();
            Toast.makeText(getApplicationContext(), R.string.batch_file_generated_toast, Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            Log.e(TAG, "onClick: Cant write the batch");
            Toast.makeText(getApplicationContext(), R.string.batch_file_generation_fail_toast, Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private void clearValueOnUsedParameters() {
        previewTvContent = previewTvContent.substring(0,0);
        ratioList.clear();
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
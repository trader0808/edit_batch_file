package com.example.editbatchfile_02;

import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = batchEditDef.batchEditTag;

    private EditText middleEt;
    private EditText focusEt;
    private EditText isoEt;
    private TextView previewTv;
    private TextView uriTV;
    private Button previewBtn;
    private Button generateBtn;

    private String previewTvContent = "";
    private ArrayList<ArrayList<Integer>> ratioList = new ArrayList<ArrayList<Integer>>();

    private ArrayList<Integer> ratioNum = new ArrayList<Integer>();
    private ArrayList<ArrayList<Integer>> customRatio = new ArrayList<ArrayList<Integer>>();

    private ArrayList<Integer> customRatio_02 = new ArrayList<Integer>();
    private double middleNumWeight01 = 1.3;
    private double middleNumWeight02 = 1.6;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        requestMediaPermission();
        implementContents();
        addRatioUserWant();
        showPreviewText();
        generateBatchText();
    }

    private void addRatioUserWant() {  // 최소, 중간, 최대 값이 비율이 일정하면 ratioNum에 할당. 그렇지 않으면 customRatio에 할당
        customRatio_02.add(1);
        customRatio_02.add(8);
        customRatio_02.add(16);
        customRatio_02.add(32);
        customRatio_02.add(64);
        customRatio_02.add(128);
        customRatio_02.add(256);
    }

    private void requestMediaPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if(checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                    || checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if(shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, R.string.request_permission_toast, Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]
                        {Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
            }
        }
    }

    private void implementContents() {
        middleEt = findViewById(R.id.middle_et);
        focusEt = findViewById(R.id.focus_et);
        isoEt = findViewById(R.id.iso_et);
        previewTv = findViewById(R.id.preview_tv);
        uriTV = findViewById(R.id.uri_tv);
        previewBtn = findViewById(R.id.preview_btn);
        generateBtn = findViewById(R.id.generate_btn);
    }

    private void showPreviewText() {

        previewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIfFullET(isoEt) && checkIfFullET(focusEt) && checkIfFullET(middleEt)) {
                    calculateRatioList();
                    Log.d(TAG, "ratioList : " + ratioList);
                    makeValueOnPreviewTvContent();
                    previewTv.setText(previewTvContent);
                    Log.d(TAG, "previewTVcontent : " + previewTvContent);
                    clearValueOnUsedParameters();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.validation_check_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateRatioList() {
        int middleValue = Integer.parseInt(middleEt.getText().toString());

        if(ratioNum.size()!=0){
            for(int i = 0; i < ratioNum.size(); i++) {
                ratioList.add(Scaling.scaling_list(middleValue, ratioNum.get(i)));
            }
        }

        if(customRatio.size()!=0){
            for(int i = 0; i < customRatio.size(); i++) {
                ratioList.add(Scaling.custom_scaling_list(middleValue, customRatio.get(i)));
            }
        }

        if(customRatio_02.size()!=0){
            for(int j = 0; j < 150; j++) {
                for(int i = 0; i <customRatio_02.size();i++) {
                    ratioList.add(Scaling.custom_scaling_list_02((int)(middleValue * 1.3), customRatio_02.get(i), customRatio_02.get(3)));
                }
            }

            for(int j = 0; j < 150; j++) {
                for(int i = 0; i <customRatio_02.size();i++) {
                    ratioList.add(Scaling.custom_scaling_list_02((int)(middleValue * 1.6), customRatio_02.get(i), customRatio_02.get(3)));
                }
            }
        }
    }

    public static class Scaling {
        public static ArrayList<Integer> scaling_list(int middleValue, int ratio) {
            ArrayList<Integer> scale_list = new ArrayList<Integer>();
            scale_list.add((int)(middleValue * 0.1));
            scale_list.add((int)(middleValue * 0.4));
            scale_list.add((int)(middleValue * 0.7));
            scale_list.add((int)(middleValue * 1));
            scale_list.add((int)(middleValue * 1.3));
            scale_list.add((int)(middleValue * 1.6));
            scale_list.add((int)(middleValue * 1.9));

            ArrayList<Integer> scale_list_buffer = new ArrayList<Integer>();
            for( int i = 0; i < scale_list.size(); i++) {
                int top = scale_list.get(i) * ratio;
                int bot = (int)(scale_list.get(i) / ratio);
                int middle = scale_list.get(i);

                scale_list_buffer.add(bot);
                scale_list_buffer.add(middle);
                scale_list_buffer.add(top);
            }

            return scale_list_buffer;
        }

        public static ArrayList<Integer> custom_scaling_list(int middleValue, ArrayList<Integer> custom_list) {
            ArrayList<Integer> scale_list = new ArrayList<Integer>();
            scale_list.add((int)(middleValue * 0.1));
            scale_list.add((int)(middleValue * 0.4));
            scale_list.add((int)(middleValue * 0.7));
            scale_list.add((int)(middleValue * 1));
            scale_list.add((int)(middleValue * 1.3));
            scale_list.add((int)(middleValue * 1.6));
            scale_list.add((int)(middleValue * 1.9));

            ArrayList<Integer> scale_list_buffer = new ArrayList<Integer>();
            for( int i = 0; i < scale_list.size(); i++) {
                int top = scale_list.get(i) * (custom_list.get(2) / custom_list.get(1));
                int bot = (int)(scale_list.get(i) / (custom_list.get(1) / custom_list.get(0)));
                int middle = scale_list.get(i);

                scale_list_buffer.add(bot);
                scale_list_buffer.add(middle);
                scale_list_buffer.add(top);
            }

            return scale_list_buffer;
        }

        public static ArrayList<Integer> custom_scaling_list_02(int middleValue, int custom_list_02_ratio, int custom_list_02_middle_ratio) {

            ArrayList<Integer> scale_list_buffer = new ArrayList<Integer>();
            scale_list_buffer.add((int)(middleValue * (custom_list_02_ratio) / custom_list_02_middle_ratio));

            return scale_list_buffer;
        }
    }

    private void makeValueOnPreviewTvContent() {
        String strFocus = focusEt.getText().toString();
        String strISO = isoEt.getText().toString();
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

    private void clearValueOnUsedParameters() {
        previewTvContent = previewTvContent.substring(0,0);
        ratioList.clear();
    }

    private String intToStr(int i) {
        return String.valueOf(i);
    }

    private void generateBatchText() {
        generateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(checkIfFullET(isoEt) && checkIfFullET(focusEt) && checkIfFullET(middleEt)) {
                    calculateRatioList();
                    Log.d(TAG, "ratioList : " + ratioList);
                    makeValueOnPreviewTvContent();
                    previewTv.setText(previewTvContent);
                    Log.d(TAG, "previewTVcontent : " + previewTvContent);
                    makeBatchFile();
                    clearValueOnUsedParameters();
                } else {
                    Toast.makeText(getApplicationContext(), R.string.validation_check_toast, Toast.LENGTH_SHORT).show();
                }
            }
        });
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
            uriTV.setText(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "batch_created.txt");
        } catch (Exception e) {
            Log.e(TAG, "onClick: Cant write the batch");
            Toast.makeText(getApplicationContext(), R.string.batch_file_generation_fail_toast, Toast.LENGTH_LONG).show();
            e.printStackTrace();
            finish();
        }
    }

    private boolean checkIfFullET(EditText editText) {
        return !editText.getText().toString().equals("");
    }

}
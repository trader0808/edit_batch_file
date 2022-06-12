package com.example.editbatchfile_02;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        implementContents();
        showPreviewText();
        generateBatchText();
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
                } else {
                    Toast.makeText(getApplicationContext(), "check if focus, iso, shutter is filled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void calculateRatioList() {
        int middleValue = Integer.parseInt(middleEt.getText().toString());

        ratioList.add(Scaling.scaling_list(middleValue, 2));
        ratioList.add(Scaling.scaling_list(middleValue, 4));
        ratioList.add(Scaling.scaling_list(middleValue, 8));
        ratioList.add(Scaling.scaling_list(middleValue, 16));
        ratioList.add(Scaling.scaling_list(middleValue, 32));
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
                } else {
                    Toast.makeText(getApplicationContext(), "check if focus, iso, shutter is filled", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void makeBatchFile() {
        File batchFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "batch_created.txt");

        try {
            Toast.makeText(getApplicationContext(), "batchfile generated", Toast.LENGTH_SHORT).show();
            BufferedWriter buf = new BufferedWriter(new FileWriter(batchFile));
            buf.write("No\tFocus\tISO\tShutter\n" + previewTvContent );
            buf.newLine();
            buf.close();
            uriTV.setText(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath() + "/" + "batch_created.txt");
        } catch (Exception e) {
            Log.e(TAG, "onClick: Cant write the batch");
            e.printStackTrace();
            finish();
        }
    }

    private boolean checkIfFullET(EditText editText) {
        return !editText.getText().toString().equals("");
    }

}
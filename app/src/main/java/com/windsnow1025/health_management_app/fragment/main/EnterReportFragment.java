package com.windsnow1025.health_management_app.fragment.main;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.windsnow1025.health_management_app.R;
import com.windsnow1025.health_management_app.model.Report;
import com.windsnow1025.health_management_app.database.UserLocalDao;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;

import com.googlecode.tesseract.android.TessBaseAPI;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

// 体检报告录入
public class EnterReportFragment extends Fragment {

    String organ;

    String phoneNumber;

    String date;
    String hospital;
    String type;
    String detail;

    EditText editTextDate;
    EditText editTextHospital;
    EditText editTextType;
    EditText editTextOCRTxt;

    Bitmap bitmap;
    UserLocalDao userLocalDao;
    String base64Image = null;


    public EnterReportFragment(String organ) {
        this.organ = organ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.enter_report, container, false);

        // Require storage permission
        if (ContextCompat.checkSelfPermission(getActivity(), READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{READ_EXTERNAL_STORAGE, WRITE_EXTERNAL_STORAGE}, 1);
        }

        // Get username
        try {
            userLocalDao = new UserLocalDao(this.getActivity().getApplicationContext());
            userLocalDao.open();
            phoneNumber = userLocalDao.getPhoneNumber();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set an OnClickListener on the button to launch the gallery
        Button buttonUpload = view.findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        // Get views
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextHospital = view.findViewById(R.id.editTextHospital);
        editTextType = view.findViewById(R.id.editTextType);
        editTextOCRTxt = view.findViewById(R.id.editTextOCRTxt);

        // Set to open date picker when click on date EditText
        editTextDate.setOnClickListener(v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker view1, int year, int month, int dayOfMonth) {
                    // Set date
                    String date = year + "-" + (month + 1) + "-" + dayOfMonth;
                    // Set date to EditText
                    editTextDate.setText(date);
                }
            }, year, month, day);

            // Show DatePickerDialog
            datePickerDialog.show();
        });

        // Confirm button
        Button buttonConfirm = view.findViewById(R.id.buttonConfirm);
        buttonConfirm.setOnClickListener(v -> {
            // Get data
            date = editTextDate.getText().toString();
            hospital = editTextHospital.getText().toString();
            type = editTextType.getText().toString();
            detail = editTextOCRTxt.getText().toString();

            if (bitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] bitmapBytes = byteArrayOutputStream.toByteArray();
                base64Image = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }
            // Insert report into database
            Report report = new Report();
            report.setPhone_number(phoneNumber);
            report.setReport_type(type);
            report.setHospital(hospital);
            report.setPicture(base64Image);
            report.setDetail(detail);
            if (date.equals("")) {
                //判定日期是否填写 未填写则设置为null
                report.setReport_date(null);
            } else {
                report.setReport_date(date);
            }
            userLocalDao.insertReport(phoneNumber, report);

            // Jump to organ page
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new MainOrganFragment(organ));
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        if (uri != null) {
            // Get bitmap from uri
            bitmap = null;
            try {
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
            } catch (IOException e) {
                e.printStackTrace();
            }


        }
//
//        // Set datapath
//        String datapath = this.getActivity().getExternalFilesDir(null) + "/tesseract/";
//        String datapath2 = this.getActivity().getExternalFilesDir(null) + "/tesseract/tessdata/";
//
//        // Create directory
//        File dir = new File(datapath);
//        if (!dir.exists()) {
//            if (!dir.mkdirs()) {
//                Log.e("test", "创建路径1失败");
//                return;
//            }
//        }
//
//        // Create directory
//        File dir2 = new File(datapath2);
//        if (!dir2.exists()) {
//            if (!dir2.mkdirs()) {
//                Log.e("test", "创建路径2失败");
//                return;
//            }
//        }
//
//        // Path exists
//        Log.i("test", "路径2存在");
//
//        // New thread
//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                // Download trained data
//                String filename = "chi_sim.traineddata";
//                File file = new File(dir2, filename);
//                if (!file.exists()) {
//                    try {
//                        URL url = new URL("https://github.com/tesseract-ocr/tessdata/raw/main/chi_sim.traineddata");
//                        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//                        connection.connect();
//                        InputStream input = connection.getInputStream();
//                        OutputStream output = new FileOutputStream(file);
//                        byte[] buffer = new byte[1024];
//                        int length;
//                        while ((length = input.read(buffer)) > 0) {
//                            output.write(buffer, 0, length);
//                        }
//                        output.flush();
//                        output.close();
//                        input.close();
//                    } catch (IOException e) {
//                        Log.e("test", "下载失败");
//                        return;
//                    }
//                }
//
//                try {
//                    // Extract text
//                    String ocrtxt = extractText(bitmap);
//
//                    // Set OCR result to EditText
//                    editTextOCRTxt.setText(ocrtxt);
//                } catch (Exception e) {
//                    Log.e("test", "OCR出错");
//                    e.printStackTrace();
//                }
//            }
//        }).start();
//

    });
//
//    private String extractText(Bitmap bitmap) {
//        TessBaseAPI tessBaseApi = new TessBaseAPI();
//        tessBaseApi.init(this.getActivity().getExternalFilesDir(null) + "/tesseract/", "chi_sim");
//        tessBaseApi.setImage(bitmap);
//        String extractedText = tessBaseApi.getUTF8Text();
//        tessBaseApi.end();
//        return extractedText;
//    }
}

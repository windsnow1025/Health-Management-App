package com.windsnow1025.health_management_app.fragment.main;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import android.app.DatePickerDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.googlecode.tesseract.android.TessBaseAPI;
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
import java.util.ArrayList;
import java.util.Calendar;

// 体检报告修改
public class EditReportFragment extends Fragment {

    Integer reportId;

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
    ImageView reportImageView;

    Bitmap bitmap;
    UserLocalDao userLocalDao;

    public EditReportFragment(int reportId, String organ) {
        this.organ = organ;
        this.reportId = reportId;
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

        // Get views
        editTextDate = view.findViewById(R.id.editTextDate);
        editTextHospital = view.findViewById(R.id.editTextHospital);
        editTextType = view.findViewById(R.id.editTextType);
        editTextOCRTxt = view.findViewById(R.id.editTextOCRTxt);
        reportImageView = view.findViewById(R.id.report_imageView);

        try {
            // Get username
            userLocalDao = new UserLocalDao(getContext());
            userLocalDao.open();
            phoneNumber = userLocalDao.getPhoneNumber();

            // Get record
            ArrayList<Report> reportList = userLocalDao.getReportList(phoneNumber);
            Report report = reportList.get(reportId - 1);

            // Get data
            date = report.getReport_date();
            hospital = report.getHospital();
            type = report.getReport_type();
            detail = report.getDetail();

            String base64Image = report.getPicture();
            if (base64Image != null) {
                byte[] decodedString = Base64.decode(base64Image, Base64.NO_WRAP);
                bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
            }

            // Set data to views
            editTextDate.setText(date);
            editTextHospital.setText(hospital);
            editTextType.setText(type);
            editTextOCRTxt.setText(detail);
            reportImageView.setImageBitmap(bitmap);

        } catch (Exception e) {
            e.printStackTrace();
        }

        // Set an OnClickListener on the button to launch the gallery
        Button buttonUpload = view.findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(v -> galleryLauncher.launch("image/*"));

        // Set to open date picker when click on date EditText
        editTextDate.setOnClickListener(v -> {
            // Get current date
            Calendar calendar = Calendar.getInstance();
            int year = calendar.get(Calendar.YEAR);
            int month = calendar.get(Calendar.MONTH);
            int day = calendar.get(Calendar.DAY_OF_MONTH);

            // Create DatePickerDialog
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), (view1, year1, month1, dayOfMonth) -> {
                // Set date
                String date = year1 + "-" + (month1 + 1) + "-" + dayOfMonth;
                // Set date to EditText
                editTextDate.setText(date);
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

            // bitmap to base64
            String base64Image = null;
            if (bitmap != null) {
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] bitmapBytes = byteArrayOutputStream.toByteArray();
                base64Image = Base64.encodeToString(bitmapBytes, Base64.NO_WRAP);
            }

            // Upload to database
            Report report = new Report();
            report.setPhone_number(phoneNumber);
            report.setReport_type(type);
            report.setHospital(hospital);
            report.setPicture(base64Image);
            report.setDetail(detail);
            report.setId(reportId);
            if (date.equals("")) {
                //判定日期是否填写 未填写则设置为null
                report.setReport_date(null);
            } else {
                report.setReport_date(date);
            }
            userLocalDao.updateReport(phoneNumber, report);

            // Jump to organ page
            FragmentTransaction transaction = getParentFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_container, new MainOrganFragment(organ));
            transaction.addToBackStack(null);
            transaction.commit();
        });
        return view;
    }

    private final ActivityResultLauncher<String> galleryLauncher = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
        // Get bitmap from uri
        bitmap = null;
        try {
            bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
        } catch (IOException e) {
            e.printStackTrace();
        }

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
//                        e.printStackTrace();
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


    });

//    private String extractText(Bitmap bitmap) {
//        TessBaseAPI tessBaseApi = new TessBaseAPI();
//        Log.i("test", "测试");
//        tessBaseApi.init(this.getActivity().getExternalFilesDir(null) + "/tesseract/", "chi_sim");
//        Log.i("test", "测试");
//        tessBaseApi.setImage(bitmap);
//        String extractedText = tessBaseApi.getUTF8Text();
//        tessBaseApi.end();
//        return extractedText;
//    }
}

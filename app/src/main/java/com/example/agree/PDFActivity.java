package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class PDFActivity extends AppCompatActivity {

    private String path;
    private ImageView imgView;
    private int currentPage = 0;
    private ImageButton btn_zoomin, btn_zoomout;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page curPage;
    private ParcelFileDescriptor descriptor;
    private float currentZoomLevel = 5;
    private String fileDir;
    String CURRENT_PAGE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        fileDir =  getFilesDir().toString();
        path = fileDir + getIntent().getStringExtra("fileName");
        imgView = findViewById(R.id.imgView);
        btn_zoomin = findViewById(R.id.zoomin);
        btn_zoomout = findViewById(R.id.zoomout);
        //       btn_zoomin.setOnClickListener((View.OnClickListener) this);
//        btn_zoomout.setOnClickListener(this);



    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            openPdfRenderer();
            displayPage(currentPage);
        } catch (Exception e) {
            //Toast.makeText(this, "PDF-файл защищен паролем.", Toast.LENGTH_SHORT).show();
            MainActivity.infoString = e.toString();
            //e.printStackTrace();
        }
    }

    private void openPdfRenderer(){
        File file = new File(path);
        descriptor = null;
        pdfRenderer = null;
        try {
            descriptor = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            pdfRenderer = new PdfRenderer(descriptor);
        } catch (Exception e) {
            //Toast.makeText(this, "Ошибка", Toast.LENGTH_LONG).show();
            //e.printStackTrace();
            MainActivity.infoString = e.toString();
        }
    }

    private void displayPage(int index){
        if (pdfRenderer.getPageCount() <= index) return;
        if (curPage != null) curPage.close();
        curPage = pdfRenderer.openPage(index);
        int newWidth = (int) (getResources().getDisplayMetrics().widthPixels * curPage.getWidth() / 72
                * currentZoomLevel / 40);
        int newHeight = (int) (getResources().getDisplayMetrics().heightPixels * curPage.getHeight() / 72
                        * currentZoomLevel / 64);
        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Matrix matrix = new Matrix();
        float dpiAdjustedZoomLevel = currentZoomLevel * DisplayMetrics.DENSITY_MEDIUM
                / getResources().getDisplayMetrics().densityDpi;
        matrix.setScale(dpiAdjustedZoomLevel, dpiAdjustedZoomLevel);
        curPage.render(bitmap, null, matrix, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
        imgView.setImageBitmap(bitmap);
        btn_zoomout.setEnabled(currentZoomLevel != 2);
        btn_zoomin.setEnabled(currentZoomLevel != 12);
    }

    public void zoomIn(View v){
        ++currentZoomLevel;
        displayPage(curPage.getIndex());
    }

    public void zoomOut(View v){
        --currentZoomLevel;
        displayPage(curPage.getIndex());
    }

    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (curPage != null) {
            outState.putInt(CURRENT_PAGE, curPage.getIndex());
        }
    }

    @Override public void onStop() {
        try {
            closePdfRenderer();
        } catch (IOException e) {
            MainActivity.infoString = e.toString();
            //e.printStackTrace();
        }
        super.onStop();
    }

    private void closePdfRenderer() throws IOException {
        if (curPage != null) curPage.close();
        if (pdfRenderer != null) pdfRenderer.close();
        if (descriptor != null) descriptor.close();
    }
}

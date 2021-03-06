package com.example.agree;

import androidx.appcompat.app.AppCompatActivity;


import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import java.io.File;
import java.io.IOException;
import java.util.Date;

public class PDFActivity extends AppCompatActivity {

    private String path;
    private ImageView imgView;
    private int currentPage = 0;
    private ImageButton btn_zoomin, btn_zoomout, btn_Previous, btn_Next;
    //private Button btnPrevious, btnNext;
    private PdfRenderer pdfRenderer;
    private PdfRenderer.Page curPage;
    private ParcelFileDescriptor descriptor;
    private float currentZoomLevel = 5;
    private String fileDir;
    String CURRENT_PAGE;
    private String logFileName;

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
        btn_Previous = findViewById(R.id.previous);
        btn_Next = findViewById(R.id.next);
        btn_zoomin = findViewById(R.id.zoomin);
        btn_zoomout = findViewById(R.id.zoomout);
        logFileName = this.getFilesDir() + "/log.dat";
        //       btn_zoomin.setOnClickListener((View.OnClickListener) this);
//        btn_zoomout.setOnClickListener(this);
//        btnPrevious.setOnClickListener(new View.OnClickListener(
//
//        ) {
//            @Override
//            public void onClick(View v) {
//                int index = curPage.getIndex() - 1;
//                displayPage(index);
//            }
//        });
//        btnNext.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int index = curPage.getIndex() + 1;
//                displayPage(index);
//            }
//        });


    }

    @Override
    public void onStart() {
        super.onStart();
        try {
            openPdfRenderer();
            displayPage(currentPage);
        } catch (Exception e) {
            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
            //Toast.makeText(this, "PDF-файл защищен паролем.", Toast.LENGTH_SHORT).show();
            //MainActivity.infoString = e.toString();
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
            //MainActivity.infoString = e.toString();
            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
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
        int pageCount = pdfRenderer.getPageCount();
        btn_Previous.setEnabled(0 != index);
        btn_Next.setEnabled(index + 1 < pageCount);
        btn_zoomout.setEnabled(currentZoomLevel != 2);
        btn_zoomin.setEnabled(currentZoomLevel != 12);
    }


    public void rewind(View v){
        int index = curPage.getIndex() - 1;
        displayPage(index);
    }

    public void forward(View v){
        int index = curPage.getIndex() + 1;
        displayPage(index);
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
            //MainActivity.infoString = e.toString();
            //e.printStackTrace();
            ServiceTasks.addLogFile(logFileName, new Date()+":"+e.toString()+"\n");
        }
        super.onStop();
    }

    private void closePdfRenderer() throws IOException {
        if (curPage != null) curPage.close();
        if (pdfRenderer != null) pdfRenderer.close();
        if (descriptor != null) descriptor.close();
    }
}

package com.example.elijah.skyranch_draft.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.print.PrintHelper;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.utils.AidlUtil;
import com.example.elijah.skyranch_draft.utils.BluetoothUtil;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class PrintActivity extends BaseActivity {
    Bitmap bitmap, bitmap1;
    int myorientation;
    ImageView mImageView;
    String barcodeTXT;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_testprint);

//        setting the print position alignment
        byte[] send;
        send = com.sunmi.printerhelper.utils.ESCUtil.alignCenter();


        if (baseApp.isAidl()) {
            AidlUtil.getInstance().sendRawData(send);
        } else {
            BluetoothUtil.sendData(send);
        }

        // orientation
        myorientation = 1; // portrait
        mImageView = (ImageView) findViewById(R.id.bitmap_imageview);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inTargetDensity = 200;
        options.inDensity = 160;
        if (bitmap == null) {
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.br, options);
        }

        if (baseApp.isAidl()) {
            mImageView.setImageDrawable(new BitmapDrawable(bitmap));
        }



        Button print = findViewById(R.id.print);
        Button setImage = findViewById(R.id.setImage);
        Button resetImage = findViewById(R.id.printbytext);
        final EditText barcode_data = findViewById(R.id.br_val);

        setImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                barcodeTXT = barcode_data.getText().toString().trim();
                if (barcodeTXT == "" ){
                    Toast.makeText(PrintActivity.this, "Add some barcode", Toast.LENGTH_SHORT).show();
                    return;
                }
                generateBarcode(barcodeTXT);
            }
        });

        resetImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               doPhotoPrint();
            }
        });

        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                printBitmap();
                if(baseApp.isAidl()){
                    boolean isBold = true;
                    boolean isUnderLine = false;
                    AidlUtil.getInstance().printText("Enchanted Kingdom", 32, isBold, isUnderLine);
                    AidlUtil.getInstance().printBitmapCust(bitmap, myorientation, barcodeTXT+"\n", "", "");
                }


            }
        });
    }

    private Bitmap scaleImage(Bitmap bitmap1) {
        int width = bitmap1.getWidth();
        int height = bitmap1.getHeight();
        // 设置想要的大小
        int newWidth = (width / 8 + 1) * 8;
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, 1);
        // 得到新的图片
        return Bitmap.createBitmap(bitmap1, 0, 0, width, height, matrix, true);
    }

    public void printBitmap() {
        if (baseApp.isAidl()) {
            AidlUtil.getInstance().printBitmap(bitmap, myorientation);
        }
    }

    public void generateBarcode(String barcode_data){
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(barcode_data, BarcodeFormat.CODE_39,384,100);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            //mImageView.setImageBitmap(bitmap);
            mImageView.setImageBitmap(scaleImage(bitmap));
//            this.bitmap =  scaleImage(bitmap);
            this.bitmap = bitmap;
            barcodeTXT = barcode_data;

        } catch (WriterException e) {
            e.printStackTrace();
        }

    }

    private void doPhotoPrint() {
        PrintHelper photoPrinter = new PrintHelper(PrintActivity.this);
        photoPrinter.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(),
                R.drawable.brbr);
        photoPrinter.printBitmap("droids.jpg - test print", bitmap);
    }

}
package com.phong.hocsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    String DATABASE_NAME = "dbContact.db";
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processCopy();
    }

    private void processCopy(){//Sao chép csdl từ folder assets vào hệ thống điện thoại
        try {
            File dbFile = getDatabasePath(DATABASE_NAME);
            if (!dbFile.exists()) {
                copyDatabaseFromAsset();
                Toast.makeText(MainActivity.this,
                        "Sao chép CSDL SQLite vào hệ thống điện thoại thành công",
                        Toast.LENGTH_LONG).show();
            }
        }
        catch (Exception ex){
            Toast.makeText(MainActivity.this,
                    ex.toString(),Toast.LENGTH_LONG).show();
            Log.e("Lỗi",ex.toString());
        }
    }

    private String getDatabasePath(){//trả về đường dẫn trỏ đến CSDL
        return getApplicationInfo().dataDir//trả về thư mục gốc mà phần mềm này đc cài trong hệ thống điện thoại:data/data/com.phong.hocsqlite
        + DB_PATH_SUFFIX + DATABASE_NAME;
    }
    private void copyDatabaseFromAsset() {
        try{
            InputStream myInput = getAssets().open(DATABASE_NAME);
            String outFileName = getDatabasePath();
            File file = new File(getApplicationInfo().dataDir + DB_PATH_SUFFIX);
            if (!file.exists()){
                file.mkdirs();//tạo thư mục tên "databases"
            }
            OutputStream myOutput = new FileOutputStream(outFileName);
            byte []buffer = new byte[1024];
            int length;
            while ((length = myInput.read(buffer)) > 0){
                myOutput.write(buffer,0,length);
            }
            myOutput.flush();
            myOutput.close();
            myInput.close();
        }
        catch (Exception ex){
            Log.e("Lỗi",ex.toString());
        }
    }
}

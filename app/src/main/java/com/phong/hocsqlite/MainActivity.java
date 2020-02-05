package com.phong.hocsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.phong.model.Contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    String DATABASE_NAME = "dbContact.db";
    String DB_PATH_SUFFIX = "/databases/";
    SQLiteDatabase database = null;
    ListView lvContact;
    ArrayAdapter<Contact> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processCopy();
        addControls();
        hienThiToanBoSanPham();
    }

    private void hienThiToanBoSanPham() {
        //mở hoặc tạo csdl
        database = openOrCreateDatabase(DATABASE_NAME,MODE_PRIVATE,null);
        //Cursor cursor = database.rawQuery("SELECT * FROM Contact",null);
        //Cursor cursor = database.query("Contact", null, "Ma >= ?", new String[]{"3"}, null, null, null);
        //Cursor cursor = database.query("Contact", null, "Ma = ? or Ma = ?", new String[]{"1","4"}, null, null, null);
        Cursor cursor = database.query("Contact", null, null, null, null, null, null);
        adapter.clear();
        while (cursor.moveToNext()){
            int ma = cursor.getInt(0);
            String ten = cursor.getString(1);
            String phone = cursor.getString(2);
            Contact contact = new Contact(ma,ten,phone);
            adapter.add(contact);
        }
        cursor.close();
    }

    private void addControls() {
        lvContact = findViewById(R.id.lvContact);
        adapter = new ArrayAdapter<Contact>(
                MainActivity.this,
                android.R.layout.simple_list_item_1);
        lvContact.setAdapter(adapter);
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

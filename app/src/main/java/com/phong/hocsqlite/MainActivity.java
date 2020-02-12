package com.phong.hocsqlite;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.phong.model.Contact;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {
    public static String DATABASE_NAME = "dbContact.db";
    String DB_PATH_SUFFIX = "/databases/";
    public static SQLiteDatabase database = null;
    ListView lvContact;
    ArrayAdapter<Contact> adapter;

    public static Contact selectedContact;//dùng dể lấy đối tượng chọn trên listview

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        processCopy();
        addControls();
        addEvents();
        //hienThiToanBoSanPham();
    }

    private void addEvents() {
        lvContact.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedContact  = adapter.getItem(i);
            }
        });
        lvContact.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                selectedContact = adapter.getItem(i);
                return false;
            }
        });
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
        //Đăng ký ContextMenu cho listview:
        registerForContextMenu(lvContact);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //nạp menu lên:
        getMenuInflater().inflate(R.menu.menu_main,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuThem){
            Intent intent = new Intent(MainActivity.this,ThemContactActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        hienThiToanBoSanPham();
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.context_menu,menu);
    }

    @Override
    public boolean onContextItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.mnuEdit){
            if (selectedContact != null){
                {
                    Intent intent = new Intent(MainActivity.this,SuaContactActivity.class);
                    startActivity(intent);
                }
            }
        }
        else if (item.getItemId() == R.id.mnuXoa){
            if (selectedContact != null){
                xuLyXoa();
            }
        }
        return super.onContextItemSelected(item);
    }

    private void xuLyXoa() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Xác nhận xoá");
        builder.setMessage("Bạn có chắc chắn muốn xoá?: \n" + selectedContact);
        builder.setIcon(android.R.drawable.ic_dialog_alert);
        builder.setPositiveButton("Có", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int kq = database.delete("Contact","Ma = ?",new String[]{selectedContact.getMa() + ""});
                if (kq > 0){
                    hienThiToanBoSanPham();
                }
                else
                {
                    Toast.makeText(MainActivity.this,"Xoá thất bại",Toast.LENGTH_LONG).show();
                }
                dialogInterface.dismiss();
            }
        });
        builder.setNegativeButton("Không", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
            }
        });
        builder.create().show();
    }
}

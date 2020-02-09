package com.phong.hocsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class SuaContactActivity extends AppCompatActivity {

    EditText edtMa, edtTen, edtPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sua_contact);
        addControls();
    }

    private void addControls() {
        edtMa = findViewById(R.id.edtMa);
        edtTen = findViewById(R.id.edtTen);
        edtPhone = findViewById(R.id.edtPhone);
        //Hiển thị:
        edtMa.setText(MainActivity.selectedContact.getMa() + "");
        edtTen.setText(MainActivity.selectedContact.getTen());
        edtPhone.setText(MainActivity.selectedContact.getPhone());
        //ẩn đi để không cho tương tác:
        edtMa.setEnabled(false);
    }

    public void xuLyLuu(View view) {
        ContentValues values = new ContentValues();
        //update không put khoá chính(Primary Key):
        values.put("Ten",edtTen.getText().toString());
        values.put("Phone",edtPhone.getText().toString());
        /*
            Đối số 1: tên bảng
            Đối số 2: giá trị lưu
            Đối số 3: lọc theo khoá chính
            Đối số 4: gán giá trị cho đối số 3
         */
        int kq = MainActivity.database.update("Contact",values,"Ma = ?",new String[]{edtMa.getText().toString()});
        if (kq > 0){
            finish();
        }
        else
        {
            Toast.makeText(SuaContactActivity.this,"Sửa thất bại",Toast.LENGTH_LONG).show();
        }
    }

    public void xuLyDong(View view) {
        finish();
    }
}

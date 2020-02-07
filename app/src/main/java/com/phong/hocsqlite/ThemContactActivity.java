package com.phong.hocsqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class ThemContactActivity extends AppCompatActivity {

    EditText edtMa, edtTen, edtPhone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_them_contact);
        addControls();
    }

    private void addControls() {
        edtMa = findViewById(R.id.edtMa);
        edtTen = findViewById(R.id.edtTen);
        edtPhone = findViewById(R.id.edtPhone);
    }

    public void xuLyLuu(View view) {
        //Lưu vào csdl:
        int ma = Integer.parseInt(edtMa.getText().toString());
        String ten = edtTen.getText().toString();
        String phone = edtPhone.getText().toString();

        ContentValues values = new ContentValues();
        values.put("Ma",ma);
        values.put("Ten",ten);
        values.put("Phone",phone);

        long kq = MainActivity.database.insert("Contact",null,values);
        if (kq > 0){
            Toast.makeText(ThemContactActivity.this,"Thêm thành công",Toast.LENGTH_LONG).show();
        }
        else
        {
            Toast.makeText(ThemContactActivity.this,"Thêm thất bại",Toast.LENGTH_LONG).show();
        }
    }

    public void xuLyTiep(View view) {
        edtMa.setText("");
        edtTen.setText("");
        edtPhone.setText("");
        edtMa.requestFocus();
    }

    public void xuLyDong(View view) {
        finish();
    }
}

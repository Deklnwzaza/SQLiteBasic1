package com.example.sqlitebasics;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private ContentValues cv;
    private MyHelper mHelper;
    private SQLiteDatabase mDatabase;
    private SimpleCursorAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mHelper = new MyHelper(this);
        mDatabase = mHelper.getWritableDatabase();

        final Cursor cursor = readAllData();

        mAdapter = new SimpleCursorAdapter(
                this,
                android.R.layout.simple_list_item_2,
                cursor,
                new String[]{MyHelper.COL_NAME, MyHelper.COL_PHONE_NUMBER},
                new int[]{android.R.id.text1, android.R.id.text2},
                0
        );

        final ListView listView = (ListView) findViewById(R.id.contacts_listview);
        listView.setAdapter(mAdapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, final long id) {
                final String Id = String.valueOf(id);
                new AlertDialog.Builder(MainActivity.this)
                        .setTitle("Confirm")
                        .setMessage("Are you sure to delete data")
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                mDatabase.delete(
                                        MyHelper.TABLE_NAME,
                                        MyHelper.COL_ID + " LIKE ?",
                                        new String[]{ Id }
                                );
                                Cursor cursor = readAllData();
                                mAdapter.changeCursor(cursor);

                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
            }
        });
        // กำหนดการทำงานของปุ่ม Add Contact
        Button insertButton = (Button) findViewById(R.id.insert_button);
        insertButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(
                        MainActivity.this,
                        SecondActivity.class);
                startActivityForResult(intent, 123);


            }
        });

        // กำหนดการทำงานปุ่ม Delete Contact
        Button deleteButton = (Button) findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteContact();
            }
        });
    }


    private void deleteContact() {

        mDatabase.delete(MyHelper.TABLE_NAME,null,null);

        Cursor cursor = readAllData();
        mAdapter.changeCursor(cursor);
    }

    private Cursor readAllData() {
        String[] columns = {
                MyHelper.COL_ID,
                MyHelper.COL_NAME,
                MyHelper.COL_PHONE_NUMBER
        };

        return mDatabase.query(MyHelper.TABLE_NAME, columns, null, null, null, null, null);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 123) {
            if (RESULT_OK == resultCode) {
                String userName = data.getStringExtra("user");
                String tel = data.getStringExtra("tele");
                 cv = new ContentValues();
                cv.put(MyHelper.COL_NAME, userName);
                cv.put(MyHelper.COL_PHONE_NUMBER, tel);
                mDatabase.insert(MyHelper.TABLE_NAME, null, cv);

                Cursor cursor = readAllData();
                mAdapter.changeCursor(cursor);
            }
        }
    }

}

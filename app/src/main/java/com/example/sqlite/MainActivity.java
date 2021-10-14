package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnClear;
    EditText etName, etSize, etStrength, etPrice;

    DBHelper dbHelper;
    SQLiteDatabase database;
    ContentValues contentValues;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAdd = (Button) findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        btnClear = (Button) findViewById(R.id.btnClear);
        btnClear.setOnClickListener(this);

        etName = (EditText) findViewById(R.id.etName);
        etSize = (EditText) findViewById(R.id.etSize);
        etStrength = (EditText) findViewById(R.id.etStrength);
        etPrice = (EditText) findViewById(R.id.etPrice);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();

        UpdateTable();
    }

    public  void  UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
            int sizeIndex = cursor.getColumnIndex(DBHelper.KEY_SIZE);
            int strengthIndex = cursor.getColumnIndex(DBHelper.KEY_STRENGTH);
            int priceIndex = cursor.getColumnIndex(DBHelper.KEY_PRICE);

            TableLayout dbOutput = findViewById(R.id.dbtable);
            dbOutput.removeAllViews();
            do{
                TableRow dbOutputRow = new TableRow(this);
                dbOutputRow.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);

                TextView outputID = new TextView(this);
                params.weight =1.0f;
                outputID.setLayoutParams(params);
                outputID.setText(cursor.getString(idIndex));
                dbOutputRow.addView(outputID);

                TextView outputName = new TextView(this);
                params.weight =1.0f;
                outputName.setLayoutParams(params);
                outputName.setText(cursor.getString(nameIndex));
                dbOutputRow.addView(outputName);

                TextView outputSize = new TextView(this);
                params.weight =1.0f;
                outputSize.setLayoutParams(params);
                outputSize.setText(cursor.getString(sizeIndex));
                dbOutputRow.addView(outputSize);

                TextView outputStrength = new TextView(this);
                params.weight =1.0f;
                outputStrength.setLayoutParams(params);
                outputStrength.setText(cursor.getString(strengthIndex));
                dbOutputRow.addView(outputStrength);

                TextView outputPrice = new TextView(this);
                params.weight =1.0f;
                outputPrice.setLayoutParams(params);
                outputPrice.setText(cursor.getString(priceIndex));
                dbOutputRow.addView(outputPrice);

                Button btnDelete = new Button(this);
                btnDelete.setOnClickListener(this);
                params.weight = 1.0f;
                btnDelete.setLayoutParams(params);
                btnDelete.setText("Удалить запись");
                btnDelete.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(btnDelete);

                dbOutput.addView(dbOutputRow);
            }while (cursor.moveToNext());
        }
        cursor.close();
    }

    @Override
    public void onClick(View v) {

        database = dbHelper.getWritableDatabase();
        switch (v.getId()) {

            case R.id.btnAdd:

                String name = etName.getText().toString();
                String size = etSize.getText().toString();
                String strength = etStrength.getText().toString();
                String price = etPrice.getText().toString();

                contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_SIZE, size);
                contentValues.put(DBHelper.KEY_STRENGTH, strength);
                contentValues.put(DBHelper.KEY_PRICE, price);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

                etName.setText("");
                etSize.setText("");
                etStrength.setText("");
                etPrice.setText("");
                UpdateTable();
                break;

            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                UpdateTable();
                break;

            default:
                database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID +" = ?", new String[]{String.valueOf(v.getId())});
                UpdateTable();

                contentValues = new ContentValues();
                Cursor cursorUdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUdater.moveToFirst()) {
                    int idIndex = cursorUdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUdater.getColumnIndex(DBHelper.KEY_NAME);
                    int countryIndex = cursorUdater.getColumnIndex(DBHelper.KEY_SIZE);
                    int tasteIndex = cursorUdater.getColumnIndex(DBHelper.KEY_STRENGTH);
                    int priceIndex = cursorUdater.getColumnIndex(DBHelper.KEY_PRICE);
                    int realId = 1;
                    do{
                        if(cursorUdater.getInt(idIndex)>realId);
                        {
                            contentValues.put(DBHelper.KEY_ID, realId);
                            contentValues.put(DBHelper.KEY_NAME, cursorUdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_SIZE, cursorUdater.getString(countryIndex));
                            contentValues.put(DBHelper.KEY_STRENGTH, cursorUdater.getString(tasteIndex));
                            contentValues.put(DBHelper.KEY_PRICE, cursorUdater.getString(priceIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);

                        }
                        realId++;
                    }while (cursorUdater.moveToNext());
                    if(cursorUdater.moveToLast())
                    {
                        database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{cursorUdater.getString(idIndex)});
                    }
                }
                cursorUdater.close();
                UpdateTable();
                break;
        }
        dbHelper.close();
    }
}


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
import android.widget.Toast;


public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    Button btnAdd, btnClear, btnSum;
    EditText etName, etPrice;
    TextView VwSumma;
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
        etPrice = (EditText) findViewById(R.id.etPrice);

        VwSumma = (TextView) findViewById(R.id.VwSumma);
        VwSumma.setText("0");
        btnSum = (Button) findViewById(R.id.btnSum);
        btnSum.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        dbHelper.onUpgrade(database,2,dbHelper.DATABASE_VERSION);
        UpdateTable();
    }

    public  void  UpdateTable(){
        Cursor cursor = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(DBHelper.KEY_ID);
            int nameIndex = cursor.getColumnIndex(DBHelper.KEY_NAME);
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

                TextView outputPrice = new TextView(this);
                params.weight =1.0f;
                outputPrice.setLayoutParams(params);
                outputPrice.setText(cursor.getString(priceIndex));
                dbOutputRow.addView(outputPrice);

                Button btnDelete = new Button(this);
                btnDelete.setOnClickListener(this);
                params.weight = 1.0f;
                btnDelete.setLayoutParams(params);
                btnDelete.setText("Удалить");
                btnDelete.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(btnDelete);

                Button btnKorz = new Button(this);
                btnKorz.setOnClickListener(this);
                params.weight = 1.0f;
                btnKorz.setLayoutParams(params);
                btnKorz.setText("В корзину");
                btnKorz.setId(cursor.getInt(idIndex));
                dbOutputRow.addView(btnKorz);

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
                String price = etPrice.getText().toString();

                contentValues = new ContentValues();

                contentValues.put(DBHelper.KEY_NAME, name);
                contentValues.put(DBHelper.KEY_PRICE, price);

                database.insert(DBHelper.TABLE_CONTACTS, null, contentValues);

                etName.setText("");
                etPrice.setText("");
                UpdateTable();
                break;

            case R.id.btnClear:
                database.delete(DBHelper.TABLE_CONTACTS, null, null);
                TableLayout dbOutput = findViewById(R.id.dbtable);
                dbOutput.removeAllViews();
                UpdateTable();
                break;

            case R.id.btnSum:
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сумма заказа: " +VwSumma.getText() + " руб.", Toast.LENGTH_SHORT);
                toast.show();
                VwSumma.setText("0");
                break;

            default:
                if(((Button)v).getText() == "В корзину"){
                    Cursor cursor = database.query(DBHelper.TABLE_CONTACTS,null,DBHelper.KEY_ID + "=?",new String[]{String.valueOf(v.getId())}, null, null, null);
                    float sum = Float.parseFloat(VwSumma.getText().toString());
                    if (cursor.moveToFirst()) {
                        int prs = cursor.getColumnIndex(DBHelper.KEY_PRICE);
                        do {
                            sum = sum + cursor.getFloat(prs);
                        } while (cursor.moveToNext());
                    }
                    cursor.close();
                    VwSumma.setText(String.valueOf(sum));
                    UpdateTable();
                }

                if(((Button) v).getText()== "Удалить") {
                View outputDBRow = (View) v.getParent();
                ViewGroup outputDB = (ViewGroup) outputDBRow.getParent();
                outputDB.removeView(outputDBRow);
                outputDB.invalidate();
                database.delete(DBHelper.TABLE_CONTACTS,DBHelper.KEY_ID +" = ?", new String[]{String.valueOf(v.getId())});
                UpdateTable();

                contentValues = new ContentValues();
                Cursor cursorUdater = database.query(DBHelper.TABLE_CONTACTS, null, null, null, null, null, null);
                if (cursorUdater.moveToFirst()) {
                    int idIndex = cursorUdater.getColumnIndex(DBHelper.KEY_ID);
                    int nameIndex = cursorUdater.getColumnIndex(DBHelper.KEY_NAME);
                    int priceIndex = cursorUdater.getColumnIndex(DBHelper.KEY_PRICE);
                    int realId = 1;
                    do{
                        if(cursorUdater.getInt(idIndex)>realId);
                        {
                            contentValues.put(DBHelper.KEY_ID, realId);
                            contentValues.put(DBHelper.KEY_NAME, cursorUdater.getString(nameIndex));
                            contentValues.put(DBHelper.KEY_PRICE, cursorUdater.getString(priceIndex));
                            database.replace(DBHelper.TABLE_CONTACTS, null, contentValues);
                        }
                        realId++;
                    }while (cursorUdater.moveToNext());
                    if(cursorUdater.moveToLast()&& v.getId()!=realId)
                    {
                        database.delete(DBHelper.TABLE_CONTACTS, DBHelper.KEY_ID + " = ?", new String[]{cursorUdater.getString(idIndex)});
                    }
                }
                cursorUdater.close();
                UpdateTable();
                break;
           }
        }
        dbHelper.close();
    }
}


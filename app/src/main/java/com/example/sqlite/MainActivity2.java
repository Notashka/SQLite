package com.example.sqlite;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.database.sqlite.SQLiteDatabase;
import android.database.Cursor;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity2 extends AppCompatActivity implements View.OnClickListener{

    Button btnSum, btnDB, btnStore;
    TextView VwSumma;
    DBHelper dbHelper;
    SQLiteDatabase database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

        VwSumma = (TextView) findViewById(R.id.VwSumma);
        VwSumma.setText("0");
        btnSum = (Button) findViewById(R.id.btnSum);
        btnSum.setOnClickListener(this);

        btnDB = (Button) findViewById(R.id.btnDB);
        btnDB.setOnClickListener(this);
        btnStore = (Button) findViewById(R.id.btnStore);
        btnStore.setOnClickListener(this);

        dbHelper = new DBHelper(this);
        database = dbHelper.getWritableDatabase();
        
        CreateTable();
    }

    public  void CreateTable(){
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

            case R.id.btnSum:
                Toast toast = Toast.makeText(getApplicationContext(),
                        "Сумма заказа: " +VwSumma.getText() + " руб.", Toast.LENGTH_SHORT);
                toast.show();
                VwSumma.setText("0");
                break;

            case R.id.btnDB:
                Intent intent1 = new Intent(this,MainActivity.class);
                startActivity(intent1);
                break;

            case R.id.btnStore:
                break;

            default:
                if(((Button)v).getText() == "В корзину")
                {
                    Cursor cursor = database.query(DBHelper.TABLE_CONTACTS,null,DBHelper.KEY_ID + "=?",new String[]{String.valueOf(v.getId())}, null, null, null);
                    float sum = Float.parseFloat(VwSumma.getText().toString());
                    if (cursor.moveToFirst()) {
                        int Price = cursor.getColumnIndex(DBHelper.KEY_PRICE);
                        do { sum = sum + cursor.getFloat(Price);
                        } while (cursor.moveToNext()); }
                    cursor.close();
                    VwSumma.setText(String.valueOf(sum));
                    CreateTable();
                }
        }
        dbHelper.close();
    }
}


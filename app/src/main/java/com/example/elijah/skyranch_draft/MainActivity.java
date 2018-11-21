package com.example.elijah.skyranch_draft;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    private DatabaseHelper mDBHelper;
    private SQLiteDatabase mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // establish a database
        mDBHelper = DatabaseHelper.newInstance(this);
        mDatabase  = mDBHelper.getWritableDatabase();

        final EditText id = findViewById(R.id.etUsername);
        final TextView tvRes = findViewById(R.id.tvResData);
        Button btnLogin = findViewById(R.id.btnLogin);
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // check niya kung response is success
                // create a loginToken instance
                // add the usercreds to db

               LoginToken result = mDBHelper.getUserToken(id.getText().toString());
                if (!result.equals(null)){
                    Toast.makeText(MainActivity.this, ""+result, Toast.LENGTH_LONG).show();
//                    finish();
//                    Intent secondScreen = new Intent(MainActivity.this,SecondScreen.class);
//                    startActivity(secondScreen);
                }else{
                    Toast.makeText(MainActivity.this, "Does not exists", Toast.LENGTH_LONG).show();
                }
            }
        });


        Button btnViewAll = findViewById(R.id.bViewAll);
        btnViewAll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tvRes.setText("");
                List<LoginToken> userTokens = mDBHelper.getAllUserTokens();
                tvRes.setText(userTokens.toString());

            }
        });

        Button btnPopulate = findViewById(R.id.bPopulate);
//        btnPopulate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                createDummyData();
//            }
//        });

        Button bShop = findViewById(R.id.bShop);
        bShop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ProductActivity.class);
                startActivity(intent);
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
        mDatabase.close();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mDatabase  = mDBHelper.getWritableDatabase();
    }


//    public void createDummyData(){
//        LoginToken dummy_user1 = new LoginToken(null, "Oount123", "jean09", 1001);
//        LoginToken dummy_user2 = new LoginToken(null, "Like423", "jean79", 1002);
//        LoginToken dummy_user3 = new LoginToken(null, "Good Times", "mrhacker", 1002);
//
//        mDBHelper.addUserToken(dummy_user1);
//        mDBHelper.addUserToken(dummy_user2);
//        mDBHelper.addUserToken(dummy_user3);
//    }
}

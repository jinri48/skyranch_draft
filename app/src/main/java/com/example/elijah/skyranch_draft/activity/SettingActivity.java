package com.example.elijah.skyranch_draft.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.elijah.skyranch_draft.BaseUrlActivity;
import com.example.elijah.skyranch_draft.R;
import com.example.elijah.skyranch_draft.utils.AidlUtil;
import com.example.elijah.skyranch_draft.utils.BluetoothUtil;
import sunmi.sunmiui.dialog.DialogCreater;
import sunmi.sunmiui.dialog.ListDialog;

public class SettingActivity extends BaseActivity implements View.OnClickListener {

//    String[] method = new String[]{"AIDL", "BlueTooth"};
    String[] method = new String[]{"AIDL"};
    private TextView mTextView1, mTextView2;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        findViewById(R.id.setting_connect).setOnClickListener(this);
        findViewById(R.id.setting_info).setOnClickListener(this);
        mTextView1 = (TextView)findViewById(R.id.setting_conected);
        mTextView2 =(TextView)findViewById(R.id.setting_disconected);
        LinearLayout layout_api = (LinearLayout) findViewById(R.id.setting_api);

        layout_api.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i =  new Intent(SettingActivity.this, BaseUrlActivity.class);
                startActivity(i);
            }
        });
        mTextView1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AidlUtil.getInstance().connectPrinterService(SettingActivity.this);
                setService();
            }
        });
        mTextView2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AidlUtil.getInstance().disconnectPrinterService(SettingActivity.this);
                setService();
            }
        });

        /*Toast.makeText(SettingActivity.this, "" +baseApp.isAidl(), Toast.LENGTH_SHORT).show();*/
        if(baseApp.isAidl())
            ((TextView)findViewById(R.id.setting_textview1)).setText("AIDL");
        else
            ((TextView)findViewById(R.id.setting_textview1)).setText("BlueTooth");
        setService();
    }

    private void setService(){
        if(AidlUtil.getInstance().isConnect()){
            mTextView1.setTextColor(getResources().getColor(R.color.white1));
            mTextView1.setEnabled(false);
            mTextView2.setTextColor(getResources().getColor(R.color.white));
            mTextView2.setEnabled(true);
        }else{
            mTextView1.setTextColor(getResources().getColor(R.color.white));
            mTextView1.setEnabled(true);
            mTextView2.setTextColor(getResources().getColor(R.color.white1));
            mTextView2.setEnabled(false);
        }
    }



    @Override
    public void onClick(View v) {
        final ListDialog listDialog;
        switch (v.getId()){
            case R.id.setting_connect:
                listDialog = DialogCreater.createListDialog(this, getResources().getString(R.string.connect_method), getResources().getString(R.string.cancel), method);

                listDialog.setItemClickListener(new ListDialog.ItemClickListener() {
                    @Override
                    public void OnItemClick(int position) {
                        ((TextView)findViewById(R.id.setting_textview1)).setText(method[position]);
                        if(position == 0){
                            baseApp.setAidl(true);
                            BluetoothUtil.disconnectBlueTooth(SettingActivity.this);
                        }
//                        else{
//                            if(!BluetoothUtil.connectBlueTooth(SettingActivity.this)){
//                                ((TextView)findViewById(R.id.setting_textview1)).setText(method[0]);
//                            }else{
//                                baseApp.setAidl(false);
//                            }
//                        }

                        listDialog.cancel();
                    }
                });
                listDialog.show();
                break;
            case R.id.setting_info:
//                startActivity(new Intent(SettingActivity.this, PrinterInfoActivity.class));
                break;
            default:
                break;
        }
    }
}

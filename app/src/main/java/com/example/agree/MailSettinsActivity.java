package com.example.agree;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class MailSettinsActivity extends AppCompatActivity {

    EditText serverName;
    EditText portNumber;
    EditText account;
    EditText pass;
    Button buttonSav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_setting);
        serverName = findViewById(R.id.server);
        portNumber = findViewById(R.id.port);
        account = findViewById(R.id.account);
        pass = findViewById(R.id.pass);
        buttonSav = (Button) findViewById(R.id.button_save);
        serverName.setText(MainActivity.mailTask.getServerName());
        portNumber.setText(MainActivity.mailTask.getPortNumber());
        account.setText(MainActivity.mailTask.getAccount());
        pass.setText(MainActivity.mailTask.getPass());

        buttonSav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mailTask.setServerName(serverName.getText().toString());
                MainActivity.mailTask.setPortNumber(portNumber.getText().toString());
                MainActivity.mailTask.setAccount(account.getText().toString());
                MainActivity.mailTask.setPass(pass.getText().toString());
                finish();
            }
        });
    }
}

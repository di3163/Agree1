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
    EditText smtpServer;
    EditText smtpPort;
    EditText smtpAccount;
    EditText smtpPass;

    Button buttonSav;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.mail_setting);
        serverName = findViewById(R.id.server);
        portNumber = findViewById(R.id.port);
        account = findViewById(R.id.account);
        pass = findViewById(R.id.pass);
        smtpServer = findViewById(R.id.smtpserver);
        smtpPort = findViewById(R.id.smtpport);
        smtpAccount = findViewById(R.id.smtpaccount);
        smtpPass = findViewById(R.id.smtppass);

        buttonSav = (Button) findViewById(R.id.button_save);
        serverName.setText(MainActivity.mailTask.getServerName());
        portNumber.setText(MainActivity.mailTask.getPortNumber());
        account.setText(MainActivity.mailTask.getAccount());
        pass.setText(MainActivity.mailTask.getPass());
        smtpServer.setText(MainActivity.mailTask.getSmtpServer());
        smtpPort.setText(MainActivity.mailTask.getSmtpPort());
        smtpAccount.setText(MainActivity.mailTask.getSmtpAccount());
        smtpPass.setText(MainActivity.mailTask.getSmtpPass());

        buttonSav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mailTask.setServerName(serverName.getText().toString().replaceAll(" ","").toLowerCase());
                MainActivity.mailTask.setPortNumber(portNumber.getText().toString().replaceAll(" ",""));
                MainActivity.mailTask.setAccount(account.getText().toString().replaceAll(" ","").toLowerCase());
                MainActivity.mailTask.setPass(pass.getText().toString().replaceAll(" ",""));

                MainActivity.mailTask.setSmtpServer(smtpServer.getText().toString().replaceAll(" ","").toLowerCase());
                MainActivity.mailTask.setSmtpPort(smtpPort.getText().toString().replaceAll(" ",""));
                MainActivity.mailTask.setSmtpAccount(smtpAccount.getText().toString().replaceAll(" ","").toLowerCase());
                MainActivity.mailTask.setSmtpPass(smtpPass.getText().toString().replaceAll(" ",""));
                MainActivity.mailTask.loadMailsetting();
                finish();
            }
        });
    }
}

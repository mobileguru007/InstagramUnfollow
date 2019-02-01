package com.it_tech613.zhe.instagramunfollow.activity;

import android.app.Dialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.it_tech613.zhe.instagramunfollow.R;
import com.it_tech613.zhe.instagramunfollow.utils.ConfirmExitDlg;
import com.it_tech613.zhe.instagramunfollow.utils.PreferenceManager;

public class LoginActivity extends AppCompatActivity {

    private EditText etLogin;
    private EditText etPassword;
    ConfirmExitDlg confirmExitDlg;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        PreferenceManager.logoutManager();

        boolean show_faq=getIntent().getBooleanExtra("show_faq",false);
        if (show_faq){
            confirmExitDlg =new ConfirmExitDlg(
                    LoginActivity.this,
                    new ConfirmExitDlg.DialogNumberListener() {
                        @Override
                        public void OnYesClick(Dialog dialog) {
                            confirmExitDlg.dismiss();
                            startActivity(new Intent(LoginActivity.this,FaqActivity.class));
                        }

                        @Override
                        public void OnCancelClick(Dialog dialog) {
                            confirmExitDlg.dismiss();
                        }
                    },
                    getResources().getString(R.string.authorization_error),
                    getString(R.string.goto_faq),
                    false);
            confirmExitDlg.show();
        }
        etLogin = findViewById(R.id.etLogin);
        etPassword = findViewById(R.id.etPassword);
        if (savedInstanceState != null) {
            etLogin.setText(savedInstanceState.getString("username"));
            etPassword.setText(savedInstanceState.getString("password"));
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString("username", etLogin.getText().toString());
        outState.putString("password", etPassword.getText().toString());
        super.onSaveInstanceState(outState);
    }

    public void onClick(View view) {
        setResult(RESULT_OK, new Intent()
                .putExtra("username", etLogin.getText().toString())
                .putExtra("password", etPassword.getText().toString()));
        finish();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }
}


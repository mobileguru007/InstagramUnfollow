package com.it_tech613.zhe.instagramunfollow.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.it_tech613.zhe.instagramunfollow.R;


public class ConfirmExitDlg extends Dialog {

    Context context;
    DialogNumberListener listener;
    Button btn_ok;
    Button btn_cancel;
    public ConfirmExitDlg(@NonNull Context context, final DialogNumberListener listener) {
        super(context);
        this.context = context;
        this.listener = listener;
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_confirm_cancel);
        getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        btn_ok = (Button)findViewById(R.id.btn_ok);
        btn_cancel= (Button)findViewById(R.id.btn_skip);
        TextView warning_text=(TextView)findViewById(R.id.warning_text);
        warning_text.setText(R.string.exit_alert);
        btn_ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnYesClick(ConfirmExitDlg.this);
            }
        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.OnCancelClick(ConfirmExitDlg.this);
            }
        });
    }
    public interface DialogNumberListener {
        public void OnYesClick(Dialog dialog);
        public void OnCancelClick(Dialog dialog);
    }
}
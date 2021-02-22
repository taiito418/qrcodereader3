package com.itokikaku.qrcodereader3;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class MyConfirmDialog extends DialogFragment {

    private DialogInterface.OnClickListener okClickListener = null;
    private DialogInterface.OnClickListener ngClickListener = null;
    private DialogInterface.OnClickListener ntClickListener = null;

    public String title;
    public String message;
    private View view;
    private Boolean close = true;

    public static MyConfirmDialog newInstance() {
        MyConfirmDialog fragment = new MyConfirmDialog();
        Bundle args = new Bundle();
        fragment.setArguments(args);

        return fragment;
    }

    public void setContent(String title, String message) {
        this.title = title;
        this.message = message;
    }
    public void setLayout(View view) {
        this.view = view;
    }

    public void setNoClose(Boolean close) {
        this.close = close;
    }

    @Override
    public Dialog onCreateDialog(Bundle safedInstanceState) {


        //AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.Theme_CustomConfirmDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setView(this.view);

        builder.setTitle(this.title)
                .setMessage(this.message)
                .setPositiveButton("ブラウザで開く", this.okClickListener)
                .setNeutralButton("共有",  this.ntClickListener)
                .setNegativeButton("やり直し", this.ngClickListener);


        if (this.close) {
            return builder.create();
        } else {
            final AlertDialog alertDialog = builder.create();
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialog) {
                    Button okButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                    okButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //alertDialog.dismiss();
                        }
                    });
                }

            });
            return alertDialog;
        }
    }

    public void setOnOkClickListener(DialogInterface.OnClickListener listener) {
        this.okClickListener = listener;
    }
    public void setOnNgClickListener(DialogInterface.OnClickListener listener) {
        this.ngClickListener = listener;
    }
    public void setOnNtClickListener(DialogInterface.OnClickListener listener) {
        this.ntClickListener = listener;
    }
}
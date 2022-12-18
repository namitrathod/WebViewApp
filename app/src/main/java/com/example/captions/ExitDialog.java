package com.example.captions;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.google.android.ads.nativetemplates.TemplateView;
import com.google.android.gms.ads.nativead.NativeAd;

public class ExitDialog extends Dialog
{
    NativeAd ad;
    Activity activity;
    public ExitDialog(Activity activity, NativeAd ad)
    {
        super(activity);
        this.activity= activity;
        this.ad = ad;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.exit_dialog);
        Button yes = findViewById(R.id.btn_yes);
        Button no = findViewById(R.id.btn_no);
        yes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
            }
        });
        no.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        TemplateView ad = findViewById(R.id.ad_template);
        if(this.ad == null){
            ad.setVisibility(View.GONE);
        }
        else
        {
            ad.setVisibility(View.VISIBLE);
            ad.setNativeAd(this.ad);
        }
    }
}

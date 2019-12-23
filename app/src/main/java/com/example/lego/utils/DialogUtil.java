package com.example.lego.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

import com.example.lego.R;

public class DialogUtil {

    public static void showDialogAskSubmit(Context context, int remain, int total, final OnClickListener onClickListener){
        final Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.dialog_update_product);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        EditText edtRemain = dialog.findViewById(R.id.edtContain);
        EditText edtTotal = dialog.findViewById(R.id.edtTotal);

        edtRemain.setText(remain + "");
        edtTotal.setText(total + "");

        dialog.findViewById(R.id.si_btnConfirm).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
                onClickListener.onClickPositive(dialog, edtRemain.getText().toString(), edtTotal.getText().toString());
            }
        });

        dialog.show();
    }

    public interface OnClickListener{

        void onClickPositive(Dialog dialog, String remain, String total);
    }

}

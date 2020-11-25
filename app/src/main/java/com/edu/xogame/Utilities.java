package com.edu.xogame;

import android.content.Context;

import androidx.appcompat.app.AlertDialog;

import com.edu.xogame.network.Client;
import com.edu.xogame.network.Host;
import com.edu.xogame.players.Player;


public class Utilities {

    public static final int MESSAGE_MOVE = 1;
    public static Host HOST;
    public static Client CLIENT;

    public static void createDialog(String title, String message , String positiveText, String negativeText, Context context, IFunction positiveFunc, IFunction negativeFunc) {
        //Tạo đối tượng
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //Thiết lập tiêu đề
        builder.setTitle(title);
        builder.setMessage(message);
        // Nút Ok
        if (positiveText != null) builder.setPositiveButton(positiveText, (dialog, which) -> positiveFunc.execute());

        //Nút Cancel
        if (negativeText != null) builder.setNegativeButton(negativeText, (dialog, id) -> negativeFunc.execute());
        //Tạo dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.setCancelable(false);
        //Hiển thị
        alertDialog.show();
    }
}

package com.example.codeplayer.utils;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.example.codeplayer.CodePlayerApp;

/**
 * descreption:
 * company: codingke.com
 *
 * @author vince
 * @date 15/10/13
 */
public class AppUtils {

    //隐藏输入法
    public static void hideInputMethod(View view) {
        InputMethodManager imm = (InputMethodManager) CodePlayerApp.context
                .getSystemService(Context.INPUT_METHOD_SERVICE);
        if(imm.isActive()) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
}

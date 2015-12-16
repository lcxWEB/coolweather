package com.cxli.coolweather.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.cxli.coolweather.app.service.AutoUpdateService;

/**
 * Created by cx.li on 2015/12/16.
 */
public class AutoUpdateReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent inte = new Intent(context, AutoUpdateService.class);
        context.startService(inte);
    }
}

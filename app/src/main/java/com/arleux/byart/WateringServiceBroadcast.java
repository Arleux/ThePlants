package com.arleux.byart;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;

public class WateringServiceBroadcast extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        WateringService.setServiceAlarm(context); //включить службу при загрузке тлф
/*        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "tag");
        wl.acquire();
        wl.release();

 */
    }
}

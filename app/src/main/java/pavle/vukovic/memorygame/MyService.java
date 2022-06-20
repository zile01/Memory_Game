package pavle.vukovic.memorygame;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;

import androidx.annotation.NonNull;

public class MyService extends Service {
    private final String DB_NAME = "GAMES";
    public Binder binder = null;
    PlayerDBHelper helper = new PlayerDBHelper(this, DB_NAME, null, 1);
    HTTPHelper http_helper = new HTTPHelper();
    Adapter adapter;

    public MyService() {}

    @Override
    public IBinder onBind(Intent intent) {
        if (binder == null) {
            binder = new Binder(helper, http_helper);
        }

        return binder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        binder.stop();
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binder = null;
    }
}
package com.example.synctrouble;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class SyncService extends Service {
    SyncAdapter mSyncAdapter;

    @Override
    public void onCreate() {

        super.onCreate();
        mSyncAdapter = new SyncAdapter(getApplicationContext(), true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mSyncAdapter.getSyncAdapterBinder();
    }

    @SuppressWarnings("unused")
    private static final String TAG = "SyncService";

}


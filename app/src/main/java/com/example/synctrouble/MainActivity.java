package com.example.synctrouble;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

// See https://developer.android.com/training/id-auth/custom_auth.html

public class MainActivity extends AppCompatActivity {
    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAccount = createSyncAccount();
        if (mAccount == null) {
            Log.d(TAG, "<<<<Failed to create sync mAccount.");
        } else {
            // Add the periodic sync. If the periodic sync is already defined, this will change
            // the syncing period if it differs.
            Log.d(TAG, "<<<<Adding periodic sync");
            ContentResolver.addPeriodicSync(mAccount, AUTHORITY, Bundle.EMPTY, SYNC_SECONDS);
//            findViewById(R.id.doManualSync).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Bundle settingsBundle = new Bundle();
//                    settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_MANUAL, true);
//                    settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_EXPEDITED, true);
////settingsBundle.putBoolean(ContentResolver.SYNC_EXTRAS_FORCE, true);
//                    ContentResolver.requestSync(mAccount, AUTHORITY, settingsBundle);
//                }
//            });
        }
    }

    /**
     * Create the custom mAccount that we will use with our app for syncing.
     *
     * @return Account created or one that was already defined (assume only one.)
     */
    public Account createSyncAccount() {
        AccountManager am = AccountManager.get(this);
        Account[] accounts;

        try {
            accounts = am.getAccountsByType(ACCOUNT_TYPE);
        } catch (SecurityException e) { // This never should happen
            accounts = new Account[]{};
        }
        if (accounts.length > 0) { // already have an mAccount defined
            Log.d(TAG, "<<<<Account already defined.");
            return accounts[0];
        }

        // The following stores the mAccount on the device. "password" of addAccountExplicitly
        // should be hashed or otherwise encrypted to protect the credentials. Store clear text
        // is insecure and poor form.
        Account newAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        if (am.addAccountExplicitly(newAccount, "pass1", null)) {
            // Set this mAccount as syncable.
            Log.d(TAG, "<<<<New mAccount added. Setting syncable.");
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
        } else {// else The mAccount exists or some other error occurred.
            Log.d(TAG, "<<<<Could not add new mAccount.");
            newAccount = null;
        }
        return newAccount;
    }

    private static final String TAG = "MainActivity";
    private static final String ACCOUNT_TYPE = "com.example.synctrouble";
    private static final String ACCOUNT_NAME = "Default Account";
    public static final String AUTHORITY = "com.example.synctrouble";
    public static int SYNC_SECONDS = 15;
}

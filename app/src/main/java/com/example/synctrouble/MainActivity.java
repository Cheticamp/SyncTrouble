package com.example.synctrouble;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Account account;

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        account = createSyncAccount();
        if (account == null) {
            Log.d(TAG, "<<<<Failed to create sync account.");
        } else {
            Log.d(TAG, "<<<<Adding periodic sync");
            ContentResolver.addPeriodicSync(account, AUTHORITY, Bundle.EMPTY,
                    SYNC_SECONDS);
        }
    }

    public Account createSyncAccount() {
        AccountManager am = AccountManager.get(this);
        Account[] accounts;

        try {
            accounts = am.getAccountsByType(ACCOUNT_TYPE);
        } catch (SecurityException e) { // This never should happen
            accounts = new Account[]{};
        }
        if (accounts.length > 0) { // already have an account defined
            Log.d(TAG, "<<<<Account already defined.");
            return accounts[0];
        }

        Account newAccount = new Account(ACCOUNT_NAME, ACCOUNT_TYPE);
        if (am.addAccountExplicitly(newAccount, "pass1", null)) {
            // Set this account as syncable.
            Log.d(TAG, "<<<<New account added. Setting syncable.");
            ContentResolver.setIsSyncable(newAccount, AUTHORITY, 1);
            ContentResolver.setSyncAutomatically(newAccount, AUTHORITY, true);
        } else {// else The account exists or some other error occurred.
            Log.d(TAG, "<<<<Could not add new account.");
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

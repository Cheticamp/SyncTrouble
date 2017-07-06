package com.example.synctrouble;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        if (createSyncAccount() == null) {
            Log.d(TAG, "<<<<Failed to create sync account.");
        } else {
            Log.d(TAG, "<<<<Created sync account.");
            addPeriodicSync();
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
            newAccount = null;
            Log.d(TAG, "<<<<Could not add new account.");
        }
        return newAccount;
    }

    public void addPeriodicSync() {
        Account account;

        account = getSyncAccount();
        if (account == null) {
            Log.d(TAG, "<<<<Failed to identify account for periodic sync");
            return;
        }
        // ...add back potentially with changed parameters
        Log.d(TAG, "<<<<Adding periodic sync");
        ContentResolver.addPeriodicSync(account, AUTHORITY, Bundle.EMPTY,
                SYNC_SECONDS);
    }

    public Account getSyncAccount() {
        AccountManager accountManager = AccountManager.get(this);
        Account[] accounts;

        try {
            accounts = accountManager.getAccountsByType(ACCOUNT_TYPE);
        } catch (SecurityException e) {
            return null;
        }
        return (accounts.length > 0) ? accounts[0] : null;
    }

    private static final String TAG = "MainActivity";
    private static final String ACCOUNT_TYPE = "com.example.synctrouble";
    private static final String ACCOUNT_NAME = "Default Account";
    public static final String AUTHORITY = "com.example.synctrouble";
    public static int SYNC_SECONDS = 15;
}

package com.example.mayank.savecontacts;

import android.Manifest;
import android.content.pm.PackageManager;

import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.List;
import java.util.Set;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import ir.mirrajabi.rxcontacts.Contact;
import ir.mirrajabi.rxcontacts.RxContacts;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = MainActivity.class.getSimpleName();
    public View rootView;
    final int PERMISSION_REQUEST_CODE = 23;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    public void setupPermission() {
        if ((ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.READ_CONTACTS)
                != PackageManager.PERMISSION_GRANTED)
                || (ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED)) {
            String[] permissionNeeded = new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.WRITE_EXTERNAL_STORAGE};
            requestPermissions(permissionNeeded, PERMISSION_REQUEST_CODE);
        } else {
                performOps(rootView);
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case PERMISSION_REQUEST_CODE : {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                {
                    performOps(rootView);
                }
                else {
                    Toast.makeText(MainActivity.this, "Permission not granted", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }

        }
    }

    public void performOps(View view) {
        RxContacts.fetch(this)
                .filter(m->m.getInVisibleGroup() == 1)
                .toSortedList(Contact::compareTo)
                .observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(contacts -> {
                    (new saveContactsTask()).execute(contacts);
                });
    }

    public class saveContactsTask extends AsyncTask<List<Contact>,Void,Void>
    {
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Snackbar.make(rootView,"All Contacts Backed Up",Snackbar.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(List<Contact>[] lists) {
            if(lists!=null)
            {
                BackupContacts.saveTOCSV(lists[0]);
            }
            return null;
        }
    }


    public void saveContacts(final View view) {
        rootView = view;
        setupPermission();
    }
}
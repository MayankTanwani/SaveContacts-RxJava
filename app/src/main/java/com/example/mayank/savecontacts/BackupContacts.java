package com.example.mayank.savecontacts;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Environment;
import android.provider.ContactsContract;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


/**
 * Created by mayank on 2/19/18.
 */

public class BackupContacts {
    public static final String filenameCSV = "ContactsBackUp.csv";
    public  Context context;
    public static BackupContacts instance;

    public BackupContacts(Context context)
    {
        this.context = context;
    }

    public int saveAllContacts()
    {
        int flag = readAllContacts();
        return (flag);
    }

    public int readAllContacts() {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = cr.query(ContactsContract.Contacts.CONTENT_URI,
                null, null, null, null);
        Log.v("AllContacts", cursor.getCount() + " Coulumn : " + cursor.getColumnCount());
        return saveTOCSV(cursor);
    }
    public int saveTOCSV(Cursor c) {
        int flag = 0;
        try {
            int rowcount = 0;
            int colcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File saveFile = new File(sdCardDir, filenameCSV);
            FileWriter fw = new FileWriter(saveFile);
            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = c.getCount();
            colcount = c.getColumnCount();
            if (rowcount > 0) {
                c.moveToFirst();
                for (int i = 0; i < colcount; i++) {
                    if (i != colcount - 1) {
                        bw.write(c.getColumnName(i) + ",");
                    } else {
                        bw.write(c.getColumnName(i));
                    }
                }
            }
            bw.newLine();
            for (int i = 0; i < rowcount; i++) {
                c.moveToPosition(i);
                for (int j = 0; j < colcount; j++) {
                    if (j != colcount - 1)
                        bw.write(c.getString(j) + ",");
                    else
                        bw.write(c.getString(j));
                }
                bw.newLine();
            }
            bw.flush();

            zip(new String[]{sdCardDir + "/"+  filenameCSV},sdCardDir + "/contacts.zip");
            saveFile.delete();
        } catch (Exception ex) {
            flag = 1;
            ex.printStackTrace();
        }finally {
            return flag;
        }
    }

    public void zip(String[] _files, String zipFileName) {
        try {
            BufferedInputStream origin = null;
            FileOutputStream dest = new FileOutputStream(zipFileName);
            ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(
                    dest));
            for (int i = 0; i < _files.length; i++) {
                Log.v("Compress", "Adding: " + _files[i]);
                FileInputStream fi = new FileInputStream(_files[i]);
                origin = new BufferedInputStream(fi);

                ZipEntry entry = new ZipEntry(_files[i].substring(_files[i].lastIndexOf("/") + 1));
                out.putNextEntry(entry);
                int count;

                while ((count = origin.read()) != -1) {
                    out.write(count);
                }
                origin.close();
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

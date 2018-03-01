package com.example.mayank.savecontacts;

import android.os.Environment;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import ir.mirrajabi.rxcontacts.Contact;


/**
 * Created by mayank on 2/19/18.
 */

public class BackupContacts {
    public static final String filenameCSV = "ContactsBackUp.csv";

    public static int saveTOCSV(List<Contact> contacts)
    {
        int flag = 0;
        try {
            int rowcount = 0;
            File sdCardDir = Environment.getExternalStorageDirectory();
            File saveFile = new File(sdCardDir, filenameCSV);
            FileWriter fw = new FileWriter(saveFile);
            BufferedWriter bw = new BufferedWriter(fw);
            rowcount = contacts.size();
            if (rowcount > 0) {
                bw.write("Name,");
                bw.write("Phone Number,");
            }
            bw.newLine();
            for (int i = 0; i < rowcount; i++) {
                bw.write(contacts.get(i).getDisplayName() + ",");
                String str = contacts.get(i).getPhoneNumbers().toString();
                str = str.replace('[',' ');
                str = str.replace(']',' ');
                str = str.replace(',',':');
                bw.write(str);
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

    public static void zip(String[] _files, String zipFileName) {
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

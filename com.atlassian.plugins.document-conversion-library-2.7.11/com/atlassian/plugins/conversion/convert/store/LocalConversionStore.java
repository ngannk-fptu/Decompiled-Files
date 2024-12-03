/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugins.conversion.convert.store;

import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

public class LocalConversionStore
implements ConversionStore {
    private final String localPath;

    public LocalConversionStore(String localPath) {
        this.localPath = localPath;
    }

    @Override
    public OutputStream createFile(UUID uuid) {
        File tempDirectory = new File(this.localPath);
        File f = new File(this.localPath, uuid.toString());
        FileOutputStream fileOutputStream = null;
        try {
            if (!tempDirectory.exists()) {
                tempDirectory.mkdir();
            }
            f.createNewFile();
            fileOutputStream = new FileOutputStream(f);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return fileOutputStream;
    }

    @Override
    public InputStream readFile(UUID uuid) {
        File f = new File(this.localPath, uuid.toString());
        FileInputStream fileInputStream = null;
        try {
            fileInputStream = new FileInputStream(f);
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return fileInputStream;
    }
}


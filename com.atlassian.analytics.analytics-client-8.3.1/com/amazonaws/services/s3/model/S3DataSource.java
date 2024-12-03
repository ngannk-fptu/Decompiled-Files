/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.Log
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.util.IOUtils;
import java.io.File;
import java.io.InputStream;
import org.apache.commons.logging.Log;

public interface S3DataSource {
    public File getFile();

    public void setFile(File var1);

    public InputStream getInputStream();

    public void setInputStream(InputStream var1);

    public static enum Utils {


        public static void cleanupDataSource(S3DataSource req, File fileOrig, InputStream inputStreamOrig, InputStream inputStreamCurr, Log log) {
            if (fileOrig != null) {
                IOUtils.release(inputStreamCurr, log);
            }
            req.setInputStream(inputStreamOrig);
            req.setFile(fileOrig);
        }
    }
}


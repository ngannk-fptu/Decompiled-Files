/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.v3.filecache;

import com.mchange.v1.io.InputStreamUtils;
import com.mchange.v1.io.ReaderUtils;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLogger;
import com.mchange.v3.filecache.URLFetcher;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.URL;

public enum URLFetchers implements URLFetcher
{
    DEFAULT{

        @Override
        public InputStream openStream(URL uRL, MLogger mLogger) throws IOException {
            return uRL.openStream();
        }
    }
    ,
    BUFFERED_WGET{

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        @Override
        public InputStream openStream(URL uRL, MLogger mLogger) throws IOException {
            ByteArrayInputStream byteArrayInputStream;
            Process process = new ProcessBuilder("wget", "-O", "-", uRL.toString()).start();
            BufferedInputStream bufferedInputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(process.getInputStream(), 0x100000);
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(0x100000);
                int n = ((InputStream)bufferedInputStream).read();
                while (n >= 0) {
                    byteArrayOutputStream.write(n);
                    n = ((InputStream)bufferedInputStream).read();
                }
                byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
            }
            catch (Throwable throwable) {
                InputStreamUtils.attemptClose(bufferedInputStream);
                if (mLogger.isLoggable(MLevel.FINER)) {
                    BufferedReader bufferedReader = null;
                    try {
                        bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()), 0x100000);
                        StringWriter stringWriter = new StringWriter(0x100000);
                        int n4 = ((Reader)bufferedReader).read();
                        while (n4 >= 0) {
                            stringWriter.write(n4);
                            n4 = ((Reader)bufferedReader).read();
                        }
                        mLogger.log(MLevel.FINER, "wget error stream for '" + uRL + "':\n " + stringWriter.toString());
                    }
                    catch (Throwable throwable2) {
                        ReaderUtils.attemptClose(bufferedReader);
                        throw throwable2;
                    }
                    ReaderUtils.attemptClose(bufferedReader);
                }
                try {
                    int n5 = process.waitFor();
                    if (n5 == 0) throw throwable;
                    throw new IOException("wget process terminated abnormally [return code: " + n5 + "]");
                }
                catch (InterruptedException interruptedException) {
                    if (!mLogger.isLoggable(MLevel.FINER)) throw new IOException("Interrupted while waiting for wget to complete: " + interruptedException);
                    mLogger.log(MLevel.FINER, "InterruptedException while waiting for wget to complete.", interruptedException);
                    throw new IOException("Interrupted while waiting for wget to complete: " + interruptedException);
                }
            }
            InputStreamUtils.attemptClose(bufferedInputStream);
            if (mLogger.isLoggable(MLevel.FINER)) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(process.getErrorStream()), 0x100000);
                    StringWriter stringWriter = new StringWriter(0x100000);
                    int n2 = ((Reader)bufferedReader).read();
                    while (n2 >= 0) {
                        stringWriter.write(n2);
                        n2 = ((Reader)bufferedReader).read();
                    }
                    mLogger.log(MLevel.FINER, "wget error stream for '" + uRL + "':\n " + stringWriter.toString());
                }
                catch (Throwable throwable) {
                    ReaderUtils.attemptClose(bufferedReader);
                    throw throwable;
                }
                ReaderUtils.attemptClose(bufferedReader);
            }
            try {
                int n3 = process.waitFor();
                if (n3 == 0) return byteArrayInputStream;
                throw new IOException("wget process terminated abnormally [return code: " + n3 + "]");
            }
            catch (InterruptedException interruptedException) {
                if (!mLogger.isLoggable(MLevel.FINER)) throw new IOException("Interrupted while waiting for wget to complete: " + interruptedException);
                mLogger.log(MLevel.FINER, "InterruptedException while waiting for wget to complete.", interruptedException);
                throw new IOException("Interrupted while waiting for wget to complete: " + interruptedException);
            }
        }
    };

}


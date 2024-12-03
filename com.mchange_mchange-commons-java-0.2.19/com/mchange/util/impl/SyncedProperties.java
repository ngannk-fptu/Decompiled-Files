/*
 * Decompiled with CFR 0.152.
 */
package com.mchange.util.impl;

import com.mchange.io.InputStreamUtils;
import com.mchange.io.OutputStreamUtils;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.Properties;

public class SyncedProperties {
    private static final String[] SA_TEMPLATE = new String[0];
    private static final byte H_START_BYTE = 35;
    private static final byte[] H_LF_BYTES;
    private static final String ASCII = "8859_1";
    Properties props;
    byte[] headerBytes;
    File file;
    long last_mod = -1L;

    public SyncedProperties(File file, String string) throws IOException {
        this(file, SyncedProperties.makeHeaderBytes(string));
    }

    public SyncedProperties(File file, String[] stringArray) throws IOException {
        this(file, SyncedProperties.makeHeaderBytes(stringArray));
    }

    public SyncedProperties(File file) throws IOException {
        this(file, (byte[])null);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private SyncedProperties(File file, byte[] byArray) throws IOException {
        if (file.exists()) {
            if (!file.isFile()) {
                throw new IOException(file.getPath() + ": Properties file can't be a directory or special file!");
            }
            if (byArray == null) {
                BufferedReader bufferedReader = null;
                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                    LinkedList<String> linkedList = new LinkedList<String>();
                    String string = bufferedReader.readLine();
                    while (string.trim().equals("")) {
                        string = bufferedReader.readLine();
                    }
                    while (string.charAt(0) == '#') {
                        linkedList.add(string.substring(1).trim());
                    }
                    byArray = SyncedProperties.makeHeaderBytes(linkedList.toArray(SA_TEMPLATE));
                }
                finally {
                    if (bufferedReader != null) {
                        bufferedReader.close();
                    }
                }
            }
        }
        if (!file.canWrite()) {
            throw new IOException("Can't write to file " + file.getPath());
        }
        this.props = new Properties();
        this.headerBytes = byArray;
        this.file = file;
        this.ensureUpToDate();
    }

    public synchronized String getProperty(String string) throws IOException {
        this.ensureUpToDate();
        return this.props.getProperty(string);
    }

    public synchronized String getProperty(String string, String string2) throws IOException {
        String string3 = this.props.getProperty(string);
        return string3 == null ? string2 : string3;
    }

    public synchronized void put(String string, String string2) throws IOException {
        this.ensureUpToDate();
        this.props.put(string, string2);
        this.rewritePropsFile();
    }

    public synchronized void remove(String string) throws IOException {
        this.ensureUpToDate();
        this.props.remove(string);
        this.rewritePropsFile();
    }

    public synchronized void clear() throws IOException {
        this.ensureUpToDate();
        this.props.clear();
        this.rewritePropsFile();
    }

    public synchronized boolean contains(String string) throws IOException {
        this.ensureUpToDate();
        return this.props.contains(string);
    }

    public synchronized boolean containsKey(String string) throws IOException {
        this.ensureUpToDate();
        return this.props.containsKey(string);
    }

    public synchronized Enumeration elements() throws IOException {
        this.ensureUpToDate();
        return this.props.elements();
    }

    public synchronized Enumeration keys() throws IOException {
        this.ensureUpToDate();
        return this.props.keys();
    }

    public synchronized int size() throws IOException {
        this.ensureUpToDate();
        return this.props.size();
    }

    public synchronized boolean isEmpty() throws IOException {
        this.ensureUpToDate();
        return this.props.isEmpty();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private synchronized void ensureUpToDate() throws IOException {
        long l = this.file.lastModified();
        if (l > this.last_mod) {
            BufferedInputStream bufferedInputStream = null;
            try {
                bufferedInputStream = new BufferedInputStream(new FileInputStream(this.file));
                this.props.clear();
                this.props.load(bufferedInputStream);
                this.last_mod = l;
            }
            catch (Throwable throwable) {
                InputStreamUtils.attemptClose(bufferedInputStream);
                throw throwable;
            }
            InputStreamUtils.attemptClose(bufferedInputStream);
        }
    }

    private synchronized void rewritePropsFile() throws IOException {
        BufferedOutputStream bufferedOutputStream = null;
        try {
            bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(this.file));
            if (this.headerBytes != null) {
                ((OutputStream)bufferedOutputStream).write(this.headerBytes);
            }
            this.props.store(bufferedOutputStream, null);
            ((OutputStream)bufferedOutputStream).flush();
            this.last_mod = this.file.lastModified();
        }
        catch (Throwable throwable) {
            OutputStreamUtils.attemptClose(bufferedOutputStream);
            throw throwable;
        }
        OutputStreamUtils.attemptClose(bufferedOutputStream);
    }

    private static byte[] makeHeaderBytes(String[] stringArray) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            int n = stringArray.length;
            for (int i = 0; i < n; ++i) {
                byteArrayOutputStream.write(35);
                byteArrayOutputStream.write(stringArray[i].getBytes());
                byteArrayOutputStream.write(H_LF_BYTES);
            }
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException iOException) {
            throw new InternalError("IOException working with ByteArrayOutputStream?!?");
        }
    }

    private static byte[] makeHeaderBytes(String string) {
        try {
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            byteArrayOutputStream.write(35);
            byteArrayOutputStream.write(string.getBytes());
            byteArrayOutputStream.write(H_LF_BYTES);
            return byteArrayOutputStream.toByteArray();
        }
        catch (IOException iOException) {
            throw new InternalError("IOException working with ByteArrayOutputStream?!?");
        }
    }

    static {
        try {
            H_LF_BYTES = System.getProperty("line.separator", "\r\n").getBytes(ASCII);
        }
        catch (UnsupportedEncodingException unsupportedEncodingException) {
            throw new InternalError("Encoding 8859_1 not supported ?!?");
        }
    }
}


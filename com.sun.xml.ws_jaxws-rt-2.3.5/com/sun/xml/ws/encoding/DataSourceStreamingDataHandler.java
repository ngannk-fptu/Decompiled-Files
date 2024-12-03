/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 */
package com.sun.xml.ws.encoding;

import com.sun.xml.ws.developer.StreamingDataHandler;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataSource;

public class DataSourceStreamingDataHandler
extends StreamingDataHandler {
    public DataSourceStreamingDataHandler(DataSource ds) {
        super(ds);
    }

    public InputStream readOnce() throws IOException {
        return this.getInputStream();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void moveTo(File file) throws IOException {
        InputStream in = this.getInputStream();
        try (FileOutputStream os = new FileOutputStream(file);){
            int len;
            byte[] temp = new byte[8192];
            while ((len = in.read(temp)) != -1) {
                ((OutputStream)os).write(temp, 0, len);
            }
            in.close();
        }
    }

    public void close() throws IOException {
    }
}


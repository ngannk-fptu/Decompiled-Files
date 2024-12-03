/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataSource
 *  javax.activation.FileDataSource
 *  javax.mail.util.ByteArrayDataSource
 */
package org.apache.axiom.util.activation;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.util.ByteArrayDataSource;
import org.apache.axiom.ext.activation.SizeAwareDataSource;

public class DataSourceUtils {
    public static long getSize(DataSource ds) {
        if (ds instanceof SizeAwareDataSource) {
            return ((SizeAwareDataSource)ds).getSize();
        }
        if (ds instanceof ByteArrayDataSource) {
            try {
                return ((ByteArrayInputStream)ds.getInputStream()).available();
            }
            catch (IOException ex) {
                return -1L;
            }
        }
        if (ds instanceof FileDataSource) {
            return ((FileDataSource)ds).getFile().length();
        }
        return -1L;
    }
}


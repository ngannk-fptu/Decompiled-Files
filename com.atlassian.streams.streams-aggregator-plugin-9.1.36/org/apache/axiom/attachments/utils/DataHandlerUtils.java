/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.activation.DataHandler
 *  javax.activation.DataSource
 */
package org.apache.axiom.attachments.utils;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import org.apache.axiom.attachments.ByteArrayDataSource;
import org.apache.axiom.util.base64.Base64Utils;

public class DataHandlerUtils {
    public static Object getDataHandlerFromText(String value, String mimeType) {
        byte[] data = Base64Utils.decode(value);
        ByteArrayDataSource dataSource = mimeType != null ? new ByteArrayDataSource(data, mimeType) : new ByteArrayDataSource(data);
        return new DataHandler((DataSource)dataSource);
    }
}


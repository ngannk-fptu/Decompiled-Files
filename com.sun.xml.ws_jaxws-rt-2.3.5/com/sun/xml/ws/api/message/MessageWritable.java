/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.ws.soap.MTOMFeature
 */
package com.sun.xml.ws.api.message;

import com.oracle.webservices.api.message.ContentType;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.ws.soap.MTOMFeature;

public interface MessageWritable {
    public ContentType getContentType();

    public ContentType writeTo(OutputStream var1) throws IOException;

    public void setMTOMConfiguration(MTOMFeature var1);
}


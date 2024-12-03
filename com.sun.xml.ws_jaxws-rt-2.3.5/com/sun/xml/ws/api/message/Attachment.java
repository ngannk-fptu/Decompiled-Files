/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.activation.DataHandler
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;
import javax.xml.transform.Source;

public interface Attachment {
    @NotNull
    public String getContentId();

    public String getContentType();

    public byte[] asByteArray();

    public DataHandler asDataHandler();

    public Source asSource();

    public InputStream asInputStream();

    public void writeTo(OutputStream var1) throws IOException;

    public void writeTo(SOAPMessage var1) throws SOAPException;
}


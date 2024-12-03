/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPMessage
 */
package com.oracle.webservices.api.message;

import com.oracle.webservices.api.message.ContentType;
import com.oracle.webservices.api.message.DistributedPropertySet;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public interface MessageContext
extends DistributedPropertySet {
    public SOAPMessage getAsSOAPMessage() throws SOAPException;

    public SOAPMessage getSOAPMessage() throws SOAPException;

    public ContentType writeTo(OutputStream var1) throws IOException;

    public ContentType getContentType();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPEnvelope
 *  javax.xml.soap.SOAPException
 */
package com.sun.xml.messaging.saaj.soap;

import com.sun.xml.messaging.saaj.soap.StaxBridge;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.soap.SOAPEnvelope;
import javax.xml.soap.SOAPException;
import javax.xml.transform.Source;

public interface Envelope
extends SOAPEnvelope {
    public Source getContent();

    public void output(OutputStream var1) throws IOException;

    public void output(OutputStream var1, boolean var2) throws IOException;

    public void setStaxBridge(StaxBridge var1) throws SOAPException;

    public StaxBridge getStaxBridge() throws SOAPException;
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmlbeans.impl.soap;

import org.apache.xmlbeans.impl.soap.SOAPException;
import org.apache.xmlbeans.impl.soap.SOAPMessage;

public abstract class SOAPConnection {
    public abstract SOAPMessage call(SOAPMessage var1, Object var2) throws SOAPException;

    public abstract void close() throws SOAPException;
}


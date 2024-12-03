/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPMessage;

public abstract class SOAPConnection {
    public abstract SOAPMessage call(SOAPMessage var1, Object var2) throws SOAPException;

    public abstract void close() throws SOAPException;
}


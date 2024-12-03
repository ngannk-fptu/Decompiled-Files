/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.soap;

import javax.xml.soap.Name;
import javax.xml.soap.SOAPBody;
import javax.xml.soap.SOAPElement;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;

public interface SOAPEnvelope
extends SOAPElement {
    public Name createName(String var1, String var2, String var3) throws SOAPException;

    public Name createName(String var1) throws SOAPException;

    public SOAPHeader getHeader() throws SOAPException;

    public SOAPBody getBody() throws SOAPException;

    public SOAPHeader addHeader() throws SOAPException;

    public SOAPBody addBody() throws SOAPException;
}


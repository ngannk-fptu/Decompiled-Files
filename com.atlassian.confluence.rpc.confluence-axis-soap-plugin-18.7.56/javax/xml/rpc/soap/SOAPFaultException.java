/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.soap;

import javax.xml.namespace.QName;
import javax.xml.soap.Detail;

public class SOAPFaultException
extends RuntimeException {
    private QName faultcode;
    private String faultstring;
    private String faultactor;
    private Detail detail;

    public SOAPFaultException(QName faultcode, String faultstring, String faultactor, Detail detail) {
        super(faultstring);
        this.faultcode = faultcode;
        this.faultstring = faultstring;
        this.faultactor = faultactor;
        this.detail = detail;
    }

    public QName getFaultCode() {
        return this.faultcode;
    }

    public String getFaultString() {
        return this.faultstring;
    }

    public String getFaultActor() {
        return this.faultactor;
    }

    public Detail getDetail() {
        return this.detail;
    }
}


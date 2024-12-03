/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.remoting.soap;

import javax.xml.namespace.QName;
import org.springframework.remoting.RemoteInvocationFailureException;

public abstract class SoapFaultException
extends RemoteInvocationFailureException {
    protected SoapFaultException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public abstract String getFaultCode();

    public abstract QName getFaultCodeAsQName();

    public abstract String getFaultString();

    public abstract String getFaultActor();
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPFault
 *  javax.xml.ws.soap.SOAPFaultException
 */
package com.sun.xml.ws.fault;

import javax.xml.soap.SOAPFault;
import javax.xml.ws.soap.SOAPFaultException;

public class ServerSOAPFaultException
extends SOAPFaultException {
    public ServerSOAPFaultException(SOAPFault soapFault) {
        super(soapFault);
    }

    public String getMessage() {
        return "Client received SOAP Fault from server: " + super.getMessage() + " Please see the server log to find more detail regarding exact cause of the failure.";
    }
}


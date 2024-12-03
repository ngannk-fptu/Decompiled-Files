/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.handler.soap;

import javax.xml.rpc.handler.MessageContext;
import javax.xml.soap.SOAPMessage;

public interface SOAPMessageContext
extends MessageContext {
    public SOAPMessage getMessage();

    public void setMessage(SOAPMessage var1);

    public String[] getRoles();
}


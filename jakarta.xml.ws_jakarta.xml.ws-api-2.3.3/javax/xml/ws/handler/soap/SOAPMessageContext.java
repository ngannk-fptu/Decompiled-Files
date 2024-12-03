/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBContext
 *  javax.xml.soap.SOAPMessage
 */
package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPMessage;
import javax.xml.ws.handler.MessageContext;

public interface SOAPMessageContext
extends MessageContext {
    public SOAPMessage getMessage();

    public void setMessage(SOAPMessage var1);

    public Object[] getHeaders(QName var1, JAXBContext var2, boolean var3);

    public Set<String> getRoles();
}


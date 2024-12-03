/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.ws.handler.soap;

import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.ws.handler.Handler;
import javax.xml.ws.handler.soap.SOAPMessageContext;

public interface SOAPHandler<T extends SOAPMessageContext>
extends Handler<T> {
    public Set<QName> getHeaders();
}


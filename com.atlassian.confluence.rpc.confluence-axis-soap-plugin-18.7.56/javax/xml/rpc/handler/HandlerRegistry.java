/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.handler;

import java.io.Serializable;
import java.util.List;
import javax.xml.namespace.QName;

public interface HandlerRegistry
extends Serializable {
    public List getHandlerChain(QName var1);

    public void setHandlerChain(QName var1, List var2);
}


/*
 * Decompiled with CFR 0.152.
 */
package javax.xml.rpc.server;

import javax.xml.rpc.ServiceException;

public interface ServiceLifecycle {
    public void init(Object var1) throws ServiceException;

    public void destroy();
}


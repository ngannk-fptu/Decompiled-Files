/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.engine.jndi.spi;

import javax.naming.event.NamespaceChangeListener;
import org.hibernate.service.Service;

public interface JndiService
extends Service {
    public Object locate(String var1);

    public void bind(String var1, Object var2);

    public void unbind(String var1);

    public void addListener(String var1, NamespaceChangeListener var2);
}


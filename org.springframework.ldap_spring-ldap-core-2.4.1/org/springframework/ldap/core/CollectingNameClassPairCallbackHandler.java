/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.ldap.core;

import java.util.LinkedList;
import java.util.List;
import javax.naming.NameClassPair;
import javax.naming.NamingException;
import org.springframework.ldap.core.NameClassPairCallbackHandler;

public abstract class CollectingNameClassPairCallbackHandler<T>
implements NameClassPairCallbackHandler {
    private List<T> list = new LinkedList<T>();

    public List<T> getList() {
        return this.list;
    }

    @Override
    public final void handleNameClassPair(NameClassPair nameClassPair) throws NamingException {
        this.list.add(this.getObjectFromNameClassPair(nameClassPair));
    }

    public abstract T getObjectFromNameClassPair(NameClassPair var1) throws NamingException;
}


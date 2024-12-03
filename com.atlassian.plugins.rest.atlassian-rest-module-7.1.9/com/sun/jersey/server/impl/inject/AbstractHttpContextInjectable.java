/*
 * Decompiled with CFR 0.152.
 */
package com.sun.jersey.server.impl.inject;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.spi.inject.Injectable;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractHttpContextInjectable<T>
implements Injectable<T> {
    @Override
    public T getValue() {
        throw new IllegalStateException();
    }

    public abstract T getValue(HttpContext var1);

    public static List<AbstractHttpContextInjectable> transform(List<Injectable> l) {
        ArrayList<AbstractHttpContextInjectable> al = new ArrayList<AbstractHttpContextInjectable>(l.size());
        for (Injectable i : l) {
            al.add(AbstractHttpContextInjectable.transform(i));
        }
        return al;
    }

    public static AbstractHttpContextInjectable transform(final Injectable i) {
        if (i == null) {
            return null;
        }
        if (i instanceof AbstractHttpContextInjectable) {
            return (AbstractHttpContextInjectable)i;
        }
        return new AbstractHttpContextInjectable(){

            public Object getValue(HttpContext c) {
                return i.getValue();
            }
        };
    }
}


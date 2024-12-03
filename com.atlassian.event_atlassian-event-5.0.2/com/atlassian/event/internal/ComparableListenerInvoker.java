/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event.internal;

import com.atlassian.event.spi.ListenerInvoker;
import java.util.Objects;
import java.util.Set;

class ComparableListenerInvoker
implements Comparable<ComparableListenerInvoker>,
ListenerInvoker {
    private final ListenerInvoker delegate;
    private final int classHierarchyOrder;
    private final int registerOrder;

    ComparableListenerInvoker(ListenerInvoker listenerInvoker, int classHierarchyOrder, int registerOrder) {
        this.delegate = listenerInvoker;
        this.registerOrder = registerOrder;
        this.classHierarchyOrder = classHierarchyOrder;
    }

    @Override
    public int compareTo(ComparableListenerInvoker other) {
        int res = Integer.compare(this.getListenerOrder(), other.getListenerOrder());
        if (res != 0) {
            return res;
        }
        res = Integer.compare(this.getClassHierarchyOrder(), other.getClassHierarchyOrder());
        return res != 0 ? res : Integer.compare(this.getRegisterOrder(), other.getRegisterOrder());
    }

    private int getClassHierarchyOrder() {
        return this.classHierarchyOrder;
    }

    private int getRegisterOrder() {
        return this.registerOrder;
    }

    private int getListenerOrder() {
        return this.delegate.getOrder();
    }

    @Override
    public Set<Class<?>> getSupportedEventTypes() {
        return this.delegate.getSupportedEventTypes();
    }

    @Override
    public void invoke(Object event) {
        this.delegate.invoke(event);
    }

    @Override
    public boolean supportAsynchronousEvents() {
        return this.delegate.supportAsynchronousEvents();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof ComparableListenerInvoker)) {
            return false;
        }
        ComparableListenerInvoker that = (ComparableListenerInvoker)o;
        return this.delegate.equals(that.delegate);
    }

    public int hashCode() {
        return Objects.hash(this.delegate);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.event.internal;

import com.atlassian.event.internal.ListenerInvokerWithRegisterOrder;

final class ListenerInvokerWithClassHierarchyAndRegisterOrder {
    final ListenerInvokerWithRegisterOrder keyedListenerInvoker;
    final int classHierarchyOrder;

    ListenerInvokerWithClassHierarchyAndRegisterOrder(ListenerInvokerWithRegisterOrder keyedListenerInvoker, int classHierarchyOrder) {
        this.keyedListenerInvoker = keyedListenerInvoker;
        this.classHierarchyOrder = classHierarchyOrder;
    }

    ListenerInvokerWithRegisterOrder getListenerInvokerWithRegisterOrder() {
        return this.keyedListenerInvoker;
    }
}


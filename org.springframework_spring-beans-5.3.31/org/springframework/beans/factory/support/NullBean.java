/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.beans.factory.support;

import org.springframework.lang.Nullable;

final class NullBean {
    NullBean() {
    }

    public boolean equals(@Nullable Object obj) {
        return this == obj || obj == null;
    }

    public int hashCode() {
        return NullBean.class.hashCode();
    }

    public String toString() {
        return "null";
    }
}


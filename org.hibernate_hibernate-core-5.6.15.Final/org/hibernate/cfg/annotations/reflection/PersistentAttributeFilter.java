/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.annotations.common.reflection.Filter
 */
package org.hibernate.cfg.annotations.reflection;

import org.hibernate.annotations.common.reflection.Filter;

public class PersistentAttributeFilter
implements Filter {
    public static final PersistentAttributeFilter INSTANCE = new PersistentAttributeFilter();

    public boolean returnStatic() {
        return false;
    }

    public boolean returnTransient() {
        return false;
    }
}


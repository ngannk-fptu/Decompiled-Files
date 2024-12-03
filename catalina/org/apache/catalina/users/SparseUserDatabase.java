/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.users;

import org.apache.catalina.UserDatabase;

public abstract class SparseUserDatabase
implements UserDatabase {
    @Override
    public boolean isSparse() {
        return true;
    }
}


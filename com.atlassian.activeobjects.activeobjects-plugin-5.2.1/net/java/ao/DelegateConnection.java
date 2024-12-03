/*
 * Decompiled with CFR 0.152.
 */
package net.java.ao;

import java.sql.Connection;

public interface DelegateConnection
extends Connection {
    public void setCloseable(boolean var1);

    public boolean isCloseable();
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tomcat.dbcp.dbcp2.managed;

import org.apache.tomcat.dbcp.dbcp2.managed.TransactionContext;

public interface TransactionContextListener {
    public void afterCompletion(TransactionContext var1, boolean var2);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 */
package com.atlassian.sal.api.rdbms;

import com.atlassian.annotations.PublicApi;
import java.sql.Connection;

@PublicApi
public interface ConnectionCallback<A> {
    public A execute(Connection var1);
}


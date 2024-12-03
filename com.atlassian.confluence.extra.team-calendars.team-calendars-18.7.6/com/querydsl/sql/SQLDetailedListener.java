/*
 * Decompiled with CFR 0.152.
 */
package com.querydsl.sql;

import com.querydsl.sql.SQLListener;
import com.querydsl.sql.SQLListenerContext;

public interface SQLDetailedListener
extends SQLListener {
    public void start(SQLListenerContext var1);

    public void preRender(SQLListenerContext var1);

    public void rendered(SQLListenerContext var1);

    public void prePrepare(SQLListenerContext var1);

    public void prepared(SQLListenerContext var1);

    public void preExecute(SQLListenerContext var1);

    public void executed(SQLListenerContext var1);

    public void exception(SQLListenerContext var1);

    public void end(SQLListenerContext var1);
}


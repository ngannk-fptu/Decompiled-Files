/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.log;

import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;
import com.amazonaws.log.JulLog;
import java.util.logging.Logger;

public final class JulLogFactory
extends InternalLogFactory {
    @Override
    protected InternalLogApi doGetLog(Class<?> clazz) {
        return new JulLog(Logger.getLogger(clazz.getName()));
    }

    @Override
    protected InternalLogApi doGetLog(String name) {
        return new JulLog(Logger.getLogger(name));
    }
}


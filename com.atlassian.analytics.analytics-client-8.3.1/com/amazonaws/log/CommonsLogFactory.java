/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package com.amazonaws.log;

import com.amazonaws.log.CommonsLog;
import com.amazonaws.log.InternalLogApi;
import com.amazonaws.log.InternalLogFactory;
import org.apache.commons.logging.LogFactory;

public final class CommonsLogFactory
extends InternalLogFactory {
    @Override
    protected InternalLogApi doGetLog(Class<?> clazz) {
        return new CommonsLog(LogFactory.getLog(clazz));
    }

    @Override
    protected InternalLogApi doGetLog(String name) {
        return new CommonsLog(LogFactory.getLog((String)name));
    }
}


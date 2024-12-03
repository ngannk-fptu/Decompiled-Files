/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.logging.LogFactory
 */
package org.aspectj.weaver.tools;

import org.apache.commons.logging.LogFactory;
import org.aspectj.weaver.tools.CommonsTrace;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class CommonsTraceFactory
extends TraceFactory {
    private LogFactory logFactory = LogFactory.getFactory();

    @Override
    public Trace getTrace(Class clazz) {
        return new CommonsTrace(clazz);
    }
}


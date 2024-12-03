/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.weaver.tools;

import org.aspectj.weaver.tools.Jdk14Trace;
import org.aspectj.weaver.tools.Trace;
import org.aspectj.weaver.tools.TraceFactory;

public class Jdk14TraceFactory
extends TraceFactory {
    @Override
    public Trace getTrace(Class clazz) {
        return new Jdk14Trace(clazz);
    }
}


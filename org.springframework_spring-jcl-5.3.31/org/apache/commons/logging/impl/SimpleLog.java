/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.logging.impl;

import org.apache.commons.logging.impl.NoOpLog;

@Deprecated
public class SimpleLog
extends NoOpLog {
    public SimpleLog(String name) {
        super(name);
        System.out.println(SimpleLog.class.getName() + " is deprecated and equivalent to NoOpLog in spring-jcl. Use a standard LogFactory.getLog(Class/String) call instead.");
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.bytecode;

import org.jboss.logging.Logger;

public interface BytecodeLogging {
    public static final String NAME = "org.hibernate.orm.bytecode";
    public static final Logger LOGGER = Logger.getLogger((String)"org.hibernate.orm.bytecode");
    public static final boolean TRACE_ENABLED = LOGGER.isTraceEnabled();
    public static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

    public static String subLoggerName(String subName) {
        return "org.hibernate.orm.bytecode." + subName;
    }
}


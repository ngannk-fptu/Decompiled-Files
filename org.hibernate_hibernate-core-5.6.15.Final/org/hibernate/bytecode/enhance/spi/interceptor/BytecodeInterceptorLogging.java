/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.bytecode.enhance.spi.interceptor;

import org.hibernate.bytecode.BytecodeLogging;
import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=90005901, max=90006000)
public interface BytecodeInterceptorLogging
extends BasicLogger {
    public static final String SUB_NAME = "interceptor";
    public static final String NAME = BytecodeLogging.subLoggerName("interceptor");
    public static final Logger LOGGER = Logger.getLogger((String)NAME);
    public static final BytecodeInterceptorLogging MESSAGE_LOGGER = (BytecodeInterceptorLogging)Logger.getMessageLogger(BytecodeInterceptorLogging.class, (String)NAME);
    public static final boolean TRACE_ENABLED = LOGGER.isTraceEnabled();
    public static final boolean DEBUG_ENABLED = LOGGER.isDebugEnabled();

    @LogMessage(level=Logger.Level.WARN)
    @Message(id=90005901, value="`%s#%s` was mapped with explicit lazy-group (`%s`).  Hibernate will ignore the lazy-group - this is generally not a good idea for to-one associations as it would lead to 2 separate SQL selects to initialize the association.  This is expected to be improved in future versions of Hibernate")
    public void lazyGroupIgnoredForToOne(String var1, String var2, String var3);
}


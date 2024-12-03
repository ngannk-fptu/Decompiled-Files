/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.BasicLogger
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.Cause
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 */
package org.hibernate.annotations.common.util.impl;

import org.jboss.logging.BasicLogger;
import org.jboss.logging.Logger;
import org.jboss.logging.annotations.Cause;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;

@MessageLogger(projectCode="HCANN")
public interface Log
extends BasicLogger {
    @LogMessage(level=Logger.Level.INFO)
    @Message(id=1, value="Hibernate Commons Annotations {%1$s}")
    public void version(String var1);

    @LogMessage(level=Logger.Level.ERROR)
    @Message(id=2, value="An assertion failure occurred (this may indicate a bug in Hibernate)")
    public void assertionFailure(@Cause Throwable var1);
}


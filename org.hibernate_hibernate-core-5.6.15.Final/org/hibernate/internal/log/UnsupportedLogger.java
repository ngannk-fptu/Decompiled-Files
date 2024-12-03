/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger$Level
 *  org.jboss.logging.annotations.LogMessage
 *  org.jboss.logging.annotations.Message
 *  org.jboss.logging.annotations.MessageLogger
 *  org.jboss.logging.annotations.ValidIdRange
 */
package org.hibernate.internal.log;

import org.jboss.logging.Logger;
import org.jboss.logging.annotations.LogMessage;
import org.jboss.logging.annotations.Message;
import org.jboss.logging.annotations.MessageLogger;
import org.jboss.logging.annotations.ValidIdRange;

@MessageLogger(projectCode="HHH")
@ValidIdRange(min=90002001, max=90003000)
public interface UnsupportedLogger {
    @LogMessage(level=Logger.Level.WARN)
    @Message(value="Global configuration option 'hibernate.bytecode.enforce_legacy_proxy_classnames' was enabled. Generated proxies will use backwards compatible classnames. This option is unsupported and will be removed.", id=90002001)
    public void usingLegacyClassnamesForProxies();
}


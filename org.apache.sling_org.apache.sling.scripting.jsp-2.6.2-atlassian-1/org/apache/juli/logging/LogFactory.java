/*
 * Decompiled with CFR 0.152.
 */
package org.apache.juli.logging;

import org.apache.juli.logging.Log;
import org.apache.juli.logging.LogConfigurationException;
import org.apache.juli.logging.Slf4jLog;

public class LogFactory {
    public static Log getLog(Class<?> clazz) throws LogConfigurationException {
        return LogFactory.getLog(clazz.getName());
    }

    public static Log getLog(String name) throws LogConfigurationException {
        return new Slf4jLog(name);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.jboss.logging.Logger
 */
package org.hibernate.annotations.common.util.impl;

import org.hibernate.annotations.common.util.impl.Log;
import org.jboss.logging.Logger;

public class LoggerFactory {
    public static Log make(String category) {
        return (Log)Logger.getMessageLogger(Log.class, (String)category);
    }

    public static Logger logger(Class caller) {
        return Logger.getLogger((String)caller.getName());
    }
}


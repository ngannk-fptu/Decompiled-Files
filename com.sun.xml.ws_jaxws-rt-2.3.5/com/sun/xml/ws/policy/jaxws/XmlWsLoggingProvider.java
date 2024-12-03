/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.xml.ws.policy.spi.LoggingProvider
 */
package com.sun.xml.ws.policy.jaxws;

import com.sun.xml.ws.policy.spi.LoggingProvider;

public class XmlWsLoggingProvider
implements LoggingProvider {
    public String getLoggingSubsystemName() {
        return "com.sun.xml.ws";
    }
}


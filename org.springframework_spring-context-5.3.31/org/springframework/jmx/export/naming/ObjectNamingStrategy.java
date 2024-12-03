/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.lang.Nullable
 */
package org.springframework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import org.springframework.lang.Nullable;

@FunctionalInterface
public interface ObjectNamingStrategy {
    public ObjectName getObjectName(Object var1, @Nullable String var2) throws MalformedObjectNameException;
}


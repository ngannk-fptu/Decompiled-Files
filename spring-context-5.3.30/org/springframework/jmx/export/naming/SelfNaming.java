/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.jmx.export.naming;

import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;

public interface SelfNaming {
    public ObjectName getObjectName() throws MalformedObjectNameException;
}


/*
 * Decompiled with CFR 0.152.
 */
package org.glassfish.external.amx;

import javax.management.ObjectName;
import javax.management.remote.JMXServiceURL;
import org.glassfish.external.arc.Stability;
import org.glassfish.external.arc.Taxonomy;

@Taxonomy(stability=Stability.UNCOMMITTED)
public interface BootAMXMBean {
    public static final String BOOT_AMX_OPERATION_NAME = "bootAMX";

    public ObjectName bootAMX();

    public JMXServiceURL[] getJMXServiceURLs();
}


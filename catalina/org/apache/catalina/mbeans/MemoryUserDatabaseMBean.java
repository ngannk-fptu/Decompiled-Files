/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.modeler.ManagedBean
 */
package org.apache.catalina.mbeans;

import org.apache.catalina.mbeans.SparseUserDatabaseMBean;
import org.apache.tomcat.util.modeler.ManagedBean;

public class MemoryUserDatabaseMBean
extends SparseUserDatabaseMBean {
    protected final ManagedBean managed;

    public MemoryUserDatabaseMBean() {
        this.managed = this.registry.findManagedBean("MemoryUserDatabase");
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.modeler.BaseModelMBean
 *  org.apache.tomcat.util.modeler.ManagedBean
 *  org.apache.tomcat.util.modeler.Registry
 */
package org.apache.catalina.mbeans;

import org.apache.catalina.mbeans.MBeanUtils;
import org.apache.tomcat.util.modeler.BaseModelMBean;
import org.apache.tomcat.util.modeler.ManagedBean;
import org.apache.tomcat.util.modeler.Registry;

public class RoleMBean
extends BaseModelMBean {
    protected final Registry registry = MBeanUtils.createRegistry();
    protected final ManagedBean managed = this.registry.findManagedBean("Role");
}


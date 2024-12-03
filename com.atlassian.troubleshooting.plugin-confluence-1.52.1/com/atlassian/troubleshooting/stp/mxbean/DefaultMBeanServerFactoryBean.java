/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.jmx.support.MBeanServerFactoryBean
 */
package com.atlassian.troubleshooting.stp.mxbean;

import org.springframework.jmx.support.MBeanServerFactoryBean;

public class DefaultMBeanServerFactoryBean
extends MBeanServerFactoryBean {
    public DefaultMBeanServerFactoryBean() {
        this.setLocateExistingServerIfPossible(true);
    }
}


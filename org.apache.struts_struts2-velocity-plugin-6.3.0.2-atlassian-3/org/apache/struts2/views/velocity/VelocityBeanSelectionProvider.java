/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.xwork2.config.ConfigurationException
 *  com.opensymphony.xwork2.inject.ContainerBuilder
 *  com.opensymphony.xwork2.util.location.LocatableProperties
 *  org.apache.struts2.config.AbstractBeanSelectionProvider
 */
package org.apache.struts2.views.velocity;

import com.opensymphony.xwork2.config.ConfigurationException;
import com.opensymphony.xwork2.inject.ContainerBuilder;
import com.opensymphony.xwork2.util.location.LocatableProperties;
import java.util.Properties;
import org.apache.struts2.config.AbstractBeanSelectionProvider;
import org.apache.struts2.views.velocity.VelocityManager;

public class VelocityBeanSelectionProvider
extends AbstractBeanSelectionProvider {
    public void register(ContainerBuilder builder, LocatableProperties props) throws ConfigurationException {
        this.alias(VelocityManager.class, "struts.velocity.manager.classname", builder, (Properties)props);
    }
}


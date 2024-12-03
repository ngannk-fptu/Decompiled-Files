/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  net.sf.hibernate.cfg.Configuration
 */
package com.opensymphony.module.propertyset.hibernate;

import com.opensymphony.module.propertyset.hibernate.HibernatePropertySetDAO;
import java.util.Map;
import net.sf.hibernate.cfg.Configuration;

public interface HibernateConfigurationProvider {
    public Configuration getConfiguration();

    public HibernatePropertySetDAO getPropertySetDAO();

    public void setupConfiguration(Map var1);
}


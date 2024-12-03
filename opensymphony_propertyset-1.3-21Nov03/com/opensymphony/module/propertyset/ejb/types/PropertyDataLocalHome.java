/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.FinderException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyDataLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface PropertyDataLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyData";
    public static final String JNDI_NAME = "PropertyData";

    public PropertyDataLocal create(int var1, long var2) throws CreateException;

    public PropertyDataLocal findByPrimaryKey(Long var1) throws FinderException;
}


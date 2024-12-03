/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.FinderException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyStringLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface PropertyStringLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyString";
    public static final String JNDI_NAME = "PropertyString";

    public PropertyStringLocal create(int var1, long var2) throws CreateException;

    public PropertyStringLocal findByPrimaryKey(Long var1) throws FinderException;
}


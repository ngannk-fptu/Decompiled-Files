/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.FinderException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyDecimalLocal;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface PropertyDecimalLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyDecimal";
    public static final String JNDI_NAME = "PropertyDecimal";

    public PropertyDecimalLocal create(int var1, long var2) throws CreateException;

    public PropertyDecimalLocal findByPrimaryKey(Long var1) throws FinderException;
}


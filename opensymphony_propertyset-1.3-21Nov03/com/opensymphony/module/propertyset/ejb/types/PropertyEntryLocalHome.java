/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.FinderException
 */
package com.opensymphony.module.propertyset.ejb.types;

import com.opensymphony.module.propertyset.ejb.types.PropertyEntryLocal;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface PropertyEntryLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/PropertyEntry";
    public static final String JNDI_NAME = "PropertyEntry";

    public PropertyEntryLocal create(String var1, long var2, int var4, String var5) throws CreateException;

    public Collection findByNameAndId(String var1, long var2) throws FinderException;

    public PropertyEntryLocal findByEntity(String var1, long var2, String var4) throws FinderException;

    public PropertyEntryLocal findByPrimaryKey(Long var1) throws FinderException;
}


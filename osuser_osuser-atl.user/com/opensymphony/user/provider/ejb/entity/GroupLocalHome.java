/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.FinderException
 */
package com.opensymphony.user.provider.ejb.entity;

import com.opensymphony.user.provider.ejb.entity.GroupLocal;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface GroupLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Group";
    public static final String JNDI_NAME = "Group";

    public GroupLocal create(String var1) throws CreateException;

    public Collection findAll() throws FinderException;

    public GroupLocal findByName(String var1) throws FinderException;

    public GroupLocal findByPrimaryKey(Long var1) throws FinderException;
}


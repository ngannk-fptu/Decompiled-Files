/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBLocalHome
 *  javax.ejb.FinderException
 */
package com.opensymphony.module.sequence;

import com.opensymphony.module.sequence.SequenceLocal;
import java.util.Collection;
import javax.ejb.CreateException;
import javax.ejb.EJBLocalHome;
import javax.ejb.FinderException;

public interface SequenceLocalHome
extends EJBLocalHome {
    public static final String COMP_NAME = "java:comp/env/ejb/Sequence";
    public static final String JNDI_NAME = "Sequence";

    public SequenceLocal create(String var1) throws CreateException;

    public Collection findAll() throws FinderException;

    public SequenceLocal findByPrimaryKey(String var1) throws FinderException;
}


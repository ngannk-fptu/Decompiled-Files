/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBHome
 */
package com.opensymphony.module.sequence;

import com.opensymphony.module.sequence.SequenceGenerator;
import java.rmi.RemoteException;
import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface SequenceGeneratorHome
extends EJBHome {
    public SequenceGenerator create() throws CreateException, RemoteException;
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.opensymphony.module.sequence.SequenceGeneratorHome
 *  com.opensymphony.util.EJBUtils
 *  com.opensymphony.util.GUID
 *  javax.ejb.CreateException
 *  javax.ejb.EntityContext
 */
package com.opensymphony.ejb;

import com.opensymphony.module.propertyset.PropertySet;
import com.opensymphony.module.propertyset.PropertySetManager;
import com.opensymphony.module.sequence.SequenceGeneratorHome;
import com.opensymphony.util.EJBUtils;
import com.opensymphony.util.GUID;
import java.rmi.RemoteException;
import java.util.HashMap;
import javax.ejb.CreateException;
import javax.ejb.EntityContext;
import javax.naming.NameNotFoundException;
import javax.naming.NamingException;

public class AbstractEntityAdapter {
    protected EntityContext context;
    protected String sequenceName;
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$com$opensymphony$module$sequence$SequenceGeneratorHome;

    protected void setContext(EntityContext context) {
        this.context = context;
        try {
            this.sequenceName = (String)EJBUtils.lookup((String)"sequenceName", (Class)(class$java$lang$String == null ? (class$java$lang$String = AbstractEntityAdapter.class$("java.lang.String")) : class$java$lang$String));
        }
        catch (Exception e) {
            this.sequenceName = "";
        }
    }

    protected String generateGUID() {
        return GUID.generateGUID();
    }

    protected PropertySet locatePropertySet(long id) throws RemoteException {
        HashMap<String, Object> args = new HashMap<String, Object>(2);
        args.put("entityId", new Long(id));
        args.put("entityName", this.sequenceName);
        return PropertySetManager.getInstance("ejb", args);
    }

    protected int nextId() throws CreateException, RemoteException {
        return this.nextInt();
    }

    protected int nextInt() throws CreateException, RemoteException {
        try {
            return (int)this.nextLong();
        }
        catch (ClassCastException e) {
            throw new CreateException("Cannot generate id: Sequence cannot be downcasted to long.");
        }
        catch (NullPointerException e) {
            throw new CreateException("Cannot generate id: Sequence returning null.");
        }
    }

    protected long nextLong() throws CreateException, RemoteException {
        try {
            SequenceGeneratorHome sgHome;
            try {
                if (this.sequenceName == null) {
                    this.sequenceName = (String)EJBUtils.lookup((String)"sequenceName", (Class)(class$java$lang$String == null ? (class$java$lang$String = AbstractEntityAdapter.class$("java.lang.String")) : class$java$lang$String));
                }
                sgHome = (SequenceGeneratorHome)EJBUtils.lookup((String)"ejb/SequenceGenerator", (Class)(class$com$opensymphony$module$sequence$SequenceGeneratorHome == null ? (class$com$opensymphony$module$sequence$SequenceGeneratorHome = AbstractEntityAdapter.class$("com.opensymphony.module.sequence.SequenceGeneratorHome")) : class$com$opensymphony$module$sequence$SequenceGeneratorHome));
            }
            catch (NameNotFoundException e) {
                sgHome = (SequenceGeneratorHome)EJBUtils.lookup((String)"SequenceGenerator", (Class)(class$com$opensymphony$module$sequence$SequenceGeneratorHome == null ? (class$com$opensymphony$module$sequence$SequenceGeneratorHome = AbstractEntityAdapter.class$("com.opensymphony.module.sequence.SequenceGeneratorHome")) : class$com$opensymphony$module$sequence$SequenceGeneratorHome));
            }
            return sgHome.create().getCount(this.sequenceName);
        }
        catch (NamingException e) {
            throw new CreateException("Cannot generate id: " + e.toString());
        }
    }

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


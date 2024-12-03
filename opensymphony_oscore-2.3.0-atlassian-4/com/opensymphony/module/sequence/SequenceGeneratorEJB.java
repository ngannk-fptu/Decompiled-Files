/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ejb.CreateException
 *  javax.ejb.EJBException
 *  javax.ejb.FinderException
 *  javax.ejb.SessionBean
 *  javax.ejb.SessionContext
 *  javax.rmi.PortableRemoteObject
 */
package com.opensymphony.module.sequence;

import com.opensymphony.module.sequence.SequenceLocal;
import com.opensymphony.module.sequence.SequenceLocalHome;
import java.util.HashMap;
import java.util.Map;
import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.FinderException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

public class SequenceGeneratorEJB
implements SessionBean {
    private Map sequenceStore;
    private SequenceLocalHome sequenceHome;
    private SessionContext context;
    private int increment;
    private int retry;
    private int retryPause;

    public long getCount(String sequenceName) {
        try {
            if (sequenceName == null) {
                sequenceName = "";
            }
            MemorySequence memorySequence = this.getMemorySequence(sequenceName);
            if (memorySequence.last % (long)this.increment == 0L) {
                this.getNextHighCount(memorySequence);
            }
            return memorySequence.last++;
        }
        catch (Exception e) {
            throw new EJBException(e);
        }
    }

    public void setSessionContext(SessionContext context) {
        this.context = context;
        try {
            InitialContext ctx = new InitialContext();
            this.sequenceHome = (SequenceLocalHome)PortableRemoteObject.narrow((Object)ctx.lookup("java:comp/env/ejb/Sequence"), SequenceLocalHome.class);
        }
        catch (NamingException e) {
            throw new EJBException((Exception)e);
        }
    }

    public void ejbActivate() {
    }

    public void ejbCreate() throws CreateException {
        this.increment = this.getIntEnv("increment", 10);
        this.retry = this.getIntEnv("retry", 5);
        this.retryPause = this.getIntEnv("retryPause", 100);
        this.sequenceStore = new HashMap();
    }

    public void ejbPassivate() {
    }

    public void ejbRemove() {
    }

    private int getIntEnv(String envName, int defaultValue) {
        try {
            return (Integer)PortableRemoteObject.narrow((Object)new InitialContext().lookup(envName), Integer.class);
        }
        catch (Exception e) {
            return defaultValue;
        }
    }

    private MemorySequence getMemorySequence(String sequenceName) throws CreateException {
        MemorySequence memorySequence = (MemorySequence)this.sequenceStore.get(sequenceName);
        if (memorySequence == null) {
            memorySequence = new MemorySequence();
            try {
                memorySequence.sequence = this.sequenceHome.findByPrimaryKey(sequenceName);
            }
            catch (FinderException e) {
                memorySequence.sequence = this.sequenceHome.create(sequenceName);
            }
            this.sequenceStore.put(sequenceName, memorySequence);
        }
        return memorySequence;
    }

    private void getNextHighCount(MemorySequence memorySequence) {
        int retryAttempt = 0;
        while (true) {
            try {
                memorySequence.last = memorySequence.sequence.getCount(this.increment);
            }
            catch (Exception tre) {
                if (retryAttempt < this.retry) {
                    try {
                        Thread.sleep(this.retryPause);
                    }
                    catch (InterruptedException interruptedException) {}
                } else {
                    throw new EJBException(tre);
                }
                ++retryAttempt;
                continue;
            }
            break;
        }
    }

    private class MemorySequence {
        SequenceLocal sequence;
        long last;

        private MemorySequence() {
        }
    }
}


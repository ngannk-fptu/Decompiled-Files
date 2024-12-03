/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.io.Serializable;
import java.util.Date;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.TimerHandle;

public interface Timer {
    public void cancel() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    public long getTimeRemaining() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    public Date getNextTimeout() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    public Serializable getInfo() throws IllegalStateException, NoSuchObjectLocalException, EJBException;

    public TimerHandle getHandle() throws IllegalStateException, NoSuchObjectLocalException, EJBException;
}


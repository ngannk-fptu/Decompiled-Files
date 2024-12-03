/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.ejb.EJBException;
import javax.ejb.Timer;

public interface TimerService {
    public Timer createTimer(long var1, Serializable var3) throws IllegalArgumentException, IllegalStateException, EJBException;

    public Timer createTimer(long var1, long var3, Serializable var5) throws IllegalArgumentException, IllegalStateException, EJBException;

    public Timer createTimer(Date var1, Serializable var2) throws IllegalArgumentException, IllegalStateException, EJBException;

    public Timer createTimer(Date var1, long var2, Serializable var4) throws IllegalArgumentException, IllegalStateException, EJBException;

    public Collection getTimers() throws IllegalStateException, EJBException;
}


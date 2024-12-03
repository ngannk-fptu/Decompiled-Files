/*
 * Decompiled with CFR 0.152.
 */
package javax.ejb;

import java.io.Serializable;
import javax.ejb.EJBException;
import javax.ejb.NoSuchObjectLocalException;
import javax.ejb.Timer;

public interface TimerHandle
extends Serializable {
    public Timer getTimer() throws IllegalStateException, NoSuchObjectLocalException, EJBException;
}


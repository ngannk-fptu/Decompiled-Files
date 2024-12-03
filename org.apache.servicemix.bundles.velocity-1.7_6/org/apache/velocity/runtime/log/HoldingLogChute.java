/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import java.util.Iterator;
import java.util.Vector;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

class HoldingLogChute
implements LogChute {
    private Vector pendingMessages = new Vector();
    private volatile boolean transferring = false;

    HoldingLogChute() {
    }

    public void init(RuntimeServices rs) throws Exception {
    }

    public synchronized void log(int level, String message) {
        if (!this.transferring) {
            Object[] data = new Object[]{new Integer(level), message};
            this.pendingMessages.addElement(data);
        }
    }

    public synchronized void log(int level, String message, Throwable t) {
        if (!this.transferring) {
            Object[] data = new Object[]{new Integer(level), message, t};
            this.pendingMessages.addElement(data);
        }
    }

    public boolean isLevelEnabled(int level) {
        return true;
    }

    public synchronized void transferTo(LogChute newChute) {
        if (!this.transferring && !this.pendingMessages.isEmpty()) {
            this.transferring = true;
            Iterator i = this.pendingMessages.iterator();
            while (i.hasNext()) {
                Object[] data = (Object[])i.next();
                int level = (Integer)data[0];
                String message = (String)data[1];
                if (data.length == 2) {
                    newChute.log(level, message);
                    continue;
                }
                newChute.log(level, message, (Throwable)data[2]);
            }
        }
    }
}


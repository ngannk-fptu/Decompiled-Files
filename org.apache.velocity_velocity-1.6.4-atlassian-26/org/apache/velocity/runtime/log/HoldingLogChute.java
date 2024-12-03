/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.runtime.log;

import java.util.Vector;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.log.LogChute;

class HoldingLogChute
implements LogChute {
    private Vector pendingMessages = new Vector();
    private volatile boolean transferring = false;

    HoldingLogChute() {
    }

    @Override
    public void init(RuntimeServices rs) throws Exception {
    }

    @Override
    public synchronized void log(int level, String message) {
        if (!this.transferring) {
            Object[] data = new Object[]{new Integer(level), message};
            this.pendingMessages.addElement(data);
        }
    }

    @Override
    public synchronized void log(int level, String message, Throwable t) {
        if (!this.transferring) {
            Object[] data = new Object[]{new Integer(level), message, t};
            this.pendingMessages.addElement(data);
        }
    }

    @Override
    public boolean isLevelEnabled(int level) {
        return true;
    }

    public synchronized void transferTo(LogChute newChute) {
        if (!this.transferring && !this.pendingMessages.isEmpty()) {
            this.transferring = true;
            for (Object[] data : this.pendingMessages) {
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


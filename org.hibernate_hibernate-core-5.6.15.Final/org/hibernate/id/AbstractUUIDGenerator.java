/*
 * Decompiled with CFR 0.152.
 */
package org.hibernate.id;

import java.net.InetAddress;
import org.hibernate.id.IdentifierGenerator;
import org.hibernate.internal.util.BytesHelper;

public abstract class AbstractUUIDGenerator
implements IdentifierGenerator {
    private static final int IP;
    private static short counter;
    private static final int JVM;

    protected int getJVM() {
        return JVM;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected short getCount() {
        Class<AbstractUUIDGenerator> clazz = AbstractUUIDGenerator.class;
        synchronized (AbstractUUIDGenerator.class) {
            if (counter < 0) {
                counter = 0;
            }
            short s = counter;
            counter = (short)(s + 1);
            // ** MonitorExit[var1_1] (shouldn't be in output)
            return s;
        }
    }

    protected int getIP() {
        return IP;
    }

    protected short getHiTime() {
        return (short)(System.currentTimeMillis() >>> 32);
    }

    protected int getLoTime() {
        return (int)System.currentTimeMillis();
    }

    static {
        int ipadd;
        try {
            ipadd = BytesHelper.toInt(InetAddress.getLocalHost().getAddress());
        }
        catch (Exception e) {
            ipadd = 0;
        }
        IP = ipadd;
        counter = 0;
        JVM = (int)(System.currentTimeMillis() >>> 8);
    }
}


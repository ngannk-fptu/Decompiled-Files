/*
 * Decompiled with CFR 0.152.
 */
package org.apache.catalina.tribes;

import java.io.Serializable;

public interface Member
extends Serializable {
    public static final byte[] SHUTDOWN_PAYLOAD = new byte[]{66, 65, 66, 89, 45, 65, 76, 69, 88};

    public String getName();

    public byte[] getHost();

    public int getPort();

    public int getSecurePort();

    public int getUdpPort();

    public long getMemberAliveTime();

    public void setMemberAliveTime(long var1);

    public boolean isReady();

    public boolean isSuspect();

    public boolean isFailing();

    public byte[] getUniqueId();

    public byte[] getPayload();

    public void setPayload(byte[] var1);

    public byte[] getCommand();

    public void setCommand(byte[] var1);

    public byte[] getDomain();

    public byte[] getData(boolean var1);

    public byte[] getData(boolean var1, boolean var2);

    public int getDataLength();

    public boolean isLocal();

    public void setLocal(boolean var1);
}


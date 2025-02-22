/*
 * Decompiled with CFR 0.152.
 */
package io.netty.channel.epoll;

import java.net.SocketAddress;

public final class VSockAddress
extends SocketAddress {
    private static final long serialVersionUID = 8600894096347158429L;
    public static final int VMADDR_CID_ANY = -1;
    public static final int VMADDR_CID_HYPERVISOR = 0;
    public static final int VMADDR_CID_LOCAL = 1;
    public static final int VMADDR_CID_HOST = 2;
    public static final int VMADDR_PORT_ANY = -1;
    private final int cid;
    private final int port;

    public VSockAddress(int cid, int port) {
        this.cid = cid;
        this.port = port;
    }

    public int getCid() {
        return this.cid;
    }

    public int getPort() {
        return this.port;
    }

    public String toString() {
        return "VSockAddress{cid=" + this.cid + ", port=" + this.port + '}';
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VSockAddress)) {
            return false;
        }
        VSockAddress that = (VSockAddress)o;
        return this.cid == that.cid && this.port == that.port;
    }

    public int hashCode() {
        int result = this.cid;
        result = 31 * result + this.port;
        return result;
    }
}


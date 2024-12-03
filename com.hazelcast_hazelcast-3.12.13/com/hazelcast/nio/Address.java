/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.nio;

import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.AddressUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;

@BinaryInterface
public final class Address
implements IdentifiedDataSerializable {
    private static final byte IPV4 = 4;
    private static final byte IPV6 = 6;
    private int port = -1;
    private String host;
    private byte type;
    private String scopeId;
    private boolean hostSet;

    public Address() {
    }

    public Address(String host, int port) throws UnknownHostException {
        this(host, InetAddress.getByName(host), port);
    }

    public Address(InetAddress inetAddress, int port) {
        this(null, inetAddress, port);
        this.hostSet = false;
    }

    public Address(InetSocketAddress inetSocketAddress) {
        this(Address.resolve(inetSocketAddress), inetSocketAddress.getPort());
    }

    public Address(String hostname, InetAddress inetAddress, int port) {
        Preconditions.checkNotNull(inetAddress, "inetAddress can't be null");
        this.type = (byte)(inetAddress instanceof Inet4Address ? 4 : 6);
        String[] addressArgs = inetAddress.getHostAddress().split("\\%");
        String string = this.host = hostname != null ? hostname : addressArgs[0];
        if (addressArgs.length == 2) {
            this.scopeId = addressArgs[1];
        }
        this.port = port;
        this.hostSet = !AddressUtil.isIpAddress(this.host);
    }

    public Address(Address address) {
        this.host = address.host;
        this.port = address.port;
        this.type = address.type;
        this.scopeId = address.scopeId;
        this.hostSet = address.hostSet;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public InetAddress getInetAddress() throws UnknownHostException {
        return InetAddress.getByName(this.getScopedHost());
    }

    public InetSocketAddress getInetSocketAddress() throws UnknownHostException {
        return new InetSocketAddress(this.getInetAddress(), this.port);
    }

    public boolean isIPv4() {
        return this.type == 4;
    }

    public boolean isIPv6() {
        return this.type == 6;
    }

    public String getScopeId() {
        return this.isIPv6() ? this.scopeId : null;
    }

    public void setScopeId(String scopeId) {
        if (this.isIPv6()) {
            this.scopeId = scopeId;
        }
    }

    public String getScopedHost() {
        return this.isIPv4() || this.hostSet || this.scopeId == null ? this.getHost() : this.getHost() + '%' + this.scopeId;
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 1;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.port);
        out.write(this.type);
        if (this.host != null) {
            byte[] address = StringUtil.stringToBytes(this.host);
            out.writeInt(address.length);
            out.write(address);
        } else {
            out.writeInt(0);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.port = in.readInt();
        this.type = in.readByte();
        int len = in.readInt();
        if (len > 0) {
            byte[] address = new byte[len];
            in.readFully(address);
            this.host = StringUtil.bytesToString(address);
        }
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Address)) {
            return false;
        }
        Address address = (Address)o;
        return this.port == address.port && this.type == address.type && this.host.equals(address.host);
    }

    public int hashCode() {
        int result = this.port;
        result = 31 * result + this.host.hashCode();
        return result;
    }

    public String toString() {
        return '[' + this.host + "]:" + this.port;
    }

    private static InetAddress resolve(InetSocketAddress inetSocketAddress) {
        Preconditions.checkNotNull(inetSocketAddress, "inetSocketAddress can't be null");
        InetAddress address = inetSocketAddress.getAddress();
        if (address == null) {
            throw new IllegalArgumentException("Can't resolve address: " + inetSocketAddress);
        }
        return address;
    }
}


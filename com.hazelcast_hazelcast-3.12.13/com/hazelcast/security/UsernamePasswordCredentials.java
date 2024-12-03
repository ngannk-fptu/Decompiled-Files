/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.security.AbstractCredentials;
import com.hazelcast.spi.impl.SpiPortableHook;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.StringUtil;
import java.io.IOException;

@BinaryInterface
public class UsernamePasswordCredentials
extends AbstractCredentials {
    private static final long serialVersionUID = -1508314631354255039L;
    private byte[] password;

    public UsernamePasswordCredentials() {
    }

    public UsernamePasswordCredentials(String username, String password) {
        super(username);
        Preconditions.checkNotNull(password);
        this.password = StringUtil.stringToBytes(password);
    }

    public String getUsername() {
        return this.getPrincipal();
    }

    public String getPassword() {
        Preconditions.checkNotNull(this.password);
        return StringUtil.bytesToString(this.password);
    }

    public void setUsername(String username) {
        this.setPrincipal(username);
    }

    public void setPassword(String password) {
        Preconditions.checkNotNull(password);
        this.password = StringUtil.stringToBytes(password);
    }

    @Override
    protected void writePortableInternal(PortableWriter writer) throws IOException {
        writer.writeByteArray("pwd", this.password);
    }

    @Override
    protected void readPortableInternal(PortableReader reader) throws IOException {
        this.password = reader.readByteArray("pwd");
    }

    @Override
    public int getFactoryId() {
        return SpiPortableHook.ID;
    }

    @Override
    public int getClassId() {
        return 1;
    }

    public String toString() {
        return "UsernamePasswordCredentials [username=" + this.getUsername() + "]";
    }
}


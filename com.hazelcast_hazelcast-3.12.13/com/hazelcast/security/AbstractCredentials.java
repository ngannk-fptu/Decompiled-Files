/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.security;

import com.hazelcast.nio.serialization.BinaryInterface;
import com.hazelcast.nio.serialization.Portable;
import com.hazelcast.nio.serialization.PortableReader;
import com.hazelcast.nio.serialization.PortableWriter;
import com.hazelcast.security.Credentials;
import java.io.IOException;

@BinaryInterface
public abstract class AbstractCredentials
implements Credentials,
Portable {
    private static final long serialVersionUID = 3587995040707072446L;
    private String endpoint;
    private String principal;

    public AbstractCredentials() {
    }

    public AbstractCredentials(String principal) {
        this.principal = principal;
    }

    @Override
    public final String getEndpoint() {
        return this.endpoint;
    }

    @Override
    public final void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    @Override
    public String getPrincipal() {
        return this.principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public int hashCode() {
        int prime = 31;
        int result = 1;
        result = this.principal == null ? 31 * result : 31 * result + this.principal.hashCode();
        return result;
    }

    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        AbstractCredentials other = (AbstractCredentials)obj;
        return !(this.principal == null ? other.principal != null : !this.principal.equals(other.principal));
    }

    @Override
    public final void writePortable(PortableWriter writer) throws IOException {
        writer.writeUTF("principal", this.principal);
        writer.writeUTF("endpoint", this.endpoint);
        this.writePortableInternal(writer);
    }

    @Override
    public final void readPortable(PortableReader reader) throws IOException {
        this.principal = reader.readUTF("principal");
        this.endpoint = reader.readUTF("endpoint");
        this.readPortableInternal(reader);
    }

    protected abstract void writePortableInternal(PortableWriter var1) throws IOException;

    protected abstract void readPortableInternal(PortableReader var1) throws IOException;
}


/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.instance.ProtocolType;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.Preconditions;
import java.io.IOException;

public final class EndpointQualifier
implements IdentifiedDataSerializable {
    public static final EndpointQualifier MEMBER = new EndpointQualifier(ProtocolType.MEMBER, null);
    public static final EndpointQualifier CLIENT = new EndpointQualifier(ProtocolType.CLIENT, null);
    public static final EndpointQualifier REST = new EndpointQualifier(ProtocolType.REST, null);
    public static final EndpointQualifier MEMCACHE = new EndpointQualifier(ProtocolType.MEMCACHE, null);
    private ProtocolType type;
    private String identifier;

    public EndpointQualifier() {
    }

    private EndpointQualifier(ProtocolType type, String identifier) {
        Preconditions.checkNotNull(type);
        this.type = type;
        this.identifier = identifier;
    }

    public String getIdentifier() {
        return this.identifier;
    }

    public ProtocolType getType() {
        return this.type;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        EndpointQualifier that = (EndpointQualifier)o;
        if (this.type != that.type) {
            return false;
        }
        if (this.type.getServerSocketCardinality() == 1) {
            return true;
        }
        return this.identifier != null ? this.identifier.equals(that.identifier) : that.identifier == null;
    }

    public int hashCode() {
        int result = this.type.hashCode();
        if (!EndpointQualifier.isSingleType(this.type)) {
            result = 31 * result + (this.identifier != null ? this.identifier.hashCode() : 0);
        }
        return result;
    }

    public String toMetricsPrefixString() {
        String identifier = this.identifier != null ? this.identifier : "";
        return this.type.name() + (!EndpointQualifier.isSingleType(this.type) ? "-" + identifier.replaceAll("\\s", "_") : "");
    }

    private static boolean isSingleType(ProtocolType type) {
        return type.getServerSocketCardinality() == 1;
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.type = ProtocolType.valueOf(in.readInt());
        this.identifier = in.readUTF();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeInt(this.type.ordinal());
        out.writeUTF(this.identifier);
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 45;
    }

    public String toString() {
        return "EndpointQualifier{type='" + (Object)((Object)this.type) + (!EndpointQualifier.isSingleType(this.type) ? "', id='" + this.identifier : "") + '\'' + '}';
    }

    public static EndpointQualifier resolve(ProtocolType protocolType, String name) {
        switch (protocolType) {
            case MEMBER: {
                return MEMBER;
            }
            case CLIENT: {
                return CLIENT;
            }
            case MEMCACHE: {
                return MEMCACHE;
            }
            case REST: {
                return REST;
            }
            case WAN: {
                return new EndpointQualifier(ProtocolType.WAN, name);
            }
        }
        throw new IllegalArgumentException("Cannot resolve EndpointQualifier for protocol type " + (Object)((Object)protocolType));
    }
}


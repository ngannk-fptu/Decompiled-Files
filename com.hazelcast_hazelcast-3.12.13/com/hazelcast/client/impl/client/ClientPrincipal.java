/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.client.impl.client;

public final class ClientPrincipal {
    private String uuid;
    private String ownerUuid;

    public ClientPrincipal() {
    }

    public ClientPrincipal(String uuid, String ownerUuid) {
        this.uuid = uuid;
        this.ownerUuid = ownerUuid;
    }

    public String getUuid() {
        return this.uuid;
    }

    public String getOwnerUuid() {
        return this.ownerUuid;
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        ClientPrincipal that = (ClientPrincipal)o;
        if (this.ownerUuid != null ? !this.ownerUuid.equals(that.ownerUuid) : that.ownerUuid != null) {
            return false;
        }
        return !(this.uuid != null ? !this.uuid.equals(that.uuid) : that.uuid != null);
    }

    public int hashCode() {
        int result = this.uuid != null ? this.uuid.hashCode() : 0;
        result = 31 * result + (this.ownerUuid != null ? this.ownerUuid.hashCode() : 0);
        return result;
    }

    public String toString() {
        return "ClientPrincipal{uuid='" + this.uuid + '\'' + ", ownerUuid='" + this.ownerUuid + '\'' + '}';
    }
}


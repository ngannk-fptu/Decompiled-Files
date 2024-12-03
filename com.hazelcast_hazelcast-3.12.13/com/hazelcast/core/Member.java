/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.core;

import com.hazelcast.core.Endpoint;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.version.MemberVersion;
import java.net.InetSocketAddress;
import java.util.Map;

public interface Member
extends DataSerializable,
Endpoint {
    public boolean localMember();

    public boolean isLiteMember();

    public Address getAddress();

    public Map<EndpointQualifier, Address> getAddressMap();

    @Deprecated
    public InetSocketAddress getInetSocketAddress();

    @Override
    @Deprecated
    public InetSocketAddress getSocketAddress();

    public InetSocketAddress getSocketAddress(EndpointQualifier var1);

    @Override
    public String getUuid();

    public Map<String, Object> getAttributes();

    public String getStringAttribute(String var1);

    public void setStringAttribute(String var1, String var2);

    public Boolean getBooleanAttribute(String var1);

    public void setBooleanAttribute(String var1, boolean var2);

    public Byte getByteAttribute(String var1);

    public void setByteAttribute(String var1, byte var2);

    public Short getShortAttribute(String var1);

    public void setShortAttribute(String var1, short var2);

    public Integer getIntAttribute(String var1);

    public void setIntAttribute(String var1, int var2);

    public Long getLongAttribute(String var1);

    public void setLongAttribute(String var1, long var2);

    public Float getFloatAttribute(String var1);

    public void setFloatAttribute(String var1, float var2);

    public Double getDoubleAttribute(String var1);

    public void setDoubleAttribute(String var1, double var2);

    public void removeAttribute(String var1);

    public MemberVersion getVersion();
}


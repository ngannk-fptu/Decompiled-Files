/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.client.impl;

import com.hazelcast.core.Member;
import com.hazelcast.instance.AbstractMember;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.SerializableByConvention;
import com.hazelcast.version.MemberVersion;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.util.Collections;
import java.util.Map;

@SerializableByConvention(value=SerializableByConvention.Reason.INHERITANCE)
public final class MemberImpl
extends AbstractMember
implements Member {
    public MemberImpl() {
    }

    public MemberImpl(Address address, MemberVersion version) {
        super(Collections.singletonMap(EndpointQualifier.MEMBER, address), version, null, null, false);
    }

    public MemberImpl(Address address, MemberVersion version, String uuid) {
        super(Collections.singletonMap(EndpointQualifier.MEMBER, address), version, uuid, null, false);
    }

    public MemberImpl(Address address, String uuid, Map<String, Object> attributes, boolean liteMember) {
        super(Collections.singletonMap(EndpointQualifier.MEMBER, address), MemberVersion.UNKNOWN, uuid, attributes, liteMember);
    }

    public MemberImpl(Address address, MemberVersion version, String uuid, Map<String, Object> attributes, boolean liteMember) {
        super(Collections.singletonMap(EndpointQualifier.MEMBER, address), version, uuid, attributes, liteMember);
    }

    public MemberImpl(AbstractMember member) {
        super(member);
    }

    @Override
    protected ILogger getLogger() {
        return null;
    }

    @Override
    public boolean localMember() {
        return false;
    }

    @Override
    public String getStringAttribute(String key) {
        return (String)this.getAttribute(key);
    }

    @Override
    public void setStringAttribute(String key, String value) {
        throw this.notSupportedOnClient();
    }

    @Override
    @SuppressFBWarnings(value={"NP_BOOLEAN_RETURN_NULL"}, justification="null means 'missing'")
    public Boolean getBooleanAttribute(String key) {
        Object attribute = this.getAttribute(key);
        return attribute != null ? Boolean.valueOf(attribute.toString()) : null;
    }

    @Override
    public void setBooleanAttribute(String key, boolean value) {
        throw this.notSupportedOnClient();
    }

    @Override
    public Byte getByteAttribute(String key) {
        Object attribute = this.getAttribute(key);
        return attribute != null ? Byte.valueOf(attribute.toString()) : null;
    }

    @Override
    public void setByteAttribute(String key, byte value) {
        throw this.notSupportedOnClient();
    }

    @Override
    public Short getShortAttribute(String key) {
        Object attribute = this.getAttribute(key);
        return attribute != null ? Short.valueOf(attribute.toString()) : null;
    }

    @Override
    public void setShortAttribute(String key, short value) {
        throw this.notSupportedOnClient();
    }

    @Override
    public Integer getIntAttribute(String key) {
        Object attribute = this.getAttribute(key);
        return attribute != null ? Integer.valueOf(attribute.toString()) : null;
    }

    @Override
    public void setIntAttribute(String key, int value) {
        throw this.notSupportedOnClient();
    }

    @Override
    public Long getLongAttribute(String key) {
        Object attribute = this.getAttribute(key);
        return attribute != null ? Long.valueOf(attribute.toString()) : null;
    }

    @Override
    public void setLongAttribute(String key, long value) {
        throw this.notSupportedOnClient();
    }

    @Override
    public Float getFloatAttribute(String key) {
        Object attribute = this.getAttribute(key);
        return attribute != null ? Float.valueOf(attribute.toString()) : null;
    }

    @Override
    public void setFloatAttribute(String key, float value) {
        throw this.notSupportedOnClient();
    }

    @Override
    public Double getDoubleAttribute(String key) {
        Object attribute = this.getAttribute(key);
        return attribute != null ? Double.valueOf(attribute.toString()) : null;
    }

    @Override
    public void setDoubleAttribute(String key, double value) {
        throw this.notSupportedOnClient();
    }

    @Override
    public void removeAttribute(String key) {
        throw this.notSupportedOnClient();
    }

    private UnsupportedOperationException notSupportedOnClient() {
        return new UnsupportedOperationException("Attributes on remote members must not be changed");
    }
}


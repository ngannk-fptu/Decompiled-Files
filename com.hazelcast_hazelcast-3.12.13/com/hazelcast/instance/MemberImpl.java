/*
 * Decompiled with CFR 0.152.
 */
package com.hazelcast.instance;

import com.hazelcast.cluster.MemberAttributeOperationType;
import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.HazelcastInstanceAware;
import com.hazelcast.core.Member;
import com.hazelcast.instance.AbstractMember;
import com.hazelcast.instance.EndpointQualifier;
import com.hazelcast.instance.HazelcastInstanceImpl;
import com.hazelcast.internal.cluster.impl.operations.MemberAttributeChangedOp;
import com.hazelcast.logging.ILogger;
import com.hazelcast.nio.Address;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.spi.Operation;
import com.hazelcast.spi.annotation.PrivateApi;
import com.hazelcast.spi.impl.NodeEngineImpl;
import com.hazelcast.spi.impl.operationservice.InternalOperationService;
import com.hazelcast.util.ExceptionUtil;
import com.hazelcast.util.Preconditions;
import com.hazelcast.util.function.Supplier;
import com.hazelcast.version.MemberVersion;
import java.util.Collections;
import java.util.Map;

@PrivateApi
public final class MemberImpl
extends AbstractMember
implements Member,
HazelcastInstanceAware,
IdentifiedDataSerializable {
    public static final int NA_MEMBER_LIST_JOIN_VERSION = -1;
    private boolean localMember;
    private volatile int memberListJoinVersion = -1;
    private volatile HazelcastInstanceImpl instance;
    private volatile ILogger logger;

    public MemberImpl() {
    }

    public MemberImpl(Address address, MemberVersion version, boolean localMember) {
        this(Collections.singletonMap(EndpointQualifier.MEMBER, address), version, localMember, null, null, false, -1, null);
    }

    public MemberImpl(Address address, MemberVersion version, boolean localMember, String uuid) {
        this(Collections.singletonMap(EndpointQualifier.MEMBER, address), version, localMember, uuid, null, false, -1, null);
    }

    public MemberImpl(MemberImpl member) {
        super(member);
        this.localMember = member.localMember;
        this.memberListJoinVersion = member.memberListJoinVersion;
        this.instance = member.instance;
    }

    private MemberImpl(Map<EndpointQualifier, Address> addresses, MemberVersion version, boolean localMember, String uuid, Map<String, Object> attributes, boolean liteMember, int memberListJoinVersion, HazelcastInstanceImpl instance) {
        super(addresses, version, uuid, attributes, liteMember);
        this.memberListJoinVersion = memberListJoinVersion;
        this.localMember = localMember;
        this.instance = instance;
    }

    @Override
    protected ILogger getLogger() {
        return this.logger;
    }

    @Override
    public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
        if (hazelcastInstance instanceof HazelcastInstanceImpl) {
            this.instance = (HazelcastInstanceImpl)hazelcastInstance;
            this.localMember = this.instance.node.address.equals(this.address);
            this.logger = this.instance.node.getLogger(this.getClass().getName());
        }
    }

    @Override
    public boolean localMember() {
        return this.localMember;
    }

    @Override
    public String getStringAttribute(String key) {
        return (String)this.getAttribute(key);
    }

    @Override
    public void setStringAttribute(String key, String value) {
        this.setAttribute(key, value);
    }

    @Override
    public Boolean getBooleanAttribute(String key) {
        return (Boolean)this.getAttribute(key);
    }

    @Override
    public void setBooleanAttribute(String key, boolean value) {
        this.setAttribute(key, value);
    }

    @Override
    public Byte getByteAttribute(String key) {
        return (Byte)this.getAttribute(key);
    }

    @Override
    public void setByteAttribute(String key, byte value) {
        this.setAttribute(key, value);
    }

    @Override
    public Short getShortAttribute(String key) {
        return (Short)this.getAttribute(key);
    }

    @Override
    public void setShortAttribute(String key, short value) {
        this.setAttribute(key, value);
    }

    @Override
    public Integer getIntAttribute(String key) {
        return (Integer)this.getAttribute(key);
    }

    @Override
    public void setIntAttribute(String key, int value) {
        this.setAttribute(key, value);
    }

    @Override
    public Long getLongAttribute(String key) {
        return (Long)this.getAttribute(key);
    }

    @Override
    public void setLongAttribute(String key, long value) {
        this.setAttribute(key, value);
    }

    @Override
    public Float getFloatAttribute(String key) {
        return (Float)this.getAttribute(key);
    }

    @Override
    public void setFloatAttribute(String key, float value) {
        this.setAttribute(key, Float.valueOf(value));
    }

    @Override
    public Double getDoubleAttribute(String key) {
        return (Double)this.getAttribute(key);
    }

    @Override
    public void setDoubleAttribute(String key, double value) {
        this.setAttribute(key, value);
    }

    @Override
    public void removeAttribute(String key) {
        this.ensureLocalMember();
        Preconditions.isNotNull(key, "key");
        Object value = this.attributes.remove(key);
        if (value == null) {
            return;
        }
        if (this.instance != null) {
            this.invokeOnAllMembers(new MemberAttributeOperationSupplier(MemberAttributeOperationType.REMOVE, key, null));
        }
    }

    public void setMemberListJoinVersion(int memberListJoinVersion) {
        this.memberListJoinVersion = memberListJoinVersion;
    }

    public int getMemberListJoinVersion() {
        return this.memberListJoinVersion;
    }

    private void ensureLocalMember() {
        if (!this.localMember) {
            throw new UnsupportedOperationException("Attributes on remote members must not be changed");
        }
    }

    private void setAttribute(String key, Object value) {
        this.ensureLocalMember();
        Preconditions.isNotNull(key, "key");
        Preconditions.isNotNull(value, "value");
        Object oldValue = this.attributes.put(key, value);
        if (value.equals(oldValue)) {
            return;
        }
        if (this.instance != null) {
            this.invokeOnAllMembers(new MemberAttributeOperationSupplier(MemberAttributeOperationType.PUT, key, value));
        }
    }

    private void invokeOnAllMembers(Supplier<Operation> operationSupplier) {
        NodeEngineImpl nodeEngine = this.instance.node.nodeEngine;
        InternalOperationService os = nodeEngine.getOperationService();
        try {
            for (Member member : nodeEngine.getClusterService().getMembers()) {
                if (!member.localMember()) {
                    os.invokeOnTarget("hz:core:clusterService", operationSupplier.get(), member.getAddress());
                    continue;
                }
                os.execute(operationSupplier.get());
            }
        }
        catch (Throwable t) {
            throw ExceptionUtil.rethrow(t);
        }
    }

    @Override
    public int getFactoryId() {
        return 0;
    }

    @Override
    public int getId() {
        return 2;
    }

    public static class Builder {
        private final Map<EndpointQualifier, Address> addressMap;
        private Map<String, Object> attributes;
        private boolean localMember;
        private String uuid;
        private boolean liteMember;
        private MemberVersion version;
        private int memberListJoinVersion = -1;
        private HazelcastInstanceImpl instance;

        public Builder(Address address) {
            Preconditions.isNotNull(address, "address");
            this.addressMap = Collections.singletonMap(EndpointQualifier.MEMBER, address);
        }

        public Builder(Map<EndpointQualifier, Address> addresses) {
            Preconditions.isNotNull(addresses, "addresses");
            Preconditions.isNotNull(addresses.get(EndpointQualifier.MEMBER), "addresses.get(MEMBER)");
            this.addressMap = addresses;
        }

        public Builder localMember(boolean localMember) {
            this.localMember = localMember;
            return this;
        }

        public Builder version(MemberVersion memberVersion) {
            this.version = memberVersion;
            return this;
        }

        public Builder uuid(String uuid) {
            this.uuid = uuid;
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            this.attributes = attributes;
            return this;
        }

        public Builder memberListJoinVersion(int memberListJoinVersion) {
            this.memberListJoinVersion = memberListJoinVersion;
            return this;
        }

        public Builder liteMember(boolean liteMember) {
            this.liteMember = liteMember;
            return this;
        }

        public Builder instance(HazelcastInstanceImpl hazelcastInstanceImpl) {
            this.instance = hazelcastInstanceImpl;
            return this;
        }

        public MemberImpl build() {
            return new MemberImpl(this.addressMap, this.version, this.localMember, this.uuid, this.attributes, this.liteMember, this.memberListJoinVersion, this.instance);
        }
    }

    private class MemberAttributeOperationSupplier
    implements Supplier<Operation> {
        private final MemberAttributeOperationType operationType;
        private final String key;
        private final Object value;

        MemberAttributeOperationSupplier(MemberAttributeOperationType operationType, String key, Object value) {
            this.operationType = operationType;
            this.key = key;
            this.value = value;
        }

        @Override
        public Operation get() {
            NodeEngineImpl nodeEngine = ((MemberImpl)MemberImpl.this).instance.node.nodeEngine;
            String uuid = nodeEngine.getLocalMember().getUuid();
            return new MemberAttributeChangedOp(this.operationType, this.key, this.value).setCallerUuid(uuid).setNodeEngine(nodeEngine);
        }
    }
}


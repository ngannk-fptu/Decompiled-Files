/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.core;

import com.hazelcast.cluster.MemberAttributeOperationType;
import com.hazelcast.core.Cluster;
import com.hazelcast.core.Member;
import com.hazelcast.core.MembershipEvent;
import com.hazelcast.instance.MemberImpl;
import com.hazelcast.nio.IOUtil;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.DataSerializable;
import com.hazelcast.nio.serialization.SerializableByConvention;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;
import java.util.Collections;

@SerializableByConvention(value=SerializableByConvention.Reason.PUBLIC_API)
@SuppressFBWarnings(value={"SE_BAD_FIELD"})
public class MemberAttributeEvent
extends MembershipEvent
implements DataSerializable {
    private MemberAttributeOperationType operationType;
    private String key;
    private Object value;
    private Member member;

    public MemberAttributeEvent() {
        super(null, null, 5, Collections.EMPTY_SET);
    }

    public MemberAttributeEvent(Cluster cluster, Member member, MemberAttributeOperationType operationType, String key, Object value) {
        super(cluster, member, 5, Collections.EMPTY_SET);
        this.member = member;
        this.operationType = operationType;
        this.key = key;
        this.value = value;
    }

    public MemberAttributeOperationType getOperationType() {
        return this.operationType;
    }

    public String getKey() {
        return this.key;
    }

    public Object getValue() {
        return this.value;
    }

    @Override
    public Member getMember() {
        return this.member;
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeUTF(this.key);
        this.member.writeData(out);
        out.writeByte(this.operationType.getId());
        if (this.operationType == MemberAttributeOperationType.PUT) {
            IOUtil.writeAttributeValue(this.value, out);
        }
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.key = in.readUTF();
        this.member = new MemberImpl();
        this.member.readData(in);
        this.operationType = MemberAttributeOperationType.getValue(in.readByte());
        if (this.operationType == MemberAttributeOperationType.PUT) {
            this.value = IOUtil.readAttributeValue(in);
        }
        this.source = this.member;
    }
}


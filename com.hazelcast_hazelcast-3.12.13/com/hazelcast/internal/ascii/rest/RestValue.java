/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 */
package com.hazelcast.internal.ascii.rest;

import com.hazelcast.internal.ascii.TextProtocolsDataSerializerHook;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;
import com.hazelcast.util.StringUtil;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import java.io.IOException;

@SuppressFBWarnings(value={"EI_EXPOSE_REP"})
public class RestValue
implements IdentifiedDataSerializable {
    private byte[] value;
    private byte[] contentType;

    public RestValue() {
    }

    public RestValue(byte[] value, byte[] contentType) {
        this.value = value;
        this.contentType = contentType;
    }

    @Override
    public void readData(ObjectDataInput in) throws IOException {
        this.value = in.readByteArray();
        this.contentType = in.readByteArray();
    }

    @Override
    public void writeData(ObjectDataOutput out) throws IOException {
        out.writeByteArray(this.value);
        out.writeByteArray(this.contentType);
    }

    public byte[] getContentType() {
        return this.contentType;
    }

    public void setContentType(byte[] contentType) {
        this.contentType = contentType;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String toString() {
        String contentTypeStr = this.contentType == null ? "unknown-content-type" : StringUtil.bytesToString(this.contentType);
        String valueStr = this.value == null ? "value.length=0" : (contentTypeStr.contains("text") ? "value=\"" + StringUtil.bytesToString(this.value) + "\"" : "value.length=" + this.value.length);
        return "RestValue{contentType='" + contentTypeStr + "', " + valueStr + '}';
    }

    @Override
    public int getFactoryId() {
        return TextProtocolsDataSerializerHook.F_ID;
    }

    @Override
    public int getId() {
        return 1;
    }
}


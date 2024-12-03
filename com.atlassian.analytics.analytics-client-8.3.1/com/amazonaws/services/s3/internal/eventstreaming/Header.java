/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.internal.eventstreaming;

import com.amazonaws.services.s3.internal.eventstreaming.HeaderValue;
import com.amazonaws.services.s3.internal.eventstreaming.Utils;
import com.amazonaws.util.ValidationUtils;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Map;

class Header {
    private final String name;
    private final HeaderValue value;

    Header(String name, HeaderValue value) {
        this.name = ValidationUtils.assertNotNull(name, "value");
        this.value = ValidationUtils.assertNotNull(value, "value");
    }

    Header(String name, String value) {
        this(name, HeaderValue.fromString(value));
    }

    public String getName() {
        return this.name;
    }

    public HeaderValue getValue() {
        return this.value;
    }

    static Header decode(ByteBuffer buf) {
        String name = Utils.readShortString(buf);
        return new Header(name, HeaderValue.decode(buf));
    }

    static void encode(Map.Entry<String, HeaderValue> header, DataOutputStream dos) throws IOException {
        new Header(header.getKey(), header.getValue()).encode(dos);
    }

    void encode(DataOutputStream dos) throws IOException {
        Utils.writeShortString(dos, this.name);
        this.value.encode(dos);
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Header header = (Header)o;
        if (!this.name.equals(header.name)) {
            return false;
        }
        return this.value.equals(header.value);
    }

    public int hashCode() {
        int result = this.name.hashCode();
        result = 31 * result + this.value.hashCode();
        return result;
    }

    public String toString() {
        return "Header{name='" + this.name + '\'' + ", value=" + this.value + '}';
    }
}


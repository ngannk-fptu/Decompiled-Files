/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.encoding;

import org.apache.axis.encoding.SerializationContext;
import org.apache.axis.encoding.Serializer;

public interface SimpleValueSerializer
extends Serializer {
    public String getValueAsString(Object var1, SerializationContext var2);
}


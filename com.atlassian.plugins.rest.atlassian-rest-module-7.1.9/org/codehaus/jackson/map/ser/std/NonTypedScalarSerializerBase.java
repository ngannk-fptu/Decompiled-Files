/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.io.IOException;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.TypeSerializer;
import org.codehaus.jackson.map.ser.std.ScalarSerializerBase;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class NonTypedScalarSerializerBase<T>
extends ScalarSerializerBase<T> {
    protected NonTypedScalarSerializerBase(Class<T> t) {
        super(t);
    }

    @Override
    public final void serializeWithType(T value, JsonGenerator jgen, SerializerProvider provider, TypeSerializer typeSer) throws IOException, JsonGenerationException {
        this.serialize(value, jgen, provider);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.ser.std;

import java.lang.reflect.Type;
import java.util.Collection;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.SerializerProvider;
import org.codehaus.jackson.map.ser.std.SerializerBase;
import org.codehaus.jackson.node.ObjectNode;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public abstract class StaticListSerializerBase<T extends Collection<?>>
extends SerializerBase<T> {
    protected final BeanProperty _property;

    protected StaticListSerializerBase(Class<?> cls, BeanProperty property) {
        super(cls, false);
        this._property = property;
    }

    @Override
    public JsonNode getSchema(SerializerProvider provider, Type typeHint) {
        ObjectNode o = this.createSchemaNode("array", true);
        o.put("items", this.contentSchema());
        return o;
    }

    protected abstract JsonNode contentSchema();
}


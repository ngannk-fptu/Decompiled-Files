/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.std;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.BeanProperty;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.DeserializerProvider;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ResolvableDeserializer;
import org.codehaus.jackson.map.deser.std.StdScalarDeserializer;
import org.codehaus.jackson.type.JavaType;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class AtomicReferenceDeserializer
extends StdScalarDeserializer<AtomicReference<?>>
implements ResolvableDeserializer {
    protected final JavaType _referencedType;
    protected final BeanProperty _property;
    protected JsonDeserializer<?> _valueDeserializer;

    public AtomicReferenceDeserializer(JavaType referencedType, BeanProperty property) {
        super(AtomicReference.class);
        this._referencedType = referencedType;
        this._property = property;
    }

    @Override
    public AtomicReference<?> deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException, JsonProcessingException {
        return new AtomicReference(this._valueDeserializer.deserialize(jp, ctxt));
    }

    @Override
    public void resolve(DeserializationConfig config, DeserializerProvider provider) throws JsonMappingException {
        this._valueDeserializer = provider.findValueDeserializer(config, this._referencedType, this._property);
    }
}


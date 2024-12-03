/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.impl;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.util.TokenBuffer;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class ExternalTypeHandler {
    private final ExtTypedProperty[] _properties;
    private final HashMap<String, Integer> _nameToPropertyIndex;
    private final String[] _typeIds;
    private final TokenBuffer[] _tokens;

    protected ExternalTypeHandler(ExtTypedProperty[] properties, HashMap<String, Integer> nameToPropertyIndex, String[] typeIds, TokenBuffer[] tokens) {
        this._properties = properties;
        this._nameToPropertyIndex = nameToPropertyIndex;
        this._typeIds = typeIds;
        this._tokens = tokens;
    }

    protected ExternalTypeHandler(ExternalTypeHandler h) {
        this._properties = h._properties;
        this._nameToPropertyIndex = h._nameToPropertyIndex;
        int len = this._properties.length;
        this._typeIds = new String[len];
        this._tokens = new TokenBuffer[len];
    }

    public ExternalTypeHandler start() {
        return new ExternalTypeHandler(this);
    }

    public boolean handleTypePropertyValue(JsonParser jp, DeserializationContext ctxt, String propName, Object bean) throws IOException, JsonProcessingException {
        boolean canDeserialize;
        Integer I = this._nameToPropertyIndex.get(propName);
        if (I == null) {
            return false;
        }
        int index = I;
        ExtTypedProperty prop = this._properties[index];
        if (!prop.hasTypePropertyName(propName)) {
            return false;
        }
        this._typeIds[index] = jp.getText();
        boolean bl = canDeserialize = bean != null && this._tokens[index] != null;
        if (canDeserialize) {
            this._deserialize(jp, ctxt, bean, index);
            this._typeIds[index] = null;
            this._tokens[index] = null;
        }
        return true;
    }

    public boolean handleToken(JsonParser jp, DeserializationContext ctxt, String propName, Object bean) throws IOException, JsonProcessingException {
        boolean canDeserialize;
        Integer I = this._nameToPropertyIndex.get(propName);
        if (I == null) {
            return false;
        }
        int index = I;
        ExtTypedProperty prop = this._properties[index];
        if (prop.hasTypePropertyName(propName)) {
            this._typeIds[index] = jp.getText();
            jp.skipChildren();
            canDeserialize = bean != null && this._tokens[index] != null;
        } else {
            TokenBuffer tokens = new TokenBuffer(jp.getCodec());
            tokens.copyCurrentStructure(jp);
            this._tokens[index] = tokens;
            boolean bl = canDeserialize = bean != null && this._typeIds[index] != null;
        }
        if (canDeserialize) {
            this._deserialize(jp, ctxt, bean, index);
            this._typeIds[index] = null;
            this._tokens[index] = null;
        }
        return true;
    }

    public Object complete(JsonParser jp, DeserializationContext ctxt, Object bean) throws IOException, JsonProcessingException {
        int len = this._properties.length;
        for (int i = 0; i < len; ++i) {
            if (this._typeIds[i] == null) {
                if (this._tokens[i] == null) continue;
                throw ctxt.mappingException("Missing external type id property '" + this._properties[i].getTypePropertyName() + "'");
            }
            if (this._tokens[i] == null) {
                SettableBeanProperty prop = this._properties[i].getProperty();
                throw ctxt.mappingException("Missing property '" + prop.getName() + "' for external type id '" + this._properties[i].getTypePropertyName());
            }
            this._deserialize(jp, ctxt, bean, i);
        }
        return bean;
    }

    protected final void _deserialize(JsonParser jp, DeserializationContext ctxt, Object bean, int index) throws IOException, JsonProcessingException {
        TokenBuffer merged = new TokenBuffer(jp.getCodec());
        merged.writeStartArray();
        merged.writeString(this._typeIds[index]);
        JsonParser p2 = this._tokens[index].asParser(jp);
        p2.nextToken();
        merged.copyCurrentStructure(p2);
        merged.writeEndArray();
        p2 = merged.asParser(jp);
        p2.nextToken();
        this._properties[index].getProperty().deserializeAndSet(p2, ctxt, bean);
    }

    private static final class ExtTypedProperty {
        private final SettableBeanProperty _property;
        private final String _typePropertyName;

        public ExtTypedProperty(SettableBeanProperty property, String typePropertyName) {
            this._property = property;
            this._typePropertyName = typePropertyName;
        }

        public boolean hasTypePropertyName(String n) {
            return n.equals(this._typePropertyName);
        }

        public String getTypePropertyName() {
            return this._typePropertyName;
        }

        public SettableBeanProperty getProperty() {
            return this._property;
        }
    }

    public static class Builder {
        private final ArrayList<ExtTypedProperty> _properties = new ArrayList();
        private final HashMap<String, Integer> _nameToPropertyIndex = new HashMap();

        public void addExternal(SettableBeanProperty property, String extPropName) {
            Integer index = this._properties.size();
            this._properties.add(new ExtTypedProperty(property, extPropName));
            this._nameToPropertyIndex.put(property.getName(), index);
            this._nameToPropertyIndex.put(extPropName, index);
        }

        public ExternalTypeHandler build() {
            return new ExternalTypeHandler(this._properties.toArray(new ExtTypedProperty[this._properties.size()]), this._nameToPropertyIndex, null, null);
        }
    }
}


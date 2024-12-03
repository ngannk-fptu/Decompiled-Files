/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.impl;

import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.deser.SettableAnyProperty;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.deser.impl.PropertyValue;

public final class PropertyValueBuffer {
    final JsonParser _parser;
    final DeserializationContext _context;
    final Object[] _creatorParameters;
    private int _paramsNeeded;
    private PropertyValue _buffered;

    public PropertyValueBuffer(JsonParser jp, DeserializationContext ctxt, int paramCount) {
        this._parser = jp;
        this._context = ctxt;
        this._paramsNeeded = paramCount;
        this._creatorParameters = new Object[paramCount];
    }

    public void inject(SettableBeanProperty[] injectableProperties) {
        for (SettableBeanProperty prop : injectableProperties) {
            if (prop == null) continue;
            this._creatorParameters[i] = this._context.findInjectableValue(prop.getInjectableValueId(), prop, null);
        }
    }

    protected final Object[] getParameters(Object[] defaults) {
        if (defaults != null) {
            int len = this._creatorParameters.length;
            for (int i = 0; i < len; ++i) {
                Object value;
                if (this._creatorParameters[i] != null || (value = defaults[i]) == null) continue;
                this._creatorParameters[i] = value;
            }
        }
        return this._creatorParameters;
    }

    protected PropertyValue buffered() {
        return this._buffered;
    }

    public boolean assignParameter(int index, Object value) {
        this._creatorParameters[index] = value;
        return --this._paramsNeeded <= 0;
    }

    public void bufferProperty(SettableBeanProperty prop, Object value) {
        this._buffered = new PropertyValue.Regular(this._buffered, value, prop);
    }

    public void bufferAnyProperty(SettableAnyProperty prop, String propName, Object value) {
        this._buffered = new PropertyValue.Any(this._buffered, value, prop, propName);
    }

    public void bufferMapProperty(Object key, Object value) {
        this._buffered = new PropertyValue.Map(this._buffered, value, key);
    }
}


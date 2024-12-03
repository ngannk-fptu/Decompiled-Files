/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.impl;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.DeserializationContext;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.deser.SettableBeanProperty;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.impl.PropertyValue;
import org.codehaus.jackson.map.deser.impl.PropertyValueBuffer;
import org.codehaus.jackson.map.util.ClassUtil;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public final class PropertyBasedCreator {
    protected final ValueInstantiator _valueInstantiator;
    protected final HashMap<String, SettableBeanProperty> _properties;
    protected final int _propertyCount;
    protected Object[] _defaultValues;
    protected final SettableBeanProperty[] _propertiesWithInjectables;

    public PropertyBasedCreator(ValueInstantiator valueInstantiator) {
        int len;
        this._valueInstantiator = valueInstantiator;
        this._properties = new HashMap();
        Object[] defValues = null;
        SettableBeanProperty[] creatorProps = valueInstantiator.getFromObjectArguments();
        SettableBeanProperty[] propertiesWithInjectables = null;
        this._propertyCount = len = creatorProps.length;
        for (int i = 0; i < len; ++i) {
            Object injectableValueId;
            SettableBeanProperty prop = creatorProps[i];
            this._properties.put(prop.getName(), prop);
            if (prop.getType().isPrimitive()) {
                if (defValues == null) {
                    defValues = new Object[len];
                }
                defValues[i] = ClassUtil.defaultValue(prop.getType().getRawClass());
            }
            if ((injectableValueId = prop.getInjectableValueId()) == null) continue;
            if (propertiesWithInjectables == null) {
                propertiesWithInjectables = new SettableBeanProperty[len];
            }
            propertiesWithInjectables[i] = prop;
        }
        this._defaultValues = defValues;
        this._propertiesWithInjectables = propertiesWithInjectables;
    }

    public Collection<SettableBeanProperty> getCreatorProperties() {
        return this._properties.values();
    }

    public SettableBeanProperty findCreatorProperty(String name) {
        return this._properties.get(name);
    }

    public void assignDeserializer(SettableBeanProperty prop, JsonDeserializer<Object> deser) {
        prop = prop.withValueDeserializer(deser);
        this._properties.put(prop.getName(), prop);
        Object nullValue = deser.getNullValue();
        if (nullValue != null) {
            if (this._defaultValues == null) {
                this._defaultValues = new Object[this._properties.size()];
            }
            this._defaultValues[prop.getPropertyIndex()] = nullValue;
        }
    }

    public PropertyValueBuffer startBuilding(JsonParser jp, DeserializationContext ctxt) {
        PropertyValueBuffer buffer = new PropertyValueBuffer(jp, ctxt, this._propertyCount);
        if (this._propertiesWithInjectables != null) {
            buffer.inject(this._propertiesWithInjectables);
        }
        return buffer;
    }

    public Object build(PropertyValueBuffer buffer) throws IOException {
        Object bean = this._valueInstantiator.createFromObjectWith(buffer.getParameters(this._defaultValues));
        PropertyValue pv = buffer.buffered();
        while (pv != null) {
            pv.assign(bean);
            pv = pv.next;
        }
        return bean;
    }
}


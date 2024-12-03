/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.codehaus.jackson.Version
 */
package org.codehaus.jackson.map.module;

import java.util.HashMap;
import java.util.Map;
import org.codehaus.jackson.Version;
import org.codehaus.jackson.map.JsonDeserializer;
import org.codehaus.jackson.map.JsonSerializer;
import org.codehaus.jackson.map.KeyDeserializer;
import org.codehaus.jackson.map.Module;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.module.SimpleAbstractTypeResolver;
import org.codehaus.jackson.map.module.SimpleDeserializers;
import org.codehaus.jackson.map.module.SimpleKeyDeserializers;
import org.codehaus.jackson.map.module.SimpleSerializers;
import org.codehaus.jackson.map.module.SimpleValueInstantiators;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleModule
extends Module {
    protected final String _name;
    protected final Version _version;
    protected SimpleSerializers _serializers = null;
    protected SimpleDeserializers _deserializers = null;
    protected SimpleSerializers _keySerializers = null;
    protected SimpleKeyDeserializers _keyDeserializers = null;
    protected SimpleAbstractTypeResolver _abstractTypes = null;
    protected SimpleValueInstantiators _valueInstantiators = null;
    protected HashMap<Class<?>, Class<?>> _mixins = null;

    public SimpleModule(String name, Version version) {
        this._name = name;
        this._version = version;
    }

    public void setSerializers(SimpleSerializers s) {
        this._serializers = s;
    }

    public void setDeserializers(SimpleDeserializers d) {
        this._deserializers = d;
    }

    public void setKeySerializers(SimpleSerializers ks) {
        this._keySerializers = ks;
    }

    public void setKeyDeserializers(SimpleKeyDeserializers kd) {
        this._keyDeserializers = kd;
    }

    public void setAbstractTypes(SimpleAbstractTypeResolver atr) {
        this._abstractTypes = atr;
    }

    public void setValueInstantiators(SimpleValueInstantiators svi) {
        this._valueInstantiators = svi;
    }

    public SimpleModule addSerializer(JsonSerializer<?> ser) {
        if (this._serializers == null) {
            this._serializers = new SimpleSerializers();
        }
        this._serializers.addSerializer(ser);
        return this;
    }

    public <T> SimpleModule addSerializer(Class<? extends T> type, JsonSerializer<T> ser) {
        if (this._serializers == null) {
            this._serializers = new SimpleSerializers();
        }
        this._serializers.addSerializer(type, ser);
        return this;
    }

    public <T> SimpleModule addKeySerializer(Class<? extends T> type, JsonSerializer<T> ser) {
        if (this._keySerializers == null) {
            this._keySerializers = new SimpleSerializers();
        }
        this._keySerializers.addSerializer(type, ser);
        return this;
    }

    public <T> SimpleModule addDeserializer(Class<T> type, JsonDeserializer<? extends T> deser) {
        if (this._deserializers == null) {
            this._deserializers = new SimpleDeserializers();
        }
        this._deserializers.addDeserializer(type, deser);
        return this;
    }

    public SimpleModule addKeyDeserializer(Class<?> type, KeyDeserializer deser) {
        if (this._keyDeserializers == null) {
            this._keyDeserializers = new SimpleKeyDeserializers();
        }
        this._keyDeserializers.addDeserializer(type, deser);
        return this;
    }

    public <T> SimpleModule addAbstractTypeMapping(Class<T> superType, Class<? extends T> subType) {
        if (this._abstractTypes == null) {
            this._abstractTypes = new SimpleAbstractTypeResolver();
        }
        this._abstractTypes = this._abstractTypes.addMapping(superType, subType);
        return this;
    }

    public SimpleModule addValueInstantiator(Class<?> beanType, ValueInstantiator inst) {
        if (this._valueInstantiators == null) {
            this._valueInstantiators = new SimpleValueInstantiators();
        }
        this._valueInstantiators = this._valueInstantiators.addValueInstantiator(beanType, inst);
        return this;
    }

    public SimpleModule setMixInAnnotation(Class<?> targetType, Class<?> mixinClass) {
        if (this._mixins == null) {
            this._mixins = new HashMap();
        }
        this._mixins.put(targetType, mixinClass);
        return this;
    }

    @Override
    public String getModuleName() {
        return this._name;
    }

    @Override
    public void setupModule(Module.SetupContext context) {
        if (this._serializers != null) {
            context.addSerializers(this._serializers);
        }
        if (this._deserializers != null) {
            context.addDeserializers(this._deserializers);
        }
        if (this._keySerializers != null) {
            context.addKeySerializers(this._keySerializers);
        }
        if (this._keyDeserializers != null) {
            context.addKeyDeserializers(this._keyDeserializers);
        }
        if (this._abstractTypes != null) {
            context.addAbstractTypeResolver(this._abstractTypes);
        }
        if (this._valueInstantiators != null) {
            context.addValueInstantiators(this._valueInstantiators);
        }
        if (this._mixins != null) {
            for (Map.Entry<Class<?>, Class<?>> entry : this._mixins.entrySet()) {
                context.setMixInAnnotations(entry.getKey(), entry.getValue());
            }
        }
    }

    @Override
    public Version version() {
        return this._version;
    }
}


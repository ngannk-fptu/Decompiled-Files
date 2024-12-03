/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.module;

import java.util.HashMap;
import org.codehaus.jackson.map.BeanDescription;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.ValueInstantiators;
import org.codehaus.jackson.map.type.ClassKey;

/*
 * This class specifies class file version 49.0 but uses Java 6 signatures.  Assumed Java 6.
 */
public class SimpleValueInstantiators
extends ValueInstantiators.Base {
    protected HashMap<ClassKey, ValueInstantiator> _classMappings = new HashMap();

    public SimpleValueInstantiators addValueInstantiator(Class<?> forType, ValueInstantiator inst) {
        this._classMappings.put(new ClassKey(forType), inst);
        return this;
    }

    @Override
    public ValueInstantiator findValueInstantiator(DeserializationConfig config, BeanDescription beanDesc, ValueInstantiator defaultInstantiator) {
        ValueInstantiator inst = this._classMappings.get(new ClassKey(beanDesc.getBeanClass()));
        return inst == null ? defaultInstantiator : inst;
    }
}


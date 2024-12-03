/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.jackson.map.deser.impl;

import java.lang.reflect.Member;
import java.util.HashMap;
import org.codehaus.jackson.map.DeserializationConfig;
import org.codehaus.jackson.map.deser.ValueInstantiator;
import org.codehaus.jackson.map.deser.impl.CreatorProperty;
import org.codehaus.jackson.map.deser.std.StdValueInstantiator;
import org.codehaus.jackson.map.introspect.AnnotatedConstructor;
import org.codehaus.jackson.map.introspect.AnnotatedWithParams;
import org.codehaus.jackson.map.introspect.BasicBeanDescription;
import org.codehaus.jackson.map.type.TypeBindings;
import org.codehaus.jackson.map.util.ClassUtil;
import org.codehaus.jackson.type.JavaType;

public class CreatorCollector {
    final BasicBeanDescription _beanDesc;
    final boolean _canFixAccess;
    protected AnnotatedConstructor _defaultConstructor;
    protected AnnotatedWithParams _stringCreator;
    protected AnnotatedWithParams _intCreator;
    protected AnnotatedWithParams _longCreator;
    protected AnnotatedWithParams _doubleCreator;
    protected AnnotatedWithParams _booleanCreator;
    protected AnnotatedWithParams _delegateCreator;
    protected AnnotatedWithParams _propertyBasedCreator;
    protected CreatorProperty[] _propertyBasedArgs = null;

    public CreatorCollector(BasicBeanDescription beanDesc, boolean canFixAccess) {
        this._beanDesc = beanDesc;
        this._canFixAccess = canFixAccess;
    }

    public ValueInstantiator constructValueInstantiator(DeserializationConfig config) {
        JavaType delegateType;
        StdValueInstantiator inst = new StdValueInstantiator(config, this._beanDesc.getType());
        if (this._delegateCreator == null) {
            delegateType = null;
        } else {
            TypeBindings bindings = this._beanDesc.bindingsForBeanType();
            delegateType = bindings.resolveType(this._delegateCreator.getParameterType(0));
        }
        inst.configureFromObjectSettings(this._defaultConstructor, this._delegateCreator, delegateType, this._propertyBasedCreator, this._propertyBasedArgs);
        inst.configureFromStringCreator(this._stringCreator);
        inst.configureFromIntCreator(this._intCreator);
        inst.configureFromLongCreator(this._longCreator);
        inst.configureFromDoubleCreator(this._doubleCreator);
        inst.configureFromBooleanCreator(this._booleanCreator);
        return inst;
    }

    public void setDefaultConstructor(AnnotatedConstructor ctor) {
        this._defaultConstructor = ctor;
    }

    public void addStringCreator(AnnotatedWithParams creator) {
        this._stringCreator = this.verifyNonDup(creator, this._stringCreator, "String");
    }

    public void addIntCreator(AnnotatedWithParams creator) {
        this._intCreator = this.verifyNonDup(creator, this._intCreator, "int");
    }

    public void addLongCreator(AnnotatedWithParams creator) {
        this._longCreator = this.verifyNonDup(creator, this._longCreator, "long");
    }

    public void addDoubleCreator(AnnotatedWithParams creator) {
        this._doubleCreator = this.verifyNonDup(creator, this._doubleCreator, "double");
    }

    public void addBooleanCreator(AnnotatedWithParams creator) {
        this._booleanCreator = this.verifyNonDup(creator, this._booleanCreator, "boolean");
    }

    public void addDelegatingCreator(AnnotatedWithParams creator) {
        this._delegateCreator = this.verifyNonDup(creator, this._delegateCreator, "delegate");
    }

    public void addPropertyCreator(AnnotatedWithParams creator, CreatorProperty[] properties) {
        this._propertyBasedCreator = this.verifyNonDup(creator, this._propertyBasedCreator, "property-based");
        if (properties.length > 1) {
            HashMap<String, Integer> names = new HashMap<String, Integer>();
            int len = properties.length;
            for (int i = 0; i < len; ++i) {
                Integer old;
                String name = properties[i].getName();
                if (name.length() == 0 && properties[i].getInjectableValueId() != null || (old = names.put(name, i)) == null) continue;
                throw new IllegalArgumentException("Duplicate creator property \"" + name + "\" (index " + old + " vs " + i + ")");
            }
        }
        this._propertyBasedArgs = properties;
    }

    protected AnnotatedWithParams verifyNonDup(AnnotatedWithParams newOne, AnnotatedWithParams oldOne, String type) {
        if (oldOne != null && oldOne.getClass() == newOne.getClass()) {
            throw new IllegalArgumentException("Conflicting " + type + " creators: already had " + oldOne + ", encountered " + newOne);
        }
        if (this._canFixAccess) {
            ClassUtil.checkAndFixAccess((Member)((Object)newOne.getAnnotated()));
        }
        return newOne;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.transform.Generated
 *  groovy.transform.Internal
 */
package net.fortuna.ical4j.model.property;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.Generated;
import groovy.transform.Internal;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.Parameter;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.PropertyFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class PropertyFactoryWrapper
extends AbstractFactory
implements GroovyObject {
    private Class propertyClass;
    private PropertyFactory factory;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public PropertyFactoryWrapper(Class propClass, PropertyFactory factory) {
        MetaClass metaClass;
        CallSite[] callSiteArray = PropertyFactoryWrapper.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = propClass;
        this.propertyClass = ShortTypeHandling.castToClass(clazz);
        PropertyFactory propertyFactory = factory;
        this.factory = (PropertyFactory)ScriptBytecodeAdapter.castToType(propertyFactory, PropertyFactory.class);
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = PropertyFactoryWrapper.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, this.propertyClass))) {
            return value;
        }
        ParameterList parameters = (ParameterList)ScriptBytecodeAdapter.castToType(callSiteArray[1].call((Object)attributes, "parameters"), ParameterList.class);
        if (ScriptBytecodeAdapter.compareEqual(parameters, null)) {
            Object object = callSiteArray[2].callConstructor(ParameterList.class);
            parameters = (ParameterList)ScriptBytecodeAdapter.castToType(object, ParameterList.class);
        }
        String propValue = ShortTypeHandling.castToString(callSiteArray[3].call((Object)attributes, "value"));
        if (ScriptBytecodeAdapter.compareNotEqual(propValue, null)) {
            return callSiteArray[4].call(this.factory, parameters, propValue);
        }
        return callSiteArray[5].call(this.factory, parameters, value);
    }

    @Override
    public void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        CallSite[] callSiteArray = PropertyFactoryWrapper.$getCallSiteArray();
        if (child instanceof Parameter) {
            callSiteArray[6].call(callSiteArray[7].callGetProperty(parent), child);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != PropertyFactoryWrapper.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    @Generated
    @Internal
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    @Generated
    public Class getPropertyClass() {
        return this.propertyClass;
    }

    @Generated
    public void setPropertyClass(Class clazz) {
        this.propertyClass = clazz;
    }

    @Generated
    public PropertyFactory getFactory() {
        return this.factory;
    }

    @Generated
    public void setFactory(PropertyFactory propertyFactory) {
        this.factory = propertyFactory;
    }

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "remove";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "remove";
        stringArray[4] = "createProperty";
        stringArray[5] = "createProperty";
        stringArray[6] = "add";
        stringArray[7] = "parameters";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        PropertyFactoryWrapper.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(PropertyFactoryWrapper.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = PropertyFactoryWrapper.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


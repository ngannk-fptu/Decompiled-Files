/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.transform.Generated
 *  groovy.transform.Internal
 */
package net.fortuna.ical4j.model.parameter;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.Generated;
import groovy.transform.Internal;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.ParameterFactory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ParameterFactoryWrapper
extends AbstractFactory
implements GroovyObject {
    private Class parameterClass;
    private ParameterFactory factory;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ParameterFactoryWrapper(Class paramClass, ParameterFactory factory) {
        MetaClass metaClass;
        CallSite[] callSiteArray = ParameterFactoryWrapper.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        Class clazz = paramClass;
        this.parameterClass = ShortTypeHandling.castToClass(clazz);
        ParameterFactory parameterFactory = factory;
        this.factory = (ParameterFactory)ScriptBytecodeAdapter.castToType(parameterFactory, ParameterFactory.class);
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ParameterFactoryWrapper.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, this.parameterClass))) {
            return value;
        }
        return callSiteArray[1].call((Object)this.factory, value);
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = ParameterFactoryWrapper.$getCallSiteArray();
        return true;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ParameterFactoryWrapper.class) {
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
    public Class getParameterClass() {
        return this.parameterClass;
    }

    @Generated
    public void setParameterClass(Class clazz) {
        this.parameterClass = clazz;
    }

    @Generated
    public ParameterFactory getFactory() {
        return this.factory;
    }

    @Generated
    public void setFactory(ParameterFactory parameterFactory) {
        this.factory = parameterFactory;
    }

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "createParameter";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[2];
        ParameterFactoryWrapper.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ParameterFactoryWrapper.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ParameterFactoryWrapper.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


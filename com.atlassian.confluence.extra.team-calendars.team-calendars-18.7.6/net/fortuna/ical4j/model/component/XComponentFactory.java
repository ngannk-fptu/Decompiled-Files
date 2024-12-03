/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  groovy.transform.Generated
 *  groovy.transform.Internal
 */
package net.fortuna.ical4j.model.component;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.transform.Generated;
import groovy.transform.Internal;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.PropertyList;
import net.fortuna.ical4j.model.component.XComponent;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class XComponentFactory
extends AbstractFactory
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public XComponentFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = XComponentFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = XComponentFactory.$getCallSiteArray();
        XComponent component = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, XComponent.class))) {
            XComponent xComponent;
            component = xComponent = (XComponent)ScriptBytecodeAdapter.castToType(value, XComponent.class);
        } else {
            Object componentName = callSiteArray[1].call((Object)attributes, "name");
            if (ScriptBytecodeAdapter.compareEqual(componentName, null)) {
                Object object;
                componentName = object = value;
            }
            PropertyList properties = (PropertyList)ScriptBytecodeAdapter.castToType(callSiteArray[2].call((Object)attributes, "properties"), PropertyList.class);
            if (ScriptBytecodeAdapter.compareEqual(properties, null)) {
                Object object = callSiteArray[3].callConstructor(PropertyList.class);
                properties = (PropertyList)ScriptBytecodeAdapter.castToType(object, PropertyList.class);
            }
            Object object = callSiteArray[4].callStatic(XComponentFactory.class, componentName, properties);
            component = (XComponent)ScriptBytecodeAdapter.castToType(object, XComponent.class);
        }
        return component;
    }

    protected static Object newInstance(String name, PropertyList properties) {
        CallSite[] callSiteArray = XComponentFactory.$getCallSiteArray();
        return callSiteArray[5].callConstructor(XComponent.class, name, properties);
    }

    @Override
    public void setChild(FactoryBuilderSupport build, Object parent, Object child) {
        CallSite[] callSiteArray = XComponentFactory.$getCallSiteArray();
        if (child instanceof Property) {
            callSiteArray[6].call(callSiteArray[7].callGetProperty(parent), child);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != XComponentFactory.class) {
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

    public /* synthetic */ void super$2$setChild(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.setChild(factoryBuilderSupport, object, object2);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "remove";
        stringArray[2] = "remove";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "newInstance";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "add";
        stringArray[7] = "properties";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        XComponentFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(XComponentFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = XComponentFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


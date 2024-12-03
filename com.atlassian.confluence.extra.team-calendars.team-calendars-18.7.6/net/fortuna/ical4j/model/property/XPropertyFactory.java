/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.property;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.ParameterList;
import net.fortuna.ical4j.model.property.AbstractPropertyFactory;
import net.fortuna.ical4j.model.property.XProperty;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class XPropertyFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public XPropertyFactory() {
        CallSite[] callSiteArray = XPropertyFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = XPropertyFactory.$getCallSiteArray();
        XProperty instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, XProperty.class))) {
            XProperty xProperty;
            instance = xProperty = (XProperty)ScriptBytecodeAdapter.castToType(value, XProperty.class);
        } else {
            String propertyName = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "name"));
            if (ScriptBytecodeAdapter.compareEqual(propertyName, null)) {
                Object object = value;
                propertyName = ShortTypeHandling.castToString(object);
            }
            String propertyValue = ShortTypeHandling.castToString(callSiteArray[2].call((Object)attributes, "value"));
            ParameterList parameters = (ParameterList)ScriptBytecodeAdapter.castToType(callSiteArray[3].call((Object)attributes, "parameters"), ParameterList.class);
            if (ScriptBytecodeAdapter.compareEqual(parameters, null)) {
                Object object = callSiteArray[4].callConstructor(ParameterList.class);
                parameters = (ParameterList)ScriptBytecodeAdapter.castToType(object, ParameterList.class);
            }
            Object object = callSiteArray[5].callStatic(XPropertyFactory.class, propertyName, parameters, propertyValue);
            instance = (XProperty)ScriptBytecodeAdapter.castToType(object, XProperty.class);
        }
        return instance;
    }

    protected static Object newInstance(String name, ParameterList parameters, String value) {
        CallSite[] callSiteArray = XPropertyFactory.$getCallSiteArray();
        return callSiteArray[6].callConstructor(XProperty.class, name, parameters, value);
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = XPropertyFactory.$getCallSiteArray();
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != XPropertyFactory.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ MetaClass super$3$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    public /* synthetic */ Object super$3$newInstance(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2, Map map) {
        return super.newInstance(factoryBuilderSupport, object, object2, map);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "remove";
        stringArray[2] = "remove";
        stringArray[3] = "remove";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "newInstance";
        stringArray[6] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[7];
        XPropertyFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(XPropertyFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = XPropertyFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


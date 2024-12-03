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
import net.fortuna.ical4j.model.property.CalScale;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class CalScaleFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CalScaleFactory() {
        CallSite[] callSiteArray = CalScaleFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = CalScaleFactory.$getCallSiteArray();
        CalScale calScale = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, CalScale.class))) {
            CalScale calScale2;
            calScale = calScale2 = (CalScale)ScriptBytecodeAdapter.castToType(value, CalScale.class);
        } else {
            String calScaleValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(calScaleValue, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGetProperty(CalScale.class)), calScaleValue))) {
                    Object object = callSiteArray[5].callGetProperty(CalScale.class);
                    calScale = (CalScale)ScriptBytecodeAdapter.castToType(object, CalScale.class);
                } else {
                    callSiteArray[6].call(attributes, "value", calScaleValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    calScale = (CalScale)ScriptBytecodeAdapter.castToType(object, CalScale.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(callSiteArray[8].call(callSiteArray[9].callGetProperty(CalScale.class)), value))) {
                Object object = callSiteArray[10].callGetProperty(CalScale.class);
                calScale = (CalScale)ScriptBytecodeAdapter.castToType(object, CalScale.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                calScale = (CalScale)ScriptBytecodeAdapter.castToType(object, CalScale.class);
            }
        }
        return calScale;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = CalScaleFactory.$getCallSiteArray();
        return callSiteArray[11].callConstructor(CalScale.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CalScaleFactory.class) {
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
        stringArray[2] = "equals";
        stringArray[3] = "getValue";
        stringArray[4] = "GREGORIAN";
        stringArray[5] = "GREGORIAN";
        stringArray[6] = "put";
        stringArray[7] = "equals";
        stringArray[8] = "getValue";
        stringArray[9] = "GREGORIAN";
        stringArray[10] = "GREGORIAN";
        stringArray[11] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[12];
        CalScaleFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CalScaleFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CalScaleFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


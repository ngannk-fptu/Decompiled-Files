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
import net.fortuna.ical4j.model.property.Clazz;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ClazzFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ClazzFactory() {
        CallSite[] callSiteArray = ClazzFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ClazzFactory.$getCallSiteArray();
        Clazz clazz = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Clazz.class))) {
            Clazz clazz2;
            clazz = clazz2 = (Clazz)ScriptBytecodeAdapter.castToType(value, Clazz.class);
        } else {
            String clazzValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(clazzValue, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGetProperty(Clazz.class)), clazzValue))) {
                    Object object = callSiteArray[5].callGetProperty(Clazz.class);
                    clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetProperty(Clazz.class)), clazzValue))) {
                    Object object = callSiteArray[9].callGetProperty(Clazz.class);
                    clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].callGetProperty(Clazz.class)), clazzValue))) {
                    Object object = callSiteArray[13].callGetProperty(Clazz.class);
                    clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
                } else {
                    callSiteArray[14].call(attributes, "value", clazzValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call(callSiteArray[16].call(callSiteArray[17].callGetProperty(Clazz.class)), value))) {
                Object object = callSiteArray[18].callGetProperty(Clazz.class);
                clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call(callSiteArray[20].call(callSiteArray[21].callGetProperty(Clazz.class)), value))) {
                Object object = callSiteArray[22].callGetProperty(Clazz.class);
                clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(callSiteArray[24].call(callSiteArray[25].callGetProperty(Clazz.class)), value))) {
                Object object = callSiteArray[26].callGetProperty(Clazz.class);
                clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                clazz = (Clazz)ScriptBytecodeAdapter.castToType(object, Clazz.class);
            }
        }
        return clazz;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = ClazzFactory.$getCallSiteArray();
        return callSiteArray[27].callConstructor(Clazz.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ClazzFactory.class) {
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
        stringArray[4] = "CONFIDENTIAL";
        stringArray[5] = "CONFIDENTIAL";
        stringArray[6] = "equals";
        stringArray[7] = "getValue";
        stringArray[8] = "PRIVATE";
        stringArray[9] = "PRIVATE";
        stringArray[10] = "equals";
        stringArray[11] = "getValue";
        stringArray[12] = "PUBLIC";
        stringArray[13] = "PUBLIC";
        stringArray[14] = "put";
        stringArray[15] = "equals";
        stringArray[16] = "getValue";
        stringArray[17] = "CONFIDENTIAL";
        stringArray[18] = "CONFIDENTIAL";
        stringArray[19] = "equals";
        stringArray[20] = "getValue";
        stringArray[21] = "PRIVATE";
        stringArray[22] = "PRIVATE";
        stringArray[23] = "equals";
        stringArray[24] = "getValue";
        stringArray[25] = "PUBLIC";
        stringArray[26] = "PUBLIC";
        stringArray[27] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[28];
        ClazzFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ClazzFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ClazzFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


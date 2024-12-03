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
import net.fortuna.ical4j.model.property.Action;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class ActionFactory
extends AbstractPropertyFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ActionFactory() {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        Action action = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Action.class))) {
            Action action2;
            action = action2 = (Action)ScriptBytecodeAdapter.castToType(value, Action.class);
        } else {
            String actionValue = ShortTypeHandling.castToString(callSiteArray[1].call((Object)attributes, "value"));
            if (ScriptBytecodeAdapter.compareNotEqual(actionValue, null)) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGetProperty(Action.class)), actionValue))) {
                    Object object = callSiteArray[5].callGetProperty(Action.class);
                    action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetProperty(Action.class)), actionValue))) {
                    Object object = callSiteArray[9].callGetProperty(Action.class);
                    action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].callGetProperty(Action.class)), actionValue))) {
                    Object object = callSiteArray[13].callGetProperty(Action.class);
                    action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
                } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call(callSiteArray[15].call(callSiteArray[16].callGetProperty(Action.class)), actionValue))) {
                    Object object = callSiteArray[17].callGetProperty(Action.class);
                    action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
                } else {
                    callSiteArray[18].call(attributes, "value", actionValue);
                    Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                    action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call(callSiteArray[20].call(callSiteArray[21].callGetProperty(Action.class)), value))) {
                Object object = callSiteArray[22].callGetProperty(Action.class);
                action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call(callSiteArray[24].call(callSiteArray[25].callGetProperty(Action.class)), value))) {
                Object object = callSiteArray[26].callGetProperty(Action.class);
                action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call(callSiteArray[28].call(callSiteArray[29].callGetProperty(Action.class)), value))) {
                Object object = callSiteArray[30].callGetProperty(Action.class);
                action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[31].call(callSiteArray[32].call(callSiteArray[33].callGetProperty(Action.class)), value))) {
                Object object = callSiteArray[34].callGetProperty(Action.class);
                action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
            } else {
                Object object = ScriptBytecodeAdapter.invokeMethodOnSuperN(AbstractPropertyFactory.class, this, "newInstance", new Object[]{builder, name, value, attributes});
                action = (Action)ScriptBytecodeAdapter.castToType(object, Action.class);
            }
        }
        return action;
    }

    @Override
    protected Object newInstance(ParameterList parameters, String value) {
        CallSite[] callSiteArray = ActionFactory.$getCallSiteArray();
        return callSiteArray[35].callConstructor(Action.class, parameters, value);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ActionFactory.class) {
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
        stringArray[4] = "AUDIO";
        stringArray[5] = "AUDIO";
        stringArray[6] = "equals";
        stringArray[7] = "getValue";
        stringArray[8] = "DISPLAY";
        stringArray[9] = "DISPLAY";
        stringArray[10] = "equals";
        stringArray[11] = "getValue";
        stringArray[12] = "EMAIL";
        stringArray[13] = "EMAIL";
        stringArray[14] = "equals";
        stringArray[15] = "getValue";
        stringArray[16] = "PROCEDURE";
        stringArray[17] = "PROCEDURE";
        stringArray[18] = "put";
        stringArray[19] = "equals";
        stringArray[20] = "getValue";
        stringArray[21] = "AUDIO";
        stringArray[22] = "AUDIO";
        stringArray[23] = "equals";
        stringArray[24] = "getValue";
        stringArray[25] = "DISPLAY";
        stringArray[26] = "DISPLAY";
        stringArray[27] = "equals";
        stringArray[28] = "getValue";
        stringArray[29] = "EMAIL";
        stringArray[30] = "EMAIL";
        stringArray[31] = "equals";
        stringArray[32] = "getValue";
        stringArray[33] = "PROCEDURE";
        stringArray[34] = "PROCEDURE";
        stringArray[35] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[36];
        ActionFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ActionFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ActionFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


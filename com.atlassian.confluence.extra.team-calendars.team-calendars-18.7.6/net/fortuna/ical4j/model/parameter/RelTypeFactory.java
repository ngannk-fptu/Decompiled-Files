/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import net.fortuna.ical4j.model.parameter.AbstractParameterFactory;
import net.fortuna.ical4j.model.parameter.RelType;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class RelTypeFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RelTypeFactory() {
        CallSite[] callSiteArray = RelTypeFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = RelTypeFactory.$getCallSiteArray();
        RelType relType = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, RelType.class))) {
            Object object = value;
            relType = (RelType)ScriptBytecodeAdapter.castToType(object, RelType.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(RelType.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(RelType.class);
                relType = (RelType)ScriptBytecodeAdapter.castToType(object2, RelType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(RelType.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(RelType.class);
                relType = (RelType)ScriptBytecodeAdapter.castToType(object3, RelType.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(RelType.class)))) {
                Object object4 = callSiteArray[9].callGetProperty(RelType.class);
                relType = (RelType)ScriptBytecodeAdapter.castToType(object4, RelType.class);
            } else {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value});
                relType = (RelType)ScriptBytecodeAdapter.castToType(list, RelType.class);
            }
        }
        return relType;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RelTypeFactory.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "checkValueIsTypeNotString";
        stringArray[1] = "value";
        stringArray[2] = "CHILD";
        stringArray[3] = "CHILD";
        stringArray[4] = "value";
        stringArray[5] = "PARENT";
        stringArray[6] = "PARENT";
        stringArray[7] = "value";
        stringArray[8] = "SIBLING";
        stringArray[9] = "SIBLING";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[10];
        RelTypeFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RelTypeFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RelTypeFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


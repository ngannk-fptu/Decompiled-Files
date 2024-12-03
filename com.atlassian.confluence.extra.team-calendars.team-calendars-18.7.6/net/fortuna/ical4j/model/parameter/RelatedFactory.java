/*
 * Decompiled with CFR 0.152.
 */
package net.fortuna.ical4j.model.parameter;

import groovy.lang.MetaClass;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Map;
import net.fortuna.ical4j.model.parameter.AbstractParameterFactory;
import net.fortuna.ical4j.model.parameter.Related;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class RelatedFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RelatedFactory() {
        CallSite[] callSiteArray = RelatedFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = RelatedFactory.$getCallSiteArray();
        Related related = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Related.class))) {
            Object object = value;
            related = (Related)ScriptBytecodeAdapter.castToType(object, Related.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(Related.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(Related.class);
                related = (Related)ScriptBytecodeAdapter.castToType(object2, Related.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(Related.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(Related.class);
                related = (Related)ScriptBytecodeAdapter.castToType(object3, Related.class);
            } else {
                Object object4 = callSiteArray[7].callConstructor(Related.class, value);
                related = (Related)ScriptBytecodeAdapter.castToType(object4, Related.class);
            }
        }
        return related;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RelatedFactory.class) {
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
        stringArray[2] = "END";
        stringArray[3] = "END";
        stringArray[4] = "value";
        stringArray[5] = "START";
        stringArray[6] = "START";
        stringArray[7] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[8];
        RelatedFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RelatedFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RelatedFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


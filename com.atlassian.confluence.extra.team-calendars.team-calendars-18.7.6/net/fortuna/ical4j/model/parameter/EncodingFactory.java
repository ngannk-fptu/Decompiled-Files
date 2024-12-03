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
import net.fortuna.ical4j.model.parameter.Encoding;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class EncodingFactory
extends AbstractParameterFactory {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public EncodingFactory() {
        CallSite[] callSiteArray = EncodingFactory.$getCallSiteArray();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object name, Object value, Map attributes) throws InstantiationException, IllegalAccessException {
        CallSite[] callSiteArray = EncodingFactory.$getCallSiteArray();
        Encoding encoding = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(FactoryBuilderSupport.class, value, name, Encoding.class))) {
            Object object = value;
            encoding = (Encoding)ScriptBytecodeAdapter.castToType(object, Encoding.class);
        } else {
            Object object = value;
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(Encoding.class)))) {
                Object object2 = callSiteArray[3].callGetProperty(Encoding.class);
                encoding = (Encoding)ScriptBytecodeAdapter.castToType(object2, Encoding.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(Encoding.class)))) {
                Object object3 = callSiteArray[6].callGetProperty(Encoding.class);
                encoding = (Encoding)ScriptBytecodeAdapter.castToType(object3, Encoding.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(Encoding.class)))) {
                Object object4 = callSiteArray[9].callGetProperty(Encoding.class);
                encoding = (Encoding)ScriptBytecodeAdapter.castToType(object4, Encoding.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[10].callGetProperty(callSiteArray[11].callGetProperty(Encoding.class)))) {
                Object object5 = callSiteArray[12].callGetProperty(Encoding.class);
                encoding = (Encoding)ScriptBytecodeAdapter.castToType(object5, Encoding.class);
            } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[13].callGetProperty(callSiteArray[14].callGetProperty(Encoding.class)))) {
                Object object6 = callSiteArray[15].callGetProperty(Encoding.class);
                encoding = (Encoding)ScriptBytecodeAdapter.castToType(object6, Encoding.class);
            } else {
                List list = ScriptBytecodeAdapter.createList(new Object[]{value});
                encoding = (Encoding)ScriptBytecodeAdapter.castToType(list, Encoding.class);
            }
        }
        return encoding;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != EncodingFactory.class) {
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
        stringArray[2] = "BASE64";
        stringArray[3] = "BASE64";
        stringArray[4] = "value";
        stringArray[5] = "BINARY";
        stringArray[6] = "BINARY";
        stringArray[7] = "value";
        stringArray[8] = "EIGHT_BIT";
        stringArray[9] = "EIGHT_BIT";
        stringArray[10] = "value";
        stringArray[11] = "QUOTED_PRINTABLE";
        stringArray[12] = "QUOTED_PRINTABLE";
        stringArray[13] = "value";
        stringArray[14] = "SEVEN_BIT";
        stringArray[15] = "SEVEN_BIT";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[16];
        EncodingFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(EncodingFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = EncodingFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package groovy.mock.interceptor;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.PropertyAccessInterceptor;
import groovy.mock.interceptor.MockProxyMetaClass;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class MockInterceptor
implements PropertyAccessInterceptor,
GroovyObject {
    private Object expectation;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public MockInterceptor() {
        MetaClass metaClass;
        CallSite[] callSiteArray = MockInterceptor.$getCallSiteArray();
        Object var2_2 = null;
        this.expectation = var2_2;
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object beforeInvoke(Object object, String methodName, Object ... arguments) {
        CallSite[] callSiteArray = MockInterceptor.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(this.expectation)) {
            throw (Throwable)callSiteArray[0].callConstructor(IllegalStateException.class, "Property 'expectation' must be set before use.");
        }
        Object result = callSiteArray[1].call(this.expectation, methodName);
        if (ScriptBytecodeAdapter.compareEqual(result, callSiteArray[2].callGetProperty(MockProxyMetaClass.class))) {
            return result;
        }
        return callSiteArray[3].call(result, ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{arguments}, new int[]{0}));
    }

    @Override
    public Object beforeGet(Object object, String property) {
        CallSite[] callSiteArray = MockInterceptor.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(this.expectation)) {
            throw (Throwable)callSiteArray[4].callConstructor(IllegalStateException.class, "Property 'expectation' must be set before use.");
        }
        String name = ShortTypeHandling.castToString(new GStringImpl(new Object[]{callSiteArray[5].call(callSiteArray[6].call((Object)property, 0)), callSiteArray[7].call((Object)property, ScriptBytecodeAdapter.createRange(1, -1, true))}, new String[]{"get", "", ""}));
        Object result = callSiteArray[8].call(this.expectation, name);
        if (ScriptBytecodeAdapter.compareEqual(result, callSiteArray[9].callGetProperty(MockProxyMetaClass.class))) {
            return result;
        }
        return callSiteArray[10].call(result);
    }

    @Override
    public void beforeSet(Object object, String property, Object newValue) {
        CallSite[] callSiteArray = MockInterceptor.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(this.expectation)) {
            throw (Throwable)callSiteArray[11].callConstructor(IllegalStateException.class, "Property 'expectation' must be set before use.");
        }
        String name = ShortTypeHandling.castToString(new GStringImpl(new Object[]{callSiteArray[12].call(callSiteArray[13].call((Object)property, 0)), callSiteArray[14].call((Object)property, ScriptBytecodeAdapter.createRange(1, -1, true))}, new String[]{"set", "", ""}));
        Object result = callSiteArray[15].call(this.expectation, name);
        if (ScriptBytecodeAdapter.compareNotEqual(result, callSiteArray[16].callGetProperty(MockProxyMetaClass.class))) {
            callSiteArray[17].call(result, newValue);
            Object var7_7 = null;
            result = var7_7;
        }
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (object instanceof Object[]) {
                Object object2 = result;
                callSiteArray[18].call((Object[])ScriptBytecodeAdapter.castToType(object, Object[].class), 0, object2);
            }
        } else if (object instanceof Object[]) {
            Object object3 = result;
            BytecodeInterface8.objectArraySet((Object[])ScriptBytecodeAdapter.castToType(object, Object[].class), 0, object3);
        }
    }

    @Override
    public Object afterInvoke(Object object, String methodName, Object[] arguments, Object result) {
        CallSite[] callSiteArray = MockInterceptor.$getCallSiteArray();
        return null;
    }

    @Override
    public boolean doInvoke() {
        CallSite[] callSiteArray = MockInterceptor.$getCallSiteArray();
        return false;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != MockInterceptor.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public /* synthetic */ MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public /* synthetic */ void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public /* synthetic */ Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public Object getExpectation() {
        return this.expectation;
    }

    public void setExpectation(Object object) {
        this.expectation = object;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "match";
        stringArray[2] = "FALL_THROUGH_MARKER";
        stringArray[3] = "call";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "toUpperCase";
        stringArray[6] = "getAt";
        stringArray[7] = "getAt";
        stringArray[8] = "match";
        stringArray[9] = "FALL_THROUGH_MARKER";
        stringArray[10] = "call";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "toUpperCase";
        stringArray[13] = "getAt";
        stringArray[14] = "getAt";
        stringArray[15] = "match";
        stringArray[16] = "FALL_THROUGH_MARKER";
        stringArray[17] = "call";
        stringArray[18] = "putAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[19];
        MockInterceptor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(MockInterceptor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = MockInterceptor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


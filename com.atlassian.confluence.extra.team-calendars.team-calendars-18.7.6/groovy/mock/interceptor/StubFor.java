/*
 * Decompiled with CFR 0.152.
 */
package groovy.mock.interceptor;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.mock.interceptor.Demand;
import groovy.mock.interceptor.Ignore;
import groovy.mock.interceptor.LooseExpectation;
import groovy.mock.interceptor.MockFor;
import groovy.mock.interceptor.MockInterceptor;
import groovy.mock.interceptor.MockProxyMetaClass;
import groovy.util.ProxyGenerator;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class StubFor
implements GroovyObject {
    private MockProxyMetaClass proxy;
    private Demand demand;
    private Ignore ignore;
    private Object expect;
    private Map instanceExpectations;
    private Class clazz;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StubFor(Class clazz, boolean interceptConstruction) {
        Object object;
        MetaClass metaClass;
        Map map;
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        this.instanceExpectations = map = ScriptBytecodeAdapter.createMap(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (!BytecodeInterface8.isOrigZ() || BytecodeInterface8.disabledStandardMetaClass()) {
            if (interceptConstruction && !DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(GroovyObject.class, clazz))) {
                throw (Throwable)callSiteArray[1].callConstructor(IllegalArgumentException.class, callSiteArray[2].call((Object)"StubFor with constructor interception enabled is only allowed for Groovy objects but found: ", callSiteArray[3].callGetProperty(clazz)));
            }
        } else if (interceptConstruction && !DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(GroovyObject.class, clazz))) {
            throw (Throwable)callSiteArray[5].callConstructor(IllegalArgumentException.class, callSiteArray[6].call((Object)"StubFor with constructor interception enabled is only allowed for Groovy objects but found: ", callSiteArray[7].callGetProperty(clazz)));
        }
        Class clazz2 = clazz;
        this.clazz = ShortTypeHandling.castToClass(clazz2);
        Object object2 = callSiteArray[8].call(MockProxyMetaClass.class, clazz, interceptConstruction);
        this.proxy = (MockProxyMetaClass)ScriptBytecodeAdapter.castToType(object2, MockProxyMetaClass.class);
        Object object3 = callSiteArray[9].callConstructor(Demand.class);
        this.demand = (Demand)ScriptBytecodeAdapter.castToType(object3, Demand.class);
        Object object4 = callSiteArray[10].callConstructor(Ignore.class, ScriptBytecodeAdapter.createMap(new Object[]{"parent", this}));
        this.ignore = (Ignore)ScriptBytecodeAdapter.castToType(object4, Ignore.class);
        this.expect = object = callSiteArray[11].callConstructor(LooseExpectation.class, this.demand);
        Object object5 = callSiteArray[12].callConstructor(MockInterceptor.class, ScriptBytecodeAdapter.createMap(new Object[]{"expectation", this.expect}));
        ScriptBytecodeAdapter.setProperty(object5, null, this.proxy, "interceptor");
    }

    public StubFor(Class clazz) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        this(clazz, false);
    }

    public void use(Closure closure) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        callSiteArray[13].call((Object)this.proxy, closure);
    }

    public void use(GroovyObject obj, Closure closure) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        callSiteArray[14].call(this.proxy, obj, closure);
    }

    public void verify(GroovyObject obj) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        callSiteArray[15].call(callSiteArray[16].call((Object)this.instanceExpectations, obj));
    }

    public void verify() {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        callSiteArray[17].call(this.expect);
    }

    public Object ignore(Object filter, Closure filterBehavior) {
        Closure closure;
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[18].callGetProperty(this.clazz), "java.lang.String") && filter instanceof String) {
            Object object;
            filter = object = callSiteArray[19].call(Pattern.class, filter);
        }
        return callSiteArray[20].call(callSiteArray[21].callGroovyObjectGetProperty(this.demand), filter, DefaultTypeTransformation.booleanUnbox(closure = filterBehavior) ? closure : callSiteArray[22].callGetProperty(MockProxyMetaClass.class));
    }

    public GroovyObject proxyInstance(Object args) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        return (GroovyObject)ScriptBytecodeAdapter.castToType(callSiteArray[23].callCurrent(this, args, false), GroovyObject.class);
    }

    public GroovyObject proxyDelegateInstance(Object args) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        return (GroovyObject)ScriptBytecodeAdapter.castToType(callSiteArray[24].callCurrent(this, args, true), GroovyObject.class);
    }

    public GroovyObject makeProxyInstance(Object args, boolean isDelegate) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        Object instance = callSiteArray[25].call(MockFor.class, this.clazz, args);
        Object thisproxy = callSiteArray[26].call(MockProxyMetaClass.class, isDelegate ? callSiteArray[27].call(instance) : this.clazz);
        Object thisdemand = callSiteArray[28].callConstructor(Demand.class, ScriptBytecodeAdapter.createMap(new Object[]{"recorded", callSiteArray[29].callConstructor(ArrayList.class, callSiteArray[30].callGroovyObjectGetProperty(this.demand)), "ignore", callSiteArray[31].callConstructor(HashMap.class, callSiteArray[32].callGroovyObjectGetProperty(this.demand))}));
        Object thisexpect = callSiteArray[33].callConstructor(LooseExpectation.class, thisdemand);
        Object object = callSiteArray[34].callConstructor(MockInterceptor.class, ScriptBytecodeAdapter.createMap(new Object[]{"expectation", thisexpect}));
        ScriptBytecodeAdapter.setProperty(object, null, thisproxy, "interceptor");
        Object object2 = thisproxy;
        ScriptBytecodeAdapter.setProperty(object2, null, instance, "metaClass");
        Object wrapped = instance;
        if (isDelegate && DefaultTypeTransformation.booleanUnbox(callSiteArray[35].call(this.clazz))) {
            Object object3;
            wrapped = object3 = callSiteArray[36].call(callSiteArray[37].callGetProperty(ProxyGenerator.class), ScriptBytecodeAdapter.createList(new Object[]{this.clazz}), instance);
        }
        Object object4 = thisexpect;
        callSiteArray[38].call(this.instanceExpectations, wrapped, object4);
        return (GroovyObject)ScriptBytecodeAdapter.castToType(wrapped, GroovyObject.class);
    }

    public Object ignore(Object filter) {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        return this.ignore(filter, null);
    }

    public GroovyObject proxyInstance() {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        return this.proxyInstance(null);
    }

    public GroovyObject proxyDelegateInstance() {
        CallSite[] callSiteArray = StubFor.$getCallSiteArray();
        return this.proxyDelegateInstance(null);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StubFor.class) {
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

    public MockProxyMetaClass getProxy() {
        return this.proxy;
    }

    public void setProxy(MockProxyMetaClass mockProxyMetaClass) {
        this.proxy = mockProxyMetaClass;
    }

    public Demand getDemand() {
        return this.demand;
    }

    public void setDemand(Demand demand) {
        this.demand = demand;
    }

    public Ignore getIgnore() {
        return this.ignore;
    }

    public void setIgnore(Ignore ignore) {
        this.ignore = ignore;
    }

    public Object getExpect() {
        return this.expect;
    }

    public void setExpect(Object object) {
        this.expect = object;
    }

    public Map getInstanceExpectations() {
        return this.instanceExpectations;
    }

    public void setInstanceExpectations(Map map) {
        this.instanceExpectations = map;
    }

    public Class getClazz() {
        return this.clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "isAssignableFrom";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "plus";
        stringArray[3] = "name";
        stringArray[4] = "isAssignableFrom";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "plus";
        stringArray[7] = "name";
        stringArray[8] = "make";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "use";
        stringArray[14] = "use";
        stringArray[15] = "verify";
        stringArray[16] = "getAt";
        stringArray[17] = "verify";
        stringArray[18] = "name";
        stringArray[19] = "compile";
        stringArray[20] = "put";
        stringArray[21] = "ignore";
        stringArray[22] = "FALL_THROUGH_MARKER";
        stringArray[23] = "makeProxyInstance";
        stringArray[24] = "makeProxyInstance";
        stringArray[25] = "getInstance";
        stringArray[26] = "make";
        stringArray[27] = "getClass";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "<$constructor$>";
        stringArray[30] = "recorded";
        stringArray[31] = "<$constructor$>";
        stringArray[32] = "ignore";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "isInterface";
        stringArray[36] = "instantiateDelegate";
        stringArray[37] = "INSTANCE";
        stringArray[38] = "putAt";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[39];
        StubFor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StubFor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StubFor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


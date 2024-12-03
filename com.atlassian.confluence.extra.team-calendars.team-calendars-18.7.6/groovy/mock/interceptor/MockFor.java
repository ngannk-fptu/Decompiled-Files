/*
 * Decompiled with CFR 0.152.
 */
package groovy.mock.interceptor;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.mock.interceptor.Demand;
import groovy.mock.interceptor.Ignore;
import groovy.mock.interceptor.MockInterceptor;
import groovy.mock.interceptor.MockProxyMetaClass;
import groovy.mock.interceptor.StrictExpectation;
import groovy.util.ProxyGenerator;
import java.lang.ref.SoftReference;
import java.lang.reflect.Modifier;
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

public class MockFor
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

    public MockFor(Class clazz, boolean interceptConstruction) {
        Object object;
        MetaClass metaClass;
        Map map;
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        this.instanceExpectations = map = ScriptBytecodeAdapter.createMap(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (!BytecodeInterface8.isOrigZ() || BytecodeInterface8.disabledStandardMetaClass()) {
            if (interceptConstruction && !DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(GroovyObject.class, clazz))) {
                throw (Throwable)callSiteArray[1].callConstructor(IllegalArgumentException.class, callSiteArray[2].call((Object)"MockFor with constructor interception enabled is only allowed for Groovy objects but found: ", callSiteArray[3].callGetProperty(clazz)));
            }
        } else if (interceptConstruction && !DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(GroovyObject.class, clazz))) {
            throw (Throwable)callSiteArray[5].callConstructor(IllegalArgumentException.class, callSiteArray[6].call((Object)"MockFor with constructor interception enabled is only allowed for Groovy objects but found: ", callSiteArray[7].callGetProperty(clazz)));
        }
        Class clazz2 = clazz;
        this.clazz = ShortTypeHandling.castToClass(clazz2);
        Object object2 = callSiteArray[8].call(MockProxyMetaClass.class, clazz, interceptConstruction);
        this.proxy = (MockProxyMetaClass)ScriptBytecodeAdapter.castToType(object2, MockProxyMetaClass.class);
        Object object3 = callSiteArray[9].callConstructor(Demand.class);
        this.demand = (Demand)ScriptBytecodeAdapter.castToType(object3, Demand.class);
        Object object4 = callSiteArray[10].callConstructor(Ignore.class, ScriptBytecodeAdapter.createMap(new Object[]{"parent", this}));
        this.ignore = (Ignore)ScriptBytecodeAdapter.castToType(object4, Ignore.class);
        this.expect = object = callSiteArray[11].callConstructor(StrictExpectation.class, this.demand);
        Object object5 = callSiteArray[12].callConstructor(MockInterceptor.class, ScriptBytecodeAdapter.createMap(new Object[]{"expectation", this.expect}));
        ScriptBytecodeAdapter.setProperty(object5, null, this.proxy, "interceptor");
    }

    public MockFor(Class clazz) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        this(clazz, false);
    }

    public void use(Closure closure) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        callSiteArray[13].call((Object)this.proxy, closure);
        callSiteArray[14].call(this.expect);
    }

    public void use(GroovyObject obj, Closure closure) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        callSiteArray[15].call(this.proxy, obj, closure);
        callSiteArray[16].call(this.expect);
    }

    public void verify(GroovyObject obj) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        callSiteArray[17].call(callSiteArray[18].call((Object)this.instanceExpectations, obj));
    }

    public Object ignore(Object filter, Closure filterBehavior) {
        Closure closure;
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[19].callGetProperty(this.clazz), "java.lang.String") && filter instanceof String) {
            Object object;
            filter = object = callSiteArray[20].call(Pattern.class, filter);
        }
        return callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(this.demand), filter, DefaultTypeTransformation.booleanUnbox(closure = filterBehavior) ? closure : callSiteArray[23].callGetProperty(MockProxyMetaClass.class));
    }

    public GroovyObject proxyInstance(Object args) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        return (GroovyObject)ScriptBytecodeAdapter.castToType(callSiteArray[24].callCurrent(this, args, false), GroovyObject.class);
    }

    public GroovyObject proxyDelegateInstance(Object args) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        return (GroovyObject)ScriptBytecodeAdapter.castToType(callSiteArray[25].callCurrent(this, args, true), GroovyObject.class);
    }

    public GroovyObject makeProxyInstance(Object args, boolean isDelegate) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        Object instance = callSiteArray[26].callStatic(MockFor.class, this.clazz, args);
        Object thisproxy = callSiteArray[27].call(MockProxyMetaClass.class, isDelegate ? callSiteArray[28].call(instance) : this.clazz);
        Object thisdemand = callSiteArray[29].callConstructor(Demand.class, ScriptBytecodeAdapter.createMap(new Object[]{"recorded", callSiteArray[30].callConstructor(ArrayList.class, callSiteArray[31].callGroovyObjectGetProperty(this.demand)), "ignore", callSiteArray[32].callConstructor(HashMap.class, callSiteArray[33].callGroovyObjectGetProperty(this.demand))}));
        Object thisexpect = callSiteArray[34].callConstructor(StrictExpectation.class, thisdemand);
        Object object = callSiteArray[35].callConstructor(MockInterceptor.class, ScriptBytecodeAdapter.createMap(new Object[]{"expectation", thisexpect}));
        ScriptBytecodeAdapter.setProperty(object, null, thisproxy, "interceptor");
        Object object2 = thisproxy;
        ScriptBytecodeAdapter.setProperty(object2, null, instance, "metaClass");
        Object wrapped = instance;
        if (isDelegate && DefaultTypeTransformation.booleanUnbox(callSiteArray[36].call(this.clazz))) {
            Object object3;
            wrapped = object3 = callSiteArray[37].call(callSiteArray[38].callGetProperty(ProxyGenerator.class), ScriptBytecodeAdapter.createList(new Object[]{this.clazz}), instance);
        }
        Object object4 = thisexpect;
        callSiteArray[39].call(this.instanceExpectations, wrapped, object4);
        return (GroovyObject)ScriptBytecodeAdapter.castToType(wrapped, GroovyObject.class);
    }

    public static GroovyObject getInstance(Class clazz, Object args) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        GroovyObject instance = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[40].call(clazz))) {
            Object object = callSiteArray[41].call(callSiteArray[42].callGetProperty(ProxyGenerator.class), clazz);
            instance = (GroovyObject)ScriptBytecodeAdapter.castToType(object, GroovyObject.class);
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[43].call(Modifier.class, callSiteArray[44].callGetProperty(clazz)))) {
            Object object = callSiteArray[45].call(callSiteArray[46].callGetProperty(ProxyGenerator.class), clazz, args);
            instance = (GroovyObject)ScriptBytecodeAdapter.castToType(object, GroovyObject.class);
        } else if (ScriptBytecodeAdapter.compareNotEqual(args, null)) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[47].call(GroovyObject.class, clazz))) {
                Object object = callSiteArray[48].call((Object)clazz, args);
                instance = (GroovyObject)ScriptBytecodeAdapter.castToType(object, GroovyObject.class);
            } else {
                Object object = callSiteArray[49].call(callSiteArray[50].callGetProperty(ProxyGenerator.class), callSiteArray[51].call((Object)clazz, args));
                instance = (GroovyObject)ScriptBytecodeAdapter.castToType(object, GroovyObject.class);
            }
        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[52].call(GroovyObject.class, clazz))) {
            Object object = callSiteArray[53].call(clazz);
            instance = (GroovyObject)ScriptBytecodeAdapter.castToType(object, GroovyObject.class);
        } else {
            Object object = callSiteArray[54].call(callSiteArray[55].callGetProperty(ProxyGenerator.class), callSiteArray[56].call(clazz));
            instance = (GroovyObject)ScriptBytecodeAdapter.castToType(object, GroovyObject.class);
        }
        return instance;
    }

    public Object ignore(Object filter) {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        return this.ignore(filter, null);
    }

    public GroovyObject proxyInstance() {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        return this.proxyInstance(null);
    }

    public GroovyObject proxyDelegateInstance() {
        CallSite[] callSiteArray = MockFor.$getCallSiteArray();
        return this.proxyDelegateInstance(null);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != MockFor.class) {
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
        stringArray[14] = "verify";
        stringArray[15] = "use";
        stringArray[16] = "verify";
        stringArray[17] = "verify";
        stringArray[18] = "getAt";
        stringArray[19] = "name";
        stringArray[20] = "compile";
        stringArray[21] = "put";
        stringArray[22] = "ignore";
        stringArray[23] = "FALL_THROUGH_MARKER";
        stringArray[24] = "makeProxyInstance";
        stringArray[25] = "makeProxyInstance";
        stringArray[26] = "getInstance";
        stringArray[27] = "make";
        stringArray[28] = "getClass";
        stringArray[29] = "<$constructor$>";
        stringArray[30] = "<$constructor$>";
        stringArray[31] = "recorded";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "ignore";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "<$constructor$>";
        stringArray[36] = "isInterface";
        stringArray[37] = "instantiateDelegate";
        stringArray[38] = "INSTANCE";
        stringArray[39] = "putAt";
        stringArray[40] = "isInterface";
        stringArray[41] = "instantiateAggregateFromInterface";
        stringArray[42] = "INSTANCE";
        stringArray[43] = "isAbstract";
        stringArray[44] = "modifiers";
        stringArray[45] = "instantiateAggregateFromBaseClass";
        stringArray[46] = "INSTANCE";
        stringArray[47] = "isAssignableFrom";
        stringArray[48] = "newInstance";
        stringArray[49] = "instantiateDelegate";
        stringArray[50] = "INSTANCE";
        stringArray[51] = "newInstance";
        stringArray[52] = "isAssignableFrom";
        stringArray[53] = "newInstance";
        stringArray[54] = "instantiateDelegate";
        stringArray[55] = "INSTANCE";
        stringArray[56] = "newInstance";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[57];
        MockFor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(MockFor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = MockFor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


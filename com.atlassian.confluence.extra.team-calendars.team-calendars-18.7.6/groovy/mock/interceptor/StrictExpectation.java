/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.AssertionFailedError
 */
package groovy.mock.interceptor;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.mock.interceptor.Demand;
import java.lang.ref.SoftReference;
import java.util.List;
import junit.framework.AssertionFailedError;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class StrictExpectation
implements GroovyObject {
    private Demand fDemand;
    private int fCallSpecIdx;
    private List fCalls;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public StrictExpectation(Demand demand) {
        Demand demand2;
        MetaClass metaClass;
        List list;
        int n;
        CallSite[] callSiteArray = StrictExpectation.$getCallSiteArray();
        Object var3_3 = null;
        this.fDemand = (Demand)ScriptBytecodeAdapter.castToType(var3_3, Demand.class);
        this.fCallSpecIdx = n = 0;
        this.fCalls = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.fDemand = demand2 = demand;
    }

    public Closure match(String name) {
        Reference<String> name2 = new Reference<String>(name);
        CallSite[] callSiteArray = StrictExpectation.$getCallSiteArray();
        public class _match_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference name;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _match_closure1(Object _outerInstance, Object _thisObject, Reference name) {
                Reference reference;
                CallSite[] callSiteArray = _match_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.name = reference = name;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _match_closure1.$getCallSiteArray();
                return callSiteArray[0].call(DefaultGroovyMethods.class, ScriptBytecodeAdapter.createList(new Object[]{this.name.get()}), it);
            }

            public String getName() {
                CallSite[] callSiteArray = _match_closure1.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.name.get());
            }

            public Object doCall() {
                CallSite[] callSiteArray = _match_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _match_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "grep";
                return new CallSiteArray(_match_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _match_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object filter = callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this.fDemand)), new _match_closure1(this, this, name2));
        if (DefaultTypeTransformation.booleanUnbox(filter)) {
            return (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this.fDemand), filter), Closure.class);
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call((Object)this.fCalls, this.fCallSpecIdx))) {
            int n = 0;
            callSiteArray[6].call(this.fCalls, this.fCallSpecIdx, n);
        }
        if (ScriptBytecodeAdapter.compareGreaterThanEqual(this.fCallSpecIdx, callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty(this.fDemand)))) {
            throw (Throwable)callSiteArray[9].callConstructor(AssertionFailedError.class, new GStringImpl(new Object[]{name2.get()}, new String[]{"No more calls to '", "' expected at this point. End of demands."}));
        }
        Object call = callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(this.fDemand), this.fCallSpecIdx);
        if (ScriptBytecodeAdapter.compareNotEqual(name2.get(), callSiteArray[12].callGetProperty(call))) {
            Object open = callSiteArray[13].call(callSiteArray[14].callGetProperty(callSiteArray[15].callGetProperty(call)), callSiteArray[16].call((Object)this.fCalls, this.fCallSpecIdx));
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareGreaterThan(open, 0)) {
                    throw (Throwable)callSiteArray[17].callConstructor(AssertionFailedError.class, callSiteArray[18].call((Object)new GStringImpl(new Object[]{name2.get()}, new String[]{"No call to '", "' expected at this point. "}), new GStringImpl(new Object[]{open, callSiteArray[19].callGetProperty(call)}, new String[]{"Still ", " call(s) to '", "' expected."})));
                }
                int n = this.fCallSpecIdx;
                this.fCallSpecIdx = DefaultTypeTransformation.intUnbox(callSiteArray[20].call(n));
                return (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[21].callCurrent((GroovyObject)this, name2.get()), Closure.class);
            }
            if (ScriptBytecodeAdapter.compareGreaterThan(open, 0)) {
                throw (Throwable)callSiteArray[22].callConstructor(AssertionFailedError.class, callSiteArray[23].call((Object)new GStringImpl(new Object[]{name2.get()}, new String[]{"No call to '", "' expected at this point. "}), new GStringImpl(new Object[]{open, callSiteArray[24].callGetProperty(call)}, new String[]{"Still ", " call(s) to '", "' expected."})));
            }
            int n = this.fCallSpecIdx;
            this.fCallSpecIdx = n + 1;
            return this.match(name2.get());
        }
        List list = this.fCalls;
        int n = this.fCallSpecIdx;
        Object object = callSiteArray[26].call(callSiteArray[27].call((Object)list, n), 1);
        callSiteArray[25].call(list, n, object);
        Object result = callSiteArray[28].callGetProperty(call);
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[29].call((Object)this.fCalls, this.fCallSpecIdx), callSiteArray[30].callGetProperty(callSiteArray[31].callGetProperty(call)))) {
                int n2 = this.fCallSpecIdx;
                this.fCallSpecIdx = DefaultTypeTransformation.intUnbox(callSiteArray[32].call(n2));
            }
        } else if (ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[33].call((Object)this.fCalls, this.fCallSpecIdx), callSiteArray[34].callGetProperty(callSiteArray[35].callGetProperty(call)))) {
            int n3 = this.fCallSpecIdx;
            this.fCallSpecIdx = n3 + 1;
        }
        return (Closure)ScriptBytecodeAdapter.castToType(result, Closure.class);
    }

    public void verify() {
        CallSite[] callSiteArray = StrictExpectation.$getCallSiteArray();
        callSiteArray[36].call((Object)this.fDemand, this.fCalls);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != StrictExpectation.class) {
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

    public Demand getfDemand() {
        return this.fDemand;
    }

    public void setfDemand(Demand demand) {
        this.fDemand = demand;
    }

    public int getfCallSpecIdx() {
        return this.fCallSpecIdx;
    }

    public void setfCallSpecIdx(int n) {
        this.fCallSpecIdx = n;
    }

    public List getfCalls() {
        return this.fCalls;
    }

    public void setfCalls(List list) {
        this.fCalls = list;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "find";
        stringArray[1] = "keySet";
        stringArray[2] = "ignore";
        stringArray[3] = "get";
        stringArray[4] = "ignore";
        stringArray[5] = "getAt";
        stringArray[6] = "putAt";
        stringArray[7] = "size";
        stringArray[8] = "recorded";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "getAt";
        stringArray[11] = "recorded";
        stringArray[12] = "name";
        stringArray[13] = "minus";
        stringArray[14] = "from";
        stringArray[15] = "range";
        stringArray[16] = "getAt";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "plus";
        stringArray[19] = "name";
        stringArray[20] = "next";
        stringArray[21] = "match";
        stringArray[22] = "<$constructor$>";
        stringArray[23] = "plus";
        stringArray[24] = "name";
        stringArray[25] = "putAt";
        stringArray[26] = "plus";
        stringArray[27] = "getAt";
        stringArray[28] = "behavior";
        stringArray[29] = "getAt";
        stringArray[30] = "to";
        stringArray[31] = "range";
        stringArray[32] = "next";
        stringArray[33] = "getAt";
        stringArray[34] = "to";
        stringArray[35] = "range";
        stringArray[36] = "verify";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[37];
        StrictExpectation.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(StrictExpectation.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = StrictExpectation.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


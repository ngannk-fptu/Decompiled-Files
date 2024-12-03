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
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class LooseExpectation
implements GroovyObject {
    private Demand fDemand;
    private List fCalls;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public LooseExpectation(Demand demand) {
        Demand demand2;
        MetaClass metaClass;
        List list;
        CallSite[] callSiteArray = LooseExpectation.$getCallSiteArray();
        Object var3_3 = null;
        this.fDemand = (Demand)ScriptBytecodeAdapter.castToType(var3_3, Demand.class);
        this.fCalls = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        this.fDemand = demand2 = demand;
    }

    public Closure match(String name) {
        Reference<String> name2 = new Reference<String>(name);
        CallSite[] callSiteArray = LooseExpectation.$getCallSiteArray();
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
        Integer callIndex = 0;
        while (!DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callCurrent(this, name2.get(), callIndex))) {
            Integer n = callIndex;
            callSiteArray[6].call(n);
        }
        List list = this.fCalls;
        Integer n = callIndex;
        Object object = callSiteArray[8].call(callSiteArray[9].call((Object)list, n), 1);
        callSiteArray[7].call(list, n, object);
        return (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[10].callGetProperty(callSiteArray[11].call(callSiteArray[12].callGroovyObjectGetProperty(this.fDemand), callIndex)), Closure.class);
    }

    public boolean isEligible(String name, int i) {
        CallSite[] callSiteArray = LooseExpectation.$getCallSiteArray();
        Object calls = callSiteArray[13].callGroovyObjectGetProperty(this.fDemand);
        if (ScriptBytecodeAdapter.compareGreaterThanEqual(i, callSiteArray[14].call(calls))) {
            throw (Throwable)callSiteArray[15].callConstructor(AssertionFailedError.class, new GStringImpl(new Object[]{name}, new String[]{"No more calls to '", "' expected at this point. End of demands."}));
        }
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[16].callGetProperty(callSiteArray[17].call(calls, i)), name)) {
            return false;
        }
        if (ScriptBytecodeAdapter.compareEqual(null, callSiteArray[18].call((Object)this.fCalls, i))) {
            int n = 0;
            callSiteArray[19].call(this.fCalls, i, n);
        }
        return !ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[20].call((Object)this.fCalls, i), callSiteArray[21].callGetProperty(callSiteArray[22].callGetProperty(callSiteArray[23].call(calls, i))));
    }

    public void verify() {
        CallSite[] callSiteArray = LooseExpectation.$getCallSiteArray();
        callSiteArray[24].call((Object)this.fDemand, this.fCalls);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != LooseExpectation.class) {
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
        stringArray[5] = "isEligible";
        stringArray[6] = "next";
        stringArray[7] = "putAt";
        stringArray[8] = "plus";
        stringArray[9] = "getAt";
        stringArray[10] = "behavior";
        stringArray[11] = "getAt";
        stringArray[12] = "recorded";
        stringArray[13] = "recorded";
        stringArray[14] = "size";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "name";
        stringArray[17] = "getAt";
        stringArray[18] = "getAt";
        stringArray[19] = "putAt";
        stringArray[20] = "getAt";
        stringArray[21] = "to";
        stringArray[22] = "range";
        stringArray[23] = "getAt";
        stringArray[24] = "verify";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[25];
        LooseExpectation.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(LooseExpectation.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = LooseExpectation.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


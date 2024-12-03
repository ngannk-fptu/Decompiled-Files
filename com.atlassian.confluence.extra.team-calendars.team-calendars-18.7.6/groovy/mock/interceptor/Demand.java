/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  junit.framework.AssertionFailedError
 */
package groovy.mock.interceptor;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.IntRange;
import groovy.lang.MetaClass;
import groovy.mock.interceptor.CallSpec;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import junit.framework.AssertionFailedError;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class Demand
implements GroovyObject {
    private List recorded;
    private Map ignore;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Demand() {
        MetaClass metaClass;
        Map map;
        List list;
        CallSite[] callSiteArray = Demand.$getCallSiteArray();
        this.recorded = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.ignore = map = ScriptBytecodeAdapter.createMap(new Object[0]);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object invokeMethod(String methodName, Object args) {
        CallSite[] callSiteArray = Demand.$getCallSiteArray();
        Object range = ScriptBytecodeAdapter.createRange(1, 1, true);
        if (callSiteArray[0].call(args, 0) instanceof IntRange) {
            Object object;
            range = object = callSiteArray[1].call(args, 0);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].callGetProperty(range))) {
                throw (Throwable)callSiteArray[3].callConstructor(IllegalArgumentException.class, "Reverse ranges not supported.");
            }
        } else if (callSiteArray[4].call(args, 0) instanceof Integer) {
            List list = ScriptBytecodeAdapter.createRange(callSiteArray[5].call(args, 0), callSiteArray[6].call(args, 0), true);
            range = list;
        }
        if (callSiteArray[7].call(args, -1) instanceof Closure) {
            return callSiteArray[8].call((Object)this.recorded, callSiteArray[9].callConstructor(CallSpec.class, ScriptBytecodeAdapter.createMap(new Object[]{"name", methodName, "behavior", callSiteArray[10].call(args, -1), "range", range})));
        }
        return null;
    }

    public Object verify(List calls) {
        CallSite[] callSiteArray = Demand.$getCallSiteArray();
        Object i = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[11].call(ScriptBytecodeAdapter.createRange(0, callSiteArray[12].call(this.recorded), false)), Iterator.class);
        while (iterator.hasNext()) {
            i = iterator.next();
            Object call = callSiteArray[13].call((Object)this.recorded, (Object)i);
            Integer callCounter = DefaultTypeTransformation.booleanUnbox(callSiteArray[14].call((Object)calls, (Object)i)) ? callSiteArray[15].call((Object)calls, (Object)i) : Integer.valueOf(0);
            if (!(!DefaultTypeTransformation.booleanUnbox(callSiteArray[16].call(callSiteArray[17].callGetProperty(call), callCounter)))) continue;
            GStringImpl msg = new GStringImpl(new Object[]{i, callSiteArray[18].call(callSiteArray[19].callGetProperty(call)), callSiteArray[20].callGetProperty(call)}, new String[]{"verify[", "]: expected ", " call(s) to '", "' but was "});
            throw (Throwable)callSiteArray[21].callConstructor(AssertionFailedError.class, callSiteArray[22].call((Object)msg, new GStringImpl(new Object[]{callCounter}, new String[]{"called ", " time(s)."})));
        }
        return null;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Demand.class) {
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
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public List getRecorded() {
        return this.recorded;
    }

    public void setRecorded(List list) {
        this.recorded = list;
    }

    public Map getIgnore() {
        return this.ignore;
    }

    public void setIgnore(Map map) {
        this.ignore = map;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getAt";
        stringArray[1] = "getAt";
        stringArray[2] = "reverse";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "getAt";
        stringArray[5] = "getAt";
        stringArray[6] = "getAt";
        stringArray[7] = "getAt";
        stringArray[8] = "leftShift";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "getAt";
        stringArray[11] = "iterator";
        stringArray[12] = "size";
        stringArray[13] = "getAt";
        stringArray[14] = "getAt";
        stringArray[15] = "getAt";
        stringArray[16] = "contains";
        stringArray[17] = "range";
        stringArray[18] = "toString";
        stringArray[19] = "range";
        stringArray[20] = "name";
        stringArray[21] = "<$constructor$>";
        stringArray[22] = "plus";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[23];
        Demand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Demand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Demand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


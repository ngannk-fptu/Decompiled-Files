/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilder;
import groovy.jmx.builder.JmxBuilderException;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.AbstractFactory;
import groovy.util.FactoryBuilderSupport;
import groovy.util.GroovyMBean;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;
import javax.management.timer.Timer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class JmxTimerFactory
extends AbstractFactory
implements GroovyObject {
    private static final /* synthetic */ long $const$0;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxTimerFactory() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object newInstance(FactoryBuilderSupport builder, Object nodeName, Object nodeParam, Map nodeAttribs) {
        Object object;
        Object object2;
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        if (DefaultTypeTransformation.booleanUnbox(nodeParam)) {
            throw (Throwable)callSiteArray[0].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{nodeName}, new String[]{"Node '", "' only supports named attributes."}));
        }
        JmxBuilder fsb = (JmxBuilder)ScriptBytecodeAdapter.castToType(builder, JmxBuilder.class);
        Timer timer = (Timer)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(Timer.class), Timer.class);
        Map metaMap = ScriptBytecodeAdapter.createMap(new Object[0]);
        Object object3 = callSiteArray[2].call(fsb);
        ScriptBytecodeAdapter.setProperty(object3, null, metaMap, "server");
        Timer timer2 = timer;
        ScriptBytecodeAdapter.setProperty(timer2, null, metaMap, "timer");
        Object object4 = callSiteArray[3].call((Object)nodeAttribs, "name");
        ScriptBytecodeAdapter.setProperty(object4, null, metaMap, "name");
        Object object5 = callSiteArray[4].call((Object)nodeAttribs, "event");
        Object object6 = DefaultTypeTransformation.booleanUnbox(object5) ? object5 : (DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[5].call((Object)nodeAttribs, "type")) ? object2 : "jmx.builder.event");
        ScriptBytecodeAdapter.setProperty(object6, null, metaMap, "event");
        Object object7 = callSiteArray[6].call((Object)nodeAttribs, "message");
        Object object8 = DefaultTypeTransformation.booleanUnbox(object7) ? object7 : callSiteArray[7].call((Object)nodeAttribs, "msg");
        ScriptBytecodeAdapter.setProperty(object8, null, metaMap, "message");
        Object object9 = callSiteArray[8].call((Object)nodeAttribs, "data");
        Object object10 = DefaultTypeTransformation.booleanUnbox(object9) ? object9 : callSiteArray[9].call((Object)nodeAttribs, "userData");
        ScriptBytecodeAdapter.setProperty(object10, null, metaMap, "data");
        Object object11 = callSiteArray[10].call((Object)nodeAttribs, "date");
        Object object12 = DefaultTypeTransformation.booleanUnbox(object11) ? object11 : callSiteArray[11].call((Object)nodeAttribs, "startDate");
        ScriptBytecodeAdapter.setProperty(object12, null, metaMap, "date");
        Object object13 = callSiteArray[12].call((Object)nodeAttribs, "period");
        Object object14 = DefaultTypeTransformation.booleanUnbox(object13) ? object13 : callSiteArray[13].call((Object)nodeAttribs, "frequency");
        ScriptBytecodeAdapter.setProperty(object14, null, metaMap, "period");
        Object object15 = callSiteArray[14].call((Object)nodeAttribs, "occurs");
        Object object16 = DefaultTypeTransformation.booleanUnbox(object15) ? object15 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[15].call((Object)nodeAttribs, "occurences")) ? object : Integer.valueOf(0));
        ScriptBytecodeAdapter.setProperty(object16, null, metaMap, "occurences");
        Object object17 = callSiteArray[16].callStatic(JmxTimerFactory.class, fsb, timer, callSiteArray[17].callGetProperty(metaMap));
        ScriptBytecodeAdapter.setProperty(object17, null, metaMap, "name");
        Object object18 = callSiteArray[18].callStatic(JmxTimerFactory.class, callSiteArray[19].callGetProperty(metaMap));
        ScriptBytecodeAdapter.setProperty(object18, null, metaMap, "date");
        Object object19 = callSiteArray[20].callStatic(JmxTimerFactory.class, callSiteArray[21].callGetProperty(metaMap));
        ScriptBytecodeAdapter.setProperty(object19, null, metaMap, "period");
        Object object20 = callSiteArray[22].callStatic(JmxTimerFactory.class, callSiteArray[23].callGetProperty(metaMap));
        ScriptBytecodeAdapter.setProperty(object20, null, metaMap, "listeners");
        Object result = callSiteArray[24].callStatic(JmxTimerFactory.class, metaMap);
        return result;
    }

    private static Object getNormalizedName(Object fsb, Object timer, Object name) {
        Object object;
        Object object2;
        Object object3;
        Object object4;
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        Object result = null;
        result = !DefaultTypeTransformation.booleanUnbox(name) ? (object4 = callSiteArray[25].callStatic(JmxTimerFactory.class, fsb, timer)) : (name instanceof String ? (object3 = callSiteArray[26].callConstructor(ObjectName.class, name)) : (name instanceof ObjectName ? (object2 = name) : (object = callSiteArray[27].callStatic(JmxTimerFactory.class, fsb, timer))));
        return result;
    }

    private static Object getDefaultName(Object fsb, Object timer) {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        GStringImpl name = new GStringImpl(new Object[]{callSiteArray[28].call(fsb), callSiteArray[29].call(timer)}, new String[]{"", ":type=TimerService,name=Timer@", ""});
        return callSiteArray[30].callConstructor(ObjectName.class, name);
    }

    private static Object getNormalizedDate(Object date) {
        Object object;
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(date)) {
            return callSiteArray[31].callConstructor(Date.class);
        }
        if (date instanceof Date) {
            return date;
        }
        Object startDate = null;
        Object object2 = date;
        if (ScriptBytecodeAdapter.isCase(object2, null) || ScriptBytecodeAdapter.isCase(object2, "now")) {
            // empty if block
        }
        startDate = object = callSiteArray[32].callConstructor(Date.class);
        return startDate;
    }

    private static Object getNormalizedPeriod(Object period) {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(period)) {
            return $const$0;
        }
        if (period instanceof Number) {
            return period;
        }
        Object result = $const$0;
        if (period instanceof String) {
            Object multiplier = callSiteArray[33].call(period, -1);
            Object value = null;
            try {
                Object object;
                value = object = callSiteArray[34].call(callSiteArray[35].call(period, ScriptBytecodeAdapter.createRange(0, -2, true)));
            }
            catch (Exception ignore) {
                String string = "x";
                multiplier = string;
                int n = 0;
                value = n;
            }
            Object object = multiplier;
            if (ScriptBytecodeAdapter.isCase(object, "s")) {
                Object object2;
                result = object2 = callSiteArray[36].call(value, 1000);
            } else if (ScriptBytecodeAdapter.isCase(object, "m")) {
                Object object3;
                result = object3 = callSiteArray[37].call(callSiteArray[38].call(value, 60), 1000);
            } else if (ScriptBytecodeAdapter.isCase(object, "h")) {
                Object object4;
                result = object4 = callSiteArray[39].call(callSiteArray[40].call(callSiteArray[41].call(value, 60), 60), 1000);
            } else if (ScriptBytecodeAdapter.isCase(object, "d")) {
                Object object5;
                result = object5 = callSiteArray[42].call(callSiteArray[43].call(callSiteArray[44].call(callSiteArray[45].call(value, 24), 60), 60), 1000);
            } else {
                long l = $const$0;
                result = l;
            }
        }
        return result;
    }

    private static Object getNormalizedRecipientList(Object list) {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(list)) {
            return null;
        }
        Reference<List> result = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        public class _getNormalizedRecipientList_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference result;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getNormalizedRecipientList_closure1(Object _outerInstance, Object _thisObject, Reference result) {
                Reference reference;
                CallSite[] callSiteArray = _getNormalizedRecipientList_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.result = reference = result;
            }

            public Object doCall(Object name) {
                Object object;
                Object object2;
                Object object3;
                CallSite[] callSiteArray = _getNormalizedRecipientList_closure1.$getCallSiteArray();
                Object on = null;
                on = name instanceof String ? (object3 = callSiteArray[0].callConstructor(ObjectName.class, name)) : (name instanceof ObjectName ? (object2 = name) : (object = callSiteArray[1].callConstructor(ObjectName.class, callSiteArray[2].call(name))));
                return callSiteArray[3].call(this.result.get(), on);
            }

            public Object getResult() {
                CallSite[] callSiteArray = _getNormalizedRecipientList_closure1.$getCallSiteArray();
                return this.result.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getNormalizedRecipientList_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "toString";
                stringArray[3] = "add";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _getNormalizedRecipientList_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getNormalizedRecipientList_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getNormalizedRecipientList_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[46].call(list, new _getNormalizedRecipientList_closure1(JmxTimerFactory.class, JmxTimerFactory.class, result));
        return result.get();
    }

    private static Object registerTimer(Object metaMap) {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        MBeanServer server = (MBeanServer)ScriptBytecodeAdapter.castToType(callSiteArray[47].callGetProperty(metaMap), MBeanServer.class);
        Object timer = callSiteArray[48].callGetProperty(metaMap);
        callSiteArray[49].call(timer, ArrayUtil.createArray(callSiteArray[50].callGetProperty(metaMap), callSiteArray[51].callGetProperty(metaMap), callSiteArray[52].callGetProperty(metaMap), callSiteArray[53].callGetProperty(metaMap), callSiteArray[54].callGetProperty(metaMap), callSiteArray[55].callGetProperty(metaMap)));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[56].call((Object)server, callSiteArray[57].callGetProperty(metaMap)))) {
            callSiteArray[58].call((Object)server, callSiteArray[59].callGetProperty(metaMap));
        }
        callSiteArray[60].call(server, timer, callSiteArray[61].callGetProperty(metaMap));
        return callSiteArray[62].callConstructor(GroovyMBean.class, callSiteArray[63].callGetProperty(metaMap), callSiteArray[64].callGetProperty(metaMap));
    }

    private static NotificationFilter getEventFilter(Object type) {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        Object noteFilter = callSiteArray[65].callConstructor(NotificationFilterSupport.class);
        callSiteArray[66].call(noteFilter, type);
        return (NotificationFilter)ScriptBytecodeAdapter.castToType(noteFilter, NotificationFilter.class);
    }

    @Override
    public boolean onHandleNodeAttributes(FactoryBuilderSupport builder, Object node, Map nodeAttribs) {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        return false;
    }

    @Override
    public boolean isLeaf() {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        return true;
    }

    @Override
    public void onNodeCompleted(FactoryBuilderSupport builder, Object parentNode, Object thisNode) {
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareNotEqual(parentNode, null)) {
            callSiteArray[67].call(parentNode, thisNode);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxTimerFactory.class) {
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

    public static /* synthetic */ void __$swapInit() {
        long l;
        CallSite[] callSiteArray = JmxTimerFactory.$getCallSiteArray();
        $callSiteArray = null;
        $const$0 = l = 1000L;
    }

    static {
        JmxTimerFactory.__$swapInit();
    }

    public /* synthetic */ boolean super$2$isLeaf() {
        return super.isLeaf();
    }

    public /* synthetic */ void super$2$onNodeCompleted(FactoryBuilderSupport factoryBuilderSupport, Object object, Object object2) {
        super.onNodeCompleted(factoryBuilderSupport, object, object2);
    }

    public /* synthetic */ boolean super$2$onHandleNodeAttributes(FactoryBuilderSupport factoryBuilderSupport, Object object, Map map) {
        return super.onHandleNodeAttributes(factoryBuilderSupport, object, map);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "getMBeanServer";
        stringArray[3] = "remove";
        stringArray[4] = "remove";
        stringArray[5] = "remove";
        stringArray[6] = "remove";
        stringArray[7] = "remove";
        stringArray[8] = "remove";
        stringArray[9] = "remove";
        stringArray[10] = "remove";
        stringArray[11] = "remove";
        stringArray[12] = "remove";
        stringArray[13] = "remove";
        stringArray[14] = "remove";
        stringArray[15] = "remove";
        stringArray[16] = "getNormalizedName";
        stringArray[17] = "name";
        stringArray[18] = "getNormalizedDate";
        stringArray[19] = "date";
        stringArray[20] = "getNormalizedPeriod";
        stringArray[21] = "period";
        stringArray[22] = "getNormalizedRecipientList";
        stringArray[23] = "listeners";
        stringArray[24] = "registerTimer";
        stringArray[25] = "getDefaultName";
        stringArray[26] = "<$constructor$>";
        stringArray[27] = "getDefaultName";
        stringArray[28] = "getDefaultJmxNameDomain";
        stringArray[29] = "hashCode";
        stringArray[30] = "<$constructor$>";
        stringArray[31] = "<$constructor$>";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "getAt";
        stringArray[34] = "toLong";
        stringArray[35] = "getAt";
        stringArray[36] = "multiply";
        stringArray[37] = "multiply";
        stringArray[38] = "multiply";
        stringArray[39] = "multiply";
        stringArray[40] = "multiply";
        stringArray[41] = "multiply";
        stringArray[42] = "multiply";
        stringArray[43] = "multiply";
        stringArray[44] = "multiply";
        stringArray[45] = "multiply";
        stringArray[46] = "each";
        stringArray[47] = "server";
        stringArray[48] = "timer";
        stringArray[49] = "addNotification";
        stringArray[50] = "event";
        stringArray[51] = "message";
        stringArray[52] = "data";
        stringArray[53] = "date";
        stringArray[54] = "period";
        stringArray[55] = "occurences";
        stringArray[56] = "isRegistered";
        stringArray[57] = "name";
        stringArray[58] = "unregisterMBean";
        stringArray[59] = "name";
        stringArray[60] = "registerMBean";
        stringArray[61] = "name";
        stringArray[62] = "<$constructor$>";
        stringArray[63] = "server";
        stringArray[64] = "name";
        stringArray[65] = "<$constructor$>";
        stringArray[66] = "enableType";
        stringArray[67] = "add";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[68];
        JmxTimerFactory.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxTimerFactory.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxTimerFactory.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


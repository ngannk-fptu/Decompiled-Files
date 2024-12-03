/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBeanInfoManager;
import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxBuilderModelMBean;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.GroovyMBean;
import java.lang.management.ManagementFactory;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Date;
import java.util.Iterator;
import java.util.Map;
import javax.management.DynamicMBean;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxBuilderTools
implements GroovyObject {
    private static String DEFAULT_DOMAIN;
    private static String DEFAULT_NAME_TYPE;
    private static String NODE_NAME_ATTRIBUTES;
    private static String NODE_NAME_ATTRIBS;
    private static String NODE_NAME_CONSTRUCTORS;
    private static String NODE_NAME_CTORS;
    private static String NODE_NAME_OPERATIONS;
    private static String NODE_NAME_OPS;
    private static String ATTRIB_KEY_DESCRIPTION;
    private static String ATTRIB_KEY_DESC;
    private static String ATTRIB_KEY_TYPE;
    private static String ATTRIB_KEY_DEFAULT;
    private static String JMX_KEY;
    private static String DESC_KEY;
    private static String DESC_KEY_MBEAN_RESOURCE;
    private static String DESC_KEY_MBEAN_RESOURCE_TYPE;
    private static String DESC_KEY_MBEAN_ATTRIBS;
    private static String DESC_KEY_MBEAN_OPS;
    private static String DESC_KEY_MBEAN_CTORS;
    private static String DESC_KEY_MBEAN_NOTES;
    private static String DESC_KEY_NAME;
    private static String DESC_KEY_JMX_NAME;
    private static String DESC_KEY_DISPLAY_NAME;
    private static String DESC_KEY_TYPE;
    private static String DESC_KEY_GETMETHOD;
    private static String DESC_KEY_SETMETHOD;
    private static String DESC_KEY_EVENT_TYPE;
    private static String DESC_KEY_EVENT_NAME;
    private static String DESC_KEY_EVENT_SOURCE;
    private static String DESC_KEY_EVENT_MESSAGE;
    private static String DESC_VAL_TYPE_ATTRIB;
    private static String DESC_VAL_TYPE_GETTER;
    private static String DESC_VAL_TYPE_SETTER;
    private static String DESC_VAL_TYPE_OP;
    private static String DESC_VAL_TYPE_NOTIFICATION;
    private static String DESC_VAL_TYPE_CTOR;
    private static String DESC_VAL_TYPE_MBEAN;
    private static String DESC_KEY_ROLE;
    private static String DESC_KEY_READABLE;
    private static String DESC_KEY_WRITABLE;
    private static String DESC_KEY_SIGNATURE;
    private static String EVENT_KEY_CONTEXTS;
    private static String EVENT_KEY_CALLBACK;
    private static String EVENT_KEY_CALLBACK_RESULT;
    private static String EVENT_KEY_METHOD;
    private static String EVENT_KEY_METHOD_RESULT;
    private static String EVENT_KEY_ISATTRIB;
    private static String EVENT_KEY_NAME;
    private static String EVENT_KEY_MESSAGE;
    private static String EVENT_KEY_TYPE;
    private static String EVENT_KEY_NODE_TYPE;
    private static String EVENT_VAL_NODETYPE_BROADCASTER;
    private static String EVENT_VAL_NODETYPE_LISTENER;
    private static String EVENT_KEY_TARGETS;
    private static Map PRIMITIVE_TYPES;
    private static Map TYPE_MAP;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxBuilderTools() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static String capitalize(String value) {
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(value)) {
            return ShortTypeHandling.castToString(null);
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[0].call(value), 1)) {
            return ShortTypeHandling.castToString(callSiteArray[1].call(value));
        }
        return ShortTypeHandling.castToString(ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[2].call(value), 1) ? callSiteArray[3].call(callSiteArray[4].call(callSiteArray[5].call((Object)value, 0)), callSiteArray[6].call((Object)value, ScriptBytecodeAdapter.createRange(1, -1, true))) : callSiteArray[7].call(value));
    }

    public static String uncapitalize(String value) {
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(value)) {
            return ShortTypeHandling.castToString(null);
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[8].call(value), 1)) {
            return ShortTypeHandling.castToString(callSiteArray[9].call(value));
        }
        return ShortTypeHandling.castToString(ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[10].call(value), 1) ? callSiteArray[11].call(callSiteArray[12].call(callSiteArray[13].call((Object)value, 0)), callSiteArray[14].call((Object)value, ScriptBytecodeAdapter.createRange(1, -1, true))) : callSiteArray[15].call(value));
    }

    public static ObjectName getDefaultObjectName(Object obj) {
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        String name = ShortTypeHandling.castToString(callSiteArray[16].call((Object)DEFAULT_DOMAIN, new GStringImpl(new Object[]{callSiteArray[17].call(callSiteArray[18].call(obj)), callSiteArray[19].call(obj)}, new String[]{":name=", ",hashCode=", ""})));
        ObjectName objectName = (ObjectName)ScriptBytecodeAdapter.castToType(callSiteArray[20].callConstructor(ObjectName.class, name), ObjectName.class);
        try {
            return objectName;
        }
        catch (Exception ex) {
            throw (Throwable)callSiteArray[21].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{name}, new String[]{"Unable to create JMX ObjectName ", ""}), ex);
        }
    }

    public static MBeanServerConnection getMBeanServer() {
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        Object servers = callSiteArray[22].call((Object)MBeanServerFactory.class, (Object)null);
        Object server = ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[23].call(servers), 0) ? callSiteArray[24].call(servers, 0) : callSiteArray[25].call(ManagementFactory.class);
        return (MBeanServerConnection)ScriptBytecodeAdapter.castToType(server, MBeanServerConnection.class);
    }

    public static Class[] getSignatureFromParamInfo(Object params) {
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? ScriptBytecodeAdapter.compareEqual(params, null) || ScriptBytecodeAdapter.compareEqual(callSiteArray[26].call(params), 0) : ScriptBytecodeAdapter.compareEqual(params, null) || ScriptBytecodeAdapter.compareEqual(callSiteArray[27].call(params), 0)) {
            return (Class[])ScriptBytecodeAdapter.castToType(null, Class[].class);
        }
        Reference<Object[]> result = new Reference<Object[]>(new Object[DefaultTypeTransformation.intUnbox(callSiteArray[28].call(params))]);
        public class _getSignatureFromParamInfo_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference result;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getSignatureFromParamInfo_closure1(Object _outerInstance, Object _thisObject, Reference result) {
                Reference reference;
                CallSite[] callSiteArray = _getSignatureFromParamInfo_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.result = reference = result;
            }

            public Object doCall(Object param, Object i) {
                Class<?> clazz;
                CallSite[] callSiteArray = _getSignatureFromParamInfo_closure1.$getCallSiteArray();
                Class<?> clazz2 = callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].call(param));
                Class<?> type = DefaultTypeTransformation.booleanUnbox(clazz2) ? clazz2 : (DefaultTypeTransformation.booleanUnbox(clazz = Class.forName(ShortTypeHandling.castToString(callSiteArray[3].call(param)))) ? clazz : null);
                return callSiteArray[4].call(this.result.get(), i, type);
            }

            public Object call(Object param, Object i) {
                CallSite[] callSiteArray = _getSignatureFromParamInfo_closure1.$getCallSiteArray();
                return callSiteArray[5].callCurrent(this, param, i);
            }

            public Object[] getResult() {
                CallSite[] callSiteArray = _getSignatureFromParamInfo_closure1.$getCallSiteArray();
                return (Object[])ScriptBytecodeAdapter.castToType(this.result.get(), Object[].class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getSignatureFromParamInfo_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "get";
                stringArray[1] = "TYPE_MAP";
                stringArray[2] = "getType";
                stringArray[3] = "getType";
                stringArray[4] = "putAt";
                stringArray[5] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _getSignatureFromParamInfo_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getSignatureFromParamInfo_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getSignatureFromParamInfo_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[29].call(params, new _getSignatureFromParamInfo_closure1(JmxBuilderTools.class, JmxBuilderTools.class, result));
        return (Class[])ScriptBytecodeAdapter.castToType(result.get(), Class[].class);
    }

    public static String getNormalizedType(String type) {
        Object object;
        Object object2;
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[30].callStatic(JmxBuilderTools.class, type))) {
                return ShortTypeHandling.castToString(callSiteArray[31].callGetProperty(callSiteArray[32].call((Object)PRIMITIVE_TYPES, type)));
            }
        } else if (JmxBuilderTools.typeIsPrimitive(type)) {
            return ShortTypeHandling.castToString(callSiteArray[33].callGetProperty(callSiteArray[34].call((Object)PRIMITIVE_TYPES, type)));
        }
        return ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[35].callGetPropertySafe(callSiteArray[36].call((Object)TYPE_MAP, type))) ? object2 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[37].callGetPropertySafe(Class.forName(type))) ? object : null));
    }

    private static boolean typeIsPrimitive(String typeName) {
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[38].call((Object)PRIMITIVE_TYPES, typeName));
    }

    public static boolean isClassMBean(Class cls) {
        CallSite[] callSiteArray = JmxBuilderTools.$getCallSiteArray();
        boolean result = false;
        if (ScriptBytecodeAdapter.compareEqual(cls, null)) {
            boolean bl;
            result = bl = false;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[39].call(DynamicMBean.class, cls))) {
            boolean bl;
            result = bl = true;
        }
        Object face = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[40].call(callSiteArray[41].call(cls)), Iterator.class);
        while (iterator.hasNext()) {
            boolean bl;
            face = iterator.next();
            String name = ShortTypeHandling.castToString(callSiteArray[42].call(face));
            if (!(DefaultTypeTransformation.booleanUnbox(callSiteArray[43].call((Object)name, "MBean")) || DefaultTypeTransformation.booleanUnbox(callSiteArray[44].call((Object)name, "MXBean")))) continue;
            result = bl = true;
            break;
        }
        return result;
    }

    public static GroovyMBean registerMBeanFromMap(String regPolicy, Map metaMap) {
        Object gbean;
        block7: {
            Object object;
            Object mbean;
            CallSite[] callSiteArray;
            block9: {
                String string;
                block8: {
                    block6: {
                        Object object2;
                        callSiteArray = JmxBuilderTools.$getCallSiteArray();
                        Object info = callSiteArray[45].call(JmxBeanInfoManager.class, metaMap);
                        mbean = null;
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[46].callGetProperty(metaMap))) {
                            Object object3;
                            mbean = object3 = callSiteArray[47].callGetProperty(metaMap);
                        } else {
                            Object object4;
                            mbean = object4 = callSiteArray[48].callConstructor(JmxBuilderModelMBean.class, info);
                            callSiteArray[49].call(mbean, callSiteArray[50].callGetProperty(metaMap));
                            callSiteArray[51].call(mbean, callSiteArray[52].callGetProperty(metaMap));
                            callSiteArray[53].call(mbean, callSiteArray[54].callGetProperty(metaMap));
                            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[55].callGetProperty(metaMap))) {
                                callSiteArray[56].call(mbean, callSiteArray[57].callGetProperty(metaMap), callSiteArray[58].callGetProperty(metaMap));
                            }
                        }
                        gbean = null;
                        string = regPolicy;
                        if (!ScriptBytecodeAdapter.isCase(string, "replace")) break block6;
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[59].call(callSiteArray[60].callGetProperty(metaMap), callSiteArray[61].callGetProperty(metaMap)))) {
                            callSiteArray[62].call(callSiteArray[63].callGetProperty(metaMap), callSiteArray[64].callGetProperty(metaMap));
                        }
                        callSiteArray[65].call(callSiteArray[66].callGetProperty(metaMap), mbean, callSiteArray[67].callGetProperty(metaMap));
                        gbean = object2 = callSiteArray[68].callConstructor(GroovyMBean.class, callSiteArray[69].callGetProperty(metaMap), callSiteArray[70].callGetProperty(metaMap));
                        break block7;
                    }
                    if (!ScriptBytecodeAdapter.isCase(string, "ignore")) break block8;
                    if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[71].call(callSiteArray[72].callGetProperty(metaMap), callSiteArray[73].callGetProperty(metaMap)))) break block9;
                    break block7;
                }
                if (ScriptBytecodeAdapter.isCase(string, "error")) {
                    // empty if block
                }
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[74].call(callSiteArray[75].callGetProperty(metaMap), callSiteArray[76].callGetProperty(metaMap)))) {
                throw (Throwable)callSiteArray[77].callConstructor(JmxBuilderException.class, new GStringImpl(new Object[]{callSiteArray[78].callGetProperty(metaMap)}, new String[]{"A Bean with name ", " is already registered on the server."}));
            }
            callSiteArray[79].call(callSiteArray[80].callGetProperty(metaMap), mbean, callSiteArray[81].callGetProperty(metaMap));
            gbean = object = callSiteArray[82].callConstructor(GroovyMBean.class, callSiteArray[83].callGetProperty(metaMap), callSiteArray[84].callGetProperty(metaMap));
        }
        return (GroovyMBean)ScriptBytecodeAdapter.castToType(gbean, GroovyMBean.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxBuilderTools.class) {
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

    static {
        Map map;
        Map map2;
        String string;
        String string2;
        String string3;
        String string4;
        String string5;
        String string6;
        String string7;
        String string8;
        String string9;
        String string10;
        String string11;
        String string12;
        String string13;
        String string14;
        String string15;
        String string16;
        String string17;
        String string18;
        String string19;
        String string20;
        String string21;
        String string22;
        String string23;
        String string24;
        String string25;
        String string26;
        String string27;
        String string28;
        String string29;
        String string30;
        String string31;
        String string32;
        String string33;
        String string34;
        String string35;
        String string36;
        String string37;
        String string38;
        String string39;
        String string40;
        String string41;
        String string42;
        String string43;
        String string44;
        String string45;
        String string46;
        String string47;
        String string48;
        String string49;
        String string50;
        String string51;
        String string52;
        String string53;
        String string54;
        EVENT_KEY_TARGETS = string54 = "eventListeners";
        EVENT_VAL_NODETYPE_LISTENER = string53 = "listener";
        EVENT_VAL_NODETYPE_BROADCASTER = string52 = "broadcaster";
        EVENT_KEY_NODE_TYPE = string51 = "eventNodeType";
        EVENT_KEY_TYPE = string50 = "eventType";
        EVENT_KEY_MESSAGE = string49 = "eventMessage";
        EVENT_KEY_NAME = string48 = "eventName";
        EVENT_KEY_ISATTRIB = string47 = "eventIsAttrib";
        EVENT_KEY_METHOD_RESULT = string46 = "eventMethodResult";
        EVENT_KEY_METHOD = string45 = "eventMethod";
        EVENT_KEY_CALLBACK_RESULT = string44 = "eventCallbackResult";
        EVENT_KEY_CALLBACK = string43 = "eventCallback";
        EVENT_KEY_CONTEXTS = string42 = "eventContexts";
        DESC_KEY_SIGNATURE = string41 = "signature";
        DESC_KEY_WRITABLE = string40 = "writable";
        DESC_KEY_READABLE = string39 = "readable";
        DESC_KEY_ROLE = string38 = "role";
        DESC_VAL_TYPE_MBEAN = string37 = "mbean";
        DESC_VAL_TYPE_CTOR = string36 = "constructor";
        DESC_VAL_TYPE_NOTIFICATION = string35 = "notification";
        DESC_VAL_TYPE_OP = string34 = "operation";
        DESC_VAL_TYPE_SETTER = string33 = "setter";
        DESC_VAL_TYPE_GETTER = string32 = "getter";
        DESC_VAL_TYPE_ATTRIB = string31 = "attribute";
        DESC_KEY_EVENT_MESSAGE = string30 = "messageText";
        DESC_KEY_EVENT_SOURCE = string29 = "eventSource";
        DESC_KEY_EVENT_NAME = string28 = "eventName";
        DESC_KEY_EVENT_TYPE = string27 = "eventType";
        DESC_KEY_SETMETHOD = string26 = "setMethod";
        DESC_KEY_GETMETHOD = string25 = "getMethod";
        DESC_KEY_TYPE = string24 = "descriptorType";
        DESC_KEY_DISPLAY_NAME = string23 = "displayName";
        DESC_KEY_JMX_NAME = string22 = "jmxName";
        DESC_KEY_NAME = string21 = "name";
        DESC_KEY_MBEAN_NOTES = string20 = "notifications";
        DESC_KEY_MBEAN_CTORS = string19 = "constructors";
        DESC_KEY_MBEAN_OPS = string18 = "operations";
        DESC_KEY_MBEAN_ATTRIBS = string17 = "attributes";
        DESC_KEY_MBEAN_RESOURCE_TYPE = string16 = "ObjectReference";
        DESC_KEY_MBEAN_RESOURCE = string15 = "resource";
        DESC_KEY = string14 = "descriptor";
        JMX_KEY = string13 = "jmx";
        ATTRIB_KEY_DEFAULT = string12 = "defaultValue";
        ATTRIB_KEY_TYPE = string11 = "type";
        ATTRIB_KEY_DESC = string10 = "desc";
        ATTRIB_KEY_DESCRIPTION = string9 = "description";
        NODE_NAME_OPS = string8 = "ops";
        NODE_NAME_OPERATIONS = string7 = "operations";
        NODE_NAME_CTORS = string6 = "ctors";
        NODE_NAME_CONSTRUCTORS = string5 = "constructors";
        NODE_NAME_ATTRIBS = string4 = "attribs";
        NODE_NAME_ATTRIBUTES = string3 = "attributes";
        DEFAULT_NAME_TYPE = string2 = "ExportedObject";
        DEFAULT_DOMAIN = string = "jmx.builder";
        PRIMITIVE_TYPES = map2 = ScriptBytecodeAdapter.createMap(new Object[]{"char", JmxBuilderTools.$getCallSiteArray()[85].callGetProperty(Integer.class), "byte", JmxBuilderTools.$getCallSiteArray()[86].callGetProperty(Byte.class), "short", JmxBuilderTools.$getCallSiteArray()[87].callGetProperty(Short.class), "int", JmxBuilderTools.$getCallSiteArray()[88].callGetProperty(Integer.class), "long", JmxBuilderTools.$getCallSiteArray()[89].callGetProperty(Long.class), "float", JmxBuilderTools.$getCallSiteArray()[90].callGetProperty(Float.class), "double", JmxBuilderTools.$getCallSiteArray()[91].callGetProperty(Double.class), "boolean", JmxBuilderTools.$getCallSiteArray()[92].callGetProperty(Boolean.class)});
        TYPE_MAP = map = ScriptBytecodeAdapter.createMap(new Object[]{"object", Object.class, "Object", Object.class, "java.lang.Object", Object.class, "string", String.class, "String", String.class, "java.lang.String", String.class, "char", Character.TYPE, "character", Character.class, "Character", Character.class, "java.lang.Character", Character.class, "byte", Byte.TYPE, "Byte", Byte.class, "java.lang.Byte", Byte.class, "short", Short.TYPE, "Short", Short.class, "java.lang.Short", Short.class, "int", Integer.TYPE, "integer", Integer.class, "Integer", Integer.class, "java.lang.Integer", Integer.class, "long", Long.TYPE, "Long", Long.class, "java.lang.Long", Long.class, "float", Float.TYPE, "Float", Float.class, "java.lang.Float", Float.class, "double", Double.TYPE, "Double", Double.class, "java.lang.Double", Double.class, "boolean", Boolean.TYPE, "Boolean", Boolean.class, "java.lang.Boolean", Boolean.class, "bigDec", BigDecimal.class, "bigDecimal", BigDecimal.class, "BigDecimal", BigDecimal.class, "java.math.BigDecimal", BigDecimal.class, "bigInt", BigInteger.class, "bigInteger", BigInteger.class, "BigInteger", BigInteger.class, "java.math.BigInteger", BigInteger.class, "date", Date.class, "java.util.Date", Date.class});
    }

    public static String getDEFAULT_DOMAIN() {
        return DEFAULT_DOMAIN;
    }

    public static void setDEFAULT_DOMAIN(String string) {
        DEFAULT_DOMAIN = string;
    }

    public static String getDEFAULT_NAME_TYPE() {
        return DEFAULT_NAME_TYPE;
    }

    public static void setDEFAULT_NAME_TYPE(String string) {
        DEFAULT_NAME_TYPE = string;
    }

    public static String getNODE_NAME_ATTRIBUTES() {
        return NODE_NAME_ATTRIBUTES;
    }

    public static void setNODE_NAME_ATTRIBUTES(String string) {
        NODE_NAME_ATTRIBUTES = string;
    }

    public static String getNODE_NAME_ATTRIBS() {
        return NODE_NAME_ATTRIBS;
    }

    public static void setNODE_NAME_ATTRIBS(String string) {
        NODE_NAME_ATTRIBS = string;
    }

    public static String getNODE_NAME_CONSTRUCTORS() {
        return NODE_NAME_CONSTRUCTORS;
    }

    public static void setNODE_NAME_CONSTRUCTORS(String string) {
        NODE_NAME_CONSTRUCTORS = string;
    }

    public static String getNODE_NAME_CTORS() {
        return NODE_NAME_CTORS;
    }

    public static void setNODE_NAME_CTORS(String string) {
        NODE_NAME_CTORS = string;
    }

    public static String getNODE_NAME_OPERATIONS() {
        return NODE_NAME_OPERATIONS;
    }

    public static void setNODE_NAME_OPERATIONS(String string) {
        NODE_NAME_OPERATIONS = string;
    }

    public static String getNODE_NAME_OPS() {
        return NODE_NAME_OPS;
    }

    public static void setNODE_NAME_OPS(String string) {
        NODE_NAME_OPS = string;
    }

    public static String getATTRIB_KEY_DESCRIPTION() {
        return ATTRIB_KEY_DESCRIPTION;
    }

    public static void setATTRIB_KEY_DESCRIPTION(String string) {
        ATTRIB_KEY_DESCRIPTION = string;
    }

    public static String getATTRIB_KEY_DESC() {
        return ATTRIB_KEY_DESC;
    }

    public static void setATTRIB_KEY_DESC(String string) {
        ATTRIB_KEY_DESC = string;
    }

    public static String getATTRIB_KEY_TYPE() {
        return ATTRIB_KEY_TYPE;
    }

    public static void setATTRIB_KEY_TYPE(String string) {
        ATTRIB_KEY_TYPE = string;
    }

    public static String getATTRIB_KEY_DEFAULT() {
        return ATTRIB_KEY_DEFAULT;
    }

    public static void setATTRIB_KEY_DEFAULT(String string) {
        ATTRIB_KEY_DEFAULT = string;
    }

    public static String getJMX_KEY() {
        return JMX_KEY;
    }

    public static void setJMX_KEY(String string) {
        JMX_KEY = string;
    }

    public static String getDESC_KEY() {
        return DESC_KEY;
    }

    public static void setDESC_KEY(String string) {
        DESC_KEY = string;
    }

    public static String getDESC_KEY_MBEAN_RESOURCE() {
        return DESC_KEY_MBEAN_RESOURCE;
    }

    public static void setDESC_KEY_MBEAN_RESOURCE(String string) {
        DESC_KEY_MBEAN_RESOURCE = string;
    }

    public static String getDESC_KEY_MBEAN_RESOURCE_TYPE() {
        return DESC_KEY_MBEAN_RESOURCE_TYPE;
    }

    public static void setDESC_KEY_MBEAN_RESOURCE_TYPE(String string) {
        DESC_KEY_MBEAN_RESOURCE_TYPE = string;
    }

    public static String getDESC_KEY_MBEAN_ATTRIBS() {
        return DESC_KEY_MBEAN_ATTRIBS;
    }

    public static void setDESC_KEY_MBEAN_ATTRIBS(String string) {
        DESC_KEY_MBEAN_ATTRIBS = string;
    }

    public static String getDESC_KEY_MBEAN_OPS() {
        return DESC_KEY_MBEAN_OPS;
    }

    public static void setDESC_KEY_MBEAN_OPS(String string) {
        DESC_KEY_MBEAN_OPS = string;
    }

    public static String getDESC_KEY_MBEAN_CTORS() {
        return DESC_KEY_MBEAN_CTORS;
    }

    public static void setDESC_KEY_MBEAN_CTORS(String string) {
        DESC_KEY_MBEAN_CTORS = string;
    }

    public static String getDESC_KEY_MBEAN_NOTES() {
        return DESC_KEY_MBEAN_NOTES;
    }

    public static void setDESC_KEY_MBEAN_NOTES(String string) {
        DESC_KEY_MBEAN_NOTES = string;
    }

    public static String getDESC_KEY_NAME() {
        return DESC_KEY_NAME;
    }

    public static void setDESC_KEY_NAME(String string) {
        DESC_KEY_NAME = string;
    }

    public static String getDESC_KEY_JMX_NAME() {
        return DESC_KEY_JMX_NAME;
    }

    public static void setDESC_KEY_JMX_NAME(String string) {
        DESC_KEY_JMX_NAME = string;
    }

    public static String getDESC_KEY_DISPLAY_NAME() {
        return DESC_KEY_DISPLAY_NAME;
    }

    public static void setDESC_KEY_DISPLAY_NAME(String string) {
        DESC_KEY_DISPLAY_NAME = string;
    }

    public static String getDESC_KEY_TYPE() {
        return DESC_KEY_TYPE;
    }

    public static void setDESC_KEY_TYPE(String string) {
        DESC_KEY_TYPE = string;
    }

    public static String getDESC_KEY_GETMETHOD() {
        return DESC_KEY_GETMETHOD;
    }

    public static void setDESC_KEY_GETMETHOD(String string) {
        DESC_KEY_GETMETHOD = string;
    }

    public static String getDESC_KEY_SETMETHOD() {
        return DESC_KEY_SETMETHOD;
    }

    public static void setDESC_KEY_SETMETHOD(String string) {
        DESC_KEY_SETMETHOD = string;
    }

    public static String getDESC_KEY_EVENT_TYPE() {
        return DESC_KEY_EVENT_TYPE;
    }

    public static void setDESC_KEY_EVENT_TYPE(String string) {
        DESC_KEY_EVENT_TYPE = string;
    }

    public static String getDESC_KEY_EVENT_NAME() {
        return DESC_KEY_EVENT_NAME;
    }

    public static void setDESC_KEY_EVENT_NAME(String string) {
        DESC_KEY_EVENT_NAME = string;
    }

    public static String getDESC_KEY_EVENT_SOURCE() {
        return DESC_KEY_EVENT_SOURCE;
    }

    public static void setDESC_KEY_EVENT_SOURCE(String string) {
        DESC_KEY_EVENT_SOURCE = string;
    }

    public static String getDESC_KEY_EVENT_MESSAGE() {
        return DESC_KEY_EVENT_MESSAGE;
    }

    public static void setDESC_KEY_EVENT_MESSAGE(String string) {
        DESC_KEY_EVENT_MESSAGE = string;
    }

    public static String getDESC_VAL_TYPE_ATTRIB() {
        return DESC_VAL_TYPE_ATTRIB;
    }

    public static void setDESC_VAL_TYPE_ATTRIB(String string) {
        DESC_VAL_TYPE_ATTRIB = string;
    }

    public static String getDESC_VAL_TYPE_GETTER() {
        return DESC_VAL_TYPE_GETTER;
    }

    public static void setDESC_VAL_TYPE_GETTER(String string) {
        DESC_VAL_TYPE_GETTER = string;
    }

    public static String getDESC_VAL_TYPE_SETTER() {
        return DESC_VAL_TYPE_SETTER;
    }

    public static void setDESC_VAL_TYPE_SETTER(String string) {
        DESC_VAL_TYPE_SETTER = string;
    }

    public static String getDESC_VAL_TYPE_OP() {
        return DESC_VAL_TYPE_OP;
    }

    public static void setDESC_VAL_TYPE_OP(String string) {
        DESC_VAL_TYPE_OP = string;
    }

    public static String getDESC_VAL_TYPE_NOTIFICATION() {
        return DESC_VAL_TYPE_NOTIFICATION;
    }

    public static void setDESC_VAL_TYPE_NOTIFICATION(String string) {
        DESC_VAL_TYPE_NOTIFICATION = string;
    }

    public static String getDESC_VAL_TYPE_CTOR() {
        return DESC_VAL_TYPE_CTOR;
    }

    public static void setDESC_VAL_TYPE_CTOR(String string) {
        DESC_VAL_TYPE_CTOR = string;
    }

    public static String getDESC_VAL_TYPE_MBEAN() {
        return DESC_VAL_TYPE_MBEAN;
    }

    public static void setDESC_VAL_TYPE_MBEAN(String string) {
        DESC_VAL_TYPE_MBEAN = string;
    }

    public static String getDESC_KEY_ROLE() {
        return DESC_KEY_ROLE;
    }

    public static void setDESC_KEY_ROLE(String string) {
        DESC_KEY_ROLE = string;
    }

    public static String getDESC_KEY_READABLE() {
        return DESC_KEY_READABLE;
    }

    public static void setDESC_KEY_READABLE(String string) {
        DESC_KEY_READABLE = string;
    }

    public static String getDESC_KEY_WRITABLE() {
        return DESC_KEY_WRITABLE;
    }

    public static void setDESC_KEY_WRITABLE(String string) {
        DESC_KEY_WRITABLE = string;
    }

    public static String getDESC_KEY_SIGNATURE() {
        return DESC_KEY_SIGNATURE;
    }

    public static void setDESC_KEY_SIGNATURE(String string) {
        DESC_KEY_SIGNATURE = string;
    }

    public static String getEVENT_KEY_CONTEXTS() {
        return EVENT_KEY_CONTEXTS;
    }

    public static void setEVENT_KEY_CONTEXTS(String string) {
        EVENT_KEY_CONTEXTS = string;
    }

    public static String getEVENT_KEY_CALLBACK() {
        return EVENT_KEY_CALLBACK;
    }

    public static void setEVENT_KEY_CALLBACK(String string) {
        EVENT_KEY_CALLBACK = string;
    }

    public static String getEVENT_KEY_CALLBACK_RESULT() {
        return EVENT_KEY_CALLBACK_RESULT;
    }

    public static void setEVENT_KEY_CALLBACK_RESULT(String string) {
        EVENT_KEY_CALLBACK_RESULT = string;
    }

    public static String getEVENT_KEY_METHOD() {
        return EVENT_KEY_METHOD;
    }

    public static void setEVENT_KEY_METHOD(String string) {
        EVENT_KEY_METHOD = string;
    }

    public static String getEVENT_KEY_METHOD_RESULT() {
        return EVENT_KEY_METHOD_RESULT;
    }

    public static void setEVENT_KEY_METHOD_RESULT(String string) {
        EVENT_KEY_METHOD_RESULT = string;
    }

    public static String getEVENT_KEY_ISATTRIB() {
        return EVENT_KEY_ISATTRIB;
    }

    public static void setEVENT_KEY_ISATTRIB(String string) {
        EVENT_KEY_ISATTRIB = string;
    }

    public static String getEVENT_KEY_NAME() {
        return EVENT_KEY_NAME;
    }

    public static void setEVENT_KEY_NAME(String string) {
        EVENT_KEY_NAME = string;
    }

    public static String getEVENT_KEY_MESSAGE() {
        return EVENT_KEY_MESSAGE;
    }

    public static void setEVENT_KEY_MESSAGE(String string) {
        EVENT_KEY_MESSAGE = string;
    }

    public static String getEVENT_KEY_TYPE() {
        return EVENT_KEY_TYPE;
    }

    public static void setEVENT_KEY_TYPE(String string) {
        EVENT_KEY_TYPE = string;
    }

    public static String getEVENT_KEY_NODE_TYPE() {
        return EVENT_KEY_NODE_TYPE;
    }

    public static void setEVENT_KEY_NODE_TYPE(String string) {
        EVENT_KEY_NODE_TYPE = string;
    }

    public static String getEVENT_VAL_NODETYPE_BROADCASTER() {
        return EVENT_VAL_NODETYPE_BROADCASTER;
    }

    public static void setEVENT_VAL_NODETYPE_BROADCASTER(String string) {
        EVENT_VAL_NODETYPE_BROADCASTER = string;
    }

    public static String getEVENT_VAL_NODETYPE_LISTENER() {
        return EVENT_VAL_NODETYPE_LISTENER;
    }

    public static void setEVENT_VAL_NODETYPE_LISTENER(String string) {
        EVENT_VAL_NODETYPE_LISTENER = string;
    }

    public static String getEVENT_KEY_TARGETS() {
        return EVENT_KEY_TARGETS;
    }

    public static void setEVENT_KEY_TARGETS(String string) {
        EVENT_KEY_TARGETS = string;
    }

    public static Map getPRIMITIVE_TYPES() {
        return PRIMITIVE_TYPES;
    }

    public static void setPRIMITIVE_TYPES(Map map) {
        PRIMITIVE_TYPES = map;
    }

    public static Map getTYPE_MAP() {
        return TYPE_MAP;
    }

    public static void setTYPE_MAP(Map map) {
        TYPE_MAP = map;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "length";
        stringArray[1] = "toUpperCase";
        stringArray[2] = "length";
        stringArray[3] = "plus";
        stringArray[4] = "toUpperCase";
        stringArray[5] = "getAt";
        stringArray[6] = "getAt";
        stringArray[7] = "toUpperCase";
        stringArray[8] = "length";
        stringArray[9] = "toLowerCase";
        stringArray[10] = "length";
        stringArray[11] = "plus";
        stringArray[12] = "toLowerCase";
        stringArray[13] = "getAt";
        stringArray[14] = "getAt";
        stringArray[15] = "toLowerCase";
        stringArray[16] = "plus";
        stringArray[17] = "getName";
        stringArray[18] = "getClass";
        stringArray[19] = "hashCode";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "<$constructor$>";
        stringArray[22] = "findMBeanServer";
        stringArray[23] = "size";
        stringArray[24] = "getAt";
        stringArray[25] = "getPlatformMBeanServer";
        stringArray[26] = "size";
        stringArray[27] = "size";
        stringArray[28] = "size";
        stringArray[29] = "eachWithIndex";
        stringArray[30] = "typeIsPrimitive";
        stringArray[31] = "name";
        stringArray[32] = "getAt";
        stringArray[33] = "name";
        stringArray[34] = "getAt";
        stringArray[35] = "name";
        stringArray[36] = "getAt";
        stringArray[37] = "name";
        stringArray[38] = "containsKey";
        stringArray[39] = "isAssignableFrom";
        stringArray[40] = "iterator";
        stringArray[41] = "getInterfaces";
        stringArray[42] = "getName";
        stringArray[43] = "endsWith";
        stringArray[44] = "endsWith";
        stringArray[45] = "getModelMBeanInfoFromMap";
        stringArray[46] = "isMBean";
        stringArray[47] = "target";
        stringArray[48] = "<$constructor$>";
        stringArray[49] = "setManagedResource";
        stringArray[50] = "target";
        stringArray[51] = "addOperationCallListeners";
        stringArray[52] = "attributes";
        stringArray[53] = "addOperationCallListeners";
        stringArray[54] = "operations";
        stringArray[55] = "listeners";
        stringArray[56] = "addEventListeners";
        stringArray[57] = "server";
        stringArray[58] = "listeners";
        stringArray[59] = "isRegistered";
        stringArray[60] = "server";
        stringArray[61] = "jmxName";
        stringArray[62] = "unregisterMBean";
        stringArray[63] = "server";
        stringArray[64] = "jmxName";
        stringArray[65] = "registerMBean";
        stringArray[66] = "server";
        stringArray[67] = "jmxName";
        stringArray[68] = "<$constructor$>";
        stringArray[69] = "server";
        stringArray[70] = "jmxName";
        stringArray[71] = "isRegistered";
        stringArray[72] = "server";
        stringArray[73] = "jmxName";
        stringArray[74] = "isRegistered";
        stringArray[75] = "server";
        stringArray[76] = "jmxName";
        stringArray[77] = "<$constructor$>";
        stringArray[78] = "jmxName";
        stringArray[79] = "registerMBean";
        stringArray[80] = "server";
        stringArray[81] = "jmxName";
        stringArray[82] = "<$constructor$>";
        stringArray[83] = "server";
        stringArray[84] = "jmxName";
        stringArray[85] = "TYPE";
        stringArray[86] = "TYPE";
        stringArray[87] = "TYPE";
        stringArray[88] = "TYPE";
        stringArray[89] = "TYPE";
        stringArray[90] = "TYPE";
        stringArray[91] = "TYPE";
        stringArray[92] = "TYPE";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[93];
        JmxBuilderTools.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxBuilderTools.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxBuilderTools.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


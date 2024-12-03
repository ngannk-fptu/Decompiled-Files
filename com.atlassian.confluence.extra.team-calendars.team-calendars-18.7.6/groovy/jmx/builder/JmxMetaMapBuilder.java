/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBeanInfoManager;
import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxBuilderTools;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.lang.reflect.Constructor;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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

public class JmxMetaMapBuilder
implements GroovyObject {
    private static final Object ATTRIB_EXCEPTION_LIST;
    private static final Object OPS_EXCEPTION_LIST;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxMetaMapBuilder() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static Map buildObjectMapFrom(Object object) {
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(object)) {
            throw (Throwable)callSiteArray[0].callConstructor(JmxBuilderException.class, "Unable to create MBean, missing target object.");
        }
        Map map = null;
        Object object2 = callSiteArray[1].call(callSiteArray[2].callGetProperty(object), "descriptor");
        Object metaProp = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : callSiteArray[3].call(callSiteArray[4].callGetProperty(object), "jmx");
        if (DefaultTypeTransformation.booleanUnbox(metaProp)) {
            Object descriptor = callSiteArray[5].call(callSiteArray[6].callGetProperty(object), callSiteArray[7].call(object), callSiteArray[8].callGetPropertySafe(metaProp));
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[9].call(descriptor), 1) && DefaultTypeTransformation.booleanUnbox(callSiteArray[10].callGetProperty(descriptor))) {
                Map map2;
                map = map2 = ScriptBytecodeAdapter.createMap(new Object[]{"target", object, "name", callSiteArray[11].callGetProperty(callSiteArray[12].call(object)), "jmxName", callSiteArray[13].callStatic(JmxMetaMapBuilder.class, descriptor), "displayName", callSiteArray[14].call(new GStringImpl(new Object[]{callSiteArray[15].callGetProperty(callSiteArray[16].callGetProperty(object))}, new String[]{"JMX Managed Object ", ""})), "attributes", callSiteArray[17].callStatic(JmxMetaMapBuilder.class, object), "constructors", callSiteArray[18].callStatic(JmxMetaMapBuilder.class, object), "operations", callSiteArray[19].callStatic(JmxMetaMapBuilder.class, object)});
            } else {
                Map map3;
                Object[] objectArray = new Object[16];
                objectArray[0] = "target";
                objectArray[1] = object;
                objectArray[2] = "name";
                objectArray[3] = callSiteArray[20].callGetProperty(callSiteArray[21].call(object));
                objectArray[4] = "displayName";
                Object object3 = callSiteArray[22].callGetProperty(descriptor);
                objectArray[5] = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : callSiteArray[23].callGetProperty(descriptor);
                objectArray[6] = "attributes";
                Object object4 = callSiteArray[25].callGetProperty(descriptor);
                objectArray[7] = callSiteArray[24].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object4) ? object4 : callSiteArray[26].callGetProperty(descriptor));
                objectArray[8] = "constructors";
                Object object5 = callSiteArray[28].callGetProperty(descriptor);
                objectArray[9] = callSiteArray[27].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object5) ? object5 : callSiteArray[29].callGetProperty(descriptor));
                objectArray[10] = "operations";
                Object object6 = callSiteArray[31].callGetProperty(descriptor);
                objectArray[11] = callSiteArray[30].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object6) ? object6 : callSiteArray[32].callGetProperty(descriptor));
                objectArray[12] = "listeners";
                objectArray[13] = callSiteArray[33].callStatic(JmxMetaMapBuilder.class, callSiteArray[34].callGetProperty(descriptor));
                objectArray[14] = "mbeanServer";
                Object object7 = callSiteArray[35].callGetProperty(descriptor);
                objectArray[15] = DefaultTypeTransformation.booleanUnbox(object7) ? object7 : callSiteArray[36].callGetProperty(descriptor);
                map = map3 = ScriptBytecodeAdapter.createMap(objectArray);
                Object object8 = callSiteArray[37].callStatic(JmxMetaMapBuilder.class, descriptor);
                Object object9 = DefaultTypeTransformation.booleanUnbox(object8) ? object8 : callSiteArray[38].call(JmxBeanInfoManager.class, callSiteArray[39].callGetProperty(JmxBuilderTools.class), callSiteArray[40].callGetProperty(JmxBuilderTools.class), object);
                ScriptBytecodeAdapter.setProperty(object9, null, map, "jmxName");
            }
        } else {
            Map map4;
            map = map4 = ScriptBytecodeAdapter.createMap(new Object[]{"target", object, "name", callSiteArray[41].callGetProperty(callSiteArray[42].call(object)), "jmxName", callSiteArray[43].call(JmxBeanInfoManager.class, callSiteArray[44].callGetProperty(JmxBuilderTools.class), callSiteArray[45].callGetProperty(JmxBuilderTools.class), object), "displayName", callSiteArray[46].call(new GStringImpl(new Object[]{callSiteArray[47].callGetProperty(callSiteArray[48].callGetProperty(object))}, new String[]{"JMX Managed Object ", ""})), "attributes", callSiteArray[49].callStatic(JmxMetaMapBuilder.class, object), "constructors", callSiteArray[50].callStatic(JmxMetaMapBuilder.class, object), "operations", callSiteArray[51].callStatic(JmxMetaMapBuilder.class, object)});
        }
        return (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    public static Map buildObjectMapFrom(Object object, Object descriptor) {
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(object)) {
            throw (Throwable)callSiteArray[52].callConstructor(JmxBuilderException.class, "Unable to create MBean, missing target object.");
        }
        Map map = null;
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[53].call(descriptor), 2) && DefaultTypeTransformation.booleanUnbox(callSiteArray[54].callGetProperty(descriptor)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[55].callGetProperty(descriptor))) {
                Map map2;
                map = map2 = ScriptBytecodeAdapter.createMap(new Object[]{"target", object, "jmxName", callSiteArray[56].callStatic(JmxMetaMapBuilder.class, descriptor), "name", callSiteArray[57].callGetProperty(callSiteArray[58].call(object)), "displayName", callSiteArray[59].call(new GStringImpl(new Object[]{callSiteArray[60].callGetProperty(callSiteArray[61].callGetProperty(object))}, new String[]{"JMX Managed Object ", ""})), "attributes", callSiteArray[62].callStatic(JmxMetaMapBuilder.class, object), "constructors", callSiteArray[63].callStatic(JmxMetaMapBuilder.class, object), "operations", callSiteArray[64].callStatic(JmxMetaMapBuilder.class, object)});
            } else {
                Map map3;
                Object[] objectArray = new Object[16];
                objectArray[0] = "target";
                objectArray[1] = object;
                objectArray[2] = "name";
                objectArray[3] = callSiteArray[65].callGetProperty(callSiteArray[66].call(object));
                objectArray[4] = "displayName";
                Object object2 = callSiteArray[67].callGetProperty(descriptor);
                objectArray[5] = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : callSiteArray[68].callGetProperty(descriptor);
                objectArray[6] = "attributes";
                Object object3 = callSiteArray[70].callGetProperty(descriptor);
                objectArray[7] = callSiteArray[69].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object3) ? object3 : callSiteArray[71].callGetProperty(descriptor));
                objectArray[8] = "constructors";
                Object object4 = callSiteArray[73].callGetProperty(descriptor);
                objectArray[9] = callSiteArray[72].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object4) ? object4 : callSiteArray[74].callGetProperty(descriptor));
                objectArray[10] = "operations";
                Object object5 = callSiteArray[76].callGetProperty(descriptor);
                objectArray[11] = callSiteArray[75].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object5) ? object5 : callSiteArray[77].callGetProperty(descriptor));
                objectArray[12] = "listeners";
                objectArray[13] = callSiteArray[78].callStatic(JmxMetaMapBuilder.class, callSiteArray[79].callGetProperty(descriptor));
                objectArray[14] = "mbeanServer";
                Object object6 = callSiteArray[80].callGetProperty(descriptor);
                objectArray[15] = DefaultTypeTransformation.booleanUnbox(object6) ? object6 : callSiteArray[81].callGetProperty(descriptor);
                map = map3 = ScriptBytecodeAdapter.createMap(objectArray);
                Object object7 = callSiteArray[82].callStatic(JmxMetaMapBuilder.class, descriptor);
                Object object8 = DefaultTypeTransformation.booleanUnbox(object7) ? object7 : callSiteArray[83].call(JmxBeanInfoManager.class, callSiteArray[84].callGetProperty(JmxBuilderTools.class), callSiteArray[85].callGetProperty(JmxBuilderTools.class), object);
                ScriptBytecodeAdapter.setProperty(object8, null, map, "jmxName");
            }
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[86].call(descriptor), 2) && DefaultTypeTransformation.booleanUnbox(callSiteArray[87].callGetProperty(descriptor)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[88].callGetProperty(descriptor))) {
            Map map4;
            map = map4 = ScriptBytecodeAdapter.createMap(new Object[]{"target", object, "jmxName", callSiteArray[89].callStatic(JmxMetaMapBuilder.class, descriptor), "name", callSiteArray[90].callGetProperty(callSiteArray[91].call(object)), "displayName", callSiteArray[92].call(new GStringImpl(new Object[]{callSiteArray[93].callGetProperty(callSiteArray[94].callGetProperty(object))}, new String[]{"JMX Managed Object ", ""})), "attributes", callSiteArray[95].callStatic(JmxMetaMapBuilder.class, object), "constructors", callSiteArray[96].callStatic(JmxMetaMapBuilder.class, object), "operations", callSiteArray[97].callStatic(JmxMetaMapBuilder.class, object)});
        } else {
            Map map5;
            Object[] objectArray = new Object[16];
            objectArray[0] = "target";
            objectArray[1] = object;
            objectArray[2] = "name";
            objectArray[3] = callSiteArray[98].callGetProperty(callSiteArray[99].call(object));
            objectArray[4] = "displayName";
            Object object9 = callSiteArray[100].callGetProperty(descriptor);
            objectArray[5] = DefaultTypeTransformation.booleanUnbox(object9) ? object9 : callSiteArray[101].callGetProperty(descriptor);
            objectArray[6] = "attributes";
            Object object10 = callSiteArray[103].callGetProperty(descriptor);
            objectArray[7] = callSiteArray[102].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object10) ? object10 : callSiteArray[104].callGetProperty(descriptor));
            objectArray[8] = "constructors";
            Object object11 = callSiteArray[106].callGetProperty(descriptor);
            objectArray[9] = callSiteArray[105].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object11) ? object11 : callSiteArray[107].callGetProperty(descriptor));
            objectArray[10] = "operations";
            Object object12 = callSiteArray[109].callGetProperty(descriptor);
            objectArray[11] = callSiteArray[108].callStatic(JmxMetaMapBuilder.class, object, DefaultTypeTransformation.booleanUnbox(object12) ? object12 : callSiteArray[110].callGetProperty(descriptor));
            objectArray[12] = "listeners";
            objectArray[13] = callSiteArray[111].callStatic(JmxMetaMapBuilder.class, callSiteArray[112].callGetProperty(descriptor));
            objectArray[14] = "mbeanServer";
            Object object13 = callSiteArray[113].callGetProperty(descriptor);
            objectArray[15] = DefaultTypeTransformation.booleanUnbox(object13) ? object13 : callSiteArray[114].callGetProperty(descriptor);
            map = map5 = ScriptBytecodeAdapter.createMap(objectArray);
            Object object14 = callSiteArray[115].callStatic(JmxMetaMapBuilder.class, descriptor);
            Object object15 = DefaultTypeTransformation.booleanUnbox(object14) ? object14 : callSiteArray[116].call(JmxBeanInfoManager.class, callSiteArray[117].callGetProperty(JmxBuilderTools.class), callSiteArray[118].callGetProperty(JmxBuilderTools.class), object);
            ScriptBytecodeAdapter.setProperty(object15, null, map, "jmxName");
        }
        return (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    private static ObjectName getObjectName(Object map) {
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(map)) {
            return (ObjectName)ScriptBytecodeAdapter.castToType(null, ObjectName.class);
        }
        Object jmxName = null;
        if (callSiteArray[119].callGetProperty(map) instanceof String) {
            Object object;
            jmxName = object = callSiteArray[120].callConstructor(ObjectName.class, callSiteArray[121].callGetProperty(map));
        } else if (callSiteArray[122].callGetProperty(map) instanceof ObjectName) {
            Object object;
            jmxName = object = callSiteArray[123].callGetProperty(map);
        }
        return (ObjectName)ScriptBytecodeAdapter.castToType(jmxName, ObjectName.class);
    }

    public static Map buildAttributeMapFrom(Object object) {
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object properties = callSiteArray[124].call(callSiteArray[125].callGetProperty(object));
        Reference<Map> attribs = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        public class _buildAttributeMapFrom_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference attribs;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _buildAttributeMapFrom_closure1(Object _outerInstance, Object _thisObject, Reference attribs) {
                Reference reference;
                CallSite[] callSiteArray = _buildAttributeMapFrom_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.attribs = reference = attribs;
            }

            public Object doCall(MetaProperty prop) {
                CallSite[] callSiteArray = _buildAttributeMapFrom_closure1.$getCallSiteArray();
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGetProperty(JmxMetaMapBuilder.class), callSiteArray[2].callGetProperty(prop)))) {
                    String string;
                    String string2;
                    Map attrib = ScriptBytecodeAdapter.createMap(new Object[0]);
                    String getterPrefix = null;
                    getterPrefix = !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (string2 = ScriptBytecodeAdapter.compareEqual(callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(prop)), "java.lang.Boolean") || ScriptBytecodeAdapter.compareEqual(callSiteArray[5].callGetProperty(callSiteArray[6].callGetProperty(prop)), "boolean") ? "is" : "get") : (string = ScriptBytecodeAdapter.compareEqual(callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(prop)), "java.lang.Boolean") || ScriptBytecodeAdapter.compareEqual(callSiteArray[9].callGetProperty(callSiteArray[10].callGetProperty(prop)), "boolean") ? "is" : "get");
                    Object name = callSiteArray[11].call(JmxBuilderTools.class, callSiteArray[12].callGetProperty(prop));
                    Object object = name;
                    ScriptBytecodeAdapter.setProperty(object, null, attrib, "name");
                    Object object2 = callSiteArray[13].call(new GStringImpl(new Object[]{callSiteArray[14].callGetProperty(prop)}, new String[]{"Property ", ""}));
                    ScriptBytecodeAdapter.setProperty(object2, null, attrib, "displayName");
                    boolean bl = true;
                    ScriptBytecodeAdapter.setProperty(bl, null, attrib, "readable");
                    Object object3 = callSiteArray[15].call((Object)getterPrefix, name);
                    ScriptBytecodeAdapter.setProperty(object3, null, attrib, "getMethod");
                    boolean bl2 = false;
                    ScriptBytecodeAdapter.setProperty(bl2, null, attrib, "writable");
                    Object object4 = callSiteArray[16].callGetProperty(callSiteArray[17].callGetProperty(prop));
                    ScriptBytecodeAdapter.setProperty(object4, null, attrib, "type");
                    MetaProperty metaProperty = prop;
                    ScriptBytecodeAdapter.setProperty(metaProperty, null, attrib, "property");
                    return callSiteArray[18].call(this.attribs.get(), name, attrib);
                }
                return null;
            }

            public Object call(MetaProperty prop) {
                CallSite[] callSiteArray = _buildAttributeMapFrom_closure1.$getCallSiteArray();
                return callSiteArray[19].callCurrent((GroovyObject)this, prop);
            }

            public Object getAttribs() {
                CallSite[] callSiteArray = _buildAttributeMapFrom_closure1.$getCallSiteArray();
                return this.attribs.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _buildAttributeMapFrom_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "contains";
                stringArray[1] = "ATTRIB_EXCEPTION_LIST";
                stringArray[2] = "name";
                stringArray[3] = "name";
                stringArray[4] = "type";
                stringArray[5] = "name";
                stringArray[6] = "type";
                stringArray[7] = "name";
                stringArray[8] = "type";
                stringArray[9] = "name";
                stringArray[10] = "type";
                stringArray[11] = "capitalize";
                stringArray[12] = "name";
                stringArray[13] = "toString";
                stringArray[14] = "name";
                stringArray[15] = "plus";
                stringArray[16] = "name";
                stringArray[17] = "type";
                stringArray[18] = "put";
                stringArray[19] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[20];
                _buildAttributeMapFrom_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_buildAttributeMapFrom_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _buildAttributeMapFrom_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[126].call(properties, new _buildAttributeMapFrom_closure1(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, attribs));
        return (Map)ScriptBytecodeAdapter.castToType(attribs.get(), Map.class);
    }

    public static Map buildAttributeMapFrom(Object object, Object descCollection) {
        Reference<Object> object2 = new Reference<Object>(object);
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Reference<Map> map = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        if (descCollection instanceof String && DefaultTypeTransformation.booleanUnbox(callSiteArray[127].call(descCollection, "*"))) {
            Object object3 = callSiteArray[128].callStatic(JmxMetaMapBuilder.class, object2.get());
            map.set((Map)object3);
        }
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof List) {
            public class _buildAttributeMapFrom_closure2
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference object;
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildAttributeMapFrom_closure2(Object _outerInstance, Object _thisObject, Reference object, Reference map) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.object = reference2 = object;
                    this.map = reference = map;
                }

                public Object doCall(Object attrib) {
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure2.$getCallSiteArray();
                    MetaProperty prop = (MetaProperty)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callGetProperty(this.object.get()), callSiteArray[2].call(JmxBuilderTools.class, attrib)), MetaProperty.class);
                    if (DefaultTypeTransformation.booleanUnbox(prop)) {
                        return callSiteArray[3].call(this.map.get(), callSiteArray[4].call(JmxBuilderTools.class, attrib), callSiteArray[5].callCurrent(this, prop, attrib, "*"));
                    }
                    return null;
                }

                public Object getObject() {
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure2.$getCallSiteArray();
                    return this.object.get();
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure2.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildAttributeMapFrom_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "getMetaProperty";
                    stringArray[1] = "metaClass";
                    stringArray[2] = "uncapitalize";
                    stringArray[3] = "put";
                    stringArray[4] = "capitalize";
                    stringArray[5] = "createAttributeMap";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[6];
                    _buildAttributeMapFrom_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildAttributeMapFrom_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildAttributeMapFrom_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[129].call(descCollection, new _buildAttributeMapFrom_closure2(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, object2, map));
        }
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof Map) {
            public class _buildAttributeMapFrom_closure3
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference object;
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildAttributeMapFrom_closure3(Object _outerInstance, Object _thisObject, Reference object, Reference map) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure3.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.object = reference2 = object;
                    this.map = reference = map;
                }

                public Object doCall(Object attrib, Object attrDescriptor) {
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure3.$getCallSiteArray();
                    MetaProperty prop = (MetaProperty)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callGetProperty(this.object.get()), callSiteArray[2].call(JmxBuilderTools.class, attrib)), MetaProperty.class);
                    if (DefaultTypeTransformation.booleanUnbox(prop)) {
                        return callSiteArray[3].call(this.map.get(), callSiteArray[4].call(JmxBuilderTools.class, attrib), callSiteArray[5].callCurrent(this, prop, attrib, attrDescriptor));
                    }
                    return null;
                }

                public Object call(Object attrib, Object attrDescriptor) {
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure3.$getCallSiteArray();
                    return callSiteArray[6].callCurrent(this, attrib, attrDescriptor);
                }

                public Object getObject() {
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure3.$getCallSiteArray();
                    return this.object.get();
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildAttributeMapFrom_closure3.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildAttributeMapFrom_closure3.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "getMetaProperty";
                    stringArray[1] = "metaClass";
                    stringArray[2] = "uncapitalize";
                    stringArray[3] = "put";
                    stringArray[4] = "capitalize";
                    stringArray[5] = "createAttributeMap";
                    stringArray[6] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[7];
                    _buildAttributeMapFrom_closure3.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildAttributeMapFrom_closure3.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildAttributeMapFrom_closure3.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[130].call(descCollection, new _buildAttributeMapFrom_closure3(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, object2, map));
        }
        return (Map)ScriptBytecodeAdapter.castToType(map.get(), Map.class);
    }

    private static Map createAttributeMap(Object prop, Object attribName, Object descriptor) {
        Object object;
        Object object2;
        String string;
        String string2;
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object desc = descriptor instanceof Map ? descriptor : ScriptBytecodeAdapter.createMap(new Object[0]);
        Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
        Object name = callSiteArray[131].call(JmxBuilderTools.class, attribName);
        String getterPrefix = null;
        getterPrefix = !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (string2 = ScriptBytecodeAdapter.compareEqual(callSiteArray[132].callGetProperty(callSiteArray[133].callGetProperty(prop)), "java.lang.Boolean") || ScriptBytecodeAdapter.compareEqual(callSiteArray[134].callGetProperty(callSiteArray[135].callGetProperty(prop)), "boolean") ? "is" : "get") : (string = ScriptBytecodeAdapter.compareEqual(callSiteArray[136].callGetProperty(callSiteArray[137].callGetProperty(prop)), "java.lang.Boolean") || ScriptBytecodeAdapter.compareEqual(callSiteArray[138].callGetProperty(callSiteArray[139].callGetProperty(prop)), "boolean") ? "is" : "get");
        Object object3 = name;
        ScriptBytecodeAdapter.setProperty(object3, null, map, "name");
        Object object4 = callSiteArray[140].callGetProperty(desc);
        Object object5 = DefaultTypeTransformation.booleanUnbox(object4) ? object4 : (DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[141].callGetProperty(desc)) ? object2 : callSiteArray[142].call(new GStringImpl(new Object[]{name}, new String[]{"Property ", ""})));
        ScriptBytecodeAdapter.setProperty(object5, null, map, "displayName");
        Object object6 = callSiteArray[143].callGetProperty(callSiteArray[144].callGetProperty(prop));
        ScriptBytecodeAdapter.setProperty(object6, null, map, "type");
        Boolean bl = ScriptBytecodeAdapter.compareNotEqual(callSiteArray[145].callGetProperty(desc), null) ? callSiteArray[146].callGetProperty(desc) : Boolean.valueOf(true);
        ScriptBytecodeAdapter.setProperty(bl, null, map, "readable");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[147].callGetProperty(map))) {
            Object object7 = callSiteArray[148].call((Object)getterPrefix, name);
            ScriptBytecodeAdapter.setProperty(object7, null, map, "getMethod");
        }
        Boolean bl2 = ScriptBytecodeAdapter.compareNotEqual(callSiteArray[149].callGetProperty(desc), null) ? callSiteArray[150].callGetProperty(desc) : Boolean.valueOf(false);
        ScriptBytecodeAdapter.setProperty(bl2, null, map, "writable");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[151].callGetProperty(map))) {
            Object object8 = callSiteArray[152].call((Object)"set", name);
            ScriptBytecodeAdapter.setProperty(object8, null, map, "setMethod");
        }
        Object object9 = DefaultTypeTransformation.booleanUnbox(object = callSiteArray[153].callGetProperty(desc)) ? object : callSiteArray[154].callGetProperty(desc);
        ScriptBytecodeAdapter.setProperty(object9, null, map, "defaultValue");
        Object object10 = prop;
        ScriptBytecodeAdapter.setProperty(object10, null, map, "property");
        Object object11 = callSiteArray[155].callGetProperty(desc);
        Object listener = DefaultTypeTransformation.booleanUnbox(object11) ? object11 : callSiteArray[156].callGetProperty(desc);
        if (DefaultTypeTransformation.booleanUnbox(listener)) {
            Map map2 = ScriptBytecodeAdapter.createMap(new Object[0]);
            ScriptBytecodeAdapter.setProperty(map2, null, map, "methodListener");
            Object object12 = listener;
            ScriptBytecodeAdapter.setProperty(object12, null, callSiteArray[157].callGetProperty(map), "callback");
            Object object13 = callSiteArray[158].call((Object)"set", name);
            ScriptBytecodeAdapter.setProperty(object13, null, callSiteArray[159].callGetProperty(map), "target");
            String string3 = "attributeChangeListener";
            ScriptBytecodeAdapter.setProperty(string3, null, callSiteArray[160].callGetProperty(map), "type");
            Object object14 = name;
            ScriptBytecodeAdapter.setProperty(object14, null, callSiteArray[161].callGetProperty(map), "attribute");
        }
        return (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    public static Map buildConstructorMapFrom(Object object) {
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object methods = callSiteArray[162].call(callSiteArray[163].call(object));
        Map ctors = ScriptBytecodeAdapter.createMap(new Object[0]);
        Integer cntr = 0;
        Constructor ctor = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[164].call(methods), Iterator.class);
        while (iterator.hasNext()) {
            ctor = (Constructor)ScriptBytecodeAdapter.castToType(iterator.next(), Constructor.class);
            Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
            Object object2 = callSiteArray[165].callGetProperty(ctor);
            ScriptBytecodeAdapter.setProperty(object2, null, map, "name");
            Object object3 = callSiteArray[166].call(new GStringImpl(new Object[]{callSiteArray[167].call(callSiteArray[168].call(object))}, new String[]{"Constructor for class ", ""}));
            ScriptBytecodeAdapter.setProperty(object3, null, map, "displayName");
            String string = "constructor";
            ScriptBytecodeAdapter.setProperty(string, null, map, "role");
            Constructor constructor = ctor;
            ScriptBytecodeAdapter.setProperty(constructor, null, map, "constructor");
            callSiteArray[169].call(map, "params", callSiteArray[170].callStatic(JmxMetaMapBuilder.class, ctor));
            Integer n = cntr;
            callSiteArray[179].call(n);
            callSiteArray[171].call(ctors, callSiteArray[172].call(callSiteArray[173].call(callSiteArray[174].call(callSiteArray[175].callGetProperty(ctor), ScriptBytecodeAdapter.createRange(callSiteArray[176].call(callSiteArray[177].call(callSiteArray[178].callGetProperty(ctor), "."), 1), -1, true)), "@"), n), map);
        }
        return (Map)ScriptBytecodeAdapter.castToType(ctors, Map.class);
    }

    public static Map buildConstructorMapFrom(Object object, Object descCollection) {
        Reference<Object> object2 = new Reference<Object>(object);
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Reference<Map> map = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof String && DefaultTypeTransformation.booleanUnbox(callSiteArray[180].call(descCollection, "*"))) {
            Object object3 = callSiteArray[181].callStatic(JmxMetaMapBuilder.class, object2.get());
            map.set((Map)object3);
        }
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof Map) {
            public class _buildConstructorMapFrom_closure4
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference object;
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildConstructorMapFrom_closure4(Object _outerInstance, Object _thisObject, Reference object, Reference map) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildConstructorMapFrom_closure4.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.object = reference2 = object;
                    this.map = reference = map;
                }

                public Object doCall(Object ctorKey, Object descriptor) {
                    CallSite[] callSiteArray = _buildConstructorMapFrom_closure4.$getCallSiteArray();
                    Reference<Object> params = new Reference<Object>(null);
                    params.get();
                    if (DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof List && ScriptBytecodeAdapter.compareEqual(callSiteArray[0].call(descriptor), 0)) {
                        Object t = null;
                        params.set(t);
                    }
                    if (DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof List && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[1].call(descriptor), 0)) {
                        List list = ScriptBytecodeAdapter.createList(new Object[0]);
                        params.set(list);
                        public class _closure12
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference params;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure12(Object _outerInstance, Object _thisObject, Reference params) {
                                Reference reference;
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.params = reference = params;
                            }

                            public Object doCall(Object param) {
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                return callSiteArray[0].call(this.params.get(), callSiteArray[1].call(callSiteArray[2].callGetProperty(JmxBuilderTools.class), callSiteArray[3].call(JmxBuilderTools.class, param)));
                            }

                            public Object getParams() {
                                CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                                return this.params.get();
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure12.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "leftShift";
                                stringArray[1] = "getAt";
                                stringArray[2] = "TYPE_MAP";
                                stringArray[3] = "getNormalizedType";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[4];
                                _closure12.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure12.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure12.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[2].call(descriptor, new _closure12(this, this.getThisObject(), params));
                    }
                    if (DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof Map) {
                        Object paramTypes = ScriptBytecodeAdapter.createList(new Object[0]);
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].callGetProperty(descriptor)) && callSiteArray[4].callGetProperty(descriptor) instanceof Map) {
                            Object object;
                            paramTypes = object = callSiteArray[5].call(callSiteArray[6].call(callSiteArray[7].callGetProperty(descriptor)));
                        } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGetProperty(descriptor)) && callSiteArray[9].callGetProperty(descriptor) instanceof List) {
                            Object object;
                            paramTypes = object = callSiteArray[10].callGetProperty(descriptor);
                        }
                        List list = ScriptBytecodeAdapter.createList(new Object[0]);
                        params.set(list);
                        public class _closure13
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference params;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure13(Object _outerInstance, Object _thisObject, Reference params) {
                                Reference reference;
                                CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.params = reference = params;
                            }

                            public Object doCall(Object p) {
                                CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                return callSiteArray[0].call(this.params.get(), callSiteArray[1].call(callSiteArray[2].callGetProperty(JmxBuilderTools.class), callSiteArray[3].call(JmxBuilderTools.class, p)));
                            }

                            public Object getParams() {
                                CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                                return this.params.get();
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure13.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "leftShift";
                                stringArray[1] = "getAt";
                                stringArray[2] = "TYPE_MAP";
                                stringArray[3] = "getNormalizedType";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[4];
                                _closure13.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure13.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure13.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        callSiteArray[11].call(paramTypes, new _closure13(this, this.getThisObject(), params));
                    }
                    Constructor ctor = (Constructor)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(callSiteArray[13].callGetProperty(this.object.get()), (Object)(ScriptBytecodeAdapter.compareNotEqual(params.get(), null) ? (Class[])ScriptBytecodeAdapter.castToType(params.get(), Class[].class) : null)), Constructor.class);
                    return callSiteArray[14].call(this.map.get(), ctorKey, callSiteArray[15].callCurrent(this, ctor, descriptor));
                }

                public Object call(Object ctorKey, Object descriptor) {
                    CallSite[] callSiteArray = _buildConstructorMapFrom_closure4.$getCallSiteArray();
                    return callSiteArray[16].callCurrent(this, ctorKey, descriptor);
                }

                public Object getObject() {
                    CallSite[] callSiteArray = _buildConstructorMapFrom_closure4.$getCallSiteArray();
                    return this.object.get();
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildConstructorMapFrom_closure4.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildConstructorMapFrom_closure4.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "size";
                    stringArray[1] = "size";
                    stringArray[2] = "each";
                    stringArray[3] = "params";
                    stringArray[4] = "params";
                    stringArray[5] = "toList";
                    stringArray[6] = "keySet";
                    stringArray[7] = "params";
                    stringArray[8] = "params";
                    stringArray[9] = "params";
                    stringArray[10] = "params";
                    stringArray[11] = "each";
                    stringArray[12] = "getDeclaredConstructor";
                    stringArray[13] = "class";
                    stringArray[14] = "put";
                    stringArray[15] = "createConstructorMap";
                    stringArray[16] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[17];
                    _buildConstructorMapFrom_closure4.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildConstructorMapFrom_closure4.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildConstructorMapFrom_closure4.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[182].call(descCollection, new _buildConstructorMapFrom_closure4(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, object2, map));
        }
        return (Map)ScriptBytecodeAdapter.castToType(map.get(), Map.class);
    }

    private static Map createConstructorMap(Object ctor, Object descriptor) {
        Object object;
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object desc = DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof Map ? descriptor : ScriptBytecodeAdapter.createMap(new Object[0]);
        Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
        Object object2 = callSiteArray[183].callGetProperty(ctor);
        ScriptBytecodeAdapter.setProperty(object2, null, map, "name");
        Object object3 = callSiteArray[184].callGetProperty(desc);
        Object object4 = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[185].callGetProperty(desc)) ? object : "Class constructor");
        ScriptBytecodeAdapter.setProperty(object4, null, map, "displayName");
        String string = "constructor";
        ScriptBytecodeAdapter.setProperty(string, null, map, "role");
        Object object5 = ctor;
        ScriptBytecodeAdapter.setProperty(object5, null, map, "constructor");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[186].callGetProperty(desc))) {
            callSiteArray[187].call(map, "params", callSiteArray[188].callStatic(JmxMetaMapBuilder.class, ctor, callSiteArray[189].callGetProperty(desc)));
        } else {
            callSiteArray[190].call(map, "params", callSiteArray[191].callStatic(JmxMetaMapBuilder.class, ctor));
        }
        return (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    public static Map buildOperationMapFrom(Object object) {
        Reference<Object> object2 = new Reference<Object>(object);
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object methods = callSiteArray[192].call(callSiteArray[193].callGetProperty(object2.get()));
        Reference<Map> ops = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        Reference<Object> declaredMethods = new Reference<Object>(ScriptBytecodeAdapter.getPropertySpreadSafe(JmxMetaMapBuilder.class, callSiteArray[194].call(callSiteArray[195].call(object2.get())), "name"));
        public class _buildOperationMapFrom_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference declaredMethods;
            private /* synthetic */ Reference object;
            private /* synthetic */ Reference ops;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _buildOperationMapFrom_closure5(Object _outerInstance, Object _thisObject, Reference declaredMethods, Reference object, Reference ops) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                CallSite[] callSiteArray = _buildOperationMapFrom_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.declaredMethods = reference3 = declaredMethods;
                this.object = reference2 = object;
                this.ops = reference = ops;
            }

            public Object doCall(Object method) {
                CallSite[] callSiteArray = _buildOperationMapFrom_closure5.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(this.declaredMethods.get(), callSiteArray[1].callGetProperty(method))) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(callSiteArray[3].callGetProperty(JmxMetaMapBuilder.class), callSiteArray[4].callGetProperty(method))) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(callSiteArray[6].callGetProperty(JmxMetaMapBuilder.class), callSiteArray[7].callGetProperty(method)))) {
                        String mName = ShortTypeHandling.castToString(callSiteArray[8].callGetProperty(method));
                        MetaProperty prop = (MetaProperty)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call((Object)mName, "get")) || DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call((Object)mName, "set")) ? callSiteArray[11].call(callSiteArray[12].callGetProperty(this.object.get()), callSiteArray[13].call(JmxBuilderTools.class, callSiteArray[14].call((Object)mName, ScriptBytecodeAdapter.createRange(3, -1, true)))) : null, MetaProperty.class);
                        if (!DefaultTypeTransformation.booleanUnbox(prop)) {
                            Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
                            String string = mName;
                            ScriptBytecodeAdapter.setProperty(string, null, map, "name");
                            Object object = callSiteArray[15].call(new GStringImpl(new Object[]{callSiteArray[16].callGetProperty(method), callSiteArray[17].call(callSiteArray[18].call(this.object.get()))}, new String[]{"Method ", " for class ", ""}));
                            ScriptBytecodeAdapter.setProperty(object, null, map, "displayName");
                            String string2 = "operation";
                            ScriptBytecodeAdapter.setProperty(string2, null, map, "role");
                            Object object2 = method;
                            ScriptBytecodeAdapter.setProperty(object2, null, map, "method");
                            callSiteArray[19].call(map, "params", callSiteArray[20].callCurrent((GroovyObject)this, method));
                            return callSiteArray[21].call(this.ops.get(), mName, map);
                        }
                        return null;
                    }
                    return null;
                }
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call(this.declaredMethods.get(), callSiteArray[23].callGetProperty(method))) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[24].call(callSiteArray[25].callGetProperty(JmxMetaMapBuilder.class), callSiteArray[26].callGetProperty(method))) || !DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call(callSiteArray[28].callGetProperty(JmxMetaMapBuilder.class), callSiteArray[29].callGetProperty(method)))) {
                    String mName = ShortTypeHandling.castToString(callSiteArray[30].callGetProperty(method));
                    MetaProperty prop = (MetaProperty)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(callSiteArray[31].call((Object)mName, "get")) || DefaultTypeTransformation.booleanUnbox(callSiteArray[32].call((Object)mName, "set")) ? callSiteArray[33].call(callSiteArray[34].callGetProperty(this.object.get()), callSiteArray[35].call(JmxBuilderTools.class, callSiteArray[36].call((Object)mName, ScriptBytecodeAdapter.createRange(3, -1, true)))) : null, MetaProperty.class);
                    if (!DefaultTypeTransformation.booleanUnbox(prop)) {
                        Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
                        String string = mName;
                        ScriptBytecodeAdapter.setProperty(string, null, map, "name");
                        Object object = callSiteArray[37].call(new GStringImpl(new Object[]{callSiteArray[38].callGetProperty(method), callSiteArray[39].call(callSiteArray[40].call(this.object.get()))}, new String[]{"Method ", " for class ", ""}));
                        ScriptBytecodeAdapter.setProperty(object, null, map, "displayName");
                        String string3 = "operation";
                        ScriptBytecodeAdapter.setProperty(string3, null, map, "role");
                        Object object3 = method;
                        ScriptBytecodeAdapter.setProperty(object3, null, map, "method");
                        callSiteArray[41].call(map, "params", callSiteArray[42].callCurrent((GroovyObject)this, method));
                        return callSiteArray[43].call(this.ops.get(), mName, map);
                    }
                    return null;
                }
                return null;
            }

            public Object getDeclaredMethods() {
                CallSite[] callSiteArray = _buildOperationMapFrom_closure5.$getCallSiteArray();
                return this.declaredMethods.get();
            }

            public Object getObject() {
                CallSite[] callSiteArray = _buildOperationMapFrom_closure5.$getCallSiteArray();
                return this.object.get();
            }

            public Object getOps() {
                CallSite[] callSiteArray = _buildOperationMapFrom_closure5.$getCallSiteArray();
                return this.ops.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _buildOperationMapFrom_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "contains";
                stringArray[1] = "name";
                stringArray[2] = "contains";
                stringArray[3] = "OPS_EXCEPTION_LIST";
                stringArray[4] = "name";
                stringArray[5] = "contains";
                stringArray[6] = "OPS_EXCEPTION_LIST";
                stringArray[7] = "name";
                stringArray[8] = "name";
                stringArray[9] = "startsWith";
                stringArray[10] = "startsWith";
                stringArray[11] = "getMetaProperty";
                stringArray[12] = "metaClass";
                stringArray[13] = "uncapitalize";
                stringArray[14] = "getAt";
                stringArray[15] = "toString";
                stringArray[16] = "name";
                stringArray[17] = "getName";
                stringArray[18] = "getClass";
                stringArray[19] = "put";
                stringArray[20] = "buildParameterMapFrom";
                stringArray[21] = "put";
                stringArray[22] = "contains";
                stringArray[23] = "name";
                stringArray[24] = "contains";
                stringArray[25] = "OPS_EXCEPTION_LIST";
                stringArray[26] = "name";
                stringArray[27] = "contains";
                stringArray[28] = "OPS_EXCEPTION_LIST";
                stringArray[29] = "name";
                stringArray[30] = "name";
                stringArray[31] = "startsWith";
                stringArray[32] = "startsWith";
                stringArray[33] = "getMetaProperty";
                stringArray[34] = "metaClass";
                stringArray[35] = "uncapitalize";
                stringArray[36] = "getAt";
                stringArray[37] = "toString";
                stringArray[38] = "name";
                stringArray[39] = "getName";
                stringArray[40] = "getClass";
                stringArray[41] = "put";
                stringArray[42] = "buildParameterMapFrom";
                stringArray[43] = "put";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[44];
                _buildOperationMapFrom_closure5.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_buildOperationMapFrom_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _buildOperationMapFrom_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[196].call(methods, new _buildOperationMapFrom_closure5(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, declaredMethods, object2, ops));
        return (Map)ScriptBytecodeAdapter.castToType(ops.get(), Map.class);
    }

    public static Map buildOperationMapFrom(Object object, Object descCollection) {
        Reference<Object> object2 = new Reference<Object>(object);
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Reference<Map> map = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof String && DefaultTypeTransformation.booleanUnbox(callSiteArray[197].call(descCollection, "*"))) {
            Object object3 = callSiteArray[198].callStatic(JmxMetaMapBuilder.class, object2.get());
            map.set((Map)object3);
        }
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof List) {
            public class _buildOperationMapFrom_closure6
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference object;
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildOperationMapFrom_closure6(Object _outerInstance, Object _thisObject, Reference object, Reference map) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure6.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.object = reference2 = object;
                    this.map = reference = map;
                }

                public Object doCall(Object opName) {
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure6.$getCallSiteArray();
                    Object method = null;
                    Object m = null;
                    Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].callGetProperty(this.object.get()))), Iterator.class);
                    while (iterator.hasNext()) {
                        Object var6_6;
                        m = iterator.next();
                        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(callSiteArray[4].callGetProperty(m), opName))) continue;
                        method = var6_6 = m;
                        break;
                    }
                    if (DefaultTypeTransformation.booleanUnbox(method)) {
                        return callSiteArray[5].call(this.map.get(), opName, callSiteArray[6].callCurrent(this, this.object.get(), method, "*"));
                    }
                    return null;
                }

                public Object getObject() {
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure6.$getCallSiteArray();
                    return this.object.get();
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure6.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildOperationMapFrom_closure6.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "iterator";
                    stringArray[1] = "getMethods";
                    stringArray[2] = "metaClass";
                    stringArray[3] = "equals";
                    stringArray[4] = "name";
                    stringArray[5] = "put";
                    stringArray[6] = "createOperationMap";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[7];
                    _buildOperationMapFrom_closure6.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildOperationMapFrom_closure6.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildOperationMapFrom_closure6.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[199].call(descCollection, new _buildOperationMapFrom_closure6(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, object2, map));
        }
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof Map) {
            public class _buildOperationMapFrom_closure7
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference object;
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildOperationMapFrom_closure7(Object _outerInstance, Object _thisObject, Reference object, Reference map) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure7.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.object = reference2 = object;
                    this.map = reference = map;
                }

                public Object doCall(Object opName, Object descriptor) {
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure7.$getCallSiteArray();
                    Object params = null;
                    Object method = null;
                    if (DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof String && DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(descriptor, "*"))) {
                        Object object;
                        method = object = callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].callGetProperty(this.object.get()), this.object.get(), opName), 0);
                    } else {
                        Object object;
                        if (DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof List) {
                            Object object2;
                            params = object2 = descriptor;
                        }
                        if (DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof Map) {
                            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].callGetProperty(descriptor)) && callSiteArray[5].callGetProperty(descriptor) instanceof Map) {
                                Object object3;
                                params = object3 = callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].callGetPropertySafe(descriptor)));
                            }
                            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[9].callGetProperty(descriptor)) && callSiteArray[10].callGetProperty(descriptor) instanceof List) {
                                Object object4;
                                params = object4 = callSiteArray[11].callGetProperty(descriptor);
                            }
                        }
                        if (DefaultTypeTransformation.booleanUnbox(params)) {
                            Reference<List> paramTypes = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
                            public class _closure14
                            extends Closure
                            implements GeneratedClosure {
                                private /* synthetic */ Reference paramTypes;
                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                public static transient /* synthetic */ boolean __$stMC;
                                private static /* synthetic */ SoftReference $callSiteArray;

                                public _closure14(Object _outerInstance, Object _thisObject, Reference paramTypes) {
                                    Reference reference;
                                    CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                    super(_outerInstance, _thisObject);
                                    this.paramTypes = reference = paramTypes;
                                }

                                public Object doCall(Object key) {
                                    CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                    return callSiteArray[0].call(this.paramTypes.get(), callSiteArray[1].call(callSiteArray[2].callGetProperty(JmxBuilderTools.class), callSiteArray[3].call(JmxBuilderTools.class, key)));
                                }

                                public Object getParamTypes() {
                                    CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                    return this.paramTypes.get();
                                }

                                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                    if (this.getClass() != _closure14.class) {
                                        return ScriptBytecodeAdapter.initMetaClass(this);
                                    }
                                    ClassInfo classInfo = $staticClassInfo;
                                    if (classInfo == null) {
                                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                    }
                                    return classInfo.getMetaClass();
                                }

                                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                    stringArray[0] = "leftShift";
                                    stringArray[1] = "getAt";
                                    stringArray[2] = "TYPE_MAP";
                                    stringArray[3] = "getNormalizedType";
                                }

                                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                    String[] stringArray = new String[4];
                                    _closure14.$createCallSiteArray_1(stringArray);
                                    return new CallSiteArray(_closure14.class, stringArray);
                                }

                                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                    CallSiteArray callSiteArray;
                                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                        callSiteArray = _closure14.$createCallSiteArray();
                                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                    }
                                    return callSiteArray.array;
                                }
                            }
                            callSiteArray[12].callSafe(params, new _closure14(this, this.getThisObject(), paramTypes));
                            List list = paramTypes.get();
                            List list2 = DefaultTypeTransformation.booleanUnbox(list) ? list : null;
                            params = list2;
                        }
                        Object[] signature = ScriptBytecodeAdapter.compareNotEqual(params, null) ? (Object[])ScriptBytecodeAdapter.castToType(params, Object[].class) : null;
                        Object methods = callSiteArray[13].call(callSiteArray[14].callGetProperty(this.object.get()), this.object.get(), opName, signature);
                        Object object5 = callSiteArray[15].call(methods, 0);
                        method = object = DefaultTypeTransformation.booleanUnbox(object5) ? object5 : null;
                    }
                    if (DefaultTypeTransformation.booleanUnbox(method)) {
                        return callSiteArray[16].call(this.map.get(), opName, callSiteArray[17].callCurrent(this, this.object.get(), method, descriptor));
                    }
                    return null;
                }

                public Object call(Object opName, Object descriptor) {
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure7.$getCallSiteArray();
                    return callSiteArray[18].callCurrent(this, opName, descriptor);
                }

                public Object getObject() {
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure7.$getCallSiteArray();
                    return this.object.get();
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildOperationMapFrom_closure7.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildOperationMapFrom_closure7.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "equals";
                    stringArray[1] = "getAt";
                    stringArray[2] = "respondsTo";
                    stringArray[3] = "metaClass";
                    stringArray[4] = "params";
                    stringArray[5] = "params";
                    stringArray[6] = "toList";
                    stringArray[7] = "keySet";
                    stringArray[8] = "params";
                    stringArray[9] = "params";
                    stringArray[10] = "params";
                    stringArray[11] = "params";
                    stringArray[12] = "each";
                    stringArray[13] = "respondsTo";
                    stringArray[14] = "metaClass";
                    stringArray[15] = "getAt";
                    stringArray[16] = "put";
                    stringArray[17] = "createOperationMap";
                    stringArray[18] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[19];
                    _buildOperationMapFrom_closure7.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildOperationMapFrom_closure7.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildOperationMapFrom_closure7.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[200].call(descCollection, new _buildOperationMapFrom_closure7(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, object2, map));
        }
        return (Map)ScriptBytecodeAdapter.castToType(map.get(), Map.class);
    }

    private static Map createOperationMap(Object object, Object method, Object descriptor) {
        Object object2;
        Object object3;
        Object object4;
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object desc = DefaultTypeTransformation.booleanUnbox(descriptor) && descriptor instanceof Map ? descriptor : ScriptBytecodeAdapter.createMap(new Object[0]);
        Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
        Object object5 = callSiteArray[201].callGetProperty(method);
        ScriptBytecodeAdapter.setProperty(object5, null, map, "name");
        Object object6 = callSiteArray[202].callGetProperty(desc);
        Object object7 = DefaultTypeTransformation.booleanUnbox(object6) ? object6 : (DefaultTypeTransformation.booleanUnbox(object4 = callSiteArray[203].callGetProperty(desc)) ? object4 : callSiteArray[204].call(new GStringImpl(new Object[]{callSiteArray[205].callGetProperty(method), callSiteArray[206].call(callSiteArray[207].call(object))}, new String[]{"Method ", " for class ", ""})));
        ScriptBytecodeAdapter.setProperty(object7, null, map, "displayName");
        String string = "operation";
        ScriptBytecodeAdapter.setProperty(string, null, map, "role");
        Object object8 = method;
        ScriptBytecodeAdapter.setProperty(object8, null, map, "method");
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[208].call(desc), 0) && DefaultTypeTransformation.booleanUnbox(callSiteArray[209].callGetProperty(desc))) {
            callSiteArray[210].call(map, "params", callSiteArray[211].callStatic(JmxMetaMapBuilder.class, method, callSiteArray[212].callGetProperty(desc)));
        } else {
            callSiteArray[213].call(map, "params", callSiteArray[214].callStatic(JmxMetaMapBuilder.class, method));
        }
        Object object9 = callSiteArray[215].callGetProperty(desc);
        Object listener = DefaultTypeTransformation.booleanUnbox(object9) ? object9 : (DefaultTypeTransformation.booleanUnbox(object3 = callSiteArray[216].callGetProperty(desc)) ? object3 : (DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[217].callGetProperty(desc)) ? object2 : callSiteArray[218].callGetProperty(desc)));
        if (DefaultTypeTransformation.booleanUnbox(listener)) {
            Map map2 = ScriptBytecodeAdapter.createMap(new Object[0]);
            ScriptBytecodeAdapter.setProperty(map2, null, map, "methodListener");
            Object object10 = listener;
            ScriptBytecodeAdapter.setProperty(object10, null, callSiteArray[219].callGetProperty(map), "callback");
            Object object11 = callSiteArray[220].callGetProperty(method);
            ScriptBytecodeAdapter.setProperty(object11, null, callSiteArray[221].callGetProperty(map), "target");
            String string2 = "operationCallListener";
            ScriptBytecodeAdapter.setProperty(string2, null, callSiteArray[222].callGetProperty(map), "type");
        }
        return (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    public static Map buildParameterMapFrom(Object method) {
        Reference<Object> method2 = new Reference<Object>(method);
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Reference<Map> map = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        if (!DefaultTypeTransformation.booleanUnbox(method2.get())) {
            return (Map)ScriptBytecodeAdapter.castToType(map.get(), Map.class);
        }
        Object params = callSiteArray[223].call(method2.get());
        if (DefaultTypeTransformation.booleanUnbox(params)) {
            public class _buildParameterMapFrom_closure8
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference map;
                private /* synthetic */ Reference method;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildParameterMapFrom_closure8(Object _outerInstance, Object _thisObject, Reference map, Reference method) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure8.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.map = reference2 = map;
                    this.method = reference = method;
                }

                public Object doCall(Object param) {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure8.$getCallSiteArray();
                    return callSiteArray[0].call(this.map.get(), callSiteArray[1].callGetProperty(param), callSiteArray[2].callCurrent(this, this.method.get(), param, "*"));
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure8.$getCallSiteArray();
                    return this.map.get();
                }

                public Object getMethod() {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure8.$getCallSiteArray();
                    return this.method.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildParameterMapFrom_closure8.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "put";
                    stringArray[1] = "name";
                    stringArray[2] = "createParameterMap";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _buildParameterMapFrom_closure8.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildParameterMapFrom_closure8.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildParameterMapFrom_closure8.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[224].call(params, new _buildParameterMapFrom_closure8(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, map, method2));
        }
        return (Map)ScriptBytecodeAdapter.castToType(map.get(), Map.class);
    }

    public static Map buildParameterMapFrom(Object method, Object descCollection) {
        Reference<Object> method2 = new Reference<Object>(method);
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Reference<Map> map = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        if (!DefaultTypeTransformation.booleanUnbox(method2.get())) {
            return (Map)ScriptBytecodeAdapter.castToType(map.get(), Map.class);
        }
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof Map) {
            public class _buildParameterMapFrom_closure9
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference method;
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildParameterMapFrom_closure9(Object _outerInstance, Object _thisObject, Reference method, Reference map) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure9.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.method = reference2 = method;
                    this.map = reference = map;
                }

                public Object doCall(Object param, Object paramMap) {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure9.$getCallSiteArray();
                    Object type = callSiteArray[0].callCurrent(this, this.method.get(), callSiteArray[1].call(JmxBuilderTools.class, param));
                    if (DefaultTypeTransformation.booleanUnbox(type)) {
                        return callSiteArray[2].call(this.map.get(), callSiteArray[3].callGetProperty(type), callSiteArray[4].callCurrent(this, this.method.get(), type, paramMap));
                    }
                    return null;
                }

                public Object call(Object param, Object paramMap) {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure9.$getCallSiteArray();
                    return callSiteArray[5].callCurrent(this, param, paramMap);
                }

                public Object getMethod() {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure9.$getCallSiteArray();
                    return this.method.get();
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure9.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildParameterMapFrom_closure9.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "getParamTypeByName";
                    stringArray[1] = "getNormalizedType";
                    stringArray[2] = "put";
                    stringArray[3] = "name";
                    stringArray[4] = "createParameterMap";
                    stringArray[5] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[6];
                    _buildParameterMapFrom_closure9.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildParameterMapFrom_closure9.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildParameterMapFrom_closure9.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[225].call(descCollection, new _buildParameterMapFrom_closure9(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, method2, map));
        } else if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof List) {
            public class _buildParameterMapFrom_closure10
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference method;
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildParameterMapFrom_closure10(Object _outerInstance, Object _thisObject, Reference method, Reference map) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure10.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.method = reference2 = method;
                    this.map = reference = map;
                }

                public Object doCall(Object param) {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure10.$getCallSiteArray();
                    Object type = callSiteArray[0].callCurrent(this, this.method.get(), callSiteArray[1].call(JmxBuilderTools.class, param));
                    if (DefaultTypeTransformation.booleanUnbox(type)) {
                        return callSiteArray[2].call(this.map.get(), callSiteArray[3].callGetProperty(type), callSiteArray[4].callCurrent(this, this.method.get(), type, "*"));
                    }
                    return null;
                }

                public Object getMethod() {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure10.$getCallSiteArray();
                    return this.method.get();
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildParameterMapFrom_closure10.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildParameterMapFrom_closure10.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "getParamTypeByName";
                    stringArray[1] = "getNormalizedType";
                    stringArray[2] = "put";
                    stringArray[3] = "name";
                    stringArray[4] = "createParameterMap";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _buildParameterMapFrom_closure10.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildParameterMapFrom_closure10.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildParameterMapFrom_closure10.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[226].call(descCollection, new _buildParameterMapFrom_closure10(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, method2, map));
        }
        return (Map)ScriptBytecodeAdapter.castToType(map.get(), Map.class);
    }

    private static Map createParameterMap(Object method, Object type, Object descriptor) {
        Object object;
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object desc = descriptor instanceof Map ? descriptor : ScriptBytecodeAdapter.createMap(new Object[0]);
        Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
        Object object2 = callSiteArray[227].callGetProperty(desc);
        Object object3 = DefaultTypeTransformation.booleanUnbox(object2) ? object2 : callSiteArray[228].callGetProperty(type);
        ScriptBytecodeAdapter.setProperty(object3, null, map, "name");
        Object object4 = callSiteArray[229].callGetProperty(desc);
        Object object5 = DefaultTypeTransformation.booleanUnbox(object4) ? object4 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[230].callGetProperty(desc)) ? object : callSiteArray[231].call(new GStringImpl(new Object[]{callSiteArray[232].callGetProperty(type), callSiteArray[233].callGetProperty(method)}, new String[]{"Parameter ", " for ", ""})));
        ScriptBytecodeAdapter.setProperty(object5, null, map, "displayName");
        Object object6 = type;
        ScriptBytecodeAdapter.setProperty(object6, null, map, "type");
        Object object7 = method;
        ScriptBytecodeAdapter.setProperty(object7, null, map, "method");
        return (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    private static Object getParamTypeByName(Object method, Object typeName) {
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Object type = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[234].call(callSiteArray[235].call(method)), Iterator.class);
        while (iterator.hasNext()) {
            type = iterator.next();
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[236].call(callSiteArray[237].callGetProperty(type), typeName))) continue;
            return type;
        }
        return null;
    }

    public static Object buildListenerMapFrom(Object descCollection) {
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Reference<Map> map = new Reference<Map>(ScriptBytecodeAdapter.createMap(new Object[0]));
        if (DefaultTypeTransformation.booleanUnbox(descCollection) && descCollection instanceof Map) {
            public class _buildListenerMapFrom_closure11
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference map;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _buildListenerMapFrom_closure11(Object _outerInstance, Object _thisObject, Reference map) {
                    Reference reference;
                    CallSite[] callSiteArray = _buildListenerMapFrom_closure11.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.map = reference = map;
                }

                public Object doCall(Object name, Object listenerMap) {
                    CallSite[] callSiteArray = _buildListenerMapFrom_closure11.$getCallSiteArray();
                    return callSiteArray[0].call(this.map.get(), name, callSiteArray[1].callCurrent((GroovyObject)this, listenerMap));
                }

                public Object call(Object name, Object listenerMap) {
                    CallSite[] callSiteArray = _buildListenerMapFrom_closure11.$getCallSiteArray();
                    return callSiteArray[2].callCurrent(this, name, listenerMap);
                }

                public Object getMap() {
                    CallSite[] callSiteArray = _buildListenerMapFrom_closure11.$getCallSiteArray();
                    return this.map.get();
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _buildListenerMapFrom_closure11.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "put";
                    stringArray[1] = "createListenerMap";
                    stringArray[2] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _buildListenerMapFrom_closure11.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_buildListenerMapFrom_closure11.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _buildListenerMapFrom_closure11.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[238].call(descCollection, new _buildListenerMapFrom_closure11(JmxMetaMapBuilder.class, JmxMetaMapBuilder.class, map));
        }
        return map.get();
    }

    public static Map createListenerMap(Object descriptor) {
        Object object;
        CallSite[] callSiteArray = JmxMetaMapBuilder.$getCallSiteArray();
        Map map = ScriptBytecodeAdapter.createMap(new Object[0]);
        String string = "eventListener";
        ScriptBytecodeAdapter.setProperty(string, null, map, "type");
        Object object2 = callSiteArray[239].callGetProperty(descriptor);
        ScriptBytecodeAdapter.setProperty(object2, null, map, "event");
        Object object3 = callSiteArray[240].callGetProperty(descriptor);
        Object object4 = DefaultTypeTransformation.booleanUnbox(object3) ? object3 : (DefaultTypeTransformation.booleanUnbox(object = callSiteArray[241].callGetProperty(descriptor)) ? object : callSiteArray[242].callGetProperty(descriptor));
        ScriptBytecodeAdapter.setProperty(object4, null, map, "from");
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[243].callGetProperty(map))) {
            throw (Throwable)callSiteArray[244].callConstructor(JmxBuilderException.class, "Missing event source: specify source ObjectName (i.e. from:'...').");
        }
        try {
            Object object5 = callSiteArray[245].callGetProperty(map) instanceof String ? callSiteArray[246].callConstructor(ObjectName.class, callSiteArray[247].callGetProperty(map)) : callSiteArray[248].callGetProperty(map);
            ScriptBytecodeAdapter.setProperty(object5, null, map, "from");
        }
        catch (Exception e) {
            throw (Throwable)callSiteArray[249].callConstructor(JmxBuilderException.class, e);
        }
        Object object6 = callSiteArray[250].callGetProperty(descriptor);
        ScriptBytecodeAdapter.setProperty(object6, null, map, "callback");
        return (Map)ScriptBytecodeAdapter.castToType(map, Map.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxMetaMapBuilder.class) {
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
        List list = ScriptBytecodeAdapter.createList(new Object[]{"class", "descriptor", "jmx", "metaClass"});
        ATTRIB_EXCEPTION_LIST = list;
        List list2 = ScriptBytecodeAdapter.createList(new Object[]{"clone", "equals", "finalize", "getClass", "getProperty", "hashCode", "invokeMethod", "notify", "notifyAll", "setProperty", "toString", "wait"});
        OPS_EXCEPTION_LIST = list2;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "getMetaProperty";
        stringArray[2] = "metaClass";
        stringArray[3] = "getMetaProperty";
        stringArray[4] = "metaClass";
        stringArray[5] = "getProperty";
        stringArray[6] = "metaClass";
        stringArray[7] = "getClass";
        stringArray[8] = "name";
        stringArray[9] = "size";
        stringArray[10] = "name";
        stringArray[11] = "name";
        stringArray[12] = "getClass";
        stringArray[13] = "getObjectName";
        stringArray[14] = "toString";
        stringArray[15] = "canonicalName";
        stringArray[16] = "class";
        stringArray[17] = "buildAttributeMapFrom";
        stringArray[18] = "buildConstructorMapFrom";
        stringArray[19] = "buildOperationMapFrom";
        stringArray[20] = "name";
        stringArray[21] = "getClass";
        stringArray[22] = "desc";
        stringArray[23] = "desc";
        stringArray[24] = "buildAttributeMapFrom";
        stringArray[25] = "attributes";
        stringArray[26] = "attribs";
        stringArray[27] = "buildConstructorMapFrom";
        stringArray[28] = "constructors";
        stringArray[29] = "ctors";
        stringArray[30] = "buildOperationMapFrom";
        stringArray[31] = "operations";
        stringArray[32] = "ops";
        stringArray[33] = "buildListenerMapFrom";
        stringArray[34] = "listeners";
        stringArray[35] = "server";
        stringArray[36] = "mbeanServer";
        stringArray[37] = "getObjectName";
        stringArray[38] = "buildDefaultObjectName";
        stringArray[39] = "DEFAULT_DOMAIN";
        stringArray[40] = "DEFAULT_NAME_TYPE";
        stringArray[41] = "name";
        stringArray[42] = "getClass";
        stringArray[43] = "buildDefaultObjectName";
        stringArray[44] = "DEFAULT_DOMAIN";
        stringArray[45] = "DEFAULT_NAME_TYPE";
        stringArray[46] = "toString";
        stringArray[47] = "canonicalName";
        stringArray[48] = "class";
        stringArray[49] = "buildAttributeMapFrom";
        stringArray[50] = "buildConstructorMapFrom";
        stringArray[51] = "buildOperationMapFrom";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "size";
        stringArray[54] = "name";
        stringArray[55] = "target";
        stringArray[56] = "getObjectName";
        stringArray[57] = "name";
        stringArray[58] = "getClass";
        stringArray[59] = "toString";
        stringArray[60] = "canonicalName";
        stringArray[61] = "class";
        stringArray[62] = "buildAttributeMapFrom";
        stringArray[63] = "buildConstructorMapFrom";
        stringArray[64] = "buildOperationMapFrom";
        stringArray[65] = "name";
        stringArray[66] = "getClass";
        stringArray[67] = "desc";
        stringArray[68] = "desc";
        stringArray[69] = "buildAttributeMapFrom";
        stringArray[70] = "attributes";
        stringArray[71] = "attribs";
        stringArray[72] = "buildConstructorMapFrom";
        stringArray[73] = "constructors";
        stringArray[74] = "ctors";
        stringArray[75] = "buildOperationMapFrom";
        stringArray[76] = "operations";
        stringArray[77] = "ops";
        stringArray[78] = "buildListenerMapFrom";
        stringArray[79] = "listeners";
        stringArray[80] = "server";
        stringArray[81] = "mbeanServer";
        stringArray[82] = "getObjectName";
        stringArray[83] = "buildDefaultObjectName";
        stringArray[84] = "DEFAULT_DOMAIN";
        stringArray[85] = "DEFAULT_NAME_TYPE";
        stringArray[86] = "size";
        stringArray[87] = "name";
        stringArray[88] = "target";
        stringArray[89] = "getObjectName";
        stringArray[90] = "name";
        stringArray[91] = "getClass";
        stringArray[92] = "toString";
        stringArray[93] = "canonicalName";
        stringArray[94] = "class";
        stringArray[95] = "buildAttributeMapFrom";
        stringArray[96] = "buildConstructorMapFrom";
        stringArray[97] = "buildOperationMapFrom";
        stringArray[98] = "name";
        stringArray[99] = "getClass";
        stringArray[100] = "desc";
        stringArray[101] = "desc";
        stringArray[102] = "buildAttributeMapFrom";
        stringArray[103] = "attributes";
        stringArray[104] = "attribs";
        stringArray[105] = "buildConstructorMapFrom";
        stringArray[106] = "constructors";
        stringArray[107] = "ctors";
        stringArray[108] = "buildOperationMapFrom";
        stringArray[109] = "operations";
        stringArray[110] = "ops";
        stringArray[111] = "buildListenerMapFrom";
        stringArray[112] = "listeners";
        stringArray[113] = "server";
        stringArray[114] = "mbeanServer";
        stringArray[115] = "getObjectName";
        stringArray[116] = "buildDefaultObjectName";
        stringArray[117] = "DEFAULT_DOMAIN";
        stringArray[118] = "DEFAULT_NAME_TYPE";
        stringArray[119] = "name";
        stringArray[120] = "<$constructor$>";
        stringArray[121] = "name";
        stringArray[122] = "name";
        stringArray[123] = "name";
        stringArray[124] = "getProperties";
        stringArray[125] = "metaClass";
        stringArray[126] = "each";
        stringArray[127] = "equals";
        stringArray[128] = "buildAttributeMapFrom";
        stringArray[129] = "each";
        stringArray[130] = "each";
        stringArray[131] = "capitalize";
        stringArray[132] = "name";
        stringArray[133] = "type";
        stringArray[134] = "name";
        stringArray[135] = "type";
        stringArray[136] = "name";
        stringArray[137] = "type";
        stringArray[138] = "name";
        stringArray[139] = "type";
        stringArray[140] = "desc";
        stringArray[141] = "description";
        stringArray[142] = "toString";
        stringArray[143] = "name";
        stringArray[144] = "type";
        stringArray[145] = "readable";
        stringArray[146] = "readable";
        stringArray[147] = "readable";
        stringArray[148] = "plus";
        stringArray[149] = "writable";
        stringArray[150] = "writable";
        stringArray[151] = "writable";
        stringArray[152] = "plus";
        stringArray[153] = "defaultValue";
        stringArray[154] = "default";
        stringArray[155] = "onChange";
        stringArray[156] = "onChanged";
        stringArray[157] = "methodListener";
        stringArray[158] = "plus";
        stringArray[159] = "methodListener";
        stringArray[160] = "methodListener";
        stringArray[161] = "methodListener";
        stringArray[162] = "getDeclaredConstructors";
        stringArray[163] = "getClass";
        stringArray[164] = "iterator";
        stringArray[165] = "name";
        stringArray[166] = "toString";
        stringArray[167] = "getName";
        stringArray[168] = "getClass";
        stringArray[169] = "put";
        stringArray[170] = "buildParameterMapFrom";
        stringArray[171] = "put";
        stringArray[172] = "plus";
        stringArray[173] = "plus";
        stringArray[174] = "getAt";
        stringArray[175] = "name";
        stringArray[176] = "plus";
        stringArray[177] = "lastIndexOf";
        stringArray[178] = "name";
        stringArray[179] = "next";
        stringArray[180] = "equals";
        stringArray[181] = "buildConstructorMapFrom";
        stringArray[182] = "each";
        stringArray[183] = "name";
        stringArray[184] = "description";
        stringArray[185] = "desc";
        stringArray[186] = "params";
        stringArray[187] = "put";
        stringArray[188] = "buildParameterMapFrom";
        stringArray[189] = "params";
        stringArray[190] = "put";
        stringArray[191] = "buildParameterMapFrom";
        stringArray[192] = "getMethods";
        stringArray[193] = "metaClass";
        stringArray[194] = "getDeclaredMethods";
        stringArray[195] = "getClass";
        stringArray[196] = "each";
        stringArray[197] = "equals";
        stringArray[198] = "buildOperationMapFrom";
        stringArray[199] = "each";
        stringArray[200] = "each";
        stringArray[201] = "name";
        stringArray[202] = "description";
        stringArray[203] = "desc";
        stringArray[204] = "toString";
        stringArray[205] = "name";
        stringArray[206] = "getName";
        stringArray[207] = "getClass";
        stringArray[208] = "size";
        stringArray[209] = "params";
        stringArray[210] = "put";
        stringArray[211] = "buildParameterMapFrom";
        stringArray[212] = "params";
        stringArray[213] = "put";
        stringArray[214] = "buildParameterMapFrom";
        stringArray[215] = "onInvoke";
        stringArray[216] = "onInvoked";
        stringArray[217] = "onCall";
        stringArray[218] = "onCalled";
        stringArray[219] = "methodListener";
        stringArray[220] = "name";
        stringArray[221] = "methodListener";
        stringArray[222] = "methodListener";
        stringArray[223] = "getParameterTypes";
        stringArray[224] = "each";
        stringArray[225] = "each";
        stringArray[226] = "each";
        stringArray[227] = "name";
        stringArray[228] = "name";
        stringArray[229] = "description";
        stringArray[230] = "desc";
        stringArray[231] = "toString";
        stringArray[232] = "name";
        stringArray[233] = "name";
        stringArray[234] = "iterator";
        stringArray[235] = "getParameterTypes";
        stringArray[236] = "equals";
        stringArray[237] = "name";
        stringArray[238] = "each";
        stringArray[239] = "event";
        stringArray[240] = "from";
        stringArray[241] = "source";
        stringArray[242] = "broadcaster";
        stringArray[243] = "from";
        stringArray[244] = "<$constructor$>";
        stringArray[245] = "from";
        stringArray[246] = "<$constructor$>";
        stringArray[247] = "from";
        stringArray[248] = "from";
        stringArray[249] = "<$constructor$>";
        stringArray[250] = "call";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[251];
        JmxMetaMapBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxMetaMapBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxMetaMapBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


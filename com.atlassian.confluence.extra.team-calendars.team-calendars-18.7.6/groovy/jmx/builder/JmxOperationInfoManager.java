/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxBuilderTools;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaMethod;
import groovy.lang.MetaProperty;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxOperationInfoManager
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxOperationInfoManager() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static List<ModelMBeanConstructorInfo> getConstructorInfosFromMap(Map metaMap) {
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(metaMap)) {
            return (List)ScriptBytecodeAdapter.castToType(null, List.class);
        }
        Reference<List> ctors = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        public class _getConstructorInfosFromMap_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference ctors;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getConstructorInfosFromMap_closure1(Object _outerInstance, Object _thisObject, Reference ctors) {
                Reference reference;
                CallSite[] callSiteArray = _getConstructorInfosFromMap_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.ctors = reference = ctors;
            }

            public Object doCall(Object ctorName, Object map) {
                CallSite[] callSiteArray = _getConstructorInfosFromMap_closure1.$getCallSiteArray();
                ModelMBeanConstructorInfo info = (ModelMBeanConstructorInfo)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent((GroovyObject)this, map), ModelMBeanConstructorInfo.class);
                return callSiteArray[1].call(this.ctors.get(), info);
            }

            public Object call(Object ctorName, Object map) {
                CallSite[] callSiteArray = _getConstructorInfosFromMap_closure1.$getCallSiteArray();
                return callSiteArray[2].callCurrent(this, ctorName, map);
            }

            public Object getCtors() {
                CallSite[] callSiteArray = _getConstructorInfosFromMap_closure1.$getCallSiteArray();
                return this.ctors.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getConstructorInfosFromMap_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getConstructorInfoFromMap";
                stringArray[1] = "leftShift";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _getConstructorInfosFromMap_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getConstructorInfosFromMap_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getConstructorInfosFromMap_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[0].call((Object)metaMap, new _getConstructorInfosFromMap_closure1(JmxOperationInfoManager.class, JmxOperationInfoManager.class, ctors));
        return (List)ScriptBytecodeAdapter.castToType(ctors.get(), List.class);
    }

    public static ModelMBeanConstructorInfo getConstructorInfoFromMap(Map map) {
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(map)) {
            return (ModelMBeanConstructorInfo)ScriptBytecodeAdapter.castToType(null, ModelMBeanConstructorInfo.class);
        }
        Object ctor = callSiteArray[1].call((Object)map, "constructor");
        if (!DefaultTypeTransformation.booleanUnbox(ctor)) {
            throw (Throwable)callSiteArray[2].callConstructor(JmxBuilderException.class, "Unable generate ModelMBeanConstructorInfo, missing constructor reference.");
        }
        MBeanParameterInfo[] params = (MBeanParameterInfo[])ScriptBytecodeAdapter.castToType(callSiteArray[3].callStatic(JmxOperationInfoManager.class, callSiteArray[4].call((Object)map, "params")), MBeanParameterInfo[].class);
        Reference<Descriptor> desc = new Reference<Descriptor>((Descriptor)ScriptBytecodeAdapter.castToType(callSiteArray[5].callConstructor(DescriptorSupport.class), Descriptor.class));
        callSiteArray[6].call(desc.get(), callSiteArray[7].callGetProperty(JmxBuilderTools.class), callSiteArray[8].call((Object)map, callSiteArray[9].callGetProperty(JmxBuilderTools.class)));
        callSiteArray[10].call(desc.get(), callSiteArray[11].callGetProperty(JmxBuilderTools.class), callSiteArray[12].callGetProperty(JmxBuilderTools.class));
        callSiteArray[13].call(desc.get(), callSiteArray[14].callGetProperty(JmxBuilderTools.class), callSiteArray[15].call((Object)map, callSiteArray[16].callGetProperty(JmxBuilderTools.class)));
        callSiteArray[17].call(desc.get(), callSiteArray[18].callGetProperty(JmxBuilderTools.class), callSiteArray[19].call((Object)map, callSiteArray[20].callGetProperty(JmxBuilderTools.class)));
        public class _getConstructorInfoFromMap_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference desc;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getConstructorInfoFromMap_closure2(Object _outerInstance, Object _thisObject, Reference desc) {
                Reference reference;
                CallSite[] callSiteArray = _getConstructorInfoFromMap_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.desc = reference = desc;
            }

            public Object doCall(Object key, Object value) {
                CallSite[] callSiteArray = _getConstructorInfoFromMap_closure2.$getCallSiteArray();
                return callSiteArray[0].call(this.desc.get(), key, value);
            }

            public Object call(Object key, Object value) {
                CallSite[] callSiteArray = _getConstructorInfoFromMap_closure2.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, key, value);
            }

            public Descriptor getDesc() {
                CallSite[] callSiteArray = _getConstructorInfoFromMap_closure2.$getCallSiteArray();
                return (Descriptor)ScriptBytecodeAdapter.castToType(this.desc.get(), Descriptor.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getConstructorInfoFromMap_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "setField";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _getConstructorInfoFromMap_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getConstructorInfoFromMap_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getConstructorInfoFromMap_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[21].call((Object)map, new _getConstructorInfoFromMap_closure2(JmxOperationInfoManager.class, JmxOperationInfoManager.class, desc));
        ModelMBeanConstructorInfo info = (ModelMBeanConstructorInfo)ScriptBytecodeAdapter.castToType(callSiteArray[22].callConstructor(ModelMBeanConstructorInfo.class, callSiteArray[23].callGetProperty(ctor), ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[24].call((Object)desc.get(), callSiteArray[25].callGetProperty(JmxBuilderTools.class))), String.class), params, desc.get()), ModelMBeanConstructorInfo.class);
        return info;
    }

    public static List<ModelMBeanOperationInfo> getOperationInfosFromMap(Map metaMap) {
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(metaMap)) {
            return (List)ScriptBytecodeAdapter.castToType(null, List.class);
        }
        Reference<List> ops = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        public class _getOperationInfosFromMap_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference ops;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getOperationInfosFromMap_closure3(Object _outerInstance, Object _thisObject, Reference ops) {
                Reference reference;
                CallSite[] callSiteArray = _getOperationInfosFromMap_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.ops = reference = ops;
            }

            public Object doCall(Object opNames, Object map) {
                CallSite[] callSiteArray = _getOperationInfosFromMap_closure3.$getCallSiteArray();
                ModelMBeanOperationInfo info = (ModelMBeanOperationInfo)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent((GroovyObject)this, map), ModelMBeanOperationInfo.class);
                return callSiteArray[1].call(this.ops.get(), info);
            }

            public Object call(Object opNames, Object map) {
                CallSite[] callSiteArray = _getOperationInfosFromMap_closure3.$getCallSiteArray();
                return callSiteArray[2].callCurrent(this, opNames, map);
            }

            public Object getOps() {
                CallSite[] callSiteArray = _getOperationInfosFromMap_closure3.$getCallSiteArray();
                return this.ops.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getOperationInfosFromMap_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getOperationInfoFromMap";
                stringArray[1] = "leftShift";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _getOperationInfosFromMap_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getOperationInfosFromMap_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getOperationInfosFromMap_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[26].call((Object)metaMap, new _getOperationInfosFromMap_closure3(JmxOperationInfoManager.class, JmxOperationInfoManager.class, ops));
        return (List)ScriptBytecodeAdapter.castToType(ops.get(), List.class);
    }

    public static ModelMBeanOperationInfo getOperationInfoFromMap(Map map) {
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(map)) {
            return (ModelMBeanOperationInfo)ScriptBytecodeAdapter.castToType(null, ModelMBeanOperationInfo.class);
        }
        MetaMethod method = (MetaMethod)ScriptBytecodeAdapter.castToType(callSiteArray[27].call((Object)map, "method"), MetaMethod.class);
        if (!DefaultTypeTransformation.booleanUnbox(method)) {
            throw (Throwable)callSiteArray[28].callConstructor(JmxBuilderException.class, "Unable to generate ModelMBeanOperationInfo, missing method reference.");
        }
        MBeanParameterInfo[] params = (MBeanParameterInfo[])ScriptBytecodeAdapter.castToType(callSiteArray[29].callStatic(JmxOperationInfoManager.class, callSiteArray[30].call((Object)map, "params")), MBeanParameterInfo[].class);
        Descriptor desc = (Descriptor)ScriptBytecodeAdapter.castToType(callSiteArray[31].callConstructor(DescriptorSupport.class), Descriptor.class);
        callSiteArray[32].call(desc, callSiteArray[33].callGetProperty(JmxBuilderTools.class), callSiteArray[34].call((Object)map, callSiteArray[35].callGetProperty(JmxBuilderTools.class)));
        callSiteArray[36].call(desc, callSiteArray[37].callGetProperty(JmxBuilderTools.class), callSiteArray[38].callGetProperty(JmxBuilderTools.class));
        callSiteArray[39].call(desc, callSiteArray[40].callGetProperty(JmxBuilderTools.class), callSiteArray[41].call((Object)map, callSiteArray[42].callGetProperty(JmxBuilderTools.class)));
        callSiteArray[43].call(desc, callSiteArray[44].callGetProperty(JmxBuilderTools.class), callSiteArray[45].call((Object)map, callSiteArray[46].callGetProperty(JmxBuilderTools.class)));
        ModelMBeanOperationInfo info = (ModelMBeanOperationInfo)ScriptBytecodeAdapter.castToType(callSiteArray[47].callConstructor((Object)ModelMBeanOperationInfo.class, ArrayUtil.createArray(callSiteArray[48].callGetProperty(method), ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[49].call((Object)desc, callSiteArray[50].callGetProperty(JmxBuilderTools.class))), String.class), params, callSiteArray[51].callGetProperty(callSiteArray[52].callGetProperty(method)), callSiteArray[53].callGetProperty(ModelMBeanOperationInfo.class), desc)), ModelMBeanOperationInfo.class);
        return info;
    }

    public static List<MBeanParameterInfo> buildParamInfosFromMaps(Map metaMap) {
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? !DefaultTypeTransformation.booleanUnbox(metaMap) || ScriptBytecodeAdapter.compareEqual(callSiteArray[54].call(metaMap), 0) : !DefaultTypeTransformation.booleanUnbox(metaMap) || ScriptBytecodeAdapter.compareEqual(callSiteArray[55].call(metaMap), 0)) {
            return (List)ScriptBytecodeAdapter.castToType(null, List.class);
        }
        Reference<List> result = new Reference<List>((List)ScriptBytecodeAdapter.castToType(callSiteArray[56].callConstructor(ArrayList.class, callSiteArray[57].call(metaMap)), List.class));
        public class _buildParamInfosFromMaps_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference result;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _buildParamInfosFromMaps_closure4(Object _outerInstance, Object _thisObject, Reference result) {
                Reference reference;
                CallSite[] callSiteArray = _buildParamInfosFromMaps_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.result = reference = result;
            }

            public Object doCall(Object paramType, Object param) {
                CallSite[] callSiteArray = _buildParamInfosFromMaps_closure4.$getCallSiteArray();
                String type = ShortTypeHandling.castToString(callSiteArray[0].callGetProperty(param) instanceof String ? callSiteArray[1].call(JmxBuilderTools.class, callSiteArray[2].callGetProperty(param)) : callSiteArray[3].call(JmxBuilderTools.class, callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(param))));
                MBeanParameterInfo info = (MBeanParameterInfo)ScriptBytecodeAdapter.castToType(callSiteArray[6].callConstructor(MBeanParameterInfo.class, callSiteArray[7].callGetProperty(param), type, callSiteArray[8].callGetProperty(param)), MBeanParameterInfo.class);
                return callSiteArray[9].call(this.result.get(), info);
            }

            public Object call(Object paramType, Object param) {
                CallSite[] callSiteArray = _buildParamInfosFromMaps_closure4.$getCallSiteArray();
                return callSiteArray[10].callCurrent(this, paramType, param);
            }

            public List getResult() {
                CallSite[] callSiteArray = _buildParamInfosFromMaps_closure4.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.result.get(), List.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _buildParamInfosFromMaps_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "type";
                stringArray[1] = "getNormalizedType";
                stringArray[2] = "type";
                stringArray[3] = "getNormalizedType";
                stringArray[4] = "name";
                stringArray[5] = "type";
                stringArray[6] = "<$constructor$>";
                stringArray[7] = "name";
                stringArray[8] = "displayName";
                stringArray[9] = "leftShift";
                stringArray[10] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[11];
                _buildParamInfosFromMaps_closure4.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_buildParamInfosFromMaps_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _buildParamInfosFromMaps_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[58].call((Object)metaMap, new _buildParamInfosFromMaps_closure4(JmxOperationInfoManager.class, JmxOperationInfoManager.class, result));
        return result.get();
    }

    public static ModelMBeanOperationInfo createGetterOperationInfoFromProperty(MetaProperty prop) {
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(prop, null)) {
            return (ModelMBeanOperationInfo)ScriptBytecodeAdapter.castToType(null, ModelMBeanOperationInfo.class);
        }
        Descriptor desc = (Descriptor)ScriptBytecodeAdapter.castToType(callSiteArray[59].callConstructor(DescriptorSupport.class), Descriptor.class);
        String opType = DefaultTypeTransformation.booleanUnbox(callSiteArray[60].call(callSiteArray[61].call(callSiteArray[62].callGetProperty(prop)), "Boolean")) ? "is" : "get";
        String name = ShortTypeHandling.castToString(callSiteArray[63].call((Object)opType, callSiteArray[64].call(JmxBuilderTools.class, callSiteArray[65].callGetProperty(prop))));
        callSiteArray[66].call(desc, callSiteArray[67].callGetProperty(JmxBuilderTools.class), name);
        callSiteArray[68].call(desc, callSiteArray[69].callGetProperty(JmxBuilderTools.class), callSiteArray[70].callGetProperty(JmxBuilderTools.class));
        callSiteArray[71].call(desc, callSiteArray[72].callGetProperty(JmxBuilderTools.class), callSiteArray[73].callGetProperty(JmxBuilderTools.class));
        callSiteArray[74].call(desc, callSiteArray[75].callGetProperty(JmxBuilderTools.class), callSiteArray[76].call(new GStringImpl(new Object[]{callSiteArray[77].callGetProperty(prop)}, new String[]{"Getter for attribute ", ""})));
        ModelMBeanOperationInfo op = (ModelMBeanOperationInfo)ScriptBytecodeAdapter.castToType(callSiteArray[78].callConstructor((Object)ModelMBeanOperationInfo.class, ArrayUtil.createArray(name, ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[79].call((Object)desc, callSiteArray[80].callGetProperty(JmxBuilderTools.class))), String.class), null, callSiteArray[81].call(callSiteArray[82].callGetProperty(prop)), callSiteArray[83].callGetProperty(ModelMBeanOperationInfo.class), desc)), ModelMBeanOperationInfo.class);
        return op;
    }

    public static ModelMBeanOperationInfo createSetterOperationInfoFromProperty(MetaProperty prop) {
        CallSite[] callSiteArray = JmxOperationInfoManager.$getCallSiteArray();
        Descriptor desc = (Descriptor)ScriptBytecodeAdapter.castToType(callSiteArray[84].callConstructor(DescriptorSupport.class), Descriptor.class);
        String name = ShortTypeHandling.castToString(callSiteArray[85].call((Object)"set", callSiteArray[86].call(JmxBuilderTools.class, callSiteArray[87].callGetProperty(prop))));
        callSiteArray[88].call(desc, callSiteArray[89].callGetProperty(JmxBuilderTools.class), name);
        callSiteArray[90].call(desc, callSiteArray[91].callGetProperty(JmxBuilderTools.class), callSiteArray[92].callGetProperty(JmxBuilderTools.class));
        callSiteArray[93].call(desc, callSiteArray[94].callGetProperty(JmxBuilderTools.class), callSiteArray[95].callGetProperty(JmxBuilderTools.class));
        callSiteArray[96].call(desc, callSiteArray[97].callGetProperty(JmxBuilderTools.class), callSiteArray[98].call(new GStringImpl(new Object[]{callSiteArray[99].callGetProperty(prop)}, new String[]{"Getter for attribute ", ""})));
        MBeanParameterInfo[] params = (MBeanParameterInfo[])ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[100].callConstructor(MBeanParameterInfo.class, callSiteArray[101].call(new GStringImpl(new Object[]{callSiteArray[102].call(prop)}, new String[]{"", ""})), callSiteArray[103].callGetProperty(callSiteArray[104].callGetProperty(prop)), "Parameter for setter")}), MBeanParameterInfo[].class);
        ModelMBeanOperationInfo op = (ModelMBeanOperationInfo)ScriptBytecodeAdapter.castToType(callSiteArray[105].callConstructor((Object)ModelMBeanOperationInfo.class, ArrayUtil.createArray(name, ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[106].call((Object)desc, callSiteArray[107].callGetProperty(JmxBuilderTools.class))), String.class), params, callSiteArray[108].callGetProperty(callSiteArray[109].callGetProperty(Void.class)), callSiteArray[110].callGetProperty(ModelMBeanOperationInfo.class), desc)), ModelMBeanOperationInfo.class);
        return op;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxOperationInfoManager.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "each";
        stringArray[1] = "remove";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "buildParamInfosFromMaps";
        stringArray[4] = "remove";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "setField";
        stringArray[7] = "DESC_KEY_NAME";
        stringArray[8] = "remove";
        stringArray[9] = "DESC_KEY_NAME";
        stringArray[10] = "setField";
        stringArray[11] = "DESC_KEY_TYPE";
        stringArray[12] = "DESC_VAL_TYPE_OP";
        stringArray[13] = "setField";
        stringArray[14] = "DESC_KEY_ROLE";
        stringArray[15] = "remove";
        stringArray[16] = "DESC_KEY_ROLE";
        stringArray[17] = "setField";
        stringArray[18] = "DESC_KEY_DISPLAY_NAME";
        stringArray[19] = "remove";
        stringArray[20] = "DESC_KEY_DISPLAY_NAME";
        stringArray[21] = "each";
        stringArray[22] = "<$constructor$>";
        stringArray[23] = "name";
        stringArray[24] = "getFieldValue";
        stringArray[25] = "DESC_KEY_DISPLAY_NAME";
        stringArray[26] = "each";
        stringArray[27] = "remove";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "buildParamInfosFromMaps";
        stringArray[30] = "remove";
        stringArray[31] = "<$constructor$>";
        stringArray[32] = "setField";
        stringArray[33] = "DESC_KEY_NAME";
        stringArray[34] = "remove";
        stringArray[35] = "DESC_KEY_NAME";
        stringArray[36] = "setField";
        stringArray[37] = "DESC_KEY_TYPE";
        stringArray[38] = "DESC_VAL_TYPE_OP";
        stringArray[39] = "setField";
        stringArray[40] = "DESC_KEY_ROLE";
        stringArray[41] = "remove";
        stringArray[42] = "DESC_KEY_ROLE";
        stringArray[43] = "setField";
        stringArray[44] = "DESC_KEY_DISPLAY_NAME";
        stringArray[45] = "remove";
        stringArray[46] = "DESC_KEY_DISPLAY_NAME";
        stringArray[47] = "<$constructor$>";
        stringArray[48] = "name";
        stringArray[49] = "getFieldValue";
        stringArray[50] = "DESC_KEY_DISPLAY_NAME";
        stringArray[51] = "name";
        stringArray[52] = "returnType";
        stringArray[53] = "ACTION";
        stringArray[54] = "size";
        stringArray[55] = "size";
        stringArray[56] = "<$constructor$>";
        stringArray[57] = "size";
        stringArray[58] = "each";
        stringArray[59] = "<$constructor$>";
        stringArray[60] = "contains";
        stringArray[61] = "getName";
        stringArray[62] = "type";
        stringArray[63] = "plus";
        stringArray[64] = "capitalize";
        stringArray[65] = "name";
        stringArray[66] = "setField";
        stringArray[67] = "DESC_KEY_NAME";
        stringArray[68] = "setField";
        stringArray[69] = "DESC_KEY_TYPE";
        stringArray[70] = "DESC_VAL_TYPE_OP";
        stringArray[71] = "setField";
        stringArray[72] = "DESC_KEY_ROLE";
        stringArray[73] = "DESC_VAL_TYPE_GETTER";
        stringArray[74] = "setField";
        stringArray[75] = "DESC_KEY_DISPLAY_NAME";
        stringArray[76] = "toString";
        stringArray[77] = "name";
        stringArray[78] = "<$constructor$>";
        stringArray[79] = "getFieldValue";
        stringArray[80] = "DESC_KEY_DISPLAY_NAME";
        stringArray[81] = "getName";
        stringArray[82] = "type";
        stringArray[83] = "INFO";
        stringArray[84] = "<$constructor$>";
        stringArray[85] = "plus";
        stringArray[86] = "capitalize";
        stringArray[87] = "name";
        stringArray[88] = "setField";
        stringArray[89] = "DESC_KEY_NAME";
        stringArray[90] = "setField";
        stringArray[91] = "DESC_KEY_TYPE";
        stringArray[92] = "DESC_VAL_TYPE_OP";
        stringArray[93] = "setField";
        stringArray[94] = "DESC_KEY_ROLE";
        stringArray[95] = "DESC_VAL_TYPE_SETTER";
        stringArray[96] = "setField";
        stringArray[97] = "DESC_KEY_DISPLAY_NAME";
        stringArray[98] = "toString";
        stringArray[99] = "name";
        stringArray[100] = "<$constructor$>";
        stringArray[101] = "toString";
        stringArray[102] = "getName";
        stringArray[103] = "name";
        stringArray[104] = "type";
        stringArray[105] = "<$constructor$>";
        stringArray[106] = "getFieldValue";
        stringArray[107] = "DESC_KEY_DISPLAY_NAME";
        stringArray[108] = "name";
        stringArray[109] = "TYPE";
        stringArray[110] = "INFO";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[111];
        JmxOperationInfoManager.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxOperationInfoManager.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxOperationInfoManager.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


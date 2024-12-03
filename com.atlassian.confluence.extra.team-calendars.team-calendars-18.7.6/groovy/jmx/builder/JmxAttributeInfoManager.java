/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxBuilderTools;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import java.util.Map;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxAttributeInfoManager
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxAttributeInfoManager() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxAttributeInfoManager.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static List<ModelMBeanAttributeInfo> getAttributeInfosFromMap(Map metaMap) {
        CallSite[] callSiteArray = JmxAttributeInfoManager.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(metaMap)) {
            return (List)ScriptBytecodeAdapter.castToType(null, List.class);
        }
        Reference<List> attribs = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        public class _getAttributeInfosFromMap_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference attribs;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getAttributeInfosFromMap_closure1(Object _outerInstance, Object _thisObject, Reference attribs) {
                Reference reference;
                CallSite[] callSiteArray = _getAttributeInfosFromMap_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.attribs = reference = attribs;
            }

            public Object doCall(Object attribName, Object map) {
                CallSite[] callSiteArray = _getAttributeInfosFromMap_closure1.$getCallSiteArray();
                Object object = attribName;
                ScriptBytecodeAdapter.setProperty(object, null, map, "name");
                ModelMBeanAttributeInfo info = (ModelMBeanAttributeInfo)ScriptBytecodeAdapter.castToType(callSiteArray[0].callCurrent((GroovyObject)this, map), ModelMBeanAttributeInfo.class);
                return callSiteArray[1].call(this.attribs.get(), info);
            }

            public Object call(Object attribName, Object map) {
                CallSite[] callSiteArray = _getAttributeInfosFromMap_closure1.$getCallSiteArray();
                return callSiteArray[2].callCurrent(this, attribName, map);
            }

            public Object getAttribs() {
                CallSite[] callSiteArray = _getAttributeInfosFromMap_closure1.$getCallSiteArray();
                return this.attribs.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getAttributeInfosFromMap_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getAttributeInfoFromMap";
                stringArray[1] = "leftShift";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _getAttributeInfosFromMap_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getAttributeInfosFromMap_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getAttributeInfosFromMap_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[0].call((Object)metaMap, new _getAttributeInfosFromMap_closure1(JmxAttributeInfoManager.class, JmxAttributeInfoManager.class, attribs));
        return (List)ScriptBytecodeAdapter.castToType(attribs.get(), List.class);
    }

    public static ModelMBeanAttributeInfo getAttributeInfoFromMap(Map map) {
        CallSite[] callSiteArray = JmxAttributeInfoManager.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(map)) {
            return (ModelMBeanAttributeInfo)ScriptBytecodeAdapter.castToType(null, ModelMBeanAttributeInfo.class);
        }
        MetaProperty prop = (MetaProperty)ScriptBytecodeAdapter.castToType(callSiteArray[1].call((Object)map, "property"), MetaProperty.class);
        if (!DefaultTypeTransformation.booleanUnbox(prop)) {
            throw (Throwable)callSiteArray[2].callConstructor(JmxBuilderException.class, "Unable generate ModelMBeanAttributeInfo, missing property object.");
        }
        DescriptorSupport desc = (DescriptorSupport)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(DescriptorSupport.class), DescriptorSupport.class);
        callSiteArray[4].call(desc, callSiteArray[5].callGetProperty(JmxBuilderTools.class), callSiteArray[6].call((Object)map, callSiteArray[7].callGetProperty(JmxBuilderTools.class)));
        callSiteArray[8].call(desc, callSiteArray[9].callGetProperty(JmxBuilderTools.class), callSiteArray[10].callGetProperty(JmxBuilderTools.class));
        Object object = callSiteArray[11].call((Object)map, callSiteArray[12].callGetProperty(JmxBuilderTools.class));
        boolean isReadable = DefaultTypeTransformation.booleanUnbox(DefaultTypeTransformation.booleanUnbox(object) ? object : Boolean.valueOf(true));
        Object object2 = callSiteArray[13].call((Object)map, callSiteArray[14].callGetProperty(JmxBuilderTools.class));
        boolean isWritable = DefaultTypeTransformation.booleanUnbox(DefaultTypeTransformation.booleanUnbox(object2) ? object2 : Boolean.valueOf(false));
        callSiteArray[15].call(desc, callSiteArray[16].callGetProperty(JmxBuilderTools.class), isReadable);
        callSiteArray[17].call(desc, callSiteArray[18].callGetProperty(JmxBuilderTools.class), isWritable);
        if (isReadable) {
            callSiteArray[19].call(desc, callSiteArray[20].callGetProperty(JmxBuilderTools.class), callSiteArray[21].call((Object)map, callSiteArray[22].callGetProperty(JmxBuilderTools.class)));
        }
        if (isWritable) {
            callSiteArray[23].call(desc, callSiteArray[24].callGetProperty(JmxBuilderTools.class), callSiteArray[25].call((Object)map, callSiteArray[26].callGetProperty(JmxBuilderTools.class)));
        }
        callSiteArray[27].call(desc, "default", callSiteArray[28].call((Object)map, "defaultValue"));
        callSiteArray[29].call(desc, callSiteArray[30].callGetProperty(JmxBuilderTools.class), callSiteArray[31].call((Object)map, callSiteArray[32].callGetProperty(JmxBuilderTools.class)));
        ModelMBeanAttributeInfo attrib = (ModelMBeanAttributeInfo)ScriptBytecodeAdapter.castToType(callSiteArray[33].callConstructor((Object)ModelMBeanAttributeInfo.class, ArrayUtil.createArray(ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[34].call((Object)desc, callSiteArray[35].callGetProperty(JmxBuilderTools.class))), String.class), callSiteArray[36].call(callSiteArray[37].callGetProperty(prop)), ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[38].call((Object)desc, callSiteArray[39].callGetProperty(JmxBuilderTools.class))), String.class), ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.booleanUnbox(callSiteArray[40].call((Object)desc, callSiteArray[41].callGetProperty(JmxBuilderTools.class))), Boolean.TYPE), ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.booleanUnbox(callSiteArray[42].call((Object)desc, callSiteArray[43].callGetProperty(JmxBuilderTools.class))), Boolean.TYPE), callSiteArray[44].callGetProperty(prop) instanceof Boolean)), ModelMBeanAttributeInfo.class);
        callSiteArray[45].call((Object)attrib, desc);
        return attrib;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxAttributeInfoManager.class) {
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
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "setField";
        stringArray[5] = "DESC_KEY_NAME";
        stringArray[6] = "remove";
        stringArray[7] = "DESC_KEY_NAME";
        stringArray[8] = "setField";
        stringArray[9] = "DESC_KEY_TYPE";
        stringArray[10] = "DESC_VAL_TYPE_ATTRIB";
        stringArray[11] = "remove";
        stringArray[12] = "DESC_KEY_READABLE";
        stringArray[13] = "remove";
        stringArray[14] = "DESC_KEY_WRITABLE";
        stringArray[15] = "setField";
        stringArray[16] = "DESC_KEY_READABLE";
        stringArray[17] = "setField";
        stringArray[18] = "DESC_KEY_WRITABLE";
        stringArray[19] = "setField";
        stringArray[20] = "DESC_KEY_GETMETHOD";
        stringArray[21] = "remove";
        stringArray[22] = "DESC_KEY_GETMETHOD";
        stringArray[23] = "setField";
        stringArray[24] = "DESC_KEY_SETMETHOD";
        stringArray[25] = "remove";
        stringArray[26] = "DESC_KEY_SETMETHOD";
        stringArray[27] = "setField";
        stringArray[28] = "remove";
        stringArray[29] = "setField";
        stringArray[30] = "DESC_KEY_DISPLAY_NAME";
        stringArray[31] = "remove";
        stringArray[32] = "DESC_KEY_DISPLAY_NAME";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "getFieldValue";
        stringArray[35] = "DESC_KEY_NAME";
        stringArray[36] = "getName";
        stringArray[37] = "type";
        stringArray[38] = "getFieldValue";
        stringArray[39] = "DESC_KEY_DISPLAY_NAME";
        stringArray[40] = "getFieldValue";
        stringArray[41] = "DESC_KEY_READABLE";
        stringArray[42] = "getFieldValue";
        stringArray[43] = "DESC_KEY_WRITABLE";
        stringArray[44] = "type";
        stringArray[45] = "setDescriptor";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[46];
        JmxAttributeInfoManager.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxAttributeInfoManager.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxAttributeInfoManager.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


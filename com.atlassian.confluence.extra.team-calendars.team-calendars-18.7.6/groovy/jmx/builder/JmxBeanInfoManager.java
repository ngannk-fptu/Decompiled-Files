/*
 * Decompiled with CFR 0.152.
 */
package groovy.jmx.builder;

import groovy.jmx.builder.JmxAttributeInfoManager;
import groovy.jmx.builder.JmxBuilderException;
import groovy.jmx.builder.JmxBuilderTools;
import groovy.jmx.builder.JmxOperationInfoManager;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MetaProperty;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.Map;
import javax.management.ObjectName;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JmxBeanInfoManager
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JmxBeanInfoManager() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JmxBeanInfoManager.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static ObjectName buildDefaultObjectName(String defaultDomain, String defaultType, Object object) {
        CallSite[] callSiteArray = JmxBeanInfoManager.$getCallSiteArray();
        GStringImpl name = new GStringImpl(new Object[]{defaultDomain, defaultType, callSiteArray[0].callGetProperty(callSiteArray[1].callGetProperty(object)), callSiteArray[2].call(object)}, new String[]{"", ":type=", ",name=", "@", ""});
        return (ObjectName)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(ObjectName.class, name), ObjectName.class);
    }

    public static ModelMBeanInfo getModelMBeanInfoFromMap(Map map) {
        CallSite[] callSiteArray = JmxBeanInfoManager.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(map)) {
            throw (Throwable)callSiteArray[4].callConstructor(JmxBuilderException.class, "Unable to create default ModelMBeanInfo, missing meta map.");
        }
        Reference<Object> object = new Reference<Object>(callSiteArray[5].callGetProperty(map));
        if (!DefaultTypeTransformation.booleanUnbox(object.get())) {
            throw (Throwable)callSiteArray[6].callConstructor(JmxBuilderException.class, "Unable to create default ModelMBeanInfo, missing target object.");
        }
        Object attributes = callSiteArray[7].call(JmxAttributeInfoManager.class, callSiteArray[8].callGetProperty(map));
        Object object2 = callSiteArray[9].call(JmxOperationInfoManager.class, callSiteArray[10].callGetProperty(map));
        Reference<Object> operations = new Reference<Object>(DefaultTypeTransformation.booleanUnbox(object2) ? object2 : ScriptBytecodeAdapter.createList(new Object[0]));
        public class _getModelMBeanInfoFromMap_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference object;
            private /* synthetic */ Reference operations;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getModelMBeanInfoFromMap_closure1(Object _outerInstance, Object _thisObject, Reference object, Reference operations) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _getModelMBeanInfoFromMap_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.object = reference2 = object;
                this.operations = reference = operations;
            }

            public Object doCall(Object info) {
                CallSite[] callSiteArray = _getModelMBeanInfoFromMap_closure1.$getCallSiteArray();
                MetaProperty prop = (MetaProperty)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callGetProperty(this.object.get()), callSiteArray[2].call(JmxBuilderTools.class, callSiteArray[3].callGetProperty(info))), MetaProperty.class);
                if (DefaultTypeTransformation.booleanUnbox(prop) && DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(info))) {
                    callSiteArray[5].call(this.operations.get(), callSiteArray[6].call(JmxOperationInfoManager.class, prop));
                }
                if (DefaultTypeTransformation.booleanUnbox(prop) && DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(info))) {
                    return callSiteArray[8].call(this.operations.get(), callSiteArray[9].call(JmxOperationInfoManager.class, prop));
                }
                return null;
            }

            public Object getObject() {
                CallSite[] callSiteArray = _getModelMBeanInfoFromMap_closure1.$getCallSiteArray();
                return this.object.get();
            }

            public Object getOperations() {
                CallSite[] callSiteArray = _getModelMBeanInfoFromMap_closure1.$getCallSiteArray();
                return this.operations.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getModelMBeanInfoFromMap_closure1.class) {
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
                stringArray[3] = "name";
                stringArray[4] = "isReadable";
                stringArray[5] = "leftShift";
                stringArray[6] = "createGetterOperationInfoFromProperty";
                stringArray[7] = "isWritable";
                stringArray[8] = "leftShift";
                stringArray[9] = "createSetterOperationInfoFromProperty";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[10];
                _getModelMBeanInfoFromMap_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_getModelMBeanInfoFromMap_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getModelMBeanInfoFromMap_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[11].call(attributes, new _getModelMBeanInfoFromMap_closure1(JmxBeanInfoManager.class, JmxBeanInfoManager.class, object, operations));
        ModelMBeanAttributeInfo[] attribs = (ModelMBeanAttributeInfo[])ScriptBytecodeAdapter.castToType(attributes, ModelMBeanAttributeInfo[].class);
        ModelMBeanConstructorInfo[] ctors = (ModelMBeanConstructorInfo[])ScriptBytecodeAdapter.castToType(callSiteArray[12].call(JmxOperationInfoManager.class, callSiteArray[13].callGetProperty(map)), ModelMBeanConstructorInfo[].class);
        ModelMBeanOperationInfo[] ops = (ModelMBeanOperationInfo[])ScriptBytecodeAdapter.castToType(operations.get(), ModelMBeanOperationInfo[].class);
        Object notes = null;
        DescriptorSupport desc = (DescriptorSupport)ScriptBytecodeAdapter.castToType(callSiteArray[14].callConstructor(DescriptorSupport.class), DescriptorSupport.class);
        callSiteArray[15].call(desc, callSiteArray[16].callGetProperty(JmxBuilderTools.class), callSiteArray[17].callGetProperty(JmxBuilderTools.class));
        callSiteArray[18].call(desc, callSiteArray[19].callGetProperty(JmxBuilderTools.class), callSiteArray[20].callGetProperty(map));
        callSiteArray[21].call(desc, callSiteArray[22].callGetProperty(JmxBuilderTools.class), callSiteArray[23].callGetProperty(map));
        return (ModelMBeanInfo)ScriptBytecodeAdapter.castToType(callSiteArray[24].callConstructor((Object)ModelMBeanInfoSupport.class, ArrayUtil.createArray(ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[25].callGetProperty(callSiteArray[26].call(object.get()))), String.class), ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[27].call((Object)desc, callSiteArray[28].callGetProperty(JmxBuilderTools.class))), String.class), attribs, ctors, ops, notes, desc)), ModelMBeanInfo.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JmxBeanInfoManager.class) {
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
        stringArray[0] = "canonicalName";
        stringArray[1] = "class";
        stringArray[2] = "hashCode";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "target";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "getAttributeInfosFromMap";
        stringArray[8] = "attributes";
        stringArray[9] = "getOperationInfosFromMap";
        stringArray[10] = "operations";
        stringArray[11] = "each";
        stringArray[12] = "getConstructorInfosFromMap";
        stringArray[13] = "constructors";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "setField";
        stringArray[16] = "DESC_KEY_TYPE";
        stringArray[17] = "DESC_VAL_TYPE_MBEAN";
        stringArray[18] = "setField";
        stringArray[19] = "DESC_KEY_DISPLAY_NAME";
        stringArray[20] = "displayName";
        stringArray[21] = "setField";
        stringArray[22] = "DESC_KEY_NAME";
        stringArray[23] = "name";
        stringArray[24] = "<$constructor$>";
        stringArray[25] = "name";
        stringArray[26] = "getClass";
        stringArray[27] = "getFieldValue";
        stringArray[28] = "DESC_KEY_DISPLAY_NAME";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[29];
        JmxBeanInfoManager.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JmxBeanInfoManager.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JmxBeanInfoManager.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


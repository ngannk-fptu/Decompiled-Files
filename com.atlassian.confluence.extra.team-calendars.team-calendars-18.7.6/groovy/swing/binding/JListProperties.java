/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing.binding;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.swing.binding.JListElementsBinding;
import groovy.swing.binding.JListSelectedElementBinding;
import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JList;
import org.codehaus.groovy.binding.FullBinding;
import org.codehaus.groovy.binding.PropertyBinding;
import org.codehaus.groovy.binding.SourceBinding;
import org.codehaus.groovy.binding.TargetBinding;
import org.codehaus.groovy.binding.TriggerBinding;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class JListProperties
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public JListProperties() {
        MetaClass metaClass;
        CallSite[] callSiteArray = JListProperties.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static Map<String, TriggerBinding> getSyntheticProperties() {
        CallSite[] callSiteArray = JListProperties.$getCallSiteArray();
        Map result = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(HashMap.class), Map.class);
        callSiteArray[1].call(result, callSiteArray[2].call(callSiteArray[3].call(JList.class), "#selectedValue"), new TriggerBinding(JListProperties.class){
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Class clazz;
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(JListSelectedElementBinding.class, ScriptBytecodeAdapter.createPojoWrapper((PropertyBinding)ScriptBytecodeAdapter.castToType(source, PropertyBinding.class), PropertyBinding.class), target, "selectedValue"), FullBinding.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(1.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(1.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(1.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(1.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 1.class) {
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        });
        callSiteArray[4].call(result, callSiteArray[5].call(callSiteArray[6].call(JList.class), "#selectedElement"), new TriggerBinding(JListProperties.class){
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Class clazz;
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(JListSelectedElementBinding.class, ScriptBytecodeAdapter.createPojoWrapper((PropertyBinding)ScriptBytecodeAdapter.castToType(source, PropertyBinding.class), PropertyBinding.class), target, "selectedElement"), FullBinding.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(2.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(2.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(2.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 2.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(2.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 2.class) {
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        });
        callSiteArray[7].call(result, callSiteArray[8].call(callSiteArray[9].call(JList.class), "#selectedValues"), new TriggerBinding(JListProperties.class){
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Class clazz;
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(JListSelectedElementBinding.class, ScriptBytecodeAdapter.createPojoWrapper((PropertyBinding)ScriptBytecodeAdapter.castToType(source, PropertyBinding.class), PropertyBinding.class), target, "selectedValues"), FullBinding.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(3.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(3.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(3.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 3.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(3.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 3.class) {
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        });
        callSiteArray[10].call(result, callSiteArray[11].call(callSiteArray[12].call(JList.class), "#selectedElements"), new TriggerBinding(JListProperties.class){
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Class clazz;
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(JListSelectedElementBinding.class, ScriptBytecodeAdapter.createPojoWrapper((PropertyBinding)ScriptBytecodeAdapter.castToType(source, PropertyBinding.class), PropertyBinding.class), target, "selectedElements"), FullBinding.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(4.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(4.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(4.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 4.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(4.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 4.class) {
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        });
        callSiteArray[13].call(result, callSiteArray[14].call(callSiteArray[15].call(JList.class), "#selectedIndex"), new TriggerBinding(JListProperties.class){
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Class clazz;
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(JListSelectedElementBinding.class, ScriptBytecodeAdapter.createPojoWrapper((PropertyBinding)ScriptBytecodeAdapter.castToType(source, PropertyBinding.class), PropertyBinding.class), target, "selectedIndex"), FullBinding.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(5.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(5.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(5.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 5.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(5.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 5.class) {
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        });
        callSiteArray[16].call(result, callSiteArray[17].call(callSiteArray[18].call(JList.class), "#selectedIndices"), new TriggerBinding(JListProperties.class){
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Class clazz;
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(JListSelectedElementBinding.class, ScriptBytecodeAdapter.createPojoWrapper((PropertyBinding)ScriptBytecodeAdapter.castToType(source, PropertyBinding.class), PropertyBinding.class), target, "selectedIndices"), FullBinding.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(6.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(6.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(6.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 6.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(6.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 6.class) {
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        });
        callSiteArray[19].call(result, callSiteArray[20].call(callSiteArray[21].call(JList.class), "#elements"), new TriggerBinding(JListProperties.class){
            public /* synthetic */ Class this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Class clazz;
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                this.this$0 = clazz = p0;
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public FullBinding createBinding(SourceBinding source, TargetBinding target) {
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                return (FullBinding)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(JListElementsBinding.class, ScriptBytecodeAdapter.createPojoWrapper((PropertyBinding)ScriptBytecodeAdapter.castToType(source, PropertyBinding.class), PropertyBinding.class), target), FullBinding.class);
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(7.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(7.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(7.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 7.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(7.class, JListProperties.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 7.class) {
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

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[1];
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        });
        return result;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != JListProperties.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object this$dist$invoke$1(String name, Object args) {
        CallSite[] callSiteArray = JListProperties.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(JListProperties.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$1(String name, Object value) {
        CallSite[] callSiteArray = JListProperties.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, JListProperties.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$1(String name) {
        CallSite[] callSiteArray = JListProperties.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(JListProperties.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "put";
        stringArray[2] = "plus";
        stringArray[3] = "getName";
        stringArray[4] = "put";
        stringArray[5] = "plus";
        stringArray[6] = "getName";
        stringArray[7] = "put";
        stringArray[8] = "plus";
        stringArray[9] = "getName";
        stringArray[10] = "put";
        stringArray[11] = "plus";
        stringArray[12] = "getName";
        stringArray[13] = "put";
        stringArray[14] = "plus";
        stringArray[15] = "getName";
        stringArray[16] = "put";
        stringArray[17] = "plus";
        stringArray[18] = "getName";
        stringArray[19] = "put";
        stringArray[20] = "plus";
        stringArray[21] = "getName";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[22];
        JListProperties.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(JListProperties.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = JListProperties.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


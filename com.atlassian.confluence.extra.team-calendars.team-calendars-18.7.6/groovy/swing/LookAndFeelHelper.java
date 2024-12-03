/*
 * Decompiled with CFR 0.152.
 */
package groovy.swing;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import groovy.util.FactoryBuilderSupport;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.Map;
import javax.swing.LookAndFeel;
import javax.swing.UIManager;
import javax.swing.plaf.metal.DefaultMetalTheme;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.MetalTheme;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class LookAndFeelHelper
implements GroovyObject {
    protected static LookAndFeelHelper instance;
    private final Map lafCodeNames;
    private final Map extendedAttributes;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    private LookAndFeelHelper() {
        MetaClass metaClass;
        Map map;
        Map map2;
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        this.lafCodeNames = map2 = ScriptBytecodeAdapter.createMap(new Object[]{"metal", "javax.swing.plaf.metal.MetalLookAndFeel", "nimbus", callSiteArray[0].callStatic(LookAndFeelHelper.class), "mac", callSiteArray[1].callStatic(LookAndFeelHelper.class), "motif", "com.sun.java.swing.plaf.motif.MotifLookAndFeel", "windows", "com.sun.java.swing.plaf.windows.WindowsLookAndFeel", "win2k", "com.sun.java.swing.plaf.windows.WindowsClassicLookAndFeel", "gtk", "com.sun.java.swing.plaf.gtk.GTKLookAndFeel", "synth", "javax.swing.plaf.synth.SynthLookAndFeel", "system", callSiteArray[2].call(UIManager.class), "crossPlatform", callSiteArray[3].call(UIManager.class), "plastic", "com.jgoodies.looks.plastic.PlasticLookAndFeel", "plastic3D", "com.jgoodies.looks.plastic.Plastic3DLookAndFeel", "plasticXP", "com.jgoodies.looks.plastic.PlasticXPLookAndFeel", "substance", callSiteArray[4].callStatic(LookAndFeelHelper.class), "napkin", "net.sourceforge.napkinlaf.NapkinLookAndFeel"});
        this.extendedAttributes = map = ScriptBytecodeAdapter.createMap(new Object[]{"javax.swing.plaf.metal.MetalLookAndFeel", ScriptBytecodeAdapter.createMap(new Object[]{"theme", new _closure1(this, this), "boldFonts", new _closure2(this, this), "noxp", new _closure3(this, this)}), "org.jvnet.substance.SubstanceLookAndFeel", ScriptBytecodeAdapter.createMap(new Object[]{"theme", new _closure4(this, this), "skin", new _closure5(this, this), "watermark", new _closure6(this, this)})});
        this.metaClass = metaClass = this.$getStaticMetaClass();
        callSiteArray[5].call(UIManager.class);
    }

    public static LookAndFeelHelper getInstance() {
        Object object;
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        LookAndFeelHelper lookAndFeelHelper = instance;
        if (DefaultTypeTransformation.booleanUnbox(lookAndFeelHelper)) {
            object = lookAndFeelHelper;
        } else {
            Object object2 = callSiteArray[6].callConstructor(LookAndFeelHelper.class);
            instance = (LookAndFeelHelper)ScriptBytecodeAdapter.castToType(object2, LookAndFeelHelper.class);
            object = object2;
        }
        return (LookAndFeelHelper)ScriptBytecodeAdapter.castToType(object, LookAndFeelHelper.class);
    }

    public String addLookAndFeelAlias(String alias, String className) {
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        String string = className;
        callSiteArray[7].call(this.lafCodeNames, alias, string);
        return string;
    }

    public String addLookAndFeelAttributeHandler(String className, String attr, Closure handler) {
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        Map attrs = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[8].call((Object)this.extendedAttributes, className), Map.class);
        if (ScriptBytecodeAdapter.compareEqual(attrs, null)) {
            Map map;
            attrs = map = ScriptBytecodeAdapter.createMap(new Object[0]);
            Map map2 = attrs;
            callSiteArray[9].call(this.extendedAttributes, className, map2);
        }
        Closure closure = handler;
        callSiteArray[10].call(attrs, attr, closure);
        return ShortTypeHandling.castToString(closure);
    }

    public boolean isLeaf() {
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        return true;
    }

    public LookAndFeel lookAndFeel(Object value, Map attributes, Closure initClosure) {
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        Reference<Object> lafInstance = new Reference<Object>(null);
        LookAndFeel cfr_ignored_0 = lafInstance.get();
        Reference<Object> lafClassName = new Reference<Object>(null);
        String cfr_ignored_1 = lafClassName.get();
        if (value instanceof Closure && ScriptBytecodeAdapter.compareEqual(initClosure, null)) {
            Object object = value;
            initClosure = (Closure)ScriptBytecodeAdapter.castToType(object, Closure.class);
            Object var8_8 = null;
            value = var8_8;
        }
        if (ScriptBytecodeAdapter.compareEqual(value, null)) {
            Object object;
            value = object = callSiteArray[11].call((Object)attributes, "lookAndFeel");
        }
        if (value instanceof GString) {
            String string = (String)ScriptBytecodeAdapter.asType(value, String.class);
            value = string;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(FactoryBuilderSupport.class, value, "lookAndFeel", LookAndFeel.class))) {
            Object object = value;
            lafInstance.set(((LookAndFeel)ScriptBytecodeAdapter.castToType(object, LookAndFeel.class)));
            Object object2 = callSiteArray[13].callGetProperty(callSiteArray[14].callGetProperty(lafInstance.get()));
            lafClassName.set(ShortTypeHandling.castToString(object2));
        } else if (ScriptBytecodeAdapter.compareNotEqual(value, null)) {
            Object object = callSiteArray[15].call((Object)this.lafCodeNames, value);
            Object object3 = DefaultTypeTransformation.booleanUnbox(object) ? object : value;
            lafClassName.set(ShortTypeHandling.castToString(object3));
            Object object4 = callSiteArray[16].call(callSiteArray[17].call(Class.class, lafClassName.get(), true, callSiteArray[18].callGetProperty(callSiteArray[19].callCurrent(this))));
            lafInstance.set(((LookAndFeel)ScriptBytecodeAdapter.castToType(object4, LookAndFeel.class)));
        }
        Object object = callSiteArray[20].call((Object)this.extendedAttributes, (Object)lafClassName.get());
        Reference<Map> possibleAttributes = new Reference<Map>((Map)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(object) ? object : ScriptBytecodeAdapter.createMap(new Object[0]), Map.class));
        public class _lookAndFeel_closure7
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference possibleAttributes;
            private /* synthetic */ Reference lafInstance;
            private /* synthetic */ Reference lafClassName;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _lookAndFeel_closure7(Object _outerInstance, Object _thisObject, Reference possibleAttributes, Reference lafInstance, Reference lafClassName) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                CallSite[] callSiteArray = _lookAndFeel_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.possibleAttributes = reference3 = possibleAttributes;
                this.lafInstance = reference2 = lafInstance;
                this.lafClassName = reference = lafClassName;
            }

            public Object doCall(Object k, Object v) {
                CallSite[] callSiteArray = _lookAndFeel_closure7.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(this.possibleAttributes.get(), k))) {
                    return callSiteArray[1].call(callSiteArray[2].call(this.possibleAttributes.get(), k), this.lafInstance.get(), v);
                }
                Object object = v;
                ScriptBytecodeAdapter.setProperty(object, null, this.lafInstance.get(), ShortTypeHandling.castToString(new GStringImpl(new Object[]{k}, new String[]{"", ""})));
                Object object2 = object;
                try {
                    return object2;
                }
                catch (MissingPropertyException mpe) {
                    throw (Throwable)callSiteArray[3].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{this.lafClassName.get(), k}, new String[]{"SwingBuilder initialization for the Look and Feel Class ", " does accept the attribute ", ""}));
                }
            }

            public Object call(Object k, Object v) {
                CallSite[] callSiteArray = _lookAndFeel_closure7.$getCallSiteArray();
                return callSiteArray[4].callCurrent(this, k, v);
            }

            public Map getPossibleAttributes() {
                CallSite[] callSiteArray = _lookAndFeel_closure7.$getCallSiteArray();
                return (Map)ScriptBytecodeAdapter.castToType(this.possibleAttributes.get(), Map.class);
            }

            public LookAndFeel getLafInstance() {
                CallSite[] callSiteArray = _lookAndFeel_closure7.$getCallSiteArray();
                return (LookAndFeel)ScriptBytecodeAdapter.castToType(this.lafInstance.get(), LookAndFeel.class);
            }

            public String getLafClassName() {
                CallSite[] callSiteArray = _lookAndFeel_closure7.$getCallSiteArray();
                return ShortTypeHandling.castToString(this.lafClassName.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _lookAndFeel_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getAt";
                stringArray[1] = "call";
                stringArray[2] = "getAt";
                stringArray[3] = "<$constructor$>";
                stringArray[4] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _lookAndFeel_closure7.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_lookAndFeel_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _lookAndFeel_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[21].call((Object)attributes, new _lookAndFeel_closure7(this, this, possibleAttributes, lafInstance, lafClassName));
        if (DefaultTypeTransformation.booleanUnbox(initClosure)) {
            callSiteArray[22].call((Object)initClosure, (Object)lafInstance.get());
        }
        callSiteArray[23].call(UIManager.class, (Object)lafInstance.get());
        return lafInstance.get();
    }

    public static String getNimbusLAFName() {
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object klass = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[24].call(ScriptBytecodeAdapter.createList(new Object[]{"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", "sun.swing.plaf.nimbus.NimbusLookAndFeel", "org.jdesktop.swingx.plaf.nimbus.NimbusLookAndFeel"})), Iterator.class);
            while (iterator.hasNext()) {
                klass = iterator.next();
                String string = ShortTypeHandling.castToString(callSiteArray[25].call(Class.forName(ShortTypeHandling.castToString(klass))));
                try {
                    return string;
                }
                catch (Throwable t) {
                }
            }
        } else {
            Object klass = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[26].call(ScriptBytecodeAdapter.createList(new Object[]{"com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", "sun.swing.plaf.nimbus.NimbusLookAndFeel", "org.jdesktop.swingx.plaf.nimbus.NimbusLookAndFeel"})), Iterator.class);
            while (iterator.hasNext()) {
                klass = iterator.next();
                String string = ShortTypeHandling.castToString(callSiteArray[27].call(Class.forName(ShortTypeHandling.castToString(klass))));
                try {
                    return string;
                }
                catch (Throwable t) {
                }
            }
        }
        return ShortTypeHandling.castToString(null);
    }

    public static String getAquaLAFName() {
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object klass = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[28].call(ScriptBytecodeAdapter.createList(new Object[]{"com.apple.laf.AquaLookAndFeel", "apple.laf.AquaLookAndFeel"})), Iterator.class);
            while (iterator.hasNext()) {
                klass = iterator.next();
                String string = ShortTypeHandling.castToString(callSiteArray[29].call(Class.forName(ShortTypeHandling.castToString(klass))));
                try {
                    return string;
                }
                catch (Throwable t) {
                }
            }
        } else {
            Object klass = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[30].call(ScriptBytecodeAdapter.createList(new Object[]{"com.apple.laf.AquaLookAndFeel", "apple.laf.AquaLookAndFeel"})), Iterator.class);
            while (iterator.hasNext()) {
                klass = iterator.next();
                String string = ShortTypeHandling.castToString(callSiteArray[31].call(Class.forName(ShortTypeHandling.castToString(klass))));
                try {
                    return string;
                }
                catch (Throwable t) {
                }
            }
        }
        return ShortTypeHandling.castToString(null);
    }

    public static String getSubstanceLAFName() {
        CallSite[] callSiteArray = LookAndFeelHelper.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object klass = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[32].call(ScriptBytecodeAdapter.createList(new Object[]{"org.pushingpixels.substance.api.SubstanceLookAndFeel", "org.jvnet.substance.SubstanceLookAndFeel"})), Iterator.class);
            while (iterator.hasNext()) {
                klass = iterator.next();
                String string = ShortTypeHandling.castToString(callSiteArray[33].call(Class.forName(ShortTypeHandling.castToString(klass))));
                try {
                    return string;
                }
                catch (Throwable t) {
                }
            }
        } else {
            Object klass = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[34].call(ScriptBytecodeAdapter.createList(new Object[]{"org.pushingpixels.substance.api.SubstanceLookAndFeel", "org.jvnet.substance.SubstanceLookAndFeel"})), Iterator.class);
            while (iterator.hasNext()) {
                klass = iterator.next();
                String string = ShortTypeHandling.castToString(callSiteArray[35].call(Class.forName(ShortTypeHandling.castToString(klass))));
                try {
                    return string;
                }
                catch (Throwable t) {
                }
            }
        }
        return ShortTypeHandling.castToString(null);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != LookAndFeelHelper.class) {
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
        stringArray[0] = "getNimbusLAFName";
        stringArray[1] = "getAquaLAFName";
        stringArray[2] = "getSystemLookAndFeelClassName";
        stringArray[3] = "getCrossPlatformLookAndFeelClassName";
        stringArray[4] = "getSubstanceLAFName";
        stringArray[5] = "getInstalledLookAndFeels";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "putAt";
        stringArray[8] = "getAt";
        stringArray[9] = "putAt";
        stringArray[10] = "putAt";
        stringArray[11] = "remove";
        stringArray[12] = "checkValueIsTypeNotString";
        stringArray[13] = "name";
        stringArray[14] = "class";
        stringArray[15] = "getAt";
        stringArray[16] = "newInstance";
        stringArray[17] = "forName";
        stringArray[18] = "classLoader";
        stringArray[19] = "getClass";
        stringArray[20] = "getAt";
        stringArray[21] = "each";
        stringArray[22] = "call";
        stringArray[23] = "setLookAndFeel";
        stringArray[24] = "iterator";
        stringArray[25] = "getName";
        stringArray[26] = "iterator";
        stringArray[27] = "getName";
        stringArray[28] = "iterator";
        stringArray[29] = "getName";
        stringArray[30] = "iterator";
        stringArray[31] = "getName";
        stringArray[32] = "iterator";
        stringArray[33] = "getName";
        stringArray[34] = "iterator";
        stringArray[35] = "getName";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[36];
        LookAndFeelHelper.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(LookAndFeelHelper.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = LookAndFeelHelper.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    public class _closure1
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure1(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object laf, Object theme) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            if (!(theme instanceof MetalTheme)) {
                Object object;
                Object object2;
                Object object3;
                theme = ScriptBytecodeAdapter.compareEqual(theme, "ocean") ? (object3 = callSiteArray[0].call(Class.forName("javax.swing.plaf.metal.OceanTheme"))) : (ScriptBytecodeAdapter.compareEqual(theme, "steel") ? (object2 = callSiteArray[1].callConstructor(DefaultMetalTheme.class)) : (object = callSiteArray[2].call(Class.forName((String)ScriptBytecodeAdapter.asType(theme, String.class)))));
            }
            Object object = theme;
            ScriptBytecodeAdapter.setProperty(object, null, MetalLookAndFeel.class, "currentTheme");
            return object;
        }

        public Object call(Object laf, Object theme) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[3].callCurrent(this, laf, theme);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure1.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "newInstance";
            stringArray[1] = "<$constructor$>";
            stringArray[2] = "newInstance";
            stringArray[3] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[4];
            _closure1.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure1.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure1.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure2
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure2(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object laf, Object bold) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            return callSiteArray[0].call(UIManager.class, "swing.boldMetal", ScriptBytecodeAdapter.createPojoWrapper((Boolean)ScriptBytecodeAdapter.asType(bold, Boolean.class), Boolean.class));
        }

        public Object call(Object laf, Object bold) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            return callSiteArray[1].callCurrent(this, laf, bold);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure2.class) {
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
            stringArray[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
            _closure2.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure2.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure2.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure3
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure3(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object laf, Object xp) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            return callSiteArray[0].call(UIManager.class, "swing.noxp", ScriptBytecodeAdapter.createPojoWrapper((Boolean)ScriptBytecodeAdapter.asType(callSiteArray[1].callGroovyObjectGetProperty(this), Boolean.class), Boolean.class));
        }

        public Object call(Object laf, Object xp) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            return callSiteArray[2].callCurrent(this, laf, xp);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure3.class) {
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
            stringArray[1] = "bold";
            stringArray[2] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[3];
            _closure3.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure3.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure3.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure4
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure4(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object laf, Object theme) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            return callSiteArray[0].call(laf, theme);
        }

        public Object call(Object laf, Object theme) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            return callSiteArray[1].callCurrent(this, laf, theme);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure4.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "setCurrentTheme";
            stringArray[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
            _closure4.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure4.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure4.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure5
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure5(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure5.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object laf, Object skin) {
            CallSite[] callSiteArray = _closure5.$getCallSiteArray();
            return callSiteArray[0].call(laf, skin);
        }

        public Object call(Object laf, Object skin) {
            CallSite[] callSiteArray = _closure5.$getCallSiteArray();
            return callSiteArray[1].callCurrent(this, laf, skin);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure5.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "setSkin";
            stringArray[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
            _closure5.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure5.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure5.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    public class _closure6
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure6(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure6.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(Object laf, Object watermark) {
            CallSite[] callSiteArray = _closure6.$getCallSiteArray();
            return callSiteArray[0].call(laf, watermark);
        }

        public Object call(Object laf, Object watermark) {
            CallSite[] callSiteArray = _closure6.$getCallSiteArray();
            return callSiteArray[1].callCurrent(this, laf, watermark);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure6.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "setCurrentWatermark";
            stringArray[1] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[2];
            _closure6.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure6.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure6.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}


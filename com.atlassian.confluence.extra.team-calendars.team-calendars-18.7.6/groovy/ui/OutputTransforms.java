/*
 * Decompiled with CFR 0.152.
 */
package groovy.ui;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Transparency;
import java.awt.Window;
import java.awt.image.BufferedImage;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JComponent;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class OutputTransforms
implements GroovyObject {
    private static Object $localTransforms;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public OutputTransforms() {
        MetaClass metaClass;
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static Object loadOutputTransforms() {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        List transforms = ScriptBytecodeAdapter.createList(new Object[0]);
        Object userHome = callSiteArray[0].callConstructor(File.class, callSiteArray[1].call(System.class, "user.home"));
        Object groovyDir = callSiteArray[2].callConstructor(File.class, userHome, ".groovy");
        Object userTransforms = callSiteArray[3].callConstructor(File.class, groovyDir, "OutputTransforms.groovy");
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(userTransforms))) {
            GroovyShell shell = (GroovyShell)ScriptBytecodeAdapter.castToType(callSiteArray[5].callConstructor(GroovyShell.class), GroovyShell.class);
            callSiteArray[6].call(shell, "transforms", transforms);
            callSiteArray[7].call((Object)shell, userTransforms);
        }
        public class _loadOutputTransforms_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _loadOutputTransforms_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure1.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (it instanceof Component && !(it instanceof Window) && ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), null)) {
                        return it;
                    }
                    return null;
                }
                if (it instanceof Component && !(it instanceof Window) && ScriptBytecodeAdapter.compareEqual(callSiteArray[1].callGetProperty(it), null)) {
                    return it;
                }
                return null;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _loadOutputTransforms_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "parent";
                stringArray[1] = "parent";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _loadOutputTransforms_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_loadOutputTransforms_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _loadOutputTransforms_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[8].call((Object)transforms, new _loadOutputTransforms_closure1(OutputTransforms.class, OutputTransforms.class));
        public class _loadOutputTransforms_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _loadOutputTransforms_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure2.$getCallSiteArray();
                if (it instanceof JComponent) {
                    Dimension d = (Dimension)ScriptBytecodeAdapter.castToType(callSiteArray[0].callGetProperty(it), Dimension.class);
                    if (ScriptBytecodeAdapter.compareEqual(callSiteArray[1].callGetProperty(d), 0)) {
                        Object object = callSiteArray[2].callGetProperty(it);
                        d = (Dimension)ScriptBytecodeAdapter.castToType(object, Dimension.class);
                        Dimension dimension = d;
                        ScriptBytecodeAdapter.setProperty(dimension, null, it, "size");
                    }
                    GraphicsEnvironment ge = (GraphicsEnvironment)ScriptBytecodeAdapter.castToType(callSiteArray[3].callGetProperty(GraphicsEnvironment.class), GraphicsEnvironment.class);
                    GraphicsDevice gs = (GraphicsDevice)ScriptBytecodeAdapter.castToType(callSiteArray[4].callGetProperty(ge), GraphicsDevice.class);
                    GraphicsConfiguration gc = (GraphicsConfiguration)ScriptBytecodeAdapter.castToType(callSiteArray[5].callGetProperty(gs), GraphicsConfiguration.class);
                    BufferedImage image = (BufferedImage)ScriptBytecodeAdapter.castToType(callSiteArray[6].call(gc, ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[7].callGetProperty(d), Integer.TYPE)), Integer.TYPE), ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[8].callGetProperty(d), Integer.TYPE)), Integer.TYPE), callSiteArray[9].callGetProperty(Transparency.class)), BufferedImage.class);
                    Graphics2D g2 = (Graphics2D)ScriptBytecodeAdapter.castToType(callSiteArray[10].call(image), Graphics2D.class);
                    callSiteArray[11].call(it, g2);
                    callSiteArray[12].call(g2);
                    return callSiteArray[13].callConstructor(ImageIcon.class, image);
                }
                return null;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _loadOutputTransforms_closure2.class) {
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
                stringArray[1] = "width";
                stringArray[2] = "preferredSize";
                stringArray[3] = "localGraphicsEnvironment";
                stringArray[4] = "defaultScreenDevice";
                stringArray[5] = "defaultConfiguration";
                stringArray[6] = "createCompatibleImage";
                stringArray[7] = "width";
                stringArray[8] = "height";
                stringArray[9] = "TRANSLUCENT";
                stringArray[10] = "createGraphics";
                stringArray[11] = "print";
                stringArray[12] = "dispose";
                stringArray[13] = "<$constructor$>";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[14];
                _loadOutputTransforms_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_loadOutputTransforms_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _loadOutputTransforms_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[9].call((Object)transforms, new _loadOutputTransforms_closure2(OutputTransforms.class, OutputTransforms.class));
        public class _loadOutputTransforms_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _loadOutputTransforms_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure3.$getCallSiteArray();
                if (it instanceof Icon) {
                    return it;
                }
                return null;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _loadOutputTransforms_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[]{};
                return new CallSiteArray(_loadOutputTransforms_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _loadOutputTransforms_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[10].call((Object)transforms, new _loadOutputTransforms_closure3(OutputTransforms.class, OutputTransforms.class));
        public class _loadOutputTransforms_closure4
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _loadOutputTransforms_closure4(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure4.$getCallSiteArray();
                if (it instanceof Image) {
                    return callSiteArray[0].callConstructor(ImageIcon.class, it);
                }
                return null;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _loadOutputTransforms_closure4.class) {
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
                stringArray[0] = "<$constructor$>";
                return new CallSiteArray(_loadOutputTransforms_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _loadOutputTransforms_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[11].call((Object)transforms, new _loadOutputTransforms_closure4(OutputTransforms.class, OutputTransforms.class));
        public class _loadOutputTransforms_closure5
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _loadOutputTransforms_closure5(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _loadOutputTransforms_closure5.$getCallSiteArray();
                if (ScriptBytecodeAdapter.compareNotEqual(it, null)) {
                    return new GStringImpl(new Object[]{callSiteArray[0].call(InvokerHelper.class, it)}, new String[]{"", ""});
                }
                return null;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _loadOutputTransforms_closure5.class) {
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
                stringArray[0] = "inspect";
                return new CallSiteArray(_loadOutputTransforms_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _loadOutputTransforms_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[12].call((Object)transforms, new _loadOutputTransforms_closure5(OutputTransforms.class, OutputTransforms.class));
        return transforms;
    }

    public static Object transformResult(Object base, Object transforms) {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        Closure c = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[13].call(transforms), Iterator.class);
        while (iterator.hasNext()) {
            c = (Closure)ScriptBytecodeAdapter.castToType(iterator.next(), Closure.class);
            Object result = callSiteArray[14].call((Object)c, ScriptBytecodeAdapter.createPojoWrapper(base, Object.class));
            if (!ScriptBytecodeAdapter.compareNotEqual(result, null)) continue;
            return result;
        }
        return base;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != OutputTransforms.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    private static final Object groovy_ui_OutputTransforms$ObjectHolder_localTransforms_initExpr() {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return callSiteArray[15].callStatic(OutputTransforms.class);
        }
        return OutputTransforms.loadOutputTransforms();
    }

    public static Object getLocalTransforms() {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        return callSiteArray[16].callGetProperty(ObjectHolder_localTransforms.class);
    }

    public /* synthetic */ Object this$dist$invoke$1(String name, Object args) {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(OutputTransforms.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$1(String name, Object value) {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, OutputTransforms.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$1(String name) {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(OutputTransforms.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public static Object transformResult(Object base) {
        CallSite[] callSiteArray = OutputTransforms.$getCallSiteArray();
        return OutputTransforms.transformResult(base, callSiteArray[17].callGroovyObjectGetProperty(OutputTransforms.class));
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
        stringArray[1] = "getProperty";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "exists";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "setVariable";
        stringArray[7] = "evaluate";
        stringArray[8] = "leftShift";
        stringArray[9] = "leftShift";
        stringArray[10] = "leftShift";
        stringArray[11] = "leftShift";
        stringArray[12] = "leftShift";
        stringArray[13] = "iterator";
        stringArray[14] = "call";
        stringArray[15] = "loadOutputTransforms";
        stringArray[16] = "INSTANCE";
        stringArray[17] = "localTransforms";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[18];
        OutputTransforms.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(OutputTransforms.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = OutputTransforms.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    static class ObjectHolder_localTransforms
    implements GroovyObject {
        private static final Object INSTANCE;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ SoftReference $callSiteArray;

        public ObjectHolder_localTransforms() {
            MetaClass metaClass;
            CallSite[] callSiteArray = ObjectHolder_localTransforms.$getCallSiteArray();
            this.metaClass = metaClass = this.$getStaticMetaClass();
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = ObjectHolder_localTransforms.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(ObjectHolder_localTransforms.class, OutputTransforms.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = ObjectHolder_localTransforms.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(ObjectHolder_localTransforms.class, OutputTransforms.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = ObjectHolder_localTransforms.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, OutputTransforms.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = ObjectHolder_localTransforms.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, OutputTransforms.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = ObjectHolder_localTransforms.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(ObjectHolder_localTransforms.class, OutputTransforms.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = ObjectHolder_localTransforms.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(ObjectHolder_localTransforms.class, OutputTransforms.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != ObjectHolder_localTransforms.class) {
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
            Object object;
            INSTANCE = object = ObjectHolder_localTransforms.$getCallSiteArray()[0].callStatic(OutputTransforms.class);
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[1];
            stringArray[0] = "groovy_ui_OutputTransforms$ObjectHolder_localTransforms_initExpr";
            return new CallSiteArray(ObjectHolder_localTransforms.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = ObjectHolder_localTransforms.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}


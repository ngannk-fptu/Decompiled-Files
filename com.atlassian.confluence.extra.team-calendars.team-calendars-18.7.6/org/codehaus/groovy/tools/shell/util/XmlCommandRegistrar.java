/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.util.Node;
import groovy.util.XmlParser;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.net.URL;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.Shell;
import org.codehaus.groovy.tools.shell.util.Logger;

public class XmlCommandRegistrar
implements GroovyObject {
    private final Logger log;
    private final Shell shell;
    private final ClassLoader classLoader;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public XmlCommandRegistrar(Shell shell, ClassLoader classLoader) {
        MetaClass metaClass;
        CallSite[] callSiteArray = XmlCommandRegistrar.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Shell shell2 = shell;
            valueRecorder.record(shell2, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(shell2, null);
            valueRecorder.record(bl, 14);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert shell != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        ValueRecorder valueRecorder2 = new ValueRecorder();
        try {
            ClassLoader classLoader2 = classLoader;
            valueRecorder2.record(classLoader2, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(classLoader2, null);
            valueRecorder2.record(bl, 20);
            if (bl) {
                valueRecorder2.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert classLoader != null", valueRecorder2), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder2.clear();
            throw throwable;
        }
        Shell shell3 = shell;
        this.shell = (Shell)ScriptBytecodeAdapter.castToType(shell3, Shell.class);
        ClassLoader classLoader3 = classLoader;
        this.classLoader = (ClassLoader)ScriptBytecodeAdapter.castToType(classLoader3, ClassLoader.class);
    }

    public void register(URL url) {
        CallSite[] callSiteArray = XmlCommandRegistrar.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            URL uRL = url;
            valueRecorder.record(uRL, 8);
            if (DefaultTypeTransformation.booleanUnbox(uRL)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert url", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].callGetProperty(this.log))) {
            callSiteArray[3].call((Object)this.log, new GStringImpl(new Object[]{url}, new String[]{"Registering commands from: ", ""}));
        }
        public class _register_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _register_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _register_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Reader reader) {
                CallSite[] callSiteArray = _register_closure1.$getCallSiteArray();
                Node doc = (Node)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callConstructor(XmlParser.class), reader), Node.class);
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

                    public Object doCall(Node element) {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        String classname = ShortTypeHandling.castToString(callSiteArray[0].call(element));
                        Class type = ShortTypeHandling.castToClass(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this), classname));
                        Command command = (Command)ScriptBytecodeAdapter.asType(callSiteArray[3].call((Object)type, callSiteArray[4].callGroovyObjectGetProperty(this)), Command.class);
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this)))) {
                            callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[9].callGetProperty(command), command}, new String[]{"Created command '", "': ", ""}));
                        }
                        return callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(this), command);
                    }

                    public Object call(Node element) {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return callSiteArray[12].callCurrent((GroovyObject)this, element);
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
                        stringArray[0] = "text";
                        stringArray[1] = "loadClass";
                        stringArray[2] = "classLoader";
                        stringArray[3] = "newInstance";
                        stringArray[4] = "shell";
                        stringArray[5] = "debugEnabled";
                        stringArray[6] = "log";
                        stringArray[7] = "debug";
                        stringArray[8] = "log";
                        stringArray[9] = "name";
                        stringArray[10] = "leftShift";
                        stringArray[11] = "shell";
                        stringArray[12] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[13];
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
                return callSiteArray[2].call(callSiteArray[3].call(doc), new _closure2(this, this.getThisObject()));
            }

            public Object call(Reader reader) {
                CallSite[] callSiteArray = _register_closure1.$getCallSiteArray();
                return callSiteArray[4].callCurrent((GroovyObject)this, reader);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _register_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "parse";
                stringArray[1] = "<$constructor$>";
                stringArray[2] = "each";
                stringArray[3] = "children";
                stringArray[4] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _register_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_register_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _register_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[4].call((Object)url, new _register_closure1(this, this));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != XmlCommandRegistrar.class) {
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
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "debugEnabled";
        stringArray[3] = "debug";
        stringArray[4] = "withReader";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[5];
        XmlCommandRegistrar.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(XmlCommandRegistrar.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = XmlCommandRegistrar.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


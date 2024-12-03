/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import jline.console.completer.Completer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.util.SimpleCompletor;

public abstract class ComplexCommandSupport
extends CommandSupport {
    protected final List<String> functions;
    protected final String defaultFunction;
    private Object do_all;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    protected ComplexCommandSupport(Groovysh shell, String name, String shortcut, List<String> comFunctions) {
        CallSite[] callSiteArray = ComplexCommandSupport.$getCallSiteArray();
        this(shell, name, shortcut, comFunctions, null);
    }

    /*
     * Unable to fully structure code
     */
    protected ComplexCommandSupport(Groovysh shell, String name, String shortcut, List<String> comFunctions, String defaultFunction) {
        var6_6 = ComplexCommandSupport.$getCallSiteArray();
        super(shell, name, shortcut);
        var7_7 = new _closure1(this, this);
        this.do_all = var7_7;
        var8_8 = comFunctions;
        this.functions = (List)ScriptBytecodeAdapter.castToType(var8_8, List.class);
        var9_9 = defaultFunction;
        this.defaultFunction = ShortTypeHandling.castToString(var9_9);
        var10_10 = new ValueRecorder();
        try {
            v0 = defaultFunction;
            var10_10.record(v0, 8);
            v1 = ScriptBytecodeAdapter.compareEqual(v0, null);
            var10_10.record(v1, 25);
            if (v1) ** GOTO lbl-1000
            v2 = defaultFunction;
            var10_10.record(v2, 36);
            v3 = this.functions;
            var10_10.record(v3, 55);
            var10_10.record(v3, 55);
            v4 = ScriptBytecodeAdapter.isCase(v2, v3);
            var10_10.record(v4, 52);
            if (v4) lbl-1000:
            // 2 sources

            {
                v5 = true;
            } else {
                v5 = false;
            }
            var10_10.record(v5, 33);
            if (v5) {
                var10_10.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert(defaultFunction  == null || defaultFunction in functions)", var10_10), null);
            }
        }
        catch (Throwable v6) {
            var10_10.clear();
            throw v6;
        }
    }

    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = ComplexCommandSupport.$getCallSiteArray();
        Reference<Object> c = new Reference<Object>(callSiteArray[0].callConstructor(SimpleCompletor.class));
        public class _createCompleters_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference c;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _createCompleters_closure2(Object _outerInstance, Object _thisObject, Reference c) {
                Reference reference;
                CallSite[] callSiteArray = _createCompleters_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.c = reference = c;
            }

            public Object doCall(String it) {
                CallSite[] callSiteArray = _createCompleters_closure2.$getCallSiteArray();
                return callSiteArray[0].call(this.c.get(), it);
            }

            public Object call(String it) {
                CallSite[] callSiteArray = _createCompleters_closure2.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[1].callCurrent((GroovyObject)this, it);
                }
                return this.doCall(it);
            }

            public Object getC() {
                CallSite[] callSiteArray = _createCompleters_closure2.$getCallSiteArray();
                return this.c.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _createCompleters_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "add";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _createCompleters_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_createCompleters_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _createCompleters_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[1].call(this.functions, new _createCompleters_closure2(this, this, c));
        return ScriptBytecodeAdapter.createList(new Object[]{c.get(), null});
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = ComplexCommandSupport.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            List list = args;
            valueRecorder.record(list, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(list, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert args != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[2].call(args), 0)) {
            if (DefaultTypeTransformation.booleanUnbox(this.defaultFunction)) {
                List list;
                args = list = ScriptBytecodeAdapter.createList(new Object[]{this.defaultFunction});
            } else {
                callSiteArray[3].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[4].callGroovyObjectGetProperty(this), this.functions}, new String[]{"Command '", "' requires at least one argument of ", ""}));
            }
        }
        return callSiteArray[5].callCurrent(this, callSiteArray[6].call(args, 0), callSiteArray[7].call(args));
    }

    protected Object executeFunction(String fname, List<String> args) {
        CallSite[] callSiteArray = ComplexCommandSupport.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            List<String> list = args;
            valueRecorder.record(list, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(list, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert args != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        List<String> myFunctions = this.functions;
        if (ScriptBytecodeAdapter.isCase(fname, myFunctions)) {
            Closure func = null;
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                Object object = callSiteArray[8].callCurrent((GroovyObject)this, fname);
                func = (Closure)ScriptBytecodeAdapter.castToType(object, Closure.class);
            } else {
                Closure closure;
                func = closure = this.loadFunction(fname);
            }
            callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{fname, args}, new String[]{"Invoking function '", "' w/args: ", ""}));
            return callSiteArray[11].call((Object)func, args);
        }
        return callSiteArray[12].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{fname, myFunctions}, new String[]{"Unknown function name: '", "'. Valid arguments: ", ""}));
    }

    protected Closure loadFunction(String name) {
        CallSite[] callSiteArray = ComplexCommandSupport.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = name;
            valueRecorder.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert name", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Closure closure = (Closure)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.getGroovyObjectProperty(ComplexCommandSupport.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"do_", ""}))), Closure.class);
        try {
            return closure;
        }
        catch (MissingPropertyException e) {
            Closure closure2 = (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[13].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{e}, new String[]{"Failed to load delegate function: ", ""})), Closure.class);
            return closure2;
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ComplexCommandSupport.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getDo_all() {
        return this.do_all;
    }

    public void setDo_all(Object object) {
        this.do_all = object;
    }

    public /* synthetic */ List super$2$createCompleters() {
        return super.createCompleters();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "each";
        stringArray[2] = "size";
        stringArray[3] = "fail";
        stringArray[4] = "name";
        stringArray[5] = "executeFunction";
        stringArray[6] = "getAt";
        stringArray[7] = "tail";
        stringArray[8] = "loadFunction";
        stringArray[9] = "debug";
        stringArray[10] = "log";
        stringArray[11] = "call";
        stringArray[12] = "fail";
        stringArray[13] = "fail";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[14];
        ComplexCommandSupport.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ComplexCommandSupport.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ComplexCommandSupport.$createCallSiteArray();
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

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
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

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                    return ScriptBytecodeAdapter.compareNotEqual(it, "all");
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _closure3.$getCallSiteArray();
                    return this.doCall(null);
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

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[]{};
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

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                    return callSiteArray[0].callCurrent(this, it, ScriptBytecodeAdapter.createList(new Object[0]));
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                    return this.doCall(null);
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

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "executeFunction";
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
            return callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].callGroovyObjectGetProperty(this), new _closure3(this, this.getThisObject())), new _closure4(this, this.getThisObject()));
        }

        public Object doCall() {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return this.doCall(null);
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
            stringArray[0] = "collect";
            stringArray[1] = "findAll";
            stringArray[2] = "functions";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[3];
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
}


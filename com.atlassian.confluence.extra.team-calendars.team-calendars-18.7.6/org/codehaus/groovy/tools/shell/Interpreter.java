/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Script;
import java.lang.ref.SoftReference;
import java.lang.reflect.Method;
import java.util.Collection;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Evaluator;
import org.codehaus.groovy.tools.shell.Parser;
import org.codehaus.groovy.tools.shell.util.Logger;

public class Interpreter
implements Evaluator,
GroovyObject {
    private static final String SCRIPT_FILENAME = "groovysh_evaluate";
    private final Logger log;
    private final GroovyShell shell;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Interpreter(ClassLoader classLoader, Binding binding) {
        MetaClass metaClass;
        CallSite[] callSiteArray = Interpreter.$getCallSiteArray();
        Object object = callSiteArray[0].call(Logger.class, callSiteArray[1].callGroovyObjectGetProperty(this));
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object, Logger.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            ClassLoader classLoader2 = classLoader;
            valueRecorder.record(classLoader2, 8);
            if (DefaultTypeTransformation.booleanUnbox(classLoader2)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert classLoader", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        ValueRecorder valueRecorder2 = new ValueRecorder();
        try {
            Binding binding2 = binding;
            valueRecorder2.record(binding2, 8);
            if (DefaultTypeTransformation.booleanUnbox(binding2)) {
                valueRecorder2.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert binding", valueRecorder2), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder2.clear();
            throw throwable;
        }
        Object object2 = callSiteArray[2].callConstructor(GroovyShell.class, classLoader, binding);
        this.shell = (GroovyShell)ScriptBytecodeAdapter.castToType(object2, GroovyShell.class);
    }

    public Binding getContext() {
        CallSite[] callSiteArray = Interpreter.$getCallSiteArray();
        return (Binding)ScriptBytecodeAdapter.castToType(callSiteArray[3].callGroovyObjectGetProperty(this.shell), Binding.class);
    }

    public GroovyClassLoader getClassLoader() {
        CallSite[] callSiteArray = Interpreter.$getCallSiteArray();
        return (GroovyClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[4].callGroovyObjectGetProperty(this.shell), GroovyClassLoader.class);
    }

    @Override
    public Object evaluate(Collection<String> buffer) {
        CallSite[] callSiteArray = Interpreter.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Collection<String> collection = buffer;
            valueRecorder.record(collection, 8);
            if (DefaultTypeTransformation.booleanUnbox(collection)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert buffer", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Object source = callSiteArray[5].call(buffer, callSiteArray[6].callGetProperty(Parser.class));
        Object result = null;
        Reference<Object> type = new Reference<Object>(null);
        Class cfr_ignored_0 = type.get();
        try {
            Script script = (Script)ScriptBytecodeAdapter.castToType(callSiteArray[7].call(this.shell, source, SCRIPT_FILENAME), Script.class);
            Object object = callSiteArray[8].call(script);
            type.set(ShortTypeHandling.castToClass(object));
            callSiteArray[9].call((Object)this.log, new GStringImpl(new Object[]{script}, new String[]{"Compiled script: ", ""}));
            public class _evaluate_closure1
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _evaluate_closure1(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _evaluate_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Method it) {
                    CallSite[] callSiteArray = _evaluate_closure1.$getCallSiteArray();
                    return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), "main");
                }

                public Object call(Method it) {
                    CallSite[] callSiteArray = _evaluate_closure1.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[1].callCurrent((GroovyObject)this, it);
                    }
                    return this.doCall(it);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _evaluate_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "name";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _evaluate_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_evaluate_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _evaluate_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].callGetProperty(type.get()), new _evaluate_closure1(this, this)))) {
                Object object2;
                result = object2 = callSiteArray[12].call(script);
            }
            callSiteArray[13].call((Object)this.log, new GStringImpl(new Object[]{callSiteArray[14].call(InvokerHelper.class, result), callSiteArray[15].callSafe(result)}, new String[]{"Evaluation result: ", " (", ")"}));
            public class _evaluate_closure2
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference type;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _evaluate_closure2(Object _outerInstance, Object _thisObject, Reference type) {
                    Reference reference;
                    CallSite[] callSiteArray = _evaluate_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.type = reference = type;
                }

                public Object doCall(Method m) {
                    CallSite[] callSiteArray = _evaluate_closure2.$getCallSiteArray();
                    if (!(ScriptBytecodeAdapter.isCase(callSiteArray[0].callGetProperty(m), ScriptBytecodeAdapter.createList(new Object[]{"main", "run"})) || DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(callSiteArray[2].callGetProperty(m), "super$")) || DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(callSiteArray[4].callGetProperty(m), "class$")) || DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(callSiteArray[6].callGetProperty(m), "$")))) {
                        callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[9].callGetProperty(m)}, new String[]{"Saving method definition: ", ""}));
                        Object object = callSiteArray[10].callConstructor(MethodClosure.class, callSiteArray[11].call(this.type.get()), callSiteArray[12].callGetProperty(m));
                        callSiteArray[13].call(callSiteArray[14].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[15].callGetProperty(m)}, new String[]{"", ""}), object);
                        return object;
                    }
                    return null;
                }

                public Object call(Method m) {
                    CallSite[] callSiteArray = _evaluate_closure2.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[16].callCurrent((GroovyObject)this, m);
                    }
                    return this.doCall(m);
                }

                public Class getType() {
                    CallSite[] callSiteArray = _evaluate_closure2.$getCallSiteArray();
                    return ShortTypeHandling.castToClass(this.type.get());
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _evaluate_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "name";
                    stringArray[1] = "startsWith";
                    stringArray[2] = "name";
                    stringArray[3] = "startsWith";
                    stringArray[4] = "name";
                    stringArray[5] = "startsWith";
                    stringArray[6] = "name";
                    stringArray[7] = "debug";
                    stringArray[8] = "log";
                    stringArray[9] = "name";
                    stringArray[10] = "<$constructor$>";
                    stringArray[11] = "newInstance";
                    stringArray[12] = "name";
                    stringArray[13] = "putAt";
                    stringArray[14] = "context";
                    stringArray[15] = "name";
                    stringArray[16] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[17];
                    _evaluate_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_evaluate_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _evaluate_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[16].call(callSiteArray[17].callGetProperty(type.get()), new _evaluate_closure2(this, this, type));
        }
        catch (Throwable throwable) {
            callSiteArray[23].call(callSiteArray[24].callGroovyObjectGetProperty(this), callSiteArray[25].callGetPropertySafe(type.get()));
            callSiteArray[26].call(callSiteArray[27].callGroovyObjectGetProperty(this), "$_run_closure");
            throw throwable;
        }
        callSiteArray[18].call(callSiteArray[19].callGroovyObjectGetProperty(this), callSiteArray[20].callGetPropertySafe(type.get()));
        callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(this), "$_run_closure");
        return result;
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Interpreter.class) {
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

    public static String getSCRIPT_FILENAME() {
        return SCRIPT_FILENAME;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "create";
        stringArray[1] = "class";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "context";
        stringArray[4] = "classLoader";
        stringArray[5] = "join";
        stringArray[6] = "NEWLINE";
        stringArray[7] = "parse";
        stringArray[8] = "getClass";
        stringArray[9] = "debug";
        stringArray[10] = "any";
        stringArray[11] = "declaredMethods";
        stringArray[12] = "run";
        stringArray[13] = "debug";
        stringArray[14] = "toString";
        stringArray[15] = "getClass";
        stringArray[16] = "each";
        stringArray[17] = "declaredMethods";
        stringArray[18] = "removeClassCacheEntry";
        stringArray[19] = "classLoader";
        stringArray[20] = "name";
        stringArray[21] = "removeClassCacheEntry";
        stringArray[22] = "classLoader";
        stringArray[23] = "removeClassCacheEntry";
        stringArray[24] = "classLoader";
        stringArray[25] = "name";
        stringArray[26] = "removeClassCacheEntry";
        stringArray[27] = "classLoader";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[28];
        Interpreter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Interpreter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Interpreter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


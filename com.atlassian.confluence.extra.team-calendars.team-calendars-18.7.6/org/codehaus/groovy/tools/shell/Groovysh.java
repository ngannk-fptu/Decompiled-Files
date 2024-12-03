/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.Terminal
 *  jline.WindowsTerminal
 *  jline.console.history.FileHistory
 *  org.fusesource.jansi.AnsiRenderer
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.MissingPropertyException;
import groovy.lang.Reference;
import groovyjarjarantlr.TokenStreamException;
import java.io.File;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;
import jline.Terminal;
import jline.WindowsTerminal;
import jline.console.history.FileHistory;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.MultipleCompilationErrorsException;
import org.codehaus.groovy.control.messages.Message;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StackTraceUtils;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.BufferManager;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.ExitNotification;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.InteractiveShellRunner;
import org.codehaus.groovy.tools.shell.Interpreter;
import org.codehaus.groovy.tools.shell.ParseCode;
import org.codehaus.groovy.tools.shell.Parser;
import org.codehaus.groovy.tools.shell.Shell;
import org.codehaus.groovy.tools.shell.commands.LoadCommand;
import org.codehaus.groovy.tools.shell.commands.RecordCommand;
import org.codehaus.groovy.tools.shell.util.CurlyCountingGroovyLexer;
import org.codehaus.groovy.tools.shell.util.DefaultCommandsRegistrar;
import org.codehaus.groovy.tools.shell.util.MessageSource;
import org.codehaus.groovy.tools.shell.util.PackageHelper;
import org.codehaus.groovy.tools.shell.util.PackageHelperImpl;
import org.codehaus.groovy.tools.shell.util.Preferences;
import org.codehaus.groovy.tools.shell.util.ScriptVariableAnalyzer;
import org.codehaus.groovy.tools.shell.util.XmlCommandRegistrar;
import org.fusesource.jansi.AnsiRenderer;

public class Groovysh
extends Shell {
    private static final MessageSource messages;
    private static final Pattern TYPEDEF_PATTERN;
    private static final Pattern METHODDEF_PATTERN;
    public static final String COLLECTED_BOUND_VARS_MAP_VARNAME = "groovysh_collected_boundvars";
    public static final String INTERPRETER_MODE_PREFERENCE_KEY = "interpreterMode";
    public static final String AUTOINDENT_PREFERENCE_KEY = "autoindent";
    public static final String COLORS_PREFERENCE_KEY = "colors";
    public static final String SANITIZE_PREFERENCE_KEY = "sanitizeStackTrace";
    public static final String SHOW_LAST_RESULT_PREFERENCE_KEY = "showLastResult";
    public static final String METACLASS_COMPLETION_PREFIX_LENGTH_PREFERENCE_KEY = "meta-completion-prefix-length";
    private final BufferManager buffers;
    private final Parser parser;
    private final Interpreter interp;
    private final List<String> imports;
    private int indentSize;
    private InteractiveShellRunner runner;
    private FileHistory history;
    private boolean historyFull;
    private String evictedLine;
    private PackageHelper packageHelper;
    private final AnsiRenderer prompt;
    private final Closure defaultResultHook;
    private Closure resultHook;
    private final Closure defaultErrorHook;
    private Closure errorHook;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Groovysh(ClassLoader classLoader, Binding binding, IO io, Closure registrar) {
        Closure closure;
        Closure closure2;
        int n;
        List list;
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        super(io);
        Object object = callSiteArray[0].callConstructor(BufferManager.class);
        this.buffers = (BufferManager)ScriptBytecodeAdapter.castToType(object, BufferManager.class);
        this.imports = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.indentSize = n = 2;
        Object object2 = callSiteArray[1].callConstructor(AnsiRenderer.class);
        this.prompt = (AnsiRenderer)ScriptBytecodeAdapter.castToType(object2, AnsiRenderer.class);
        _closure1 _closure110 = new _closure1(this, this);
        this.defaultResultHook = _closure110;
        this.resultHook = closure2 = this.defaultResultHook;
        _closure2 _closure210 = new _closure2(this, this);
        this.defaultErrorHook = _closure210;
        this.errorHook = closure = this.defaultErrorHook;
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
        ValueRecorder valueRecorder3 = new ValueRecorder();
        try {
            Closure closure3 = registrar;
            valueRecorder3.record(closure3, 8);
            if (DefaultTypeTransformation.booleanUnbox(closure3)) {
                valueRecorder3.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert registrar", valueRecorder3), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder3.clear();
            throw throwable;
        }
        Object object3 = callSiteArray[2].callConstructor(Parser.class);
        this.parser = (Parser)ScriptBytecodeAdapter.castToType(object3, Parser.class);
        Object object4 = callSiteArray[3].callConstructor(Interpreter.class, classLoader, binding);
        this.interp = (Interpreter)ScriptBytecodeAdapter.castToType(object4, Interpreter.class);
        callSiteArray[4].call((Object)registrar, this);
        Object object5 = callSiteArray[5].callConstructor(PackageHelperImpl.class, classLoader);
        this.packageHelper = (PackageHelper)ScriptBytecodeAdapter.castToType(object5, PackageHelper.class);
    }

    public Groovysh(ClassLoader classLoader, Binding binding, IO io) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        this(classLoader, binding, io, (Closure)ScriptBytecodeAdapter.castToType(callSiteArray[6].callStatic(Groovysh.class, classLoader), Closure.class));
    }

    public Groovysh(Binding binding, IO io) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        this((ClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[7].callGetProperty(callSiteArray[8].call(Thread.class)), ClassLoader.class), binding, io);
    }

    public Groovysh(IO io) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        this((Binding)ScriptBytecodeAdapter.castToType(callSiteArray[9].callConstructor(Binding.class), Binding.class), io);
    }

    public Groovysh() {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        this((IO)ScriptBytecodeAdapter.castToType(callSiteArray[10].callConstructor(IO.class), IO.class));
    }

    private static Closure createDefaultRegistrar(ClassLoader classLoader) {
        Reference<ClassLoader> classLoader2 = new Reference<ClassLoader>(classLoader);
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        public class _createDefaultRegistrar_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference classLoader;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _createDefaultRegistrar_closure3(Object _outerInstance, Object _thisObject, Reference classLoader) {
                Reference reference;
                CallSite[] callSiteArray = _createDefaultRegistrar_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.classLoader = reference = classLoader;
            }

            public Object doCall(Groovysh shell) {
                CallSite[] callSiteArray = _createDefaultRegistrar_closure3.$getCallSiteArray();
                URL xmlCommandResource = (URL)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callCurrent(this), "commands.xml"), URL.class);
                if (ScriptBytecodeAdapter.compareNotEqual(xmlCommandResource, null)) {
                    Object r = callSiteArray[2].callConstructor(XmlCommandRegistrar.class, shell, this.classLoader.get());
                    return callSiteArray[3].call(r, xmlCommandResource);
                }
                return callSiteArray[4].call(callSiteArray[5].callConstructor(DefaultCommandsRegistrar.class, shell));
            }

            public Object call(Groovysh shell) {
                CallSite[] callSiteArray = _createDefaultRegistrar_closure3.$getCallSiteArray();
                return callSiteArray[6].callCurrent((GroovyObject)this, shell);
            }

            public ClassLoader getClassLoader() {
                CallSite[] callSiteArray = _createDefaultRegistrar_closure3.$getCallSiteArray();
                return (ClassLoader)ScriptBytecodeAdapter.castToType(this.classLoader.get(), ClassLoader.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _createDefaultRegistrar_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getResource";
                stringArray[1] = "getClass";
                stringArray[2] = "<$constructor$>";
                stringArray[3] = "register";
                stringArray[4] = "register";
                stringArray[5] = "<$constructor$>";
                stringArray[6] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _createDefaultRegistrar_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_createDefaultRegistrar_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _createDefaultRegistrar_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return new _createDefaultRegistrar_closure3(Groovysh.class, Groovysh.class, classLoader2);
    }

    @Override
    public Object execute(String line) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = line;
            valueRecorder.record(string, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(string, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert line != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[11].call(callSiteArray[12].call(line)), 0)) {
            return null;
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[13].callCurrent((GroovyObject)this, line);
        } else {
            this.maybeRecordInput(line);
        }
        Object result = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].callCurrent((GroovyObject)this, line))) {
            Object object;
            Object object2;
            result = __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (object2 = callSiteArray[15].callCurrent((GroovyObject)this, line)) : (object = this.executeCommand(line));
            if (ScriptBytecodeAdapter.compareNotEqual(result, null)) {
                callSiteArray[16].callCurrent((GroovyObject)this, result);
            }
            return result;
        }
        List current = (List)ScriptBytecodeAdapter.castToType(callSiteArray[17].callConstructor(ArrayList.class, callSiteArray[18].call(this.buffers)), List.class);
        callSiteArray[19].call((Object)current, line);
        String importsSpec = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[20].callCurrent(this);
            importsSpec = ShortTypeHandling.castToString(object);
        } else {
            String string;
            importsSpec = string = this.getImportStatements();
        }
        Object status = callSiteArray[21].call((Object)this.parser, callSiteArray[22].call((Object)ScriptBytecodeAdapter.createList(new Object[]{importsSpec}), current));
        Object object = callSiteArray[23].callGetProperty(status);
        if (ScriptBytecodeAdapter.isCase(object, callSiteArray[24].callGetProperty(ParseCode.class))) {
            callSiteArray[25].call(callSiteArray[26].callGroovyObjectGetProperty(this), "Evaluating buffer...");
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[27].callGetProperty(callSiteArray[28].callGroovyObjectGetProperty(this)))) {
                callSiteArray[29].callCurrent((GroovyObject)this, current);
            }
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[30].call(Boolean.class, callSiteArray[31].callCurrent(this, INTERPRETER_MODE_PREFERENCE_KEY, "false"))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[32].callStatic(Groovysh.class, current))) {
                    Object object3;
                    List buff = (List)ScriptBytecodeAdapter.castToType(callSiteArray[33].call(callSiteArray[34].call((Object)ScriptBytecodeAdapter.createList(new Object[]{importsSpec}), ScriptBytecodeAdapter.createList(new Object[]{"true"})), current), List.class);
                    result = object3 = callSiteArray[36].call((Object)this.interp, buff);
                    callSiteArray[35].callCurrent((GroovyObject)this, object3);
                } else {
                    Object object4;
                    result = object4 = callSiteArray[37].callCurrent(this, importsSpec, current);
                }
            } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[38].call(Boolean.class, this.getPreference(INTERPRETER_MODE_PREFERENCE_KEY, "false"))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[39].callStatic(Groovysh.class, current))) {
                Object object5;
                List buff = (List)ScriptBytecodeAdapter.castToType(callSiteArray[40].call(callSiteArray[41].call((Object)ScriptBytecodeAdapter.createList(new Object[]{importsSpec}), ScriptBytecodeAdapter.createList(new Object[]{"true"})), current), List.class);
                result = object5 = callSiteArray[43].call((Object)this.interp, buff);
                callSiteArray[42].callCurrent((GroovyObject)this, object5);
            } else {
                Object object6;
                result = object6 = callSiteArray[44].callCurrent(this, importsSpec, current);
            }
            callSiteArray[45].call(this.buffers);
        } else if (ScriptBytecodeAdapter.isCase(object, callSiteArray[46].callGetProperty(ParseCode.class))) {
            callSiteArray[47].call((Object)this.buffers, current);
        } else {
            if (ScriptBytecodeAdapter.isCase(object, callSiteArray[48].callGetProperty(ParseCode.class))) {
                throw (Throwable)callSiteArray[49].callGetProperty(status);
            }
            throw (Throwable)callSiteArray[50].callConstructor(Error.class, new GStringImpl(new Object[]{callSiteArray[51].callGetProperty(status)}, new String[]{"Invalid parse status: ", ""}));
        }
        return result;
    }

    public static boolean isTypeOrMethodDeclaration(List<String> buffer) {
        String joined = DefaultGroovyMethods.join(buffer, "");
        return StringGroovyMethods.matches((CharSequence)joined, TYPEDEF_PATTERN) || StringGroovyMethods.matches((CharSequence)joined, METHODDEF_PATTERN);
    }

    private Object evaluateWithStoredBoundVars(String importsSpec, List<String> current) {
        Object object;
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        Object result = null;
        Reference<Object> variableBlocks = new Reference<Object>(null);
        Set boundVars = (Set)ScriptBytecodeAdapter.castToType(callSiteArray[52].call(ScriptVariableAnalyzer.class, callSiteArray[53].call(callSiteArray[54].call((Object)importsSpec, callSiteArray[55].callGetProperty(Parser.class)), callSiteArray[56].call(current, callSiteArray[57].callGetProperty(Parser.class))), callSiteArray[58].callGroovyObjectGetProperty(this.interp)), Set.class);
        if (DefaultTypeTransformation.booleanUnbox(boundVars)) {
            GStringImpl gStringImpl = new GStringImpl(new Object[]{COLLECTED_BOUND_VARS_MAP_VARNAME}, new String[]{"", " = new HashMap();"});
            variableBlocks.set(ShortTypeHandling.castToString(gStringImpl));
            public class _evaluateWithStoredBoundVars_closure4
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference variableBlocks;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _evaluateWithStoredBoundVars_closure4(Object _outerInstance, Object _thisObject, Reference variableBlocks) {
                    Reference reference;
                    CallSite[] callSiteArray = _evaluateWithStoredBoundVars_closure4.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.variableBlocks = reference = variableBlocks;
                }

                public Object doCall(String varname) {
                    CallSite[] callSiteArray = _evaluateWithStoredBoundVars_closure4.$getCallSiteArray();
                    Object object = callSiteArray[0].call(this.variableBlocks.get(), new GStringImpl(new Object[]{callSiteArray[1].callGetProperty(Groovysh.class), varname, varname}, new String[]{"\ntry {", "[\"", "\"] = ", ";\n} catch (MissingPropertyException e){}"}));
                    this.variableBlocks.set(ShortTypeHandling.castToString(object));
                    return object;
                }

                public Object call(String varname) {
                    CallSite[] callSiteArray = _evaluateWithStoredBoundVars_closure4.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[2].callCurrent((GroovyObject)this, varname);
                    }
                    return this.doCall(varname);
                }

                public String getVariableBlocks() {
                    CallSite[] callSiteArray = _evaluateWithStoredBoundVars_closure4.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.variableBlocks.get());
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _evaluateWithStoredBoundVars_closure4.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "plus";
                    stringArray[1] = "COLLECTED_BOUND_VARS_MAP_VARNAME";
                    stringArray[2] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _evaluateWithStoredBoundVars_closure4.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_evaluateWithStoredBoundVars_closure4.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _evaluateWithStoredBoundVars_closure4.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[59].call((Object)boundVars, new _evaluateWithStoredBoundVars_closure4(this, this, variableBlocks));
        }
        List buff = null;
        if (DefaultTypeTransformation.booleanUnbox(variableBlocks.get())) {
            Object object2 = callSiteArray[60].call(callSiteArray[61].call(callSiteArray[62].call((Object)ScriptBytecodeAdapter.createList(new Object[]{importsSpec}), ScriptBytecodeAdapter.createList(new Object[]{"try {", "true"})), current), ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[63].call(callSiteArray[64].call((Object)"} finally {", (Object)variableBlocks.get()), "}")}));
            buff = (List)ScriptBytecodeAdapter.castToType(object2, List.class);
        } else {
            Object object3 = callSiteArray[65].call(callSiteArray[66].call((Object)ScriptBytecodeAdapter.createList(new Object[]{importsSpec}), ScriptBytecodeAdapter.createList(new Object[]{"true"})), current);
            buff = (List)ScriptBytecodeAdapter.castToType(object3, List.class);
        }
        result = object = callSiteArray[68].call((Object)this.interp, buff);
        callSiteArray[67].callCurrent((GroovyObject)this, object);
        if (DefaultTypeTransformation.booleanUnbox(variableBlocks.get())) {
            Map boundVarValues = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[69].call(callSiteArray[70].callGroovyObjectGetProperty(this.interp), COLLECTED_BOUND_VARS_MAP_VARNAME), Map.class);
            public class _evaluateWithStoredBoundVars_closure5
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _evaluateWithStoredBoundVars_closure5(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _evaluateWithStoredBoundVars_closure5.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(String name, Object value) {
                    CallSite[] callSiteArray = _evaluateWithStoredBoundVars_closure5.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), name, value);
                }

                public Object call(String name, Object value) {
                    CallSite[] callSiteArray = _evaluateWithStoredBoundVars_closure5.$getCallSiteArray();
                    return callSiteArray[3].callCurrent(this, name, value);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _evaluateWithStoredBoundVars_closure5.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "setVariable";
                    stringArray[1] = "context";
                    stringArray[2] = "interp";
                    stringArray[3] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _evaluateWithStoredBoundVars_closure5.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_evaluateWithStoredBoundVars_closure5.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _evaluateWithStoredBoundVars_closure5.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[71].call((Object)boundVarValues, new _evaluateWithStoredBoundVars_closure5(this, this));
        }
        return result;
    }

    protected Object executeCommand(String line) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnSuperN(Shell.class, this, "execute", new Object[]{line});
    }

    public void displayBuffer(List buffer) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            List list = buffer;
            valueRecorder.record(list, 8);
            if (DefaultTypeTransformation.booleanUnbox(list)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert buffer", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        public class _displayBuffer_closure6
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _displayBuffer_closure6(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _displayBuffer_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object line, Object index) {
                CallSite[] callSiteArray = _displayBuffer_closure6.$getCallSiteArray();
                Object lineNum = callSiteArray[0].callCurrent((GroovyObject)this, index);
                return callSiteArray[1].call(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{lineNum, line}, new String[]{" ", "@|bold >|@ ", ""}));
            }

            public Object call(Object line, Object index) {
                CallSite[] callSiteArray = _displayBuffer_closure6.$getCallSiteArray();
                return callSiteArray[4].callCurrent(this, line, index);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _displayBuffer_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "formatLineNumber";
                stringArray[1] = "println";
                stringArray[2] = "out";
                stringArray[3] = "io";
                stringArray[4] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _displayBuffer_closure6.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_displayBuffer_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _displayBuffer_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[72].call((Object)buffer, new _displayBuffer_closure6(this, this));
    }

    public String getImportStatements() {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        public class _getImportStatements_closure7
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _getImportStatements_closure7(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _getImportStatements_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String it) {
                CallSite[] callSiteArray = _getImportStatements_closure7.$getCallSiteArray();
                return new GStringImpl(new Object[]{it}, new String[]{"import ", ";"});
            }

            public Object call(String it) {
                CallSite[] callSiteArray = _getImportStatements_closure7.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[0].callCurrent((GroovyObject)this, it);
                }
                return this.doCall(it);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _getImportStatements_closure7.class) {
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
                stringArray[0] = "doCall";
                return new CallSiteArray(_getImportStatements_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _getImportStatements_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return ShortTypeHandling.castToString(callSiteArray[73].call(callSiteArray[74].call(this.imports, new _getImportStatements_closure7(this, this)), ""));
    }

    private String buildPrompt() {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        Object lineNum = callSiteArray[75].callCurrent((GroovyObject)this, callSiteArray[76].call(callSiteArray[77].call(this.buffers)));
        Object groovyshellProperty = callSiteArray[78].call(System.class, "groovysh.prompt");
        if (DefaultTypeTransformation.booleanUnbox(groovyshellProperty)) {
            return ShortTypeHandling.castToString(new GStringImpl(new Object[]{groovyshellProperty, lineNum}, new String[]{"@|bold ", ":|@", "@|bold >|@ "}));
        }
        Object groovyshellEnv = callSiteArray[79].call(System.class, "GROOVYSH_PROMPT");
        if (DefaultTypeTransformation.booleanUnbox(groovyshellEnv)) {
            return ShortTypeHandling.castToString(new GStringImpl(new Object[]{groovyshellEnv, lineNum}, new String[]{"@|bold ", ":|@", "@|bold >|@ "}));
        }
        return ShortTypeHandling.castToString(new GStringImpl(new Object[]{lineNum}, new String[]{"@|bold groovy:|@", "@|bold >|@ "}));
    }

    public String getIndentPrefix() {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        List buffer = (List)ScriptBytecodeAdapter.castToType(callSiteArray[80].call(this.buffers), List.class);
        if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[81].call(buffer), 1)) {
            return "";
        }
        StringBuilder src = (StringBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[82].callConstructor(StringBuilder.class), StringBuilder.class);
        String line = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[83].call(buffer), Iterator.class);
        while (iterator.hasNext()) {
            line = ShortTypeHandling.castToString(iterator.next());
            callSiteArray[84].call((Object)src, callSiteArray[85].call((Object)line, "\n"));
        }
        Object lexer = callSiteArray[86].call(CurlyCountingGroovyLexer.class, callSiteArray[87].call(src));
        try {
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                while (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[88].call(callSiteArray[89].call(lexer)), callSiteArray[90].callGetProperty(CurlyCountingGroovyLexer.class))) {
                }
            } else {
                while (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[91].call(callSiteArray[92].call(lexer)), callSiteArray[93].callGetProperty(CurlyCountingGroovyLexer.class))) {
                }
            }
        }
        catch (TokenStreamException e) {
        }
        int parenIndent = DefaultTypeTransformation.intUnbox(callSiteArray[94].call(callSiteArray[95].call(lexer), this.indentSize));
        return ShortTypeHandling.castToString(callSiteArray[96].call((Object)" ", callSiteArray[97].call(Math.class, parenIndent, 0)));
    }

    public String renderPrompt() {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return ShortTypeHandling.castToString(callSiteArray[98].call((Object)this.prompt, callSiteArray[99].callCurrent(this)));
        }
        return ShortTypeHandling.castToString(callSiteArray[100].call((Object)this.prompt, this.buildPrompt()));
    }

    protected String formatLineNumber(int num) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            int n = num;
            valueRecorder.record(n, 8);
            boolean bl = n >= 0;
            valueRecorder.record(bl, 12);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert num >= 0", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        return ShortTypeHandling.castToString(callSiteArray[101].call(callSiteArray[102].call(num), 3, "0"));
    }

    public File getUserStateDirectory() {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        Object userHome = callSiteArray[103].callConstructor(File.class, callSiteArray[104].call(System.class, "user.home"));
        Object dir = callSiteArray[105].callConstructor(File.class, userHome, ".groovy");
        return (File)ScriptBytecodeAdapter.castToType(callSiteArray[106].callGetProperty(dir), File.class);
    }

    protected void loadUserScript(String filename) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = filename;
            valueRecorder.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert filename", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        File file = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[107].callConstructor(File.class, callSiteArray[108].callCurrent(this), filename);
            file = (File)ScriptBytecodeAdapter.castToType(object, File.class);
        } else {
            Object object = callSiteArray[109].callConstructor(File.class, this.getUserStateDirectory(), filename);
            file = (File)ScriptBytecodeAdapter.castToType(object, File.class);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[110].call(file))) {
            Command command = (Command)ScriptBytecodeAdapter.asType(callSiteArray[111].call(callSiteArray[112].callGroovyObjectGetProperty(this), callSiteArray[113].callGetProperty(LoadCommand.class)), Command.class);
            if (DefaultTypeTransformation.booleanUnbox(command)) {
                callSiteArray[114].call(callSiteArray[115].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{file}, new String[]{"Loading user-script: ", ""}));
                Closure previousHook = this.resultHook;
                public class _loadUserScript_closure8
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _loadUserScript_closure8(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _loadUserScript_closure8.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object result) {
                        CallSite[] callSiteArray = _loadUserScript_closure8.$getCallSiteArray();
                        return null;
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _loadUserScript_closure8.class) {
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
                        return new CallSiteArray(_loadUserScript_closure8.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _loadUserScript_closure8.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                _loadUserScript_closure8 _loadUserScript_closure82 = new _loadUserScript_closure8(this, this);
                this.resultHook = _loadUserScript_closure82;
                try {
                    callSiteArray[116].call((Object)command, callSiteArray[117].call(callSiteArray[118].call(file)));
                }
                finally {
                    Closure closure = previousHook;
                    this.resultHook = (Closure)ScriptBytecodeAdapter.castToType(closure, Closure.class);
                }
            } else {
                callSiteArray[119].call(callSiteArray[120].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[121].callGetProperty(LoadCommand.class)}, new String[]{"Unable to load user-script, missing '", "' command"}));
            }
        }
    }

    protected void maybeRecordInput(String line) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        RecordCommand record = (RecordCommand)ScriptBytecodeAdapter.castToType(callSiteArray[122].call(callSiteArray[123].callGroovyObjectGetProperty(this), callSiteArray[124].callGetProperty(RecordCommand.class)), RecordCommand.class);
        if (ScriptBytecodeAdapter.compareNotEqual(record, null)) {
            callSiteArray[125].call((Object)record, line);
        }
    }

    protected void maybeRecordResult(Object result) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        RecordCommand record = (RecordCommand)ScriptBytecodeAdapter.castToType(callSiteArray[126].call(callSiteArray[127].callGroovyObjectGetProperty(this), callSiteArray[128].callGetProperty(RecordCommand.class)), RecordCommand.class);
        if (ScriptBytecodeAdapter.compareNotEqual(record, null)) {
            callSiteArray[129].call((Object)record, result);
        }
    }

    protected void maybeRecordError(Throwable cause) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        RecordCommand record = (RecordCommand)ScriptBytecodeAdapter.castToType(callSiteArray[130].call(callSiteArray[131].callGroovyObjectGetProperty(this), callSiteArray[132].callGetProperty(RecordCommand.class)), RecordCommand.class);
        if (ScriptBytecodeAdapter.compareNotEqual(record, null)) {
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[133].callCurrent(this, SANITIZE_PREFERENCE_KEY, "false"))) {
                    Object object = callSiteArray[134].call(StackTraceUtils.class, cause);
                    cause = (Throwable)ScriptBytecodeAdapter.castToType(object, Throwable.class);
                }
            } else if (DefaultTypeTransformation.booleanUnbox(this.getPreference(SANITIZE_PREFERENCE_KEY, "false"))) {
                Object object = callSiteArray[135].call(StackTraceUtils.class, cause);
                cause = (Throwable)ScriptBytecodeAdapter.castToType(object, Throwable.class);
            }
            callSiteArray[136].call((Object)record, cause);
        }
    }

    private void setLastResult(Object result) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(this.resultHook, null)) {
            throw (Throwable)callSiteArray[137].callConstructor(IllegalStateException.class, "Result hook is not set");
        }
        callSiteArray[138].call((Object)this.resultHook, ScriptBytecodeAdapter.createPojoWrapper(result, Object.class));
        Object object = result;
        callSiteArray[139].call(callSiteArray[140].callGroovyObjectGetProperty(this.interp), "_", object);
        callSiteArray[141].callCurrent((GroovyObject)this, result);
    }

    protected String getPreference(String key, String theDefault) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[142].call(Preferences.class, key, theDefault));
    }

    private void displayError(Throwable cause) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(this.errorHook, null)) {
            throw (Throwable)callSiteArray[143].callConstructor(IllegalStateException.class, "Error hook is not set");
        }
        if (cause instanceof MissingPropertyException && DefaultTypeTransformation.booleanUnbox(callSiteArray[144].callGetProperty(cause)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[145].callGetProperty(callSiteArray[146].callGetProperty(cause)), callSiteArray[147].callGetProperty(Interpreter.class))) {
            callSiteArray[148].call(callSiteArray[149].callGetProperty(callSiteArray[150].callGroovyObjectGetProperty(this)), callSiteArray[151].call((Object)"@|bold,red Unknown property|@: ", callSiteArray[152].callGetProperty(cause)));
            return;
        }
        callSiteArray[153].call((Object)this.errorHook, cause);
    }

    public int run(String evalString, List<String> filenames) {
        public class _run_closure9
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _run_closure9(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _run_closure9.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String it) {
                CallSite[] callSiteArray = _run_closure9.$getCallSiteArray();
                return new GStringImpl(new Object[]{callSiteArray[0].callGetProperty(LoadCommand.class), it}, new String[]{"", " ", ""});
            }

            public Object call(String it) {
                CallSite[] callSiteArray = _run_closure9.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[1].callCurrent((GroovyObject)this, it);
                }
                return this.doCall(it);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _run_closure9.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "COMMAND_NAME";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _run_closure9.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_run_closure9.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _run_closure9.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        List startCommands = ScriptBytecodeAdapter.createList(new Object[0]);
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual(evalString, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[154].call(callSiteArray[155].call(evalString)), 0)) {
                callSiteArray[156].call((Object)startCommands, evalString);
            }
        } else if (ScriptBytecodeAdapter.compareNotEqual(evalString, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[157].call(callSiteArray[158].call(evalString)), 0)) {
            callSiteArray[159].call((Object)startCommands, evalString);
        }
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareNotEqual(filenames, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[160].call(filenames), 0)) {
                callSiteArray[161].call((Object)startCommands, callSiteArray[162].call(filenames, new _run_closure9(this, this)));
            }
        } else if (ScriptBytecodeAdapter.compareNotEqual(filenames, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[163].call(filenames), 0)) {
            callSiteArray[164].call((Object)startCommands, callSiteArray[165].call(filenames, new _run_closure9(this, this)));
        }
        return DefaultTypeTransformation.intUnbox(callSiteArray[166].callCurrent((GroovyObject)this, callSiteArray[167].call((Object)startCommands, "\n")));
    }

    public int run(String commandLine) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        Object code = null;
        try {
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[168].callCurrent((GroovyObject)this, "groovysh.profile");
            } else {
                this.loadUserScript("groovysh.profile");
            }
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[169].callCurrent((GroovyObject)this, "groovysh.rc");
            } else {
                this.loadUserScript("groovysh.rc");
            }
            Object object = callSiteArray[170].callConstructor(InteractiveShellRunner.class, this, ScriptBytecodeAdapter.createGroovyObjectWrapper(ScriptBytecodeAdapter.getMethodPointer(this, "renderPrompt"), Closure.class));
            this.runner = (InteractiveShellRunner)ScriptBytecodeAdapter.castToType(object, InteractiveShellRunner.class);
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareNotEqual(commandLine, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[171].call(callSiteArray[172].call(commandLine)), 0)) {
                    callSiteArray[173].call(callSiteArray[174].callGroovyObjectGetProperty(this.runner), callSiteArray[175].call((Object)commandLine, "\n"));
                }
            } else if (ScriptBytecodeAdapter.compareNotEqual(commandLine, null) && ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[176].call(callSiteArray[177].call(commandLine)), 0)) {
                callSiteArray[178].call(callSiteArray[179].callGroovyObjectGetProperty(this.runner), callSiteArray[180].call((Object)commandLine, "\n"));
            }
            File histFile = (File)ScriptBytecodeAdapter.castToType(callSiteArray[181].callConstructor(File.class, callSiteArray[182].callGroovyObjectGetProperty(this), "groovysh.history"), File.class);
            Object object2 = callSiteArray[183].callConstructor(FileHistory.class, histFile);
            this.history = (FileHistory)ScriptBytecodeAdapter.castToType(object2, FileHistory.class);
            callSiteArray[184].call((Object)this.runner, this.history);
            Closure closure = ScriptBytecodeAdapter.getMethodPointer(this, "displayError");
            ScriptBytecodeAdapter.setGroovyObjectProperty(closure, Groovysh.class, this.runner, "errorHandler");
            callSiteArray[185].callCurrent((GroovyObject)this, this.runner);
            callSiteArray[186].call(this.runner);
            int n = 0;
            code = n;
        }
        catch (ExitNotification n) {
            Object object;
            callSiteArray[187].call(callSiteArray[188].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[189].callGetProperty(n)}, new String[]{"Exiting w/code: ", ""}));
            code = object = callSiteArray[190].callGetProperty(n);
        }
        catch (Throwable t) {
            callSiteArray[191].call(callSiteArray[192].callGetProperty(callSiteArray[193].callGroovyObjectGetProperty(this)), callSiteArray[194].call(messages, "info.fatal", t));
            callSiteArray[195].call((Object)t, callSiteArray[196].callGetProperty(callSiteArray[197].callGroovyObjectGetProperty(this)));
            int n = 1;
            code = n;
        }
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Object object = code;
            valueRecorder.record(object, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(object, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert code != null // This should never happen", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        return DefaultTypeTransformation.intUnbox(code);
    }

    public void displayWelcomeBanner(InteractiveShellRunner runner) {
        CallSite[] callSiteArray = Groovysh.$getCallSiteArray();
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[198].callGetProperty(callSiteArray[199].callGroovyObjectGetProperty(this))) && DefaultTypeTransformation.booleanUnbox(callSiteArray[200].callGetProperty(callSiteArray[201].callGroovyObjectGetProperty(this)))) {
            return;
        }
        Terminal term = (Terminal)ScriptBytecodeAdapter.castToType(callSiteArray[202].callGetProperty(callSiteArray[203].callGroovyObjectGetProperty(runner)), Terminal.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[204].callGetProperty(callSiteArray[205].callGroovyObjectGetProperty(this)))) {
            callSiteArray[206].call(callSiteArray[207].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{term}, new String[]{"Terminal (", ")"}));
            callSiteArray[208].call(callSiteArray[209].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[210].callGetProperty(term)}, new String[]{"    Supported:  ", ""}));
            callSiteArray[211].call(callSiteArray[212].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[213].callGetProperty(term)}, new String[]{"    ECHO:       (enabled: ", ")"}));
            callSiteArray[214].call(callSiteArray[215].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[216].call(term), callSiteArray[217].call(term)}, new String[]{"    H x W:      ", " x ", ""}));
            callSiteArray[218].call(callSiteArray[219].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[220].call(term)}, new String[]{"    ANSI:       ", ""}));
            if (term instanceof WindowsTerminal) {
                WindowsTerminal winterm = (WindowsTerminal)ScriptBytecodeAdapter.castToType(term, WindowsTerminal.class);
                callSiteArray[221].call(callSiteArray[222].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[223].callGetProperty(winterm)}, new String[]{"    Direct:     ", ""}));
            }
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[224].callGetProperty(callSiteArray[225].callGroovyObjectGetProperty(this)))) {
            int width = DefaultTypeTransformation.intUnbox(callSiteArray[226].call(term));
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (width < 1) {
                    int n;
                    width = n = 80;
                }
            } else if (width < 1) {
                int n;
                width = n = 80;
            }
            callSiteArray[227].call(callSiteArray[228].callGetProperty(callSiteArray[229].callGroovyObjectGetProperty(this)), callSiteArray[230].call(messages, "startup_banner.0", callSiteArray[231].callGetProperty(GroovySystem.class), callSiteArray[232].call(callSiteArray[233].callGetProperty(System.class), "java.version")));
            callSiteArray[234].call(callSiteArray[235].callGetProperty(callSiteArray[236].callGroovyObjectGetProperty(this)), callSiteArray[237].call((Object)messages, "startup_banner.1"));
            if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[238].call(callSiteArray[239].callGetProperty(callSiteArray[240].callGroovyObjectGetProperty(this)), callSiteArray[241].call((Object)"-", callSiteArray[242].call((Object)width, 1)));
            } else {
                callSiteArray[243].call(callSiteArray[244].callGetProperty(callSiteArray[245].callGroovyObjectGetProperty(this)), callSiteArray[246].call((Object)"-", width - 1));
            }
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Groovysh.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    static {
        Object object = Groovysh.$getCallSiteArray()[247].callConstructor(MessageSource.class, Groovysh.class);
        messages = (MessageSource)ScriptBytecodeAdapter.castToType(object, MessageSource.class);
        Object object2 = ScriptBytecodeAdapter.bitwiseNegate("^\\s*((?:public|protected|private|static|abstract|final)\\s+)*(?:class|enum|interface).*");
        TYPEDEF_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object2, Pattern.class);
        Object object3 = ScriptBytecodeAdapter.bitwiseNegate("^\\s*((?:public|protected|private|static|abstract|final|synchronized)\\s+)*[a-zA-Z_.]+[a-zA-Z_.<>]+\\s+[a-zA-Z_]+\\(.*");
        METHODDEF_PATTERN = (Pattern)ScriptBytecodeAdapter.castToType(object3, Pattern.class);
    }

    public final BufferManager getBuffers() {
        return this.buffers;
    }

    public final Parser getParser() {
        return this.parser;
    }

    public final Interpreter getInterp() {
        return this.interp;
    }

    public final List<String> getImports() {
        return this.imports;
    }

    public int getIndentSize() {
        return this.indentSize;
    }

    public void setIndentSize(int n) {
        this.indentSize = n;
    }

    public InteractiveShellRunner getRunner() {
        return this.runner;
    }

    public void setRunner(InteractiveShellRunner interactiveShellRunner) {
        this.runner = interactiveShellRunner;
    }

    public FileHistory getHistory() {
        return this.history;
    }

    public void setHistory(FileHistory fileHistory) {
        this.history = fileHistory;
    }

    public boolean getHistoryFull() {
        return this.historyFull;
    }

    public boolean isHistoryFull() {
        return this.historyFull;
    }

    public void setHistoryFull(boolean bl) {
        this.historyFull = bl;
    }

    public String getEvictedLine() {
        return this.evictedLine;
    }

    public void setEvictedLine(String string) {
        this.evictedLine = string;
    }

    public PackageHelper getPackageHelper() {
        return this.packageHelper;
    }

    public void setPackageHelper(PackageHelper packageHelper) {
        this.packageHelper = packageHelper;
    }

    public final Closure getDefaultResultHook() {
        return this.defaultResultHook;
    }

    public Closure getResultHook() {
        return this.resultHook;
    }

    public void setResultHook(Closure closure) {
        this.resultHook = closure;
    }

    public final Closure getDefaultErrorHook() {
        return this.defaultErrorHook;
    }

    public Closure getErrorHook() {
        return this.errorHook;
    }

    public void setErrorHook(Closure closure) {
        this.errorHook = closure;
    }

    public /* synthetic */ Object super$2$execute(String string) {
        return super.execute(string);
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "call";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "createDefaultRegistrar";
        stringArray[7] = "contextClassLoader";
        stringArray[8] = "currentThread";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "size";
        stringArray[12] = "trim";
        stringArray[13] = "maybeRecordInput";
        stringArray[14] = "isExecutable";
        stringArray[15] = "executeCommand";
        stringArray[16] = "setLastResult";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "current";
        stringArray[19] = "leftShift";
        stringArray[20] = "getImportStatements";
        stringArray[21] = "parse";
        stringArray[22] = "plus";
        stringArray[23] = "code";
        stringArray[24] = "COMPLETE";
        stringArray[25] = "debug";
        stringArray[26] = "log";
        stringArray[27] = "verbose";
        stringArray[28] = "io";
        stringArray[29] = "displayBuffer";
        stringArray[30] = "valueOf";
        stringArray[31] = "getPreference";
        stringArray[32] = "isTypeOrMethodDeclaration";
        stringArray[33] = "plus";
        stringArray[34] = "plus";
        stringArray[35] = "setLastResult";
        stringArray[36] = "evaluate";
        stringArray[37] = "evaluateWithStoredBoundVars";
        stringArray[38] = "valueOf";
        stringArray[39] = "isTypeOrMethodDeclaration";
        stringArray[40] = "plus";
        stringArray[41] = "plus";
        stringArray[42] = "setLastResult";
        stringArray[43] = "evaluate";
        stringArray[44] = "evaluateWithStoredBoundVars";
        stringArray[45] = "clearSelected";
        stringArray[46] = "INCOMPLETE";
        stringArray[47] = "updateSelected";
        stringArray[48] = "ERROR";
        stringArray[49] = "cause";
        stringArray[50] = "<$constructor$>";
        stringArray[51] = "code";
        stringArray[52] = "getBoundVars";
        stringArray[53] = "plus";
        stringArray[54] = "plus";
        stringArray[55] = "NEWLINE";
        stringArray[56] = "join";
        stringArray[57] = "NEWLINE";
        stringArray[58] = "classLoader";
        stringArray[59] = "each";
        stringArray[60] = "plus";
        stringArray[61] = "plus";
        stringArray[62] = "plus";
        stringArray[63] = "plus";
        stringArray[64] = "plus";
        stringArray[65] = "plus";
        stringArray[66] = "plus";
        stringArray[67] = "setLastResult";
        stringArray[68] = "evaluate";
        stringArray[69] = "getVariable";
        stringArray[70] = "context";
        stringArray[71] = "each";
        stringArray[72] = "eachWithIndex";
        stringArray[73] = "join";
        stringArray[74] = "collect";
        stringArray[75] = "formatLineNumber";
        stringArray[76] = "size";
        stringArray[77] = "current";
        stringArray[78] = "getProperty";
        stringArray[79] = "getenv";
        stringArray[80] = "current";
        stringArray[81] = "size";
        stringArray[82] = "<$constructor$>";
        stringArray[83] = "iterator";
        stringArray[84] = "append";
        stringArray[85] = "plus";
        stringArray[86] = "createGroovyLexer";
        stringArray[87] = "toString";
        stringArray[88] = "getType";
        stringArray[89] = "nextToken";
        stringArray[90] = "EOF";
        stringArray[91] = "getType";
        stringArray[92] = "nextToken";
        stringArray[93] = "EOF";
        stringArray[94] = "multiply";
        stringArray[95] = "getParenLevel";
        stringArray[96] = "multiply";
        stringArray[97] = "max";
        stringArray[98] = "render";
        stringArray[99] = "buildPrompt";
        stringArray[100] = "render";
        stringArray[101] = "padLeft";
        stringArray[102] = "toString";
        stringArray[103] = "<$constructor$>";
        stringArray[104] = "getProperty";
        stringArray[105] = "<$constructor$>";
        stringArray[106] = "canonicalFile";
        stringArray[107] = "<$constructor$>";
        stringArray[108] = "getUserStateDirectory";
        stringArray[109] = "<$constructor$>";
        stringArray[110] = "exists";
        stringArray[111] = "getAt";
        stringArray[112] = "registry";
        stringArray[113] = "COMMAND_NAME";
        stringArray[114] = "debug";
        stringArray[115] = "log";
        stringArray[116] = "load";
        stringArray[117] = "toURL";
        stringArray[118] = "toURI";
        stringArray[119] = "error";
        stringArray[120] = "log";
        stringArray[121] = "COMMAND_NAME";
        stringArray[122] = "getAt";
        stringArray[123] = "registry";
        stringArray[124] = "COMMAND_NAME";
        stringArray[125] = "recordInput";
        stringArray[126] = "getAt";
        stringArray[127] = "registry";
        stringArray[128] = "COMMAND_NAME";
        stringArray[129] = "recordResult";
        stringArray[130] = "getAt";
        stringArray[131] = "registry";
        stringArray[132] = "COMMAND_NAME";
        stringArray[133] = "getPreference";
        stringArray[134] = "deepSanitize";
        stringArray[135] = "deepSanitize";
        stringArray[136] = "recordError";
        stringArray[137] = "<$constructor$>";
        stringArray[138] = "call";
        stringArray[139] = "putAt";
        stringArray[140] = "context";
        stringArray[141] = "maybeRecordResult";
        stringArray[142] = "get";
        stringArray[143] = "<$constructor$>";
        stringArray[144] = "type";
        stringArray[145] = "canonicalName";
        stringArray[146] = "type";
        stringArray[147] = "SCRIPT_FILENAME";
        stringArray[148] = "println";
        stringArray[149] = "err";
        stringArray[150] = "io";
        stringArray[151] = "plus";
        stringArray[152] = "property";
        stringArray[153] = "call";
        stringArray[154] = "size";
        stringArray[155] = "trim";
        stringArray[156] = "add";
        stringArray[157] = "size";
        stringArray[158] = "trim";
        stringArray[159] = "add";
        stringArray[160] = "size";
        stringArray[161] = "addAll";
        stringArray[162] = "collect";
        stringArray[163] = "size";
        stringArray[164] = "addAll";
        stringArray[165] = "collect";
        stringArray[166] = "run";
        stringArray[167] = "join";
        stringArray[168] = "loadUserScript";
        stringArray[169] = "loadUserScript";
        stringArray[170] = "<$constructor$>";
        stringArray[171] = "size";
        stringArray[172] = "trim";
        stringArray[173] = "insert";
        stringArray[174] = "wrappedInputStream";
        stringArray[175] = "plus";
        stringArray[176] = "size";
        stringArray[177] = "trim";
        stringArray[178] = "insert";
        stringArray[179] = "wrappedInputStream";
        stringArray[180] = "plus";
        stringArray[181] = "<$constructor$>";
        stringArray[182] = "userStateDirectory";
        stringArray[183] = "<$constructor$>";
        stringArray[184] = "setHistory";
        stringArray[185] = "displayWelcomeBanner";
        stringArray[186] = "run";
        stringArray[187] = "debug";
        stringArray[188] = "log";
        stringArray[189] = "code";
        stringArray[190] = "code";
        stringArray[191] = "println";
        stringArray[192] = "err";
        stringArray[193] = "io";
        stringArray[194] = "format";
        stringArray[195] = "printStackTrace";
        stringArray[196] = "err";
        stringArray[197] = "io";
        stringArray[198] = "debug";
        stringArray[199] = "log";
        stringArray[200] = "quiet";
        stringArray[201] = "io";
        stringArray[202] = "terminal";
        stringArray[203] = "reader";
        stringArray[204] = "debug";
        stringArray[205] = "log";
        stringArray[206] = "debug";
        stringArray[207] = "log";
        stringArray[208] = "debug";
        stringArray[209] = "log";
        stringArray[210] = "supported";
        stringArray[211] = "debug";
        stringArray[212] = "log";
        stringArray[213] = "echoEnabled";
        stringArray[214] = "debug";
        stringArray[215] = "log";
        stringArray[216] = "getHeight";
        stringArray[217] = "getWidth";
        stringArray[218] = "debug";
        stringArray[219] = "log";
        stringArray[220] = "isAnsiSupported";
        stringArray[221] = "debug";
        stringArray[222] = "log";
        stringArray[223] = "directConsole";
        stringArray[224] = "quiet";
        stringArray[225] = "io";
        stringArray[226] = "getWidth";
        stringArray[227] = "println";
        stringArray[228] = "out";
        stringArray[229] = "io";
        stringArray[230] = "format";
        stringArray[231] = "version";
        stringArray[232] = "getAt";
        stringArray[233] = "properties";
        stringArray[234] = "println";
        stringArray[235] = "out";
        stringArray[236] = "io";
        stringArray[237] = "getAt";
        stringArray[238] = "println";
        stringArray[239] = "out";
        stringArray[240] = "io";
        stringArray[241] = "multiply";
        stringArray[242] = "minus";
        stringArray[243] = "println";
        stringArray[244] = "out";
        stringArray[245] = "io";
        stringArray[246] = "multiply";
        stringArray[247] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[248];
        Groovysh.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Groovysh.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Groovysh.$createCallSiteArray();
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

        public Object doCall(Object result) {
            int n;
            int n2;
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            int showLastResult = 0;
            showLastResult = !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (n2 = !DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this))) && (DefaultTypeTransformation.booleanUnbox(callSiteArray[2].callGetProperty(callSiteArray[3].callGroovyObjectGetProperty(this))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[4].callCurrent(this, callSiteArray[5].callGetProperty(Groovysh.class), "false"))) ? 1 : 0) : (n = !DefaultTypeTransformation.booleanUnbox(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this))) && (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this))) || DefaultTypeTransformation.booleanUnbox(callSiteArray[10].callCurrent(this, callSiteArray[11].callGetProperty(Groovysh.class), "false"))) ? 1 : 0);
            if (showLastResult != 0) {
                return callSiteArray[12].call(callSiteArray[13].callGetProperty(callSiteArray[14].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[15].call(InvokerHelper.class, result)}, new String[]{"@|bold ===>|@ ", ""}));
            }
            return null;
        }

        public Object call(Object result) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[16].callCurrent((GroovyObject)this, result);
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
            stringArray[0] = "quiet";
            stringArray[1] = "io";
            stringArray[2] = "verbose";
            stringArray[3] = "io";
            stringArray[4] = "getPreference";
            stringArray[5] = "SHOW_LAST_RESULT_PREFERENCE_KEY";
            stringArray[6] = "quiet";
            stringArray[7] = "io";
            stringArray[8] = "verbose";
            stringArray[9] = "io";
            stringArray[10] = "getPreference";
            stringArray[11] = "SHOW_LAST_RESULT_PREFERENCE_KEY";
            stringArray[12] = "println";
            stringArray[13] = "out";
            stringArray[14] = "io";
            stringArray[15] = "toString";
            stringArray[16] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[17];
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

        public Object doCall(Throwable cause) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            ValueRecorder valueRecorder = new ValueRecorder();
            try {
                Throwable throwable = cause;
                valueRecorder.record(throwable, 8);
                boolean bl = ScriptBytecodeAdapter.compareNotEqual(throwable, null);
                valueRecorder.record(bl, 14);
                if (bl) {
                    valueRecorder.clear();
                } else {
                    ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert cause != null", valueRecorder), null);
                }
            }
            catch (Throwable throwable) {
                valueRecorder.clear();
                throw throwable;
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callGetProperty(callSiteArray[1].callGroovyObjectGetProperty(this))) || !(cause instanceof CompilationFailedException)) {
                callSiteArray[2].call(callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[5].callGetProperty(callSiteArray[6].call(cause))}, new String[]{"@|bold,red ERROR|@ ", ":"}));
            }
            if (cause instanceof MultipleCompilationErrorsException) {
                StringWriter data = (StringWriter)ScriptBytecodeAdapter.castToType(callSiteArray[7].callConstructor(StringWriter.class), StringWriter.class);
                PrintWriter writer = (PrintWriter)ScriptBytecodeAdapter.castToType(callSiteArray[8].callConstructor(PrintWriter.class, data), PrintWriter.class);
                ErrorCollector collector = (ErrorCollector)ScriptBytecodeAdapter.castToType(callSiteArray[9].call((MultipleCompilationErrorsException)ScriptBytecodeAdapter.castToType(cause, MultipleCompilationErrorsException.class)), ErrorCollector.class);
                Iterator msgIterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[10].call(callSiteArray[11].call(collector)), Iterator.class);
                while (DefaultTypeTransformation.booleanUnbox(callSiteArray[12].call(msgIterator))) {
                    Message errorMsg = (Message)ScriptBytecodeAdapter.castToType(callSiteArray[13].call(msgIterator), Message.class);
                    callSiteArray[14].call((Object)errorMsg, writer);
                    if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call(msgIterator))) continue;
                    callSiteArray[16].call(writer);
                }
                return callSiteArray[17].call(callSiteArray[18].callGetProperty(callSiteArray[19].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[20].call(data)}, new String[]{"@|bold,red ", "|@"}));
            }
            callSiteArray[21].call(callSiteArray[22].callGetProperty(callSiteArray[23].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[24].callGetProperty(cause)}, new String[]{"@|bold,red ", "|@"}));
            callSiteArray[25].callCurrent((GroovyObject)this, cause);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[26].callGetProperty(callSiteArray[27].callGroovyObjectGetProperty(this)))) {
                return callSiteArray[28].call(callSiteArray[29].callGroovyObjectGetProperty(this), cause);
            }
            boolean sanitize = DefaultTypeTransformation.booleanUnbox(callSiteArray[30].callCurrent(this, callSiteArray[31].callGetProperty(Groovysh.class), "false"));
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[32].callGetProperty(callSiteArray[33].callGroovyObjectGetProperty(this))) && sanitize) {
                    Object object = callSiteArray[34].call(StackTraceUtils.class, cause);
                    cause = (Throwable)ScriptBytecodeAdapter.castToType(object, Throwable.class);
                }
            } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[35].callGetProperty(callSiteArray[36].callGroovyObjectGetProperty(this))) && sanitize) {
                Object object = callSiteArray[37].call(StackTraceUtils.class, cause);
                cause = (Throwable)ScriptBytecodeAdapter.castToType(object, Throwable.class);
            }
            Object trace = callSiteArray[38].callGetProperty(cause);
            Object buff = callSiteArray[39].callConstructor(StringBuilder.class);
            boolean doBreak = false;
            Object e = null;
            Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[40].call(trace), Iterator.class);
            while (iterator.hasNext()) {
                e = iterator.next();
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[41].callGetProperty(e), callSiteArray[42].callGetProperty(Interpreter.class)) && ScriptBytecodeAdapter.compareEqual(callSiteArray[43].callGetProperty(e), "run")) {
                    boolean bl;
                    if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[44].callGetProperty(callSiteArray[45].callGroovyObjectGetProperty(this)), callSiteArray[46].callGetProperty(IO.Verbosity.class)) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[47].callGetProperty(callSiteArray[48].callGroovyObjectGetProperty(this)), callSiteArray[49].callGetProperty(IO.Verbosity.class))) break;
                    doBreak = bl = true;
                }
                callSiteArray[50].call(buff, new GStringImpl(new Object[]{callSiteArray[51].callGetProperty(e), callSiteArray[52].callGetProperty(e)}, new String[]{"        @|bold at|@ ", ".", " (@|bold "}));
                callSiteArray[53].call(buff, DefaultTypeTransformation.booleanUnbox(callSiteArray[54].callGetProperty(e)) ? "Native Method" : (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[55].callGetProperty(e), null) && ScriptBytecodeAdapter.compareNotEqual(callSiteArray[56].callGetProperty(e), -1) ? new GStringImpl(new Object[]{callSiteArray[57].callGetProperty(e), callSiteArray[58].callGetProperty(e)}, new String[]{"", ":", ""}) : (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[59].callGetProperty(e), null) ? callSiteArray[60].callGetProperty(e) : "Unknown Source")));
                callSiteArray[61].call(buff, "|@)");
                callSiteArray[62].call(callSiteArray[63].callGetProperty(callSiteArray[64].callGroovyObjectGetProperty(this)), buff);
                callSiteArray[65].call(buff, 0);
                if (!doBreak) continue;
                callSiteArray[66].call(callSiteArray[67].callGetProperty(callSiteArray[68].callGroovyObjectGetProperty(this)), "        @|bold ...|@");
                break;
            }
            return null;
        }

        public Object call(Throwable cause) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            return callSiteArray[69].callCurrent((GroovyObject)this, cause);
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
            stringArray[0] = "debug";
            stringArray[1] = "log";
            stringArray[2] = "println";
            stringArray[3] = "err";
            stringArray[4] = "io";
            stringArray[5] = "name";
            stringArray[6] = "getClass";
            stringArray[7] = "<$constructor$>";
            stringArray[8] = "<$constructor$>";
            stringArray[9] = "getErrorCollector";
            stringArray[10] = "iterator";
            stringArray[11] = "getErrors";
            stringArray[12] = "hasNext";
            stringArray[13] = "next";
            stringArray[14] = "write";
            stringArray[15] = "hasNext";
            stringArray[16] = "println";
            stringArray[17] = "println";
            stringArray[18] = "err";
            stringArray[19] = "io";
            stringArray[20] = "toString";
            stringArray[21] = "println";
            stringArray[22] = "err";
            stringArray[23] = "io";
            stringArray[24] = "message";
            stringArray[25] = "maybeRecordError";
            stringArray[26] = "debug";
            stringArray[27] = "log";
            stringArray[28] = "debug";
            stringArray[29] = "log";
            stringArray[30] = "getPreference";
            stringArray[31] = "SANITIZE_PREFERENCE_KEY";
            stringArray[32] = "verbose";
            stringArray[33] = "io";
            stringArray[34] = "deepSanitize";
            stringArray[35] = "verbose";
            stringArray[36] = "io";
            stringArray[37] = "deepSanitize";
            stringArray[38] = "stackTrace";
            stringArray[39] = "<$constructor$>";
            stringArray[40] = "iterator";
            stringArray[41] = "className";
            stringArray[42] = "SCRIPT_FILENAME";
            stringArray[43] = "methodName";
            stringArray[44] = "verbosity";
            stringArray[45] = "io";
            stringArray[46] = "DEBUG";
            stringArray[47] = "verbosity";
            stringArray[48] = "io";
            stringArray[49] = "VERBOSE";
            stringArray[50] = "leftShift";
            stringArray[51] = "className";
            stringArray[52] = "methodName";
            stringArray[53] = "leftShift";
            stringArray[54] = "nativeMethod";
            stringArray[55] = "fileName";
            stringArray[56] = "lineNumber";
            stringArray[57] = "fileName";
            stringArray[58] = "lineNumber";
            stringArray[59] = "fileName";
            stringArray[60] = "fileName";
            stringArray[61] = "leftShift";
            stringArray[62] = "println";
            stringArray[63] = "err";
            stringArray[64] = "io";
            stringArray[65] = "setLength";
            stringArray[66] = "println";
            stringArray[67] = "err";
            stringArray[68] = "io";
            stringArray[69] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[70];
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
}


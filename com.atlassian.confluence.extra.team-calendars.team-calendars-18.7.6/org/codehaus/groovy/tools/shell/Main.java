/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.TerminalFactory
 *  jline.UnixTerminal
 *  jline.UnsupportedTerminal
 *  jline.WindowsTerminal
 *  org.fusesource.jansi.Ansi
 *  org.fusesource.jansi.AnsiConsole
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.CliBuilder;
import groovy.util.OptionAccessor;
import java.lang.ref.SoftReference;
import java.util.List;
import jline.TerminalFactory;
import jline.UnixTerminal;
import jline.UnsupportedTerminal;
import jline.WindowsTerminal;
import org.apache.groovy.util.SystemUtil;
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
import org.codehaus.groovy.tools.shell.AnsiDetector;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.util.HelpFormatter;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.tools.shell.util.MessageSource;
import org.codehaus.groovy.tools.shell.util.NoExitSecurityManager;
import org.fusesource.jansi.Ansi;
import org.fusesource.jansi.AnsiConsole;

public class Main
implements GroovyObject {
    private final Groovysh groovysh;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Main(IO io) {
        MetaClass metaClass;
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        IO iO = io;
        ScriptBytecodeAdapter.setProperty(iO, null, Logger.class, "io");
        Object object = callSiteArray[0].callConstructor(Groovysh.class, io);
        this.groovysh = (Groovysh)ScriptBytecodeAdapter.castToType(object, Groovysh.class);
    }

    public Groovysh getGroovysh() {
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        return this.groovysh;
    }

    public static void main(String ... args) {
        OptionAccessor options;
        CallSite[] callSiteArray;
        block16: {
            callSiteArray = Main.$getCallSiteArray();
            CliBuilder cli = (CliBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(CliBuilder.class, ScriptBytecodeAdapter.createMap(new Object[]{"usage", "groovysh [options] [...]", "formatter", callSiteArray[2].callConstructor(HelpFormatter.class), "stopAtNonOption", false})), CliBuilder.class);
            Reference<MessageSource> messages = new Reference<MessageSource>((MessageSource)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(MessageSource.class, Main.class), MessageSource.class));
            public class _main_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference messages;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _main_closure1(Object _outerInstance, Object _thisObject, Reference messages) {
                    Reference reference;
                    CallSite[] callSiteArray = _main_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.messages = reference = messages;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _main_closure1.$getCallSiteArray();
                    callSiteArray[0].callCurrent((GroovyObject)this, callSiteArray[1].call(this.messages.get(), "cli.option.classpath.description"));
                    callSiteArray[2].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "classpath"}), callSiteArray[3].call(this.messages.get(), "cli.option.cp.description"));
                    callSiteArray[4].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "help"}), callSiteArray[5].call(this.messages.get(), "cli.option.help.description"));
                    callSiteArray[6].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "version"}), callSiteArray[7].call(this.messages.get(), "cli.option.version.description"));
                    callSiteArray[8].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "verbose"}), callSiteArray[9].call(this.messages.get(), "cli.option.verbose.description"));
                    callSiteArray[10].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "quiet"}), callSiteArray[11].call(this.messages.get(), "cli.option.quiet.description"));
                    callSiteArray[12].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "debug"}), callSiteArray[13].call(this.messages.get(), "cli.option.debug.description"));
                    callSiteArray[14].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "evaluate", "args", 1, "argName", "CODE", "optionalArg", false}), callSiteArray[15].call(this.messages.get(), "cli.option.evaluate.description"));
                    callSiteArray[16].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "color", "args", 1, "argName", "FLAG", "optionalArg", true}), callSiteArray[17].call(this.messages.get(), "cli.option.color.description"));
                    callSiteArray[18].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "define", "args", 2, "argName", "name=value", "valueSeparator", "="}), callSiteArray[19].call(this.messages.get(), "cli.option.define.description"));
                    return callSiteArray[20].callCurrent(this, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "terminal", "args", 1, "argName", "TYPE"}), callSiteArray[21].call(this.messages.get(), "cli.option.terminal.description"));
                }

                public MessageSource getMessages() {
                    CallSite[] callSiteArray = _main_closure1.$getCallSiteArray();
                    return (MessageSource)ScriptBytecodeAdapter.castToType(this.messages.get(), MessageSource.class);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _main_closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _main_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "classpath";
                    stringArray[1] = "getAt";
                    stringArray[2] = "cp";
                    stringArray[3] = "getAt";
                    stringArray[4] = "h";
                    stringArray[5] = "getAt";
                    stringArray[6] = "V";
                    stringArray[7] = "getAt";
                    stringArray[8] = "v";
                    stringArray[9] = "getAt";
                    stringArray[10] = "q";
                    stringArray[11] = "getAt";
                    stringArray[12] = "d";
                    stringArray[13] = "getAt";
                    stringArray[14] = "e";
                    stringArray[15] = "getAt";
                    stringArray[16] = "C";
                    stringArray[17] = "getAt";
                    stringArray[18] = "D";
                    stringArray[19] = "getAt";
                    stringArray[20] = "T";
                    stringArray[21] = "getAt";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[22];
                    _main_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_main_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _main_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[4].call((Object)cli, new _main_closure1(Main.class, Main.class, messages));
            options = (OptionAccessor)ScriptBytecodeAdapter.castToType(callSiteArray[5].call((Object)cli, (Object)args), OptionAccessor.class);
            if (ScriptBytecodeAdapter.compareEqual(options, null)) {
                callSiteArray[6].call(System.class, 22);
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[7].callGroovyObjectGetProperty(options))) {
                callSiteArray[8].call(cli);
                callSiteArray[9].call(System.class, 0);
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].callGroovyObjectGetProperty(options))) {
                callSiteArray[11].call(callSiteArray[12].callGetProperty(System.class), callSiteArray[13].call(messages.get(), "cli.info.version", callSiteArray[14].callGetProperty(GroovySystem.class)));
                callSiteArray[15].call(System.class, 0);
            }
            boolean suppressColor = false;
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[16].call((Object)options, "C"))) {
                Object value = callSiteArray[17].call((Object)options, "C");
                if (ScriptBytecodeAdapter.compareNotEqual(value, null)) {
                    boolean bl;
                    suppressColor = bl = !DefaultTypeTransformation.booleanUnbox(callSiteArray[18].call(callSiteArray[19].call(Boolean.class, value)));
                }
            }
            String type = ShortTypeHandling.castToString(callSiteArray[20].callGetProperty(TerminalFactory.class));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call((Object)options, "T"))) {
                Object object = callSiteArray[22].call((Object)options, "T");
                type = ShortTypeHandling.castToString(object);
            }
            try {
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    callSiteArray[23].callStatic(Main.class, type, suppressColor);
                    break block16;
                }
                Main.setTerminalType(type, suppressColor);
            }
            catch (IllegalArgumentException e) {
                callSiteArray[24].call(callSiteArray[25].callGetProperty(System.class), callSiteArray[26].call(e));
                callSiteArray[27].call(cli);
                callSiteArray[28].call(System.class, 22);
            }
        }
        IO io = (IO)ScriptBytecodeAdapter.castToType(callSiteArray[29].callConstructor(IO.class), IO.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[30].call((Object)options, "D"))) {
            public class _main_closure2
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _main_closure2(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object k, Object v) {
                    CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                    return callSiteArray[0].call(System.class, k, v);
                }

                public Object call(Object k, Object v) {
                    CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                    return callSiteArray[1].callCurrent(this, k, v);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _main_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "setProperty";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _main_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_main_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _main_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[31].callSafe(callSiteArray[32].call((Object)options, "D"), new _main_closure2(Main.class, Main.class));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[33].callGroovyObjectGetProperty(options))) {
            Object object = callSiteArray[34].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object, null, io, "verbosity");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[35].callGroovyObjectGetProperty(options))) {
            Object object = callSiteArray[36].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object, null, io, "verbosity");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[37].callGroovyObjectGetProperty(options))) {
            Object object = callSiteArray[38].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object, null, io, "verbosity");
        }
        String evalString = null;
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[39].callGroovyObjectGetProperty(options))) {
            Object object = callSiteArray[40].call((Object)options, "e");
            evalString = ShortTypeHandling.castToString(object);
        }
        List filenames = (List)ScriptBytecodeAdapter.castToType(callSiteArray[41].call(options), List.class);
        Main main = (Main)ScriptBytecodeAdapter.castToType(callSiteArray[42].callConstructor(Main.class, io), Main.class);
        callSiteArray[43].call(main, evalString, filenames);
    }

    protected void startGroovysh(String evalString, List<String> filenames) {
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        Reference<Integer> code = new Reference<Integer>(0);
        code.get();
        Reference<Object> shell = new Reference<Object>(null);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[44].callCurrent(this);
            shell.set(((Groovysh)ScriptBytecodeAdapter.castToType(object, Groovysh.class)));
        } else {
            Groovysh groovysh = this.getGroovysh();
            shell.set(groovysh);
        }
        public class _startGroovysh_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference code;
            private /* synthetic */ Reference shell;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _startGroovysh_closure3(Object _outerInstance, Object _thisObject, Reference code, Reference shell) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _startGroovysh_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.code = reference2 = code;
                this.shell = reference = shell;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _startGroovysh_closure3.$getCallSiteArray();
                if (ScriptBytecodeAdapter.compareEqual(this.code.get(), null)) {
                    callSiteArray[0].callCurrent((GroovyObject)this, "WARNING: Abnormal JVM shutdown detected");
                }
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].callGetProperty(this.shell.get()))) {
                    return callSiteArray[2].call(callSiteArray[3].callGetProperty(this.shell.get()));
                }
                return null;
            }

            public Integer getCode() {
                CallSite[] callSiteArray = _startGroovysh_closure3.$getCallSiteArray();
                return (Integer)ScriptBytecodeAdapter.castToType(this.code.get(), Integer.class);
            }

            public Groovysh getShell() {
                CallSite[] callSiteArray = _startGroovysh_closure3.$getCallSiteArray();
                return (Groovysh)ScriptBytecodeAdapter.castToType(this.shell.get(), Groovysh.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _startGroovysh_closure3.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _startGroovysh_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "println";
                stringArray[1] = "history";
                stringArray[2] = "flush";
                stringArray[3] = "history";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[4];
                _startGroovysh_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_startGroovysh_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _startGroovysh_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[45].callCurrent((GroovyObject)this, new _startGroovysh_closure3(this, this, code, shell));
        SecurityManager psm = (SecurityManager)ScriptBytecodeAdapter.castToType(callSiteArray[46].call(System.class), SecurityManager.class);
        callSiteArray[47].call(System.class, callSiteArray[48].callConstructor(NoExitSecurityManager.class));
        try {
            Object object = callSiteArray[49].call(shell.get(), evalString, filenames);
            code.set((Integer)ScriptBytecodeAdapter.castToType(object, Integer.class));
        }
        catch (Throwable throwable) {
            callSiteArray[51].call(System.class, psm);
            throw throwable;
        }
        callSiteArray[50].call(System.class, psm);
        callSiteArray[52].call(System.class, code.get());
    }

    public static void setTerminalType(String type, boolean suppressColor) {
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = type;
            valueRecorder.record(string, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(string, null);
            valueRecorder.record(bl, 13);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert type != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Object object = callSiteArray[53].call(type);
        type = ShortTypeHandling.castToString(object);
        boolean enableAnsi = true;
        String string = type;
        if (ScriptBytecodeAdapter.isCase(string, callSiteArray[54].callGetProperty(TerminalFactory.class))) {
            Object var7_7 = null;
            type = ShortTypeHandling.castToString(var7_7);
        } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[55].callGetProperty(TerminalFactory.class))) {
            Object object2 = callSiteArray[56].callGetProperty(UnixTerminal.class);
            type = ShortTypeHandling.castToString(object2);
        } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[57].callGetProperty(TerminalFactory.class)) || ScriptBytecodeAdapter.isCase(string, callSiteArray[58].callGetProperty(TerminalFactory.class))) {
            Object object3 = callSiteArray[59].callGetProperty(WindowsTerminal.class);
            type = ShortTypeHandling.castToString(object3);
        } else if (ScriptBytecodeAdapter.isCase(string, callSiteArray[60].callGetProperty(TerminalFactory.class)) || ScriptBytecodeAdapter.isCase(string, callSiteArray[61].callGetProperty(TerminalFactory.class)) || ScriptBytecodeAdapter.isCase(string, callSiteArray[62].callGetProperty(TerminalFactory.class))) {
            boolean bl;
            Object object4 = callSiteArray[63].callGetProperty(UnsupportedTerminal.class);
            type = ShortTypeHandling.castToString(object4);
            enableAnsi = bl = false;
        } else {
            throw (Throwable)callSiteArray[64].callConstructor(IllegalArgumentException.class, new GStringImpl(new Object[]{type}, new String[]{"Invalid Terminal type: ", ""}));
        }
        if (enableAnsi) {
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[65].callStatic(Main.class);
            } else {
                Main.installAnsi();
            }
            boolean bl = !suppressColor;
            ScriptBytecodeAdapter.setProperty(bl, null, Ansi.class, "enabled");
        } else {
            boolean bl = false;
            ScriptBytecodeAdapter.setProperty(bl, null, Ansi.class, "enabled");
        }
        if (ScriptBytecodeAdapter.compareNotEqual(type, null)) {
            callSiteArray[66].call(System.class, callSiteArray[67].callGetProperty(TerminalFactory.class), type);
        }
    }

    public static void installAnsi() {
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        callSiteArray[68].call(AnsiConsole.class);
        callSiteArray[69].call(Ansi.class, callSiteArray[70].callConstructor(AnsiDetector.class));
    }

    @Deprecated
    public static void setSystemProperty(String nameValue) {
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        callSiteArray[71].callStatic(SystemUtil.class, nameValue);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != Main.class) {
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
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "with";
        stringArray[5] = "parse";
        stringArray[6] = "exit";
        stringArray[7] = "h";
        stringArray[8] = "usage";
        stringArray[9] = "exit";
        stringArray[10] = "V";
        stringArray[11] = "println";
        stringArray[12] = "out";
        stringArray[13] = "format";
        stringArray[14] = "version";
        stringArray[15] = "exit";
        stringArray[16] = "hasOption";
        stringArray[17] = "getOptionValue";
        stringArray[18] = "booleanValue";
        stringArray[19] = "valueOf";
        stringArray[20] = "AUTO";
        stringArray[21] = "hasOption";
        stringArray[22] = "getOptionValue";
        stringArray[23] = "setTerminalType";
        stringArray[24] = "println";
        stringArray[25] = "err";
        stringArray[26] = "getMessage";
        stringArray[27] = "usage";
        stringArray[28] = "exit";
        stringArray[29] = "<$constructor$>";
        stringArray[30] = "hasOption";
        stringArray[31] = "each";
        stringArray[32] = "getOptionProperties";
        stringArray[33] = "v";
        stringArray[34] = "VERBOSE";
        stringArray[35] = "d";
        stringArray[36] = "DEBUG";
        stringArray[37] = "q";
        stringArray[38] = "QUIET";
        stringArray[39] = "e";
        stringArray[40] = "getOptionValue";
        stringArray[41] = "arguments";
        stringArray[42] = "<$constructor$>";
        stringArray[43] = "startGroovysh";
        stringArray[44] = "getGroovysh";
        stringArray[45] = "addShutdownHook";
        stringArray[46] = "getSecurityManager";
        stringArray[47] = "setSecurityManager";
        stringArray[48] = "<$constructor$>";
        stringArray[49] = "run";
        stringArray[50] = "setSecurityManager";
        stringArray[51] = "setSecurityManager";
        stringArray[52] = "exit";
        stringArray[53] = "toLowerCase";
        stringArray[54] = "AUTO";
        stringArray[55] = "UNIX";
        stringArray[56] = "canonicalName";
        stringArray[57] = "WIN";
        stringArray[58] = "WINDOWS";
        stringArray[59] = "canonicalName";
        stringArray[60] = "FALSE";
        stringArray[61] = "OFF";
        stringArray[62] = "NONE";
        stringArray[63] = "canonicalName";
        stringArray[64] = "<$constructor$>";
        stringArray[65] = "installAnsi";
        stringArray[66] = "setProperty";
        stringArray[67] = "JLINE_TERMINAL";
        stringArray[68] = "systemInstall";
        stringArray[69] = "setDetector";
        stringArray[70] = "<$constructor$>";
        stringArray[71] = "setSystemPropertyFrom";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[72];
        Main.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(Main.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = Main.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


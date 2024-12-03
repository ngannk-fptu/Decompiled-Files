/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.ivy.util.DefaultMessageLogger
 *  org.apache.ivy.util.Message
 */
package org.codehaus.groovy.tools;

import groovy.grape.Grape;
import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.lang.Script;
import groovyjarjarcommonscli.CommandLine;
import groovyjarjarcommonscli.GroovyInternalPosixParser;
import groovyjarjarcommonscli.HelpFormatter;
import groovyjarjarcommonscli.OptionBuilder;
import groovyjarjarcommonscli.OptionGroup;
import groovyjarjarcommonscli.Options;
import java.io.File;
import java.io.PrintWriter;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.ivy.util.DefaultMessageLogger;
import org.apache.ivy.util.Message;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class GrapeMain
extends Script {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    Object install;
    Object uninstall;
    Object list;
    Object resolve;
    Object help;
    Object commands;
    Object grapeHelp;
    Object setupLogging;
    Options options;
    CommandLine cmd;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GrapeMain() {
        CallSite[] callSiteArray = GrapeMain.$getCallSiteArray();
        _closure1 _closure110 = new _closure1(this, this);
        this.install = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.uninstall = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.list = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.resolve = _closure44;
        _closure5 _closure52 = new _closure5(this, this);
        this.help = _closure52;
        Map map = ScriptBytecodeAdapter.createMap(new Object[]{"install", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.install, "shortHelp", "Installs a particular grape"}), "uninstall", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.uninstall, "shortHelp", "Uninstalls a particular grape (non-transitively removes the respective jar file from the grape cache)"}), "list", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.list, "shortHelp", "Lists all installed grapes"}), "resolve", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.resolve, "shortHelp", "Enumerates the jars used by a grape"}), "help", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.help, "shortHelp", "Usage information"})});
        this.commands = map;
        _closure6 _closure62 = new _closure6(this, this);
        this.grapeHelp = _closure62;
        _closure7 _closure72 = new _closure7(this, this);
        this.setupLogging = _closure72;
        Object object = callSiteArray[0].callConstructor(Options.class);
        this.options = (Options)ScriptBytecodeAdapter.castToType(object, Options.class);
        Object var11_11 = null;
        this.cmd = var11_11;
    }

    public GrapeMain(Binding context) {
        CallSite[] callSiteArray = GrapeMain.$getCallSiteArray();
        super(context);
        _closure1 _closure110 = new _closure1(this, this);
        this.install = _closure110;
        _closure2 _closure210 = new _closure2(this, this);
        this.uninstall = _closure210;
        _closure3 _closure310 = new _closure3(this, this);
        this.list = _closure310;
        _closure4 _closure44 = new _closure4(this, this);
        this.resolve = _closure44;
        _closure5 _closure52 = new _closure5(this, this);
        this.help = _closure52;
        Map map = ScriptBytecodeAdapter.createMap(new Object[]{"install", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.install, "shortHelp", "Installs a particular grape"}), "uninstall", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.uninstall, "shortHelp", "Uninstalls a particular grape (non-transitively removes the respective jar file from the grape cache)"}), "list", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.list, "shortHelp", "Lists all installed grapes"}), "resolve", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.resolve, "shortHelp", "Enumerates the jars used by a grape"}), "help", ScriptBytecodeAdapter.createMap(new Object[]{"closure", this.help, "shortHelp", "Usage information"})});
        this.commands = map;
        _closure6 _closure62 = new _closure6(this, this);
        this.grapeHelp = _closure62;
        _closure7 _closure72 = new _closure7(this, this);
        this.setupLogging = _closure72;
        Object object = callSiteArray[1].callConstructor(Options.class);
        this.options = (Options)ScriptBytecodeAdapter.castToType(object, Options.class);
        Object var12_12 = null;
        this.cmd = var12_12;
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = GrapeMain.$getCallSiteArray();
        callSiteArray[2].call(InvokerHelper.class, GrapeMain.class, args);
    }

    @Override
    public Object run() {
        CallSite[] callSiteArray = GrapeMain.$getCallSiteArray();
        callSiteArray[3].call((Object)this.options, callSiteArray[4].call(callSiteArray[5].call(callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].call(callSiteArray[9].call(OptionBuilder.class, "define"), "define a system property"), 2)), "name=value"), "D"));
        callSiteArray[10].call((Object)this.options, callSiteArray[11].call(callSiteArray[12].call(callSiteArray[13].call(callSiteArray[14].call(callSiteArray[15].call(OptionBuilder.class, "resolver"), "define a grab resolver (for install)"), true), "url"), "r"));
        callSiteArray[16].call((Object)this.options, callSiteArray[17].call(callSiteArray[18].call(callSiteArray[19].call(callSiteArray[20].call(OptionBuilder.class, false), "usage information"), "help"), "h"));
        callSiteArray[21].call((Object)this.options, callSiteArray[22].call(callSiteArray[23].call(callSiteArray[24].call(callSiteArray[25].call(callSiteArray[26].call(callSiteArray[27].callConstructor(OptionGroup.class), callSiteArray[28].call(callSiteArray[29].call(callSiteArray[30].call(callSiteArray[31].call(OptionBuilder.class, false), "Log level 0 - only errors"), "quiet"), "q")), callSiteArray[32].call(callSiteArray[33].call(callSiteArray[34].call(callSiteArray[35].call(OptionBuilder.class, false), "Log level 1 - errors and warnings"), "warn"), "w")), callSiteArray[36].call(callSiteArray[37].call(callSiteArray[38].call(callSiteArray[39].call(OptionBuilder.class, false), "Log level 2 - info"), "info"), "i")), callSiteArray[40].call(callSiteArray[41].call(callSiteArray[42].call(callSiteArray[43].call(OptionBuilder.class, false), "Log level 3 - verbose"), "verbose"), "V")), callSiteArray[44].call(callSiteArray[45].call(callSiteArray[46].call(callSiteArray[47].call(OptionBuilder.class, false), "Log level 4 - debug"), "debug"), "d")));
        callSiteArray[48].call((Object)this.options, callSiteArray[49].call(callSiteArray[50].call(callSiteArray[51].call(callSiteArray[52].call(OptionBuilder.class, false), "display the Groovy and JVM versions"), "version"), "v"));
        Object object = callSiteArray[53].call(callSiteArray[54].callConstructor(GroovyInternalPosixParser.class), this.options, callSiteArray[55].callGroovyObjectGetProperty(this), true);
        this.cmd = (CommandLine)ScriptBytecodeAdapter.castToType(object, CommandLine.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[56].call((Object)this.cmd, "h"))) {
            callSiteArray[57].call(this.grapeHelp);
            return null;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[58].call((Object)this.cmd, "v"))) {
            String version = ShortTypeHandling.castToString(callSiteArray[59].call(GroovySystem.class));
            callSiteArray[60].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{version, callSiteArray[61].call(System.class, "java.version")}, new String[]{"Groovy Version: ", " JVM: ", ""}));
            return null;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[62].call((Object)this.options, "D"))) {
            public class _run_closure8
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _run_closure8(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _run_closure8.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object k, Object v) {
                    CallSite[] callSiteArray = _run_closure8.$getCallSiteArray();
                    return callSiteArray[0].call(System.class, k, v);
                }

                public Object call(Object k, Object v) {
                    CallSite[] callSiteArray = _run_closure8.$getCallSiteArray();
                    return callSiteArray[1].callCurrent(this, k, v);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _run_closure8.class) {
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
                    _run_closure8.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_run_closure8.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _run_closure8.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[63].callSafe(callSiteArray[64].call((Object)this.cmd, "D"), new _run_closure8(this, this));
        }
        Object[] arg = (String[])ScriptBytecodeAdapter.castToType(callSiteArray[65].callGetProperty(this.cmd), String[].class);
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[66].callGetPropertySafe(arg), 0)) {
                return callSiteArray[67].call(this.grapeHelp);
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[68].call(this.commands, callSiteArray[69].call((Object)arg, 0)))) {
                return callSiteArray[70].call(callSiteArray[71].call(this.commands, callSiteArray[72].call((Object)arg, 0)), arg, this.cmd);
            }
            return callSiteArray[73].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[74].call((Object)arg, 0)}, new String[]{"grape: '", "' is not a grape command. See 'grape --help'"}));
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[75].callGetPropertySafe(arg), 0)) {
            return callSiteArray[76].call(this.grapeHelp);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[77].call(this.commands, BytecodeInterface8.objectArrayGet(arg, 0)))) {
            return callSiteArray[78].call(callSiteArray[79].call(this.commands, BytecodeInterface8.objectArrayGet(arg, 0)), arg, this.cmd);
        }
        return callSiteArray[80].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{BytecodeInterface8.objectArrayGet(arg, 0)}, new String[]{"grape: '", "' is not a grape command. See 'grape --help'"}));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GrapeMain.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "runScript";
        stringArray[3] = "addOption";
        stringArray[4] = "create";
        stringArray[5] = "withArgName";
        stringArray[6] = "withValueSeparator";
        stringArray[7] = "hasArgs";
        stringArray[8] = "withDescription";
        stringArray[9] = "withLongOpt";
        stringArray[10] = "addOption";
        stringArray[11] = "create";
        stringArray[12] = "withArgName";
        stringArray[13] = "hasArg";
        stringArray[14] = "withDescription";
        stringArray[15] = "withLongOpt";
        stringArray[16] = "addOption";
        stringArray[17] = "create";
        stringArray[18] = "withLongOpt";
        stringArray[19] = "withDescription";
        stringArray[20] = "hasArg";
        stringArray[21] = "addOptionGroup";
        stringArray[22] = "addOption";
        stringArray[23] = "addOption";
        stringArray[24] = "addOption";
        stringArray[25] = "addOption";
        stringArray[26] = "addOption";
        stringArray[27] = "<$constructor$>";
        stringArray[28] = "create";
        stringArray[29] = "withLongOpt";
        stringArray[30] = "withDescription";
        stringArray[31] = "hasArg";
        stringArray[32] = "create";
        stringArray[33] = "withLongOpt";
        stringArray[34] = "withDescription";
        stringArray[35] = "hasArg";
        stringArray[36] = "create";
        stringArray[37] = "withLongOpt";
        stringArray[38] = "withDescription";
        stringArray[39] = "hasArg";
        stringArray[40] = "create";
        stringArray[41] = "withLongOpt";
        stringArray[42] = "withDescription";
        stringArray[43] = "hasArg";
        stringArray[44] = "create";
        stringArray[45] = "withLongOpt";
        stringArray[46] = "withDescription";
        stringArray[47] = "hasArg";
        stringArray[48] = "addOption";
        stringArray[49] = "create";
        stringArray[50] = "withLongOpt";
        stringArray[51] = "withDescription";
        stringArray[52] = "hasArg";
        stringArray[53] = "parse";
        stringArray[54] = "<$constructor$>";
        stringArray[55] = "args";
        stringArray[56] = "hasOption";
        stringArray[57] = "call";
        stringArray[58] = "hasOption";
        stringArray[59] = "getVersion";
        stringArray[60] = "println";
        stringArray[61] = "getProperty";
        stringArray[62] = "hasOption";
        stringArray[63] = "each";
        stringArray[64] = "getOptionProperties";
        stringArray[65] = "args";
        stringArray[66] = "length";
        stringArray[67] = "call";
        stringArray[68] = "containsKey";
        stringArray[69] = "getAt";
        stringArray[70] = "closure";
        stringArray[71] = "getAt";
        stringArray[72] = "getAt";
        stringArray[73] = "println";
        stringArray[74] = "getAt";
        stringArray[75] = "length";
        stringArray[76] = "call";
        stringArray[77] = "containsKey";
        stringArray[78] = "closure";
        stringArray[79] = "getAt";
        stringArray[80] = "println";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[81];
        GrapeMain.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GrapeMain.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GrapeMain.$createCallSiteArray();
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

        public Object doCall(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[0].call(arg), 5) || ScriptBytecodeAdapter.compareLessThan(callSiteArray[1].call(arg), 3)) {
                    callSiteArray[2].callCurrent((GroovyObject)this, "install requires two to four arguments: <group> <module> [<version> [<classifier>]]");
                    return null;
                }
            } else if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[3].call(arg), 5) || ScriptBytecodeAdapter.compareLessThan(callSiteArray[4].call(arg), 3)) {
                callSiteArray[5].callCurrent((GroovyObject)this, "install requires two to four arguments: <group> <module> [<version> [<classifier>]]");
                return null;
            }
            Object ver = "*";
            if (ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[6].call(arg), 4)) {
                Object object;
                ver = object = callSiteArray[7].call(arg, 3);
            }
            Object classifier = null;
            if (ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[8].call(arg), 5)) {
                Object object;
                classifier = object = callSiteArray[9].call(arg, 4);
            }
            callSiteArray[10].call(Grape.class);
            callSiteArray[11].callCurrent(this);
            public class _closure9
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure9(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(String url) {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    return callSiteArray[0].call(Grape.class, ScriptBytecodeAdapter.createMap(new Object[]{"name", url, "root", url}));
                }

                public Object call(String url) {
                    CallSite[] callSiteArray = _closure9.$getCallSiteArray();
                    if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                        return callSiteArray[1].callCurrent((GroovyObject)this, url);
                    }
                    return this.doCall(url);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure9.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "addResolver";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _closure9.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure9.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure9.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[12].callSafe(callSiteArray[13].call(cmd, "r"), new _closure9(this, this.getThisObject()));
            Object object = callSiteArray[14].call(Grape.class, ScriptBytecodeAdapter.createMap(new Object[]{"autoDownload", true, "group", callSiteArray[15].call(arg, 1), "module", callSiteArray[16].call(arg, 2), "version", ver, "classifier", classifier, "noExceptions", true}));
            try {
                return object;
            }
            catch (Exception e) {
                Object object2 = callSiteArray[17].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[18].callGroovyObjectGetProperty(this)}, new String[]{"An error occured : ", ""}));
                return object2;
            }
        }

        public Object call(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure1.$getCallSiteArray();
            return callSiteArray[19].callCurrent(this, arg, cmd);
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
            stringArray[0] = "size";
            stringArray[1] = "size";
            stringArray[2] = "println";
            stringArray[3] = "size";
            stringArray[4] = "size";
            stringArray[5] = "println";
            stringArray[6] = "size";
            stringArray[7] = "getAt";
            stringArray[8] = "size";
            stringArray[9] = "getAt";
            stringArray[10] = "getInstance";
            stringArray[11] = "setupLogging";
            stringArray[12] = "each";
            stringArray[13] = "getOptionValues";
            stringArray[14] = "grab";
            stringArray[15] = "getAt";
            stringArray[16] = "getAt";
            stringArray[17] = "println";
            stringArray[18] = "ex";
            stringArray[19] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[20];
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

        public Object doCall(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[0].call(arg), 4)) {
                callSiteArray[1].callCurrent((GroovyObject)this, "uninstall requires three arguments: <group> <module> <version>");
                return null;
            }
            Reference<String> group = new Reference<String>(ShortTypeHandling.castToString(callSiteArray[2].call(arg, 1)));
            Reference<String> module = new Reference<String>(ShortTypeHandling.castToString(callSiteArray[3].call(arg, 2)));
            Reference<String> ver = new Reference<String>(ShortTypeHandling.castToString(callSiteArray[4].call(arg, 3)));
            callSiteArray[5].call(Grape.class);
            callSiteArray[6].callCurrent(this);
            public class _closure10
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference group;
                private /* synthetic */ Reference module;
                private /* synthetic */ Reference ver;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure10(Object _outerInstance, Object _thisObject, Reference group, Reference module, Reference ver) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.group = reference3 = group;
                    this.module = reference2 = module;
                    this.ver = reference = ver;
                }

                public Object doCall(String groupName, Map g) {
                    Reference<String> groupName2 = new Reference<String>(groupName);
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    public class _closure13
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference group;
                        private /* synthetic */ Reference groupName;
                        private /* synthetic */ Reference module;
                        private /* synthetic */ Reference ver;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure13(Object _outerInstance, Object _thisObject, Reference group, Reference groupName, Reference module, Reference ver) {
                            Reference reference;
                            Reference reference2;
                            Reference reference3;
                            Reference reference4;
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.group = reference4 = group;
                            this.groupName = reference3 = groupName;
                            this.module = reference2 = module;
                            this.ver = reference = ver;
                        }

                        public Object doCall(String moduleName, List<String> versions) {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                                return ScriptBytecodeAdapter.compareEqual(this.group.get(), this.groupName.get()) && ScriptBytecodeAdapter.compareEqual(this.module.get(), moduleName) && ScriptBytecodeAdapter.isCase(this.ver.get(), versions);
                            }
                            return ScriptBytecodeAdapter.compareEqual(this.group.get(), this.groupName.get()) && ScriptBytecodeAdapter.compareEqual(this.module.get(), moduleName) && ScriptBytecodeAdapter.isCase(this.ver.get(), versions);
                        }

                        public Object call(String moduleName, List<String> versions) {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return callSiteArray[0].callCurrent(this, moduleName, versions);
                        }

                        public String getGroup() {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return ShortTypeHandling.castToString(this.group.get());
                        }

                        public String getGroupName() {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return ShortTypeHandling.castToString(this.groupName.get());
                        }

                        public String getModule() {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return ShortTypeHandling.castToString(this.module.get());
                        }

                        public String getVer() {
                            CallSite[] callSiteArray = _closure13.$getCallSiteArray();
                            return ShortTypeHandling.castToString(this.ver.get());
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure13.class) {
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
                            return new CallSiteArray(_closure13.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure13.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[0].call((Object)g, new _closure13(this, this.getThisObject(), this.group, groupName2, this.module, this.ver));
                }

                public Object call(String groupName, Map g) {
                    Reference<String> groupName2 = new Reference<String>(groupName);
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return callSiteArray[1].callCurrent(this, groupName2.get(), g);
                }

                public String getGroup() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.group.get());
                }

                public String getModule() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.module.get());
                }

                public String getVer() {
                    CallSite[] callSiteArray = _closure10.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.ver.get());
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure10.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "any";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _closure10.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure10.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure10.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[7].call(callSiteArray[8].call(Grape.class), new _closure10(this, this.getThisObject(), group, module, ver)))) {
                callSiteArray[9].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{group.get(), module.get(), ver.get()}, new String[]{"uninstall did not find grape matching: ", " ", " ", ""}));
                public class _closure11
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference group;
                    private /* synthetic */ Reference module;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure11(Object _outerInstance, Object _thisObject, Reference group, Reference module) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.group = reference2 = group;
                        this.module = reference = module;
                    }

                    public Object doCall(String groupName, Map g) {
                        Reference<String> groupName2 = new Reference<String>(groupName);
                        CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                        public class _closure14
                        extends Closure
                        implements GeneratedClosure {
                            private /* synthetic */ Reference groupName;
                            private /* synthetic */ Reference group;
                            private /* synthetic */ Reference module;
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure14(Object _outerInstance, Object _thisObject, Reference groupName, Reference group, Reference module) {
                                Reference reference;
                                Reference reference2;
                                Reference reference3;
                                CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                                this.groupName = reference3 = groupName;
                                this.group = reference2 = group;
                                this.module = reference = module;
                            }

                            public Object doCall(String moduleName, List<String> versions) {
                                CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                return DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(this.groupName.get(), this.group.get())) || DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call((Object)moduleName, this.module.get())) || DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(this.group.get(), this.groupName.get())) || DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(this.module.get(), moduleName));
                            }

                            public Object call(String moduleName, List<String> versions) {
                                CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                return callSiteArray[4].callCurrent(this, moduleName, versions);
                            }

                            public String getGroupName() {
                                CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                return ShortTypeHandling.castToString(this.groupName.get());
                            }

                            public String getGroup() {
                                CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                return ShortTypeHandling.castToString(this.group.get());
                            }

                            public String getModule() {
                                CallSite[] callSiteArray = _closure14.$getCallSiteArray();
                                return ShortTypeHandling.castToString(this.module.get());
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure14.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "contains";
                                stringArray[1] = "contains";
                                stringArray[2] = "contains";
                                stringArray[3] = "contains";
                                stringArray[4] = "doCall";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[5];
                                _closure14.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure14.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure14.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        return callSiteArray[0].call((Object)g, new _closure14(this, this.getThisObject(), groupName2, this.group, this.module));
                    }

                    public Object call(String groupName, Map g) {
                        Reference<String> groupName2 = new Reference<String>(groupName);
                        CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                        return callSiteArray[1].callCurrent(this, groupName2.get(), g);
                    }

                    public String getGroup() {
                        CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.group.get());
                    }

                    public String getModule() {
                        CallSite[] callSiteArray = _closure11.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.module.get());
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure11.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "any";
                        stringArray[1] = "doCall";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _closure11.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure11.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure11.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                Object fuzzyMatches = callSiteArray[10].call(callSiteArray[11].call(Grape.class), new _closure11(this, this.getThisObject(), group, module));
                if (DefaultTypeTransformation.booleanUnbox(fuzzyMatches)) {
                    callSiteArray[12].callCurrent((GroovyObject)this, "possible matches:");
                    public class _closure12
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure12(Object _outerInstance, Object _thisObject) {
                            CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(String groupName, Map g) {
                            CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                            return callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{groupName, g}, new String[]{"    ", ": ", ""}));
                        }

                        public Object call(String groupName, Map g) {
                            CallSite[] callSiteArray = _closure12.$getCallSiteArray();
                            return callSiteArray[1].callCurrent(this, groupName, g);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure12.class) {
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
                            stringArray[1] = "doCall";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[2];
                            _closure12.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure12.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure12.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[13].call(fuzzyMatches, new _closure12(this, this.getThisObject()));
                }
                return null;
            }
            return callSiteArray[14].call(callSiteArray[15].callGetProperty(Grape.class), group.get(), module.get(), ver.get());
        }

        public Object call(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure2.$getCallSiteArray();
            return callSiteArray[16].callCurrent(this, arg, cmd);
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
            stringArray[0] = "size";
            stringArray[1] = "println";
            stringArray[2] = "getAt";
            stringArray[3] = "getAt";
            stringArray[4] = "getAt";
            stringArray[5] = "getInstance";
            stringArray[6] = "setupLogging";
            stringArray[7] = "find";
            stringArray[8] = "enumerateGrapes";
            stringArray[9] = "println";
            stringArray[10] = "findAll";
            stringArray[11] = "enumerateGrapes";
            stringArray[12] = "println";
            stringArray[13] = "each";
            stringArray[14] = "uninstallArtifact";
            stringArray[15] = "instance";
            stringArray[16] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[17];
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

        public Object doCall(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            callSiteArray[0].callCurrent((GroovyObject)this, "");
            Reference<Integer> moduleCount = new Reference<Integer>(0);
            Reference<Integer> versionCount = new Reference<Integer>(0);
            callSiteArray[1].call(Grape.class);
            callSiteArray[2].callCurrent(this);
            public class _closure15
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference moduleCount;
                private /* synthetic */ Reference versionCount;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure15(Object _outerInstance, Object _thisObject, Reference moduleCount, Reference versionCount) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.moduleCount = reference2 = moduleCount;
                    this.versionCount = reference = versionCount;
                }

                public Object doCall(String groupName, Map group) {
                    Reference<String> groupName2 = new Reference<String>(groupName);
                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                    public class _closure16
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference groupName;
                        private /* synthetic */ Reference moduleCount;
                        private /* synthetic */ Reference versionCount;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure16(Object _outerInstance, Object _thisObject, Reference groupName, Reference moduleCount, Reference versionCount) {
                            Reference reference;
                            Reference reference2;
                            Reference reference3;
                            CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.groupName = reference3 = groupName;
                            this.moduleCount = reference2 = moduleCount;
                            this.versionCount = reference = versionCount;
                        }

                        public Object doCall(String moduleName, List<String> versions) {
                            CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                            callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{this.groupName.get(), moduleName, versions}, new String[]{"", " ", "  ", ""}));
                            Object t = this.moduleCount.get();
                            this.moduleCount.set((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[1].call(t), Integer.class));
                            Object object = callSiteArray[2].call(this.versionCount.get(), callSiteArray[3].call(versions));
                            this.versionCount.set((Integer)ScriptBytecodeAdapter.castToType(object, Integer.class));
                            return object;
                        }

                        public Object call(String moduleName, List<String> versions) {
                            CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                            return callSiteArray[4].callCurrent(this, moduleName, versions);
                        }

                        public String getGroupName() {
                            CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                            return ShortTypeHandling.castToString(this.groupName.get());
                        }

                        public Integer getModuleCount() {
                            CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                            return (Integer)ScriptBytecodeAdapter.castToType(this.moduleCount.get(), Integer.class);
                        }

                        public Integer getVersionCount() {
                            CallSite[] callSiteArray = _closure16.$getCallSiteArray();
                            return (Integer)ScriptBytecodeAdapter.castToType(this.versionCount.get(), Integer.class);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure16.class) {
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
                            stringArray[1] = "next";
                            stringArray[2] = "plus";
                            stringArray[3] = "size";
                            stringArray[4] = "doCall";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[5];
                            _closure16.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure16.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure16.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[0].call((Object)group, new _closure16(this, this.getThisObject(), groupName2, this.moduleCount, this.versionCount));
                }

                public Object call(String groupName, Map group) {
                    Reference<String> groupName2 = new Reference<String>(groupName);
                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                    return callSiteArray[1].callCurrent(this, groupName2.get(), group);
                }

                public Integer getModuleCount() {
                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                    return (Integer)ScriptBytecodeAdapter.castToType(this.moduleCount.get(), Integer.class);
                }

                public Integer getVersionCount() {
                    CallSite[] callSiteArray = _closure15.$getCallSiteArray();
                    return (Integer)ScriptBytecodeAdapter.castToType(this.versionCount.get(), Integer.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure15.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "each";
                    stringArray[1] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[2];
                    _closure15.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure15.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure15.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[3].call(callSiteArray[4].call(Grape.class), new _closure15(this, this.getThisObject(), moduleCount, versionCount));
            callSiteArray[5].callCurrent((GroovyObject)this, "");
            callSiteArray[6].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{moduleCount.get()}, new String[]{"", " Grape modules cached"}));
            return callSiteArray[7].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{versionCount.get()}, new String[]{"", " Grape module versions cached"}));
        }

        public Object call(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure3.$getCallSiteArray();
            return callSiteArray[8].callCurrent(this, arg, cmd);
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
            stringArray[0] = "println";
            stringArray[1] = "getInstance";
            stringArray[2] = "setupLogging";
            stringArray[3] = "each";
            stringArray[4] = "enumerateGrapes";
            stringArray[5] = "println";
            stringArray[6] = "println";
            stringArray[7] = "println";
            stringArray[8] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[9];
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

        /*
         * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
         * Enabled aggressive block sorting
         * Enabled unnecessary exception pruning
         * Enabled aggressive exception aggregation
         */
        public Object doCall(Object arg, Object cmd) {
            Object object;
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            Options options = (Options)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(Options.class), Options.class);
            callSiteArray[1].call((Object)options, callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].call(OptionBuilder.class, false), "ant"), "a"));
            callSiteArray[5].call((Object)options, callSiteArray[6].call(callSiteArray[7].call(callSiteArray[8].call(OptionBuilder.class, false), "dos"), "d"));
            callSiteArray[9].call((Object)options, callSiteArray[10].call(callSiteArray[11].call(callSiteArray[12].call(OptionBuilder.class, false), "shell"), "s"));
            callSiteArray[13].call((Object)options, callSiteArray[14].call(callSiteArray[15].call(callSiteArray[16].call(OptionBuilder.class, false), "ivy"), "i"));
            CommandLine cmd2 = (CommandLine)ScriptBytecodeAdapter.castToType(callSiteArray[17].call(callSiteArray[18].callConstructor(GroovyInternalPosixParser.class), options, ScriptBytecodeAdapter.createPojoWrapper((String[])ScriptBytecodeAdapter.asType(callSiteArray[19].call(arg, ScriptBytecodeAdapter.createRange(1, -1, true)), String[].class), String[].class), true), CommandLine.class);
            arg = object = callSiteArray[20].callGetProperty(cmd2);
            callSiteArray[21].call(Grape.class);
            callSiteArray[22].callCurrent((GroovyObject)this, callSiteArray[23].callGetProperty(Message.class));
            if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[24].call(callSiteArray[25].call(arg), 3), 0)) {
                callSiteArray[26].callCurrent((GroovyObject)this, "There needs to be a multiple of three arguments: (group module version)+");
                return null;
            }
            if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[27].call(callSiteArray[28].callGroovyObjectGetProperty(this)), 3)) {
                callSiteArray[29].callCurrent((GroovyObject)this, "At least one Grape reference is required");
                return null;
            }
            String before = null;
            String between = null;
            String after = null;
            Boolean ivyFormatRequested = false;
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[30].call((Object)cmd2, "a"))) {
                String string;
                String string2;
                String string3;
                before = string3 = "<pathelement location=\"";
                between = string2 = "\">\n<pathelement location=\"";
                after = string = "\">";
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[31].call((Object)cmd2, "d"))) {
                String string;
                String string4;
                String string5;
                before = string5 = "set CLASSPATH=";
                between = string4 = ";";
                after = string = "";
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[32].call((Object)cmd2, "s"))) {
                String string;
                String string6;
                String string7;
                before = string7 = "export CLASSPATH=";
                between = string6 = ":";
                after = string = "";
            } else if (DefaultTypeTransformation.booleanUnbox(callSiteArray[33].call((Object)cmd2, "i"))) {
                String string;
                String string8;
                String string9;
                boolean bl = true;
                ivyFormatRequested = bl;
                before = string9 = "<dependency ";
                between = string8 = "\">\n<dependency ";
                after = string = "\">";
            } else {
                String string;
                String string10;
                String string11;
                before = string11 = "";
                between = string10 = "\n";
                after = string = "\n";
            }
            Object object2 = callSiteArray[34].call(arg);
            ScriptBytecodeAdapter.setGroovyObjectProperty(object2, _closure4.class, this, "iter");
            List params = ScriptBytecodeAdapter.createList(new Object[]{ScriptBytecodeAdapter.createMap(new Object[0])});
            List depsInfo = ScriptBytecodeAdapter.createList(new Object[0]);
            if (DefaultTypeTransformation.booleanUnbox(ivyFormatRequested)) {
                callSiteArray[35].call((Object)params, depsInfo);
            }
            while (DefaultTypeTransformation.booleanUnbox(callSiteArray[36].call(callSiteArray[37].callGroovyObjectGetProperty(this)))) {
                callSiteArray[38].call((Object)params, ScriptBytecodeAdapter.createMap(new Object[]{"group", callSiteArray[39].call(callSiteArray[40].callGroovyObjectGetProperty(this)), "module", callSiteArray[41].call(callSiteArray[42].callGroovyObjectGetProperty(this)), "version", callSiteArray[43].call(callSiteArray[44].callGroovyObjectGetProperty(this))}));
            }
            try {
                Reference<List> results = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
                Object uris = callSiteArray[45].call((Object)Grape.class, ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{params}, new int[]{0}));
                if (!DefaultTypeTransformation.booleanUnbox(ivyFormatRequested)) {
                    URI uri = null;
                    Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[46].call(uris), Iterator.class);
                    while (iterator.hasNext()) {
                        uri = (URI)ScriptBytecodeAdapter.castToType(iterator.next(), URI.class);
                        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[47].callGetProperty(uri), "file")) {
                            results.set((List)callSiteArray[48].call((Object)results.get(), callSiteArray[49].callGetProperty(callSiteArray[50].callConstructor(File.class, uri))));
                            continue;
                        }
                        results.set((List)callSiteArray[51].call((Object)results.get(), callSiteArray[52].call(uri)));
                    }
                } else {
                    public class _closure17
                    extends Closure
                    implements GeneratedClosure {
                        private /* synthetic */ Reference results;
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure17(Object _outerInstance, Object _thisObject, Reference results) {
                            Reference reference;
                            CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                            this.results = reference = results;
                        }

                        public Object doCall(Object dep) {
                            CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                            Object object = callSiteArray[0].call(this.results.get(), callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].call(callSiteArray[5].call((Object)"org=\"", callSiteArray[6].callGetProperty(dep)), "\" name=\""), callSiteArray[7].callGetProperty(dep)), "\" revision=\""), callSiteArray[8].callGetProperty(dep)));
                            this.results.set(object);
                            return object;
                        }

                        public Object getResults() {
                            CallSite[] callSiteArray = _closure17.$getCallSiteArray();
                            return this.results.get();
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure17.class) {
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
                            stringArray[1] = "plus";
                            stringArray[2] = "plus";
                            stringArray[3] = "plus";
                            stringArray[4] = "plus";
                            stringArray[5] = "plus";
                            stringArray[6] = "group";
                            stringArray[7] = "module";
                            stringArray[8] = "revision";
                        }

                        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                            String[] stringArray = new String[9];
                            _closure17.$createCallSiteArray_1(stringArray);
                            return new CallSiteArray(_closure17.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure17.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    callSiteArray[53].call((Object)depsInfo, new _closure17(this, this.getThisObject(), results));
                }
                if (!DefaultTypeTransformation.booleanUnbox(results.get())) return callSiteArray[56].callCurrent((GroovyObject)this, "Nothing was resolved");
                return callSiteArray[54].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{before, callSiteArray[55].call((Object)results.get(), between), after}, new String[]{"", "", "", ""}));
            }
            catch (Exception e) {
                callSiteArray[57].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[58].callGetProperty(e)}, new String[]{"Error in resolve:\n\t", ""}));
                if (!DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.findRegex(callSiteArray[59].callGetProperty(e), "unresolved dependency"))) return null;
                Object object3 = callSiteArray[60].callCurrent((GroovyObject)this, "Perhaps the grape is not installed?");
                return object3;
            }
        }

        public Object call(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure4.$getCallSiteArray();
            return callSiteArray[61].callCurrent(this, arg, cmd);
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
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "addOption";
            stringArray[2] = "create";
            stringArray[3] = "withLongOpt";
            stringArray[4] = "hasArg";
            stringArray[5] = "addOption";
            stringArray[6] = "create";
            stringArray[7] = "withLongOpt";
            stringArray[8] = "hasArg";
            stringArray[9] = "addOption";
            stringArray[10] = "create";
            stringArray[11] = "withLongOpt";
            stringArray[12] = "hasArg";
            stringArray[13] = "addOption";
            stringArray[14] = "create";
            stringArray[15] = "withLongOpt";
            stringArray[16] = "hasArg";
            stringArray[17] = "parse";
            stringArray[18] = "<$constructor$>";
            stringArray[19] = "getAt";
            stringArray[20] = "args";
            stringArray[21] = "getInstance";
            stringArray[22] = "setupLogging";
            stringArray[23] = "MSG_ERR";
            stringArray[24] = "mod";
            stringArray[25] = "size";
            stringArray[26] = "println";
            stringArray[27] = "size";
            stringArray[28] = "args";
            stringArray[29] = "println";
            stringArray[30] = "hasOption";
            stringArray[31] = "hasOption";
            stringArray[32] = "hasOption";
            stringArray[33] = "hasOption";
            stringArray[34] = "iterator";
            stringArray[35] = "leftShift";
            stringArray[36] = "hasNext";
            stringArray[37] = "iter";
            stringArray[38] = "add";
            stringArray[39] = "next";
            stringArray[40] = "iter";
            stringArray[41] = "next";
            stringArray[42] = "iter";
            stringArray[43] = "next";
            stringArray[44] = "iter";
            stringArray[45] = "resolve";
            stringArray[46] = "iterator";
            stringArray[47] = "scheme";
            stringArray[48] = "plus";
            stringArray[49] = "path";
            stringArray[50] = "<$constructor$>";
            stringArray[51] = "plus";
            stringArray[52] = "toASCIIString";
            stringArray[53] = "each";
            stringArray[54] = "println";
            stringArray[55] = "join";
            stringArray[56] = "println";
            stringArray[57] = "println";
            stringArray[58] = "message";
            stringArray[59] = "message";
            stringArray[60] = "println";
            stringArray[61] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[62];
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

        public Object doCall(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure5.$getCallSiteArray();
            return callSiteArray[0].callCurrent(this);
        }

        public Object call(Object arg, Object cmd) {
            CallSite[] callSiteArray = _closure5.$getCallSiteArray();
            return callSiteArray[1].callCurrent(this, arg, cmd);
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
            stringArray[0] = "grapeHelp";
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

        public Object doCall(Object it) {
            CallSite[] callSiteArray = _closure6.$getCallSiteArray();
            public class _closure18
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure18(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                    return callSiteArray[0].call(it);
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _closure18.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure18.class) {
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
                    stringArray[0] = "length";
                    return new CallSiteArray(_closure18.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure18.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            Reference<Integer> spacesLen = new Reference<Integer>((Integer)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].call(callSiteArray[2].call(callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this)), new _closure18(this, this.getThisObject()))), 3), Integer.class));
            Reference<String> spaces = new Reference<String>(ShortTypeHandling.castToString(callSiteArray[5].call((Object)" ", spacesLen.get())));
            Object object = callSiteArray[7].callGetProperty(callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this)));
            PrintWriter pw = (PrintWriter)ScriptBytecodeAdapter.castToType(callSiteArray[6].callConstructor(PrintWriter.class, DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[10].callGetProperty(System.class)), PrintWriter.class);
            callSiteArray[11].call(callSiteArray[12].callConstructor(HelpFormatter.class), ArrayUtil.createArray(pw, 80, "grape [options] <command> [args]\n", "options:", callSiteArray[13].callGroovyObjectGetProperty(this), 2, 4, null, true));
            callSiteArray[14].call(pw);
            callSiteArray[15].callCurrent((GroovyObject)this, "");
            callSiteArray[16].callCurrent((GroovyObject)this, "commands:");
            public class _closure19
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference spaces;
                private /* synthetic */ Reference spacesLen;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure19(Object _outerInstance, Object _thisObject, Reference spaces, Reference spacesLen) {
                    Reference reference;
                    Reference reference2;
                    CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.spaces = reference2 = spaces;
                    this.spacesLen = reference = spacesLen;
                }

                public Object doCall(String k, Object v) {
                    CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                    return callSiteArray[0].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[1].call(callSiteArray[2].call((Object)k, this.spaces.get()), 0, this.spacesLen.get()), callSiteArray[3].callGetProperty(v)}, new String[]{"  ", " ", ""}));
                }

                public Object call(String k, Object v) {
                    CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                    return callSiteArray[4].callCurrent(this, k, v);
                }

                public String getSpaces() {
                    CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                    return ShortTypeHandling.castToString(this.spaces.get());
                }

                public Integer getSpacesLen() {
                    CallSite[] callSiteArray = _closure19.$getCallSiteArray();
                    return (Integer)ScriptBytecodeAdapter.castToType(this.spacesLen.get(), Integer.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure19.class) {
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
                    stringArray[1] = "substring";
                    stringArray[2] = "plus";
                    stringArray[3] = "shortHelp";
                    stringArray[4] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _closure19.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_closure19.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure19.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[17].call(callSiteArray[18].callGroovyObjectGetProperty(this), new _closure19(this, this.getThisObject(), spaces, spacesLen));
            return callSiteArray[19].callCurrent((GroovyObject)this, "");
        }

        public Object doCall() {
            CallSite[] callSiteArray = _closure6.$getCallSiteArray();
            return this.doCall(null);
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
            stringArray[0] = "plus";
            stringArray[1] = "length";
            stringArray[2] = "max";
            stringArray[3] = "keySet";
            stringArray[4] = "commands";
            stringArray[5] = "multiply";
            stringArray[6] = "<$constructor$>";
            stringArray[7] = "out";
            stringArray[8] = "variables";
            stringArray[9] = "binding";
            stringArray[10] = "out";
            stringArray[11] = "printHelp";
            stringArray[12] = "<$constructor$>";
            stringArray[13] = "options";
            stringArray[14] = "flush";
            stringArray[15] = "println";
            stringArray[16] = "println";
            stringArray[17] = "each";
            stringArray[18] = "commands";
            stringArray[19] = "println";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[20];
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

    public class _closure7
    extends Closure
    implements GeneratedClosure {
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private static /* synthetic */ SoftReference $callSiteArray;

        public _closure7(Object _outerInstance, Object _thisObject) {
            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
            super(_outerInstance, _thisObject);
        }

        public Object doCall(int defaultLevel) {
            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), "q"))) {
                return callSiteArray[2].call(Message.class, callSiteArray[3].callConstructor(DefaultMessageLogger.class, callSiteArray[4].callGetProperty(Message.class)));
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), "w"))) {
                return callSiteArray[7].call(Message.class, callSiteArray[8].callConstructor(DefaultMessageLogger.class, callSiteArray[9].callGetProperty(Message.class)));
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(this), "i"))) {
                return callSiteArray[12].call(Message.class, callSiteArray[13].callConstructor(DefaultMessageLogger.class, callSiteArray[14].callGetProperty(Message.class)));
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call(callSiteArray[16].callGroovyObjectGetProperty(this), "V"))) {
                return callSiteArray[17].call(Message.class, callSiteArray[18].callConstructor(DefaultMessageLogger.class, callSiteArray[19].callGetProperty(Message.class)));
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[20].call(callSiteArray[21].callGroovyObjectGetProperty(this), "d"))) {
                return callSiteArray[22].call(Message.class, callSiteArray[23].callConstructor(DefaultMessageLogger.class, callSiteArray[24].callGetProperty(Message.class)));
            }
            return callSiteArray[25].call(Message.class, callSiteArray[26].callConstructor(DefaultMessageLogger.class, defaultLevel));
        }

        public Object call(int defaultLevel) {
            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                return callSiteArray[27].callCurrent((GroovyObject)this, defaultLevel);
            }
            return this.doCall(defaultLevel);
        }

        public Object doCall() {
            CallSite[] callSiteArray = _closure7.$getCallSiteArray();
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                return this.doCall(2);
            }
            return this.doCall(2);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != _closure7.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "hasOption";
            stringArray[1] = "cmd";
            stringArray[2] = "setDefaultLogger";
            stringArray[3] = "<$constructor$>";
            stringArray[4] = "MSG_ERR";
            stringArray[5] = "hasOption";
            stringArray[6] = "cmd";
            stringArray[7] = "setDefaultLogger";
            stringArray[8] = "<$constructor$>";
            stringArray[9] = "MSG_WARN";
            stringArray[10] = "hasOption";
            stringArray[11] = "cmd";
            stringArray[12] = "setDefaultLogger";
            stringArray[13] = "<$constructor$>";
            stringArray[14] = "MSG_INFO";
            stringArray[15] = "hasOption";
            stringArray[16] = "cmd";
            stringArray[17] = "setDefaultLogger";
            stringArray[18] = "<$constructor$>";
            stringArray[19] = "MSG_VERBOSE";
            stringArray[20] = "hasOption";
            stringArray[21] = "cmd";
            stringArray[22] = "setDefaultLogger";
            stringArray[23] = "<$constructor$>";
            stringArray[24] = "MSG_DEBUG";
            stringArray[25] = "setDefaultLogger";
            stringArray[26] = "<$constructor$>";
            stringArray[27] = "doCall";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[28];
            _closure7.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(_closure7.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = _closure7.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}


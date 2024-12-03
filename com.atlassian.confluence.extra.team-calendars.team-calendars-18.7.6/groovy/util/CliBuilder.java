/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.cli.CommandLineParser
 *  org.apache.commons.cli.GnuParser
 *  org.apache.commons.cli.HelpFormatter
 *  org.apache.commons.cli.Option
 *  org.apache.commons.cli.OptionBuilder
 *  org.apache.commons.cli.Options
 *  org.apache.commons.cli.ParseException
 *  org.apache.commons.cli.Parser
 *  org.apache.commons.cli.PosixParser
 */
package groovy.util;

import groovy.lang.Closure;
import groovy.lang.GString;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.OptionAccessor;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StreamTokenizer;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.GnuParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.Parser;
import org.apache.commons.cli.PosixParser;
import org.codehaus.groovy.cli.GroovyPosixParser;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.InvokerHelper;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

public class CliBuilder
implements GroovyObject {
    private String usage;
    private CommandLineParser parser;
    @Deprecated
    private Boolean posix;
    private boolean expandArgumentFiles;
    private HelpFormatter formatter;
    private PrintWriter writer;
    private String header;
    private String footer;
    private boolean stopAtNonOption;
    private int width;
    private Options options;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CliBuilder() {
        MetaClass metaClass;
        boolean bl;
        String string;
        String string2;
        boolean bl2;
        String string3;
        CallSite[] callSiteArray = CliBuilder.$getCallSiteArray();
        this.usage = string3 = "groovy";
        Object var3_3 = null;
        this.parser = (CommandLineParser)ScriptBytecodeAdapter.castToType(var3_3, CommandLineParser.class);
        Object var4_4 = null;
        this.posix = (Boolean)ScriptBytecodeAdapter.castToType(var4_4, Boolean.class);
        this.expandArgumentFiles = bl2 = true;
        Object object = callSiteArray[0].callConstructor(HelpFormatter.class);
        this.formatter = (HelpFormatter)ScriptBytecodeAdapter.castToType(object, HelpFormatter.class);
        Object object2 = callSiteArray[1].callConstructor(PrintWriter.class, callSiteArray[2].callGetProperty(System.class));
        this.writer = (PrintWriter)ScriptBytecodeAdapter.castToType(object2, PrintWriter.class);
        this.header = string2 = "";
        this.footer = string = "";
        this.stopAtNonOption = bl = true;
        Object object3 = callSiteArray[3].callGetProperty(this.formatter);
        this.width = DefaultTypeTransformation.intUnbox(object3);
        Object object4 = callSiteArray[4].callConstructor(Options.class);
        this.options = (Options)ScriptBytecodeAdapter.castToType(object4, Options.class);
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public Object invokeMethod(String name, Object args) {
        CallSite[] callSiteArray = CliBuilder.$getCallSiteArray();
        if (args instanceof Object[]) {
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[5].call(args), 1) && (callSiteArray[6].call(args, 0) instanceof String || callSiteArray[7].call(args, 0) instanceof GString)) {
                    callSiteArray[8].call((Object)this.options, callSiteArray[9].callCurrent(this, name, ScriptBytecodeAdapter.createMap(new Object[0]), callSiteArray[10].call(args, 0)));
                    return null;
                }
            } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[11].call(args), 1) && (callSiteArray[12].call(args, 0) instanceof String || callSiteArray[13].call(args, 0) instanceof GString)) {
                callSiteArray[14].call((Object)this.options, callSiteArray[15].callCurrent(this, name, ScriptBytecodeAdapter.createMap(new Object[0]), callSiteArray[16].call(args, 0)));
                return null;
            }
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareEqual(callSiteArray[17].call(args), 1) && callSiteArray[18].call(args, 0) instanceof Option && ScriptBytecodeAdapter.compareEqual(name, "leftShift")) {
                    callSiteArray[19].call((Object)this.options, callSiteArray[20].call(args, 0));
                    return null;
                }
            } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[21].call(args), 1) && callSiteArray[22].call(args, 0) instanceof Option && ScriptBytecodeAdapter.compareEqual(name, "leftShift")) {
                callSiteArray[23].call((Object)this.options, callSiteArray[24].call(args, 0));
                return null;
            }
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[25].call(args), 2) && callSiteArray[26].call(args, 0) instanceof Map) {
                callSiteArray[27].call((Object)this.options, callSiteArray[28].callCurrent(this, name, callSiteArray[29].call(args, 0), callSiteArray[30].call(args, 1)));
                return null;
            }
        }
        return callSiteArray[31].call(callSiteArray[32].call(InvokerHelper.class, this), this, name, args);
    }

    public OptionAccessor parse(Object args) {
        CallSite[] callSiteArray = CliBuilder.$getCallSiteArray();
        if (this.expandArgumentFiles) {
            Object object;
            args = object = callSiteArray[33].callStatic(CliBuilder.class, args);
        }
        if (!DefaultTypeTransformation.booleanUnbox(this.parser)) {
            Object object = ScriptBytecodeAdapter.compareEqual(this.posix, null) ? callSiteArray[34].callConstructor(GroovyPosixParser.class) : (ScriptBytecodeAdapter.compareEqual(this.posix, true) ? (Parser)ScriptBytecodeAdapter.castToType(callSiteArray[35].callConstructor(PosixParser.class), Parser.class) : (Parser)ScriptBytecodeAdapter.castToType(callSiteArray[36].callConstructor(GnuParser.class), Parser.class));
            this.parser = (CommandLineParser)ScriptBytecodeAdapter.castToType(object, CommandLineParser.class);
        }
        OptionAccessor optionAccessor = (OptionAccessor)ScriptBytecodeAdapter.castToType(callSiteArray[37].callConstructor(OptionAccessor.class, callSiteArray[38].call(this.parser, this.options, ScriptBytecodeAdapter.createPojoWrapper((String[])ScriptBytecodeAdapter.asType(args, String[].class), String[].class), this.stopAtNonOption)), OptionAccessor.class);
        try {
            return optionAccessor;
        }
        catch (ParseException pe) {
            callSiteArray[39].call((Object)this.writer, callSiteArray[40].call((Object)"error: ", callSiteArray[41].callGetProperty((Object)pe)));
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[42].callCurrent(this);
            } else {
                this.usage();
            }
            OptionAccessor optionAccessor2 = (OptionAccessor)ScriptBytecodeAdapter.castToType(null, OptionAccessor.class);
            return optionAccessor2;
        }
    }

    public void usage() {
        CallSite[] callSiteArray = CliBuilder.$getCallSiteArray();
        callSiteArray[43].call((Object)this.formatter, ArrayUtil.createArray(this.writer, this.width, this.usage, this.header, this.options, callSiteArray[44].callGetProperty(this.formatter), callSiteArray[45].callGetProperty(this.formatter), this.footer));
        callSiteArray[46].call(this.writer);
    }

    public Option option(Object shortname, Map details, Object info) {
        CallSite[] callSiteArray = CliBuilder.$getCallSiteArray();
        Reference<Object> option = new Reference<Object>(null);
        Option cfr_ignored_0 = option.get();
        if (ScriptBytecodeAdapter.compareEqual(shortname, "_")) {
            Object object = callSiteArray[47].call(callSiteArray[48].call(callSiteArray[49].call(OptionBuilder.class, info), callSiteArray[50].callGetProperty(details)));
            option.set(((Option)ScriptBytecodeAdapter.castToType(object, Option.class)));
            callSiteArray[51].call((Object)details, "longOpt");
        } else {
            Object object = callSiteArray[52].callConstructor(Option.class, shortname, info);
            option.set(((Option)ScriptBytecodeAdapter.castToType(object, Option.class)));
        }
        public class _option_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference option;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _option_closure1(Object _outerInstance, Object _thisObject, Reference option) {
                Reference reference;
                CallSite[] callSiteArray = _option_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.option = reference = option;
            }

            public Object doCall(Object key, Object value) {
                CallSite[] callSiteArray = _option_closure1.$getCallSiteArray();
                Object object = value;
                callSiteArray[0].call(this.option.get(), key, object);
                return object;
            }

            public Object call(Object key, Object value) {
                CallSite[] callSiteArray = _option_closure1.$getCallSiteArray();
                return callSiteArray[1].callCurrent(this, key, value);
            }

            public Option getOption() {
                CallSite[] callSiteArray = _option_closure1.$getCallSiteArray();
                return (Option)ScriptBytecodeAdapter.castToType(this.option.get(), Option.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _option_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "putAt";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _option_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_option_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _option_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[53].call((Object)details, new _option_closure1(this, this, option));
        return option.get();
    }

    public static Object expandArgumentFiles(Object args) throws IOException {
        CallSite[] callSiteArray = CliBuilder.$getCallSiteArray();
        List result = ScriptBytecodeAdapter.createList(new Object[0]);
        Object arg = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[54].call(args), Iterator.class);
        while (iterator.hasNext()) {
            arg = iterator.next();
            if (DefaultTypeTransformation.booleanUnbox(arg) && ScriptBytecodeAdapter.compareNotEqual(arg, "@") && ScriptBytecodeAdapter.compareEqual(callSiteArray[55].call(arg, 0), "@")) {
                Object object;
                arg = object = callSiteArray[56].call(arg, 1);
                if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[57].call(arg, 0), "@")) {
                    callSiteArray[58].callStatic(CliBuilder.class, arg, result);
                    continue;
                }
            }
            callSiteArray[59].call((Object)result, arg);
        }
        return result;
    }

    private static Object expandArgumentFile(Object name, Object args) throws IOException {
        Reference<Object> args2 = new Reference<Object>(args);
        CallSite[] callSiteArray = CliBuilder.$getCallSiteArray();
        public class _expandArgumentFile_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _expandArgumentFile_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _expandArgumentFile_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String s) {
                CallSite[] callSiteArray = _expandArgumentFile_closure2.$getCallSiteArray();
                return DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[0].call(s), Integer.TYPE));
            }

            public Object call(String s) {
                CallSite[] callSiteArray = _expandArgumentFile_closure2.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[1].callCurrent((GroovyObject)this, s);
                }
                return this.doCall(s);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _expandArgumentFile_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "toCharacter";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _expandArgumentFile_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_expandArgumentFile_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _expandArgumentFile_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Reference<_expandArgumentFile_closure2> charAsInt = new Reference<_expandArgumentFile_closure2>(new _expandArgumentFile_closure2(CliBuilder.class, CliBuilder.class));
        public class _expandArgumentFile_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference charAsInt;
            private /* synthetic */ Reference args;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _expandArgumentFile_closure3(Object _outerInstance, Object _thisObject, Reference charAsInt, Reference args) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _expandArgumentFile_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.charAsInt = reference2 = charAsInt;
                this.args = reference = args;
            }

            public Object doCall(Object r) {
                CallSite[] callSiteArray = _expandArgumentFile_closure3.$getCallSiteArray();
                public class _closure4
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference charAsInt;
                    private /* synthetic */ Reference args;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure4(Object _outerInstance, Object _thisObject, Reference charAsInt, Reference args) {
                        Reference reference;
                        Reference reference2;
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.charAsInt = reference2 = charAsInt;
                        this.args = reference = args;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        callSiteArray[0].callCurrent(this);
                        callSiteArray[1].callCurrent(this, callSiteArray[2].call(this.charAsInt.get(), " "), 255);
                        callSiteArray[3].callCurrent(this, 0, callSiteArray[4].call(this.charAsInt.get(), " "));
                        callSiteArray[5].callCurrent((GroovyObject)this, callSiteArray[6].call(this.charAsInt.get(), "#"));
                        callSiteArray[7].callCurrent((GroovyObject)this, callSiteArray[8].call(this.charAsInt.get(), "\""));
                        callSiteArray[9].callCurrent((GroovyObject)this, callSiteArray[10].call(this.charAsInt.get(), "'"));
                        while (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[11].callCurrent(this), callSiteArray[12].callGetProperty(StreamTokenizer.class))) {
                            callSiteArray[13].call(this.args.get(), callSiteArray[14].callGroovyObjectGetProperty(this));
                        }
                        return null;
                    }

                    public Object getCharAsInt() {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return this.charAsInt.get();
                    }

                    public Object getArgs() {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return this.args.get();
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

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "resetSyntax";
                        stringArray[1] = "wordChars";
                        stringArray[2] = "call";
                        stringArray[3] = "whitespaceChars";
                        stringArray[4] = "call";
                        stringArray[5] = "commentChar";
                        stringArray[6] = "call";
                        stringArray[7] = "quoteChar";
                        stringArray[8] = "call";
                        stringArray[9] = "quoteChar";
                        stringArray[10] = "call";
                        stringArray[11] = "nextToken";
                        stringArray[12] = "TT_EOF";
                        stringArray[13] = "leftShift";
                        stringArray[14] = "sval";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[15];
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
                return callSiteArray[0].call(callSiteArray[1].callConstructor(StreamTokenizer.class, r), new _closure4(this, this.getThisObject(), this.charAsInt, this.args));
            }

            public Object getCharAsInt() {
                CallSite[] callSiteArray = _expandArgumentFile_closure3.$getCallSiteArray();
                return this.charAsInt.get();
            }

            public Object getArgs() {
                CallSite[] callSiteArray = _expandArgumentFile_closure3.$getCallSiteArray();
                return this.args.get();
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _expandArgumentFile_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "with";
                stringArray[1] = "<$constructor$>";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _expandArgumentFile_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_expandArgumentFile_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _expandArgumentFile_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[60].call(callSiteArray[61].callConstructor(File.class, name), new _expandArgumentFile_closure3(CliBuilder.class, CliBuilder.class, charAsInt, args2));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CliBuilder.class) {
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
    public /* synthetic */ Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public /* synthetic */ void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    public String getUsage() {
        return this.usage;
    }

    public void setUsage(String string) {
        this.usage = string;
    }

    public CommandLineParser getParser() {
        return this.parser;
    }

    public void setParser(CommandLineParser commandLineParser) {
        this.parser = commandLineParser;
    }

    public Boolean getPosix() {
        return this.posix;
    }

    public void setPosix(Boolean bl) {
        this.posix = bl;
    }

    public boolean getExpandArgumentFiles() {
        return this.expandArgumentFiles;
    }

    public boolean isExpandArgumentFiles() {
        return this.expandArgumentFiles;
    }

    public void setExpandArgumentFiles(boolean bl) {
        this.expandArgumentFiles = bl;
    }

    public HelpFormatter getFormatter() {
        return this.formatter;
    }

    public void setFormatter(HelpFormatter helpFormatter) {
        this.formatter = helpFormatter;
    }

    public PrintWriter getWriter() {
        return this.writer;
    }

    public void setWriter(PrintWriter printWriter) {
        this.writer = printWriter;
    }

    public String getHeader() {
        return this.header;
    }

    public void setHeader(String string) {
        this.header = string;
    }

    public String getFooter() {
        return this.footer;
    }

    public void setFooter(String string) {
        this.footer = string;
    }

    public boolean getStopAtNonOption() {
        return this.stopAtNonOption;
    }

    public boolean isStopAtNonOption() {
        return this.stopAtNonOption;
    }

    public void setStopAtNonOption(boolean bl) {
        this.stopAtNonOption = bl;
    }

    public int getWidth() {
        return this.width;
    }

    public void setWidth(int n) {
        this.width = n;
    }

    public Options getOptions() {
        return this.options;
    }

    public void setOptions(Options options) {
        this.options = options;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "out";
        stringArray[3] = "defaultWidth";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "size";
        stringArray[6] = "getAt";
        stringArray[7] = "getAt";
        stringArray[8] = "addOption";
        stringArray[9] = "option";
        stringArray[10] = "getAt";
        stringArray[11] = "size";
        stringArray[12] = "getAt";
        stringArray[13] = "getAt";
        stringArray[14] = "addOption";
        stringArray[15] = "option";
        stringArray[16] = "getAt";
        stringArray[17] = "size";
        stringArray[18] = "getAt";
        stringArray[19] = "addOption";
        stringArray[20] = "getAt";
        stringArray[21] = "size";
        stringArray[22] = "getAt";
        stringArray[23] = "addOption";
        stringArray[24] = "getAt";
        stringArray[25] = "size";
        stringArray[26] = "getAt";
        stringArray[27] = "addOption";
        stringArray[28] = "option";
        stringArray[29] = "getAt";
        stringArray[30] = "getAt";
        stringArray[31] = "invokeMethod";
        stringArray[32] = "getMetaClass";
        stringArray[33] = "expandArgumentFiles";
        stringArray[34] = "<$constructor$>";
        stringArray[35] = "<$constructor$>";
        stringArray[36] = "<$constructor$>";
        stringArray[37] = "<$constructor$>";
        stringArray[38] = "parse";
        stringArray[39] = "println";
        stringArray[40] = "plus";
        stringArray[41] = "message";
        stringArray[42] = "usage";
        stringArray[43] = "printHelp";
        stringArray[44] = "defaultLeftPad";
        stringArray[45] = "defaultDescPad";
        stringArray[46] = "flush";
        stringArray[47] = "create";
        stringArray[48] = "withLongOpt";
        stringArray[49] = "withDescription";
        stringArray[50] = "longOpt";
        stringArray[51] = "remove";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "each";
        stringArray[54] = "iterator";
        stringArray[55] = "getAt";
        stringArray[56] = "substring";
        stringArray[57] = "getAt";
        stringArray[58] = "expandArgumentFile";
        stringArray[59] = "leftShift";
        stringArray[60] = "withReader";
        stringArray[61] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[62];
        CliBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CliBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CliBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import groovy.io.FileType;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.util.CliBuilder;
import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.groovydoc.ClasspathResourceManager;
import org.codehaus.groovy.tools.groovydoc.FileOutputTool;
import org.codehaus.groovy.tools.groovydoc.GroovyDocTool;
import org.codehaus.groovy.tools.groovydoc.gstringTemplates.GroovyDocTemplateInfo;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.tools.shell.util.MessageSource;

public class Main
implements GroovyObject {
    private static final MessageSource messages;
    private static File styleSheetFile;
    private static File overviewFile;
    private static File destDir;
    private static String windowTitle;
    private static String docTitle;
    private static String header;
    private static String footer;
    private static String charset;
    private static String fileEncoding;
    private static Boolean author;
    private static Boolean noScripts;
    private static Boolean noMainForScripts;
    private static Boolean noTimestamp;
    private static Boolean noVersionStamp;
    private static Boolean privateScope;
    private static Boolean packageScope;
    private static Boolean publicScope;
    private static Boolean protectedScope;
    private static Boolean debug;
    private static String[] sourcepath;
    private static List<String> sourceFilesToDoc;
    private static List<String> remainingArgs;
    private static List<String> exclusions;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public Main() {
        MetaClass metaClass;
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static void main(String ... args) {
        Object object;
        Object object2;
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        IO io = (IO)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(IO.class), IO.class);
        IO iO = io;
        ScriptBytecodeAdapter.setProperty(iO, null, Logger.class, "io");
        Object cli = callSiteArray[1].callConstructor(CliBuilder.class, ScriptBytecodeAdapter.createMap(new Object[]{"usage", "groovydoc [options] [packagenames] [sourcefiles]", "writer", callSiteArray[2].callGetProperty(io), "posix", false}));
        callSiteArray[3].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "help"}), callSiteArray[4].call((Object)messages, "cli.option.help.description"));
        callSiteArray[5].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "version"}), callSiteArray[6].call((Object)messages, "cli.option.version.description"));
        callSiteArray[7].call(cli, callSiteArray[8].call((Object)messages, "cli.option.verbose.description"));
        callSiteArray[9].call(cli, callSiteArray[10].call((Object)messages, "cli.option.quiet.description"));
        callSiteArray[11].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "debug"}), callSiteArray[12].call((Object)messages, "cli.option.debug.description"));
        callSiteArray[13].call(cli, callSiteArray[14].call((Object)messages, "cli.option.classpath.description"));
        callSiteArray[15].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "classpath"}), callSiteArray[16].call((Object)messages, "cli.option.cp.description"));
        callSiteArray[17].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"longOpt", "destdir", "args", 1, "argName", "dir"}), callSiteArray[18].call((Object)messages, "cli.option.destdir.description"));
        callSiteArray[19].call(cli, callSiteArray[20].call((Object)messages, "cli.option.author.description"));
        callSiteArray[21].call(cli, callSiteArray[22].call((Object)messages, "cli.option.noscripts.description"));
        callSiteArray[23].call(cli, callSiteArray[24].call((Object)messages, "cli.option.nomainforscripts.description"));
        callSiteArray[25].call(cli, callSiteArray[26].call((Object)messages, "cli.option.notimestamp.description"));
        callSiteArray[27].call(cli, callSiteArray[28].call((Object)messages, "cli.option.noversionstamp.description"));
        callSiteArray[29].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "file"}), callSiteArray[30].call((Object)messages, "cli.option.overview.description"));
        callSiteArray[31].call(cli, callSiteArray[32].call((Object)messages, "cli.option.public.description"));
        callSiteArray[33].call(cli, callSiteArray[34].call((Object)messages, "cli.option.protected.description"));
        callSiteArray[35].call(cli, callSiteArray[36].call((Object)messages, "cli.option.package.description"));
        callSiteArray[37].call(cli, callSiteArray[38].call((Object)messages, "cli.option.private.description"));
        callSiteArray[39].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "charset"}), callSiteArray[40].call((Object)messages, "cli.option.charset.description"));
        callSiteArray[41].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "charset"}), callSiteArray[42].call((Object)messages, "cli.option.fileEncoding.description"));
        callSiteArray[43].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "text"}), callSiteArray[44].call((Object)messages, "cli.option.windowtitle.description"));
        callSiteArray[45].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "html"}), callSiteArray[46].call((Object)messages, "cli.option.doctitle.description"));
        callSiteArray[47].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "html"}), callSiteArray[48].call((Object)messages, "cli.option.header.description"));
        callSiteArray[49].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "html"}), callSiteArray[50].call((Object)messages, "cli.option.footer.description"));
        callSiteArray[51].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "pkglist"}), callSiteArray[52].call((Object)messages, "cli.option.exclude.description"));
        callSiteArray[53].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "path"}), callSiteArray[54].call((Object)messages, "cli.option.stylesheetfile.description"));
        callSiteArray[55].call(cli, ScriptBytecodeAdapter.createMap(new Object[]{"args", 1, "argName", "pathlist"}), callSiteArray[56].call((Object)messages, "cli.option.sourcepath.description"));
        Object options = callSiteArray[57].call(cli, (Object)args);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[58].callGetProperty(options))) {
            callSiteArray[59].call(cli);
            return;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[60].callGetProperty(options))) {
            callSiteArray[61].call(callSiteArray[62].callGetProperty(io), callSiteArray[63].call(messages, "cli.info.version", callSiteArray[64].callGetProperty(GroovySystem.class)));
            return;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[65].callGetProperty(options))) {
            Object object3 = callSiteArray[66].callConstructor(File.class, callSiteArray[67].callGetProperty(options));
            styleSheetFile = (File)ScriptBytecodeAdapter.castToType(object3, File.class);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[68].callGetProperty(options))) {
            Object object4 = callSiteArray[69].callConstructor(File.class, callSiteArray[70].callGetProperty(options));
            overviewFile = (File)ScriptBytecodeAdapter.castToType(object4, File.class);
        }
        Object object5 = callSiteArray[71].callConstructor(File.class, DefaultTypeTransformation.booleanUnbox(object2 = callSiteArray[72].callGetProperty(options)) ? object2 : ".");
        destDir = (File)ScriptBytecodeAdapter.castToType(object5, File.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[73].callGetProperty(options))) {
            Object object6 = callSiteArray[74].call(callSiteArray[75].callGetProperty(options), ":");
            exclusions = (List)ScriptBytecodeAdapter.castToType(object6, List.class);
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[76].callGetProperty(options))) {
            Reference<List> list = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
            public class _main_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference list;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _main_closure1(Object _outerInstance, Object _thisObject, Reference list) {
                    Reference reference;
                    CallSite[] callSiteArray = _main_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.list = reference = list;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _main_closure1.$getCallSiteArray();
                    return callSiteArray[0].call(this.list.get(), callSiteArray[1].call(it, callSiteArray[2].callGetProperty(File.class)));
                }

                public Object getList() {
                    CallSite[] callSiteArray = _main_closure1.$getCallSiteArray();
                    return this.list.get();
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
                    stringArray[0] = "addAll";
                    stringArray[1] = "tokenize";
                    stringArray[2] = "pathSeparator";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
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
            callSiteArray[77].call(callSiteArray[78].callGetProperty(options), new _main_closure1(Main.class, Main.class, list));
            Object object7 = callSiteArray[79].call(list.get());
            sourcepath = (String[])ScriptBytecodeAdapter.castToType(object7, String[].class);
        }
        Object object8 = DefaultTypeTransformation.booleanUnbox(object = callSiteArray[80].call(Boolean.class, callSiteArray[81].callGetProperty(options))) ? object : Boolean.valueOf(false);
        author = (Boolean)ScriptBytecodeAdapter.castToType(object8, Boolean.class);
        Object object9 = callSiteArray[82].call(Boolean.class, callSiteArray[83].callGetProperty(options));
        Object object10 = DefaultTypeTransformation.booleanUnbox(object9) ? object9 : Boolean.valueOf(false);
        noScripts = (Boolean)ScriptBytecodeAdapter.castToType(object10, Boolean.class);
        Object object11 = callSiteArray[84].call(Boolean.class, callSiteArray[85].callGetProperty(options));
        Object object12 = DefaultTypeTransformation.booleanUnbox(object11) ? object11 : Boolean.valueOf(false);
        noMainForScripts = (Boolean)ScriptBytecodeAdapter.castToType(object12, Boolean.class);
        Object object13 = callSiteArray[86].call(Boolean.class, callSiteArray[87].callGetProperty(options));
        Object object14 = DefaultTypeTransformation.booleanUnbox(object13) ? object13 : Boolean.valueOf(false);
        noTimestamp = (Boolean)ScriptBytecodeAdapter.castToType(object14, Boolean.class);
        Object object15 = callSiteArray[88].call(Boolean.class, callSiteArray[89].callGetProperty(options));
        Object object16 = DefaultTypeTransformation.booleanUnbox(object15) ? object15 : Boolean.valueOf(false);
        noVersionStamp = (Boolean)ScriptBytecodeAdapter.castToType(object16, Boolean.class);
        Object object17 = callSiteArray[90].call(Boolean.class, callSiteArray[91].callGetProperty(options));
        Object object18 = DefaultTypeTransformation.booleanUnbox(object17) ? object17 : Boolean.valueOf(false);
        packageScope = (Boolean)ScriptBytecodeAdapter.castToType(object18, Boolean.class);
        Object object19 = callSiteArray[92].call(Boolean.class, callSiteArray[93].callGetProperty(options));
        Object object20 = DefaultTypeTransformation.booleanUnbox(object19) ? object19 : Boolean.valueOf(false);
        privateScope = (Boolean)ScriptBytecodeAdapter.castToType(object20, Boolean.class);
        Object object21 = callSiteArray[94].call(Boolean.class, callSiteArray[95].callGetProperty(options));
        Object object22 = DefaultTypeTransformation.booleanUnbox(object21) ? object21 : Boolean.valueOf(false);
        protectedScope = (Boolean)ScriptBytecodeAdapter.castToType(object22, Boolean.class);
        Object object23 = callSiteArray[96].call(Boolean.class, callSiteArray[97].callGetProperty(options));
        Object object24 = DefaultTypeTransformation.booleanUnbox(object23) ? object23 : Boolean.valueOf(false);
        publicScope = (Boolean)ScriptBytecodeAdapter.castToType(object24, Boolean.class);
        int scopeCount = 0;
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(packageScope)) {
                int n = scopeCount;
                scopeCount = DefaultTypeTransformation.intUnbox(callSiteArray[98].call(n));
            }
        } else if (DefaultTypeTransformation.booleanUnbox(packageScope)) {
            int n = scopeCount;
            scopeCount = n + 1;
        }
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(privateScope)) {
                int n = scopeCount;
                scopeCount = DefaultTypeTransformation.intUnbox(callSiteArray[99].call(n));
            }
        } else if (DefaultTypeTransformation.booleanUnbox(privateScope)) {
            int n = scopeCount;
            scopeCount = n + 1;
        }
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(protectedScope)) {
                int n = scopeCount;
                scopeCount = DefaultTypeTransformation.intUnbox(callSiteArray[100].call(n));
            }
        } else if (DefaultTypeTransformation.booleanUnbox(protectedScope)) {
            int n = scopeCount;
            scopeCount = n + 1;
        }
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (DefaultTypeTransformation.booleanUnbox(publicScope)) {
                int n = scopeCount;
                scopeCount = DefaultTypeTransformation.intUnbox(callSiteArray[101].call(n));
            }
        } else if (DefaultTypeTransformation.booleanUnbox(publicScope)) {
            int n = scopeCount;
            scopeCount = n + 1;
        }
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (scopeCount == 0) {
                boolean bl = true;
                protectedScope = bl;
            } else if (scopeCount > 1) {
                callSiteArray[102].call(callSiteArray[103].callGetProperty(System.class), "groovydoc: Error - More than one of -public, -private, -package, or -protected specified.");
                callSiteArray[104].call(cli);
                return;
            }
        } else if (scopeCount == 0) {
            boolean bl = true;
            protectedScope = bl;
        } else if (scopeCount > 1) {
            callSiteArray[105].call(callSiteArray[106].callGetProperty(System.class), "groovydoc: Error - More than one of -public, -private, -package, or -protected specified.");
            callSiteArray[107].call(cli);
            return;
        }
        Object object25 = callSiteArray[108].callGetProperty(options);
        Object object26 = DefaultTypeTransformation.booleanUnbox(object25) ? object25 : "";
        windowTitle = ShortTypeHandling.castToString(object26);
        Object object27 = callSiteArray[109].callGetProperty(options);
        Object object28 = DefaultTypeTransformation.booleanUnbox(object27) ? object27 : "";
        docTitle = ShortTypeHandling.castToString(object28);
        Object object29 = callSiteArray[110].callGetProperty(options);
        Object object30 = DefaultTypeTransformation.booleanUnbox(object29) ? object29 : "";
        header = ShortTypeHandling.castToString(object30);
        Object object31 = callSiteArray[111].callGetProperty(options);
        Object object32 = DefaultTypeTransformation.booleanUnbox(object31) ? object31 : "";
        footer = ShortTypeHandling.castToString(object32);
        Object object33 = callSiteArray[112].callGetProperty(options);
        Object object34 = DefaultTypeTransformation.booleanUnbox(object33) ? object33 : "";
        charset = ShortTypeHandling.castToString(object34);
        Object object35 = callSiteArray[113].callGetProperty(options);
        Object object36 = DefaultTypeTransformation.booleanUnbox(object35) ? object35 : "";
        fileEncoding = ShortTypeHandling.castToString(object36);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[114].callGetProperty(options))) {
            Object values = callSiteArray[115].callGetProperty(options);
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

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                    return callSiteArray[0].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createPojoWrapper((String)ScriptBytecodeAdapter.asType(it, String.class), String.class));
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _main_closure2.$getCallSiteArray();
                    return this.doCall(null);
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

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "setSystemProperty";
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
            callSiteArray[116].call(values, new _main_closure2(Main.class, Main.class));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[117].callGetProperty(options))) {
            Object object37 = callSiteArray[118].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object37, null, io, "verbosity");
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[119].callGetProperty(options))) {
            Object object38 = callSiteArray[120].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object38, null, io, "verbosity");
            boolean bl = true;
            debug = bl;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[121].callGetProperty(options))) {
            Object object39 = callSiteArray[122].callGetProperty(IO.Verbosity.class);
            ScriptBytecodeAdapter.setProperty(object39, null, io, "verbosity");
        }
        Object object40 = callSiteArray[123].call(options);
        remainingArgs = (List)ScriptBytecodeAdapter.castToType(object40, List.class);
        if (!DefaultTypeTransformation.booleanUnbox(remainingArgs)) {
            callSiteArray[124].call(callSiteArray[125].callGetProperty(System.class), "groovydoc: Error - No packages or classes specified.");
            callSiteArray[126].call(cli);
            return;
        }
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[127].callStatic(Main.class);
        } else {
            Main.execute();
        }
    }

    public static void execute() {
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        Properties properties = (Properties)ScriptBytecodeAdapter.castToType(callSiteArray[128].callConstructor(Properties.class), Properties.class);
        callSiteArray[129].call(properties, "windowTitle", windowTitle);
        callSiteArray[130].call(properties, "docTitle", docTitle);
        callSiteArray[131].call(properties, "footer", footer);
        callSiteArray[132].call(properties, "header", header);
        callSiteArray[133].call(properties, "charset", charset);
        callSiteArray[134].call(properties, "fileEncoding", fileEncoding);
        callSiteArray[135].call(properties, "privateScope", callSiteArray[136].call(privateScope));
        callSiteArray[137].call(properties, "protectedScope", callSiteArray[138].call(protectedScope));
        callSiteArray[139].call(properties, "publicScope", callSiteArray[140].call(publicScope));
        callSiteArray[141].call(properties, "packageScope", callSiteArray[142].call(packageScope));
        callSiteArray[143].call(properties, "author", callSiteArray[144].call(author));
        callSiteArray[145].call(properties, "processScripts", callSiteArray[146].call(!DefaultTypeTransformation.booleanUnbox(noScripts)));
        callSiteArray[147].call(properties, "includeMainForScripts", callSiteArray[148].call(!DefaultTypeTransformation.booleanUnbox(noMainForScripts)));
        callSiteArray[149].call(properties, "timestamp", callSiteArray[150].call(!DefaultTypeTransformation.booleanUnbox(noTimestamp)));
        callSiteArray[151].call(properties, "versionStamp", callSiteArray[152].call(!DefaultTypeTransformation.booleanUnbox(noVersionStamp)));
        Object object = callSiteArray[154].callGetPropertySafe(overviewFile);
        callSiteArray[153].call(properties, "overviewFile", DefaultTypeTransformation.booleanUnbox(object) ? object : "");
        Object links = callSiteArray[155].callConstructor(ArrayList.class);
        callSiteArray[156].callStatic(Main.class, remainingArgs, sourcepath, exclusions);
        GroovyDocTool htmlTool = (GroovyDocTool)ScriptBytecodeAdapter.castToType(callSiteArray[157].callConstructor((Object)GroovyDocTool.class, ArrayUtil.createArray(callSiteArray[158].callConstructor(ClasspathResourceManager.class), sourcepath, callSiteArray[159].callGetProperty(GroovyDocTemplateInfo.class), callSiteArray[160].callGetProperty(GroovyDocTemplateInfo.class), callSiteArray[161].callGetProperty(GroovyDocTemplateInfo.class), links, properties)), GroovyDocTool.class);
        callSiteArray[162].call((Object)htmlTool, sourceFilesToDoc);
        FileOutputTool output = (FileOutputTool)ScriptBytecodeAdapter.castToType(callSiteArray[163].callConstructor(FileOutputTool.class), FileOutputTool.class);
        callSiteArray[164].call(htmlTool, output, callSiteArray[165].callGetProperty(destDir));
        if (ScriptBytecodeAdapter.compareNotEqual(styleSheetFile, null)) {
            try {
                Object object2 = callSiteArray[166].callGetProperty(styleSheetFile);
                ScriptBytecodeAdapter.setProperty(object2, null, callSiteArray[167].callConstructor(File.class, destDir, "stylesheet.css"), "text");
            }
            catch (IOException e) {
                callSiteArray[168].callStatic(Main.class, callSiteArray[169].call(callSiteArray[170].call(callSiteArray[171].call((Object)"Warning: Unable to copy specified stylesheet '", callSiteArray[172].callGetProperty(styleSheetFile)), "'. Using default stylesheet instead. Due to: "), callSiteArray[173].callGetProperty(e)));
            }
        }
    }

    /*
     * WARNING - void declaration
     */
    public static Object collectSourceFileNames(List<String> remainingArgs, String[] sourceDirs, List<String> exclusions) {
        List list;
        void var2_2;
        Reference<String[]> sourceDirs2 = new Reference<String[]>(sourceDirs);
        Reference<void> exclusions2 = new Reference<void>(var2_2);
        CallSite[] callSiteArray = Main.$getCallSiteArray();
        sourceFilesToDoc = list = ScriptBytecodeAdapter.createList(new Object[0]);
        public class _collectSourceFileNames_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference exclusions;
            private /* synthetic */ Reference sourceDirs;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _collectSourceFileNames_closure3(Object _outerInstance, Object _thisObject, Reference exclusions, Reference sourceDirs) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _collectSourceFileNames_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.exclusions = reference2 = exclusions;
                this.sourceDirs = reference = sourceDirs;
            }

            public Object doCall(String pkgOrFile) {
                Reference<String> pkgOrFile2 = new Reference<String>(pkgOrFile);
                CallSite[] callSiteArray = _collectSourceFileNames_closure3.$getCallSiteArray();
                if (ScriptBytecodeAdapter.isCase(pkgOrFile2.get(), this.exclusions.get())) {
                    return null;
                }
                File srcFile = (File)ScriptBytecodeAdapter.castToType(callSiteArray[0].callConstructor(File.class, pkgOrFile2.get()), File.class);
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(srcFile)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(srcFile))) {
                    callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), pkgOrFile2.get());
                    return null;
                }
                public class _closure4
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference pkgOrFile;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure4(Object _outerInstance, Object _thisObject, Reference pkgOrFile) {
                        Reference reference;
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.pkgOrFile = reference = pkgOrFile;
                    }

                    public Object doCall(Object dirStr) {
                        Object object;
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        Object dir = callSiteArray[0].callConstructor(File.class, dirStr);
                        Reference<Object> pkgOrFileSlashes = new Reference<Object>(callSiteArray[1].call(this.pkgOrFile.get(), ".", "/"));
                        Object candidate = callSiteArray[2].callConstructor(File.class, dir, this.pkgOrFile.get());
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(candidate)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(candidate))) {
                            callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), this.pkgOrFile.get());
                        }
                        candidate = object = callSiteArray[7].callConstructor(File.class, dir, pkgOrFileSlashes.get());
                        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[8].call(candidate)) && DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call(candidate))) {
                            public class _closure5
                            extends Closure
                            implements GeneratedClosure {
                                private /* synthetic */ Reference pkgOrFileSlashes;
                                private static /* synthetic */ ClassInfo $staticClassInfo;
                                public static transient /* synthetic */ boolean __$stMC;
                                private static /* synthetic */ SoftReference $callSiteArray;

                                public _closure5(Object _outerInstance, Object _thisObject, Reference pkgOrFileSlashes) {
                                    Reference reference;
                                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                                    super(_outerInstance, _thisObject);
                                    this.pkgOrFileSlashes = reference = pkgOrFileSlashes;
                                }

                                public Object doCall(File f) {
                                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                                    return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].call(callSiteArray[3].call(this.pkgOrFileSlashes.get(), "/"), callSiteArray[4].call(f)));
                                }

                                public Object call(File f) {
                                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                                    return callSiteArray[5].callCurrent((GroovyObject)this, f);
                                }

                                public Object getPkgOrFileSlashes() {
                                    CallSite[] callSiteArray = _closure5.$getCallSiteArray();
                                    return this.pkgOrFileSlashes.get();
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
                                    stringArray[0] = "leftShift";
                                    stringArray[1] = "sourceFilesToDoc";
                                    stringArray[2] = "plus";
                                    stringArray[3] = "plus";
                                    stringArray[4] = "getName";
                                    stringArray[5] = "doCall";
                                }

                                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                    String[] stringArray = new String[6];
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
                            return callSiteArray[10].call(candidate, callSiteArray[11].callGetProperty(FileType.class), ScriptBytecodeAdapter.bitwiseNegate(".*\\.(?:groovy|java)"), new _closure5(this, this.getThisObject(), pkgOrFileSlashes));
                        }
                        return null;
                    }

                    public String getPkgOrFile() {
                        CallSite[] callSiteArray = _closure4.$getCallSiteArray();
                        return ShortTypeHandling.castToString(this.pkgOrFile.get());
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
                        stringArray[1] = "replace";
                        stringArray[2] = "<$constructor$>";
                        stringArray[3] = "exists";
                        stringArray[4] = "isFile";
                        stringArray[5] = "leftShift";
                        stringArray[6] = "sourceFilesToDoc";
                        stringArray[7] = "<$constructor$>";
                        stringArray[8] = "exists";
                        stringArray[9] = "isDirectory";
                        stringArray[10] = "eachFileMatch";
                        stringArray[11] = "FILES";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[12];
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
                return callSiteArray[5].call(this.sourceDirs.get(), new _closure4(this, this.getThisObject(), pkgOrFile2));
            }

            public Object call(String pkgOrFile) {
                Reference<String> pkgOrFile2 = new Reference<String>(pkgOrFile);
                CallSite[] callSiteArray = _collectSourceFileNames_closure3.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[6].callCurrent((GroovyObject)this, pkgOrFile2.get());
                }
                return this.doCall(pkgOrFile2.get());
            }

            public List getExclusions() {
                CallSite[] callSiteArray = _collectSourceFileNames_closure3.$getCallSiteArray();
                return (List)ScriptBytecodeAdapter.castToType(this.exclusions.get(), List.class);
            }

            public String[] getSourceDirs() {
                CallSite[] callSiteArray = _collectSourceFileNames_closure3.$getCallSiteArray();
                return (String[])ScriptBytecodeAdapter.castToType(this.sourceDirs.get(), String[].class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _collectSourceFileNames_closure3.class) {
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
                stringArray[1] = "exists";
                stringArray[2] = "isFile";
                stringArray[3] = "leftShift";
                stringArray[4] = "sourceFilesToDoc";
                stringArray[5] = "each";
                stringArray[6] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
                _collectSourceFileNames_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_collectSourceFileNames_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _collectSourceFileNames_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return callSiteArray[174].call(remainingArgs, new _collectSourceFileNames_closure3(Main.class, Main.class, exclusions2, sourceDirs2));
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

    static {
        boolean bl = false;
        debug = bl;
        Object object = Main.$getCallSiteArray()[175].callConstructor(MessageSource.class, Main.class);
        messages = (MessageSource)ScriptBytecodeAdapter.castToType(object, MessageSource.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "out";
        stringArray[3] = "help";
        stringArray[4] = "getAt";
        stringArray[5] = "_";
        stringArray[6] = "getAt";
        stringArray[7] = "verbose";
        stringArray[8] = "getAt";
        stringArray[9] = "quiet";
        stringArray[10] = "getAt";
        stringArray[11] = "_";
        stringArray[12] = "getAt";
        stringArray[13] = "classpath";
        stringArray[14] = "getAt";
        stringArray[15] = "cp";
        stringArray[16] = "getAt";
        stringArray[17] = "d";
        stringArray[18] = "getAt";
        stringArray[19] = "author";
        stringArray[20] = "getAt";
        stringArray[21] = "noscripts";
        stringArray[22] = "getAt";
        stringArray[23] = "nomainforscripts";
        stringArray[24] = "getAt";
        stringArray[25] = "notimestamp";
        stringArray[26] = "getAt";
        stringArray[27] = "noversionstamp";
        stringArray[28] = "getAt";
        stringArray[29] = "overview";
        stringArray[30] = "getAt";
        stringArray[31] = "public";
        stringArray[32] = "getAt";
        stringArray[33] = "protected";
        stringArray[34] = "getAt";
        stringArray[35] = "package";
        stringArray[36] = "getAt";
        stringArray[37] = "private";
        stringArray[38] = "getAt";
        stringArray[39] = "charset";
        stringArray[40] = "getAt";
        stringArray[41] = "fileEncoding";
        stringArray[42] = "getAt";
        stringArray[43] = "windowtitle";
        stringArray[44] = "getAt";
        stringArray[45] = "doctitle";
        stringArray[46] = "getAt";
        stringArray[47] = "header";
        stringArray[48] = "getAt";
        stringArray[49] = "footer";
        stringArray[50] = "getAt";
        stringArray[51] = "exclude";
        stringArray[52] = "getAt";
        stringArray[53] = "stylesheetfile";
        stringArray[54] = "getAt";
        stringArray[55] = "sourcepath";
        stringArray[56] = "getAt";
        stringArray[57] = "parse";
        stringArray[58] = "help";
        stringArray[59] = "usage";
        stringArray[60] = "version";
        stringArray[61] = "println";
        stringArray[62] = "out";
        stringArray[63] = "format";
        stringArray[64] = "version";
        stringArray[65] = "stylesheetfile";
        stringArray[66] = "<$constructor$>";
        stringArray[67] = "stylesheetfile";
        stringArray[68] = "overview";
        stringArray[69] = "<$constructor$>";
        stringArray[70] = "overview";
        stringArray[71] = "<$constructor$>";
        stringArray[72] = "d";
        stringArray[73] = "exclude";
        stringArray[74] = "tokenize";
        stringArray[75] = "exclude";
        stringArray[76] = "sourcepath";
        stringArray[77] = "each";
        stringArray[78] = "sourcepaths";
        stringArray[79] = "toArray";
        stringArray[80] = "valueOf";
        stringArray[81] = "author";
        stringArray[82] = "valueOf";
        stringArray[83] = "noscripts";
        stringArray[84] = "valueOf";
        stringArray[85] = "nomainforscripts";
        stringArray[86] = "valueOf";
        stringArray[87] = "notimestamp";
        stringArray[88] = "valueOf";
        stringArray[89] = "noversionstamp";
        stringArray[90] = "valueOf";
        stringArray[91] = "package";
        stringArray[92] = "valueOf";
        stringArray[93] = "private";
        stringArray[94] = "valueOf";
        stringArray[95] = "protected";
        stringArray[96] = "valueOf";
        stringArray[97] = "public";
        stringArray[98] = "next";
        stringArray[99] = "next";
        stringArray[100] = "next";
        stringArray[101] = "next";
        stringArray[102] = "println";
        stringArray[103] = "err";
        stringArray[104] = "usage";
        stringArray[105] = "println";
        stringArray[106] = "err";
        stringArray[107] = "usage";
        stringArray[108] = "windowtitle";
        stringArray[109] = "doctitle";
        stringArray[110] = "header";
        stringArray[111] = "footer";
        stringArray[112] = "charset";
        stringArray[113] = "fileEncoding";
        stringArray[114] = "Ds";
        stringArray[115] = "Ds";
        stringArray[116] = "each";
        stringArray[117] = "verbose";
        stringArray[118] = "VERBOSE";
        stringArray[119] = "debug";
        stringArray[120] = "DEBUG";
        stringArray[121] = "quiet";
        stringArray[122] = "QUIET";
        stringArray[123] = "arguments";
        stringArray[124] = "println";
        stringArray[125] = "err";
        stringArray[126] = "usage";
        stringArray[127] = "execute";
        stringArray[128] = "<$constructor$>";
        stringArray[129] = "put";
        stringArray[130] = "put";
        stringArray[131] = "put";
        stringArray[132] = "put";
        stringArray[133] = "put";
        stringArray[134] = "put";
        stringArray[135] = "put";
        stringArray[136] = "toString";
        stringArray[137] = "put";
        stringArray[138] = "toString";
        stringArray[139] = "put";
        stringArray[140] = "toString";
        stringArray[141] = "put";
        stringArray[142] = "toString";
        stringArray[143] = "put";
        stringArray[144] = "toString";
        stringArray[145] = "put";
        stringArray[146] = "toString";
        stringArray[147] = "put";
        stringArray[148] = "toString";
        stringArray[149] = "put";
        stringArray[150] = "toString";
        stringArray[151] = "put";
        stringArray[152] = "toString";
        stringArray[153] = "put";
        stringArray[154] = "absolutePath";
        stringArray[155] = "<$constructor$>";
        stringArray[156] = "collectSourceFileNames";
        stringArray[157] = "<$constructor$>";
        stringArray[158] = "<$constructor$>";
        stringArray[159] = "DEFAULT_DOC_TEMPLATES";
        stringArray[160] = "DEFAULT_PACKAGE_TEMPLATES";
        stringArray[161] = "DEFAULT_CLASS_TEMPLATES";
        stringArray[162] = "add";
        stringArray[163] = "<$constructor$>";
        stringArray[164] = "renderToOutput";
        stringArray[165] = "canonicalPath";
        stringArray[166] = "text";
        stringArray[167] = "<$constructor$>";
        stringArray[168] = "println";
        stringArray[169] = "plus";
        stringArray[170] = "plus";
        stringArray[171] = "plus";
        stringArray[172] = "absolutePath";
        stringArray[173] = "message";
        stringArray[174] = "each";
        stringArray[175] = "<$constructor$>";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[176];
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


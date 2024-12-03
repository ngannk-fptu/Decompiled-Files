/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.AggregateCompleter
 *  jline.console.completer.ArgumentCompleter
 *  jline.console.completer.Completer
 *  jline.console.completer.StringsCompleter
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.GroovySystem;
import groovy.lang.MetaClass;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;
import jline.console.completer.AggregateCompleter;
import jline.console.completer.ArgumentCompleter;
import jline.console.completer.Completer;
import jline.console.completer.StringsCompleter;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.commands.ImportCompleter;

public class DocCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":doc";
    private static final String ENV_BROWSER = "BROWSER";
    private static final String ENV_BROWSER_GROOVYSH = "GROOVYSH_BROWSER";
    private static final int TIMEOUT_CONN;
    private static final int TIMEOUT_READ;
    private static boolean hasAWTDesktopPlatformSupport;
    private static Object desktop;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public DocCommand(Groovysh shell) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":D");
    }

    static {
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = DocCommand.$getCallSiteArray()[0].call((Object)5, 1000);
            TIMEOUT_CONN = DefaultTypeTransformation.intUnbox(object);
        } else {
            int n;
            TIMEOUT_CONN = n = 5 * 1000;
        }
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = DocCommand.$getCallSiteArray()[1].call((Object)5, 1000);
            TIMEOUT_READ = DefaultTypeTransformation.intUnbox(object);
        } else {
            int n;
            TIMEOUT_READ = n = 5 * 1000;
        }
        try {
            boolean bl;
            Object object;
            Class<?> desktopClass = Class.forName("java.awt.Desktop");
            desktop = object = DefaultTypeTransformation.booleanUnbox(DocCommand.$getCallSiteArray()[2].callGetProperty(desktopClass)) ? DocCommand.$getCallSiteArray()[3].callGetProperty(desktopClass) : null;
            public class __clinit__closure1
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public __clinit__closure1(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = __clinit__closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = __clinit__closure1.$getCallSiteArray();
                    return ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), "Action");
                }

                public Object doCall() {
                    CallSite[] callSiteArray = __clinit__closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != __clinit__closure1.class) {
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
                    stringArray[0] = "simpleName";
                    return new CallSiteArray(__clinit__closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = __clinit__closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            hasAWTDesktopPlatformSupport = bl = ScriptBytecodeAdapter.compareNotEqual(desktop, null) && DefaultTypeTransformation.booleanUnbox(DocCommand.$getCallSiteArray()[4].call(desktop, DocCommand.$getCallSiteArray()[5].callGetProperty(DocCommand.$getCallSiteArray()[6].call(DocCommand.$getCallSiteArray()[7].callGetProperty(desktopClass), new __clinit__closure1(DocCommand.class, DocCommand.class)))));
        }
        catch (Exception e) {
            boolean bl;
            hasAWTDesktopPlatformSupport = bl = false;
            Object var9_9 = null;
            desktop = var9_9;
        }
    }

    @Override
    public Completer getCompleter() {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        return (Completer)ScriptBytecodeAdapter.castToType(callSiteArray[8].callConstructor(AggregateCompleter.class, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[9].callConstructor(ArgumentCompleter.class, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[10].callConstructor(StringsCompleter.class, callSiteArray[11].callGroovyObjectGetProperty(this), callSiteArray[12].callGroovyObjectGetProperty(this)), callSiteArray[13].callConstructor(ImportCompleter.class, callSiteArray[14].callGroovyObjectGetProperty(callSiteArray[15].callGroovyObjectGetProperty(this)), callSiteArray[16].callGroovyObjectGetProperty(callSiteArray[17].callGroovyObjectGetProperty(this)), false)}))})), Completer.class);
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[18].callSafe(args), 1)) {
            callSiteArray[19].callCurrent((GroovyObject)this, callSiteArray[20].call(args, 0));
            return null;
        }
        return callSiteArray[21].callCurrent((GroovyObject)this, callSiteArray[22].call(callSiteArray[23].callGroovyObjectGetProperty(this), "error.unexpected_args", DefaultTypeTransformation.booleanUnbox(args) ? callSiteArray[24].call(args, " ") : "no arguments"));
    }

    public void doc(String className) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        Object normalizedClassName = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object;
            normalizedClassName = object = callSiteArray[25].callCurrent((GroovyObject)this, className);
        } else {
            String string = this.normalizeClassName(className);
            normalizedClassName = string;
        }
        Object urls = callSiteArray[26].callCurrent((GroovyObject)this, normalizedClassName);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[27].callGetProperty(urls))) {
            callSiteArray[28].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{normalizedClassName}, new String[]{"Documentation for \"", "\" could not be found."}));
        }
        public class _doc_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _doc_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _doc_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object url) {
                CallSite[] callSiteArray = _doc_closure2.$getCallSiteArray();
                return callSiteArray[0].call(callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(this)), url);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _doc_closure2.class) {
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
                stringArray[1] = "out";
                stringArray[2] = "io";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _doc_closure2.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_doc_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _doc_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[29].call(urls, new _doc_closure2(this, this));
        callSiteArray[30].callCurrent((GroovyObject)this, urls);
    }

    protected String normalizeClassName(String className) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[31].call(callSiteArray[32].call(className, "\"", ""), "'", ""));
    }

    protected void browse(List urls) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        Object browser = callSiteArray[33].callGroovyObjectGetProperty(this);
        if (DefaultTypeTransformation.booleanUnbox(browser)) {
            callSiteArray[34].callCurrent(this, browser, urls);
        } else if (hasAWTDesktopPlatformSupport) {
            callSiteArray[35].callCurrent((GroovyObject)this, urls);
        } else {
            callSiteArray[36].callCurrent((GroovyObject)this, callSiteArray[37].call(callSiteArray[38].call((Object)"Browser could not be opened due to missing platform support for \"java.awt.Desktop\". Please set ", new GStringImpl(new Object[]{ENV_BROWSER_GROOVYSH, ENV_BROWSER}, new String[]{"a ", " or ", " environment variable referring to the browser binary to "})), "solve this issue."));
        }
    }

    protected String getBrowserEnvironmentVariable() {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        Object object = callSiteArray[39].call(System.class, ENV_BROWSER_GROOVYSH);
        return ShortTypeHandling.castToString(DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[40].call(System.class, ENV_BROWSER));
    }

    protected void browseWithAWT(List urls) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        try {
            public class _browseWithAWT_closure3
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _browseWithAWT_closure3(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _browseWithAWT_closure3.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object url) {
                    CallSite[] callSiteArray = _browseWithAWT_closure3.$getCallSiteArray();
                    return callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), callSiteArray[2].call(url));
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _browseWithAWT_closure3.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "browse";
                    stringArray[1] = "desktop";
                    stringArray[2] = "toURI";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[3];
                    _browseWithAWT_closure3.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_browseWithAWT_closure3.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _browseWithAWT_closure3.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[41].call((Object)urls, new _browseWithAWT_closure3(this, this));
        }
        catch (Exception e) {
            callSiteArray[42].callCurrent((GroovyObject)this, callSiteArray[43].call((Object)new GStringImpl(new Object[]{e}, new String[]{"Browser could not be opened, an unexpected error occured (", "). You can add a "}), new GStringImpl(new Object[]{ENV_BROWSER_GROOVYSH, ENV_BROWSER}, new String[]{"", " or ", " environment variable to explicitly specify a browser binary."})));
        }
    }

    protected void browseWithNativeBrowser(String browser, List urls) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        try {
            callSiteArray[44].call(new GStringImpl(new Object[]{browser, callSiteArray[45].call((Object)urls, " ")}, new String[]{"", " ", ""}));
        }
        catch (Exception e) {
            callSiteArray[46].callCurrent((GroovyObject)this, callSiteArray[47].call((Object)new GStringImpl(new Object[]{e, ENV_BROWSER_GROOVYSH, ENV_BROWSER}, new String[]{"Browser could not be opened (", "). Please check the ", " or ", " "}), "environment variable."));
        }
    }

    protected List urlsFor(String className) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        String groovyVersion = ShortTypeHandling.castToString(callSiteArray[48].callGetProperty(GroovySystem.class));
        Object path = callSiteArray[49].call(callSiteArray[50].call(className, "\\.", "/"), ".html");
        List urls = ScriptBytecodeAdapter.createList(new Object[0]);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[51].call((Object)className, "^(groovy|org\\.codehaus\\.groovy|)\\..+"))) {
            Object url = callSiteArray[52].callConstructor(URL.class, new GStringImpl(new Object[]{groovyVersion, path}, new String[]{"http://docs.groovy-lang.org/", "/html/gapi/", ""}));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[53].callCurrent((GroovyObject)this, url))) {
                callSiteArray[54].call((Object)urls, url);
            }
        } else {
            Object object;
            Object object2;
            Object url = null;
            url = __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (object2 = callSiteArray[55].callConstructor(URL.class, new GStringImpl(new Object[]{callSiteArray[56].callStatic(DocCommand.class), path}, new String[]{"http://docs.oracle.com/javase/", "/docs/api/", ""}))) : (object = callSiteArray[57].callConstructor(URL.class, new GStringImpl(new Object[]{DocCommand.simpleVersion(), path}, new String[]{"http://docs.oracle.com/javase/", "/docs/api/", ""})));
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[58].callCurrent((GroovyObject)this, url))) {
                Object object3;
                callSiteArray[59].call((Object)urls, url);
                url = object3 = callSiteArray[60].callConstructor(URL.class, new GStringImpl(new Object[]{groovyVersion, path}, new String[]{"http://docs.groovy-lang.org/", "/html/groovy-jdk/", ""}));
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[61].callCurrent((GroovyObject)this, url))) {
                    callSiteArray[62].call((Object)urls, url);
                }
            }
        }
        return (List)ScriptBytecodeAdapter.castToType(urls, List.class);
    }

    private static Object simpleVersion() {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        String javaVersion = ShortTypeHandling.castToString(callSiteArray[63].call(System.class, "java.version"));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[64].call((Object)javaVersion, "1."))) {
            return callSiteArray[65].call(callSiteArray[66].call((Object)javaVersion, "\\."), 1);
        }
        return callSiteArray[67].call(callSiteArray[68].call(callSiteArray[69].call(javaVersion, "-.*", ""), "\\."), 0);
    }

    protected boolean sendHEADRequest(URL url) {
        CallSite[] callSiteArray = DocCommand.$getCallSiteArray();
        HttpURLConnection conn = (HttpURLConnection)ScriptBytecodeAdapter.asType(callSiteArray[70].call(url), HttpURLConnection.class);
        String string = "HEAD";
        ScriptBytecodeAdapter.setProperty(string, null, conn, "requestMethod");
        int n = TIMEOUT_CONN;
        ScriptBytecodeAdapter.setProperty(n, null, conn, "connectTimeout");
        int n2 = TIMEOUT_READ;
        ScriptBytecodeAdapter.setProperty(n2, null, conn, "readTimeout");
        boolean bl = true;
        ScriptBytecodeAdapter.setProperty(bl, null, conn, "instanceFollowRedirects");
        boolean bl2 = ScriptBytecodeAdapter.compareEqual(callSiteArray[71].callGetProperty(conn), 200);
        try {
            return bl2;
        }
        catch (IOException e) {
            boolean bl3 = DefaultTypeTransformation.booleanUnbox(callSiteArray[72].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{url, e}, new String[]{"Sending a HEAD request to ", " failed (", "). Please check your network settings."})));
            return bl3;
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != DocCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Completer super$2$getCompleter() {
        return super.getCompleter();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "multiply";
        stringArray[1] = "multiply";
        stringArray[2] = "desktopSupported";
        stringArray[3] = "desktop";
        stringArray[4] = "isSupported";
        stringArray[5] = "BROWSE";
        stringArray[6] = "find";
        stringArray[7] = "declaredClasses";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "name";
        stringArray[12] = "shortcut";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "packageHelper";
        stringArray[15] = "shell";
        stringArray[16] = "interp";
        stringArray[17] = "shell";
        stringArray[18] = "size";
        stringArray[19] = "doc";
        stringArray[20] = "getAt";
        stringArray[21] = "fail";
        stringArray[22] = "format";
        stringArray[23] = "messages";
        stringArray[24] = "join";
        stringArray[25] = "normalizeClassName";
        stringArray[26] = "urlsFor";
        stringArray[27] = "empty";
        stringArray[28] = "fail";
        stringArray[29] = "each";
        stringArray[30] = "browse";
        stringArray[31] = "replaceAll";
        stringArray[32] = "replaceAll";
        stringArray[33] = "browserEnvironmentVariable";
        stringArray[34] = "browseWithNativeBrowser";
        stringArray[35] = "browseWithAWT";
        stringArray[36] = "fail";
        stringArray[37] = "plus";
        stringArray[38] = "plus";
        stringArray[39] = "getenv";
        stringArray[40] = "getenv";
        stringArray[41] = "each";
        stringArray[42] = "fail";
        stringArray[43] = "plus";
        stringArray[44] = "execute";
        stringArray[45] = "join";
        stringArray[46] = "fail";
        stringArray[47] = "plus";
        stringArray[48] = "version";
        stringArray[49] = "plus";
        stringArray[50] = "replaceAll";
        stringArray[51] = "matches";
        stringArray[52] = "<$constructor$>";
        stringArray[53] = "sendHEADRequest";
        stringArray[54] = "leftShift";
        stringArray[55] = "<$constructor$>";
        stringArray[56] = "simpleVersion";
        stringArray[57] = "<$constructor$>";
        stringArray[58] = "sendHEADRequest";
        stringArray[59] = "leftShift";
        stringArray[60] = "<$constructor$>";
        stringArray[61] = "sendHEADRequest";
        stringArray[62] = "leftShift";
        stringArray[63] = "getProperty";
        stringArray[64] = "startsWith";
        stringArray[65] = "getAt";
        stringArray[66] = "split";
        stringArray[67] = "getAt";
        stringArray[68] = "split";
        stringArray[69] = "replaceAll";
        stringArray[70] = "openConnection";
        stringArray[71] = "responseCode";
        stringArray[72] = "fail";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[73];
        DocCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(DocCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = DocCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


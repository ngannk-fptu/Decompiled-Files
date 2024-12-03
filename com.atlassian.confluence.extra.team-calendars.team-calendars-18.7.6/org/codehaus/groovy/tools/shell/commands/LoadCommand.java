/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.File;
import java.lang.ref.SoftReference;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
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
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.FileNameCompleter;

public class LoadCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":load";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public LoadCommand(Groovysh shell) {
        CallSite[] callSiteArray = LoadCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":l");
        callSiteArray[0].callCurrent(this, ".", ":.");
    }

    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = LoadCommand.$getCallSiteArray();
        return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[1].callConstructor(FileNameCompleter.class, true, true)});
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = LoadCommand.$getCallSiteArray();
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
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[2].call(args), 0)) {
            callSiteArray[3].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{COMMAND_NAME}, new String[]{"Command '", "' requires at least one argument"}));
        }
        Object source = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(args), Iterator.class);
        while (iterator.hasNext()) {
            source = iterator.next();
            URL url = null;
            callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{url}, new String[]{"Attempting to load: \"", "\""}));
            try {
                Object object = callSiteArray[7].callConstructor(URL.class, new GStringImpl(new Object[]{source}, new String[]{"", ""}));
                url = (URL)ScriptBytecodeAdapter.castToType(object, URL.class);
            }
            catch (MalformedURLException e) {
                Object file = callSiteArray[8].callConstructor(File.class, new GStringImpl(new Object[]{source}, new String[]{"", ""}));
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[9].call(file))) {
                    callSiteArray[10].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{file}, new String[]{"File not found: \"", "\""}));
                }
                Object object = callSiteArray[11].call(callSiteArray[12].call(file));
                url = (URL)ScriptBytecodeAdapter.castToType(object, URL.class);
            }
            callSiteArray[13].callCurrent((GroovyObject)this, url);
        }
        return null;
    }

    public void load(URL url) {
        CallSite[] callSiteArray = LoadCommand.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            URL uRL = url;
            valueRecorder.record(uRL, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(uRL, null);
            valueRecorder.record(bl, 12);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert url != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[14].callGetProperty(callSiteArray[15].callGroovyObjectGetProperty(this)))) {
            callSiteArray[16].call(callSiteArray[17].callGetProperty(callSiteArray[18].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{url}, new String[]{"Loading: ", ""}));
        }
        public class _load_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _load_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _load_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(String it, int lineNumber) {
                CallSite[] callSiteArray = _load_closure1.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? lineNumber == 1 && DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call((Object)it, "#!")) : lineNumber == 1 && DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call((Object)it, "#!"))) {
                    return null;
                }
                return (String)ScriptBytecodeAdapter.asType(callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), it), String.class);
            }

            public Object call(String it, int lineNumber) {
                CallSite[] callSiteArray = _load_closure1.$getCallSiteArray();
                if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return callSiteArray[4].callCurrent(this, it, lineNumber);
                }
                return this.doCall(it, lineNumber);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _load_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "startsWith";
                stringArray[1] = "startsWith";
                stringArray[2] = "leftShift";
                stringArray[3] = "shell";
                stringArray[4] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[5];
                _load_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_load_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _load_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[19].call((Object)url, new _load_closure1(this, this));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != LoadCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ List super$2$createCompleters() {
        return super.createCompleters();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "alias";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "size";
        stringArray[3] = "fail";
        stringArray[4] = "iterator";
        stringArray[5] = "debug";
        stringArray[6] = "log";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "exists";
        stringArray[10] = "fail";
        stringArray[11] = "toURL";
        stringArray[12] = "toURI";
        stringArray[13] = "load";
        stringArray[14] = "verbose";
        stringArray[15] = "io";
        stringArray[16] = "println";
        stringArray[17] = "out";
        stringArray[18] = "io";
        stringArray[19] = "eachLine";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[20];
        LoadCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(LoadCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = LoadCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


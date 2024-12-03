/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.File;
import java.lang.ref.SoftReference;
import java.math.BigDecimal;
import java.util.Iterator;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.util.Preferences;

public class EditCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":edit";
    private static final /* synthetic */ BigDecimal $const$0;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public EditCommand(Groovysh shell) {
        CallSite[] callSiteArray = EditCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":e");
    }

    public ProcessBuilder getEditorProcessBuilder(String editCommand, String tempFilename) {
        CallSite[] callSiteArray = EditCommand.$getCallSiteArray();
        Object pb = callSiteArray[0].callConstructor(ProcessBuilder.class, editCommand, tempFilename);
        callSiteArray[1].call(pb, true);
        Object javaVer = callSiteArray[2].call(Double.class, callSiteArray[3].call(System.class, "java.specification.version"));
        if (ScriptBytecodeAdapter.compareGreaterThanEqual(javaVer, $const$0)) {
            callSiteArray[4].call(pb, callSiteArray[5].callGetProperty(ProcessBuilder.Redirect.class));
            callSiteArray[6].call(pb, callSiteArray[7].callGetProperty(ProcessBuilder.Redirect.class));
        }
        return (ProcessBuilder)ScriptBytecodeAdapter.castToType(pb, ProcessBuilder.class);
    }

    private String getEditorCommand() {
        CallSite[] callSiteArray = EditCommand.$getCallSiteArray();
        Object editor = callSiteArray[8].callGetProperty(Preferences.class);
        callSiteArray[9].call(callSiteArray[10].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{editor}, new String[]{"Using editor: ", ""}));
        if (!DefaultTypeTransformation.booleanUnbox(editor)) {
            callSiteArray[11].callCurrent((GroovyObject)this, "Unable to determine which editor to use; check $EDITOR");
        }
        return ShortTypeHandling.castToString(editor);
    }

    @Override
    public Object execute(List<String> args) {
        Object object;
        CallSite[] callSiteArray = EditCommand.$getCallSiteArray();
        callSiteArray[12].callCurrent((GroovyObject)this, args);
        File file = (File)ScriptBytecodeAdapter.castToType(callSiteArray[13].call(File.class, "groovysh-buffer", ".groovy"), File.class);
        callSiteArray[14].call(file);
        try {
            callSiteArray[15].call((Object)file, callSiteArray[16].call(callSiteArray[17].callGroovyObjectGetProperty(this), callSiteArray[18].callGroovyObjectGetProperty(this)));
            callSiteArray[19].call(callSiteArray[20].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[21].callGroovyObjectGetProperty(this), file}, new String[]{"Executing: ", " ", ""}));
            Object pb = callSiteArray[22].callCurrent(this, new GStringImpl(new Object[]{callSiteArray[23].callGroovyObjectGetProperty(this)}, new String[]{"", ""}), new GStringImpl(new Object[]{file}, new String[]{"", ""}));
            Object p = callSiteArray[24].call(pb);
            callSiteArray[25].call(callSiteArray[26].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{p}, new String[]{"Waiting for process: ", ""}));
            callSiteArray[27].call(p);
            callSiteArray[28].call(callSiteArray[29].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[30].callGetProperty(file)}, new String[]{"Editor contents: ", ""}));
            object = callSiteArray[31].callCurrent((GroovyObject)this, callSiteArray[32].call(file));
        }
        catch (Throwable throwable) {
            callSiteArray[35].call(file);
            throw throwable;
        }
        callSiteArray[33].call(file);
        return object;
    }

    public void replaceCurrentBuffer(List<String> contents) {
        CallSite[] callSiteArray = EditCommand.$getCallSiteArray();
        callSiteArray[36].call(callSiteArray[37].callGroovyObjectGetProperty(callSiteArray[38].callGroovyObjectGetProperty(this)));
        String line = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[39].call(contents), Iterator.class);
        while (iterator.hasNext()) {
            line = ShortTypeHandling.castToString(iterator.next());
            callSiteArray[40].call(callSiteArray[41].callGroovyObjectGetProperty(this), line);
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != EditCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public static /* synthetic */ void __$swapInit() {
        BigDecimal bigDecimal;
        CallSite[] callSiteArray = EditCommand.$getCallSiteArray();
        $callSiteArray = null;
        $const$0 = bigDecimal = new BigDecimal("1.7");
    }

    static {
        EditCommand.__$swapInit();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "redirectErrorStream";
        stringArray[2] = "valueOf";
        stringArray[3] = "getProperty";
        stringArray[4] = "redirectInput";
        stringArray[5] = "INHERIT";
        stringArray[6] = "redirectOutput";
        stringArray[7] = "INHERIT";
        stringArray[8] = "editor";
        stringArray[9] = "debug";
        stringArray[10] = "log";
        stringArray[11] = "fail";
        stringArray[12] = "assertNoArguments";
        stringArray[13] = "createTempFile";
        stringArray[14] = "deleteOnExit";
        stringArray[15] = "write";
        stringArray[16] = "join";
        stringArray[17] = "buffer";
        stringArray[18] = "NEWLINE";
        stringArray[19] = "debug";
        stringArray[20] = "log";
        stringArray[21] = "editorCommand";
        stringArray[22] = "getEditorProcessBuilder";
        stringArray[23] = "editorCommand";
        stringArray[24] = "start";
        stringArray[25] = "debug";
        stringArray[26] = "log";
        stringArray[27] = "waitFor";
        stringArray[28] = "debug";
        stringArray[29] = "log";
        stringArray[30] = "text";
        stringArray[31] = "replaceCurrentBuffer";
        stringArray[32] = "readLines";
        stringArray[33] = "delete";
        stringArray[34] = "delete";
        stringArray[35] = "delete";
        stringArray[36] = "clearSelected";
        stringArray[37] = "buffers";
        stringArray[38] = "shell";
        stringArray[39] = "iterator";
        stringArray[40] = "execute";
        stringArray[41] = "shell";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[42];
        EditCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(EditCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = EditCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


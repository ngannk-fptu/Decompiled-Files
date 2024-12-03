/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 *  jline.console.completer.FileNameCompleter
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.io.File;
import java.lang.ref.SoftReference;
import java.util.List;
import jline.console.completer.Completer;
import jline.console.completer.FileNameCompleter;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;

public class SaveCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":save";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public SaveCommand(Groovysh shell) {
        CallSite[] callSiteArray = SaveCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":s");
    }

    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = SaveCommand.$getCallSiteArray();
        return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[0].callConstructor(FileNameCompleter.class), null});
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = SaveCommand.$getCallSiteArray();
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
        if (ScriptBytecodeAdapter.compareNotEqual(callSiteArray[1].call(args), 1)) {
            callSiteArray[2].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{COMMAND_NAME}, new String[]{"Command '", "' requires a single file argument"}));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this)))) {
            callSiteArray[5].call(callSiteArray[6].callGetProperty(callSiteArray[7].callGroovyObjectGetProperty(this)), "Buffer is empty");
            return null;
        }
        Object file = callSiteArray[8].callConstructor(File.class, new GStringImpl(new Object[]{callSiteArray[9].call(args, 0)}, new String[]{"", ""}));
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[10].callGetProperty(callSiteArray[11].callGroovyObjectGetProperty(this)))) {
            callSiteArray[12].call(callSiteArray[13].callGetProperty(callSiteArray[14].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{file}, new String[]{"Saving current buffer to file: \"", "\""}));
        }
        Object dir = callSiteArray[15].callGetProperty(file);
        if (DefaultTypeTransformation.booleanUnbox(dir) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[16].call(dir))) {
            callSiteArray[17].call(callSiteArray[18].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{dir}, new String[]{"Creating parent directory path: \"", "\""}));
            callSiteArray[19].call(dir);
        }
        return callSiteArray[20].call(file, callSiteArray[21].call(callSiteArray[22].callGroovyObjectGetProperty(this), callSiteArray[23].callGroovyObjectGetProperty(this)));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != SaveCommand.class) {
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
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "size";
        stringArray[2] = "fail";
        stringArray[3] = "isEmpty";
        stringArray[4] = "buffer";
        stringArray[5] = "println";
        stringArray[6] = "out";
        stringArray[7] = "io";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "getAt";
        stringArray[10] = "verbose";
        stringArray[11] = "io";
        stringArray[12] = "println";
        stringArray[13] = "out";
        stringArray[14] = "io";
        stringArray[15] = "parentFile";
        stringArray[16] = "exists";
        stringArray[17] = "debug";
        stringArray[18] = "log";
        stringArray[19] = "mkdirs";
        stringArray[20] = "write";
        stringArray[21] = "join";
        stringArray[22] = "buffer";
        stringArray[23] = "NEWLINE";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[24];
        SaveCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(SaveCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = SaveCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


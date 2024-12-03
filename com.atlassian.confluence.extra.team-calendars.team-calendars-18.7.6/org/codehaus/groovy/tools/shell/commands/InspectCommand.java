/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.inspect.swingui.ObjectBrowser;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.awt.Frame;
import java.awt.HeadlessException;
import java.lang.ref.SoftReference;
import java.util.List;
import javax.swing.UIManager;
import jline.console.completer.Completer;
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
import org.codehaus.groovy.tools.shell.commands.InspectCommandCompletor;

public class InspectCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":inspect";
    private Object lafInitialized;
    private Object headless;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public InspectCommand(Groovysh shell) {
        CallSite[] callSiteArray = InspectCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":n");
        boolean bl = false;
        this.lafInitialized = bl;
    }

    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = InspectCommand.$getCallSiteArray();
        return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[0].callConstructor(InspectCommandCompletor.class, callSiteArray[1].callGroovyObjectGetProperty(this)), null});
    }

    @Override
    public Object execute(List<String> args) {
        Object object;
        Object object2;
        CallSite[] callSiteArray = InspectCommand.$getCallSiteArray();
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
        callSiteArray[2].call(callSiteArray[3].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{args}, new String[]{"Inspecting w/args: ", ""}));
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[4].call(args), 1)) {
            callSiteArray[5].callCurrent((GroovyObject)this, callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this), "error.unexpected_args", callSiteArray[8].call(args, " ")));
        }
        Object subject = null;
        subject = ScriptBytecodeAdapter.compareEqual(callSiteArray[9].call(args), 1) ? (object2 = callSiteArray[10].call(callSiteArray[11].callGetProperty(callSiteArray[12].callGroovyObjectGetProperty(this)), callSiteArray[13].call(args, 0))) : (object = callSiteArray[14].call(callSiteArray[15].callGetProperty(callSiteArray[16].callGroovyObjectGetProperty(this)), "_"));
        if (!DefaultTypeTransformation.booleanUnbox(subject)) {
            return callSiteArray[17].call(callSiteArray[18].callGetProperty(callSiteArray[19].callGroovyObjectGetProperty(this)), "Subject is null; nothing to inspect");
        }
        if (!DefaultTypeTransformation.booleanUnbox(this.lafInitialized)) {
            boolean bl = true;
            this.lafInitialized = bl;
            try {
                callSiteArray[20].call(UIManager.class, callSiteArray[21].callGetProperty(UIManager.class));
                callSiteArray[22].call(callSiteArray[23].callConstructor(Frame.class));
                boolean bl2 = false;
                this.headless = bl2;
            }
            catch (HeadlessException he) {
                boolean bl3 = true;
                this.headless = bl3;
            }
        }
        if (DefaultTypeTransformation.booleanUnbox(this.headless)) {
            callSiteArray[24].call(callSiteArray[25].callGetProperty(callSiteArray[26].callGroovyObjectGetProperty(this)), "@|red ERROR:|@ Running in AWT Headless mode, 'inspect' is not available.");
            return null;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[27].callGetProperty(callSiteArray[28].callGroovyObjectGetProperty(this)))) {
            callSiteArray[29].call(callSiteArray[30].callGetProperty(callSiteArray[31].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{subject}, new String[]{"Launching object browser to inspect: ", ""}));
        }
        return callSiteArray[32].call(ObjectBrowser.class, subject);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != InspectCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public Object getLafInitialized() {
        return this.lafInitialized;
    }

    public void setLafInitialized(Object object) {
        this.lafInitialized = object;
    }

    public Object getHeadless() {
        return this.headless;
    }

    public void setHeadless(Object object) {
        this.headless = object;
    }

    public /* synthetic */ List super$2$createCompleters() {
        return super.createCompleters();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "binding";
        stringArray[2] = "debug";
        stringArray[3] = "log";
        stringArray[4] = "size";
        stringArray[5] = "fail";
        stringArray[6] = "format";
        stringArray[7] = "messages";
        stringArray[8] = "join";
        stringArray[9] = "size";
        stringArray[10] = "getAt";
        stringArray[11] = "variables";
        stringArray[12] = "binding";
        stringArray[13] = "getAt";
        stringArray[14] = "getAt";
        stringArray[15] = "variables";
        stringArray[16] = "binding";
        stringArray[17] = "println";
        stringArray[18] = "out";
        stringArray[19] = "io";
        stringArray[20] = "setLookAndFeel";
        stringArray[21] = "systemLookAndFeelClassName";
        stringArray[22] = "dispose";
        stringArray[23] = "<$constructor$>";
        stringArray[24] = "println";
        stringArray[25] = "err";
        stringArray[26] = "io";
        stringArray[27] = "verbose";
        stringArray[28] = "io";
        stringArray[29] = "println";
        stringArray[30] = "out";
        stringArray[31] = "io";
        stringArray[32] = "inspect";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[33];
        InspectCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(InspectCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = InspectCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


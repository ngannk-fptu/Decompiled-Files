/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;

public class RegisterCommand
extends CommandSupport {
    private static final String COMMAND_NAME = ":register";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public RegisterCommand(Groovysh shell) {
        CallSite[] callSiteArray = RegisterCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":rc");
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = RegisterCommand.$getCallSiteArray();
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
        if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[0].call(args), 1)) {
            callSiteArray[1].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{COMMAND_NAME}, new String[]{"Command '", "' requires at least 1 arguments"}));
        }
        String classname = ShortTypeHandling.castToString(callSiteArray[2].call(args, 0));
        Class type = ShortTypeHandling.castToClass(callSiteArray[3].call(callSiteArray[4].callGroovyObjectGetProperty(this), classname));
        Command command = null;
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[5].call(args), 1)) {
            Command command2;
            command = command2 = (Command)ScriptBytecodeAdapter.asType(callSiteArray[6].call((Object)type, callSiteArray[7].callGroovyObjectGetProperty(this)), Command.class);
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[8].call(args), 2)) {
            Command command3;
            command = command3 = (Command)ScriptBytecodeAdapter.asType(callSiteArray[9].call(type, callSiteArray[10].callGroovyObjectGetProperty(this), callSiteArray[11].call(args, 1), null), Command.class);
        } else if (ScriptBytecodeAdapter.compareEqual(callSiteArray[12].call(args), 3)) {
            Command command4;
            command = command4 = (Command)ScriptBytecodeAdapter.asType(callSiteArray[13].call(type, callSiteArray[14].callGroovyObjectGetProperty(this), callSiteArray[15].call(args, 1), callSiteArray[16].call(args, 2)), Command.class);
        }
        Object oldcommand = callSiteArray[17].call(callSiteArray[18].callGroovyObjectGetProperty(this), callSiteArray[19].callGetProperty(command));
        if (DefaultTypeTransformation.booleanUnbox(oldcommand)) {
            callSiteArray[20].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[21].callGetProperty(command)}, new String[]{"Can not rebind command: ", ""}));
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[22].callGetProperty(callSiteArray[23].callGroovyObjectGetProperty(this)))) {
            callSiteArray[24].call(callSiteArray[25].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[26].callGetProperty(command), command}, new String[]{"Created command '", "': ", ""}));
        }
        Object object = callSiteArray[27].call(callSiteArray[28].callGroovyObjectGetProperty(this), command);
        command = (Command)ScriptBytecodeAdapter.castToType(object, Command.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[29].callGroovyObjectGetProperty(callSiteArray[30].callGroovyObjectGetProperty(this)))) {
            return callSiteArray[31].call(callSiteArray[32].callGetProperty(callSiteArray[33].callGroovyObjectGetProperty(callSiteArray[34].callGroovyObjectGetProperty(this))), command);
        }
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != RegisterCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public static String getCOMMAND_NAME() {
        return COMMAND_NAME;
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "size";
        stringArray[1] = "fail";
        stringArray[2] = "get";
        stringArray[3] = "loadClass";
        stringArray[4] = "classLoader";
        stringArray[5] = "size";
        stringArray[6] = "newInstance";
        stringArray[7] = "shell";
        stringArray[8] = "size";
        stringArray[9] = "newInstance";
        stringArray[10] = "shell";
        stringArray[11] = "get";
        stringArray[12] = "size";
        stringArray[13] = "newInstance";
        stringArray[14] = "shell";
        stringArray[15] = "get";
        stringArray[16] = "get";
        stringArray[17] = "getAt";
        stringArray[18] = "registry";
        stringArray[19] = "name";
        stringArray[20] = "fail";
        stringArray[21] = "name";
        stringArray[22] = "debugEnabled";
        stringArray[23] = "log";
        stringArray[24] = "debug";
        stringArray[25] = "log";
        stringArray[26] = "name";
        stringArray[27] = "leftShift";
        stringArray[28] = "shell";
        stringArray[29] = "runner";
        stringArray[30] = "shell";
        stringArray[31] = "add";
        stringArray[32] = "completer";
        stringArray[33] = "runner";
        stringArray[34] = "shell";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[35];
        RegisterCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(RegisterCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = RegisterCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


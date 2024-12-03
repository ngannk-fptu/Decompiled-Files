/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import jline.console.completer.Completer;
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
import org.codehaus.groovy.tools.shell.commands.AliasTargetProxyCommand;
import org.codehaus.groovy.tools.shell.completion.CommandNameCompleter;

public class AliasCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":alias";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AliasCommand(Groovysh shell) {
        CallSite[] callSiteArray = AliasCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":a");
    }

    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = AliasCommand.$getCallSiteArray();
        return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[0].callConstructor(CommandNameCompleter.class, callSiteArray[1].callGroovyObjectGetProperty(this)), null});
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = AliasCommand.$getCallSiteArray();
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
        if (ScriptBytecodeAdapter.compareLessThan(callSiteArray[2].call(args), 2)) {
            callSiteArray[3].callCurrent((GroovyObject)this, "Command 'alias' requires at least 2 arguments");
        }
        String name = ShortTypeHandling.castToString(callSiteArray[4].call(args, 0));
        List target = (List)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(args, ScriptBytecodeAdapter.createRange(1, -1, true)), List.class);
        Command command = (Command)ScriptBytecodeAdapter.castToType(callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this), name), Command.class);
        if (ScriptBytecodeAdapter.compareEqual(command, null)) {
            Object object = callSiteArray[8].call(callSiteArray[9].callGroovyObjectGetProperty(this), name);
            command = (Command)ScriptBytecodeAdapter.castToType(object, Command.class);
        }
        if (ScriptBytecodeAdapter.compareNotEqual(command, null)) {
            if (command instanceof AliasTargetProxyCommand) {
                callSiteArray[10].call(callSiteArray[11].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{name}, new String[]{"Rebinding alias: ", ""}));
                callSiteArray[12].call(callSiteArray[13].callGroovyObjectGetProperty(this), command);
            } else {
                callSiteArray[14].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{callSiteArray[15].callGetProperty(command)}, new String[]{"Can not rebind non-user aliased command: ", ""}));
            }
        }
        callSiteArray[16].call(callSiteArray[17].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{name, target}, new String[]{"Creating alias '", "' to: ", ""}));
        Object object = callSiteArray[18].call(callSiteArray[19].callGroovyObjectGetProperty(this), callSiteArray[20].callConstructor(AliasTargetProxyCommand.class, callSiteArray[21].callGroovyObjectGetProperty(this), name, target));
        command = (Command)ScriptBytecodeAdapter.castToType(object, Command.class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[22].callGroovyObjectGetProperty(callSiteArray[23].callGroovyObjectGetProperty(this)))) {
            return callSiteArray[24].call(callSiteArray[25].callGetProperty(callSiteArray[26].callGroovyObjectGetProperty(callSiteArray[27].callGroovyObjectGetProperty(this))), command);
        }
        return null;
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AliasCommand.class) {
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
        stringArray[1] = "registry";
        stringArray[2] = "size";
        stringArray[3] = "fail";
        stringArray[4] = "getAt";
        stringArray[5] = "getAt";
        stringArray[6] = "find";
        stringArray[7] = "registry";
        stringArray[8] = "find";
        stringArray[9] = "registry";
        stringArray[10] = "debug";
        stringArray[11] = "log";
        stringArray[12] = "remove";
        stringArray[13] = "registry";
        stringArray[14] = "fail";
        stringArray[15] = "name";
        stringArray[16] = "debug";
        stringArray[17] = "log";
        stringArray[18] = "leftShift";
        stringArray[19] = "shell";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "shell";
        stringArray[22] = "runner";
        stringArray[23] = "shell";
        stringArray[24] = "add";
        stringArray[25] = "completer";
        stringArray[26] = "runner";
        stringArray[27] = "shell";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[28];
        AliasCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AliasCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AliasCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


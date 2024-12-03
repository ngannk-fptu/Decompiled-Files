/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import jline.console.completer.Completer;
import org.codehaus.groovy.reflection.ClassInfo;
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

public class CommandAlias
extends CommandSupport {
    private final String targetName;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public CommandAlias(Groovysh shell, String name, String shortcut, String target) {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        super(shell, name, shortcut);
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = target;
            valueRecorder.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert target", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        String string = target;
        this.targetName = ShortTypeHandling.castToString(string);
    }

    public Command getTarget() {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        Command command = (Command)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(callSiteArray[1].callGroovyObjectGetProperty(this), this.targetName), Command.class);
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Command command2 = command;
            valueRecorder.record(command2, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(command2, null);
            valueRecorder.record(bl, 16);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert command != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        return command;
    }

    /*
     * WARNING - Removed back jump from a try to a catch block - possible behaviour change.
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        try {
            if (!(callSiteArray[2].callGroovyObjectGetProperty(this) instanceof CommandSupport)) return (List)ScriptBytecodeAdapter.castToType(null, List.class);
            CommandSupport support = (CommandSupport)ScriptBytecodeAdapter.castToType(callSiteArray[3].callGroovyObjectGetProperty(this), CommandSupport.class);
            return (List)ScriptBytecodeAdapter.castToType(callSiteArray[4].call(support), List.class);
        }
        catch (Exception MissingMethodException2) {
            List list = (List)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), "Aliased Command without createCompleters Method"), List.class);
            return list;
        }
    }

    @Override
    public String getDescription() {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[7].call(callSiteArray[8].callGroovyObjectGetProperty(this), "info.alias_to", this.targetName));
    }

    @Override
    public String getUsage() {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[9].callGetProperty(callSiteArray[10].callGroovyObjectGetProperty(this)));
    }

    @Override
    public String getHelp() {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[11].callGetProperty(callSiteArray[12].callGroovyObjectGetProperty(this)));
    }

    @Override
    public boolean getHidden() {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        return DefaultTypeTransformation.booleanUnbox(callSiteArray[13].callGetProperty(callSiteArray[14].callGroovyObjectGetProperty(this)));
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = CommandAlias.$getCallSiteArray();
        return callSiteArray[15].call(callSiteArray[16].callGroovyObjectGetProperty(this), args);
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CommandAlias.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public final String getTargetName() {
        return this.targetName;
    }

    public /* synthetic */ String super$2$getDescription() {
        return super.getDescription();
    }

    public /* synthetic */ String super$2$getUsage() {
        return super.getUsage();
    }

    public /* synthetic */ String super$2$getHelp() {
        return super.getHelp();
    }

    public /* synthetic */ boolean super$2$getHidden() {
        return super.getHidden();
    }

    public /* synthetic */ List super$2$createCompleters() {
        return super.createCompleters();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "find";
        stringArray[1] = "registry";
        stringArray[2] = "target";
        stringArray[3] = "target";
        stringArray[4] = "createCompleters";
        stringArray[5] = "warn";
        stringArray[6] = "log";
        stringArray[7] = "format";
        stringArray[8] = "messages";
        stringArray[9] = "usage";
        stringArray[10] = "target";
        stringArray[11] = "help";
        stringArray[12] = "target";
        stringArray[13] = "hidden";
        stringArray[14] = "target";
        stringArray[15] = "execute";
        stringArray[16] = "target";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[17];
        CommandAlias.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CommandAlias.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CommandAlias.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.commands;

import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
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

public class AliasTargetProxyCommand
extends CommandSupport
implements Command {
    private static int counter;
    private final List<String> args;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AliasTargetProxyCommand(Groovysh shell, String name, List args) {
        CallSite[] callSiteArray = AliasTargetProxyCommand.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || BytecodeInterface8.disabledStandardMetaClass()) {
            int n = counter;
            counter = DefaultTypeTransformation.intUnbox(callSiteArray[1].call(n));
            super(shell, name, ShortTypeHandling.castToString(callSiteArray[0].call((Object)":a", n)));
        } else {
            int n = counter;
            counter = n + 1;
            super(shell, name, ShortTypeHandling.castToString(callSiteArray[2].call((Object)":a", n)));
        }
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            List list = args;
            valueRecorder.record(list, 8);
            if (DefaultTypeTransformation.booleanUnbox(list)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert args", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        List list = args;
        this.args = (List)ScriptBytecodeAdapter.castToType(list, List.class);
    }

    @Override
    public String getDescription() {
        CallSite[] callSiteArray = AliasTargetProxyCommand.$getCallSiteArray();
        return ShortTypeHandling.castToString(new GStringImpl(new Object[]{callSiteArray[3].call(this.args, " ")}, new String[]{"User defined alias to: @|bold ", "|@"}));
    }

    @Override
    public String getUsage() {
        CallSite[] callSiteArray = AliasTargetProxyCommand.$getCallSiteArray();
        return "";
    }

    @Override
    public String getHelp() {
        CallSite[] callSiteArray = AliasTargetProxyCommand.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[4].callGroovyObjectGetProperty(this));
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = AliasTargetProxyCommand.$getCallSiteArray();
        List allArgs = (List)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(this.args, args), List.class);
        callSiteArray[6].call(callSiteArray[7].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{allArgs}, new String[]{"Executing with args: ", ""}));
        return callSiteArray[8].call(callSiteArray[9].callGroovyObjectGetProperty(this), callSiteArray[10].call((Object)allArgs, " "));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AliasTargetProxyCommand.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    static {
        int n;
        counter = n = 0;
    }

    public final List<String> getArgs() {
        return this.args;
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

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "plus";
        stringArray[1] = "next";
        stringArray[2] = "plus";
        stringArray[3] = "join";
        stringArray[4] = "description";
        stringArray[5] = "plus";
        stringArray[6] = "debug";
        stringArray[7] = "log";
        stringArray[8] = "executeCommand";
        stringArray[9] = "shell";
        stringArray[10] = "join";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[11];
        AliasTargetProxyCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AliasTargetProxyCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AliasTargetProxyCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


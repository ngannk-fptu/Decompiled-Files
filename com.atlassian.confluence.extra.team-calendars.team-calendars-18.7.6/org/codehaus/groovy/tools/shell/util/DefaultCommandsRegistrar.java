/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.shell.util;

import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.Shell;
import org.codehaus.groovy.tools.shell.commands.AliasCommand;
import org.codehaus.groovy.tools.shell.commands.ClearCommand;
import org.codehaus.groovy.tools.shell.commands.DisplayCommand;
import org.codehaus.groovy.tools.shell.commands.DocCommand;
import org.codehaus.groovy.tools.shell.commands.EditCommand;
import org.codehaus.groovy.tools.shell.commands.ExitCommand;
import org.codehaus.groovy.tools.shell.commands.HelpCommand;
import org.codehaus.groovy.tools.shell.commands.HistoryCommand;
import org.codehaus.groovy.tools.shell.commands.ImportCommand;
import org.codehaus.groovy.tools.shell.commands.InspectCommand;
import org.codehaus.groovy.tools.shell.commands.LoadCommand;
import org.codehaus.groovy.tools.shell.commands.PurgeCommand;
import org.codehaus.groovy.tools.shell.commands.RecordCommand;
import org.codehaus.groovy.tools.shell.commands.RegisterCommand;
import org.codehaus.groovy.tools.shell.commands.SaveCommand;
import org.codehaus.groovy.tools.shell.commands.SetCommand;
import org.codehaus.groovy.tools.shell.commands.ShowCommand;

public class DefaultCommandsRegistrar
implements GroovyObject {
    private final Shell shell;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public DefaultCommandsRegistrar(Shell shell) {
        MetaClass metaClass;
        CallSite[] callSiteArray = DefaultCommandsRegistrar.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Shell shell2 = shell;
            valueRecorder.record(shell2, 8);
            boolean bl = ScriptBytecodeAdapter.compareNotEqual(shell2, null);
            valueRecorder.record(bl, 14);
            if (bl) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert shell != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Shell shell3 = shell;
        this.shell = (Shell)ScriptBytecodeAdapter.castToType(shell3, Shell.class);
    }

    public void register() {
        CallSite[] callSiteArray = DefaultCommandsRegistrar.$getCallSiteArray();
        Command classname = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[0].call(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[1].callConstructor(HelpCommand.class, this.shell), callSiteArray[2].callConstructor(ExitCommand.class, this.shell), callSiteArray[3].callConstructor(ImportCommand.class, this.shell), callSiteArray[4].callConstructor(DisplayCommand.class, this.shell), callSiteArray[5].callConstructor(ClearCommand.class, this.shell), callSiteArray[6].callConstructor(ShowCommand.class, this.shell), callSiteArray[7].callConstructor(InspectCommand.class, this.shell), callSiteArray[8].callConstructor(PurgeCommand.class, this.shell), callSiteArray[9].callConstructor(EditCommand.class, this.shell), callSiteArray[10].callConstructor(LoadCommand.class, this.shell), callSiteArray[11].callConstructor(SaveCommand.class, this.shell), callSiteArray[12].callConstructor(RecordCommand.class, this.shell), callSiteArray[13].callConstructor(HistoryCommand.class, this.shell), callSiteArray[14].callConstructor(AliasCommand.class, this.shell), callSiteArray[15].callConstructor(SetCommand.class, this.shell), callSiteArray[16].callConstructor(RegisterCommand.class, this.shell), callSiteArray[17].callConstructor(DocCommand.class, this.shell)})), Iterator.class);
        while (iterator.hasNext()) {
            classname = (Command)ScriptBytecodeAdapter.castToType(iterator.next(), Command.class);
            callSiteArray[18].call((Object)this.shell, classname);
        }
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != DefaultCommandsRegistrar.class) {
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

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "iterator";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "<$constructor$>";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "<$constructor$>";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "register";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[19];
        DefaultCommandsRegistrar.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(DefaultCommandsRegistrar.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = DefaultCommandsRegistrar.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


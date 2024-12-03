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
import java.util.Iterator;
import java.util.List;
import jline.console.completer.Completer;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.CommandSupport;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.completion.CommandNameCompleter;

public class HelpCommand
extends CommandSupport {
    public static final String COMMAND_NAME = ":help";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public HelpCommand(Groovysh shell) {
        CallSite[] callSiteArray = HelpCommand.$getCallSiteArray();
        super(shell, COMMAND_NAME, ":h");
        callSiteArray[0].callCurrent(this, "?", ":?");
    }

    @Override
    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = HelpCommand.$getCallSiteArray();
        return ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[1].callConstructor(CommandNameCompleter.class, callSiteArray[2].callGroovyObjectGetProperty(this)), null});
    }

    @Override
    public Object execute(List<String> args) {
        CallSite[] callSiteArray = HelpCommand.$getCallSiteArray();
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
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[3].call(args), 1)) {
            callSiteArray[4].callCurrent((GroovyObject)this, callSiteArray[5].call(callSiteArray[6].callGroovyObjectGetProperty(this), "error.unexpected_args", callSiteArray[7].call(args, " ")));
        }
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[8].call(args), 1)) {
                return callSiteArray[9].callCurrent((GroovyObject)this, callSiteArray[10].call(args, 0));
            }
            return callSiteArray[11].callCurrent(this);
        }
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[12].call(args), 1)) {
            return callSiteArray[13].callCurrent((GroovyObject)this, callSiteArray[14].call(args, 0));
        }
        this.list();
        return null;
    }

    private void help(String name) {
        CallSite[] callSiteArray = HelpCommand.$getCallSiteArray();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            String string = name;
            valueRecorder.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert name", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        Command command = (Command)ScriptBytecodeAdapter.castToType(callSiteArray[15].call(callSiteArray[16].callGroovyObjectGetProperty(this), name), Command.class);
        if (!DefaultTypeTransformation.booleanUnbox(command)) {
            callSiteArray[17].callCurrent((GroovyObject)this, new GStringImpl(new Object[]{name}, new String[]{"No such command: ", ""}));
        }
        callSiteArray[18].call(callSiteArray[19].callGetProperty(callSiteArray[20].callGroovyObjectGetProperty(this)));
        callSiteArray[21].call(callSiteArray[22].callGetProperty(callSiteArray[23].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{callSiteArray[24].callGetProperty(command), callSiteArray[25].callGetProperty(command)}, new String[]{"usage: @|bold ", "|@ ", ""}));
        callSiteArray[26].call(callSiteArray[27].callGetProperty(callSiteArray[28].callGroovyObjectGetProperty(this)));
        callSiteArray[29].call(callSiteArray[30].callGetProperty(callSiteArray[31].callGroovyObjectGetProperty(this)), callSiteArray[32].callGetProperty(command));
        callSiteArray[33].call(callSiteArray[34].callGetProperty(callSiteArray[35].callGroovyObjectGetProperty(this)));
    }

    private void list() {
        CallSite[] callSiteArray = HelpCommand.$getCallSiteArray();
        int maxName = 0;
        int maxShortcut = 0;
        Command command = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[36].call(callSiteArray[37].call(callSiteArray[38].callGroovyObjectGetProperty(this))), Iterator.class);
        while (iterator.hasNext()) {
            command = (Command)ScriptBytecodeAdapter.castToType(iterator.next(), Command.class);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[39].callGetProperty(command))) continue;
            if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[40].call(callSiteArray[41].callGetProperty(command)), maxName)) {
                Object object = callSiteArray[42].call(callSiteArray[43].callGetProperty(command));
                maxName = DefaultTypeTransformation.intUnbox(object);
            }
            if (!ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[44].call(callSiteArray[45].callGetProperty(command)), maxShortcut)) continue;
            Object object = callSiteArray[46].call(callSiteArray[47].callGetProperty(command));
            maxShortcut = DefaultTypeTransformation.intUnbox(object);
        }
        callSiteArray[48].call(callSiteArray[49].callGetProperty(callSiteArray[50].callGroovyObjectGetProperty(this)));
        callSiteArray[51].call(callSiteArray[52].callGetProperty(callSiteArray[53].callGroovyObjectGetProperty(this)), "For information about @|green Groovy|@, visit:");
        callSiteArray[54].call(callSiteArray[55].callGetProperty(callSiteArray[56].callGroovyObjectGetProperty(this)), "    @|cyan http://groovy-lang.org|@ ");
        callSiteArray[57].call(callSiteArray[58].callGetProperty(callSiteArray[59].callGroovyObjectGetProperty(this)));
        callSiteArray[60].call(callSiteArray[61].callGetProperty(callSiteArray[62].callGroovyObjectGetProperty(this)), "Available commands:");
        Command command2 = null;
        Iterator iterator2 = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[63].call(callSiteArray[64].call(callSiteArray[65].callGroovyObjectGetProperty(this))), Iterator.class);
        while (iterator2.hasNext()) {
            command2 = (Command)ScriptBytecodeAdapter.castToType(iterator2.next(), Command.class);
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[66].callGetProperty(command2))) continue;
            Object n = callSiteArray[67].call(callSiteArray[68].callGetProperty(command2), maxName, " ");
            Object s = callSiteArray[69].call(callSiteArray[70].callGetProperty(command2), maxShortcut, " ");
            Object d = callSiteArray[71].callGetProperty(command2);
            callSiteArray[72].call(callSiteArray[73].callGetProperty(callSiteArray[74].callGroovyObjectGetProperty(this)), new GStringImpl(new Object[]{n, s, d}, new String[]{"  @|bold ", "|@  (@|bold ", "|@) ", ""}));
        }
        callSiteArray[75].call(callSiteArray[76].callGetProperty(callSiteArray[77].callGroovyObjectGetProperty(this)));
        callSiteArray[78].call(callSiteArray[79].callGetProperty(callSiteArray[80].callGroovyObjectGetProperty(this)), "For help on a specific command type:");
        callSiteArray[81].call(callSiteArray[82].callGetProperty(callSiteArray[83].callGroovyObjectGetProperty(this)), "    :help @|bold command|@ ");
        callSiteArray[84].call(callSiteArray[85].callGetProperty(callSiteArray[86].callGroovyObjectGetProperty(this)));
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != HelpCommand.class) {
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
        stringArray[2] = "registry";
        stringArray[3] = "size";
        stringArray[4] = "fail";
        stringArray[5] = "format";
        stringArray[6] = "messages";
        stringArray[7] = "join";
        stringArray[8] = "size";
        stringArray[9] = "help";
        stringArray[10] = "getAt";
        stringArray[11] = "list";
        stringArray[12] = "size";
        stringArray[13] = "help";
        stringArray[14] = "getAt";
        stringArray[15] = "find";
        stringArray[16] = "registry";
        stringArray[17] = "fail";
        stringArray[18] = "println";
        stringArray[19] = "out";
        stringArray[20] = "io";
        stringArray[21] = "println";
        stringArray[22] = "out";
        stringArray[23] = "io";
        stringArray[24] = "name";
        stringArray[25] = "usage";
        stringArray[26] = "println";
        stringArray[27] = "out";
        stringArray[28] = "io";
        stringArray[29] = "println";
        stringArray[30] = "out";
        stringArray[31] = "io";
        stringArray[32] = "help";
        stringArray[33] = "println";
        stringArray[34] = "out";
        stringArray[35] = "io";
        stringArray[36] = "iterator";
        stringArray[37] = "commands";
        stringArray[38] = "registry";
        stringArray[39] = "hidden";
        stringArray[40] = "size";
        stringArray[41] = "name";
        stringArray[42] = "size";
        stringArray[43] = "name";
        stringArray[44] = "size";
        stringArray[45] = "shortcut";
        stringArray[46] = "size";
        stringArray[47] = "shortcut";
        stringArray[48] = "println";
        stringArray[49] = "out";
        stringArray[50] = "io";
        stringArray[51] = "println";
        stringArray[52] = "out";
        stringArray[53] = "io";
        stringArray[54] = "println";
        stringArray[55] = "out";
        stringArray[56] = "io";
        stringArray[57] = "println";
        stringArray[58] = "out";
        stringArray[59] = "io";
        stringArray[60] = "println";
        stringArray[61] = "out";
        stringArray[62] = "io";
        stringArray[63] = "iterator";
        stringArray[64] = "commands";
        stringArray[65] = "registry";
        stringArray[66] = "hidden";
        stringArray[67] = "padRight";
        stringArray[68] = "name";
        stringArray[69] = "padRight";
        stringArray[70] = "shortcut";
        stringArray[71] = "description";
        stringArray[72] = "println";
        stringArray[73] = "out";
        stringArray[74] = "io";
        stringArray[75] = "println";
        stringArray[76] = "out";
        stringArray[77] = "io";
        stringArray[78] = "println";
        stringArray[79] = "out";
        stringArray[80] = "io";
        stringArray[81] = "println";
        stringArray[82] = "out";
        stringArray[83] = "io";
        stringArray[84] = "println";
        stringArray[85] = "out";
        stringArray[86] = "io";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[87];
        HelpCommand.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(HelpCommand.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = HelpCommand.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


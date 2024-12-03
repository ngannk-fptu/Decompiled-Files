/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.completer.Completer
 *  jline.console.completer.NullCompleter
 *  jline.console.completer.StringsCompleter
 *  jline.console.history.FileHistory
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jline.console.completer.Completer;
import jline.console.completer.NullCompleter;
import jline.console.completer.StringsCompleter;
import jline.console.history.FileHistory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.powerassert.AssertionRenderer;
import org.codehaus.groovy.runtime.powerassert.ValueRecorder;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.tools.shell.BufferManager;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.CommandAlias;
import org.codehaus.groovy.tools.shell.CommandException;
import org.codehaus.groovy.tools.shell.CommandRegistry;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.IO;
import org.codehaus.groovy.tools.shell.completion.StricterArgumentCompleter;
import org.codehaus.groovy.tools.shell.util.Logger;
import org.codehaus.groovy.tools.shell.util.MessageSource;

public abstract class CommandSupport
implements Command,
GroovyObject {
    protected static final String NEWLINE;
    protected final Logger log;
    protected final MessageSource messages;
    private final String name;
    private final String shortcut;
    protected final Groovysh shell;
    protected final IO io;
    protected CommandRegistry registry;
    private final List aliases;
    private boolean hidden;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    protected CommandSupport(Groovysh shell, String name, String shortcut) {
        MetaClass metaClass;
        boolean bl;
        List list;
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        Object object = callSiteArray[0].callConstructor(MessageSource.class, callSiteArray[1].callGroovyObjectGetProperty(this), CommandSupport.class);
        this.messages = (MessageSource)ScriptBytecodeAdapter.castToType(object, MessageSource.class);
        this.aliases = list = ScriptBytecodeAdapter.createList(new Object[0]);
        this.hidden = bl = false;
        this.metaClass = metaClass = this.$getStaticMetaClass();
        ValueRecorder valueRecorder = new ValueRecorder();
        try {
            Groovysh groovysh = shell;
            valueRecorder.record(groovysh, 8);
            boolean bl2 = ScriptBytecodeAdapter.compareNotEqual(groovysh, null);
            valueRecorder.record(bl2, 14);
            if (bl2) {
                valueRecorder.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert shell != null", valueRecorder), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder.clear();
            throw throwable;
        }
        ValueRecorder valueRecorder2 = new ValueRecorder();
        try {
            String string = name;
            valueRecorder2.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder2.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert name", valueRecorder2), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder2.clear();
            throw throwable;
        }
        ValueRecorder valueRecorder3 = new ValueRecorder();
        try {
            String string = shortcut;
            valueRecorder3.record(string, 8);
            if (DefaultTypeTransformation.booleanUnbox(string)) {
                valueRecorder3.clear();
            } else {
                ScriptBytecodeAdapter.assertFailed(AssertionRenderer.render("assert shortcut", valueRecorder3), null);
            }
        }
        catch (Throwable throwable) {
            valueRecorder3.clear();
            throw throwable;
        }
        Object object2 = callSiteArray[2].call(Logger.class, callSiteArray[3].callGroovyObjectGetProperty(this), name);
        this.log = (Logger)ScriptBytecodeAdapter.castToType(object2, Logger.class);
        Groovysh groovysh = shell;
        this.shell = (Groovysh)ScriptBytecodeAdapter.castToType(groovysh, Groovysh.class);
        Object object3 = callSiteArray[4].callGetProperty(shell);
        this.io = (IO)ScriptBytecodeAdapter.castToType(object3, IO.class);
        String string = name;
        this.name = ShortTypeHandling.castToString(string);
        String string2 = shortcut;
        this.shortcut = ShortTypeHandling.castToString(string2);
    }

    @Override
    public String getDescription() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[5].call((Object)this.messages, "command.description"));
    }

    @Override
    public String getUsage() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[6].call((Object)this.messages, "command.usage"));
    }

    @Override
    public String getHelp() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return ShortTypeHandling.castToString(callSiteArray[7].call((Object)this.messages, "command.help"));
    }

    @Override
    public boolean getHidden() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return this.hidden;
    }

    @Override
    public List getAliases() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return this.aliases;
    }

    @Override
    public String getShortcut() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return this.shortcut;
    }

    @Override
    public String getName() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return this.name;
    }

    protected List<Completer> createCompleters() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return ScriptBytecodeAdapter.createList(new Object[0]);
    }

    @Override
    public Completer getCompleter() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        if (this.hidden) {
            return (Completer)ScriptBytecodeAdapter.castToType(null, Completer.class);
        }
        Reference<List> list = new Reference<List>((List)ScriptBytecodeAdapter.castToType(callSiteArray[8].callConstructor(ArrayList.class), List.class));
        callSiteArray[9].call((Object)list.get(), callSiteArray[10].callConstructor(StringsCompleter.class, this.name, this.shortcut));
        List completers = null;
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[11].callCurrent(this);
            completers = (List)ScriptBytecodeAdapter.castToType(object, List.class);
        } else {
            List list2;
            completers = list2 = this.createCompleters();
        }
        if (DefaultTypeTransformation.booleanUnbox(completers)) {
            public class _getCompleter_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference list;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _getCompleter_closure1(Object _outerInstance, Object _thisObject, Reference list) {
                    Reference reference;
                    CallSite[] callSiteArray = _getCompleter_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.list = reference = list;
                }

                public Object doCall(Completer it) {
                    CallSite[] callSiteArray = _getCompleter_closure1.$getCallSiteArray();
                    if (DefaultTypeTransformation.booleanUnbox(it)) {
                        return callSiteArray[0].call(this.list.get(), it);
                    }
                    return callSiteArray[1].call(this.list.get(), callSiteArray[2].callConstructor(NullCompleter.class));
                }

                public Object call(Completer it) {
                    CallSite[] callSiteArray = _getCompleter_closure1.$getCallSiteArray();
                    return callSiteArray[3].callCurrent((GroovyObject)this, it);
                }

                public List getList() {
                    CallSite[] callSiteArray = _getCompleter_closure1.$getCallSiteArray();
                    return (List)ScriptBytecodeAdapter.castToType(this.list.get(), List.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _getCompleter_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "leftShift";
                    stringArray[1] = "leftShift";
                    stringArray[2] = "<$constructor$>";
                    stringArray[3] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _getCompleter_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_getCompleter_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _getCompleter_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[12].call((Object)completers, new _getCompleter_closure1(this, this, list));
        } else {
            callSiteArray[13].call((Object)list.get(), callSiteArray[14].callConstructor(NullCompleter.class));
        }
        return (Completer)ScriptBytecodeAdapter.castToType(callSiteArray[15].callConstructor(StricterArgumentCompleter.class, list.get()), Completer.class);
    }

    protected void alias(String name, String shortcut) {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        callSiteArray[16].call((Object)this.aliases, callSiteArray[17].callConstructor(CommandAlias.class, this.shell, name, shortcut, this.name));
    }

    protected void fail(String msg) {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        throw (Throwable)callSiteArray[18].callConstructor(CommandException.class, this, msg);
    }

    protected void fail(String msg, Throwable cause) {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        throw (Throwable)callSiteArray[19].callConstructor(CommandException.class, this, msg, cause);
    }

    protected void assertNoArguments(List<String> args) {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
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
        if (ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[20].call(args), 0)) {
            callSiteArray[21].callCurrent((GroovyObject)this, callSiteArray[22].call(this.messages, "error.unexpected_args", callSiteArray[23].call(args, " ")));
        }
    }

    protected BufferManager getBuffers() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return (BufferManager)ScriptBytecodeAdapter.castToType(callSiteArray[24].callGetProperty(this.shell), BufferManager.class);
    }

    protected List<String> getBuffer() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[25].call(callSiteArray[26].callGetProperty(this.shell)), List.class);
    }

    protected List<String> getImports() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[27].callGetProperty(this.shell), List.class);
    }

    protected Binding getBinding() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return (Binding)ScriptBytecodeAdapter.castToType(callSiteArray[28].callGetProperty(callSiteArray[29].callGetProperty(this.shell)), Binding.class);
    }

    protected Map getVariables() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return (Map)ScriptBytecodeAdapter.castToType(callSiteArray[30].callGetProperty(callSiteArray[31].callGroovyObjectGetProperty(this)), Map.class);
    }

    protected FileHistory getHistory() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return (FileHistory)ScriptBytecodeAdapter.castToType(callSiteArray[32].callGetProperty(this.shell), FileHistory.class);
    }

    protected GroovyClassLoader getClassLoader() {
        CallSite[] callSiteArray = CommandSupport.$getCallSiteArray();
        return (GroovyClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[33].callGetProperty(callSiteArray[34].callGetProperty(this.shell)), GroovyClassLoader.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != CommandSupport.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    @Override
    public MetaClass getMetaClass() {
        MetaClass metaClass = this.metaClass;
        if (metaClass != null) {
            return metaClass;
        }
        this.metaClass = this.$getStaticMetaClass();
        return this.metaClass;
    }

    @Override
    public void setMetaClass(MetaClass metaClass) {
        this.metaClass = metaClass;
    }

    @Override
    public Object invokeMethod(String string, Object object) {
        return this.getMetaClass().invokeMethod((Object)this, string, object);
    }

    @Override
    public Object getProperty(String string) {
        return this.getMetaClass().getProperty(this, string);
    }

    @Override
    public void setProperty(String string, Object object) {
        this.getMetaClass().setProperty(this, string, object);
    }

    static {
        Object object = CommandSupport.$getCallSiteArray()[35].call(CommandSupport.$getCallSiteArray()[36].callGetProperty(System.class), "line.separator");
        NEWLINE = ShortTypeHandling.castToString(object);
    }

    public void setHidden(boolean bl) {
        this.hidden = bl;
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "class";
        stringArray[2] = "create";
        stringArray[3] = "class";
        stringArray[4] = "io";
        stringArray[5] = "getAt";
        stringArray[6] = "getAt";
        stringArray[7] = "getAt";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "leftShift";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "createCompleters";
        stringArray[12] = "each";
        stringArray[13] = "leftShift";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "leftShift";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "<$constructor$>";
        stringArray[19] = "<$constructor$>";
        stringArray[20] = "size";
        stringArray[21] = "fail";
        stringArray[22] = "format";
        stringArray[23] = "join";
        stringArray[24] = "buffers";
        stringArray[25] = "current";
        stringArray[26] = "buffers";
        stringArray[27] = "imports";
        stringArray[28] = "context";
        stringArray[29] = "interp";
        stringArray[30] = "variables";
        stringArray[31] = "binding";
        stringArray[32] = "history";
        stringArray[33] = "classLoader";
        stringArray[34] = "interp";
        stringArray[35] = "getAt";
        stringArray[36] = "properties";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[37];
        CommandSupport.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(CommandSupport.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = CommandSupport.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


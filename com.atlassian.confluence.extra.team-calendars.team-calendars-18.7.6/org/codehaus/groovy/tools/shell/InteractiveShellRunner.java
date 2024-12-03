/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  jline.console.ConsoleReader
 *  jline.console.history.FileHistory
 */
package org.codehaus.groovy.tools.shell;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.Iterator;
import jline.console.ConsoleReader;
import jline.console.history.FileHistory;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.tools.shell.Command;
import org.codehaus.groovy.tools.shell.CommandsMultiCompleter;
import org.codehaus.groovy.tools.shell.Groovysh;
import org.codehaus.groovy.tools.shell.PatchedCandidateListCompletionHandler;
import org.codehaus.groovy.tools.shell.PatchedConsoleReader;
import org.codehaus.groovy.tools.shell.ShellRunner;
import org.codehaus.groovy.tools.shell.completion.CustomClassSyntaxCompletor;
import org.codehaus.groovy.tools.shell.completion.FileNameCompleter;
import org.codehaus.groovy.tools.shell.completion.GroovySyntaxCompletor;
import org.codehaus.groovy.tools.shell.completion.ImportsSyntaxCompletor;
import org.codehaus.groovy.tools.shell.completion.KeywordSyntaxCompletor;
import org.codehaus.groovy.tools.shell.completion.ReflectionCompletor;
import org.codehaus.groovy.tools.shell.completion.VariableSyntaxCompletor;
import org.codehaus.groovy.tools.shell.util.WrappedInputStream;

public class InteractiveShellRunner
extends ShellRunner
implements Runnable {
    private ConsoleReader reader;
    private final Closure prompt;
    private final CommandsMultiCompleter completer;
    private WrappedInputStream wrappedInputStream;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private static /* synthetic */ SoftReference $callSiteArray;

    public InteractiveShellRunner(Groovysh shell, Closure prompt) {
        CallSite[] callSiteArray = InteractiveShellRunner.$getCallSiteArray();
        super(shell);
        Closure closure = prompt;
        this.prompt = (Closure)ScriptBytecodeAdapter.castToType(closure, Closure.class);
        Object object = callSiteArray[0].callConstructor(WrappedInputStream.class, callSiteArray[1].callGetProperty(callSiteArray[2].callGroovyObjectGetProperty(shell)));
        this.wrappedInputStream = (WrappedInputStream)ScriptBytecodeAdapter.castToType(object, WrappedInputStream.class);
        Object object2 = callSiteArray[3].callConstructor(PatchedConsoleReader.class, this.wrappedInputStream, callSiteArray[4].callGetProperty(callSiteArray[5].callGroovyObjectGetProperty(shell)));
        this.reader = (ConsoleReader)ScriptBytecodeAdapter.castToType(object2, ConsoleReader.class);
        callSiteArray[6].call((Object)this.reader, callSiteArray[7].callConstructor(PatchedCandidateListCompletionHandler.class));
        boolean bl = false;
        ScriptBytecodeAdapter.setProperty(bl, null, this.reader, "expandEvents");
        Object object3 = callSiteArray[8].callConstructor(CommandsMultiCompleter.class);
        this.completer = (CommandsMultiCompleter)ScriptBytecodeAdapter.castToType(object3, CommandsMultiCompleter.class);
        callSiteArray[9].call((Object)this.reader, this.completer);
        CustomClassSyntaxCompletor classnameCompletor = (CustomClassSyntaxCompletor)ScriptBytecodeAdapter.castToType(callSiteArray[10].callConstructor(CustomClassSyntaxCompletor.class, shell), CustomClassSyntaxCompletor.class);
        callSiteArray[11].call((Object)this.reader, callSiteArray[12].callConstructor((Object)GroovySyntaxCompletor.class, ArrayUtil.createArray(shell, callSiteArray[13].callConstructor(ReflectionCompletor.class, shell), classnameCompletor, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[14].callConstructor(KeywordSyntaxCompletor.class), callSiteArray[15].callConstructor(VariableSyntaxCompletor.class, shell), classnameCompletor, callSiteArray[16].callConstructor(ImportsSyntaxCompletor.class, shell)}), callSiteArray[17].callConstructor(FileNameCompleter.class, false))));
    }

    @Override
    public void run() {
        CallSite[] callSiteArray = InteractiveShellRunner.$getCallSiteArray();
        Command command = null;
        Iterator iterator = (Iterator)ScriptBytecodeAdapter.castToType(callSiteArray[18].call(callSiteArray[19].call(callSiteArray[20].callGroovyObjectGetProperty(callSiteArray[21].callGroovyObjectGetProperty(this)))), Iterator.class);
        while (iterator.hasNext()) {
            command = (Command)ScriptBytecodeAdapter.castToType(iterator.next(), Command.class);
            callSiteArray[22].call((Object)this.completer, command);
        }
        callSiteArray[23].call(this.completer);
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[24].callCurrent(this);
        } else {
            this.adjustHistory();
        }
        ScriptBytecodeAdapter.invokeMethodOnSuper0(ShellRunner.class, this, "run");
    }

    public void setHistory(FileHistory history) {
        CallSite[] callSiteArray = InteractiveShellRunner.$getCallSiteArray();
        FileHistory fileHistory = history;
        ScriptBytecodeAdapter.setProperty(fileHistory, null, this.reader, "history");
        Object dir = callSiteArray[25].callGetProperty(callSiteArray[26].callGetProperty(history));
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[27].call(dir))) {
            callSiteArray[28].call(dir);
            callSiteArray[29].call(callSiteArray[30].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{dir}, new String[]{"Created base directory for history file: ", ""}));
        }
        callSiteArray[31].call(callSiteArray[32].callGroovyObjectGetProperty(this), new GStringImpl(new Object[]{callSiteArray[33].callGetProperty(history)}, new String[]{"Using history file: ", ""}));
    }

    /*
     * Exception decompiling
     */
    @Override
    protected String readLine() {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Tried to end blocks [5[CATCHBLOCK]], but top level block is 3[TRYBLOCK]
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.processEndingBlocks(Op04StructuredStatement.java:435)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:484)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    @Override
    protected boolean work() {
        CallSite[] callSiteArray = InteractiveShellRunner.$getCallSiteArray();
        boolean result = DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.invokeMethodOnSuper0(ShellRunner.class, this, "work"));
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            callSiteArray[54].callCurrent(this);
        } else {
            this.adjustHistory();
        }
        return result;
    }

    private void adjustHistory() {
        CallSite[] callSiteArray = InteractiveShellRunner.$getCallSiteArray();
        if (callSiteArray[55].callGroovyObjectGetProperty(this) instanceof Groovysh) {
            Object history = callSiteArray[56].callGroovyObjectGetProperty(callSiteArray[57].callGroovyObjectGetProperty(this));
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                boolean bl = ScriptBytecodeAdapter.compareNotEqual(history, null) && ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[58].call(history), callSiteArray[59].callGetProperty(history));
                ScriptBytecodeAdapter.setGroovyObjectProperty(bl, InteractiveShellRunner.class, (GroovyObject)callSiteArray[60].callGroovyObjectGetProperty(this), "historyFull");
            } else {
                boolean bl = ScriptBytecodeAdapter.compareNotEqual(history, null) && ScriptBytecodeAdapter.compareGreaterThanEqual(callSiteArray[61].call(history), callSiteArray[62].callGetProperty(history));
                ScriptBytecodeAdapter.setGroovyObjectProperty(bl, InteractiveShellRunner.class, (GroovyObject)callSiteArray[63].callGroovyObjectGetProperty(this), "historyFull");
            }
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[64].callGroovyObjectGetProperty(callSiteArray[65].callGroovyObjectGetProperty(this)))) {
                Object first = callSiteArray[66].call(history);
                if (DefaultTypeTransformation.booleanUnbox(first)) {
                    Object object = callSiteArray[67].call(first);
                    ScriptBytecodeAdapter.setGroovyObjectProperty(object, InteractiveShellRunner.class, (GroovyObject)callSiteArray[68].callGroovyObjectGetProperty(this), "evictedLine");
                }
            }
        }
    }

    @Override
    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != InteractiveShellRunner.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public ConsoleReader getReader() {
        return this.reader;
    }

    public void setReader(ConsoleReader consoleReader) {
        this.reader = consoleReader;
    }

    public final Closure getPrompt() {
        return this.prompt;
    }

    public final CommandsMultiCompleter getCompleter() {
        return this.completer;
    }

    public WrappedInputStream getWrappedInputStream() {
        return this.wrappedInputStream;
    }

    public void setWrappedInputStream(WrappedInputStream wrappedInputStream) {
        this.wrappedInputStream = wrappedInputStream;
    }

    public /* synthetic */ void super$2$run() {
        super.run();
    }

    public /* synthetic */ boolean super$2$work() {
        return super.work();
    }

    public /* synthetic */ MetaClass super$2$$getStaticMetaClass() {
        return super.$getStaticMetaClass();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "inputStream";
        stringArray[2] = "io";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "outputStream";
        stringArray[5] = "io";
        stringArray[6] = "setCompletionHandler";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "addCompleter";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "addCompleter";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "<$constructor$>";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "iterator";
        stringArray[19] = "commands";
        stringArray[20] = "registry";
        stringArray[21] = "shell";
        stringArray[22] = "add";
        stringArray[23] = "refresh";
        stringArray[24] = "adjustHistory";
        stringArray[25] = "parentFile";
        stringArray[26] = "file";
        stringArray[27] = "exists";
        stringArray[28] = "mkdirs";
        stringArray[29] = "debug";
        stringArray[30] = "log";
        stringArray[31] = "debug";
        stringArray[32] = "log";
        stringArray[33] = "file";
        stringArray[34] = "valueOf";
        stringArray[35] = "get";
        stringArray[36] = "AUTOINDENT_PREFERENCE_KEY";
        stringArray[37] = "available";
        stringArray[38] = "inputStream";
        stringArray[39] = "io";
        stringArray[40] = "shell";
        stringArray[41] = "insert";
        stringArray[42] = "indentPrefix";
        stringArray[43] = "shell";
        stringArray[44] = "readLine";
        stringArray[45] = "call";
        stringArray[46] = "debug";
        stringArray[47] = "log";
        stringArray[48] = "println";
        stringArray[49] = "verbosity";
        stringArray[50] = "io";
        stringArray[51] = "shell";
        stringArray[52] = "DEBUG";
        stringArray[53] = "println";
        stringArray[54] = "adjustHistory";
        stringArray[55] = "shell";
        stringArray[56] = "history";
        stringArray[57] = "shell";
        stringArray[58] = "size";
        stringArray[59] = "maxSize";
        stringArray[60] = "shell";
        stringArray[61] = "size";
        stringArray[62] = "maxSize";
        stringArray[63] = "shell";
        stringArray[64] = "historyFull";
        stringArray[65] = "shell";
        stringArray[66] = "first";
        stringArray[67] = "value";
        stringArray[68] = "shell";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[69];
        InteractiveShellRunner.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(InteractiveShellRunner.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = InteractiveShellRunner.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


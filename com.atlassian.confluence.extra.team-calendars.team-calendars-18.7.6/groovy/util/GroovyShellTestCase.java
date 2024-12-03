/*
 * Decompiled with CFR 0.152.
 */
package groovy.util;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.Delegate;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.Script;
import groovy.util.GroovyTestCase;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.lang.ref.SoftReference;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;

public class GroovyShellTestCase
extends GroovyTestCase
implements GroovyObject {
    @Delegate
    protected GroovyShell shell;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ SoftReference $callSiteArray;

    public GroovyShellTestCase() {
        MetaClass metaClass;
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    protected void setUp() {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        ScriptBytecodeAdapter.invokeMethodOnSuper0(GroovyTestCase.class, this, "setUp");
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[0].callCurrent(this);
            this.shell = (GroovyShell)ScriptBytecodeAdapter.castToType(object, GroovyShell.class);
        } else {
            GroovyShell groovyShell;
            this.shell = groovyShell = this.createNewShell();
        }
    }

    protected void tearDown() {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        Object var2_2 = null;
        this.shell = (GroovyShell)ScriptBytecodeAdapter.castToType(var2_2, GroovyShell.class);
        ScriptBytecodeAdapter.invokeMethodOnSuper0(GroovyTestCase.class, this, "tearDown");
    }

    protected GroovyShell createNewShell() {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (GroovyShell)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(GroovyShell.class), GroovyShell.class);
    }

    protected Object withBinding(Map map, Closure closure) {
        Object object;
        Map vars;
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        Binding binding = (Binding)ScriptBytecodeAdapter.castToType(callSiteArray[2].callGroovyObjectGetProperty(this.shell), Binding.class);
        Map bmap = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[3].callGroovyObjectGetProperty(binding), Map.class);
        try {
            vars = (Map)ScriptBytecodeAdapter.castToType(callSiteArray[4].callConstructor(HashMap.class, bmap), Map.class);
            callSiteArray[5].call((Object)bmap, map);
            object = callSiteArray[6].call(closure);
        }
        catch (Throwable throwable) {
            callSiteArray[12].call(bmap);
            callSiteArray[13].call((Object)bmap, callSiteArray[14].callGroovyObjectGetProperty(this));
            throw throwable;
        }
        callSiteArray[7].call(bmap);
        callSiteArray[8].call((Object)bmap, vars);
        return object;
    }

    /*
     * Unable to fully structure code
     */
    protected Object withBinding(Map map, String script) {
        var3_3 = GroovyShellTestCase.$getCallSiteArray();
        binding = (Binding)ScriptBytecodeAdapter.castToType(var3_3[15].callGroovyObjectGetProperty(this.shell), Binding.class);
        bmap = (Map)ScriptBytecodeAdapter.castToType(var3_3[16].callGroovyObjectGetProperty(binding), Map.class);
        try {
            vars = (Map)ScriptBytecodeAdapter.castToType(var3_3[17].callConstructor(HashMap.class, bmap), Map.class);
            var3_3[18].call((Object)bmap, map);
            if (!GroovyShellTestCase.__$stMC && !BytecodeInterface8.disabledStandardMetaClass()) ** GOTO lbl27
            var7_7 = var3_3[19].callCurrent((GroovyObject)this, script);
        }
        catch (Throwable var9_9) {
            var3_3[27].call(bmap);
            var3_3[28].call((Object)bmap, var3_3[29].callGroovyObjectGetProperty(this));
            throw var9_9;
        }
        var3_3[20].call(bmap);
        var3_3[21].call((Object)bmap, vars);
        return var7_7;
lbl27:
        // 1 sources

        var8_8 = this.evaluate(script);
        var3_3[22].call(bmap);
        var3_3[23].call((Object)bmap, vars);
        return var8_8;
    }

    public Object run(GroovyCodeSource param0, String ... param1) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[30].call(this.shell, param0, param1);
    }

    public Object run(GroovyCodeSource param0, List param1) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[31].call(this.shell, param0, param1);
    }

    public Object run(String param0, String param1, String ... param2) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[32].call(this.shell, param0, param1, param2);
    }

    public Object run(File param0, String ... param1) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[33].call(this.shell, param0, param1);
    }

    public Object run(Reader param0, String param1, String ... param2) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[34].call(this.shell, param0, param1, param2);
    }

    public Object run(Reader param0, String param1, List param2) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[35].call(this.shell, param0, param1, param2);
    }

    public Object run(URI param0, String ... param1) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[36].call(this.shell, param0, param1);
    }

    public Object run(URI param0, List param1) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[37].call(this.shell, param0, param1);
    }

    public Object run(String param0, String param1, List param2) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[38].call(this.shell, param0, param1, param2);
    }

    public Object run(File param0, List param1) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[39].call(this.shell, param0, param1);
    }

    public GroovyClassLoader getClassLoader() {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (GroovyClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[40].call(this.shell), GroovyClassLoader.class);
    }

    public Binding getContext() {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Binding)ScriptBytecodeAdapter.castToType(callSiteArray[41].call(this.shell), Binding.class);
    }

    public Script parse(String param0, String param1) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Script)ScriptBytecodeAdapter.castToType(callSiteArray[42].call(this.shell, param0, param1), Script.class);
    }

    public Script parse(Reader param0) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Script)ScriptBytecodeAdapter.castToType(callSiteArray[43].call((Object)this.shell, param0), Script.class);
    }

    public Script parse(File param0) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Script)ScriptBytecodeAdapter.castToType(callSiteArray[44].call((Object)this.shell, param0), Script.class);
    }

    public Script parse(Reader param0, String param1) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Script)ScriptBytecodeAdapter.castToType(callSiteArray[45].call(this.shell, param0, param1), Script.class);
    }

    public Script parse(GroovyCodeSource param0) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Script)ScriptBytecodeAdapter.castToType(callSiteArray[46].call((Object)this.shell, param0), Script.class);
    }

    public Script parse(URI param0) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Script)ScriptBytecodeAdapter.castToType(callSiteArray[47].call((Object)this.shell, param0), Script.class);
    }

    public Script parse(String param0) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return (Script)ScriptBytecodeAdapter.castToType(callSiteArray[48].call((Object)this.shell, param0), Script.class);
    }

    public Object evaluate(URI param0) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[49].call((Object)this.shell, param0);
    }

    public Object evaluate(File param0) throws CompilationFailedException, IOException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[50].call((Object)this.shell, param0);
    }

    public Object evaluate(String param0, String param1, String param2) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[51].call(this.shell, param0, param1, param2);
    }

    public Object evaluate(Reader param0) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[52].call((Object)this.shell, param0);
    }

    public Object evaluate(Reader param0, String param1) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[53].call(this.shell, param0, param1);
    }

    public Object evaluate(GroovyCodeSource param0) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[54].call((Object)this.shell, param0);
    }

    public Object evaluate(String param0) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[55].call((Object)this.shell, param0);
    }

    public Object evaluate(String param0, String param1) throws CompilationFailedException {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[56].call(this.shell, param0, param1);
    }

    public void setVariable(String param0, Object param1) {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        callSiteArray[57].call(this.shell, param0, param1);
    }

    public Object getVariable(String param0) {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        return callSiteArray[58].call((Object)this.shell, param0);
    }

    public void resetLoadedClasses() {
        CallSite[] callSiteArray = GroovyShellTestCase.$getCallSiteArray();
        callSiteArray[59].call(this.shell);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != GroovyShellTestCase.class) {
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

    public /* synthetic */ void super$3$setUp() {
        super.setUp();
    }

    public /* synthetic */ void super$3$tearDown() {
        super.tearDown();
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "createNewShell";
        stringArray[1] = "<$constructor$>";
        stringArray[2] = "context";
        stringArray[3] = "variables";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "putAll";
        stringArray[6] = "call";
        stringArray[7] = "clear";
        stringArray[8] = "putAll";
        stringArray[9] = "clear";
        stringArray[10] = "putAll";
        stringArray[11] = "vars";
        stringArray[12] = "clear";
        stringArray[13] = "putAll";
        stringArray[14] = "vars";
        stringArray[15] = "context";
        stringArray[16] = "variables";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "putAll";
        stringArray[19] = "evaluate";
        stringArray[20] = "clear";
        stringArray[21] = "putAll";
        stringArray[22] = "clear";
        stringArray[23] = "putAll";
        stringArray[24] = "clear";
        stringArray[25] = "putAll";
        stringArray[26] = "vars";
        stringArray[27] = "clear";
        stringArray[28] = "putAll";
        stringArray[29] = "vars";
        stringArray[30] = "run";
        stringArray[31] = "run";
        stringArray[32] = "run";
        stringArray[33] = "run";
        stringArray[34] = "run";
        stringArray[35] = "run";
        stringArray[36] = "run";
        stringArray[37] = "run";
        stringArray[38] = "run";
        stringArray[39] = "run";
        stringArray[40] = "getClassLoader";
        stringArray[41] = "getContext";
        stringArray[42] = "parse";
        stringArray[43] = "parse";
        stringArray[44] = "parse";
        stringArray[45] = "parse";
        stringArray[46] = "parse";
        stringArray[47] = "parse";
        stringArray[48] = "parse";
        stringArray[49] = "evaluate";
        stringArray[50] = "evaluate";
        stringArray[51] = "evaluate";
        stringArray[52] = "evaluate";
        stringArray[53] = "evaluate";
        stringArray[54] = "evaluate";
        stringArray[55] = "evaluate";
        stringArray[56] = "evaluate";
        stringArray[57] = "setVariable";
        stringArray[58] = "getVariable";
        stringArray[59] = "resetLoadedClasses";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[60];
        GroovyShellTestCase.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(GroovyShellTestCase.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = GroovyShellTestCase.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


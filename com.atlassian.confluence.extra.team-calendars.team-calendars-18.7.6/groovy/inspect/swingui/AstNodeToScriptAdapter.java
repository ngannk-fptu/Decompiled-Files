/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.inspect.swingui.AstNodeToScriptVisitor;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.io.File;
import java.io.StringWriter;
import java.lang.ref.SoftReference;
import org.codehaus.groovy.control.CompilationFailedException;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class AstNodeToScriptAdapter
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AstNodeToScriptAdapter() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AstNodeToScriptAdapter.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public static void main(String ... args) {
        CallSite[] callSiteArray = AstNodeToScriptAdapter.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(args) || ScriptBytecodeAdapter.compareLessThan(callSiteArray[0].callGetProperty(args), 2)) {
                callSiteArray[1].callStatic(AstNodeToScriptAdapter.class, "\nUsage: java groovy.inspect.swingui.AstNodeToScriptAdapter [filename] [compilephase]\nwhere [filename] is a Groovy script\nand [compilephase] is a valid Integer based org.codehaus.groovy.control.CompilePhase");
            } else {
                Object file = callSiteArray[2].callConstructor(File.class, ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[3].call((Object)args, 0)), String.class));
                Object phase = callSiteArray[4].call(CompilePhase.class, ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[5].call((Object)args, 1), Integer.TYPE)), Integer.TYPE));
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[6].call(file))) {
                    callSiteArray[7].callStatic(AstNodeToScriptAdapter.class, new GStringImpl(new Object[]{callSiteArray[8].call((Object)args, 0)}, new String[]{"File ", " cannot be found."}));
                } else if (ScriptBytecodeAdapter.compareEqual(phase, null)) {
                    callSiteArray[9].callStatic(AstNodeToScriptAdapter.class, new GStringImpl(new Object[]{callSiteArray[10].call((Object)args, 1)}, new String[]{"Compile phase ", " cannot be mapped to a org.codehaus.groovy.control.CompilePhase."}));
                } else {
                    callSiteArray[11].callStatic(AstNodeToScriptAdapter.class, callSiteArray[12].call(callSiteArray[13].callConstructor(AstNodeToScriptAdapter.class), callSiteArray[14].callGetProperty(file), callSiteArray[15].call(phase)));
                }
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(args) || ScriptBytecodeAdapter.compareLessThan(callSiteArray[16].callGetProperty(args), 2)) {
            callSiteArray[17].callStatic(AstNodeToScriptAdapter.class, "\nUsage: java groovy.inspect.swingui.AstNodeToScriptAdapter [filename] [compilephase]\nwhere [filename] is a Groovy script\nand [compilephase] is a valid Integer based org.codehaus.groovy.control.CompilePhase");
        } else {
            Object file = callSiteArray[18].callConstructor(File.class, ScriptBytecodeAdapter.createPojoWrapper(ShortTypeHandling.castToString(callSiteArray[19].call((Object)args, 0)), String.class));
            Object phase = callSiteArray[20].call(CompilePhase.class, ScriptBytecodeAdapter.createPojoWrapper(DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.asType(callSiteArray[21].call((Object)args, 1), Integer.TYPE)), Integer.TYPE));
            if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[22].call(file))) {
                callSiteArray[23].callStatic(AstNodeToScriptAdapter.class, new GStringImpl(new Object[]{callSiteArray[24].call((Object)args, 0)}, new String[]{"File ", " cannot be found."}));
            } else if (ScriptBytecodeAdapter.compareEqual(phase, null)) {
                callSiteArray[25].callStatic(AstNodeToScriptAdapter.class, new GStringImpl(new Object[]{callSiteArray[26].call((Object)args, 1)}, new String[]{"Compile phase ", " cannot be mapped to a org.codehaus.groovy.control.CompilePhase."}));
            } else {
                callSiteArray[27].callStatic(AstNodeToScriptAdapter.class, callSiteArray[28].call(callSiteArray[29].callConstructor(AstNodeToScriptAdapter.class), callSiteArray[30].callGetProperty(file), callSiteArray[31].call(phase)));
            }
        }
    }

    public String compileToScript(String script, int compilePhase, ClassLoader classLoader, boolean showScriptFreeForm, boolean showScriptClass) {
        ClassLoader classLoader2;
        CallSite[] callSiteArray = AstNodeToScriptAdapter.$getCallSiteArray();
        Reference<Object> writer = new Reference<Object>(callSiteArray[32].callConstructor(StringWriter.class));
        ClassLoader classLoader3 = classLoader;
        classLoader = classLoader2 = DefaultTypeTransformation.booleanUnbox(classLoader3) ? classLoader3 : (ClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[33].callConstructor(GroovyClassLoader.class, callSiteArray[34].callGetProperty(callSiteArray[35].callCurrent(this))), ClassLoader.class);
        Object scriptName = callSiteArray[36].call(callSiteArray[37].call((Object)"script", callSiteArray[38].call(System.class)), ".groovy");
        GroovyCodeSource codeSource = (GroovyCodeSource)ScriptBytecodeAdapter.castToType(callSiteArray[39].callConstructor(GroovyCodeSource.class, script, scriptName, "/groovy/script"), GroovyCodeSource.class);
        CompilationUnit cu = (CompilationUnit)ScriptBytecodeAdapter.castToType(callSiteArray[40].callConstructor(CompilationUnit.class, callSiteArray[41].callGetProperty(CompilerConfiguration.class), callSiteArray[42].callGetProperty(codeSource), classLoader), CompilationUnit.class);
        callSiteArray[43].call(cu, callSiteArray[44].callConstructor(AstNodeToScriptVisitor.class, writer.get(), showScriptFreeForm, showScriptClass), compilePhase);
        callSiteArray[45].call(cu, callSiteArray[46].call(codeSource), script);
        try {
            callSiteArray[47].call((Object)cu, compilePhase);
        }
        catch (CompilationFailedException cfe) {
            callSiteArray[48].call(writer.get(), "Unable to produce AST for this phase due to earlier compilation error:");
            public class _compileToScript_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference writer;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _compileToScript_closure1(Object _outerInstance, Object _thisObject, Reference writer) {
                    Reference reference;
                    CallSite[] callSiteArray = _compileToScript_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.writer = reference = writer;
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _compileToScript_closure1.$getCallSiteArray();
                    return callSiteArray[0].call(this.writer.get(), it);
                }

                public Object getWriter() {
                    CallSite[] callSiteArray = _compileToScript_closure1.$getCallSiteArray();
                    return this.writer.get();
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _compileToScript_closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _compileToScript_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[1];
                    stringArray[0] = "println";
                    return new CallSiteArray(_compileToScript_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _compileToScript_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[49].call(callSiteArray[50].callGetProperty(cfe), new _compileToScript_closure1(this, this, writer));
            callSiteArray[51].call(writer.get(), "Fix the above error(s) and then press Refresh");
        }
        catch (Throwable t) {
            callSiteArray[52].call(writer.get(), "Unable to produce AST for this phase due to an error:");
            callSiteArray[53].call(writer.get(), callSiteArray[54].call(t));
            callSiteArray[55].call(writer.get(), "Fix the above error(s) and then press Refresh");
        }
        return ShortTypeHandling.castToString(callSiteArray[56].call(writer.get()));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AstNodeToScriptAdapter.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public String compileToScript(String script, int compilePhase, ClassLoader classLoader, boolean showScriptFreeForm) {
        CallSite[] callSiteArray = AstNodeToScriptAdapter.$getCallSiteArray();
        return this.compileToScript(script, compilePhase, classLoader, showScriptFreeForm, true);
    }

    public String compileToScript(String script, int compilePhase, ClassLoader classLoader) {
        CallSite[] callSiteArray = AstNodeToScriptAdapter.$getCallSiteArray();
        return this.compileToScript(script, compilePhase, classLoader, true, true);
    }

    public String compileToScript(String script, int compilePhase) {
        CallSite[] callSiteArray = AstNodeToScriptAdapter.$getCallSiteArray();
        return this.compileToScript(script, compilePhase, null, true, true);
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
        stringArray[0] = "length";
        stringArray[1] = "println";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "getAt";
        stringArray[4] = "fromPhaseNumber";
        stringArray[5] = "getAt";
        stringArray[6] = "exists";
        stringArray[7] = "println";
        stringArray[8] = "getAt";
        stringArray[9] = "println";
        stringArray[10] = "getAt";
        stringArray[11] = "println";
        stringArray[12] = "compileToScript";
        stringArray[13] = "<$constructor$>";
        stringArray[14] = "text";
        stringArray[15] = "getPhaseNumber";
        stringArray[16] = "length";
        stringArray[17] = "println";
        stringArray[18] = "<$constructor$>";
        stringArray[19] = "getAt";
        stringArray[20] = "fromPhaseNumber";
        stringArray[21] = "getAt";
        stringArray[22] = "exists";
        stringArray[23] = "println";
        stringArray[24] = "getAt";
        stringArray[25] = "println";
        stringArray[26] = "getAt";
        stringArray[27] = "println";
        stringArray[28] = "compileToScript";
        stringArray[29] = "<$constructor$>";
        stringArray[30] = "text";
        stringArray[31] = "getPhaseNumber";
        stringArray[32] = "<$constructor$>";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "classLoader";
        stringArray[35] = "getClass";
        stringArray[36] = "plus";
        stringArray[37] = "plus";
        stringArray[38] = "currentTimeMillis";
        stringArray[39] = "<$constructor$>";
        stringArray[40] = "<$constructor$>";
        stringArray[41] = "DEFAULT";
        stringArray[42] = "codeSource";
        stringArray[43] = "addPhaseOperation";
        stringArray[44] = "<$constructor$>";
        stringArray[45] = "addSource";
        stringArray[46] = "getName";
        stringArray[47] = "compile";
        stringArray[48] = "println";
        stringArray[49] = "eachLine";
        stringArray[50] = "message";
        stringArray[51] = "println";
        stringArray[52] = "println";
        stringArray[53] = "println";
        stringArray[54] = "getMessage";
        stringArray[55] = "println";
        stringArray[56] = "toString";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[57];
        AstNodeToScriptAdapter.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AstNodeToScriptAdapter.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AstNodeToScriptAdapter.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


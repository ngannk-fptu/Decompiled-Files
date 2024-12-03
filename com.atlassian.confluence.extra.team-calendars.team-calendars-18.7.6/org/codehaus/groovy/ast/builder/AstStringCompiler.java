/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.builder;

import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyCodeSource;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

class AstStringCompiler
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AstStringCompiler() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AstStringCompiler.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public List<ASTNode> compile(String script, CompilePhase compilePhase, boolean statementsOnly) {
        Reference<Boolean> statementsOnly2 = new Reference<Boolean>(statementsOnly);
        CallSite[] callSiteArray = AstStringCompiler.$getCallSiteArray();
        Reference<Object> scriptClassName = new Reference<Object>(callSiteArray[0].call((Object)"script", callSiteArray[1].call(System.class)));
        GroovyClassLoader classLoader = (GroovyClassLoader)ScriptBytecodeAdapter.castToType(callSiteArray[2].callConstructor(GroovyClassLoader.class), GroovyClassLoader.class);
        GroovyCodeSource codeSource = (GroovyCodeSource)ScriptBytecodeAdapter.castToType(callSiteArray[3].callConstructor(GroovyCodeSource.class, script, callSiteArray[4].call(scriptClassName.get(), ".groovy"), "/groovy/script"), GroovyCodeSource.class);
        CompilationUnit cu = (CompilationUnit)ScriptBytecodeAdapter.castToType(callSiteArray[5].callConstructor(CompilationUnit.class, callSiteArray[6].callGetProperty(CompilerConfiguration.class), callSiteArray[7].callGetProperty(codeSource), classLoader), CompilationUnit.class);
        callSiteArray[8].call(cu, callSiteArray[9].call(codeSource), script);
        callSiteArray[10].call((Object)cu, callSiteArray[11].call((Object)compilePhase));
        public class _compile_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference scriptClassName;
            private /* synthetic */ Reference statementsOnly;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _compile_closure1(Object _outerInstance, Object _thisObject, Reference scriptClassName, Reference statementsOnly) {
                Reference reference;
                Reference reference2;
                CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.scriptClassName = reference2 = scriptClassName;
                this.statementsOnly = reference = statementsOnly;
            }

            public Object doCall(List acc, ModuleNode node) {
                Reference<List> acc2 = new Reference<List>(acc);
                CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callGetProperty(node))) {
                    callSiteArray[1].call((Object)acc2.get(), callSiteArray[2].callGetProperty(node));
                }
                public class _closure2
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference scriptClassName;
                    private /* synthetic */ Reference statementsOnly;
                    private /* synthetic */ Reference acc;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure2(Object _outerInstance, Object _thisObject, Reference scriptClassName, Reference statementsOnly, Reference acc) {
                        Reference reference;
                        Reference reference2;
                        Reference reference3;
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.scriptClassName = reference3 = scriptClassName;
                        this.statementsOnly = reference2 = statementsOnly;
                        this.acc = reference = acc;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                            if (!(ScriptBytecodeAdapter.compareEqual(callSiteArray[0].callGetProperty(it), this.scriptClassName.get()) && DefaultTypeTransformation.booleanUnbox(this.statementsOnly.get()))) {
                                return callSiteArray[1].call(this.acc.get(), it);
                            }
                            return null;
                        }
                        if (!(ScriptBytecodeAdapter.compareEqual(callSiteArray[2].callGetProperty(it), this.scriptClassName.get()) && DefaultTypeTransformation.booleanUnbox(this.statementsOnly.get()))) {
                            return callSiteArray[3].call(this.acc.get(), it);
                        }
                        return null;
                    }

                    public Object getScriptClassName() {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return this.scriptClassName.get();
                    }

                    public boolean getStatementsOnly() {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return DefaultTypeTransformation.booleanUnbox(this.statementsOnly.get());
                    }

                    public List getAcc() {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return (List)ScriptBytecodeAdapter.castToType(this.acc.get(), List.class);
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _closure2.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure2.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "name";
                        stringArray[1] = "leftShift";
                        stringArray[2] = "name";
                        stringArray[3] = "leftShift";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _closure2.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_closure2.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure2.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[3].callSafe(callSiteArray[4].callGetProperty(node), new _closure2(this, this.getThisObject(), this.scriptClassName, this.statementsOnly, acc2));
                return acc2.get();
            }

            public Object call(List acc, ModuleNode node) {
                Reference<List> acc2 = new Reference<List>(acc);
                CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                return callSiteArray[5].callCurrent(this, acc2.get(), node);
            }

            public Object getScriptClassName() {
                CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                return this.scriptClassName.get();
            }

            public boolean getStatementsOnly() {
                CallSite[] callSiteArray = _compile_closure1.$getCallSiteArray();
                return DefaultTypeTransformation.booleanUnbox(this.statementsOnly.get());
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _compile_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "statementBlock";
                stringArray[1] = "add";
                stringArray[2] = "statementBlock";
                stringArray[3] = "each";
                stringArray[4] = "classes";
                stringArray[5] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _compile_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_compile_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _compile_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[12].call(callSiteArray[13].callGetProperty(callSiteArray[14].callGetProperty(cu)), ScriptBytecodeAdapter.createList(new Object[0]), new _compile_closure1(this, this, scriptClassName, statementsOnly2)), List.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AstStringCompiler.class) {
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
        stringArray[0] = "plus";
        stringArray[1] = "currentTimeMillis";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "<$constructor$>";
        stringArray[4] = "plus";
        stringArray[5] = "<$constructor$>";
        stringArray[6] = "DEFAULT";
        stringArray[7] = "codeSource";
        stringArray[8] = "addSource";
        stringArray[9] = "getName";
        stringArray[10] = "compile";
        stringArray[11] = "getPhaseNumber";
        stringArray[12] = "inject";
        stringArray[13] = "modules";
        stringArray[14] = "ast";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[15];
        AstStringCompiler.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AstStringCompiler.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AstStringCompiler.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


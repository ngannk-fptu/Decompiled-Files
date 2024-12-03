/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast.builder;

import groovy.lang.Closure;
import groovy.lang.DelegatesTo;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.builder.AstSpecificationCompiler;
import org.codehaus.groovy.ast.builder.AstStringCompiler;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;

public class AstBuilder
implements GroovyObject {
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public AstBuilder() {
        MetaClass metaClass;
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public List<ASTNode> buildFromCode(CompilePhase phase, boolean statementsOnly, Closure block) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        throw (Throwable)callSiteArray[0].callConstructor(IllegalStateException.class, "AstBuilder.build(CompilePhase, boolean, Closure):List<ASTNode> should never be called at runtime.\nAre you sure you are using it correctly?\n");
    }

    public List<ASTNode> buildFromString(CompilePhase phase, boolean statementsOnly, String source) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(source) || ScriptBytecodeAdapter.compareEqual("", callSiteArray[1].call(source))) {
                throw (Throwable)callSiteArray[2].callConstructor(IllegalArgumentException.class, "A source must be specified");
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(source) || ScriptBytecodeAdapter.compareEqual("", callSiteArray[3].call(source))) {
            throw (Throwable)callSiteArray[4].callConstructor(IllegalArgumentException.class, "A source must be specified");
        }
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[5].call(callSiteArray[6].callConstructor(AstStringCompiler.class), source, (Object)phase, statementsOnly), List.class);
    }

    private List<ASTNode> buildFromBlock(CompilePhase phase, boolean statementsOnly, String source) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(source) || ScriptBytecodeAdapter.compareEqual("", callSiteArray[7].call(source))) {
                throw (Throwable)callSiteArray[8].callConstructor(IllegalArgumentException.class, "A source must be specified");
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(source) || ScriptBytecodeAdapter.compareEqual("", callSiteArray[9].call(source))) {
            throw (Throwable)callSiteArray[10].callConstructor(IllegalArgumentException.class, "A source must be specified");
        }
        Object labelledSource = callSiteArray[11].call((Object)new GStringImpl(new Object[]{callSiteArray[12].call(System.class)}, new String[]{"__synthesized__label__", "__:"}), source);
        List result = (List)ScriptBytecodeAdapter.castToType(callSiteArray[13].call(callSiteArray[14].callConstructor(AstStringCompiler.class), labelledSource, (Object)phase, statementsOnly), List.class);
        public class _buildFromBlock_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _buildFromBlock_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _buildFromBlock_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object node) {
                CallSite[] callSiteArray = _buildFromBlock_closure1.$getCallSiteArray();
                if (node instanceof BlockStatement) {
                    return callSiteArray[0].call(callSiteArray[1].callGetProperty((BlockStatement)ScriptBytecodeAdapter.castToType(node, BlockStatement.class)), 0);
                }
                return node;
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _buildFromBlock_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getAt";
                stringArray[1] = "statements";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _buildFromBlock_closure1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_buildFromBlock_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _buildFromBlock_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[15].call((Object)result, new _buildFromBlock_closure1(this, this)), List.class);
    }

    public List<ASTNode> buildFromSpec(@DelegatesTo(value=AstSpecificationCompiler.class) Closure specification) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(specification, null)) {
            throw (Throwable)callSiteArray[16].callConstructor(IllegalArgumentException.class, "Null: specification");
        }
        Object properties = callSiteArray[17].callConstructor(AstSpecificationCompiler.class, specification);
        return (List)ScriptBytecodeAdapter.castToType(callSiteArray[18].callGetProperty(properties), List.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != AstBuilder.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public List<ASTNode> buildFromCode(CompilePhase phase, Closure block) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        return this.buildFromCode(phase, true, block);
    }

    public List<ASTNode> buildFromCode(Closure block) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        return this.buildFromCode((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[19].callGetProperty(CompilePhase.class), CompilePhase.class), true, block);
    }

    public List<ASTNode> buildFromString(CompilePhase phase, String source) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return this.buildFromString(phase, true, source);
        }
        return this.buildFromString(phase, true, source);
    }

    public List<ASTNode> buildFromString(String source) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return this.buildFromString((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[20].callGetProperty(CompilePhase.class), CompilePhase.class), true, source);
        }
        return this.buildFromString((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[21].callGetProperty(CompilePhase.class), CompilePhase.class), true, source);
    }

    private List<ASTNode> buildFromBlock(CompilePhase phase, String source) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return this.buildFromBlock(phase, true, source);
        }
        return this.buildFromBlock(phase, true, source);
    }

    private List<ASTNode> buildFromBlock(String source) {
        CallSite[] callSiteArray = AstBuilder.$getCallSiteArray();
        if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            return this.buildFromBlock((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[22].callGetProperty(CompilePhase.class), CompilePhase.class), true, source);
        }
        return this.buildFromBlock((CompilePhase)ShortTypeHandling.castToEnum(callSiteArray[23].callGetProperty(CompilePhase.class), CompilePhase.class), true, source);
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
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "trim";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "trim";
        stringArray[4] = "<$constructor$>";
        stringArray[5] = "compile";
        stringArray[6] = "<$constructor$>";
        stringArray[7] = "trim";
        stringArray[8] = "<$constructor$>";
        stringArray[9] = "trim";
        stringArray[10] = "<$constructor$>";
        stringArray[11] = "plus";
        stringArray[12] = "currentTimeMillis";
        stringArray[13] = "compile";
        stringArray[14] = "<$constructor$>";
        stringArray[15] = "collect";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "<$constructor$>";
        stringArray[18] = "expression";
        stringArray[19] = "CLASS_GENERATION";
        stringArray[20] = "CLASS_GENERATION";
        stringArray[21] = "CLASS_GENERATION";
        stringArray[22] = "CLASS_GENERATION";
        stringArray[23] = "CLASS_GENERATION";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[24];
        AstBuilder.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(AstBuilder.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = AstBuilder.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


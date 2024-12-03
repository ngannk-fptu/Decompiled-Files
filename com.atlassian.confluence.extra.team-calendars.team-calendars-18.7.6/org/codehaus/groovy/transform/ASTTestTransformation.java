/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.Binding;
import groovy.lang.Closure;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyObject;
import groovy.lang.GroovyShell;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.transform.CompilationUnitAware;
import java.io.File;
import java.lang.ref.SoftReference;
import java.net.URL;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilationUnit;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.CompilerConfiguration;
import org.codehaus.groovy.control.ErrorCollector;
import org.codehaus.groovy.control.Janitor;
import org.codehaus.groovy.control.ProcessingUnit;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.customizers.ImportCustomizer;
import org.codehaus.groovy.control.io.ReaderSource;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.MethodClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.tools.Utilities;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class ASTTestTransformation
extends AbstractASTTransformation
implements CompilationUnitAware,
GroovyObject {
    private CompilationUnit compilationUnit;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ASTTestTransformation() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ASTTestTransformation.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    /*
     * WARNING - void declaration
     */
    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        Object object;
        void var2_2;
        Reference<ASTNode[]> nodes2 = new Reference<ASTNode[]>(nodes);
        Reference<void> source2 = new Reference<void>(var2_2);
        CallSite[] callSiteArray = ASTTestTransformation.$getCallSiteArray();
        AnnotationNode annotationNode = null;
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object2 = callSiteArray[0].call((Object)nodes2.get(), 0);
            annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(object2, AnnotationNode.class);
        } else {
            Object object3 = BytecodeInterface8.objectArrayGet(nodes2.get(), 0);
            annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(object3, AnnotationNode.class);
        }
        Object member = callSiteArray[1].call((Object)annotationNode, "phase");
        Reference<Object> phase = new Reference<Object>(null);
        if (DefaultTypeTransformation.booleanUnbox(member)) {
            if (member instanceof VariableExpression) {
                Object object4 = callSiteArray[2].call(CompilePhase.class, callSiteArray[3].callGetProperty(member));
                phase.set(object4);
            } else if (member instanceof PropertyExpression) {
                Object object5 = callSiteArray[4].call(CompilePhase.class, callSiteArray[5].callGetProperty(member));
                phase.set(object5);
            }
            callSiteArray[6].call(annotationNode, "phase", callSiteArray[7].callStatic(GeneralUtils.class, callSiteArray[8].callStatic(GeneralUtils.class, callSiteArray[9].call(ClassHelper.class, CompilePhase.class)), callSiteArray[10].call(phase.get())));
        }
        member = object = callSiteArray[11].call((Object)annotationNode, "value");
        if (DefaultTypeTransformation.booleanUnbox(member) && !(member instanceof ClosureExpression)) {
            throw (Throwable)callSiteArray[12].callConstructor(SyntaxException.class, "ASTTest value must be a closure", callSiteArray[13].call(member), callSiteArray[14].call(member));
        }
        if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!DefaultTypeTransformation.booleanUnbox(member) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[15].call((Object)annotationNode, ASTTestTransformation.class))) {
                throw (Throwable)callSiteArray[16].callConstructor(SyntaxException.class, "Missing test expression", callSiteArray[17].call(annotationNode), callSiteArray[18].call(annotationNode));
            }
        } else if (!DefaultTypeTransformation.booleanUnbox(member) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call((Object)annotationNode, ASTTestTransformation.class))) {
            throw (Throwable)callSiteArray[20].callConstructor(SyntaxException.class, "Missing test expression", callSiteArray[21].call(annotationNode), callSiteArray[22].call(annotationNode));
        }
        callSiteArray[23].call(annotationNode, ASTTestTransformation.class, member);
        callSiteArray[24].call(callSiteArray[25].call(annotationNode), "value");
        Object pcallback = callSiteArray[26].callGetProperty(this.compilationUnit);
        Object callback = new GroovyObject(this, phase, nodes2, source2){
            public /* synthetic */ Reference source;
            public /* synthetic */ Reference nodes;
            public /* synthetic */ Reference phase;
            private Binding binding;
            public /* synthetic */ ASTTestTransformation this$0;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private transient /* synthetic */ MetaClass metaClass;
            private static /* synthetic */ SoftReference $callSiteArray;
            {
                MetaClass metaClass;
                Reference reference;
                Reference reference2;
                Reference reference3;
                ASTTestTransformation aSTTestTransformation;
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                this.this$0 = aSTTestTransformation = p0;
                this.source = reference3 = p3;
                this.nodes = reference2 = p2;
                this.phase = reference = p1;
                Object object = callSiteArray[0].callConstructor(Binding.class, callSiteArray[1].call((Object)ScriptBytecodeAdapter.createMap(new Object[0]), new _closure1(this, this)));
                this.binding = (Binding)ScriptBytecodeAdapter.castToType(object, Binding.class);
                this.metaClass = metaClass = this.$getStaticMetaClass();
            }

            @Override
            public void call(ProcessingUnit context, int phaseRef) {
                public class _call_closure5
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference customizer;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _call_closure5(Object _outerInstance, Object _thisObject, Reference customizer) {
                        Reference reference;
                        CallSite[] callSiteArray = _call_closure5.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.customizer = reference = customizer;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _call_closure5.$getCallSiteArray();
                        return callSiteArray[0].call(this.customizer.get(), callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(it)));
                    }

                    public Object getCustomizer() {
                        CallSite[] callSiteArray = _call_closure5.$getCallSiteArray();
                        return this.customizer.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _call_closure5.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _call_closure5.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "addStaticStars";
                        stringArray[1] = "className";
                        stringArray[2] = "value";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[3];
                        _call_closure5.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_call_closure5.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _call_closure5.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                public class _call_closure4
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference customizer;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _call_closure4(Object _outerInstance, Object _thisObject, Reference customizer) {
                        Reference reference;
                        CallSite[] callSiteArray = _call_closure4.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.customizer = reference = customizer;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _call_closure4.$getCallSiteArray();
                        return callSiteArray[0].call(this.customizer.get(), callSiteArray[1].callGetProperty(callSiteArray[2].callGetProperty(it)), callSiteArray[3].callGetProperty(callSiteArray[4].callGetProperty(callSiteArray[5].callGetProperty(it))), callSiteArray[6].callGetProperty(callSiteArray[7].callGetProperty(it)));
                    }

                    public Object getCustomizer() {
                        CallSite[] callSiteArray = _call_closure4.$getCallSiteArray();
                        return this.customizer.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _call_closure4.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _call_closure4.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "addStaticImport";
                        stringArray[1] = "alias";
                        stringArray[2] = "value";
                        stringArray[3] = "name";
                        stringArray[4] = "type";
                        stringArray[5] = "value";
                        stringArray[6] = "fieldName";
                        stringArray[7] = "value";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[8];
                        _call_closure4.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_call_closure4.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _call_closure4.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                public class _call_closure3
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference customizer;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _call_closure3(Object _outerInstance, Object _thisObject, Reference customizer) {
                        Reference reference;
                        CallSite[] callSiteArray = _call_closure3.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.customizer = reference = customizer;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _call_closure3.$getCallSiteArray();
                        return callSiteArray[0].call(this.customizer.get(), callSiteArray[1].callGetProperty(it));
                    }

                    public Object getCustomizer() {
                        CallSite[] callSiteArray = _call_closure3.$getCallSiteArray();
                        return this.customizer.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _call_closure3.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _call_closure3.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "addStarImports";
                        stringArray[1] = "packageName";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[2];
                        _call_closure3.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_call_closure3.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _call_closure3.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                public class _call_closure2
                extends Closure
                implements GeneratedClosure {
                    private /* synthetic */ Reference customizer;
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _call_closure2(Object _outerInstance, Object _thisObject, Reference customizer) {
                        Reference reference;
                        CallSite[] callSiteArray = _call_closure2.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                        this.customizer = reference = customizer;
                    }

                    public Object doCall(Object it) {
                        CallSite[] callSiteArray = _call_closure2.$getCallSiteArray();
                        return callSiteArray[0].call(this.customizer.get(), callSiteArray[1].callGetProperty(it), callSiteArray[2].callGetProperty(callSiteArray[3].callGetProperty(it)));
                    }

                    public Object getCustomizer() {
                        CallSite[] callSiteArray = _call_closure2.$getCallSiteArray();
                        return this.customizer.get();
                    }

                    public Object doCall() {
                        CallSite[] callSiteArray = _call_closure2.$getCallSiteArray();
                        return this.doCall(null);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _call_closure2.class) {
                            return ScriptBytecodeAdapter.initMetaClass(this);
                        }
                        ClassInfo classInfo = $staticClassInfo;
                        if (classInfo == null) {
                            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                        }
                        return classInfo.getMetaClass();
                    }

                    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                        stringArray[0] = "addImport";
                        stringArray[1] = "alias";
                        stringArray[2] = "name";
                        stringArray[3] = "type";
                    }

                    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                        String[] stringArray = new String[4];
                        _call_closure2.$createCallSiteArray_1(stringArray);
                        return new CallSiteArray(_call_closure2.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _call_closure2.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    if (ScriptBytecodeAdapter.compareEqual(this.phase.get(), null) || ScriptBytecodeAdapter.compareEqual(phaseRef, callSiteArray[2].callGetProperty(this.phase.get()))) {
                        Object object;
                        ClosureExpression testClosure = (ClosureExpression)ScriptBytecodeAdapter.castToType(callSiteArray[3].call(callSiteArray[4].call(this.nodes.get(), 0), ASTTestTransformation.class), ClosureExpression.class);
                        StringBuilder sb = (StringBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[5].callConstructor(StringBuilder.class), StringBuilder.class);
                        int i = DefaultTypeTransformation.intUnbox(callSiteArray[6].callGetProperty(testClosure));
                        while (ScriptBytecodeAdapter.compareLessThanEqual(i, callSiteArray[7].callGetProperty(testClosure))) {
                            callSiteArray[8].call(callSiteArray[9].call((Object)sb, callSiteArray[10].call(callSiteArray[11].callGetProperty(this.source.get()), i, callSiteArray[12].callConstructor(Janitor.class))), "\n");
                            int n = i;
                            i = DefaultTypeTransformation.intUnbox(callSiteArray[13].call(n));
                        }
                        Object testSource = callSiteArray[14].call(sb, callSiteArray[15].call(callSiteArray[16].callGetProperty(testClosure), 1), callSiteArray[17].call(sb));
                        testSource = object = callSiteArray[18].call(testSource, 0, callSiteArray[19].call(testSource, "}"));
                        CompilerConfiguration config = (CompilerConfiguration)ScriptBytecodeAdapter.castToType(callSiteArray[20].callConstructor(CompilerConfiguration.class), CompilerConfiguration.class);
                        Reference<Object> customizer = new Reference<Object>(callSiteArray[21].callConstructor(ImportCustomizer.class));
                        callSiteArray[22].call((Object)config, customizer.get());
                        Object t = this.source.get();
                        callSiteArray[23].call(this.binding, "sourceUnit", t);
                        Object object2 = callSiteArray[24].call(this.nodes.get(), 1);
                        callSiteArray[25].call(this.binding, "node", object2);
                        Object object3 = callSiteArray[26].call(callSiteArray[27].callConstructor(MethodClosure.class, LabelFinder.class, "lookup"), callSiteArray[28].call(this.nodes.get(), 1));
                        callSiteArray[29].call(this.binding, "lookup", object3);
                        Object object4 = callSiteArray[30].callGroovyObjectGetProperty(this);
                        callSiteArray[31].call(this.binding, "compilationUnit", object4);
                        Object object5 = callSiteArray[32].call(CompilePhase.class, phaseRef);
                        callSiteArray[33].call(this.binding, "compilePhase", object5);
                        GroovyShell shell = (GroovyShell)ScriptBytecodeAdapter.castToType(callSiteArray[34].callConstructor(GroovyShell.class, this.binding, config), GroovyShell.class);
                        callSiteArray[35].call(callSiteArray[36].callGetProperty(callSiteArray[37].callGetProperty(this.source.get())), new _call_closure2(this, this, customizer));
                        callSiteArray[38].call(callSiteArray[39].callGetProperty(callSiteArray[40].callGetProperty(this.source.get())), new _call_closure3(this, this, customizer));
                        callSiteArray[41].call(callSiteArray[42].callGetProperty(callSiteArray[43].callGetProperty(this.source.get())), new _call_closure4(this, this, customizer));
                        callSiteArray[44].call(callSiteArray[45].callGetProperty(callSiteArray[46].callGetProperty(this.source.get())), new _call_closure5(this, this, customizer));
                        callSiteArray[47].call((Object)shell, testSource);
                    }
                } else if (ScriptBytecodeAdapter.compareEqual(this.phase.get(), null) || ScriptBytecodeAdapter.compareEqual(phaseRef, callSiteArray[48].callGetProperty(this.phase.get()))) {
                    Object object;
                    ClosureExpression testClosure = (ClosureExpression)ScriptBytecodeAdapter.castToType(callSiteArray[49].call(BytecodeInterface8.objectArrayGet((ASTNode[])ScriptBytecodeAdapter.castToType(this.nodes.get(), ASTNode[].class), 0), ASTTestTransformation.class), ClosureExpression.class);
                    StringBuilder sb = (StringBuilder)ScriptBytecodeAdapter.castToType(callSiteArray[50].callConstructor(StringBuilder.class), StringBuilder.class);
                    int i = DefaultTypeTransformation.intUnbox(callSiteArray[51].callGetProperty(testClosure));
                    while (ScriptBytecodeAdapter.compareLessThanEqual(i, callSiteArray[52].callGetProperty(testClosure))) {
                        callSiteArray[53].call(callSiteArray[54].call((Object)sb, callSiteArray[55].call(callSiteArray[56].callGetProperty(this.source.get()), i, callSiteArray[57].callConstructor(Janitor.class))), "\n");
                        int n = i;
                        int cfr_ignored_0 = n + 1;
                    }
                    Object testSource = callSiteArray[58].call(sb, callSiteArray[59].call(callSiteArray[60].callGetProperty(testClosure), 1), callSiteArray[61].call(sb));
                    testSource = object = callSiteArray[62].call(testSource, 0, callSiteArray[63].call(testSource, "}"));
                    CompilerConfiguration config = (CompilerConfiguration)ScriptBytecodeAdapter.castToType(callSiteArray[64].callConstructor(CompilerConfiguration.class), CompilerConfiguration.class);
                    Reference<Object> customizer = new Reference<Object>(callSiteArray[65].callConstructor(ImportCustomizer.class));
                    callSiteArray[66].call((Object)config, customizer.get());
                    Object t = this.source.get();
                    callSiteArray[67].call(this.binding, "sourceUnit", t);
                    Object object6 = BytecodeInterface8.objectArrayGet((ASTNode[])ScriptBytecodeAdapter.castToType(this.nodes.get(), ASTNode[].class), 1);
                    callSiteArray[68].call(this.binding, "node", object6);
                    Object object7 = callSiteArray[69].call(callSiteArray[70].callConstructor(MethodClosure.class, LabelFinder.class, "lookup"), BytecodeInterface8.objectArrayGet((ASTNode[])ScriptBytecodeAdapter.castToType(this.nodes.get(), ASTNode[].class), 1));
                    callSiteArray[71].call(this.binding, "lookup", object7);
                    Object object8 = callSiteArray[72].callGroovyObjectGetProperty(this);
                    callSiteArray[73].call(this.binding, "compilationUnit", object8);
                    Object object9 = callSiteArray[74].call(CompilePhase.class, phaseRef);
                    callSiteArray[75].call(this.binding, "compilePhase", object9);
                    GroovyShell shell = (GroovyShell)ScriptBytecodeAdapter.castToType(callSiteArray[76].callConstructor(GroovyShell.class, this.binding, config), GroovyShell.class);
                    callSiteArray[77].call(callSiteArray[78].callGetProperty(callSiteArray[79].callGetProperty(this.source.get())), new _call_closure2(this, this, customizer));
                    callSiteArray[80].call(callSiteArray[81].callGetProperty(callSiteArray[82].callGetProperty(this.source.get())), new _call_closure3(this, this, customizer));
                    callSiteArray[83].call(callSiteArray[84].callGetProperty(callSiteArray[85].callGetProperty(this.source.get())), new _call_closure4(this, this, customizer));
                    callSiteArray[86].call(callSiteArray[87].callGetProperty(callSiteArray[88].callGetProperty(this.source.get())), new _call_closure5(this, this, customizer));
                    callSiteArray[89].call((Object)shell, testSource);
                }
            }

            public /* synthetic */ Object methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return this.this$0.this$dist$invoke$2(name, args);
            }

            public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodN(1.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
            }

            public /* synthetic */ void propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                this.this$0.this$dist$set$2(name, val);
            }

            public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                Object object = val;
                ScriptBytecodeAdapter.setProperty(object, null, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            public /* synthetic */ Object propertyMissing(String name) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return this.this$0.this$dist$get$2(name);
            }

            public static /* synthetic */ Object $static_propertyMissing(String name) {
                CallSite[] callSiteArray = 1.$getCallSiteArray();
                return ScriptBytecodeAdapter.getProperty(1.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != 1.class) {
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

            public Binding getBinding() {
                return this.binding;
            }

            public void setBinding(Binding binding) {
                this.binding = binding;
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "<$constructor$>";
                stringArray[1] = "withDefault";
                stringArray[2] = "phaseNumber";
                stringArray[3] = "getNodeMetaData";
                stringArray[4] = "getAt";
                stringArray[5] = "<$constructor$>";
                stringArray[6] = "lineNumber";
                stringArray[7] = "lastLineNumber";
                stringArray[8] = "append";
                stringArray[9] = "append";
                stringArray[10] = "getLine";
                stringArray[11] = "source";
                stringArray[12] = "<$constructor$>";
                stringArray[13] = "next";
                stringArray[14] = "substring";
                stringArray[15] = "plus";
                stringArray[16] = "columnNumber";
                stringArray[17] = "length";
                stringArray[18] = "substring";
                stringArray[19] = "lastIndexOf";
                stringArray[20] = "<$constructor$>";
                stringArray[21] = "<$constructor$>";
                stringArray[22] = "addCompilationCustomizers";
                stringArray[23] = "putAt";
                stringArray[24] = "getAt";
                stringArray[25] = "putAt";
                stringArray[26] = "curry";
                stringArray[27] = "<$constructor$>";
                stringArray[28] = "getAt";
                stringArray[29] = "putAt";
                stringArray[30] = "compilationUnit";
                stringArray[31] = "putAt";
                stringArray[32] = "fromPhaseNumber";
                stringArray[33] = "putAt";
                stringArray[34] = "<$constructor$>";
                stringArray[35] = "each";
                stringArray[36] = "imports";
                stringArray[37] = "AST";
                stringArray[38] = "each";
                stringArray[39] = "starImports";
                stringArray[40] = "AST";
                stringArray[41] = "each";
                stringArray[42] = "staticImports";
                stringArray[43] = "AST";
                stringArray[44] = "each";
                stringArray[45] = "staticStarImports";
                stringArray[46] = "AST";
                stringArray[47] = "evaluate";
                stringArray[48] = "phaseNumber";
                stringArray[49] = "getNodeMetaData";
                stringArray[50] = "<$constructor$>";
                stringArray[51] = "lineNumber";
                stringArray[52] = "lastLineNumber";
                stringArray[53] = "append";
                stringArray[54] = "append";
                stringArray[55] = "getLine";
                stringArray[56] = "source";
                stringArray[57] = "<$constructor$>";
                stringArray[58] = "substring";
                stringArray[59] = "plus";
                stringArray[60] = "columnNumber";
                stringArray[61] = "length";
                stringArray[62] = "substring";
                stringArray[63] = "lastIndexOf";
                stringArray[64] = "<$constructor$>";
                stringArray[65] = "<$constructor$>";
                stringArray[66] = "addCompilationCustomizers";
                stringArray[67] = "putAt";
                stringArray[68] = "putAt";
                stringArray[69] = "curry";
                stringArray[70] = "<$constructor$>";
                stringArray[71] = "putAt";
                stringArray[72] = "compilationUnit";
                stringArray[73] = "putAt";
                stringArray[74] = "fromPhaseNumber";
                stringArray[75] = "putAt";
                stringArray[76] = "<$constructor$>";
                stringArray[77] = "each";
                stringArray[78] = "imports";
                stringArray[79] = "AST";
                stringArray[80] = "each";
                stringArray[81] = "starImports";
                stringArray[82] = "AST";
                stringArray[83] = "each";
                stringArray[84] = "staticImports";
                stringArray[85] = "AST";
                stringArray[86] = "each";
                stringArray[87] = "staticStarImports";
                stringArray[88] = "AST";
                stringArray[89] = "evaluate";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[90];
                1.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = 1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }

            public class _closure1
            extends Closure
            implements GeneratedClosure {
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _closure1(Object _outerInstance, Object _thisObject) {
                    CallSite[] callSiteArray = _closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                }

                public Object doCall(Object it) {
                    CallSite[] callSiteArray = _closure1.$getCallSiteArray();
                    return null;
                }

                public Object doCall() {
                    CallSite[] callSiteArray = _closure1.$getCallSiteArray();
                    return this.doCall(null);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[]{};
                    return new CallSiteArray(_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
        };
        if (ScriptBytecodeAdapter.compareNotEqual(pcallback, null)) {
            Object object6;
            if (pcallback instanceof ProgressCallbackChain) {
                callSiteArray[27].call(pcallback, callback);
            } else {
                Object object7;
                pcallback = object7 = callSiteArray[28].callConstructor(ProgressCallbackChain.class, pcallback, callback);
            }
            callback = object6 = pcallback;
        }
        callSiteArray[29].call((Object)this.compilationUnit, callback);
    }

    @Override
    public void setCompilationUnit(CompilationUnit unit) {
        CallSite[] callSiteArray = ASTTestTransformation.$getCallSiteArray();
        CompilationUnit compilationUnit = unit;
        this.compilationUnit = (CompilationUnit)ScriptBytecodeAdapter.castToType(compilationUnit, CompilationUnit.class);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ASTTestTransformation.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object this$dist$invoke$2(String name, Object args) {
        CallSite[] callSiteArray = ASTTestTransformation.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(ASTTestTransformation.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$2(String name, Object value) {
        CallSite[] callSiteArray = ASTTestTransformation.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, ASTTestTransformation.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$2(String name) {
        CallSite[] callSiteArray = ASTTestTransformation.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(ASTTestTransformation.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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
        stringArray[0] = "getAt";
        stringArray[1] = "getMember";
        stringArray[2] = "valueOf";
        stringArray[3] = "text";
        stringArray[4] = "valueOf";
        stringArray[5] = "propertyAsString";
        stringArray[6] = "setMember";
        stringArray[7] = "propX";
        stringArray[8] = "classX";
        stringArray[9] = "make";
        stringArray[10] = "toString";
        stringArray[11] = "getMember";
        stringArray[12] = "<$constructor$>";
        stringArray[13] = "getLineNumber";
        stringArray[14] = "getColumnNumber";
        stringArray[15] = "getNodeMetaData";
        stringArray[16] = "<$constructor$>";
        stringArray[17] = "getLineNumber";
        stringArray[18] = "getColumnNumber";
        stringArray[19] = "getNodeMetaData";
        stringArray[20] = "<$constructor$>";
        stringArray[21] = "getLineNumber";
        stringArray[22] = "getColumnNumber";
        stringArray[23] = "putNodeMetaData";
        stringArray[24] = "remove";
        stringArray[25] = "getMembers";
        stringArray[26] = "progressCallback";
        stringArray[27] = "addCallback";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "setProgressCallback";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[30];
        ASTTestTransformation.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ASTTestTransformation.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ASTTestTransformation.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    static class AssertionSourceDelegatingSourceUnit
    extends SourceUnit
    implements GroovyObject {
        private final ReaderSource delegate;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ ClassInfo $staticClassInfo$;
        private static /* synthetic */ SoftReference $callSiteArray;

        public AssertionSourceDelegatingSourceUnit(String name, ReaderSource source, CompilerConfiguration flags, GroovyClassLoader loader, ErrorCollector er) {
            ReaderSource readerSource;
            MetaClass metaClass;
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            Object[] objectArray = new Object[]{name, "", flags, loader, er};
            AssertionSourceDelegatingSourceUnit assertionSourceDelegatingSourceUnit = this;
            switch (ScriptBytecodeAdapter.selectConstructorAndTransformArguments(objectArray, -1, SourceUnit.class)) {
                case -1157484601: {
                    Object[] objectArray2 = objectArray;
                    super(ShortTypeHandling.castToString(objectArray[0]), ShortTypeHandling.castToString(objectArray[1]), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[2], CompilerConfiguration.class), (GroovyClassLoader)ScriptBytecodeAdapter.castToType(objectArray[3], GroovyClassLoader.class), (ErrorCollector)ScriptBytecodeAdapter.castToType(objectArray[4], ErrorCollector.class));
                    break;
                }
                case 1149970090: {
                    Object[] objectArray2 = objectArray;
                    super((File)ScriptBytecodeAdapter.castToType(objectArray[0], File.class), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[1], CompilerConfiguration.class), (GroovyClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], GroovyClassLoader.class), (ErrorCollector)ScriptBytecodeAdapter.castToType(objectArray[3], ErrorCollector.class));
                    break;
                }
                case 1726365996: {
                    Object[] objectArray2 = objectArray;
                    super(ShortTypeHandling.castToString(objectArray[0]), (ReaderSource)ScriptBytecodeAdapter.castToType(objectArray[1], ReaderSource.class), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[2], CompilerConfiguration.class), (GroovyClassLoader)ScriptBytecodeAdapter.castToType(objectArray[3], GroovyClassLoader.class), (ErrorCollector)ScriptBytecodeAdapter.castToType(objectArray[4], ErrorCollector.class));
                    break;
                }
                case 1840971954: {
                    Object[] objectArray2 = objectArray;
                    super((URL)ScriptBytecodeAdapter.castToType(objectArray[0], URL.class), (CompilerConfiguration)ScriptBytecodeAdapter.castToType(objectArray[1], CompilerConfiguration.class), (GroovyClassLoader)ScriptBytecodeAdapter.castToType(objectArray[2], GroovyClassLoader.class), (ErrorCollector)ScriptBytecodeAdapter.castToType(objectArray[3], ErrorCollector.class));
                    break;
                }
                default: {
                    throw new IllegalArgumentException("This class has been compiled with a super class which is binary incompatible with the current super class found on classpath. You should recompile this class with the new version.");
                }
            }
            this.metaClass = metaClass = this.$getStaticMetaClass();
            this.delegate = readerSource = source;
        }

        @Override
        public String getSample(int line, int column, Janitor janitor) {
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            String sample = null;
            String text = ShortTypeHandling.castToString(callSiteArray[0].call(this.delegate, line, janitor));
            if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (ScriptBytecodeAdapter.compareNotEqual(text, null)) {
                    if (column > 0) {
                        String marker = ShortTypeHandling.castToString(callSiteArray[1].call(callSiteArray[2].call(Utilities.class, " ", callSiteArray[3].call((Object)column, 1)), "^"));
                        if (column > 40) {
                            int start = DefaultTypeTransformation.intUnbox(callSiteArray[4].call(callSiteArray[5].call((Object)column, 30), 1));
                            int end = DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.compareGreaterThan(callSiteArray[6].call((Object)column, 10), callSiteArray[7].call(text)) ? callSiteArray[8].call(text) : callSiteArray[9].call(callSiteArray[10].call((Object)column, 10), 1));
                            Object object = callSiteArray[11].call(callSiteArray[12].call(callSiteArray[13].call(callSiteArray[14].call((Object)"   ", callSiteArray[15].call(text, start, end)), callSiteArray[16].call(Utilities.class)), "   "), callSiteArray[17].call(marker, start, callSiteArray[18].call(marker)));
                            sample = ShortTypeHandling.castToString(object);
                        } else {
                            Object object = callSiteArray[19].call(callSiteArray[20].call(callSiteArray[21].call(callSiteArray[22].call((Object)"   ", text), callSiteArray[23].call(Utilities.class)), "   "), marker);
                            sample = ShortTypeHandling.castToString(object);
                        }
                    } else {
                        String string;
                        sample = string = text;
                    }
                }
            } else if (ScriptBytecodeAdapter.compareNotEqual(text, null)) {
                if (column > 0) {
                    String marker = ShortTypeHandling.castToString(callSiteArray[24].call(callSiteArray[25].call(Utilities.class, " ", column - 1), "^"));
                    if (column > 40) {
                        int start = column - 30 - 1;
                        int end = DefaultTypeTransformation.intUnbox(ScriptBytecodeAdapter.compareGreaterThan(column + 10, callSiteArray[26].call(text)) ? callSiteArray[27].call(text) : Integer.valueOf(column + 10 - 1));
                        Object object = callSiteArray[28].call(callSiteArray[29].call(callSiteArray[30].call(callSiteArray[31].call((Object)"   ", callSiteArray[32].call(text, start, end)), callSiteArray[33].call(Utilities.class)), "   "), callSiteArray[34].call(marker, start, callSiteArray[35].call(marker)));
                        sample = ShortTypeHandling.castToString(object);
                    } else {
                        Object object = callSiteArray[36].call(callSiteArray[37].call(callSiteArray[38].call(callSiteArray[39].call((Object)"   ", text), callSiteArray[40].call(Utilities.class)), "   "), marker);
                        sample = ShortTypeHandling.castToString(object);
                    }
                } else {
                    String string;
                    sample = string = text;
                }
            }
            return sample;
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != AssertionSourceDelegatingSourceUnit.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(AssertionSourceDelegatingSourceUnit.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(AssertionSourceDelegatingSourceUnit.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(AssertionSourceDelegatingSourceUnit.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = AssertionSourceDelegatingSourceUnit.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(AssertionSourceDelegatingSourceUnit.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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

        public /* synthetic */ String super$3$getSample(int n, int n2, Janitor janitor) {
            return super.getSample(n, n2, janitor);
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "getLine";
            stringArray[1] = "plus";
            stringArray[2] = "repeatString";
            stringArray[3] = "minus";
            stringArray[4] = "minus";
            stringArray[5] = "minus";
            stringArray[6] = "plus";
            stringArray[7] = "length";
            stringArray[8] = "length";
            stringArray[9] = "minus";
            stringArray[10] = "plus";
            stringArray[11] = "plus";
            stringArray[12] = "plus";
            stringArray[13] = "plus";
            stringArray[14] = "plus";
            stringArray[15] = "substring";
            stringArray[16] = "eol";
            stringArray[17] = "substring";
            stringArray[18] = "length";
            stringArray[19] = "plus";
            stringArray[20] = "plus";
            stringArray[21] = "plus";
            stringArray[22] = "plus";
            stringArray[23] = "eol";
            stringArray[24] = "plus";
            stringArray[25] = "repeatString";
            stringArray[26] = "length";
            stringArray[27] = "length";
            stringArray[28] = "plus";
            stringArray[29] = "plus";
            stringArray[30] = "plus";
            stringArray[31] = "plus";
            stringArray[32] = "substring";
            stringArray[33] = "eol";
            stringArray[34] = "substring";
            stringArray[35] = "length";
            stringArray[36] = "plus";
            stringArray[37] = "plus";
            stringArray[38] = "plus";
            stringArray[39] = "plus";
            stringArray[40] = "eol";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[41];
            AssertionSourceDelegatingSourceUnit.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(AssertionSourceDelegatingSourceUnit.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = AssertionSourceDelegatingSourceUnit.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }

    static class ProgressCallbackChain
    extends CompilationUnit.ProgressCallback
    implements GroovyObject {
        private final List<CompilationUnit.ProgressCallback> chain;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ ClassInfo $staticClassInfo$;
        private static /* synthetic */ SoftReference $callSiteArray;

        public ProgressCallbackChain(CompilationUnit.ProgressCallback ... callbacks) {
            MetaClass metaClass;
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            Object object = callSiteArray[0].callConstructor(LinkedList.class);
            this.chain = (List)ScriptBytecodeAdapter.castToType(object, List.class);
            this.metaClass = metaClass = this.$getStaticMetaClass();
            if (ScriptBytecodeAdapter.compareNotEqual(callbacks, null)) {
                callSiteArray[1].call((Object)callbacks, new _closure1(this, this));
            }
        }

        public void addCallback(CompilationUnit.ProgressCallback callback) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            callSiteArray[2].call(this.chain, callback);
        }

        @Override
        public void call(ProcessingUnit context, int phase) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            ScriptBytecodeAdapter.invokeMethodNSpreadSafe(ProgressCallbackChain.class, this.chain, "call", new Object[]{context, phase});
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != ProgressCallbackChain.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(ProgressCallbackChain.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(ProgressCallbackChain.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(ProgressCallbackChain.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = ProgressCallbackChain.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(ProgressCallbackChain.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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
            stringArray[1] = "each";
            stringArray[2] = "leftShift";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[3];
            ProgressCallbackChain.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(ProgressCallbackChain.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = ProgressCallbackChain.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }

        public class _closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _closure1.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, it);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _closure1.class) {
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
                stringArray[0] = "addCallback";
                return new CallSiteArray(_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
    }

    public static class LabelFinder
    extends ClassCodeVisitorSupport
    implements GroovyObject {
        private final String label;
        private final SourceUnit unit;
        private final List<Statement> targets;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ ClassInfo $staticClassInfo$;
        private static /* synthetic */ SoftReference $callSiteArray;

        public LabelFinder(String label, SourceUnit unit) {
            MetaClass metaClass;
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            Object object = callSiteArray[0].callConstructor(LinkedList.class);
            this.targets = (List)ScriptBytecodeAdapter.castToType(object, List.class);
            this.metaClass = metaClass = this.$getStaticMetaClass();
            String string = label;
            this.label = ShortTypeHandling.castToString(string);
            SourceUnit sourceUnit = unit;
            this.unit = (SourceUnit)ScriptBytecodeAdapter.castToType(sourceUnit, SourceUnit.class);
        }

        public static List<Statement> lookup(MethodNode node, String label) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            LabelFinder finder = (LabelFinder)ScriptBytecodeAdapter.castToType(callSiteArray[1].callConstructor(LabelFinder.class, label, null), LabelFinder.class);
            callSiteArray[2].call(callSiteArray[3].callGetProperty(node), finder);
            return (List)ScriptBytecodeAdapter.castToType(callSiteArray[4].callGroovyObjectGetProperty(finder), List.class);
        }

        public static List<Statement> lookup(ClassNode node, String label) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            LabelFinder finder = (LabelFinder)ScriptBytecodeAdapter.castToType(callSiteArray[5].callConstructor(LabelFinder.class, label, null), LabelFinder.class);
            ScriptBytecodeAdapter.invokeMethodNSpreadSafe(LabelFinder.class, ScriptBytecodeAdapter.getPropertySpreadSafe(LabelFinder.class, callSiteArray[6].callGetProperty(node), "code"), "visit", new Object[]{finder});
            ScriptBytecodeAdapter.invokeMethodNSpreadSafe(LabelFinder.class, ScriptBytecodeAdapter.getPropertySpreadSafe(LabelFinder.class, callSiteArray[7].callGetProperty(node), "code"), "visit", new Object[]{finder});
            return (List)ScriptBytecodeAdapter.castToType(callSiteArray[8].callGroovyObjectGetProperty(finder), List.class);
        }

        @Override
        protected SourceUnit getSourceUnit() {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            return this.unit;
        }

        @Override
        protected void visitStatement(Statement statement) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitStatement", new Object[]{statement});
            if (ScriptBytecodeAdapter.compareEqual(callSiteArray[9].callGetProperty(statement), this.label)) {
                callSiteArray[10].call(this.targets, statement);
            }
        }

        public List<Statement> getTargets() {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            return (List)ScriptBytecodeAdapter.castToType(callSiteArray[11].call(Collections.class, this.targets), List.class);
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != LabelFinder.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(LabelFinder.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(LabelFinder.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(LabelFinder.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = LabelFinder.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(LabelFinder.class, ASTTestTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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

        public /* synthetic */ void super$3$visitStatement(Statement statement) {
            super.visitStatement(statement);
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "<$constructor$>";
            stringArray[1] = "<$constructor$>";
            stringArray[2] = "visit";
            stringArray[3] = "code";
            stringArray[4] = "targets";
            stringArray[5] = "<$constructor$>";
            stringArray[6] = "methods";
            stringArray[7] = "declaredConstructors";
            stringArray[8] = "targets";
            stringArray[9] = "statementLabel";
            stringArray[10] = "leftShift";
            stringArray[11] = "unmodifiableList";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[12];
            LabelFinder.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(LabelFinder.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = LabelFinder.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}


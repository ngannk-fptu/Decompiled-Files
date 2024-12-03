/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.transform.TimedInterrupt;
import java.lang.ref.SoftReference;
import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.AbstractInterruptibleASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class TimedInterruptibleASTTransformation
extends AbstractASTTransformation
implements GroovyObject {
    private static final ClassNode MY_TYPE;
    private static final String CHECK_METHOD_START_MEMBER = "checkOnMethodStart";
    private static final String APPLY_TO_ALL_CLASSES = "applyToAllClasses";
    private static final String APPLY_TO_ALL_MEMBERS = "applyToAllMembers";
    private static final String THROWN_EXCEPTION_TYPE = "thrown";
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public TimedInterruptibleASTTransformation() {
        MetaClass metaClass;
        CallSite[] callSiteArray = TimedInterruptibleASTTransformation.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        Reference<SourceUnit> source2 = new Reference<SourceUnit>(source);
        CallSite[] callSiteArray = TimedInterruptibleASTTransformation.$getCallSiteArray();
        callSiteArray[0].callCurrent(this, nodes, source2.get());
        Reference<Object> node = new Reference<Object>(null);
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[1].call((Object)nodes, 0);
            node.set(((AnnotationNode)ScriptBytecodeAdapter.castToType(object, AnnotationNode.class)));
        } else {
            Object object = BytecodeInterface8.objectArrayGet(nodes, 0);
            node.set(((AnnotationNode)ScriptBytecodeAdapter.castToType(object, AnnotationNode.class)));
        }
        AnnotatedNode annotatedNode = null;
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object = callSiteArray[2].call((Object)nodes, 1);
            annotatedNode = (AnnotatedNode)ScriptBytecodeAdapter.castToType(object, AnnotatedNode.class);
        } else {
            Object object = BytecodeInterface8.objectArrayGet(nodes, 1);
            annotatedNode = (AnnotatedNode)ScriptBytecodeAdapter.castToType(object, AnnotatedNode.class);
        }
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call((Object)MY_TYPE, callSiteArray[4].call(node.get())))) {
            callSiteArray[5].callStatic(TimedInterruptibleASTTransformation.class, new GStringImpl(new Object[]{callSiteArray[6].callGetProperty(callSiteArray[7].callGetProperty(node.get()))}, new String[]{"Transformation called from wrong annotation: ", ""}));
        }
        Reference<Object> checkOnMethodStart = new Reference<Object>(callSiteArray[8].callStatic(TimedInterruptibleASTTransformation.class, node.get(), CHECK_METHOD_START_MEMBER, callSiteArray[9].callGetProperty(Boolean.class), true));
        Reference<Object> applyToAllMembers = new Reference<Object>(callSiteArray[10].callStatic(TimedInterruptibleASTTransformation.class, node.get(), APPLY_TO_ALL_MEMBERS, callSiteArray[11].callGetProperty(Boolean.class), true));
        Reference<Boolean> applyToAllClasses = new Reference<Boolean>((Boolean)(DefaultTypeTransformation.booleanUnbox(applyToAllMembers.get()) ? callSiteArray[12].callStatic(TimedInterruptibleASTTransformation.class, node.get(), APPLY_TO_ALL_CLASSES, callSiteArray[13].callGetProperty(Boolean.class), true) : Boolean.valueOf(false)));
        Reference<Object> maximum = new Reference<Object>(callSiteArray[14].callStatic(TimedInterruptibleASTTransformation.class, node.get(), "value", callSiteArray[15].callGetProperty(Long.class), callSiteArray[16].callGetProperty(Long.class)));
        Reference<Object> thrown = new Reference<Object>(callSiteArray[17].call(AbstractInterruptibleASTTransformation.class, node.get(), THROWN_EXCEPTION_TYPE, callSiteArray[18].callStatic(ClassHelper.class, TimeoutException.class)));
        Object object = callSiteArray[19].call((Object)node.get(), "unit");
        Reference<Expression> unit = new Reference<Expression>((Expression)ScriptBytecodeAdapter.castToType(DefaultTypeTransformation.booleanUnbox(object) ? object : callSiteArray[20].callStatic(GeneralUtils.class, callSiteArray[21].callStatic(GeneralUtils.class, TimeUnit.class), "SECONDS"), Expression.class));
        if (DefaultTypeTransformation.booleanUnbox(applyToAllClasses.get())) {
            public class _visit_closure1
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference source;
                private /* synthetic */ Reference checkOnMethodStart;
                private /* synthetic */ Reference applyToAllClasses;
                private /* synthetic */ Reference applyToAllMembers;
                private /* synthetic */ Reference maximum;
                private /* synthetic */ Reference unit;
                private /* synthetic */ Reference thrown;
                private /* synthetic */ Reference node;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _visit_closure1(Object _outerInstance, Object _thisObject, Reference source, Reference checkOnMethodStart, Reference applyToAllClasses, Reference applyToAllMembers, Reference maximum, Reference unit, Reference thrown, Reference node) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    Reference reference4;
                    Reference reference5;
                    Reference reference6;
                    Reference reference7;
                    Reference reference8;
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.source = reference8 = source;
                    this.checkOnMethodStart = reference7 = checkOnMethodStart;
                    this.applyToAllClasses = reference6 = applyToAllClasses;
                    this.applyToAllMembers = reference5 = applyToAllMembers;
                    this.maximum = reference4 = maximum;
                    this.unit = reference3 = unit;
                    this.thrown = reference2 = thrown;
                    this.node = reference = node;
                }

                public Object doCall(ClassNode it) {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    Object visitor = callSiteArray[0].callConstructor((Object)TimedInterruptionVisitor.class, ArrayUtil.createArray(this.source.get(), this.checkOnMethodStart.get(), this.applyToAllClasses.get(), this.applyToAllMembers.get(), this.maximum.get(), this.unit.get(), this.thrown.get(), callSiteArray[1].call(this.node.get())));
                    return callSiteArray[2].call(visitor, it);
                }

                public Object call(ClassNode it) {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return callSiteArray[3].callCurrent((GroovyObject)this, it);
                }

                public SourceUnit getSource() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return (SourceUnit)ScriptBytecodeAdapter.castToType(this.source.get(), SourceUnit.class);
                }

                public Object getCheckOnMethodStart() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return this.checkOnMethodStart.get();
                }

                public Object getApplyToAllClasses() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return this.applyToAllClasses.get();
                }

                public Object getApplyToAllMembers() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return this.applyToAllMembers.get();
                }

                public Object getMaximum() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return this.maximum.get();
                }

                public Expression getUnit() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return (Expression)ScriptBytecodeAdapter.castToType(this.unit.get(), Expression.class);
                }

                public Object getThrown() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return this.thrown.get();
                }

                public AnnotationNode getNode() {
                    CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                    return (AnnotationNode)ScriptBytecodeAdapter.castToType(this.node.get(), AnnotationNode.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _visit_closure1.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "<$constructor$>";
                    stringArray[1] = "hashCode";
                    stringArray[2] = "visitClass";
                    stringArray[3] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[4];
                    _visit_closure1.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_visit_closure1.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _visit_closure1.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[22].callSafe(callSiteArray[23].callGetPropertySafe(callSiteArray[24].call(source2.get())), new _visit_closure1(this, this, source2, checkOnMethodStart, applyToAllClasses, applyToAllMembers, maximum, unit, thrown, node));
        } else if (annotatedNode instanceof ClassNode) {
            Object visitor = callSiteArray[25].callConstructor((Object)TimedInterruptionVisitor.class, ArrayUtil.createArray(source2.get(), checkOnMethodStart.get(), applyToAllClasses.get(), applyToAllMembers.get(), maximum.get(), unit.get(), thrown.get(), callSiteArray[26].call(node.get())));
            callSiteArray[27].call(visitor, annotatedNode);
        } else if (!DefaultTypeTransformation.booleanUnbox(applyToAllMembers.get()) && annotatedNode instanceof MethodNode) {
            Object visitor = callSiteArray[28].callConstructor((Object)TimedInterruptionVisitor.class, ArrayUtil.createArray(source2.get(), checkOnMethodStart.get(), applyToAllClasses.get(), applyToAllMembers.get(), maximum.get(), unit.get(), thrown.get(), callSiteArray[29].call(node.get())));
            callSiteArray[30].call(visitor, annotatedNode);
            callSiteArray[31].call(visitor, callSiteArray[32].callGetProperty(annotatedNode));
        } else if (!DefaultTypeTransformation.booleanUnbox(applyToAllMembers.get()) && annotatedNode instanceof FieldNode) {
            Object visitor = callSiteArray[33].callConstructor((Object)TimedInterruptionVisitor.class, ArrayUtil.createArray(source2.get(), checkOnMethodStart.get(), applyToAllClasses.get(), applyToAllMembers.get(), maximum.get(), unit.get(), thrown.get(), callSiteArray[34].call(node.get())));
            callSiteArray[35].call(visitor, annotatedNode);
            callSiteArray[36].call(visitor, callSiteArray[37].callGetProperty(annotatedNode));
        } else if (!DefaultTypeTransformation.booleanUnbox(applyToAllMembers.get()) && annotatedNode instanceof DeclarationExpression) {
            Object visitor = callSiteArray[38].callConstructor((Object)TimedInterruptionVisitor.class, ArrayUtil.createArray(source2.get(), checkOnMethodStart.get(), applyToAllClasses.get(), applyToAllMembers.get(), maximum.get(), unit.get(), thrown.get(), callSiteArray[39].call(node.get())));
            callSiteArray[40].call(visitor, annotatedNode);
            callSiteArray[41].call(visitor, callSiteArray[42].callGetProperty(annotatedNode));
        } else {
            public class _visit_closure2
            extends Closure
            implements GeneratedClosure {
                private /* synthetic */ Reference source;
                private /* synthetic */ Reference checkOnMethodStart;
                private /* synthetic */ Reference applyToAllClasses;
                private /* synthetic */ Reference applyToAllMembers;
                private /* synthetic */ Reference maximum;
                private /* synthetic */ Reference unit;
                private /* synthetic */ Reference thrown;
                private /* synthetic */ Reference node;
                private static /* synthetic */ ClassInfo $staticClassInfo;
                public static transient /* synthetic */ boolean __$stMC;
                private static /* synthetic */ SoftReference $callSiteArray;

                public _visit_closure2(Object _outerInstance, Object _thisObject, Reference source, Reference checkOnMethodStart, Reference applyToAllClasses, Reference applyToAllMembers, Reference maximum, Reference unit, Reference thrown, Reference node) {
                    Reference reference;
                    Reference reference2;
                    Reference reference3;
                    Reference reference4;
                    Reference reference5;
                    Reference reference6;
                    Reference reference7;
                    Reference reference8;
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    super(_outerInstance, _thisObject);
                    this.source = reference8 = source;
                    this.checkOnMethodStart = reference7 = checkOnMethodStart;
                    this.applyToAllClasses = reference6 = applyToAllClasses;
                    this.applyToAllMembers = reference5 = applyToAllMembers;
                    this.maximum = reference4 = maximum;
                    this.unit = reference3 = unit;
                    this.thrown = reference2 = thrown;
                    this.node = reference = node;
                }

                public Object doCall(ClassNode it) {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(it))) {
                        Object visitor = callSiteArray[1].callConstructor((Object)TimedInterruptionVisitor.class, ArrayUtil.createArray(this.source.get(), this.checkOnMethodStart.get(), this.applyToAllClasses.get(), this.applyToAllMembers.get(), this.maximum.get(), this.unit.get(), this.thrown.get(), callSiteArray[2].call(this.node.get())));
                        return callSiteArray[3].call(visitor, it);
                    }
                    return null;
                }

                public Object call(ClassNode it) {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return callSiteArray[4].callCurrent((GroovyObject)this, it);
                }

                public SourceUnit getSource() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return (SourceUnit)ScriptBytecodeAdapter.castToType(this.source.get(), SourceUnit.class);
                }

                public Object getCheckOnMethodStart() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return this.checkOnMethodStart.get();
                }

                public Object getApplyToAllClasses() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return this.applyToAllClasses.get();
                }

                public Object getApplyToAllMembers() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return this.applyToAllMembers.get();
                }

                public Object getMaximum() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return this.maximum.get();
                }

                public Expression getUnit() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return (Expression)ScriptBytecodeAdapter.castToType(this.unit.get(), Expression.class);
                }

                public Object getThrown() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return this.thrown.get();
                }

                public AnnotationNode getNode() {
                    CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                    return (AnnotationNode)ScriptBytecodeAdapter.castToType(this.node.get(), AnnotationNode.class);
                }

                protected /* synthetic */ MetaClass $getStaticMetaClass() {
                    if (this.getClass() != _visit_closure2.class) {
                        return ScriptBytecodeAdapter.initMetaClass(this);
                    }
                    ClassInfo classInfo = $staticClassInfo;
                    if (classInfo == null) {
                        $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                    }
                    return classInfo.getMetaClass();
                }

                private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                    stringArray[0] = "isScript";
                    stringArray[1] = "<$constructor$>";
                    stringArray[2] = "hashCode";
                    stringArray[3] = "visitClass";
                    stringArray[4] = "doCall";
                }

                private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                    String[] stringArray = new String[5];
                    _visit_closure2.$createCallSiteArray_1(stringArray);
                    return new CallSiteArray(_visit_closure2.class, stringArray);
                }

                private static /* synthetic */ CallSite[] $getCallSiteArray() {
                    CallSiteArray callSiteArray;
                    if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                        callSiteArray = _visit_closure2.$createCallSiteArray();
                        $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                    }
                    return callSiteArray.array;
                }
            }
            callSiteArray[43].callSafe(callSiteArray[44].callGetPropertySafe(callSiteArray[45].call(source2.get())), new _visit_closure2(this, this, source2, checkOnMethodStart, applyToAllClasses, applyToAllMembers, maximum, unit, thrown, node));
        }
    }

    public static Object getConstantAnnotationParameter(AnnotationNode node, String parameterName, Class type, Object defaultValue) {
        CallSite[] callSiteArray = TimedInterruptibleASTTransformation.$getCallSiteArray();
        Object member = callSiteArray[46].call((Object)node, parameterName);
        if (DefaultTypeTransformation.booleanUnbox(member)) {
            if (member instanceof ConstantExpression) {
                Object object = callSiteArray[47].call(callSiteArray[48].callGetProperty(member), type);
                try {
                    return object;
                }
                catch (Exception ignore) {
                    callSiteArray[49].callStatic(TimedInterruptibleASTTransformation.class, new GStringImpl(new Object[]{parameterName, member}, new String[]{"Expecting boolean value for ", " annotation parameter. Found ", ""}));
                }
            } else {
                callSiteArray[50].callStatic(TimedInterruptibleASTTransformation.class, new GStringImpl(new Object[]{parameterName, member}, new String[]{"Expecting boolean value for ", " annotation parameter. Found ", ""}));
            }
        }
        return defaultValue;
    }

    private static void internalError(String message) {
        CallSite[] callSiteArray = TimedInterruptibleASTTransformation.$getCallSiteArray();
        throw (Throwable)callSiteArray[51].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{message}, new String[]{"Internal error: ", ""}));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TimedInterruptibleASTTransformation.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public /* synthetic */ Object this$dist$invoke$2(String name, Object args) {
        CallSite[] callSiteArray = TimedInterruptibleASTTransformation.$getCallSiteArray();
        return ScriptBytecodeAdapter.invokeMethodOnCurrentN(TimedInterruptibleASTTransformation.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
    }

    public /* synthetic */ void this$dist$set$2(String name, Object value) {
        CallSite[] callSiteArray = TimedInterruptibleASTTransformation.$getCallSiteArray();
        Object object = value;
        ScriptBytecodeAdapter.setGroovyObjectProperty(object, TimedInterruptibleASTTransformation.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
    }

    public /* synthetic */ Object this$dist$get$2(String name) {
        CallSite[] callSiteArray = TimedInterruptibleASTTransformation.$getCallSiteArray();
        return ScriptBytecodeAdapter.getGroovyObjectProperty(TimedInterruptibleASTTransformation.class, this, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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

    static {
        Object object = TimedInterruptibleASTTransformation.$getCallSiteArray()[52].callStatic(ClassHelper.class, TimedInterrupt.class);
        MY_TYPE = (ClassNode)ScriptBytecodeAdapter.castToType(object, ClassNode.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "init";
        stringArray[1] = "getAt";
        stringArray[2] = "getAt";
        stringArray[3] = "equals";
        stringArray[4] = "getClassNode";
        stringArray[5] = "internalError";
        stringArray[6] = "name";
        stringArray[7] = "classNode";
        stringArray[8] = "getConstantAnnotationParameter";
        stringArray[9] = "TYPE";
        stringArray[10] = "getConstantAnnotationParameter";
        stringArray[11] = "TYPE";
        stringArray[12] = "getConstantAnnotationParameter";
        stringArray[13] = "TYPE";
        stringArray[14] = "getConstantAnnotationParameter";
        stringArray[15] = "TYPE";
        stringArray[16] = "MAX_VALUE";
        stringArray[17] = "getClassAnnotationParameter";
        stringArray[18] = "make";
        stringArray[19] = "getMember";
        stringArray[20] = "propX";
        stringArray[21] = "classX";
        stringArray[22] = "each";
        stringArray[23] = "classes";
        stringArray[24] = "getAST";
        stringArray[25] = "<$constructor$>";
        stringArray[26] = "hashCode";
        stringArray[27] = "visitClass";
        stringArray[28] = "<$constructor$>";
        stringArray[29] = "hashCode";
        stringArray[30] = "visitMethod";
        stringArray[31] = "visitClass";
        stringArray[32] = "declaringClass";
        stringArray[33] = "<$constructor$>";
        stringArray[34] = "hashCode";
        stringArray[35] = "visitField";
        stringArray[36] = "visitClass";
        stringArray[37] = "declaringClass";
        stringArray[38] = "<$constructor$>";
        stringArray[39] = "hashCode";
        stringArray[40] = "visitDeclarationExpression";
        stringArray[41] = "visitClass";
        stringArray[42] = "declaringClass";
        stringArray[43] = "each";
        stringArray[44] = "classes";
        stringArray[45] = "getAST";
        stringArray[46] = "getMember";
        stringArray[47] = "asType";
        stringArray[48] = "value";
        stringArray[49] = "internalError";
        stringArray[50] = "internalError";
        stringArray[51] = "<$constructor$>";
        stringArray[52] = "make";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[53];
        TimedInterruptibleASTTransformation.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TimedInterruptibleASTTransformation.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TimedInterruptibleASTTransformation.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }

    static class TimedInterruptionVisitor
    extends ClassCodeVisitorSupport
    implements GroovyObject {
        private final SourceUnit source;
        private final boolean checkOnMethodStart;
        private final boolean applyToAllClasses;
        private final boolean applyToAllMembers;
        private FieldNode expireTimeField;
        private FieldNode startTimeField;
        private final Expression unit;
        private final Object maximum;
        private final ClassNode thrown;
        private final String basename;
        private static /* synthetic */ ClassInfo $staticClassInfo;
        public static transient /* synthetic */ boolean __$stMC;
        private transient /* synthetic */ MetaClass metaClass;
        private static /* synthetic */ ClassInfo $staticClassInfo$;
        private static /* synthetic */ SoftReference $callSiteArray;

        public TimedInterruptionVisitor(Object source, Object checkOnMethodStart, Object applyToAllClasses, Object applyToAllMembers, Object maximum, Object unit, Object thrown, Object hash) {
            Object object;
            MetaClass metaClass;
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            Object var10_10 = null;
            this.expireTimeField = (FieldNode)ScriptBytecodeAdapter.castToType(var10_10, FieldNode.class);
            Object var11_11 = null;
            this.startTimeField = (FieldNode)ScriptBytecodeAdapter.castToType(var11_11, FieldNode.class);
            this.metaClass = metaClass = this.$getStaticMetaClass();
            Object object2 = source;
            this.source = (SourceUnit)ScriptBytecodeAdapter.castToType(object2, SourceUnit.class);
            Object object3 = checkOnMethodStart;
            this.checkOnMethodStart = DefaultTypeTransformation.booleanUnbox(object3);
            Object object4 = applyToAllClasses;
            this.applyToAllClasses = DefaultTypeTransformation.booleanUnbox(object4);
            Object object5 = applyToAllMembers;
            this.applyToAllMembers = DefaultTypeTransformation.booleanUnbox(object5);
            Object object6 = unit;
            this.unit = (Expression)ScriptBytecodeAdapter.castToType(object6, Expression.class);
            this.maximum = object = maximum;
            Object object7 = thrown;
            this.thrown = (ClassNode)ScriptBytecodeAdapter.castToType(object7, ClassNode.class);
            Object object8 = callSiteArray[0].call((Object)"timedInterrupt", hash);
            this.basename = ShortTypeHandling.castToString(object8);
        }

        public final Object createInterruptStatement() {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            return callSiteArray[1].callStatic(GeneralUtils.class, callSiteArray[2].callStatic(GeneralUtils.class, callSiteArray[3].callStatic(GeneralUtils.class, callSiteArray[4].callStatic(GeneralUtils.class, "this"), callSiteArray[5].call((Object)this.basename, "$expireTime")), callSiteArray[6].callStatic(GeneralUtils.class, callSiteArray[7].callStatic(ClassHelper.class, System.class), "nanoTime")), callSiteArray[8].callStatic(GeneralUtils.class, callSiteArray[9].callStatic(GeneralUtils.class, this.thrown, callSiteArray[10].callStatic(GeneralUtils.class, callSiteArray[11].callStatic(GeneralUtils.class, callSiteArray[12].callStatic(GeneralUtils.class, callSiteArray[13].call(callSiteArray[14].call((Object)"Execution timed out after ", this.maximum), " units. Start time: ")), callSiteArray[15].callStatic(GeneralUtils.class, callSiteArray[16].callStatic(GeneralUtils.class, "this"), callSiteArray[17].call((Object)this.basename, "$startTime")))))));
        }

        private Object wrapBlock(Object statement) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            Object stmt = callSiteArray[18].callConstructor(BlockStatement.class);
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                callSiteArray[19].call(stmt, callSiteArray[20].callCurrent(this));
            } else {
                callSiteArray[21].call(stmt, this.createInterruptStatement());
            }
            callSiteArray[22].call(stmt, statement);
            return stmt;
        }

        @Override
        public void visitClass(ClassNode node) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            if (DefaultTypeTransformation.booleanUnbox(callSiteArray[23].call((Object)node, callSiteArray[24].call((Object)this.basename, "$expireTime")))) {
                return;
            }
            Object object = callSiteArray[25].call(node, callSiteArray[26].call((Object)this.basename, "$expireTime"), callSiteArray[27].call(callSiteArray[28].callGroovyObjectGetProperty(this), callSiteArray[29].callGroovyObjectGetProperty(this)), callSiteArray[30].callGetProperty(ClassHelper.class), callSiteArray[31].callStatic(GeneralUtils.class, callSiteArray[32].callStatic(GeneralUtils.class, callSiteArray[33].callStatic(ClassHelper.class, System.class), "nanoTime"), callSiteArray[34].callStatic(GeneralUtils.class, callSiteArray[35].callStatic(GeneralUtils.class, callSiteArray[36].callStatic(GeneralUtils.class, TimeUnit.class), "NANOSECONDS"), "convert", callSiteArray[37].callStatic(GeneralUtils.class, callSiteArray[38].callStatic(GeneralUtils.class, this.maximum, true), this.unit))));
            this.expireTimeField = (FieldNode)ScriptBytecodeAdapter.castToType(object, FieldNode.class);
            boolean bl = true;
            ScriptBytecodeAdapter.setProperty(bl, null, this.expireTimeField, "synthetic");
            Object object2 = callSiteArray[39].call(node, callSiteArray[40].call((Object)this.basename, "$startTime"), callSiteArray[41].call(callSiteArray[42].callGroovyObjectGetProperty(this), callSiteArray[43].callGroovyObjectGetProperty(this)), callSiteArray[44].callStatic(ClassHelper.class, Date.class), callSiteArray[45].callStatic(GeneralUtils.class, callSiteArray[46].callStatic(ClassHelper.class, Date.class)));
            this.startTimeField = (FieldNode)ScriptBytecodeAdapter.castToType(object2, FieldNode.class);
            boolean bl2 = true;
            ScriptBytecodeAdapter.setProperty(bl2, null, this.startTimeField, "synthetic");
            callSiteArray[47].call(callSiteArray[48].callGetProperty(node), this.expireTimeField);
            callSiteArray[49].call(callSiteArray[50].callGetProperty(node), this.startTimeField);
            callSiteArray[51].call(callSiteArray[52].callGetProperty(node), 0, this.startTimeField);
            callSiteArray[53].call(callSiteArray[54].callGetProperty(node), 0, this.expireTimeField);
            if (this.applyToAllMembers) {
                ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitClass", new Object[]{node});
            }
        }

        @Override
        public void visitClosureExpression(ClosureExpression closureExpr) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            Object code = callSiteArray[55].callGetProperty(closureExpr);
            if (__$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (code instanceof BlockStatement) {
                    callSiteArray[56].call(callSiteArray[57].callGetProperty(code), 0, callSiteArray[58].callCurrent(this));
                } else {
                    Object object = callSiteArray[59].callCurrent((GroovyObject)this, code);
                    ScriptBytecodeAdapter.setProperty(object, null, closureExpr, "code");
                }
            } else if (code instanceof BlockStatement) {
                callSiteArray[60].call(callSiteArray[61].callGetProperty(code), 0, this.createInterruptStatement());
            } else {
                Object object = callSiteArray[62].callCurrent((GroovyObject)this, code);
                ScriptBytecodeAdapter.setProperty(object, null, closureExpr, "code");
            }
            ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitClosureExpression", new Object[]{closureExpr});
        }

        @Override
        public void visitField(FieldNode node) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[63].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[64].call(node))) {
                    ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitField", new Object[]{node});
                }
            } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[65].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[66].call(node))) {
                ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitField", new Object[]{node});
            }
        }

        @Override
        public void visitProperty(PropertyNode node) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[67].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[68].call(node))) {
                    ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitProperty", new Object[]{node});
                }
            } else if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[69].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[70].call(node))) {
                ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitProperty", new Object[]{node});
            }
        }

        private Object visitLoop(Object loopStatement) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            Object statement = callSiteArray[71].callGetProperty(loopStatement);
            Object object = callSiteArray[72].callCurrent((GroovyObject)this, statement);
            ScriptBytecodeAdapter.setProperty(object, null, loopStatement, "loopBlock");
            return object;
        }

        @Override
        public void visitForLoop(ForStatement forStatement) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            callSiteArray[73].callCurrent((GroovyObject)this, forStatement);
            ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitForLoop", new Object[]{forStatement});
        }

        @Override
        public void visitDoWhileLoop(DoWhileStatement doWhileStatement) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            callSiteArray[74].callCurrent((GroovyObject)this, doWhileStatement);
            ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitDoWhileLoop", new Object[]{doWhileStatement});
        }

        @Override
        public void visitWhileLoop(WhileStatement whileStatement) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            callSiteArray[75].callCurrent((GroovyObject)this, whileStatement);
            ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitWhileLoop", new Object[]{whileStatement});
        }

        @Override
        public void visitMethod(MethodNode node) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                if (this.checkOnMethodStart && !DefaultTypeTransformation.booleanUnbox(callSiteArray[76].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[77].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[78].call(node))) {
                    Object code = callSiteArray[79].callGetProperty(node);
                    Object object = callSiteArray[80].callCurrent((GroovyObject)this, code);
                    ScriptBytecodeAdapter.setProperty(object, null, node, "code");
                }
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[81].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[82].call(node))) {
                    ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitMethod", new Object[]{node});
                }
            } else {
                if (this.checkOnMethodStart && !DefaultTypeTransformation.booleanUnbox(callSiteArray[83].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[84].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[85].call(node))) {
                    Object code = callSiteArray[86].callGetProperty(node);
                    Object object = callSiteArray[87].callCurrent((GroovyObject)this, code);
                    ScriptBytecodeAdapter.setProperty(object, null, node, "code");
                }
                if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[88].call(node)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[89].call(node))) {
                    ScriptBytecodeAdapter.invokeMethodOnSuperN(ClassCodeVisitorSupport.class, this, "visitMethod", new Object[]{node});
                }
            }
        }

        @Override
        protected SourceUnit getSourceUnit() {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            return this.source;
        }

        protected /* synthetic */ MetaClass $getStaticMetaClass() {
            if (this.getClass() != TimedInterruptionVisitor.class) {
                return ScriptBytecodeAdapter.initMetaClass(this);
            }
            ClassInfo classInfo = $staticClassInfo;
            if (classInfo == null) {
                $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
            }
            return classInfo.getMetaClass();
        }

        public /* synthetic */ Object methodMissing(String name, Object args) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(TimedInterruptionVisitor.class, TimedInterruptibleASTTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public static /* synthetic */ Object $static_methodMissing(String name, Object args) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.invokeMethodN(TimedInterruptionVisitor.class, TimedInterruptibleASTTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})), ScriptBytecodeAdapter.despreadList(new Object[0], new Object[]{args}, new int[]{0}));
        }

        public /* synthetic */ void propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, TimedInterruptibleASTTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ void $static_propertyMissing(String name, Object val) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            Object object = val;
            ScriptBytecodeAdapter.setProperty(object, null, TimedInterruptibleASTTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public /* synthetic */ Object propertyMissing(String name) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(TimedInterruptionVisitor.class, TimedInterruptibleASTTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
        }

        public static /* synthetic */ Object $static_propertyMissing(String name) {
            CallSite[] callSiteArray = TimedInterruptionVisitor.$getCallSiteArray();
            return ScriptBytecodeAdapter.getProperty(TimedInterruptionVisitor.class, TimedInterruptibleASTTransformation.class, ShortTypeHandling.castToString(new GStringImpl(new Object[]{name}, new String[]{"", ""})));
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

        public /* synthetic */ void super$2$visitClosureExpression(ClosureExpression closureExpression) {
            super.visitClosureExpression(closureExpression);
        }

        public /* synthetic */ void super$3$visitForLoop(ForStatement forStatement) {
            super.visitForLoop(forStatement);
        }

        public /* synthetic */ void super$3$visitWhileLoop(WhileStatement whileStatement) {
            super.visitWhileLoop(whileStatement);
        }

        public /* synthetic */ void super$3$visitDoWhileLoop(DoWhileStatement doWhileStatement) {
            super.visitDoWhileLoop(doWhileStatement);
        }

        public /* synthetic */ void super$3$visitField(FieldNode fieldNode) {
            super.visitField(fieldNode);
        }

        public /* synthetic */ void super$3$visitMethod(MethodNode methodNode) {
            super.visitMethod(methodNode);
        }

        public /* synthetic */ void super$3$visitClass(ClassNode classNode) {
            super.visitClass(classNode);
        }

        public /* synthetic */ void super$3$visitProperty(PropertyNode propertyNode) {
            super.visitProperty(propertyNode);
        }

        private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
            stringArray[0] = "plus";
            stringArray[1] = "ifS";
            stringArray[2] = "ltX";
            stringArray[3] = "propX";
            stringArray[4] = "varX";
            stringArray[5] = "plus";
            stringArray[6] = "callX";
            stringArray[7] = "make";
            stringArray[8] = "throwS";
            stringArray[9] = "ctorX";
            stringArray[10] = "args";
            stringArray[11] = "plusX";
            stringArray[12] = "constX";
            stringArray[13] = "plus";
            stringArray[14] = "plus";
            stringArray[15] = "propX";
            stringArray[16] = "varX";
            stringArray[17] = "plus";
            stringArray[18] = "<$constructor$>";
            stringArray[19] = "addStatement";
            stringArray[20] = "createInterruptStatement";
            stringArray[21] = "addStatement";
            stringArray[22] = "addStatement";
            stringArray[23] = "getDeclaredField";
            stringArray[24] = "plus";
            stringArray[25] = "addField";
            stringArray[26] = "plus";
            stringArray[27] = "or";
            stringArray[28] = "ACC_FINAL";
            stringArray[29] = "ACC_PRIVATE";
            stringArray[30] = "long_TYPE";
            stringArray[31] = "plusX";
            stringArray[32] = "callX";
            stringArray[33] = "make";
            stringArray[34] = "callX";
            stringArray[35] = "propX";
            stringArray[36] = "classX";
            stringArray[37] = "args";
            stringArray[38] = "constX";
            stringArray[39] = "addField";
            stringArray[40] = "plus";
            stringArray[41] = "or";
            stringArray[42] = "ACC_FINAL";
            stringArray[43] = "ACC_PRIVATE";
            stringArray[44] = "make";
            stringArray[45] = "ctorX";
            stringArray[46] = "make";
            stringArray[47] = "remove";
            stringArray[48] = "fields";
            stringArray[49] = "remove";
            stringArray[50] = "fields";
            stringArray[51] = "add";
            stringArray[52] = "fields";
            stringArray[53] = "add";
            stringArray[54] = "fields";
            stringArray[55] = "code";
            stringArray[56] = "add";
            stringArray[57] = "statements";
            stringArray[58] = "createInterruptStatement";
            stringArray[59] = "wrapBlock";
            stringArray[60] = "add";
            stringArray[61] = "statements";
            stringArray[62] = "wrapBlock";
            stringArray[63] = "isStatic";
            stringArray[64] = "isSynthetic";
            stringArray[65] = "isStatic";
            stringArray[66] = "isSynthetic";
            stringArray[67] = "isStatic";
            stringArray[68] = "isSynthetic";
            stringArray[69] = "isStatic";
            stringArray[70] = "isSynthetic";
            stringArray[71] = "loopBlock";
            stringArray[72] = "wrapBlock";
            stringArray[73] = "visitLoop";
            stringArray[74] = "visitLoop";
            stringArray[75] = "visitLoop";
            stringArray[76] = "isSynthetic";
            stringArray[77] = "isStatic";
            stringArray[78] = "isAbstract";
            stringArray[79] = "code";
            stringArray[80] = "wrapBlock";
            stringArray[81] = "isSynthetic";
            stringArray[82] = "isStatic";
            stringArray[83] = "isSynthetic";
            stringArray[84] = "isStatic";
            stringArray[85] = "isAbstract";
            stringArray[86] = "code";
            stringArray[87] = "wrapBlock";
            stringArray[88] = "isSynthetic";
            stringArray[89] = "isStatic";
        }

        private static /* synthetic */ CallSiteArray $createCallSiteArray() {
            String[] stringArray = new String[90];
            TimedInterruptionVisitor.$createCallSiteArray_1(stringArray);
            return new CallSiteArray(TimedInterruptionVisitor.class, stringArray);
        }

        private static /* synthetic */ CallSite[] $getCallSiteArray() {
            CallSiteArray callSiteArray;
            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                callSiteArray = TimedInterruptionVisitor.$createCallSiteArray();
                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
            }
            return callSiteArray.array;
        }
    }
}


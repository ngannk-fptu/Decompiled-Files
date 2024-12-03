/*
 * Decompiled with CFR 0.152.
 */
package groovy.beans;

import groovy.beans.ListenerList;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovyjarjarasm.asm.Opcodes;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collection;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.control.messages.SyntaxErrorMessage;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.ArrayUtil;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.syntax.SyntaxException;
import org.codehaus.groovy.syntax.Token;
import org.codehaus.groovy.syntax.Types;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class ListenerListASTTransformation
implements ASTTransformation,
Opcodes,
GroovyObject {
    private static final Class MY_CLASS;
    private static final ClassNode COLLECTION_TYPE;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    public ListenerListASTTransformation() {
        MetaClass metaClass;
        CallSite[] callSiteArray = ListenerListASTTransformation.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        Object object;
        int n;
        int n2;
        Reference<SourceUnit> source2 = new Reference<SourceUnit>(source);
        CallSite[] callSiteArray = ListenerListASTTransformation.$getCallSiteArray();
        if (!BytecodeInterface8.isOrigInt() || !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            if (!(callSiteArray[0].call((Object)nodes, 0) instanceof AnnotationNode) || !(callSiteArray[1].call((Object)nodes, 1) instanceof AnnotatedNode)) {
                throw (Throwable)callSiteArray[2].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{callSiteArray[3].callGetProperty(callSiteArray[4].callGroovyObjectGetProperty(this)), callSiteArray[5].callGetProperty(callSiteArray[6].callGroovyObjectGetProperty(this))}, new String[]{"Internal error: wrong types: ", " / ", ""}));
            }
        } else if (!(BytecodeInterface8.objectArrayGet(nodes, 0) instanceof AnnotationNode) || !(BytecodeInterface8.objectArrayGet(nodes, 1) instanceof AnnotatedNode)) {
            throw (Throwable)callSiteArray[7].callConstructor(RuntimeException.class, new GStringImpl(new Object[]{callSiteArray[8].callGetProperty(callSiteArray[9].callGroovyObjectGetProperty(this)), callSiteArray[10].callGetProperty(callSiteArray[11].callGroovyObjectGetProperty(this))}, new String[]{"Internal error: wrong types: ", " / ", ""}));
        }
        Reference<Object> node = new Reference<Object>(null);
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object2 = callSiteArray[12].call((Object)nodes, 0);
            node.set(((AnnotationNode)ScriptBytecodeAdapter.castToType(object2, AnnotationNode.class)));
        } else {
            Object object3 = BytecodeInterface8.objectArrayGet(nodes, 0);
            node.set(((AnnotationNode)ScriptBytecodeAdapter.castToType(object3, AnnotationNode.class)));
        }
        Reference<Object> field = new Reference<Object>(null);
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object4 = callSiteArray[13].call((Object)nodes, 1);
            field.set(((FieldNode)ScriptBytecodeAdapter.castToType(object4, FieldNode.class)));
        } else {
            Object object5 = BytecodeInterface8.objectArrayGet(nodes, 1);
            field.set(((FieldNode)ScriptBytecodeAdapter.castToType(object5, FieldNode.class)));
        }
        Reference<Object> declaringClass = new Reference<Object>(null);
        if (!BytecodeInterface8.isOrigInt() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
            Object object6 = callSiteArray[14].callGetProperty(callSiteArray[15].call((Object)nodes, 1));
            declaringClass.set(((ClassNode)ScriptBytecodeAdapter.castToType(object6, ClassNode.class)));
        } else {
            Object object7 = callSiteArray[16].callGetProperty(BytecodeInterface8.objectArrayGet(nodes, 1));
            declaringClass.set(((ClassNode)ScriptBytecodeAdapter.castToType(object7, ClassNode.class)));
        }
        ClassNode parentClass = (ClassNode)ScriptBytecodeAdapter.castToType(callSiteArray[17].callGetProperty(field.get()), ClassNode.class);
        int isCollection = 0;
        isCollection = !BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass() ? (n2 = DefaultTypeTransformation.booleanUnbox(callSiteArray[18].call((Object)parentClass, COLLECTION_TYPE)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[19].call((Object)parentClass, COLLECTION_TYPE)) ? 1 : 0) : (n = DefaultTypeTransformation.booleanUnbox(callSiteArray[20].call((Object)parentClass, COLLECTION_TYPE)) || DefaultTypeTransformation.booleanUnbox(callSiteArray[21].call((Object)parentClass, COLLECTION_TYPE)) ? 1 : 0);
        if (isCollection == 0) {
            callSiteArray[22].callStatic(ListenerListASTTransformation.class, node.get(), source2.get(), callSiteArray[23].call(callSiteArray[24].call((Object)"@", callSiteArray[25].callGetProperty(MY_CLASS)), " can only annotate collection properties."));
            return;
        }
        Reference<Object> types = new Reference<Object>(callSiteArray[26].callGetProperty(callSiteArray[27].callGetProperty(field.get())));
        if (!DefaultTypeTransformation.booleanUnbox(types.get())) {
            callSiteArray[28].callStatic(ListenerListASTTransformation.class, node.get(), source2.get(), callSiteArray[29].call(callSiteArray[30].call((Object)"@", callSiteArray[31].callGetProperty(MY_CLASS)), " fields must have a generic type."));
            return;
        }
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[32].callGetProperty(callSiteArray[33].call(types.get(), 0)))) {
            callSiteArray[34].callStatic(ListenerListASTTransformation.class, node.get(), source2.get(), callSiteArray[35].call(callSiteArray[36].call((Object)"@", callSiteArray[37].callGetProperty(MY_CLASS)), " fields with generic wildcards not yet supported."));
            return;
        }
        Object listener = callSiteArray[38].callGetProperty(callSiteArray[39].call(types.get(), 0));
        if (!DefaultTypeTransformation.booleanUnbox(callSiteArray[40].callGetProperty(field.get()))) {
            Object object8 = callSiteArray[41].callConstructor(ListExpression.class);
            ScriptBytecodeAdapter.setProperty(object8, null, field.get(), "initialValueExpression");
        }
        Object name = DefaultTypeTransformation.booleanUnbox(object = callSiteArray[42].callGetPropertySafe(callSiteArray[43].call((Object)node.get(), "name"))) ? object : callSiteArray[44].callGetProperty(listener);
        public class _visit_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visit_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(MethodNode m) {
                CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                if (!BytecodeInterface8.isOrigZ() || __$stMC || BytecodeInterface8.disabledStandardMetaClass()) {
                    return DefaultTypeTransformation.booleanUnbox(callSiteArray[0].call(m)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[1].call(m)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[2].call(m));
                }
                return DefaultTypeTransformation.booleanUnbox(callSiteArray[3].call(m)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[4].call(m)) && !DefaultTypeTransformation.booleanUnbox(callSiteArray[5].call(m));
            }

            public Object call(MethodNode m) {
                CallSite[] callSiteArray = _visit_closure1.$getCallSiteArray();
                return callSiteArray[6].callCurrent((GroovyObject)this, m);
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
                stringArray[0] = "isPublic";
                stringArray[1] = "isSynthetic";
                stringArray[2] = "isStatic";
                stringArray[3] = "isPublic";
                stringArray[4] = "isSynthetic";
                stringArray[5] = "isStatic";
                stringArray[6] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[7];
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
        Object fireList = callSiteArray[45].call(callSiteArray[46].callGetProperty(listener), new _visit_closure1(this, this));
        Object object9 = callSiteArray[47].callGetPropertySafe(callSiteArray[48].call((Object)node.get(), "synchronize"));
        Reference<Object> synchronize = new Reference<Object>(DefaultTypeTransformation.booleanUnbox(object9) ? object9 : Boolean.valueOf(false));
        callSiteArray[49].callCurrent((GroovyObject)this, ArrayUtil.createArray(source2.get(), node.get(), declaringClass.get(), field.get(), listener, name, synchronize.get()));
        callSiteArray[50].callCurrent((GroovyObject)this, ArrayUtil.createArray(source2.get(), node.get(), declaringClass.get(), field.get(), listener, name, synchronize.get()));
        callSiteArray[51].callCurrent((GroovyObject)this, ArrayUtil.createArray(source2.get(), node.get(), declaringClass.get(), field.get(), listener, name, synchronize.get()));
        public class _visit_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference source;
            private /* synthetic */ Reference node;
            private /* synthetic */ Reference declaringClass;
            private /* synthetic */ Reference field;
            private /* synthetic */ Reference types;
            private /* synthetic */ Reference synchronize;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visit_closure2(Object _outerInstance, Object _thisObject, Reference source, Reference node, Reference declaringClass, Reference field, Reference types, Reference synchronize) {
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                Reference reference5;
                Reference reference6;
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.source = reference6 = source;
                this.node = reference5 = node;
                this.declaringClass = reference4 = declaringClass;
                this.field = reference3 = field;
                this.types = reference2 = types;
                this.synchronize = reference = synchronize;
            }

            public Object doCall(MethodNode method) {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return callSiteArray[0].callCurrent((GroovyObject)this, ArrayUtil.createArray(this.source.get(), this.node.get(), this.declaringClass.get(), this.field.get(), this.types.get(), this.synchronize.get(), method));
            }

            public Object call(MethodNode method) {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return callSiteArray[1].callCurrent((GroovyObject)this, method);
            }

            public SourceUnit getSource() {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return (SourceUnit)ScriptBytecodeAdapter.castToType(this.source.get(), SourceUnit.class);
            }

            public AnnotationNode getNode() {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return (AnnotationNode)ScriptBytecodeAdapter.castToType(this.node.get(), AnnotationNode.class);
            }

            public ClassNode getDeclaringClass() {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return (ClassNode)ScriptBytecodeAdapter.castToType(this.declaringClass.get(), ClassNode.class);
            }

            public FieldNode getField() {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return (FieldNode)ScriptBytecodeAdapter.castToType(this.field.get(), FieldNode.class);
            }

            public Object getTypes() {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return this.types.get();
            }

            public Object getSynchronize() {
                CallSite[] callSiteArray = _visit_closure2.$getCallSiteArray();
                return this.synchronize.get();
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
                stringArray[0] = "addFireMethods";
                stringArray[1] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
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
        callSiteArray[52].call(fireList, new _visit_closure2(this, this, source2, node, declaringClass, field, types, synchronize));
    }

    private static Object addError(AnnotationNode node, SourceUnit source, String message) {
        CallSite[] callSiteArray = ListenerListASTTransformation.$getCallSiteArray();
        return callSiteArray[53].call(callSiteArray[54].callGetProperty(source), callSiteArray[55].callConstructor(SyntaxErrorMessage.class, callSiteArray[56].callConstructor(SyntaxException.class, message, callSiteArray[57].callGetProperty(node), callSiteArray[58].callGetProperty(node)), source));
    }

    public void addAddListener(SourceUnit source, AnnotationNode node, ClassNode declaringClass, FieldNode field, ClassNode listener, String name, Object synchronize) {
        CallSite[] callSiteArray = ListenerListASTTransformation.$getCallSiteArray();
        Object methodModifiers = DefaultTypeTransformation.booleanUnbox(synchronize) ? callSiteArray[59].call(callSiteArray[60].callGroovyObjectGetProperty(this), callSiteArray[61].callGroovyObjectGetProperty(this)) : callSiteArray[62].callGroovyObjectGetProperty(this);
        Object methodReturnType = callSiteArray[63].call(ClassHelper.class, callSiteArray[64].callGetProperty(Void.class));
        GStringImpl methodName = new GStringImpl(new Object[]{callSiteArray[65].call(name)}, new String[]{"add", ""});
        Object cn = callSiteArray[66].call(ClassHelper.class, callSiteArray[67].callGetProperty(listener));
        ClassNode classNode = listener;
        ScriptBytecodeAdapter.setProperty(classNode, null, cn, "redirect");
        Parameter[] methodParameter = (Parameter[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[68].callConstructor(Parameter.class, cn, "listener")}), Parameter[].class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[69].call(declaringClass, methodName, methodParameter))) {
            callSiteArray[70].callStatic(ListenerListASTTransformation.class, node, source, new GStringImpl(new Object[]{callSiteArray[71].callGetProperty(MY_CLASS), callSiteArray[72].callGetProperty(declaringClass), methodName}, new String[]{"Conflict using @", ". Class ", " already has method ", ""}));
            return;
        }
        BlockStatement block = (BlockStatement)ScriptBytecodeAdapter.castToType(callSiteArray[73].callConstructor(BlockStatement.class), BlockStatement.class);
        callSiteArray[74].call((Object)block, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[75].callConstructor(IfStatement.class, callSiteArray[76].callConstructor(BooleanExpression.class, callSiteArray[77].callConstructor(BinaryExpression.class, callSiteArray[78].callConstructor(VariableExpression.class, "listener"), callSiteArray[79].call(Token.class, callSiteArray[80].callGetProperty(Types.class), 0, 0), callSiteArray[81].callGetProperty(ConstantExpression.class))), callSiteArray[82].callConstructor(ReturnStatement.class, callSiteArray[83].callGetProperty(ConstantExpression.class)), callSiteArray[84].callGetProperty(EmptyStatement.class)), callSiteArray[85].callConstructor(IfStatement.class, callSiteArray[86].callConstructor(BooleanExpression.class, callSiteArray[87].callConstructor(BinaryExpression.class, callSiteArray[88].callConstructor(VariableExpression.class, callSiteArray[89].callGetProperty(field)), callSiteArray[90].call(Token.class, callSiteArray[91].callGetProperty(Types.class), 0, 0), callSiteArray[92].callGetProperty(ConstantExpression.class))), callSiteArray[93].callConstructor(ExpressionStatement.class, callSiteArray[94].callConstructor(BinaryExpression.class, callSiteArray[95].callConstructor(VariableExpression.class, callSiteArray[96].callGetProperty(field)), callSiteArray[97].call(Token.class, callSiteArray[98].callGetProperty(Types.class), 0, 0), callSiteArray[99].callConstructor(ListExpression.class))), callSiteArray[100].callGetProperty(EmptyStatement.class)), callSiteArray[101].callConstructor(ExpressionStatement.class, callSiteArray[102].callConstructor(MethodCallExpression.class, callSiteArray[103].callConstructor(VariableExpression.class, callSiteArray[104].callGetProperty(field)), callSiteArray[105].callConstructor(ConstantExpression.class, "add"), callSiteArray[106].callConstructor(ArgumentListExpression.class, callSiteArray[107].callConstructor(VariableExpression.class, "listener"))))}));
        callSiteArray[108].call((Object)declaringClass, callSiteArray[109].callConstructor((Object)MethodNode.class, ArrayUtil.createArray(methodName, methodModifiers, methodReturnType, methodParameter, ScriptBytecodeAdapter.createPojoWrapper((ClassNode[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), ClassNode[].class), ClassNode[].class), block)));
    }

    public void addRemoveListener(SourceUnit source, AnnotationNode node, ClassNode declaringClass, FieldNode field, ClassNode listener, String name, Object synchronize) {
        CallSite[] callSiteArray = ListenerListASTTransformation.$getCallSiteArray();
        Object methodModifiers = DefaultTypeTransformation.booleanUnbox(synchronize) ? callSiteArray[110].call(callSiteArray[111].callGroovyObjectGetProperty(this), callSiteArray[112].callGroovyObjectGetProperty(this)) : callSiteArray[113].callGroovyObjectGetProperty(this);
        Object methodReturnType = callSiteArray[114].call(ClassHelper.class, callSiteArray[115].callGetProperty(Void.class));
        GStringImpl methodName = new GStringImpl(new Object[]{callSiteArray[116].call(name)}, new String[]{"remove", ""});
        Object cn = callSiteArray[117].call(ClassHelper.class, callSiteArray[118].callGetProperty(listener));
        ClassNode classNode = listener;
        ScriptBytecodeAdapter.setProperty(classNode, null, cn, "redirect");
        Parameter[] methodParameter = (Parameter[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[119].callConstructor(Parameter.class, cn, "listener")}), Parameter[].class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[120].call(declaringClass, methodName, methodParameter))) {
            callSiteArray[121].callStatic(ListenerListASTTransformation.class, node, source, new GStringImpl(new Object[]{callSiteArray[122].callGetProperty(MY_CLASS), callSiteArray[123].callGetProperty(declaringClass), methodName}, new String[]{"Conflict using @", ". Class ", " already has method ", ""}));
            return;
        }
        BlockStatement block = (BlockStatement)ScriptBytecodeAdapter.castToType(callSiteArray[124].callConstructor(BlockStatement.class), BlockStatement.class);
        callSiteArray[125].call((Object)block, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[126].callConstructor(IfStatement.class, callSiteArray[127].callConstructor(BooleanExpression.class, callSiteArray[128].callConstructor(BinaryExpression.class, callSiteArray[129].callConstructor(VariableExpression.class, "listener"), callSiteArray[130].call(Token.class, callSiteArray[131].callGetProperty(Types.class), 0, 0), callSiteArray[132].callGetProperty(ConstantExpression.class))), callSiteArray[133].callConstructor(ReturnStatement.class, callSiteArray[134].callGetProperty(ConstantExpression.class)), callSiteArray[135].callGetProperty(EmptyStatement.class)), callSiteArray[136].callConstructor(IfStatement.class, callSiteArray[137].callConstructor(BooleanExpression.class, callSiteArray[138].callConstructor(BinaryExpression.class, callSiteArray[139].callConstructor(VariableExpression.class, callSiteArray[140].callGetProperty(field)), callSiteArray[141].call(Token.class, callSiteArray[142].callGetProperty(Types.class), 0, 0), callSiteArray[143].callGetProperty(ConstantExpression.class))), callSiteArray[144].callConstructor(ExpressionStatement.class, callSiteArray[145].callConstructor(BinaryExpression.class, callSiteArray[146].callConstructor(VariableExpression.class, callSiteArray[147].callGetProperty(field)), callSiteArray[148].call(Token.class, callSiteArray[149].callGetProperty(Types.class), 0, 0), callSiteArray[150].callConstructor(ListExpression.class))), callSiteArray[151].callGetProperty(EmptyStatement.class)), callSiteArray[152].callConstructor(ExpressionStatement.class, callSiteArray[153].callConstructor(MethodCallExpression.class, callSiteArray[154].callConstructor(VariableExpression.class, callSiteArray[155].callGetProperty(field)), callSiteArray[156].callConstructor(ConstantExpression.class, "remove"), callSiteArray[157].callConstructor(ArgumentListExpression.class, callSiteArray[158].callConstructor(VariableExpression.class, "listener"))))}));
        callSiteArray[159].call((Object)declaringClass, callSiteArray[160].callConstructor((Object)MethodNode.class, ArrayUtil.createArray(methodName, methodModifiers, methodReturnType, methodParameter, ScriptBytecodeAdapter.createPojoWrapper((ClassNode[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), ClassNode[].class), ClassNode[].class), block)));
    }

    public void addGetListeners(SourceUnit source, AnnotationNode node, ClassNode declaringClass, FieldNode field, ClassNode listener, String name, Object synchronize) {
        CallSite[] callSiteArray = ListenerListASTTransformation.$getCallSiteArray();
        Object methodModifiers = DefaultTypeTransformation.booleanUnbox(synchronize) ? callSiteArray[161].call(callSiteArray[162].callGroovyObjectGetProperty(this), callSiteArray[163].callGroovyObjectGetProperty(this)) : callSiteArray[164].callGroovyObjectGetProperty(this);
        Object methodReturnType = callSiteArray[165].call(listener);
        GStringImpl methodName = new GStringImpl(new Object[]{callSiteArray[166].call(name)}, new String[]{"get", "s"});
        Parameter[] methodParameter = (Parameter[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), Parameter[].class);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[167].call(declaringClass, methodName, methodParameter))) {
            callSiteArray[168].callStatic(ListenerListASTTransformation.class, node, source, new GStringImpl(new Object[]{callSiteArray[169].callGetProperty(MY_CLASS), callSiteArray[170].callGetProperty(declaringClass), methodName}, new String[]{"Conflict using @", ". Class ", " already has method ", ""}));
            return;
        }
        BlockStatement block = (BlockStatement)ScriptBytecodeAdapter.castToType(callSiteArray[171].callConstructor(BlockStatement.class), BlockStatement.class);
        callSiteArray[172].call((Object)block, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[173].callConstructor(ExpressionStatement.class, callSiteArray[174].callConstructor(DeclarationExpression.class, callSiteArray[175].callConstructor(VariableExpression.class, "__result", callSiteArray[176].callGetProperty(ClassHelper.class)), callSiteArray[177].call(Token.class, callSiteArray[178].callGetProperty(Types.class), 0, 0), callSiteArray[179].callConstructor(ListExpression.class))), callSiteArray[180].callConstructor(IfStatement.class, callSiteArray[181].callConstructor(BooleanExpression.class, callSiteArray[182].callConstructor(BinaryExpression.class, callSiteArray[183].callConstructor(VariableExpression.class, callSiteArray[184].callGetProperty(field)), callSiteArray[185].call(Token.class, callSiteArray[186].callGetProperty(Types.class), 0, 0), callSiteArray[187].callGetProperty(ConstantExpression.class))), callSiteArray[188].callConstructor(ExpressionStatement.class, callSiteArray[189].callConstructor(MethodCallExpression.class, callSiteArray[190].callConstructor(VariableExpression.class, "__result"), callSiteArray[191].callConstructor(ConstantExpression.class, "addAll"), callSiteArray[192].callConstructor(ArgumentListExpression.class, callSiteArray[193].callConstructor(VariableExpression.class, callSiteArray[194].callGetProperty(field))))), callSiteArray[195].callGetProperty(EmptyStatement.class)), callSiteArray[196].callConstructor(ReturnStatement.class, callSiteArray[197].callConstructor(CastExpression.class, methodReturnType, callSiteArray[198].callConstructor(VariableExpression.class, "__result")))}));
        callSiteArray[199].call((Object)declaringClass, callSiteArray[200].callConstructor((Object)MethodNode.class, ArrayUtil.createArray(methodName, methodModifiers, methodReturnType, methodParameter, ScriptBytecodeAdapter.createPojoWrapper((ClassNode[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), ClassNode[].class), ClassNode[].class), block)));
    }

    public void addFireMethods(SourceUnit source, AnnotationNode node, ClassNode declaringClass, FieldNode field, GenericsType[] types, boolean synchronize, MethodNode method) {
        CallSite[] callSiteArray = ListenerListASTTransformation.$getCallSiteArray();
        Object methodReturnType = callSiteArray[201].call(ClassHelper.class, callSiteArray[202].callGetProperty(Void.class));
        GStringImpl methodName = new GStringImpl(new Object[]{callSiteArray[203].call(callSiteArray[204].callGetProperty(method))}, new String[]{"fire", ""});
        Object methodModifiers = synchronize ? callSiteArray[205].call(callSiteArray[206].callGroovyObjectGetProperty(this), callSiteArray[207].callGroovyObjectGetProperty(this)) : callSiteArray[208].callGroovyObjectGetProperty(this);
        if (DefaultTypeTransformation.booleanUnbox(callSiteArray[209].call(declaringClass, methodName, callSiteArray[210].callGetProperty(method)))) {
            callSiteArray[211].callStatic(ListenerListASTTransformation.class, node, source, new GStringImpl(new Object[]{callSiteArray[212].callGetProperty(MY_CLASS), callSiteArray[213].callGetProperty(declaringClass), methodName}, new String[]{"Conflict using @", ". Class ", " already has method ", ""}));
            return;
        }
        Object args = callSiteArray[214].callConstructor(ArgumentListExpression.class, callSiteArray[215].callGetProperty(method));
        BlockStatement block = (BlockStatement)ScriptBytecodeAdapter.castToType(callSiteArray[216].callConstructor(BlockStatement.class), BlockStatement.class);
        Object listenerListType = callSiteArray[217].callGetProperty(callSiteArray[218].call(ClassHelper.class, ArrayList.class));
        callSiteArray[219].call(listenerListType, (Object)types);
        callSiteArray[220].call((Object)block, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[221].callConstructor(IfStatement.class, callSiteArray[222].callConstructor(BooleanExpression.class, callSiteArray[223].callConstructor(BinaryExpression.class, callSiteArray[224].callConstructor(VariableExpression.class, callSiteArray[225].callGetProperty(field)), callSiteArray[226].call(Token.class, callSiteArray[227].callGetProperty(Types.class), 0, 0), callSiteArray[228].callGetProperty(ConstantExpression.class))), callSiteArray[229].callConstructor(BlockStatement.class, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[230].callConstructor(ExpressionStatement.class, callSiteArray[231].callConstructor(DeclarationExpression.class, callSiteArray[232].callConstructor(VariableExpression.class, "__list", listenerListType), callSiteArray[233].call(Token.class, callSiteArray[234].callGetProperty(Types.class), 0, 0), callSiteArray[235].callConstructor(ConstructorCallExpression.class, listenerListType, callSiteArray[236].callConstructor(ArgumentListExpression.class, callSiteArray[237].callConstructor(VariableExpression.class, callSiteArray[238].callGetProperty(field)))))), callSiteArray[239].callConstructor(ForStatement.class, callSiteArray[240].callConstructor(Parameter.class, callSiteArray[241].callGetProperty(ClassHelper.class), "listener"), callSiteArray[242].callConstructor(VariableExpression.class, "__list"), callSiteArray[243].callConstructor(BlockStatement.class, ScriptBytecodeAdapter.createList(new Object[]{callSiteArray[244].callConstructor(ExpressionStatement.class, callSiteArray[245].callConstructor(MethodCallExpression.class, callSiteArray[246].callConstructor(VariableExpression.class, "listener"), callSiteArray[247].callGetProperty(method), args))}), callSiteArray[248].callConstructor(VariableScope.class)))}), callSiteArray[249].callConstructor(VariableScope.class)), callSiteArray[250].callGetProperty(EmptyStatement.class))}));
        public class _addFireMethods_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _addFireMethods_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _addFireMethods_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _addFireMethods_closure3.$getCallSiteArray();
                Object paramType = callSiteArray[0].call(ClassHelper.class, callSiteArray[1].callGetProperty(it));
                Object cn = callSiteArray[2].callGetProperty(paramType);
                callSiteArray[3].call(cn, paramType);
                return callSiteArray[4].callConstructor(Parameter.class, cn, callSiteArray[5].callGetProperty(it));
            }

            public Object doCall() {
                CallSite[] callSiteArray = _addFireMethods_closure3.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _addFireMethods_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "getWrapper";
                stringArray[1] = "type";
                stringArray[2] = "plainNodeReference";
                stringArray[3] = "setRedirect";
                stringArray[4] = "<$constructor$>";
                stringArray[5] = "name";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[6];
                _addFireMethods_closure3.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_addFireMethods_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _addFireMethods_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        Object params = callSiteArray[251].call(callSiteArray[252].callGetProperty(method), new _addFireMethods_closure3(this, this));
        callSiteArray[253].call((Object)declaringClass, ArrayUtil.createArray(methodName, methodModifiers, methodReturnType, ScriptBytecodeAdapter.createPojoWrapper((Parameter[])ScriptBytecodeAdapter.asType(params, Parameter[].class), Parameter[].class), ScriptBytecodeAdapter.createPojoWrapper((ClassNode[])ScriptBytecodeAdapter.asType(ScriptBytecodeAdapter.createList(new Object[0]), ClassNode[].class), ClassNode[].class), block));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ListenerListASTTransformation.class) {
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

    static {
        Class<ListenerList> clazz;
        MY_CLASS = clazz = ListenerList.class;
        Object object = ListenerListASTTransformation.$getCallSiteArray()[254].call(ClassHelper.class, Collection.class);
        COLLECTION_TYPE = (ClassNode)ScriptBytecodeAdapter.castToType(object, ClassNode.class);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "getAt";
        stringArray[1] = "getAt";
        stringArray[2] = "<$constructor$>";
        stringArray[3] = "class";
        stringArray[4] = "node";
        stringArray[5] = "class";
        stringArray[6] = "parent";
        stringArray[7] = "<$constructor$>";
        stringArray[8] = "class";
        stringArray[9] = "node";
        stringArray[10] = "class";
        stringArray[11] = "parent";
        stringArray[12] = "getAt";
        stringArray[13] = "getAt";
        stringArray[14] = "declaringClass";
        stringArray[15] = "getAt";
        stringArray[16] = "declaringClass";
        stringArray[17] = "type";
        stringArray[18] = "isDerivedFrom";
        stringArray[19] = "implementsInterface";
        stringArray[20] = "isDerivedFrom";
        stringArray[21] = "implementsInterface";
        stringArray[22] = "addError";
        stringArray[23] = "plus";
        stringArray[24] = "plus";
        stringArray[25] = "name";
        stringArray[26] = "genericsTypes";
        stringArray[27] = "type";
        stringArray[28] = "addError";
        stringArray[29] = "plus";
        stringArray[30] = "plus";
        stringArray[31] = "name";
        stringArray[32] = "wildcard";
        stringArray[33] = "getAt";
        stringArray[34] = "addError";
        stringArray[35] = "plus";
        stringArray[36] = "plus";
        stringArray[37] = "name";
        stringArray[38] = "type";
        stringArray[39] = "getAt";
        stringArray[40] = "initialValueExpression";
        stringArray[41] = "<$constructor$>";
        stringArray[42] = "value";
        stringArray[43] = "getMember";
        stringArray[44] = "nameWithoutPackage";
        stringArray[45] = "findAll";
        stringArray[46] = "methods";
        stringArray[47] = "value";
        stringArray[48] = "getMember";
        stringArray[49] = "addAddListener";
        stringArray[50] = "addRemoveListener";
        stringArray[51] = "addGetListeners";
        stringArray[52] = "each";
        stringArray[53] = "addError";
        stringArray[54] = "errorCollector";
        stringArray[55] = "<$constructor$>";
        stringArray[56] = "<$constructor$>";
        stringArray[57] = "lineNumber";
        stringArray[58] = "columnNumber";
        stringArray[59] = "or";
        stringArray[60] = "ACC_PUBLIC";
        stringArray[61] = "ACC_SYNCHRONIZED";
        stringArray[62] = "ACC_PUBLIC";
        stringArray[63] = "make";
        stringArray[64] = "TYPE";
        stringArray[65] = "capitalize";
        stringArray[66] = "makeWithoutCaching";
        stringArray[67] = "name";
        stringArray[68] = "<$constructor$>";
        stringArray[69] = "hasMethod";
        stringArray[70] = "addError";
        stringArray[71] = "name";
        stringArray[72] = "name";
        stringArray[73] = "<$constructor$>";
        stringArray[74] = "addStatements";
        stringArray[75] = "<$constructor$>";
        stringArray[76] = "<$constructor$>";
        stringArray[77] = "<$constructor$>";
        stringArray[78] = "<$constructor$>";
        stringArray[79] = "newSymbol";
        stringArray[80] = "COMPARE_EQUAL";
        stringArray[81] = "NULL";
        stringArray[82] = "<$constructor$>";
        stringArray[83] = "NULL";
        stringArray[84] = "INSTANCE";
        stringArray[85] = "<$constructor$>";
        stringArray[86] = "<$constructor$>";
        stringArray[87] = "<$constructor$>";
        stringArray[88] = "<$constructor$>";
        stringArray[89] = "name";
        stringArray[90] = "newSymbol";
        stringArray[91] = "COMPARE_EQUAL";
        stringArray[92] = "NULL";
        stringArray[93] = "<$constructor$>";
        stringArray[94] = "<$constructor$>";
        stringArray[95] = "<$constructor$>";
        stringArray[96] = "name";
        stringArray[97] = "newSymbol";
        stringArray[98] = "EQUAL";
        stringArray[99] = "<$constructor$>";
        stringArray[100] = "INSTANCE";
        stringArray[101] = "<$constructor$>";
        stringArray[102] = "<$constructor$>";
        stringArray[103] = "<$constructor$>";
        stringArray[104] = "name";
        stringArray[105] = "<$constructor$>";
        stringArray[106] = "<$constructor$>";
        stringArray[107] = "<$constructor$>";
        stringArray[108] = "addMethod";
        stringArray[109] = "<$constructor$>";
        stringArray[110] = "or";
        stringArray[111] = "ACC_PUBLIC";
        stringArray[112] = "ACC_SYNCHRONIZED";
        stringArray[113] = "ACC_PUBLIC";
        stringArray[114] = "make";
        stringArray[115] = "TYPE";
        stringArray[116] = "capitalize";
        stringArray[117] = "makeWithoutCaching";
        stringArray[118] = "name";
        stringArray[119] = "<$constructor$>";
        stringArray[120] = "hasMethod";
        stringArray[121] = "addError";
        stringArray[122] = "name";
        stringArray[123] = "name";
        stringArray[124] = "<$constructor$>";
        stringArray[125] = "addStatements";
        stringArray[126] = "<$constructor$>";
        stringArray[127] = "<$constructor$>";
        stringArray[128] = "<$constructor$>";
        stringArray[129] = "<$constructor$>";
        stringArray[130] = "newSymbol";
        stringArray[131] = "COMPARE_EQUAL";
        stringArray[132] = "NULL";
        stringArray[133] = "<$constructor$>";
        stringArray[134] = "NULL";
        stringArray[135] = "INSTANCE";
        stringArray[136] = "<$constructor$>";
        stringArray[137] = "<$constructor$>";
        stringArray[138] = "<$constructor$>";
        stringArray[139] = "<$constructor$>";
        stringArray[140] = "name";
        stringArray[141] = "newSymbol";
        stringArray[142] = "COMPARE_EQUAL";
        stringArray[143] = "NULL";
        stringArray[144] = "<$constructor$>";
        stringArray[145] = "<$constructor$>";
        stringArray[146] = "<$constructor$>";
        stringArray[147] = "name";
        stringArray[148] = "newSymbol";
        stringArray[149] = "EQUAL";
        stringArray[150] = "<$constructor$>";
        stringArray[151] = "INSTANCE";
        stringArray[152] = "<$constructor$>";
        stringArray[153] = "<$constructor$>";
        stringArray[154] = "<$constructor$>";
        stringArray[155] = "name";
        stringArray[156] = "<$constructor$>";
        stringArray[157] = "<$constructor$>";
        stringArray[158] = "<$constructor$>";
        stringArray[159] = "addMethod";
        stringArray[160] = "<$constructor$>";
        stringArray[161] = "or";
        stringArray[162] = "ACC_PUBLIC";
        stringArray[163] = "ACC_SYNCHRONIZED";
        stringArray[164] = "ACC_PUBLIC";
        stringArray[165] = "makeArray";
        stringArray[166] = "capitalize";
        stringArray[167] = "hasMethod";
        stringArray[168] = "addError";
        stringArray[169] = "name";
        stringArray[170] = "name";
        stringArray[171] = "<$constructor$>";
        stringArray[172] = "addStatements";
        stringArray[173] = "<$constructor$>";
        stringArray[174] = "<$constructor$>";
        stringArray[175] = "<$constructor$>";
        stringArray[176] = "DYNAMIC_TYPE";
        stringArray[177] = "newSymbol";
        stringArray[178] = "EQUALS";
        stringArray[179] = "<$constructor$>";
        stringArray[180] = "<$constructor$>";
        stringArray[181] = "<$constructor$>";
        stringArray[182] = "<$constructor$>";
        stringArray[183] = "<$constructor$>";
        stringArray[184] = "name";
        stringArray[185] = "newSymbol";
        stringArray[186] = "COMPARE_NOT_EQUAL";
        stringArray[187] = "NULL";
        stringArray[188] = "<$constructor$>";
        stringArray[189] = "<$constructor$>";
        stringArray[190] = "<$constructor$>";
        stringArray[191] = "<$constructor$>";
        stringArray[192] = "<$constructor$>";
        stringArray[193] = "<$constructor$>";
        stringArray[194] = "name";
        stringArray[195] = "INSTANCE";
        stringArray[196] = "<$constructor$>";
        stringArray[197] = "<$constructor$>";
        stringArray[198] = "<$constructor$>";
        stringArray[199] = "addMethod";
        stringArray[200] = "<$constructor$>";
        stringArray[201] = "make";
        stringArray[202] = "TYPE";
        stringArray[203] = "capitalize";
        stringArray[204] = "name";
        stringArray[205] = "or";
        stringArray[206] = "ACC_PUBLIC";
        stringArray[207] = "ACC_SYNCHRONIZED";
        stringArray[208] = "ACC_PUBLIC";
        stringArray[209] = "hasMethod";
        stringArray[210] = "parameters";
        stringArray[211] = "addError";
        stringArray[212] = "name";
        stringArray[213] = "name";
        stringArray[214] = "<$constructor$>";
        stringArray[215] = "parameters";
        stringArray[216] = "<$constructor$>";
        stringArray[217] = "plainNodeReference";
        stringArray[218] = "make";
        stringArray[219] = "setGenericsTypes";
        stringArray[220] = "addStatements";
        stringArray[221] = "<$constructor$>";
        stringArray[222] = "<$constructor$>";
        stringArray[223] = "<$constructor$>";
        stringArray[224] = "<$constructor$>";
        stringArray[225] = "name";
        stringArray[226] = "newSymbol";
        stringArray[227] = "COMPARE_NOT_EQUAL";
        stringArray[228] = "NULL";
        stringArray[229] = "<$constructor$>";
        stringArray[230] = "<$constructor$>";
        stringArray[231] = "<$constructor$>";
        stringArray[232] = "<$constructor$>";
        stringArray[233] = "newSymbol";
        stringArray[234] = "EQUALS";
        stringArray[235] = "<$constructor$>";
        stringArray[236] = "<$constructor$>";
        stringArray[237] = "<$constructor$>";
        stringArray[238] = "name";
        stringArray[239] = "<$constructor$>";
        stringArray[240] = "<$constructor$>";
        stringArray[241] = "DYNAMIC_TYPE";
        stringArray[242] = "<$constructor$>";
        stringArray[243] = "<$constructor$>";
        stringArray[244] = "<$constructor$>";
        stringArray[245] = "<$constructor$>";
        stringArray[246] = "<$constructor$>";
        stringArray[247] = "name";
        stringArray[248] = "<$constructor$>";
        stringArray[249] = "<$constructor$>";
        stringArray[250] = "INSTANCE";
        stringArray[251] = "collect";
        stringArray[252] = "parameters";
        stringArray[253] = "addMethod";
        stringArray[254] = "make";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[255];
        ListenerListASTTransformation.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(ListenerListASTTransformation.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = ListenerListASTTransformation.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


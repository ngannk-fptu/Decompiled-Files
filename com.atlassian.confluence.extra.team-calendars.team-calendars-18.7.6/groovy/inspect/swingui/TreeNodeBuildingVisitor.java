/*
 * Decompiled with CFR 0.152.
 */
package groovy.inspect.swingui;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.lang.ref.SoftReference;
import java.util.List;
import org.codehaus.groovy.ast.CodeVisitorSupport;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ArrayExpression;
import org.codehaus.groovy.ast.expr.AttributeExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.BitwiseNegationExpression;
import org.codehaus.groovy.ast.expr.BooleanExpression;
import org.codehaus.groovy.ast.expr.CastExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ClosureListExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.ElvisOperatorExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.FieldExpression;
import org.codehaus.groovy.ast.expr.GStringExpression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MapExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.MethodPointerExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.NotExpression;
import org.codehaus.groovy.ast.expr.PostfixExpression;
import org.codehaus.groovy.ast.expr.PrefixExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.RangeExpression;
import org.codehaus.groovy.ast.expr.SpreadExpression;
import org.codehaus.groovy.ast.expr.SpreadMapExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.UnaryMinusExpression;
import org.codehaus.groovy.ast.expr.UnaryPlusExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.AssertStatement;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.BreakStatement;
import org.codehaus.groovy.ast.stmt.CaseStatement;
import org.codehaus.groovy.ast.stmt.CatchStatement;
import org.codehaus.groovy.ast.stmt.ContinueStatement;
import org.codehaus.groovy.ast.stmt.DoWhileStatement;
import org.codehaus.groovy.ast.stmt.EmptyStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ForStatement;
import org.codehaus.groovy.ast.stmt.IfStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.SwitchStatement;
import org.codehaus.groovy.ast.stmt.SynchronizedStatement;
import org.codehaus.groovy.ast.stmt.ThrowStatement;
import org.codehaus.groovy.ast.stmt.TryCatchStatement;
import org.codehaus.groovy.ast.stmt.WhileStatement;
import org.codehaus.groovy.classgen.BytecodeExpression;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.callsite.CallSite;
import org.codehaus.groovy.runtime.callsite.CallSiteArray;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;

class TreeNodeBuildingVisitor
extends CodeVisitorSupport
implements GroovyObject {
    private Object currentNode;
    private final Object adapter;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;
    private static /* synthetic */ SoftReference $callSiteArray;

    private TreeNodeBuildingVisitor(Object adapter) {
        Object object;
        MetaClass metaClass;
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        this.metaClass = metaClass = this.$getStaticMetaClass();
        if (!DefaultTypeTransformation.booleanUnbox(adapter)) {
            throw (Throwable)callSiteArray[0].callConstructor(IllegalArgumentException.class, "Null: adapter");
        }
        this.adapter = object = adapter;
    }

    private void addNode(Object node, Class expectedSubclass, Closure superMethod) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        if (ScriptBytecodeAdapter.compareEqual(callSiteArray[1].call(expectedSubclass), callSiteArray[2].call(callSiteArray[3].call(node)))) {
            if (ScriptBytecodeAdapter.compareEqual(this.currentNode, null)) {
                Object object;
                this.currentNode = object = callSiteArray[4].call(this.adapter, node);
                callSiteArray[5].call((Object)superMethod, node);
            } else {
                Object object;
                Object object2;
                Object temp = this.currentNode;
                this.currentNode = object2 = callSiteArray[6].call(this.adapter, node);
                callSiteArray[7].call(temp, this.currentNode);
                Object object3 = temp;
                ScriptBytecodeAdapter.setProperty(object3, null, this.currentNode, "parent");
                callSiteArray[8].call((Object)superMethod, node);
                this.currentNode = object = temp;
            }
        } else {
            callSiteArray[9].call((Object)superMethod, node);
        }
    }

    @Override
    public void visitBlockStatement(BlockStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitBlockStatement_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitBlockStatement_closure1(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitBlockStatement_closure1.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitBlockStatement_closure1.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitBlockStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitBlockStatement_closure1.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBlockStatement_closure1.class) {
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
                return new CallSiteArray(_visitBlockStatement_closure1.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitBlockStatement_closure1.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[10].callCurrent(this, node, BlockStatement.class, new _visitBlockStatement_closure1(this, this));
    }

    @Override
    public void visitForLoop(ForStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitForLoop_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitForLoop_closure2(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitForLoop_closure2.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitForLoop_closure2.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitForLoop", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitForLoop_closure2.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitForLoop_closure2.class) {
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
                return new CallSiteArray(_visitForLoop_closure2.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitForLoop_closure2.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[11].callCurrent(this, node, ForStatement.class, new _visitForLoop_closure2(this, this));
    }

    @Override
    public void visitWhileLoop(WhileStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitWhileLoop_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitWhileLoop_closure3(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitWhileLoop_closure3.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitWhileLoop_closure3.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitWhileLoop", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitWhileLoop_closure3.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitWhileLoop_closure3.class) {
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
                return new CallSiteArray(_visitWhileLoop_closure3.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitWhileLoop_closure3.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[12].callCurrent(this, node, WhileStatement.class, new _visitWhileLoop_closure3(this, this));
    }

    @Override
    public void visitDoWhileLoop(DoWhileStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitDoWhileLoop_closure4
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitDoWhileLoop_closure4(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitDoWhileLoop_closure4.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitDoWhileLoop_closure4.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitDoWhileLoop", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitDoWhileLoop_closure4.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitDoWhileLoop_closure4.class) {
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
                return new CallSiteArray(_visitDoWhileLoop_closure4.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitDoWhileLoop_closure4.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[13].callCurrent(this, node, DoWhileStatement.class, new _visitDoWhileLoop_closure4(this, this));
    }

    @Override
    public void visitIfElse(IfStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitIfElse_closure5
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitIfElse_closure5(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitIfElse_closure5.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitIfElse_closure5.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitIfElse", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitIfElse_closure5.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitIfElse_closure5.class) {
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
                return new CallSiteArray(_visitIfElse_closure5.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitIfElse_closure5.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[14].callCurrent(this, node, IfStatement.class, new _visitIfElse_closure5(this, this));
    }

    @Override
    public void visitExpressionStatement(ExpressionStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitExpressionStatement_closure6
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitExpressionStatement_closure6(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitExpressionStatement_closure6.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitExpressionStatement_closure6.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitExpressionStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitExpressionStatement_closure6.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitExpressionStatement_closure6.class) {
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
                return new CallSiteArray(_visitExpressionStatement_closure6.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitExpressionStatement_closure6.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[15].callCurrent(this, node, ExpressionStatement.class, new _visitExpressionStatement_closure6(this, this));
    }

    @Override
    public void visitReturnStatement(ReturnStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitReturnStatement_closure7
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitReturnStatement_closure7(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitReturnStatement_closure7.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitReturnStatement_closure7.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitReturnStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitReturnStatement_closure7.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitReturnStatement_closure7.class) {
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
                return new CallSiteArray(_visitReturnStatement_closure7.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitReturnStatement_closure7.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[16].callCurrent(this, node, ReturnStatement.class, new _visitReturnStatement_closure7(this, this));
    }

    @Override
    public void visitAssertStatement(AssertStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitAssertStatement_closure8
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitAssertStatement_closure8(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitAssertStatement_closure8.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitAssertStatement_closure8.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitAssertStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitAssertStatement_closure8.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitAssertStatement_closure8.class) {
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
                return new CallSiteArray(_visitAssertStatement_closure8.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitAssertStatement_closure8.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[17].callCurrent(this, node, AssertStatement.class, new _visitAssertStatement_closure8(this, this));
    }

    @Override
    public void visitTryCatchFinally(TryCatchStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitTryCatchFinally_closure9
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitTryCatchFinally_closure9(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure9.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure9.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitTryCatchFinally", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitTryCatchFinally_closure9.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitTryCatchFinally_closure9.class) {
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
                return new CallSiteArray(_visitTryCatchFinally_closure9.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitTryCatchFinally_closure9.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[18].callCurrent(this, node, TryCatchStatement.class, new _visitTryCatchFinally_closure9(this, this));
    }

    @Override
    protected void visitEmptyStatement(EmptyStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitEmptyStatement_closure10
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitEmptyStatement_closure10(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitEmptyStatement_closure10.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitEmptyStatement_closure10.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitEmptyStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitEmptyStatement_closure10.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitEmptyStatement_closure10.class) {
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
                return new CallSiteArray(_visitEmptyStatement_closure10.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitEmptyStatement_closure10.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[19].callCurrent(this, node, EmptyStatement.class, new _visitEmptyStatement_closure10(this, this));
    }

    @Override
    public void visitSwitch(SwitchStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitSwitch_closure11
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitSwitch_closure11(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitSwitch_closure11.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitSwitch_closure11.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitSwitch", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitSwitch_closure11.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitSwitch_closure11.class) {
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
                return new CallSiteArray(_visitSwitch_closure11.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitSwitch_closure11.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[20].callCurrent(this, node, SwitchStatement.class, new _visitSwitch_closure11(this, this));
    }

    @Override
    public void visitCaseStatement(CaseStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitCaseStatement_closure12
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitCaseStatement_closure12(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitCaseStatement_closure12.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitCaseStatement_closure12.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitCaseStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitCaseStatement_closure12.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitCaseStatement_closure12.class) {
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
                return new CallSiteArray(_visitCaseStatement_closure12.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitCaseStatement_closure12.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[21].callCurrent(this, node, CaseStatement.class, new _visitCaseStatement_closure12(this, this));
    }

    @Override
    public void visitBreakStatement(BreakStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitBreakStatement_closure13
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitBreakStatement_closure13(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitBreakStatement_closure13.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitBreakStatement_closure13.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitBreakStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitBreakStatement_closure13.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBreakStatement_closure13.class) {
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
                return new CallSiteArray(_visitBreakStatement_closure13.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitBreakStatement_closure13.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[22].callCurrent(this, node, BreakStatement.class, new _visitBreakStatement_closure13(this, this));
    }

    @Override
    public void visitContinueStatement(ContinueStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitContinueStatement_closure14
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitContinueStatement_closure14(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitContinueStatement_closure14.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitContinueStatement_closure14.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitContinueStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitContinueStatement_closure14.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitContinueStatement_closure14.class) {
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
                return new CallSiteArray(_visitContinueStatement_closure14.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitContinueStatement_closure14.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[23].callCurrent(this, node, ContinueStatement.class, new _visitContinueStatement_closure14(this, this));
    }

    @Override
    public void visitSynchronizedStatement(SynchronizedStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitSynchronizedStatement_closure15
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitSynchronizedStatement_closure15(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitSynchronizedStatement_closure15.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitSynchronizedStatement_closure15.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitSynchronizedStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitSynchronizedStatement_closure15.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitSynchronizedStatement_closure15.class) {
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
                return new CallSiteArray(_visitSynchronizedStatement_closure15.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitSynchronizedStatement_closure15.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[24].callCurrent(this, node, SynchronizedStatement.class, new _visitSynchronizedStatement_closure15(this, this));
    }

    @Override
    public void visitThrowStatement(ThrowStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitThrowStatement_closure16
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitThrowStatement_closure16(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitThrowStatement_closure16.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitThrowStatement_closure16.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitThrowStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitThrowStatement_closure16.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitThrowStatement_closure16.class) {
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
                return new CallSiteArray(_visitThrowStatement_closure16.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitThrowStatement_closure16.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[25].callCurrent(this, node, ThrowStatement.class, new _visitThrowStatement_closure16(this, this));
    }

    @Override
    public void visitMethodCallExpression(MethodCallExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitMethodCallExpression_closure17
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitMethodCallExpression_closure17(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitMethodCallExpression_closure17.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitMethodCallExpression_closure17.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitMethodCallExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitMethodCallExpression_closure17.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitMethodCallExpression_closure17.class) {
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
                return new CallSiteArray(_visitMethodCallExpression_closure17.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitMethodCallExpression_closure17.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[26].callCurrent(this, node, MethodCallExpression.class, new _visitMethodCallExpression_closure17(this, this));
    }

    @Override
    public void visitStaticMethodCallExpression(StaticMethodCallExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitStaticMethodCallExpression_closure18
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitStaticMethodCallExpression_closure18(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitStaticMethodCallExpression_closure18.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitStaticMethodCallExpression_closure18.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitStaticMethodCallExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitStaticMethodCallExpression_closure18.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitStaticMethodCallExpression_closure18.class) {
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
                return new CallSiteArray(_visitStaticMethodCallExpression_closure18.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitStaticMethodCallExpression_closure18.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[27].callCurrent(this, node, StaticMethodCallExpression.class, new _visitStaticMethodCallExpression_closure18(this, this));
    }

    @Override
    public void visitConstructorCallExpression(ConstructorCallExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitConstructorCallExpression_closure19
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitConstructorCallExpression_closure19(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitConstructorCallExpression_closure19.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitConstructorCallExpression_closure19.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitConstructorCallExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitConstructorCallExpression_closure19.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitConstructorCallExpression_closure19.class) {
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
                return new CallSiteArray(_visitConstructorCallExpression_closure19.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitConstructorCallExpression_closure19.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[28].callCurrent(this, node, ConstructorCallExpression.class, new _visitConstructorCallExpression_closure19(this, this));
    }

    @Override
    public void visitBinaryExpression(BinaryExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitBinaryExpression_closure20
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitBinaryExpression_closure20(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitBinaryExpression_closure20.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitBinaryExpression_closure20.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitBinaryExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitBinaryExpression_closure20.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBinaryExpression_closure20.class) {
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
                return new CallSiteArray(_visitBinaryExpression_closure20.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitBinaryExpression_closure20.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[29].callCurrent(this, node, BinaryExpression.class, new _visitBinaryExpression_closure20(this, this));
    }

    @Override
    public void visitTernaryExpression(TernaryExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitTernaryExpression_closure21
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitTernaryExpression_closure21(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitTernaryExpression_closure21.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitTernaryExpression_closure21.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitTernaryExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitTernaryExpression_closure21.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitTernaryExpression_closure21.class) {
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
                return new CallSiteArray(_visitTernaryExpression_closure21.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitTernaryExpression_closure21.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[30].callCurrent(this, node, TernaryExpression.class, new _visitTernaryExpression_closure21(this, this));
    }

    @Override
    public void visitShortTernaryExpression(ElvisOperatorExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitShortTernaryExpression_closure22
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitShortTernaryExpression_closure22(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitShortTernaryExpression_closure22.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitShortTernaryExpression_closure22.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitShortTernaryExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitShortTernaryExpression_closure22.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitShortTernaryExpression_closure22.class) {
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
                return new CallSiteArray(_visitShortTernaryExpression_closure22.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitShortTernaryExpression_closure22.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[31].callCurrent(this, node, ElvisOperatorExpression.class, new _visitShortTernaryExpression_closure22(this, this));
    }

    @Override
    public void visitPostfixExpression(PostfixExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitPostfixExpression_closure23
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitPostfixExpression_closure23(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitPostfixExpression_closure23.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitPostfixExpression_closure23.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitPostfixExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitPostfixExpression_closure23.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitPostfixExpression_closure23.class) {
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
                return new CallSiteArray(_visitPostfixExpression_closure23.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitPostfixExpression_closure23.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[32].callCurrent(this, node, PostfixExpression.class, new _visitPostfixExpression_closure23(this, this));
    }

    @Override
    public void visitPrefixExpression(PrefixExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitPrefixExpression_closure24
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitPrefixExpression_closure24(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitPrefixExpression_closure24.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitPrefixExpression_closure24.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitPrefixExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitPrefixExpression_closure24.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitPrefixExpression_closure24.class) {
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
                return new CallSiteArray(_visitPrefixExpression_closure24.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitPrefixExpression_closure24.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[33].callCurrent(this, node, PrefixExpression.class, new _visitPrefixExpression_closure24(this, this));
    }

    @Override
    public void visitBooleanExpression(BooleanExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitBooleanExpression_closure25
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitBooleanExpression_closure25(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitBooleanExpression_closure25.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitBooleanExpression_closure25.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitBooleanExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitBooleanExpression_closure25.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBooleanExpression_closure25.class) {
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
                return new CallSiteArray(_visitBooleanExpression_closure25.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitBooleanExpression_closure25.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[34].callCurrent(this, node, BooleanExpression.class, new _visitBooleanExpression_closure25(this, this));
    }

    @Override
    public void visitNotExpression(NotExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitNotExpression_closure26
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitNotExpression_closure26(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitNotExpression_closure26.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitNotExpression_closure26.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitNotExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitNotExpression_closure26.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitNotExpression_closure26.class) {
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
                return new CallSiteArray(_visitNotExpression_closure26.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitNotExpression_closure26.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[35].callCurrent(this, node, NotExpression.class, new _visitNotExpression_closure26(this, this));
    }

    @Override
    public void visitClosureExpression(ClosureExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitClosureExpression_closure27
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClosureExpression_closure27(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitClosureExpression_closure27.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClosureExpression_closure27.$getCallSiteArray();
                public class _closure55
                extends Closure
                implements GeneratedClosure {
                    private static /* synthetic */ ClassInfo $staticClassInfo;
                    public static transient /* synthetic */ boolean __$stMC;
                    private static /* synthetic */ SoftReference $callSiteArray;

                    public _closure55(Object _outerInstance, Object _thisObject) {
                        CallSite[] callSiteArray = _closure55.$getCallSiteArray();
                        super(_outerInstance, _thisObject);
                    }

                    public Object doCall(Object parameter) {
                        CallSite[] callSiteArray = _closure55.$getCallSiteArray();
                        return callSiteArray[0].callCurrent((GroovyObject)this, parameter);
                    }

                    protected /* synthetic */ MetaClass $getStaticMetaClass() {
                        if (this.getClass() != _closure55.class) {
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
                        stringArray[0] = "visitParameter";
                        return new CallSiteArray(_closure55.class, stringArray);
                    }

                    private static /* synthetic */ CallSite[] $getCallSiteArray() {
                        CallSiteArray callSiteArray;
                        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                            callSiteArray = _closure55.$createCallSiteArray();
                            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                        }
                        return callSiteArray.array;
                    }
                }
                callSiteArray[0].callSafe(callSiteArray[1].callGetProperty(it), new _closure55(this, this.getThisObject()));
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitClosureExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClosureExpression_closure27.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClosureExpression_closure27.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "each";
                stringArray[1] = "parameters";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[2];
                _visitClosureExpression_closure27.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitClosureExpression_closure27.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClosureExpression_closure27.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[36].callCurrent(this, node, ClosureExpression.class, new _visitClosureExpression_closure27(this, this));
    }

    public void visitParameter(Parameter node) {
        Reference<Parameter> node2 = new Reference<Parameter>(node);
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitParameter_closure28
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference node;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitParameter_closure28(Object _outerInstance, Object _thisObject, Reference node) {
                Reference reference;
                CallSite[] callSiteArray = _visitParameter_closure28.$getCallSiteArray();
                super(_outerInstance, _thisObject);
                this.node = reference = node;
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitParameter_closure28.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callGetProperty(this.node.get()))) {
                    return callSiteArray[1].callSafe(callSiteArray[2].callGetProperty(this.node.get()), this.getThisObject());
                }
                return null;
            }

            public Parameter getNode() {
                CallSite[] callSiteArray = _visitParameter_closure28.$getCallSiteArray();
                return (Parameter)ScriptBytecodeAdapter.castToType(this.node.get(), Parameter.class);
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitParameter_closure28.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitParameter_closure28.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "initialExpression";
                stringArray[1] = "visit";
                stringArray[2] = "initialExpression";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _visitParameter_closure28.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitParameter_closure28.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitParameter_closure28.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[37].callCurrent(this, node2.get(), Parameter.class, new _visitParameter_closure28(this, this, node2));
    }

    @Override
    public void visitTupleExpression(TupleExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitTupleExpression_closure29
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitTupleExpression_closure29(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitTupleExpression_closure29.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitTupleExpression_closure29.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitTupleExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitTupleExpression_closure29.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitTupleExpression_closure29.class) {
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
                return new CallSiteArray(_visitTupleExpression_closure29.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitTupleExpression_closure29.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[38].callCurrent(this, node, TupleExpression.class, new _visitTupleExpression_closure29(this, this));
    }

    @Override
    public void visitListExpression(ListExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitListExpression_closure30
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitListExpression_closure30(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitListExpression_closure30.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitListExpression_closure30.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitListExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitListExpression_closure30.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitListExpression_closure30.class) {
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
                return new CallSiteArray(_visitListExpression_closure30.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitListExpression_closure30.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[39].callCurrent(this, node, ListExpression.class, new _visitListExpression_closure30(this, this));
    }

    @Override
    public void visitArrayExpression(ArrayExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitArrayExpression_closure31
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitArrayExpression_closure31(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitArrayExpression_closure31.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitArrayExpression_closure31.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitArrayExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitArrayExpression_closure31.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitArrayExpression_closure31.class) {
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
                return new CallSiteArray(_visitArrayExpression_closure31.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitArrayExpression_closure31.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[40].callCurrent(this, node, ArrayExpression.class, new _visitArrayExpression_closure31(this, this));
    }

    @Override
    public void visitMapExpression(MapExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitMapExpression_closure32
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitMapExpression_closure32(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitMapExpression_closure32.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitMapExpression_closure32.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitMapExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitMapExpression_closure32.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitMapExpression_closure32.class) {
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
                return new CallSiteArray(_visitMapExpression_closure32.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitMapExpression_closure32.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[41].callCurrent(this, node, MapExpression.class, new _visitMapExpression_closure32(this, this));
    }

    @Override
    public void visitMapEntryExpression(MapEntryExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitMapEntryExpression_closure33
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitMapEntryExpression_closure33(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitMapEntryExpression_closure33.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitMapEntryExpression_closure33.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitMapEntryExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitMapEntryExpression_closure33.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitMapEntryExpression_closure33.class) {
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
                return new CallSiteArray(_visitMapEntryExpression_closure33.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitMapEntryExpression_closure33.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[42].callCurrent(this, node, MapEntryExpression.class, new _visitMapEntryExpression_closure33(this, this));
    }

    @Override
    public void visitRangeExpression(RangeExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitRangeExpression_closure34
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitRangeExpression_closure34(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitRangeExpression_closure34.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitRangeExpression_closure34.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitRangeExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitRangeExpression_closure34.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitRangeExpression_closure34.class) {
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
                return new CallSiteArray(_visitRangeExpression_closure34.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitRangeExpression_closure34.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[43].callCurrent(this, node, RangeExpression.class, new _visitRangeExpression_closure34(this, this));
    }

    @Override
    public void visitSpreadExpression(SpreadExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitSpreadExpression_closure35
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitSpreadExpression_closure35(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitSpreadExpression_closure35.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitSpreadExpression_closure35.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitSpreadExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitSpreadExpression_closure35.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitSpreadExpression_closure35.class) {
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
                return new CallSiteArray(_visitSpreadExpression_closure35.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitSpreadExpression_closure35.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[44].callCurrent(this, node, SpreadExpression.class, new _visitSpreadExpression_closure35(this, this));
    }

    @Override
    public void visitSpreadMapExpression(SpreadMapExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitSpreadMapExpression_closure36
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitSpreadMapExpression_closure36(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitSpreadMapExpression_closure36.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitSpreadMapExpression_closure36.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitSpreadMapExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitSpreadMapExpression_closure36.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitSpreadMapExpression_closure36.class) {
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
                return new CallSiteArray(_visitSpreadMapExpression_closure36.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitSpreadMapExpression_closure36.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[45].callCurrent(this, node, SpreadMapExpression.class, new _visitSpreadMapExpression_closure36(this, this));
    }

    @Override
    public void visitMethodPointerExpression(MethodPointerExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitMethodPointerExpression_closure37
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitMethodPointerExpression_closure37(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitMethodPointerExpression_closure37.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitMethodPointerExpression_closure37.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitMethodPointerExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitMethodPointerExpression_closure37.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitMethodPointerExpression_closure37.class) {
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
                return new CallSiteArray(_visitMethodPointerExpression_closure37.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitMethodPointerExpression_closure37.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[46].callCurrent(this, node, MethodPointerExpression.class, new _visitMethodPointerExpression_closure37(this, this));
    }

    @Override
    public void visitUnaryMinusExpression(UnaryMinusExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitUnaryMinusExpression_closure38
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitUnaryMinusExpression_closure38(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitUnaryMinusExpression_closure38.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitUnaryMinusExpression_closure38.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitUnaryMinusExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitUnaryMinusExpression_closure38.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitUnaryMinusExpression_closure38.class) {
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
                return new CallSiteArray(_visitUnaryMinusExpression_closure38.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitUnaryMinusExpression_closure38.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[47].callCurrent(this, node, UnaryMinusExpression.class, new _visitUnaryMinusExpression_closure38(this, this));
    }

    @Override
    public void visitUnaryPlusExpression(UnaryPlusExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitUnaryPlusExpression_closure39
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitUnaryPlusExpression_closure39(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitUnaryPlusExpression_closure39.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitUnaryPlusExpression_closure39.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitUnaryPlusExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitUnaryPlusExpression_closure39.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitUnaryPlusExpression_closure39.class) {
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
                return new CallSiteArray(_visitUnaryPlusExpression_closure39.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitUnaryPlusExpression_closure39.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[48].callCurrent(this, node, UnaryPlusExpression.class, new _visitUnaryPlusExpression_closure39(this, this));
    }

    @Override
    public void visitBitwiseNegationExpression(BitwiseNegationExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitBitwiseNegationExpression_closure40
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitBitwiseNegationExpression_closure40(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitBitwiseNegationExpression_closure40.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitBitwiseNegationExpression_closure40.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitBitwiseNegationExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitBitwiseNegationExpression_closure40.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBitwiseNegationExpression_closure40.class) {
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
                return new CallSiteArray(_visitBitwiseNegationExpression_closure40.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitBitwiseNegationExpression_closure40.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[49].callCurrent(this, node, BitwiseNegationExpression.class, new _visitBitwiseNegationExpression_closure40(this, this));
    }

    @Override
    public void visitCastExpression(CastExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitCastExpression_closure41
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitCastExpression_closure41(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitCastExpression_closure41.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitCastExpression_closure41.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitCastExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitCastExpression_closure41.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitCastExpression_closure41.class) {
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
                return new CallSiteArray(_visitCastExpression_closure41.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitCastExpression_closure41.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[50].callCurrent(this, node, CastExpression.class, new _visitCastExpression_closure41(this, this));
    }

    @Override
    public void visitConstantExpression(ConstantExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitConstantExpression_closure42
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitConstantExpression_closure42(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitConstantExpression_closure42.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitConstantExpression_closure42.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitConstantExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitConstantExpression_closure42.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitConstantExpression_closure42.class) {
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
                return new CallSiteArray(_visitConstantExpression_closure42.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitConstantExpression_closure42.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[51].callCurrent(this, node, ConstantExpression.class, new _visitConstantExpression_closure42(this, this));
    }

    @Override
    public void visitClassExpression(ClassExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitClassExpression_closure43
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClassExpression_closure43(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitClassExpression_closure43.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClassExpression_closure43.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitClassExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClassExpression_closure43.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClassExpression_closure43.class) {
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
                return new CallSiteArray(_visitClassExpression_closure43.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClassExpression_closure43.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[52].callCurrent(this, node, ClassExpression.class, new _visitClassExpression_closure43(this, this));
    }

    @Override
    public void visitVariableExpression(VariableExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitVariableExpression_closure44
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitVariableExpression_closure44(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitVariableExpression_closure44.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(VariableExpression it) {
                CallSite[] callSiteArray = _visitVariableExpression_closure44.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callGetProperty(it))) {
                    if (callSiteArray[1].callGetProperty(it) instanceof Parameter) {
                        return callSiteArray[2].callCurrent((GroovyObject)this, ScriptBytecodeAdapter.createPojoWrapper((Parameter)ScriptBytecodeAdapter.castToType(callSiteArray[3].callGetProperty(it), Parameter.class), Parameter.class));
                    }
                    if (callSiteArray[4].callGetProperty(it) instanceof DynamicVariable) {
                        public class _closure56
                        extends Closure
                        implements GeneratedClosure {
                            private static /* synthetic */ ClassInfo $staticClassInfo;
                            public static transient /* synthetic */ boolean __$stMC;
                            private static /* synthetic */ SoftReference $callSiteArray;

                            public _closure56(Object _outerInstance, Object _thisObject) {
                                CallSite[] callSiteArray = _closure56.$getCallSiteArray();
                                super(_outerInstance, _thisObject);
                            }

                            public Object doCall(Object it) {
                                CallSite[] callSiteArray = _closure56.$getCallSiteArray();
                                return callSiteArray[0].callSafe(callSiteArray[1].callGetProperty(it), this.getThisObject());
                            }

                            public Object doCall() {
                                CallSite[] callSiteArray = _closure56.$getCallSiteArray();
                                return this.doCall(null);
                            }

                            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                                if (this.getClass() != _closure56.class) {
                                    return ScriptBytecodeAdapter.initMetaClass(this);
                                }
                                ClassInfo classInfo = $staticClassInfo;
                                if (classInfo == null) {
                                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                                }
                                return classInfo.getMetaClass();
                            }

                            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                                stringArray[0] = "visit";
                                stringArray[1] = "initialExpression";
                            }

                            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                                String[] stringArray = new String[2];
                                _closure56.$createCallSiteArray_1(stringArray);
                                return new CallSiteArray(_closure56.class, stringArray);
                            }

                            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                                CallSiteArray callSiteArray;
                                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                    callSiteArray = _closure56.$createCallSiteArray();
                                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                                }
                                return callSiteArray.array;
                            }
                        }
                        return callSiteArray[5].callCurrent(this, callSiteArray[6].callGetProperty(it), DynamicVariable.class, new _closure56(this, this.getThisObject()));
                    }
                    return null;
                }
                return null;
            }

            public Object call(VariableExpression it) {
                CallSite[] callSiteArray = _visitVariableExpression_closure44.$getCallSiteArray();
                return callSiteArray[7].callCurrent((GroovyObject)this, it);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitVariableExpression_closure44.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "accessedVariable";
                stringArray[1] = "accessedVariable";
                stringArray[2] = "visitParameter";
                stringArray[3] = "accessedVariable";
                stringArray[4] = "accessedVariable";
                stringArray[5] = "addNode";
                stringArray[6] = "accessedVariable";
                stringArray[7] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[8];
                _visitVariableExpression_closure44.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitVariableExpression_closure44.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitVariableExpression_closure44.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[53].callCurrent(this, node, VariableExpression.class, new _visitVariableExpression_closure44(this, this));
    }

    @Override
    public void visitDeclarationExpression(DeclarationExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitDeclarationExpression_closure45
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitDeclarationExpression_closure45(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitDeclarationExpression_closure45.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitDeclarationExpression_closure45.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitDeclarationExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitDeclarationExpression_closure45.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitDeclarationExpression_closure45.class) {
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
                return new CallSiteArray(_visitDeclarationExpression_closure45.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitDeclarationExpression_closure45.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[54].callCurrent(this, node, DeclarationExpression.class, new _visitDeclarationExpression_closure45(this, this));
    }

    @Override
    public void visitPropertyExpression(PropertyExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitPropertyExpression_closure46
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitPropertyExpression_closure46(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitPropertyExpression_closure46.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitPropertyExpression_closure46.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitPropertyExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitPropertyExpression_closure46.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitPropertyExpression_closure46.class) {
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
                return new CallSiteArray(_visitPropertyExpression_closure46.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitPropertyExpression_closure46.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[55].callCurrent(this, node, PropertyExpression.class, new _visitPropertyExpression_closure46(this, this));
    }

    @Override
    public void visitAttributeExpression(AttributeExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitAttributeExpression_closure47
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitAttributeExpression_closure47(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitAttributeExpression_closure47.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitAttributeExpression_closure47.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitAttributeExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitAttributeExpression_closure47.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitAttributeExpression_closure47.class) {
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
                return new CallSiteArray(_visitAttributeExpression_closure47.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitAttributeExpression_closure47.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[56].callCurrent(this, node, AttributeExpression.class, new _visitAttributeExpression_closure47(this, this));
    }

    @Override
    public void visitFieldExpression(FieldExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitFieldExpression_closure48
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitFieldExpression_closure48(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitFieldExpression_closure48.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitFieldExpression_closure48.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitFieldExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitFieldExpression_closure48.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitFieldExpression_closure48.class) {
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
                return new CallSiteArray(_visitFieldExpression_closure48.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitFieldExpression_closure48.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[57].callCurrent(this, node, FieldExpression.class, new _visitFieldExpression_closure48(this, this));
    }

    @Override
    public void visitGStringExpression(GStringExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitGStringExpression_closure49
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitGStringExpression_closure49(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitGStringExpression_closure49.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitGStringExpression_closure49.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitGStringExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitGStringExpression_closure49.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitGStringExpression_closure49.class) {
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
                return new CallSiteArray(_visitGStringExpression_closure49.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitGStringExpression_closure49.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[58].callCurrent(this, node, GStringExpression.class, new _visitGStringExpression_closure49(this, this));
    }

    @Override
    public void visitCatchStatement(CatchStatement node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitCatchStatement_closure50
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitCatchStatement_closure50(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitCatchStatement_closure50.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitCatchStatement_closure50.$getCallSiteArray();
                if (DefaultTypeTransformation.booleanUnbox(callSiteArray[0].callGetProperty(it))) {
                    callSiteArray[1].callCurrent((GroovyObject)this, callSiteArray[2].callGetProperty(it));
                }
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitCatchStatement", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitCatchStatement_closure50.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitCatchStatement_closure50.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "variable";
                stringArray[1] = "visitParameter";
                stringArray[2] = "variable";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _visitCatchStatement_closure50.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitCatchStatement_closure50.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitCatchStatement_closure50.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[59].callCurrent(this, node, CatchStatement.class, new _visitCatchStatement_closure50(this, this));
    }

    @Override
    public void visitArgumentlistExpression(ArgumentListExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitArgumentlistExpression_closure51
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitArgumentlistExpression_closure51(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure51.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure51.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitArgumentlistExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitArgumentlistExpression_closure51.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitArgumentlistExpression_closure51.class) {
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
                return new CallSiteArray(_visitArgumentlistExpression_closure51.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitArgumentlistExpression_closure51.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[60].callCurrent(this, node, ArgumentListExpression.class, new _visitArgumentlistExpression_closure51(this, this));
    }

    @Override
    public void visitClosureListExpression(ClosureListExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitClosureListExpression_closure52
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitClosureListExpression_closure52(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitClosureListExpression_closure52.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitClosureListExpression_closure52.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitClosureListExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitClosureListExpression_closure52.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitClosureListExpression_closure52.class) {
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
                return new CallSiteArray(_visitClosureListExpression_closure52.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitClosureListExpression_closure52.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[61].callCurrent(this, node, ClosureListExpression.class, new _visitClosureListExpression_closure52(this, this));
    }

    @Override
    public void visitBytecodeExpression(BytecodeExpression node) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitBytecodeExpression_closure53
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitBytecodeExpression_closure53(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitBytecodeExpression_closure53.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Object it) {
                CallSite[] callSiteArray = _visitBytecodeExpression_closure53.$getCallSiteArray();
                return ScriptBytecodeAdapter.invokeMethodOnSuperN(CodeVisitorSupport.class, (GroovyObject)this.getThisObject(), "visitBytecodeExpression", new Object[]{it});
            }

            public Object doCall() {
                CallSite[] callSiteArray = _visitBytecodeExpression_closure53.$getCallSiteArray();
                return this.doCall(null);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitBytecodeExpression_closure53.class) {
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
                return new CallSiteArray(_visitBytecodeExpression_closure53.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitBytecodeExpression_closure53.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[62].callCurrent(this, node, BytecodeExpression.class, new _visitBytecodeExpression_closure53(this, this));
    }

    @Override
    protected void visitListOfExpressions(List<? extends Expression> list) {
        CallSite[] callSiteArray = TreeNodeBuildingVisitor.$getCallSiteArray();
        public class _visitListOfExpressions_closure54
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;
            private static /* synthetic */ SoftReference $callSiteArray;

            public _visitListOfExpressions_closure54(Object _outerInstance, Object _thisObject) {
                CallSite[] callSiteArray = _visitListOfExpressions_closure54.$getCallSiteArray();
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Expression node) {
                CallSite[] callSiteArray = _visitListOfExpressions_closure54.$getCallSiteArray();
                if (node instanceof NamedArgumentListExpression) {
                    public class _closure57
                    extends Closure
                    implements GeneratedClosure {
                        private static /* synthetic */ ClassInfo $staticClassInfo;
                        public static transient /* synthetic */ boolean __$stMC;
                        private static /* synthetic */ SoftReference $callSiteArray;

                        public _closure57(Object _outerInstance, Object _thisObject) {
                            CallSite[] callSiteArray = _closure57.$getCallSiteArray();
                            super(_outerInstance, _thisObject);
                        }

                        public Object doCall(Object it) {
                            CallSite[] callSiteArray = _closure57.$getCallSiteArray();
                            return callSiteArray[0].call(it, this.getThisObject());
                        }

                        public Object doCall() {
                            CallSite[] callSiteArray = _closure57.$getCallSiteArray();
                            return this.doCall(null);
                        }

                        protected /* synthetic */ MetaClass $getStaticMetaClass() {
                            if (this.getClass() != _closure57.class) {
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
                            stringArray[0] = "visit";
                            return new CallSiteArray(_closure57.class, stringArray);
                        }

                        private static /* synthetic */ CallSite[] $getCallSiteArray() {
                            CallSiteArray callSiteArray;
                            if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                                callSiteArray = _closure57.$createCallSiteArray();
                                $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                            }
                            return callSiteArray.array;
                        }
                    }
                    return callSiteArray[0].callCurrent(this, node, NamedArgumentListExpression.class, new _closure57(this, this.getThisObject()));
                }
                return callSiteArray[1].call((Object)node, this.getThisObject());
            }

            public Object call(Expression node) {
                CallSite[] callSiteArray = _visitListOfExpressions_closure54.$getCallSiteArray();
                return callSiteArray[2].callCurrent((GroovyObject)this, node);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _visitListOfExpressions_closure54.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }

            private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
                stringArray[0] = "addNode";
                stringArray[1] = "visit";
                stringArray[2] = "doCall";
            }

            private static /* synthetic */ CallSiteArray $createCallSiteArray() {
                String[] stringArray = new String[3];
                _visitListOfExpressions_closure54.$createCallSiteArray_1(stringArray);
                return new CallSiteArray(_visitListOfExpressions_closure54.class, stringArray);
            }

            private static /* synthetic */ CallSite[] $getCallSiteArray() {
                CallSiteArray callSiteArray;
                if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
                    callSiteArray = _visitListOfExpressions_closure54.$createCallSiteArray();
                    $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
                }
                return callSiteArray.array;
            }
        }
        callSiteArray[63].call(list, new _visitListOfExpressions_closure54(this, this));
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TreeNodeBuildingVisitor.class) {
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

    public Object getCurrentNode() {
        return this.currentNode;
    }

    public void setCurrentNode(Object object) {
        this.currentNode = object;
    }

    public /* synthetic */ void super$2$visitTernaryExpression(TernaryExpression ternaryExpression) {
        super.visitTernaryExpression(ternaryExpression);
    }

    public /* synthetic */ void super$2$visitAttributeExpression(AttributeExpression attributeExpression) {
        super.visitAttributeExpression(attributeExpression);
    }

    public /* synthetic */ void super$2$visitMapEntryExpression(MapEntryExpression mapEntryExpression) {
        super.visitMapEntryExpression(mapEntryExpression);
    }

    public /* synthetic */ void super$2$visitMethodPointerExpression(MethodPointerExpression methodPointerExpression) {
        super.visitMethodPointerExpression(methodPointerExpression);
    }

    public /* synthetic */ void super$2$visitExpressionStatement(ExpressionStatement expressionStatement) {
        super.visitExpressionStatement(expressionStatement);
    }

    public /* synthetic */ void super$2$visitDeclarationExpression(DeclarationExpression declarationExpression) {
        super.visitDeclarationExpression(declarationExpression);
    }

    public /* synthetic */ void super$2$visitUnaryPlusExpression(UnaryPlusExpression unaryPlusExpression) {
        super.visitUnaryPlusExpression(unaryPlusExpression);
    }

    public /* synthetic */ void super$2$visitBitwiseNegationExpression(BitwiseNegationExpression bitwiseNegationExpression) {
        super.visitBitwiseNegationExpression(bitwiseNegationExpression);
    }

    public /* synthetic */ void super$2$visitShortTernaryExpression(ElvisOperatorExpression elvisOperatorExpression) {
        super.visitShortTernaryExpression(elvisOperatorExpression);
    }

    public /* synthetic */ void super$2$visitCastExpression(CastExpression castExpression) {
        super.visitCastExpression(castExpression);
    }

    public /* synthetic */ void super$2$visitPrefixExpression(PrefixExpression prefixExpression) {
        super.visitPrefixExpression(prefixExpression);
    }

    public /* synthetic */ void super$2$visitNotExpression(NotExpression notExpression) {
        super.visitNotExpression(notExpression);
    }

    public /* synthetic */ void super$2$visitArrayExpression(ArrayExpression arrayExpression) {
        super.visitArrayExpression(arrayExpression);
    }

    public /* synthetic */ void super$2$visitThrowStatement(ThrowStatement throwStatement) {
        super.visitThrowStatement(throwStatement);
    }

    public /* synthetic */ void super$2$visitRangeExpression(RangeExpression rangeExpression) {
        super.visitRangeExpression(rangeExpression);
    }

    public /* synthetic */ void super$2$visitSpreadExpression(SpreadExpression spreadExpression) {
        super.visitSpreadExpression(spreadExpression);
    }

    public /* synthetic */ void super$2$visitBreakStatement(BreakStatement breakStatement) {
        super.visitBreakStatement(breakStatement);
    }

    public /* synthetic */ void super$2$visitSpreadMapExpression(SpreadMapExpression spreadMapExpression) {
        super.visitSpreadMapExpression(spreadMapExpression);
    }

    public /* synthetic */ void super$2$visitAssertStatement(AssertStatement assertStatement) {
        super.visitAssertStatement(assertStatement);
    }

    public /* synthetic */ void super$2$visitConstructorCallExpression(ConstructorCallExpression constructorCallExpression) {
        super.visitConstructorCallExpression(constructorCallExpression);
    }

    public /* synthetic */ void super$2$visitMapExpression(MapExpression mapExpression) {
        super.visitMapExpression(mapExpression);
    }

    public /* synthetic */ void super$2$visitArgumentlistExpression(ArgumentListExpression argumentListExpression) {
        super.visitArgumentlistExpression(argumentListExpression);
    }

    public /* synthetic */ void super$2$visitTupleExpression(TupleExpression tupleExpression) {
        super.visitTupleExpression(tupleExpression);
    }

    public /* synthetic */ void super$2$visitTryCatchFinally(TryCatchStatement tryCatchStatement) {
        super.visitTryCatchFinally(tryCatchStatement);
    }

    public /* synthetic */ void super$2$visitBooleanExpression(BooleanExpression booleanExpression) {
        super.visitBooleanExpression(booleanExpression);
    }

    public /* synthetic */ void super$2$visitVariableExpression(VariableExpression variableExpression) {
        super.visitVariableExpression(variableExpression);
    }

    public /* synthetic */ void super$2$visitSynchronizedStatement(SynchronizedStatement synchronizedStatement) {
        super.visitSynchronizedStatement(synchronizedStatement);
    }

    public /* synthetic */ void super$2$visitGStringExpression(GStringExpression gStringExpression) {
        super.visitGStringExpression(gStringExpression);
    }

    public /* synthetic */ void super$2$visitCatchStatement(CatchStatement catchStatement) {
        super.visitCatchStatement(catchStatement);
    }

    public /* synthetic */ void super$2$visitContinueStatement(ContinueStatement continueStatement) {
        super.visitContinueStatement(continueStatement);
    }

    public /* synthetic */ void super$2$visitMethodCallExpression(MethodCallExpression methodCallExpression) {
        super.visitMethodCallExpression(methodCallExpression);
    }

    public /* synthetic */ void super$2$visitCaseStatement(CaseStatement caseStatement) {
        super.visitCaseStatement(caseStatement);
    }

    public /* synthetic */ void super$2$visitClosureExpression(ClosureExpression closureExpression) {
        super.visitClosureExpression(closureExpression);
    }

    public /* synthetic */ void super$2$visitBlockStatement(BlockStatement blockStatement) {
        super.visitBlockStatement(blockStatement);
    }

    public /* synthetic */ void super$2$visitFieldExpression(FieldExpression fieldExpression) {
        super.visitFieldExpression(fieldExpression);
    }

    public /* synthetic */ void super$2$visitUnaryMinusExpression(UnaryMinusExpression unaryMinusExpression) {
        super.visitUnaryMinusExpression(unaryMinusExpression);
    }

    public /* synthetic */ void super$2$visitStaticMethodCallExpression(StaticMethodCallExpression staticMethodCallExpression) {
        super.visitStaticMethodCallExpression(staticMethodCallExpression);
    }

    public /* synthetic */ void super$2$visitBinaryExpression(BinaryExpression binaryExpression) {
        super.visitBinaryExpression(binaryExpression);
    }

    public /* synthetic */ void super$2$visitPostfixExpression(PostfixExpression postfixExpression) {
        super.visitPostfixExpression(postfixExpression);
    }

    public /* synthetic */ void super$2$visitBytecodeExpression(BytecodeExpression bytecodeExpression) {
        super.visitBytecodeExpression(bytecodeExpression);
    }

    public /* synthetic */ void super$2$visitClosureListExpression(ClosureListExpression closureListExpression) {
        super.visitClosureListExpression(closureListExpression);
    }

    public /* synthetic */ void super$2$visitConstantExpression(ConstantExpression constantExpression) {
        super.visitConstantExpression(constantExpression);
    }

    public /* synthetic */ void super$2$visitPropertyExpression(PropertyExpression propertyExpression) {
        super.visitPropertyExpression(propertyExpression);
    }

    public /* synthetic */ void super$2$visitReturnStatement(ReturnStatement returnStatement) {
        super.visitReturnStatement(returnStatement);
    }

    public /* synthetic */ void super$2$visitListExpression(ListExpression listExpression) {
        super.visitListExpression(listExpression);
    }

    public /* synthetic */ void super$2$visitClassExpression(ClassExpression classExpression) {
        super.visitClassExpression(classExpression);
    }

    public /* synthetic */ void super$2$visitSwitch(SwitchStatement switchStatement) {
        super.visitSwitch(switchStatement);
    }

    public /* synthetic */ void super$2$visitIfElse(IfStatement ifStatement) {
        super.visitIfElse(ifStatement);
    }

    public /* synthetic */ void super$2$visitForLoop(ForStatement forStatement) {
        super.visitForLoop(forStatement);
    }

    public /* synthetic */ void super$2$visitWhileLoop(WhileStatement whileStatement) {
        super.visitWhileLoop(whileStatement);
    }

    public /* synthetic */ void super$2$visitDoWhileLoop(DoWhileStatement doWhileStatement) {
        super.visitDoWhileLoop(doWhileStatement);
    }

    public /* synthetic */ void super$2$visitEmptyStatement(EmptyStatement emptyStatement) {
        super.visitEmptyStatement(emptyStatement);
    }

    public /* synthetic */ void super$2$visitListOfExpressions(List list) {
        super.visitListOfExpressions(list);
    }

    private static /* synthetic */ void $createCallSiteArray_1(String[] stringArray) {
        stringArray[0] = "<$constructor$>";
        stringArray[1] = "getName";
        stringArray[2] = "getName";
        stringArray[3] = "getClass";
        stringArray[4] = "make";
        stringArray[5] = "call";
        stringArray[6] = "make";
        stringArray[7] = "add";
        stringArray[8] = "call";
        stringArray[9] = "call";
        stringArray[10] = "addNode";
        stringArray[11] = "addNode";
        stringArray[12] = "addNode";
        stringArray[13] = "addNode";
        stringArray[14] = "addNode";
        stringArray[15] = "addNode";
        stringArray[16] = "addNode";
        stringArray[17] = "addNode";
        stringArray[18] = "addNode";
        stringArray[19] = "addNode";
        stringArray[20] = "addNode";
        stringArray[21] = "addNode";
        stringArray[22] = "addNode";
        stringArray[23] = "addNode";
        stringArray[24] = "addNode";
        stringArray[25] = "addNode";
        stringArray[26] = "addNode";
        stringArray[27] = "addNode";
        stringArray[28] = "addNode";
        stringArray[29] = "addNode";
        stringArray[30] = "addNode";
        stringArray[31] = "addNode";
        stringArray[32] = "addNode";
        stringArray[33] = "addNode";
        stringArray[34] = "addNode";
        stringArray[35] = "addNode";
        stringArray[36] = "addNode";
        stringArray[37] = "addNode";
        stringArray[38] = "addNode";
        stringArray[39] = "addNode";
        stringArray[40] = "addNode";
        stringArray[41] = "addNode";
        stringArray[42] = "addNode";
        stringArray[43] = "addNode";
        stringArray[44] = "addNode";
        stringArray[45] = "addNode";
        stringArray[46] = "addNode";
        stringArray[47] = "addNode";
        stringArray[48] = "addNode";
        stringArray[49] = "addNode";
        stringArray[50] = "addNode";
        stringArray[51] = "addNode";
        stringArray[52] = "addNode";
        stringArray[53] = "addNode";
        stringArray[54] = "addNode";
        stringArray[55] = "addNode";
        stringArray[56] = "addNode";
        stringArray[57] = "addNode";
        stringArray[58] = "addNode";
        stringArray[59] = "addNode";
        stringArray[60] = "addNode";
        stringArray[61] = "addNode";
        stringArray[62] = "addNode";
        stringArray[63] = "each";
    }

    private static /* synthetic */ CallSiteArray $createCallSiteArray() {
        String[] stringArray = new String[64];
        TreeNodeBuildingVisitor.$createCallSiteArray_1(stringArray);
        return new CallSiteArray(TreeNodeBuildingVisitor.class, stringArray);
    }

    private static /* synthetic */ CallSite[] $getCallSiteArray() {
        CallSiteArray callSiteArray;
        if ($callSiteArray == null || (callSiteArray = (CallSiteArray)$callSiteArray.get()) == null) {
            callSiteArray = TreeNodeBuildingVisitor.$createCallSiteArray();
            $callSiteArray = new SoftReference<CallSiteArray>(callSiteArray);
        }
        return callSiteArray.array;
    }
}


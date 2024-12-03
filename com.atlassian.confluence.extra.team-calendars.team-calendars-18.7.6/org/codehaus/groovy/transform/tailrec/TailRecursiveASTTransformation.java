/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import groovy.transform.Memoized;
import groovy.transform.TailRecursive;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TernaryExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.classgen.ReturnAdder;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.BytecodeInterface8;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;
import org.codehaus.groovy.transform.tailrec.AstHelper;
import org.codehaus.groovy.transform.tailrec.CollectRecursiveCalls;
import org.codehaus.groovy.transform.tailrec.HasRecursiveCalls;
import org.codehaus.groovy.transform.tailrec.InWhileLoopWrapper;
import org.codehaus.groovy.transform.tailrec.RecursivenessTester;
import org.codehaus.groovy.transform.tailrec.ReturnAdderForClosures;
import org.codehaus.groovy.transform.tailrec.ReturnStatementToIterationConverter;
import org.codehaus.groovy.transform.tailrec.StatementReplacer;
import org.codehaus.groovy.transform.tailrec.TernaryToIfStatementConverter;
import org.codehaus.groovy.transform.tailrec.VariableAccessReplacer;

@GroovyASTTransformation(phase=CompilePhase.SEMANTIC_ANALYSIS)
public class TailRecursiveASTTransformation
extends AbstractASTTransformation
implements GroovyObject {
    private static final Class MY_CLASS;
    private static final ClassNode MY_TYPE;
    private static final String MY_TYPE_NAME;
    private HasRecursiveCalls hasRecursiveCalls;
    private TernaryToIfStatementConverter ternaryToIfStatement;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public TailRecursiveASTTransformation() {
        MetaClass metaClass;
        TernaryToIfStatementConverter ternaryToIfStatementConverter;
        HasRecursiveCalls hasRecursiveCalls;
        this.hasRecursiveCalls = hasRecursiveCalls = new HasRecursiveCalls();
        this.ternaryToIfStatement = ternaryToIfStatementConverter = new TernaryToIfStatementConverter();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        MethodNode method = (MethodNode)ScriptBytecodeAdapter.asType(BytecodeInterface8.objectArrayGet(nodes, 1), MethodNode.class);
        if (method.isAbstract()) {
            this.addError(StringGroovyMethods.plus(StringGroovyMethods.plus("Annotation ", (CharSequence)MY_TYPE_NAME), (CharSequence)" cannot be used for abstract methods."), method);
            return;
        }
        if (this.hasAnnotation(method, ClassHelper.make(Memoized.class))) {
            ClassNode memoizedClassNode = ClassHelper.make(Memoized.class);
            AnnotationNode annotationNode = null;
            Iterator<AnnotationNode> iterator = method.getAnnotations().iterator();
            while (iterator.hasNext() && !ScriptBytecodeAdapter.compareEqual((annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(iterator.next(), AnnotationNode.class)).getClassNode(), MY_TYPE)) {
                if (!ScriptBytecodeAdapter.compareEqual(annotationNode.getClassNode(), memoizedClassNode)) continue;
                this.addError(StringGroovyMethods.plus(StringGroovyMethods.plus("Annotation ", (CharSequence)MY_TYPE_NAME), (CharSequence)" must be placed before annotation @Memoized."), annotationNode);
                return;
            }
        }
        if (!this.hasRecursiveMethodCalls(method)) {
            AnnotationNode annotationNode = (AnnotationNode)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(method.getAnnotations(ClassHelper.make(TailRecursive.class)), 0), AnnotationNode.class);
            this.addError(StringGroovyMethods.plus(StringGroovyMethods.plus("No recursive calls detected. You must remove annotation ", (CharSequence)MY_TYPE_NAME), (CharSequence)"."), annotationNode);
            return;
        }
        this.transformToIteration(method, source);
        this.ensureAllRecursiveCallsHaveBeenTransformed(method);
    }

    private boolean hasAnnotation(MethodNode methodNode, ClassNode annotation) {
        List<AnnotationNode> annots = methodNode.getAnnotations(annotation);
        return annots != null && annots.size() > 0;
    }

    private void transformToIteration(MethodNode method, SourceUnit source) {
        if (method.isVoidMethod()) {
            this.transformVoidMethodToIteration(method, source);
        } else {
            this.transformNonVoidMethodToIteration(method, source);
        }
    }

    private void transformVoidMethodToIteration(MethodNode method, SourceUnit source) {
        this.addError("Void methods are not supported by @TailRecursive yet.", method);
    }

    private void transformNonVoidMethodToIteration(MethodNode method, SourceUnit source) {
        this.addMissingDefaultReturnStatement(method);
        this.replaceReturnsWithTernariesToIfStatements(method);
        this.wrapMethodBodyWithWhileLoop(method);
        Map<String, Map> nameAndTypeMapping = this.name2VariableMappingFor(method);
        this.replaceAllAccessToParams(method, nameAndTypeMapping);
        this.addLocalVariablesForAllParameters(method, nameAndTypeMapping);
        Map<Integer, Map> positionMapping = this.position2VariableMappingFor(method);
        this.replaceAllRecursiveReturnsWithIteration(method, positionMapping);
        this.repairVariableScopes(source, method);
    }

    private void repairVariableScopes(SourceUnit source, MethodNode method) {
        new VariableScopeVisitor(source).visitClass(method.getDeclaringClass());
    }

    private void replaceReturnsWithTernariesToIfStatements(MethodNode method) {
        public class _replaceReturnsWithTernariesToIfStatements_closure1
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceReturnsWithTernariesToIfStatements_closure1(Object _outerInstance, Object _thisObject) {
                super(_outerInstance, _thisObject);
            }

            public Object doCall(ASTNode node) {
                if (!(node instanceof ReturnStatement)) {
                    return false;
                }
                return ((ReturnStatement)ScriptBytecodeAdapter.castToType(node, ReturnStatement.class)).getExpression() instanceof TernaryExpression;
            }

            public Object call(ASTNode node) {
                return this.doCall(node);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceReturnsWithTernariesToIfStatements_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceReturnsWithTernariesToIfStatements_closure1 whenReturnWithTernary = new _replaceReturnsWithTernariesToIfStatements_closure1(this, this);
        public class _replaceReturnsWithTernariesToIfStatements_closure2
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceReturnsWithTernariesToIfStatements_closure2(Object _outerInstance, Object _thisObject) {
                super(_outerInstance, _thisObject);
            }

            public Object doCall(ReturnStatement statement) {
                return ((TailRecursiveASTTransformation)this.getThisObject()).ternaryToIfStatement.convert(statement);
            }

            public Object call(ReturnStatement statement) {
                return this.doCall(statement);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceReturnsWithTernariesToIfStatements_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceReturnsWithTernariesToIfStatements_closure2 replaceWithIfStatement = new _replaceReturnsWithTernariesToIfStatements_closure2(this, this);
        StatementReplacer statementReplacer = new StatementReplacer();
        _replaceReturnsWithTernariesToIfStatements_closure1 _replaceReturnsWithTernariesToIfStatements_closure12 = whenReturnWithTernary;
        statementReplacer.setWhen(_replaceReturnsWithTernariesToIfStatements_closure12);
        _replaceReturnsWithTernariesToIfStatements_closure2 _replaceReturnsWithTernariesToIfStatements_closure22 = replaceWithIfStatement;
        statementReplacer.setReplaceWith(_replaceReturnsWithTernariesToIfStatements_closure22);
        StatementReplacer replacer = statementReplacer;
        replacer.replaceIn(method.getCode());
    }

    private void addLocalVariablesForAllParameters(MethodNode method, Map<String, Map> nameAndTypeMapping) {
        Reference<BlockStatement> code = new Reference<BlockStatement>((BlockStatement)ScriptBytecodeAdapter.asType(method.getCode(), BlockStatement.class));
        public class _addLocalVariablesForAllParameters_closure3
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference code;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _addLocalVariablesForAllParameters_closure3(Object _outerInstance, Object _thisObject, Reference code) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.code = reference = code;
            }

            public Object doCall(String paramName, Map localNameAndType) {
                ((BlockStatement)this.code.get()).getStatements().add(0, AstHelper.createVariableDefinition(ShortTypeHandling.castToString(DefaultGroovyMethods.getAt(localNameAndType, "name")), (ClassNode)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(localNameAndType, "type"), ClassNode.class), new VariableExpression(paramName, (ClassNode)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(localNameAndType, "type"), ClassNode.class))));
                return null;
            }

            public Object call(String paramName, Map localNameAndType) {
                return this.doCall(paramName, localNameAndType);
            }

            public BlockStatement getCode() {
                return (BlockStatement)ScriptBytecodeAdapter.castToType(this.code.get(), BlockStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _addLocalVariablesForAllParameters_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        DefaultGroovyMethods.each(nameAndTypeMapping, (Closure)new _addLocalVariablesForAllParameters_closure3(this, this, code));
    }

    private void replaceAllAccessToParams(MethodNode method, Map<String, Map> nameAndTypeMapping) {
        VariableAccessReplacer variableAccessReplacer = new VariableAccessReplacer();
        Map<String, Map> map = nameAndTypeMapping;
        variableAccessReplacer.setNameAndTypeMapping(map);
        variableAccessReplacer.replaceIn(method.getCode());
    }

    public Map<String, Map> name2VariableMappingFor(MethodNode method) {
        Reference<LinkedHashMap> nameAndTypeMapping = new Reference<LinkedHashMap>((LinkedHashMap)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createMap(new Object[0]), LinkedHashMap.class));
        public class _name2VariableMappingFor_closure4
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference nameAndTypeMapping;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _name2VariableMappingFor_closure4(Object _outerInstance, Object _thisObject, Reference nameAndTypeMapping) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.nameAndTypeMapping = reference = nameAndTypeMapping;
            }

            public Object doCall(Parameter param) {
                String paramName = param.getName();
                ClassNode paramType = param.getType();
                String iterationVariableName = ((TailRecursiveASTTransformation)this.getThisObject()).iterationVariableName(paramName);
                Map map = ScriptBytecodeAdapter.createMap(new Object[]{"name", iterationVariableName, "type", paramType});
                DefaultGroovyMethods.putAt((Map)ScriptBytecodeAdapter.castToType(this.nameAndTypeMapping.get(), Map.class), paramName, map);
                return map;
            }

            public Object call(Parameter param) {
                return this.doCall(param);
            }

            public Map getNameAndTypeMapping() {
                return (Map)ScriptBytecodeAdapter.castToType(this.nameAndTypeMapping.get(), Map.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _name2VariableMappingFor_closure4.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        DefaultGroovyMethods.each(method.getParameters(), (Closure)new _name2VariableMappingFor_closure4(this, this, nameAndTypeMapping));
        return nameAndTypeMapping.get();
    }

    public Map<Integer, Map> position2VariableMappingFor(MethodNode method) {
        Reference<LinkedHashMap> positionMapping = new Reference<LinkedHashMap>((LinkedHashMap)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createMap(new Object[0]), LinkedHashMap.class));
        public class _position2VariableMappingFor_closure5
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference positionMapping;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _position2VariableMappingFor_closure5(Object _outerInstance, Object _thisObject, Reference positionMapping) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.positionMapping = reference = positionMapping;
            }

            public Object doCall(Parameter param, int index) {
                String paramName = param.getName();
                ClassNode paramType = param.getType();
                String iterationVariableName = ((TailRecursiveASTTransformation)this.getThisObject()).iterationVariableName(paramName);
                Map map = ScriptBytecodeAdapter.createMap(new Object[]{"name", iterationVariableName, "type", paramType});
                DefaultGroovyMethods.putAt((Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class), Integer.valueOf(index), map);
                return map;
            }

            public Object call(Parameter param, int index) {
                return this.doCall(param, index);
            }

            public Map getPositionMapping() {
                return (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _position2VariableMappingFor_closure5.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        DefaultGroovyMethods.eachWithIndex(method.getParameters(), (Closure)new _position2VariableMappingFor_closure5(this, this, positionMapping));
        return positionMapping.get();
    }

    private String iterationVariableName(String paramName) {
        return StringGroovyMethods.plus(StringGroovyMethods.plus("_", (CharSequence)paramName), (CharSequence)"_");
    }

    private void replaceAllRecursiveReturnsWithIteration(MethodNode method, Map positionMapping) {
        this.replaceRecursiveReturnsOutsideClosures(method, positionMapping);
        this.replaceRecursiveReturnsInsideClosures(method, positionMapping);
    }

    /*
     * WARNING - void declaration
     */
    private void replaceRecursiveReturnsOutsideClosures(MethodNode method, Map<Integer, Map> positionMapping) {
        void var2_2;
        Reference<MethodNode> method2 = new Reference<MethodNode>(method);
        Reference<void> positionMapping2 = new Reference<void>(var2_2);
        public class _replaceRecursiveReturnsOutsideClosures_closure6
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference method;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceRecursiveReturnsOutsideClosures_closure6(Object _outerInstance, Object _thisObject, Reference method) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.method = reference = method;
            }

            public Object doCall(Statement statement, boolean inClosure) {
                if (inClosure) {
                    return false;
                }
                if (!(statement instanceof ReturnStatement)) {
                    return false;
                }
                Expression inner = ((ReturnStatement)ScriptBytecodeAdapter.castToType(statement, ReturnStatement.class)).getExpression();
                if (!(inner instanceof MethodCallExpression) && !(inner instanceof StaticMethodCallExpression)) {
                    return false;
                }
                return ((TailRecursiveASTTransformation)this.getThisObject()).isRecursiveIn(inner, (MethodNode)ScriptBytecodeAdapter.castToType(this.method.get(), MethodNode.class));
            }

            public Object call(Statement statement, boolean inClosure) {
                return this.doCall(statement, inClosure);
            }

            public MethodNode getMethod() {
                return (MethodNode)ScriptBytecodeAdapter.castToType(this.method.get(), MethodNode.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceRecursiveReturnsOutsideClosures_closure6.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceRecursiveReturnsOutsideClosures_closure6 whenRecursiveReturn = new _replaceRecursiveReturnsOutsideClosures_closure6(this, this, method2);
        public class _replaceRecursiveReturnsOutsideClosures_closure7
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference positionMapping;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceRecursiveReturnsOutsideClosures_closure7(Object _outerInstance, Object _thisObject, Reference positionMapping) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.positionMapping = reference = positionMapping;
            }

            public Object doCall(ReturnStatement statement) {
                return new ReturnStatementToIterationConverter().convert(statement, (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class));
            }

            public Object call(ReturnStatement statement) {
                return this.doCall(statement);
            }

            public Map getPositionMapping() {
                return (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceRecursiveReturnsOutsideClosures_closure7.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceRecursiveReturnsOutsideClosures_closure7 replaceWithContinueBlock = new _replaceRecursiveReturnsOutsideClosures_closure7(this, this, positionMapping2);
        StatementReplacer statementReplacer = new StatementReplacer();
        _replaceRecursiveReturnsOutsideClosures_closure6 _replaceRecursiveReturnsOutsideClosures_closure62 = whenRecursiveReturn;
        statementReplacer.setWhen(_replaceRecursiveReturnsOutsideClosures_closure62);
        _replaceRecursiveReturnsOutsideClosures_closure7 _replaceRecursiveReturnsOutsideClosures_closure72 = replaceWithContinueBlock;
        statementReplacer.setReplaceWith(_replaceRecursiveReturnsOutsideClosures_closure72);
        StatementReplacer replacer = statementReplacer;
        replacer.replaceIn(method2.get().getCode());
    }

    /*
     * WARNING - void declaration
     */
    private void replaceRecursiveReturnsInsideClosures(MethodNode method, Map<Integer, Map> positionMapping) {
        void var2_2;
        Reference<MethodNode> method2 = new Reference<MethodNode>(method);
        Reference<void> positionMapping2 = new Reference<void>(var2_2);
        public class _replaceRecursiveReturnsInsideClosures_closure8
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference method;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceRecursiveReturnsInsideClosures_closure8(Object _outerInstance, Object _thisObject, Reference method) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.method = reference = method;
            }

            public Object doCall(Statement statement, boolean inClosure) {
                if (!inClosure) {
                    return false;
                }
                if (!(statement instanceof ReturnStatement)) {
                    return false;
                }
                Expression inner = ((ReturnStatement)ScriptBytecodeAdapter.castToType(statement, ReturnStatement.class)).getExpression();
                if (!(inner instanceof MethodCallExpression) && !(inner instanceof StaticMethodCallExpression)) {
                    return false;
                }
                return ((TailRecursiveASTTransformation)this.getThisObject()).isRecursiveIn(inner, (MethodNode)ScriptBytecodeAdapter.castToType(this.method.get(), MethodNode.class));
            }

            public Object call(Statement statement, boolean inClosure) {
                return this.doCall(statement, inClosure);
            }

            public MethodNode getMethod() {
                return (MethodNode)ScriptBytecodeAdapter.castToType(this.method.get(), MethodNode.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceRecursiveReturnsInsideClosures_closure8.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceRecursiveReturnsInsideClosures_closure8 whenRecursiveReturn = new _replaceRecursiveReturnsInsideClosures_closure8(this, this, method2);
        public class _replaceRecursiveReturnsInsideClosures_closure9
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference positionMapping;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceRecursiveReturnsInsideClosures_closure9(Object _outerInstance, Object _thisObject, Reference positionMapping) {
                super(_outerInstance, _thisObject);
                Reference reference;
                this.positionMapping = reference = positionMapping;
            }

            public Object doCall(ReturnStatement statement) {
                ReturnStatementToIterationConverter returnStatementToIterationConverter = new ReturnStatementToIterationConverter();
                Statement statement2 = AstHelper.recurByThrowStatement();
                returnStatementToIterationConverter.setRecurStatement(statement2);
                return returnStatementToIterationConverter.convert(statement, (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class));
            }

            public Object call(ReturnStatement statement) {
                return this.doCall(statement);
            }

            public Map getPositionMapping() {
                return (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceRecursiveReturnsInsideClosures_closure9.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        _replaceRecursiveReturnsInsideClosures_closure9 replaceWithThrowLoopException = new _replaceRecursiveReturnsInsideClosures_closure9(this, this, positionMapping2);
        StatementReplacer statementReplacer = new StatementReplacer();
        _replaceRecursiveReturnsInsideClosures_closure8 _replaceRecursiveReturnsInsideClosures_closure82 = whenRecursiveReturn;
        statementReplacer.setWhen(_replaceRecursiveReturnsInsideClosures_closure82);
        _replaceRecursiveReturnsInsideClosures_closure9 _replaceRecursiveReturnsInsideClosures_closure92 = replaceWithThrowLoopException;
        statementReplacer.setReplaceWith(_replaceRecursiveReturnsInsideClosures_closure92);
        StatementReplacer replacer = statementReplacer;
        replacer.replaceIn(method2.get().getCode());
    }

    private void wrapMethodBodyWithWhileLoop(MethodNode method) {
        new InWhileLoopWrapper().wrap(method);
    }

    private void addMissingDefaultReturnStatement(MethodNode method) {
        new ReturnAdder().visitMethod(method);
        new ReturnAdderForClosures().visitMethod(method);
    }

    private void ensureAllRecursiveCallsHaveBeenTransformed(MethodNode method) {
        List<Expression> remainingRecursiveCalls = new CollectRecursiveCalls().collect(method);
        Expression expression = null;
        Iterator<Expression> iterator = remainingRecursiveCalls.iterator();
        while (iterator.hasNext()) {
            expression = (Expression)ScriptBytecodeAdapter.castToType(iterator.next(), Expression.class);
            this.addError("Recursive call could not be transformed by @TailRecursive. Maybe it's not a tail call.", expression);
        }
    }

    private boolean hasRecursiveMethodCalls(MethodNode method) {
        return this.hasRecursiveCalls.test(method);
    }

    private boolean isRecursiveIn(Expression methodCall, MethodNode method) {
        if (methodCall instanceof MethodCallExpression) {
            return new RecursivenessTester().isRecursive(method, (MethodCallExpression)ScriptBytecodeAdapter.castToType(methodCall, MethodCallExpression.class));
        }
        if (methodCall instanceof StaticMethodCallExpression) {
            return new RecursivenessTester().isRecursive(method, (StaticMethodCallExpression)ScriptBytecodeAdapter.castToType(methodCall, StaticMethodCallExpression.class));
        }
        return DefaultTypeTransformation.booleanUnbox(null);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != TailRecursiveASTTransformation.class) {
            return ScriptBytecodeAdapter.initMetaClass(this);
        }
        ClassInfo classInfo = $staticClassInfo;
        if (classInfo == null) {
            $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
        }
        return classInfo.getMetaClass();
    }

    public static /* synthetic */ void access$0(TailRecursiveASTTransformation $that, ASTNode[] param0, SourceUnit param1) {
        $that.init(param0, param1);
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
        String string;
        ClassNode classNode;
        Class<TailRecursive> clazz;
        MY_CLASS = clazz = TailRecursive.class;
        MY_TYPE = classNode = new ClassNode(MY_CLASS);
        MY_TYPE_NAME = string = StringGroovyMethods.plus("@", (CharSequence)MY_TYPE.getNameWithoutPackage());
    }

    public static String getMY_TYPE_NAME() {
        return MY_TYPE_NAME;
    }
}


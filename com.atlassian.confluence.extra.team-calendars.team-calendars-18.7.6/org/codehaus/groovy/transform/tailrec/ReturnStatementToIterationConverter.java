/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform.tailrec;

import groovy.lang.Closure;
import groovy.lang.GroovyObject;
import groovy.lang.MetaClass;
import groovy.lang.Reference;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.ExpressionStatement;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.reflection.ClassInfo;
import org.codehaus.groovy.runtime.DefaultGroovyMethods;
import org.codehaus.groovy.runtime.GStringImpl;
import org.codehaus.groovy.runtime.GeneratedClosure;
import org.codehaus.groovy.runtime.ScriptBytecodeAdapter;
import org.codehaus.groovy.runtime.typehandling.DefaultTypeTransformation;
import org.codehaus.groovy.runtime.typehandling.ShortTypeHandling;
import org.codehaus.groovy.transform.tailrec.AstHelper;
import org.codehaus.groovy.transform.tailrec.UsedVariableTracker;
import org.codehaus.groovy.transform.tailrec.VariableAccessReplacer;

public class ReturnStatementToIterationConverter
implements GroovyObject {
    private Statement recurStatement;
    private static /* synthetic */ ClassInfo $staticClassInfo;
    public static transient /* synthetic */ boolean __$stMC;
    private transient /* synthetic */ MetaClass metaClass;
    private static /* synthetic */ ClassInfo $staticClassInfo$;

    public ReturnStatementToIterationConverter() {
        MetaClass metaClass;
        Statement statement;
        this.recurStatement = statement = AstHelper.recurStatement();
        this.metaClass = metaClass = this.$getStaticMetaClass();
    }

    public Statement convert(ReturnStatement statement, Map<Integer, Map> positionMapping) {
        Reference<Map<Integer, Map>> positionMapping2 = new Reference<Map<Integer, Map>>(positionMapping);
        Expression recursiveCall = statement.getExpression();
        if (!this.isAMethodCalls(recursiveCall)) {
            return statement;
        }
        Reference<LinkedHashMap> tempMapping = new Reference<LinkedHashMap>((LinkedHashMap)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createMap(new Object[0]), LinkedHashMap.class));
        Reference<LinkedHashMap> tempDeclarations = new Reference<LinkedHashMap>((LinkedHashMap)ScriptBytecodeAdapter.castToType(ScriptBytecodeAdapter.createMap(new Object[0]), LinkedHashMap.class));
        Reference<List> argAssignments = new Reference<List>(ScriptBytecodeAdapter.createList(new Object[0]));
        Reference<BlockStatement> result = new Reference<BlockStatement>(new BlockStatement());
        String string = statement.getStatementLabel();
        result.get().setStatementLabel(string);
        public class _convert_closure1
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference positionMapping;
            private /* synthetic */ Reference tempMapping;
            private /* synthetic */ Reference tempDeclarations;
            private /* synthetic */ Reference result;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _convert_closure1(Object _outerInstance, Object _thisObject, Reference positionMapping, Reference tempMapping, Reference tempDeclarations, Reference result) {
                super(_outerInstance, _thisObject);
                Reference reference;
                Reference reference2;
                Reference reference3;
                Reference reference4;
                this.positionMapping = reference4 = positionMapping;
                this.tempMapping = reference3 = tempMapping;
                this.tempDeclarations = reference2 = tempDeclarations;
                this.result = reference = result;
            }

            public Object doCall(Expression expression, int index) {
                ExpressionStatement tempDeclaration = ((ReturnStatementToIterationConverter)this.getThisObject()).createTempDeclaration(index, (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class), (Map)ScriptBytecodeAdapter.castToType(this.tempMapping.get(), Map.class), (Map)ScriptBytecodeAdapter.castToType(this.tempDeclarations.get(), Map.class));
                ((BlockStatement)this.result.get()).addStatement(tempDeclaration);
                return null;
            }

            public Object call(Expression expression, int index) {
                return this.doCall(expression, index);
            }

            public Map getPositionMapping() {
                return (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class);
            }

            public Map getTempMapping() {
                return (Map)ScriptBytecodeAdapter.castToType(this.tempMapping.get(), Map.class);
            }

            public Map getTempDeclarations() {
                return (Map)ScriptBytecodeAdapter.castToType(this.tempDeclarations.get(), Map.class);
            }

            public BlockStatement getResult() {
                return (BlockStatement)ScriptBytecodeAdapter.castToType(this.result.get(), BlockStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _convert_closure1.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        DefaultGroovyMethods.eachWithIndex(this.getArguments(recursiveCall), (Closure)new _convert_closure1(this, this, positionMapping2, tempMapping, tempDeclarations, result));
        public class _convert_closure2
        extends Closure
        implements GeneratedClosure {
            private /* synthetic */ Reference positionMapping;
            private /* synthetic */ Reference argAssignments;
            private /* synthetic */ Reference result;
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _convert_closure2(Object _outerInstance, Object _thisObject, Reference positionMapping, Reference argAssignments, Reference result) {
                super(_outerInstance, _thisObject);
                Reference reference;
                Reference reference2;
                Reference reference3;
                this.positionMapping = reference3 = positionMapping;
                this.argAssignments = reference2 = argAssignments;
                this.result = reference = result;
            }

            public Object doCall(Expression expression, int index) {
                ExpressionStatement argAssignment = ((ReturnStatementToIterationConverter)this.getThisObject()).createAssignmentToIterationVariable(expression, index, (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class));
                ((List)this.argAssignments.get()).add(argAssignment);
                ((BlockStatement)this.result.get()).addStatement(argAssignment);
                return null;
            }

            public Object call(Expression expression, int index) {
                return this.doCall(expression, index);
            }

            public Map getPositionMapping() {
                return (Map)ScriptBytecodeAdapter.castToType(this.positionMapping.get(), Map.class);
            }

            public List getArgAssignments() {
                return (List)ScriptBytecodeAdapter.castToType(this.argAssignments.get(), List.class);
            }

            public BlockStatement getResult() {
                return (BlockStatement)ScriptBytecodeAdapter.castToType(this.result.get(), BlockStatement.class);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _convert_closure2.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        DefaultGroovyMethods.eachWithIndex(this.getArguments(recursiveCall), (Closure)new _convert_closure2(this, this, positionMapping2, argAssignments, result));
        Set<String> unusedTemps = this.replaceAllArgUsages(argAssignments.get(), tempMapping.get());
        String temp = null;
        Iterator<String> iterator = unusedTemps.iterator();
        while (iterator.hasNext()) {
            temp = ShortTypeHandling.castToString(iterator.next());
            result.get().getStatements().remove(DefaultGroovyMethods.getAt(tempDeclarations.get(), temp));
        }
        result.get().addStatement(this.recurStatement);
        return result.get();
    }

    private ExpressionStatement createAssignmentToIterationVariable(Expression expression, int index, Map<Integer, Map> positionMapping) {
        String argName = ShortTypeHandling.castToString(DefaultGroovyMethods.getAt((Map)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(positionMapping, Integer.valueOf(index)), Map.class), "name"));
        ClassNode argAndTempType = (ClassNode)ScriptBytecodeAdapter.asType(DefaultGroovyMethods.getAt((Map)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(positionMapping, Integer.valueOf(index)), Map.class), "type"), ClassNode.class);
        ExpressionStatement argAssignment = (ExpressionStatement)ScriptBytecodeAdapter.castToType(GeneralUtils.assignS(GeneralUtils.varX(argName, argAndTempType), expression), ExpressionStatement.class);
        return argAssignment;
    }

    private ExpressionStatement createTempDeclaration(int index, Map<Integer, Map> positionMapping, Map<String, Map> tempMapping, Map tempDeclarations) {
        String argName = ShortTypeHandling.castToString(DefaultGroovyMethods.getAt((Map)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(positionMapping, Integer.valueOf(index)), Map.class), "name"));
        String tempName = ShortTypeHandling.castToString(new GStringImpl(new Object[]{argName}, new String[]{"_", "_"}));
        ClassNode argAndTempType = (ClassNode)ScriptBytecodeAdapter.asType(DefaultGroovyMethods.getAt((Map)ScriptBytecodeAdapter.castToType(DefaultGroovyMethods.getAt(positionMapping, Integer.valueOf(index)), Map.class), "type"), ClassNode.class);
        ExpressionStatement tempDeclaration = AstHelper.createVariableAlias(tempName, argAndTempType, argName);
        Map map = ScriptBytecodeAdapter.createMap(new Object[]{"name", tempName, "type", argAndTempType});
        DefaultGroovyMethods.putAt(tempMapping, argName, map);
        ExpressionStatement expressionStatement = tempDeclaration;
        DefaultGroovyMethods.putAt(tempDeclarations, tempName, expressionStatement);
        return tempDeclaration;
    }

    private List<Expression> getArguments(Expression recursiveCall) {
        if (recursiveCall instanceof MethodCallExpression) {
            return ((TupleExpression)ScriptBytecodeAdapter.castToType(((MethodCallExpression)ScriptBytecodeAdapter.castToType(recursiveCall, MethodCallExpression.class)).getArguments(), TupleExpression.class)).getExpressions();
        }
        if (recursiveCall instanceof StaticMethodCallExpression) {
            return ((TupleExpression)ScriptBytecodeAdapter.castToType(((StaticMethodCallExpression)ScriptBytecodeAdapter.castToType(recursiveCall, StaticMethodCallExpression.class)).getArguments(), TupleExpression.class)).getExpressions();
        }
        return (List)ScriptBytecodeAdapter.castToType(null, List.class);
    }

    private boolean isAMethodCalls(Expression expression) {
        return DefaultTypeTransformation.booleanUnbox(ScriptBytecodeAdapter.createList(new Object[]{MethodCallExpression.class, StaticMethodCallExpression.class}) == null ? Boolean.valueOf(expression.getClass() == null) : Boolean.valueOf(DefaultGroovyMethods.isCase(ScriptBytecodeAdapter.createList(new Object[]{MethodCallExpression.class, StaticMethodCallExpression.class}), expression.getClass())));
    }

    private Set<String> replaceAllArgUsages(List<ExpressionStatement> iterationVariablesAssignmentNodes, Map<String, Map> tempMapping) {
        Set<String> set;
        public class _replaceAllArgUsages_closure3
        extends Closure
        implements GeneratedClosure {
            private static /* synthetic */ ClassInfo $staticClassInfo;
            public static transient /* synthetic */ boolean __$stMC;

            public _replaceAllArgUsages_closure3(Object _outerInstance, Object _thisObject) {
                super(_outerInstance, _thisObject);
            }

            public Object doCall(Map nameAndType) {
                return ShortTypeHandling.castToString(DefaultGroovyMethods.getAt(nameAndType, "name"));
            }

            public Object call(Map nameAndType) {
                return this.doCall(nameAndType);
            }

            protected /* synthetic */ MetaClass $getStaticMetaClass() {
                if (this.getClass() != _replaceAllArgUsages_closure3.class) {
                    return ScriptBytecodeAdapter.initMetaClass(this);
                }
                ClassInfo classInfo = $staticClassInfo;
                if (classInfo == null) {
                    $staticClassInfo = classInfo = ClassInfo.getClassInfo(this.getClass());
                }
                return classInfo.getMetaClass();
            }
        }
        Set<String> unusedTempNames = (Set<String>)ScriptBytecodeAdapter.asType(DefaultGroovyMethods.collect(tempMapping.values(), new _replaceAllArgUsages_closure3(this, this)), Set.class);
        UsedVariableTracker tracker = new UsedVariableTracker();
        ExpressionStatement statement = null;
        Iterator<ExpressionStatement> iterator = iterationVariablesAssignmentNodes.iterator();
        while (iterator.hasNext()) {
            statement = (ExpressionStatement)ScriptBytecodeAdapter.castToType(iterator.next(), ExpressionStatement.class);
            this.replaceArgUsageByTempUsage((BinaryExpression)ScriptBytecodeAdapter.castToType(statement.getExpression(), BinaryExpression.class), tempMapping, tracker);
        }
        unusedTempNames = set = DefaultGroovyMethods.minus(unusedTempNames, tracker.getUsedVariableNames());
        return unusedTempNames;
    }

    private void replaceArgUsageByTempUsage(BinaryExpression binary, Map tempMapping, UsedVariableTracker tracker) {
        VariableAccessReplacer variableAccessReplacer = new VariableAccessReplacer();
        Map map = tempMapping;
        variableAccessReplacer.setNameAndTypeMapping(map);
        UsedVariableTracker usedVariableTracker = tracker;
        variableAccessReplacer.setListener(usedVariableTracker);
        VariableAccessReplacer replacer = variableAccessReplacer;
        replacer.replaceIn(binary);
    }

    protected /* synthetic */ MetaClass $getStaticMetaClass() {
        if (this.getClass() != ReturnStatementToIterationConverter.class) {
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

    public Statement getRecurStatement() {
        return this.recurStatement;
    }

    public void setRecurStatement(Statement statement) {
        this.recurStatement = statement;
    }
}


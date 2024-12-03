/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.control;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.DynamicVariable;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.ImportNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.ModuleNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.Variable;
import org.codehaus.groovy.ast.expr.AnnotationConstantExpression;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.BinaryExpression;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.EmptyExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MapEntryExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.NamedArgumentListExpression;
import org.codehaus.groovy.ast.expr.PropertyExpression;
import org.codehaus.groovy.ast.expr.StaticMethodCallExpression;
import org.codehaus.groovy.ast.expr.TupleExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.MetaClassHelper;

public class StaticImportVisitor
extends ClassCodeExpressionTransformer {
    private ClassNode currentClass;
    private MethodNode currentMethod;
    private SourceUnit source;
    private boolean inSpecialConstructorCall;
    private boolean inClosure;
    private boolean inPropertyExpression;
    private Expression foundConstant;
    private Expression foundArgs;
    private boolean inAnnotation;
    private boolean inLeftExpression;

    public void visitClass(ClassNode node, SourceUnit source) {
        this.currentClass = node;
        this.source = source;
        super.visitClass(node);
    }

    @Override
    protected void visitConstructorOrMethod(MethodNode node, boolean isConstructor) {
        this.currentMethod = node;
        super.visitConstructorOrMethod(node, isConstructor);
        this.currentMethod = null;
    }

    @Override
    public void visitAnnotations(AnnotatedNode node) {
        boolean oldInAnnotation = this.inAnnotation;
        this.inAnnotation = true;
        super.visitAnnotations(node);
        this.inAnnotation = oldInAnnotation;
    }

    @Override
    public Expression transform(Expression exp) {
        if (exp == null) {
            return null;
        }
        if (exp.getClass() == VariableExpression.class) {
            return this.transformVariableExpression((VariableExpression)exp);
        }
        if (exp.getClass() == BinaryExpression.class) {
            return this.transformBinaryExpression((BinaryExpression)exp);
        }
        if (exp.getClass() == PropertyExpression.class) {
            return this.transformPropertyExpression((PropertyExpression)exp);
        }
        if (exp.getClass() == MethodCallExpression.class) {
            return this.transformMethodCallExpression((MethodCallExpression)exp);
        }
        if (exp.getClass() == ClosureExpression.class) {
            return this.transformClosureExpression((ClosureExpression)exp);
        }
        if (exp.getClass() == ConstructorCallExpression.class) {
            return this.transformConstructorCallExpression((ConstructorCallExpression)exp);
        }
        if (exp.getClass() == ArgumentListExpression.class) {
            Expression result = exp.transformExpression(this);
            if (this.inPropertyExpression) {
                this.foundArgs = result;
            }
            return result;
        }
        if (exp instanceof ConstantExpression) {
            ConstantExpression ce;
            Expression result = exp.transformExpression(this);
            if (this.inPropertyExpression) {
                this.foundConstant = result;
            }
            if (this.inAnnotation && exp instanceof AnnotationConstantExpression && (ce = (ConstantExpression)result).getValue() instanceof AnnotationNode) {
                AnnotationNode an = (AnnotationNode)ce.getValue();
                Map<String, Expression> attributes = an.getMembers();
                for (Map.Entry<String, Expression> entry : attributes.entrySet()) {
                    Expression attrExpr = this.transform(entry.getValue());
                    entry.setValue(attrExpr);
                }
            }
            return result;
        }
        return exp.transformExpression(this);
    }

    private Expression transformMapEntryExpression(MapEntryExpression me, ClassNode constructorCallType) {
        ImportNode importNode;
        Map<String, ImportNode> importNodes;
        Expression key = me.getKeyExpression();
        Expression value = me.getValueExpression();
        ModuleNode module = this.currentClass.getModule();
        if (module != null && key instanceof ConstantExpression && (importNodes = module.getStaticImports()).containsKey(key.getText()) && (importNode = importNodes.get(key.getText())).getType().equals(constructorCallType)) {
            String newKey = importNode.getFieldName();
            return new MapEntryExpression(new ConstantExpression(newKey), value.transformExpression(this));
        }
        return me;
    }

    protected Expression transformBinaryExpression(BinaryExpression be) {
        Expression left;
        int type = be.getOperation().getType();
        Expression right = this.transform(be.getRightExpression());
        be.setRightExpression(right);
        if (type == 100 && be.getLeftExpression() instanceof VariableExpression) {
            boolean oldInLeftExpression = this.inLeftExpression;
            this.inLeftExpression = true;
            left = this.transform(be.getLeftExpression());
            this.inLeftExpression = oldInLeftExpression;
            if (left instanceof StaticMethodCallExpression) {
                StaticMethodCallExpression smce = (StaticMethodCallExpression)left;
                StaticMethodCallExpression result = new StaticMethodCallExpression(smce.getOwnerType(), smce.getMethod(), right);
                StaticImportVisitor.setSourcePosition(result, be);
                return result;
            }
        } else {
            left = this.transform(be.getLeftExpression());
        }
        be.setLeftExpression(left);
        return be;
    }

    protected Expression transformVariableExpression(VariableExpression ve) {
        Expression result;
        Variable v = ve.getAccessedVariable();
        if (v != null && v instanceof DynamicVariable && (result = this.findStaticFieldOrPropAccessorImportFromModule(v.getName())) != null) {
            StaticImportVisitor.setSourcePosition(result, ve);
            if (this.inAnnotation) {
                result = this.transformInlineConstants(result);
            }
            return result;
        }
        return ve;
    }

    private static void setSourcePosition(Expression toSet, Expression origNode) {
        toSet.setSourcePosition(origNode);
        if (toSet instanceof PropertyExpression) {
            ((PropertyExpression)toSet).getProperty().setSourcePosition(origNode);
        }
    }

    private Expression transformInlineConstants(Expression exp) {
        if (exp instanceof PropertyExpression) {
            PropertyExpression pe = (PropertyExpression)exp;
            if (pe.getObjectExpression() instanceof ClassExpression) {
                ClassExpression ce = (ClassExpression)pe.getObjectExpression();
                ClassNode type = ce.getType();
                if (type.isEnum()) {
                    return exp;
                }
                Expression constant = StaticImportVisitor.findConstant(StaticImportVisitor.getField(type, pe.getPropertyAsString()));
                if (constant != null) {
                    return constant;
                }
            }
        } else if (exp instanceof ListExpression) {
            ListExpression le = (ListExpression)exp;
            ListExpression result = new ListExpression();
            for (Expression e : le.getExpressions()) {
                result.addExpression(this.transformInlineConstants(e));
            }
            return result;
        }
        return exp;
    }

    private static Expression findConstant(FieldNode fn) {
        if (fn != null && !fn.isEnum() && fn.isStatic() && fn.isFinal() && fn.getInitialValueExpression() instanceof ConstantExpression) {
            return fn.getInitialValueExpression();
        }
        return null;
    }

    protected Expression transformMethodCallExpression(MethodCallExpression mce) {
        Expression args = this.transform(mce.getArguments());
        Expression method = this.transform(mce.getMethod());
        Expression object = this.transform(mce.getObjectExpression());
        boolean isExplicitThisOrSuper = false;
        boolean isExplicitSuper = false;
        if (object instanceof VariableExpression) {
            VariableExpression ve = (VariableExpression)object;
            isExplicitThisOrSuper = !mce.isImplicitThis() && (ve.isThisExpression() || ve.isSuperExpression());
            isExplicitSuper = ve.isSuperExpression();
        }
        if (mce.isImplicitThis() || isExplicitThisOrSuper) {
            ConstantExpression ce;
            Object value;
            Expression ret;
            if (mce.isImplicitThis()) {
                String methodName;
                ret = this.findStaticMethodImportFromModule(method, args);
                if (ret != null) {
                    StaticImportVisitor.setSourcePosition(ret, mce);
                    return ret;
                }
                if (method instanceof ConstantExpression && !this.inLeftExpression && (ret = this.findStaticFieldOrPropAccessorImportFromModule(methodName = (String)((ConstantExpression)method).getValue())) != null) {
                    ret = new MethodCallExpression(ret, "call", args);
                    StaticImportVisitor.setSourcePosition(ret, mce);
                    return ret;
                }
            } else if (this.currentMethod != null && this.currentMethod.isStatic() && isExplicitSuper) {
                ret = new MethodCallExpression((Expression)new ClassExpression(this.currentClass.getSuperClass()), method, args);
                StaticImportVisitor.setSourcePosition(ret, mce);
                return ret;
            }
            if (method instanceof ConstantExpression && (value = (ce = (ConstantExpression)method).getValue()) instanceof String) {
                boolean lookForPossibleStaticMethod;
                String methodName = (String)value;
                boolean bl = lookForPossibleStaticMethod = !methodName.equals("call");
                if (this.currentMethod != null && !this.currentMethod.isStatic() && this.currentClass.hasPossibleMethod(methodName, args)) {
                    lookForPossibleStaticMethod = false;
                }
                if (!this.inClosure && (this.inSpecialConstructorCall || lookForPossibleStaticMethod && this.currentClass.hasPossibleStaticMethod(methodName, args))) {
                    StaticMethodCallExpression smce = new StaticMethodCallExpression(this.currentClass, methodName, args);
                    StaticImportVisitor.setSourcePosition(smce, mce);
                    return smce;
                }
            }
        }
        MethodCallExpression result = new MethodCallExpression(object, method, args);
        result.setSafe(mce.isSafe());
        result.setImplicitThis(mce.isImplicitThis());
        result.setSpreadSafe(mce.isSpreadSafe());
        result.setMethodTarget(mce.getMethodTarget());
        result.setGenericsTypes(mce.getGenericsTypes());
        StaticImportVisitor.setSourcePosition(result, mce);
        return result;
    }

    protected Expression transformConstructorCallExpression(ConstructorCallExpression cce) {
        TupleExpression tuple;
        this.inSpecialConstructorCall = cce.isSpecialCall();
        Expression expression = cce.getArguments();
        if (expression instanceof TupleExpression && (tuple = (TupleExpression)expression).getExpressions().size() == 1 && (expression = tuple.getExpression(0)) instanceof NamedArgumentListExpression) {
            NamedArgumentListExpression namedArgs = (NamedArgumentListExpression)expression;
            List<MapEntryExpression> entryExpressions = namedArgs.getMapEntryExpressions();
            for (int i = 0; i < entryExpressions.size(); ++i) {
                entryExpressions.set(i, (MapEntryExpression)this.transformMapEntryExpression(entryExpressions.get(i), cce.getType()));
            }
        }
        Expression ret = cce.transformExpression(this);
        this.inSpecialConstructorCall = false;
        return ret;
    }

    protected Expression transformClosureExpression(ClosureExpression ce) {
        Statement code;
        boolean oldInClosure = this.inClosure;
        this.inClosure = true;
        if (ce.getParameters() != null) {
            for (Parameter p : ce.getParameters()) {
                if (!p.hasInitialExpression()) continue;
                p.setInitialExpression(this.transform(p.getInitialExpression()));
            }
        }
        if ((code = ce.getCode()) != null) {
            code.visit(this);
        }
        this.inClosure = oldInClosure;
        return ce;
    }

    protected Expression transformPropertyExpression(PropertyExpression pe) {
        Expression result;
        if (this.currentMethod != null && this.currentMethod.isStatic() && pe.getObjectExpression() instanceof VariableExpression && ((VariableExpression)pe.getObjectExpression()).isSuperExpression()) {
            PropertyExpression pexp = new PropertyExpression((Expression)new ClassExpression(this.currentClass.getSuperClass()), this.transform(pe.getProperty()));
            pexp.setSourcePosition(pe);
            return pexp;
        }
        boolean oldInPropertyExpression = this.inPropertyExpression;
        Expression oldFoundArgs = this.foundArgs;
        Expression oldFoundConstant = this.foundConstant;
        this.inPropertyExpression = true;
        this.foundArgs = null;
        this.foundConstant = null;
        Expression objectExpression = this.transform(pe.getObjectExpression());
        boolean candidate = false;
        if (objectExpression instanceof MethodCallExpression) {
            candidate = ((MethodCallExpression)objectExpression).isImplicitThis();
        }
        if (this.foundArgs != null && this.foundConstant != null && candidate && (result = this.findStaticMethodImportFromModule(this.foundConstant, this.foundArgs)) != null) {
            objectExpression = result;
            objectExpression.setSourcePosition(pe);
        }
        this.inPropertyExpression = oldInPropertyExpression;
        this.foundArgs = oldFoundArgs;
        this.foundConstant = oldFoundConstant;
        pe.setObjectExpression(objectExpression);
        return pe;
    }

    private Expression findStaticFieldOrPropAccessorImportFromModule(String name) {
        Expression expression;
        ImportNode importNode;
        String accessorName;
        ModuleNode module = this.currentClass.getModule();
        if (module == null) {
            return null;
        }
        Map<String, ImportNode> importNodes = module.getStaticImports();
        if (importNodes.containsKey(accessorName = this.getAccessorName(name))) {
            importNode = importNodes.get(accessorName);
            expression = this.findStaticPropertyAccessorByFullName(importNode.getType(), importNode.getFieldName());
            if (expression != null) {
                return expression;
            }
            expression = this.findStaticPropertyAccessor(importNode.getType(), StaticImportVisitor.getPropNameForAccessor(importNode.getFieldName()));
            if (expression != null) {
                return expression;
            }
        }
        if (accessorName.startsWith("get") && importNodes.containsKey(accessorName = "is" + accessorName.substring(3))) {
            importNode = importNodes.get(accessorName);
            expression = this.findStaticPropertyAccessorByFullName(importNode.getType(), importNode.getFieldName());
            if (expression != null) {
                return expression;
            }
            expression = this.findStaticPropertyAccessor(importNode.getType(), StaticImportVisitor.getPropNameForAccessor(importNode.getFieldName()));
            if (expression != null) {
                return expression;
            }
        }
        if (importNodes.containsKey(name)) {
            importNode = importNodes.get(name);
            expression = this.findStaticPropertyAccessor(importNode.getType(), importNode.getFieldName());
            if (expression != null) {
                return expression;
            }
            expression = StaticImportVisitor.findStaticField(importNode.getType(), importNode.getFieldName());
            if (expression != null) {
                return expression;
            }
        }
        for (ImportNode importNode2 : module.getStaticStarImports().values()) {
            ClassNode node = importNode2.getType();
            expression = this.findStaticPropertyAccessor(node, name);
            if (expression != null) {
                return expression;
            }
            expression = StaticImportVisitor.findStaticField(node, name);
            if (expression == null) continue;
            return expression;
        }
        return null;
    }

    private Expression findStaticMethodImportFromModule(Expression method, Expression args) {
        ClassNode starImportType;
        String propName;
        Expression expression;
        ModuleNode module = this.currentClass.getModule();
        if (module == null || !(method instanceof ConstantExpression)) {
            return null;
        }
        Map<String, ImportNode> importNodes = module.getStaticImports();
        ConstantExpression ce = (ConstantExpression)method;
        Object value = ce.getValue();
        if (!(value instanceof String)) {
            return null;
        }
        String name = (String)value;
        if (importNodes.containsKey(name)) {
            ImportNode importNode = importNodes.get(name);
            expression = StaticImportVisitor.findStaticMethod(importNode.getType(), importNode.getFieldName(), args);
            if (expression != null) {
                return expression;
            }
            expression = this.findStaticPropertyAccessorGivenArgs(importNode.getType(), StaticImportVisitor.getPropNameForAccessor(importNode.getFieldName()), args);
            if (expression != null) {
                return new StaticMethodCallExpression(importNode.getType(), importNode.getFieldName(), args);
            }
        }
        if (StaticImportVisitor.validPropName(name) && importNodes.containsKey(propName = StaticImportVisitor.getPropNameForAccessor(name))) {
            ImportNode importNode = importNodes.get(propName);
            expression = StaticImportVisitor.findStaticMethod(importNode.getType(), StaticImportVisitor.prefix(name) + MetaClassHelper.capitalize(importNode.getFieldName()), args);
            if (expression != null) {
                return expression;
            }
            expression = this.findStaticPropertyAccessorGivenArgs(importNode.getType(), importNode.getFieldName(), args);
            if (expression != null) {
                return new StaticMethodCallExpression(importNode.getType(), StaticImportVisitor.prefix(name) + MetaClassHelper.capitalize(importNode.getFieldName()), args);
            }
        }
        Map<String, ImportNode> starImports = module.getStaticStarImports();
        if (this.currentClass.isEnum() && starImports.containsKey(this.currentClass.getName())) {
            ImportNode importNode = starImports.get(this.currentClass.getName());
            starImportType = importNode == null ? null : importNode.getType();
            expression = StaticImportVisitor.findStaticMethod(starImportType, name, args);
            if (expression != null) {
                return expression;
            }
        } else {
            for (ImportNode importNode : starImports.values()) {
                starImportType = importNode == null ? null : importNode.getType();
                expression = StaticImportVisitor.findStaticMethod(starImportType, name, args);
                if (expression != null) {
                    return expression;
                }
                expression = this.findStaticPropertyAccessorGivenArgs(starImportType, StaticImportVisitor.getPropNameForAccessor(name), args);
                if (expression == null) continue;
                return new StaticMethodCallExpression(starImportType, name, args);
            }
        }
        return null;
    }

    private static String prefix(String name) {
        return name.startsWith("is") ? "is" : name.substring(0, 3);
    }

    private static String getPropNameForAccessor(String fieldName) {
        int prefixLength;
        int n = prefixLength = fieldName.startsWith("is") ? 2 : 3;
        if (fieldName.length() < prefixLength + 1) {
            return fieldName;
        }
        if (!StaticImportVisitor.validPropName(fieldName)) {
            return fieldName;
        }
        return String.valueOf(fieldName.charAt(prefixLength)).toLowerCase() + fieldName.substring(prefixLength + 1);
    }

    private static boolean validPropName(String propName) {
        return propName.startsWith("get") || propName.startsWith("is") || propName.startsWith("set");
    }

    private String getAccessorName(String name) {
        return (this.inLeftExpression ? "set" : "get") + MetaClassHelper.capitalize(name);
    }

    private Expression findStaticPropertyAccessorGivenArgs(ClassNode staticImportType, String propName, Expression args) {
        return this.findStaticPropertyAccessor(staticImportType, propName);
    }

    private Expression findStaticPropertyAccessor(ClassNode staticImportType, String propName) {
        String accessorName = this.getAccessorName(propName);
        Expression accessor = this.findStaticPropertyAccessorByFullName(staticImportType, accessorName);
        if (accessor == null && accessorName.startsWith("get")) {
            accessor = this.findStaticPropertyAccessorByFullName(staticImportType, "is" + accessorName.substring(3));
        }
        if (accessor == null && StaticImportVisitor.hasStaticProperty(staticImportType, propName)) {
            accessor = this.inLeftExpression ? new StaticMethodCallExpression(staticImportType, accessorName, ArgumentListExpression.EMPTY_ARGUMENTS) : new PropertyExpression((Expression)new ClassExpression(staticImportType), propName);
        }
        return accessor;
    }

    private static boolean hasStaticProperty(ClassNode cNode, String propName) {
        return StaticImportVisitor.getStaticProperty(cNode, propName) != null;
    }

    private static PropertyNode getStaticProperty(ClassNode cNode, String propName) {
        for (ClassNode classNode = cNode; classNode != null; classNode = classNode.getSuperClass()) {
            for (PropertyNode pn : classNode.getProperties()) {
                if (!pn.getName().equals(propName) || !pn.isStatic()) continue;
                return pn;
            }
        }
        return null;
    }

    private Expression findStaticPropertyAccessorByFullName(ClassNode staticImportType, String accessorMethodName) {
        ArgumentListExpression dummyArgs = new ArgumentListExpression();
        dummyArgs.addExpression(new EmptyExpression());
        return StaticImportVisitor.findStaticMethod(staticImportType, accessorMethodName, this.inLeftExpression ? dummyArgs : ArgumentListExpression.EMPTY_ARGUMENTS);
    }

    private static Expression findStaticField(ClassNode staticImportType, String fieldName) {
        FieldNode field;
        if ((staticImportType.isPrimaryClassNode() || staticImportType.isResolved()) && (field = StaticImportVisitor.getField(staticImportType, fieldName)) != null && field.isStatic()) {
            return new PropertyExpression((Expression)new ClassExpression(staticImportType), fieldName);
        }
        return null;
    }

    private static FieldNode getField(ClassNode classNode, String fieldName) {
        HashSet<String> visited = new HashSet<String>();
        for (ClassNode node = classNode; node != null; node = node.getSuperClass()) {
            ClassNode[] interfaces;
            FieldNode fn = node.getDeclaredField(fieldName);
            if (fn != null) {
                return fn;
            }
            for (ClassNode iNode : interfaces = node.getInterfaces()) {
                if (visited.contains(iNode.getName())) continue;
                FieldNode ifn = StaticImportVisitor.getField(iNode, fieldName);
                visited.add(iNode.getName());
                if (ifn == null) continue;
                return ifn;
            }
        }
        return null;
    }

    private static Expression findStaticMethod(ClassNode staticImportType, String methodName, Expression args) {
        if ((staticImportType.isPrimaryClassNode() || staticImportType.isResolved()) && staticImportType.hasPossibleStaticMethod(methodName, args)) {
            return new StaticMethodCallExpression(staticImportType, methodName, args);
        }
        return null;
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }
}


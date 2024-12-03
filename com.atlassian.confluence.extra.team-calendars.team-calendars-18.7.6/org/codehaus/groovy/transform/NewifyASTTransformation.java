/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.lang.Newify;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassCodeExpressionTransformer;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.expr.ClassExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.ConstructorCallExpression;
import org.codehaus.groovy.ast.expr.DeclarationExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.ListExpression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.ASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class NewifyASTTransformation
extends ClassCodeExpressionTransformer
implements ASTTransformation {
    private static final ClassNode MY_TYPE = ClassHelper.make(Newify.class);
    private static final String MY_NAME = MY_TYPE.getNameWithoutPackage();
    private static final String BASE_BAD_PARAM_ERROR = "Error during @" + MY_NAME + " processing. Annotation parameter must be a class or list of classes but found ";
    private SourceUnit source;
    private ListExpression classesToNewify;
    private DeclarationExpression candidate;
    private boolean auto;

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.source = source;
        if (nodes.length != 2 || !(nodes[0] instanceof AnnotationNode) || !(nodes[1] instanceof AnnotatedNode)) {
            this.internalError("Expecting [AnnotationNode, AnnotatedClass] but got: " + Arrays.asList(nodes));
        }
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        AnnotationNode node = (AnnotationNode)nodes[0];
        if (!MY_TYPE.equals(node.getClassNode())) {
            this.internalError("Transformation called from wrong annotation: " + node.getClassNode().getName());
        }
        boolean autoFlag = this.determineAutoFlag(node.getMember("auto"));
        Expression value = node.getMember("value");
        if (parent instanceof ClassNode) {
            this.newifyClass((ClassNode)parent, autoFlag, this.determineClasses(value, false));
        } else if (parent instanceof MethodNode || parent instanceof FieldNode) {
            this.newifyMethodOrField(parent, autoFlag, this.determineClasses(value, false));
        } else if (parent instanceof DeclarationExpression) {
            this.newifyDeclaration((DeclarationExpression)parent, autoFlag, this.determineClasses(value, true));
        }
    }

    private void newifyDeclaration(DeclarationExpression de, boolean autoFlag, ListExpression list) {
        ClassNode cNode = de.getDeclaringClass();
        this.candidate = de;
        ListExpression oldClassesToNewify = this.classesToNewify;
        boolean oldAuto = this.auto;
        this.classesToNewify = list;
        this.auto = autoFlag;
        super.visitClass(cNode);
        this.classesToNewify = oldClassesToNewify;
        this.auto = oldAuto;
    }

    private boolean determineAutoFlag(Expression autoExpr) {
        return !(autoExpr instanceof ConstantExpression) || !((ConstantExpression)autoExpr).getValue().equals(false);
    }

    private ListExpression determineClasses(Expression expr, boolean searchSourceUnit) {
        ListExpression list = new ListExpression();
        if (expr instanceof ClassExpression) {
            list.addExpression(expr);
        } else if (expr instanceof VariableExpression && searchSourceUnit) {
            VariableExpression ve = (VariableExpression)expr;
            ClassNode fromSourceUnit = this.getSourceUnitClass(ve);
            if (fromSourceUnit != null) {
                ClassExpression found = GeneralUtils.classX(fromSourceUnit);
                found.setSourcePosition(ve);
                list.addExpression(found);
            } else {
                this.addError(BASE_BAD_PARAM_ERROR + "an unresolvable reference to '" + ve.getName() + "'.", expr);
            }
        } else if (expr instanceof ListExpression) {
            list = (ListExpression)expr;
            List<Expression> expressions = list.getExpressions();
            for (int i = 0; i < expressions.size(); ++i) {
                Expression next = expressions.get(i);
                if (next instanceof VariableExpression && searchSourceUnit) {
                    VariableExpression ve = (VariableExpression)next;
                    ClassNode fromSourceUnit = this.getSourceUnitClass(ve);
                    if (fromSourceUnit != null) {
                        ClassExpression found = GeneralUtils.classX(fromSourceUnit);
                        found.setSourcePosition(ve);
                        expressions.set(i, found);
                        continue;
                    }
                    this.addError(BASE_BAD_PARAM_ERROR + "a list containing an unresolvable reference to '" + ve.getName() + "'.", next);
                    continue;
                }
                if (next instanceof ClassExpression) continue;
                this.addError(BASE_BAD_PARAM_ERROR + "a list containing type: " + next.getType().getName() + ".", next);
            }
            this.checkDuplicateNameClashes(list);
        } else if (expr != null) {
            this.addError(BASE_BAD_PARAM_ERROR + "a type: " + expr.getType().getName() + ".", expr);
        }
        return list;
    }

    private ClassNode getSourceUnitClass(VariableExpression ve) {
        List<ClassNode> classes = this.source.getAST().getClasses();
        for (ClassNode classNode : classes) {
            if (!classNode.getNameWithoutPackage().equals(ve.getName())) continue;
            return classNode;
        }
        return null;
    }

    @Override
    public Expression transform(Expression expr) {
        if (expr == null) {
            return null;
        }
        if (expr instanceof MethodCallExpression && this.candidate == null) {
            MethodCallExpression mce = (MethodCallExpression)expr;
            Expression args = this.transform(mce.getArguments());
            if (this.isNewifyCandidate(mce)) {
                Expression transformed = this.transformMethodCall(mce, args);
                transformed.setSourcePosition(mce);
                return transformed;
            }
            Expression method = this.transform(mce.getMethod());
            Expression object = this.transform(mce.getObjectExpression());
            MethodCallExpression transformed = GeneralUtils.callX(object, method, args);
            transformed.setImplicitThis(mce.isImplicitThis());
            transformed.setSafe(mce.isSafe());
            transformed.setSourcePosition(mce);
            return transformed;
        }
        if (expr instanceof ClosureExpression) {
            ClosureExpression ce = (ClosureExpression)expr;
            ce.getCode().visit(this);
        } else if (expr instanceof ConstructorCallExpression) {
            ConstructorCallExpression cce = (ConstructorCallExpression)expr;
            if (cce.isUsingAnonymousInnerClass()) {
                cce.getType().visitContents(this);
            }
        } else if (expr instanceof DeclarationExpression) {
            DeclarationExpression de = (DeclarationExpression)expr;
            if (this.shouldTransform(de)) {
                this.candidate = null;
                Expression left = de.getLeftExpression();
                Expression right = this.transform(de.getRightExpression());
                DeclarationExpression newDecl = new DeclarationExpression(left, de.getOperation(), right);
                newDecl.addAnnotations(de.getAnnotations());
                return newDecl;
            }
            return de;
        }
        return expr.transformExpression(this);
    }

    private boolean shouldTransform(DeclarationExpression exp) {
        return exp == this.candidate || this.auto || this.hasClassesToNewify();
    }

    private boolean hasClassesToNewify() {
        return this.classesToNewify != null && !this.classesToNewify.getExpressions().isEmpty();
    }

    private void newifyClass(ClassNode cNode, boolean autoFlag, ListExpression list) {
        String cName = cNode.getName();
        if (cNode.isInterface()) {
            this.addError("Error processing interface '" + cName + "'. @" + MY_NAME + " not allowed for interfaces.", cNode);
        }
        ListExpression oldClassesToNewify = this.classesToNewify;
        boolean oldAuto = this.auto;
        this.classesToNewify = list;
        this.auto = autoFlag;
        super.visitClass(cNode);
        this.classesToNewify = oldClassesToNewify;
        this.auto = oldAuto;
    }

    private void newifyMethodOrField(AnnotatedNode parent, boolean autoFlag, ListExpression list) {
        ListExpression oldClassesToNewify = this.classesToNewify;
        boolean oldAuto = this.auto;
        this.checkClassLevelClashes(list);
        this.checkAutoClash(autoFlag, parent);
        this.classesToNewify = list;
        this.auto = autoFlag;
        if (parent instanceof FieldNode) {
            super.visitField((FieldNode)parent);
        } else {
            super.visitMethod((MethodNode)parent);
        }
        this.classesToNewify = oldClassesToNewify;
        this.auto = oldAuto;
    }

    private void checkDuplicateNameClashes(ListExpression list) {
        HashSet<String> seen = new HashSet<String>();
        List<Expression> classes = list.getExpressions();
        for (ClassExpression classExpression : classes) {
            String name = classExpression.getType().getNameWithoutPackage();
            if (seen.contains(name)) {
                this.addError("Duplicate name '" + name + "' found during @" + MY_NAME + " processing.", classExpression);
            }
            seen.add(name);
        }
    }

    private void checkAutoClash(boolean autoFlag, AnnotatedNode parent) {
        if (this.auto && !autoFlag) {
            this.addError("Error during @" + MY_NAME + " processing. The 'auto' flag can't be false at method/constructor/field level if it is true at the class level.", parent);
        }
    }

    private void checkClassLevelClashes(ListExpression list) {
        List<Expression> classes = list.getExpressions();
        for (ClassExpression classExpression : classes) {
            String name = classExpression.getType().getNameWithoutPackage();
            if (!this.findClassWithMatchingBasename(name)) continue;
            this.addError("Error during @" + MY_NAME + " processing. Class '" + name + "' can't appear at method/constructor/field level if it already appears at the class level.", classExpression);
        }
    }

    private boolean findClassWithMatchingBasename(String nameWithoutPackage) {
        if (this.classesToNewify == null) {
            return false;
        }
        List<Expression> classes = this.classesToNewify.getExpressions();
        for (ClassExpression classExpression : classes) {
            if (!classExpression.getType().getNameWithoutPackage().equals(nameWithoutPackage)) continue;
            return true;
        }
        return false;
    }

    private boolean isNewifyCandidate(MethodCallExpression mce) {
        return mce.getObjectExpression() == VariableExpression.THIS_EXPRESSION || this.auto && this.isNewMethodStyle(mce);
    }

    private boolean isNewMethodStyle(MethodCallExpression mce) {
        Expression obj = mce.getObjectExpression();
        Expression meth = mce.getMethod();
        return obj instanceof ClassExpression && meth instanceof ConstantExpression && ((ConstantExpression)meth).getValue().equals("new");
    }

    private Expression transformMethodCall(MethodCallExpression mce, Expression args) {
        ClassNode classType = this.isNewMethodStyle(mce) ? mce.getObjectExpression().getType() : this.findMatchingCandidateClass(mce);
        if (classType != null) {
            return new ConstructorCallExpression(classType, args);
        }
        mce.setArguments(args);
        return mce;
    }

    private ClassNode findMatchingCandidateClass(MethodCallExpression mce) {
        if (this.classesToNewify == null) {
            return null;
        }
        List<Expression> classes = this.classesToNewify.getExpressions();
        for (ClassExpression classExpression : classes) {
            ClassNode type = classExpression.getType();
            if (!type.getNameWithoutPackage().equals(mce.getMethodAsString())) continue;
            return type;
        }
        return null;
    }

    private void internalError(String message) {
        throw new GroovyBugError("Internal error: " + message);
    }

    @Override
    protected SourceUnit getSourceUnit() {
        return this.source;
    }
}


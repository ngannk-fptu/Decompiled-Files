/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.transform;

import groovy.transform.Sortable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import org.codehaus.groovy.ast.ASTNode;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AnnotationNode;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.FieldNode;
import org.codehaus.groovy.ast.InnerClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.PropertyNode;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;
import org.codehaus.groovy.ast.tools.GeneralUtils;
import org.codehaus.groovy.ast.tools.GenericsUtils;
import org.codehaus.groovy.classgen.VariableScopeVisitor;
import org.codehaus.groovy.control.CompilePhase;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.runtime.AbstractComparator;
import org.codehaus.groovy.runtime.StringGroovyMethods;
import org.codehaus.groovy.transform.AbstractASTTransformation;
import org.codehaus.groovy.transform.GroovyASTTransformation;

@GroovyASTTransformation(phase=CompilePhase.CANONICALIZATION)
public class SortableASTTransformation
extends AbstractASTTransformation {
    private static final ClassNode MY_TYPE = ClassHelper.make(Sortable.class);
    private static final String MY_TYPE_NAME = "@" + MY_TYPE.getNameWithoutPackage();
    private static final ClassNode COMPARABLE_TYPE = GenericsUtils.makeClassSafe(Comparable.class);
    private static final ClassNode COMPARATOR_TYPE = GenericsUtils.makeClassSafe(Comparator.class);
    private static final String VALUE = "value";
    private static final String OTHER = "other";
    private static final String THIS_HASH = "thisHash";
    private static final String OTHER_HASH = "otherHash";
    private static final String ARG0 = "arg0";
    private static final String ARG1 = "arg1";

    @Override
    public void visit(ASTNode[] nodes, SourceUnit source) {
        this.init(nodes, source);
        AnnotationNode annotation = (AnnotationNode)nodes[0];
        AnnotatedNode parent = (AnnotatedNode)nodes[1];
        if (parent instanceof ClassNode) {
            this.createSortable(annotation, (ClassNode)parent);
        }
    }

    private void createSortable(AnnotationNode annotation, ClassNode classNode) {
        List<String> includes = SortableASTTransformation.getMemberList(annotation, "includes");
        List<String> excludes = SortableASTTransformation.getMemberList(annotation, "excludes");
        if (!this.checkIncludeExclude(annotation, excludes, includes, MY_TYPE_NAME)) {
            return;
        }
        if (classNode.isInterface()) {
            this.addError(MY_TYPE_NAME + " cannot be applied to interface " + classNode.getName(), annotation);
        }
        List<PropertyNode> properties = this.findProperties(annotation, classNode, includes, excludes);
        this.implementComparable(classNode);
        classNode.addMethod(new MethodNode("compareTo", 1, ClassHelper.int_TYPE, GeneralUtils.params(GeneralUtils.param(GenericsUtils.newClass(classNode), OTHER)), ClassNode.EMPTY_ARRAY, SortableASTTransformation.createCompareToMethodBody(properties)));
        for (PropertyNode property : properties) {
            SortableASTTransformation.createComparatorFor(classNode, property);
        }
        new VariableScopeVisitor(this.sourceUnit, true).visitClass(classNode);
    }

    private void implementComparable(ClassNode classNode) {
        if (!classNode.implementsInterface(COMPARABLE_TYPE)) {
            classNode.addInterface(GenericsUtils.makeClassSafeWithGenerics(Comparable.class, classNode));
        }
    }

    private static Statement createCompareToMethodBody(List<PropertyNode> properties) {
        ArrayList<Statement> statements = new ArrayList<Statement>();
        statements.add(GeneralUtils.ifS((Expression)GeneralUtils.callThisX("is", GeneralUtils.args(OTHER)), GeneralUtils.returnS(GeneralUtils.constX(0))));
        if (properties.isEmpty()) {
            statements.add(GeneralUtils.declS(GeneralUtils.varX(THIS_HASH, ClassHelper.Integer_TYPE), GeneralUtils.callX(GeneralUtils.varX("this"), "hashCode")));
            statements.add(GeneralUtils.declS(GeneralUtils.varX(OTHER_HASH, ClassHelper.Integer_TYPE), GeneralUtils.callX(GeneralUtils.varX(OTHER), "hashCode")));
            statements.add(GeneralUtils.returnS(GeneralUtils.cmpX(GeneralUtils.varX(THIS_HASH), GeneralUtils.varX(OTHER_HASH))));
        } else {
            statements.add(GeneralUtils.declS(GeneralUtils.varX(VALUE, ClassHelper.int_TYPE), GeneralUtils.constX(0)));
            for (PropertyNode property : properties) {
                String propName = property.getName();
                statements.add(GeneralUtils.assignS(GeneralUtils.varX(VALUE), GeneralUtils.cmpX(GeneralUtils.propX((Expression)GeneralUtils.varX("this"), propName), GeneralUtils.propX((Expression)GeneralUtils.varX(OTHER), propName))));
                statements.add(GeneralUtils.ifS((Expression)GeneralUtils.neX(GeneralUtils.varX(VALUE), GeneralUtils.constX(0)), GeneralUtils.returnS(GeneralUtils.varX(VALUE))));
            }
            statements.add(GeneralUtils.returnS(GeneralUtils.constX(0)));
        }
        BlockStatement body = new BlockStatement();
        body.addStatements(statements);
        return body;
    }

    private static Statement createCompareMethodBody(PropertyNode property) {
        String propName = property.getName();
        return GeneralUtils.block(GeneralUtils.ifS((Expression)GeneralUtils.eqX(GeneralUtils.varX(ARG0), GeneralUtils.varX(ARG1)), GeneralUtils.returnS(GeneralUtils.constX(0))), GeneralUtils.ifS((Expression)GeneralUtils.andX(GeneralUtils.notNullX(GeneralUtils.varX(ARG0)), GeneralUtils.equalsNullX(GeneralUtils.varX(ARG1))), GeneralUtils.returnS(GeneralUtils.constX(-1))), GeneralUtils.ifS((Expression)GeneralUtils.andX(GeneralUtils.equalsNullX(GeneralUtils.varX(ARG0)), GeneralUtils.notNullX(GeneralUtils.varX(ARG1))), GeneralUtils.returnS(GeneralUtils.constX(1))), GeneralUtils.returnS(GeneralUtils.cmpX(GeneralUtils.propX((Expression)GeneralUtils.varX(ARG0), propName), GeneralUtils.propX((Expression)GeneralUtils.varX(ARG1), propName))));
    }

    private static void createComparatorFor(ClassNode classNode, PropertyNode property) {
        String propName = StringGroovyMethods.capitalize((CharSequence)property.getName());
        String className = classNode.getName() + "$" + propName + "Comparator";
        ClassNode superClass = GenericsUtils.makeClassSafeWithGenerics(AbstractComparator.class, classNode);
        InnerClassNode cmpClass = new InnerClassNode(classNode, className, 10, superClass);
        classNode.getModule().addClass(cmpClass);
        cmpClass.addMethod(new MethodNode("compare", 1, ClassHelper.int_TYPE, GeneralUtils.params(GeneralUtils.param(GenericsUtils.newClass(classNode), ARG0), GeneralUtils.param(GenericsUtils.newClass(classNode), ARG1)), ClassNode.EMPTY_ARRAY, SortableASTTransformation.createCompareMethodBody(property)));
        String fieldName = "this$" + propName + "Comparator";
        FieldNode cmpField = classNode.addField(fieldName, 4122, COMPARATOR_TYPE, GeneralUtils.ctorX(cmpClass));
        classNode.addMethod(new MethodNode("comparatorBy" + propName, 9, COMPARATOR_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, GeneralUtils.returnS(GeneralUtils.fieldX(cmpField))));
    }

    private List<PropertyNode> findProperties(AnnotationNode annotation, ClassNode classNode, final List<String> includes, List<String> excludes) {
        ArrayList<PropertyNode> properties = new ArrayList<PropertyNode>();
        for (PropertyNode property : classNode.getProperties()) {
            String propertyName = property.getName();
            if (property.isStatic() || excludes.contains(propertyName) || !includes.isEmpty() && !includes.contains(propertyName)) continue;
            properties.add(property);
        }
        for (String name : includes) {
            this.checkKnownProperty(annotation, name, properties);
        }
        for (PropertyNode pNode : properties) {
            this.checkComparable(pNode);
        }
        if (!includes.isEmpty()) {
            Comparator<PropertyNode> includeComparator = new Comparator<PropertyNode>(){

                @Override
                public int compare(PropertyNode o1, PropertyNode o2) {
                    return Integer.valueOf(includes.indexOf(o1.getName())).compareTo(includes.indexOf(o2.getName()));
                }
            };
            Collections.sort(properties, includeComparator);
        }
        return properties;
    }

    private void checkComparable(PropertyNode pNode) {
        if (pNode.getType().implementsInterface(COMPARABLE_TYPE) || ClassHelper.isPrimitiveType(pNode.getType()) || this.hasAnnotation(pNode.getType(), MY_TYPE)) {
            return;
        }
        this.addError("Error during " + MY_TYPE_NAME + " processing: property '" + pNode.getName() + "' must be Comparable", pNode);
    }

    private void checkKnownProperty(AnnotationNode annotation, String name, List<PropertyNode> properties) {
        for (PropertyNode pNode : properties) {
            if (!name.equals(pNode.getName())) continue;
            return;
        }
        this.addError("Error during " + MY_TYPE_NAME + " processing: tried to include unknown property '" + name + "'", annotation);
    }
}


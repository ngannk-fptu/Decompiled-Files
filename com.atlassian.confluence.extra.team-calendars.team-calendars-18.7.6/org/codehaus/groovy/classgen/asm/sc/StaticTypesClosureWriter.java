/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.classgen.asm.sc;

import java.util.List;
import org.codehaus.groovy.GroovyBugError;
import org.codehaus.groovy.ast.ClassCodeVisitorSupport;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.MethodNode;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.expr.ArgumentListExpression;
import org.codehaus.groovy.ast.expr.ClosureExpression;
import org.codehaus.groovy.ast.expr.ConstantExpression;
import org.codehaus.groovy.ast.expr.Expression;
import org.codehaus.groovy.ast.expr.MethodCallExpression;
import org.codehaus.groovy.ast.expr.VariableExpression;
import org.codehaus.groovy.ast.stmt.ReturnStatement;
import org.codehaus.groovy.classgen.asm.ClosureWriter;
import org.codehaus.groovy.classgen.asm.WriterController;
import org.codehaus.groovy.control.SourceUnit;
import org.codehaus.groovy.transform.sc.StaticCompilationMetadataKeys;
import org.codehaus.groovy.transform.stc.StaticTypesMarker;

public class StaticTypesClosureWriter
extends ClosureWriter {
    public StaticTypesClosureWriter(WriterController wc) {
        super(wc);
    }

    @Override
    protected ClassNode createClosureClass(ClosureExpression expression, int mods) {
        ClassNode closureClass = super.createClosureClass(expression, mods);
        List<MethodNode> methods = closureClass.getDeclaredMethods("call");
        List<MethodNode> doCall = closureClass.getMethods("doCall");
        if (doCall.size() != 1) {
            throw new GroovyBugError("Expected to find one (1) doCall method on generated closure, but found " + doCall.size());
        }
        MethodNode doCallMethod = doCall.get(0);
        if (methods.isEmpty() && doCallMethod.getParameters().length == 1) {
            StaticTypesClosureWriter.createDirectCallMethod(closureClass, doCallMethod);
        }
        MethodTargetCompletionVisitor visitor = new MethodTargetCompletionVisitor(doCallMethod);
        Object dynamic = expression.getNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION);
        if (dynamic != null) {
            doCallMethod.putNodeMetaData((Object)StaticTypesMarker.DYNAMIC_RESOLUTION, dynamic);
        }
        for (MethodNode method : methods) {
            visitor.visitMethod(method);
        }
        closureClass.putNodeMetaData((Object)StaticCompilationMetadataKeys.STATIC_COMPILE_NODE, Boolean.TRUE);
        return closureClass;
    }

    private static void createDirectCallMethod(ClassNode closureClass, MethodNode doCallMethod) {
        Parameter args = new Parameter(ClassHelper.OBJECT_TYPE, "args");
        MethodCallExpression doCall1arg = new MethodCallExpression((Expression)new VariableExpression("this", closureClass), "doCall", (Expression)new ArgumentListExpression(new VariableExpression(args)));
        doCall1arg.setImplicitThis(true);
        doCall1arg.setMethodTarget(doCallMethod);
        closureClass.addMethod(new MethodNode("call", 1, ClassHelper.OBJECT_TYPE, new Parameter[]{args}, ClassNode.EMPTY_ARRAY, new ReturnStatement(doCall1arg)));
        MethodCallExpression doCallNoArgs = new MethodCallExpression((Expression)new VariableExpression("this", closureClass), "doCall", (Expression)new ArgumentListExpression(new ConstantExpression(null)));
        doCallNoArgs.setImplicitThis(true);
        doCallNoArgs.setMethodTarget(doCallMethod);
        closureClass.addMethod(new MethodNode("call", 1, ClassHelper.OBJECT_TYPE, Parameter.EMPTY_ARRAY, ClassNode.EMPTY_ARRAY, new ReturnStatement(doCallNoArgs)));
    }

    private static final class MethodTargetCompletionVisitor
    extends ClassCodeVisitorSupport {
        private final MethodNode doCallMethod;

        private MethodTargetCompletionVisitor(MethodNode doCallMethod) {
            this.doCallMethod = doCallMethod;
        }

        @Override
        protected SourceUnit getSourceUnit() {
            return null;
        }

        @Override
        public void visitMethodCallExpression(MethodCallExpression call) {
            super.visitMethodCallExpression(call);
            MethodNode mn = call.getMethodTarget();
            if (mn == null) {
                call.setMethodTarget(this.doCallMethod);
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovyjarjarasm.asm.Opcodes;
import java.util.List;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.AstToTextHelper;
import org.codehaus.groovy.ast.ClassHelper;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GenericsType;
import org.codehaus.groovy.ast.Parameter;
import org.codehaus.groovy.ast.VariableScope;
import org.codehaus.groovy.ast.stmt.BlockStatement;
import org.codehaus.groovy.ast.stmt.Statement;

public class MethodNode
extends AnnotatedNode
implements Opcodes {
    public static final String SCRIPT_BODY_METHOD_KEY = "org.codehaus.groovy.ast.MethodNode.isScriptBody";
    private final String name;
    private int modifiers;
    private boolean syntheticPublic;
    private ClassNode returnType;
    private Parameter[] parameters;
    private boolean hasDefaultValue = false;
    private Statement code;
    private boolean dynamicReturnType;
    private VariableScope variableScope;
    private final ClassNode[] exceptions;
    private final boolean staticConstructor;
    private GenericsType[] genericsTypes = null;
    private boolean hasDefault;
    String typeDescriptor;

    public MethodNode(String name, int modifiers, ClassNode returnType, Parameter[] parameters, ClassNode[] exceptions, Statement code) {
        this.name = name;
        this.modifiers = modifiers;
        this.code = code;
        this.setReturnType(returnType);
        VariableScope scope = new VariableScope();
        this.setVariableScope(scope);
        this.setParameters(parameters);
        this.hasDefault = false;
        this.exceptions = exceptions;
        this.staticConstructor = name != null && name.equals("<clinit>");
    }

    public String getTypeDescriptor() {
        if (this.typeDescriptor == null) {
            StringBuilder buf = new StringBuilder(this.name.length() + this.parameters.length * 10);
            buf.append(this.returnType.getName());
            buf.append(' ');
            buf.append(this.name);
            buf.append('(');
            for (int i = 0; i < this.parameters.length; ++i) {
                if (i > 0) {
                    buf.append(", ");
                }
                Parameter param = this.parameters[i];
                buf.append(MethodNode.formatTypeName(param.getType()));
            }
            buf.append(')');
            this.typeDescriptor = buf.toString();
        }
        return this.typeDescriptor;
    }

    private static String formatTypeName(ClassNode type) {
        if (type.isArray()) {
            ClassNode it = type;
            int dim = 0;
            while (it.isArray()) {
                ++dim;
                it = it.getComponentType();
            }
            StringBuilder sb = new StringBuilder(it.getName().length() + 2 * dim);
            sb.append(it.getName());
            for (int i = 0; i < dim; ++i) {
                sb.append("[]");
            }
            return sb.toString();
        }
        return type.getName();
    }

    private void invalidateCachedData() {
        this.typeDescriptor = null;
    }

    public boolean isVoidMethod() {
        return this.returnType == ClassHelper.VOID_TYPE;
    }

    public Statement getCode() {
        return this.code;
    }

    public void setCode(Statement code) {
        this.code = code;
    }

    public int getModifiers() {
        return this.modifiers;
    }

    public void setModifiers(int modifiers) {
        this.invalidateCachedData();
        this.modifiers = modifiers;
    }

    public String getName() {
        return this.name;
    }

    public Parameter[] getParameters() {
        return this.parameters;
    }

    public void setParameters(Parameter[] parameters) {
        this.invalidateCachedData();
        VariableScope scope = new VariableScope();
        this.parameters = parameters;
        if (parameters != null && parameters.length > 0) {
            for (Parameter para : parameters) {
                if (para.hasInitialExpression()) {
                    this.hasDefaultValue = true;
                }
                para.setInStaticContext(this.isStatic());
                scope.putDeclaredVariable(para);
            }
        }
        this.setVariableScope(scope);
    }

    public ClassNode getReturnType() {
        return this.returnType;
    }

    public VariableScope getVariableScope() {
        return this.variableScope;
    }

    public void setVariableScope(VariableScope variableScope) {
        this.variableScope = variableScope;
        variableScope.setInStaticContext(this.isStatic());
    }

    public boolean isDynamicReturnType() {
        return this.dynamicReturnType;
    }

    public boolean isAbstract() {
        return (this.modifiers & 0x400) != 0;
    }

    public boolean isStatic() {
        return (this.modifiers & 8) != 0;
    }

    public boolean isPublic() {
        return (this.modifiers & 1) != 0;
    }

    public boolean isPrivate() {
        return (this.modifiers & 2) != 0;
    }

    public boolean isFinal() {
        return (this.modifiers & 0x10) != 0;
    }

    public boolean isProtected() {
        return (this.modifiers & 4) != 0;
    }

    public boolean hasDefaultValue() {
        return this.hasDefaultValue;
    }

    public boolean isScriptBody() {
        return this.getNodeMetaData(SCRIPT_BODY_METHOD_KEY) != null;
    }

    public void setIsScriptBody() {
        this.setNodeMetaData(SCRIPT_BODY_METHOD_KEY, true);
    }

    public String toString() {
        return "MethodNode@" + this.hashCode() + "[" + this.getTypeDescriptor() + "]";
    }

    public void setReturnType(ClassNode returnType) {
        this.invalidateCachedData();
        this.dynamicReturnType |= ClassHelper.DYNAMIC_TYPE == returnType;
        this.returnType = returnType;
        if (returnType == null) {
            this.returnType = ClassHelper.OBJECT_TYPE;
        }
    }

    public ClassNode[] getExceptions() {
        return this.exceptions;
    }

    public Statement getFirstStatement() {
        if (this.code == null) {
            return null;
        }
        Statement first = this.code;
        while (first instanceof BlockStatement) {
            List<Statement> list = ((BlockStatement)first).getStatements();
            if (list.isEmpty()) {
                first = null;
                continue;
            }
            first = list.get(0);
        }
        return first;
    }

    public GenericsType[] getGenericsTypes() {
        return this.genericsTypes;
    }

    public void setGenericsTypes(GenericsType[] genericsTypes) {
        this.invalidateCachedData();
        this.genericsTypes = genericsTypes;
    }

    public void setAnnotationDefault(boolean b) {
        this.hasDefault = b;
    }

    public boolean hasAnnotationDefault() {
        return this.hasDefault;
    }

    public boolean isStaticConstructor() {
        return this.staticConstructor;
    }

    public boolean isSyntheticPublic() {
        return this.syntheticPublic;
    }

    public void setSyntheticPublic(boolean syntheticPublic) {
        this.syntheticPublic = syntheticPublic;
    }

    @Override
    public String getText() {
        String retType = AstToTextHelper.getClassText(this.returnType);
        String exceptionTypes = AstToTextHelper.getThrowsClauseText(this.exceptions);
        String parms = AstToTextHelper.getParametersText(this.parameters);
        return AstToTextHelper.getModifiersText(this.modifiers) + " " + retType + " " + this.name + "(" + parms + ") " + exceptionTypes + " { ... }";
    }
}


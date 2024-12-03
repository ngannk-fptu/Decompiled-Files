/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Assignment;
import freemarker.core.BugException;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.InvalidReferenceException;
import freemarker.core.MarkupOutputFormat;
import freemarker.core.NonNamespaceException;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.template.SimpleScalar;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.io.IOException;
import java.io.StringWriter;

final class BlockAssignment
extends TemplateElement {
    private final String varName;
    private final Expression namespaceExp;
    private final int scope;
    private final MarkupOutputFormat<?> markupOutputFormat;

    BlockAssignment(TemplateElements children, String varName, int scope, Expression namespaceExp, MarkupOutputFormat<?> markupOutputFormat) {
        this.setChildren(children);
        this.varName = varName;
        this.namespaceExp = namespaceExp;
        this.scope = scope;
        this.markupOutputFormat = markupOutputFormat;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        TemplateModel value;
        TemplateElement[] children = this.getChildBuffer();
        if (children != null) {
            StringWriter out = new StringWriter();
            env.visit(children, out);
            value = this.capturedStringToModel(out.toString());
        } else {
            value = this.capturedStringToModel("");
        }
        if (this.namespaceExp != null) {
            Environment.Namespace namespace;
            TemplateModel uncheckedNamespace = this.namespaceExp.eval(env);
            try {
                namespace = (Environment.Namespace)uncheckedNamespace;
            }
            catch (ClassCastException e) {
                throw new NonNamespaceException(this.namespaceExp, uncheckedNamespace, env);
            }
            if (namespace == null) {
                throw InvalidReferenceException.getInstance(this.namespaceExp, env);
            }
            namespace.put(this.varName, value);
        } else if (this.scope == 1) {
            env.setVariable(this.varName, value);
        } else if (this.scope == 3) {
            env.setGlobalVariable(this.varName, value);
        } else if (this.scope == 2) {
            env.setLocalVariable(this.varName, value);
        } else {
            throw new BugException("Unhandled scope");
        }
        return null;
    }

    private TemplateModel capturedStringToModel(String s) throws TemplateModelException {
        return this.markupOutputFormat == null ? new SimpleScalar(s) : this.markupOutputFormat.fromMarkup(s);
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append("<");
        }
        sb.append(this.getNodeTypeSymbol());
        sb.append(' ');
        sb.append(this.varName);
        if (this.namespaceExp != null) {
            sb.append(" in ");
            sb.append(this.namespaceExp.getCanonicalForm());
        }
        if (canonical) {
            sb.append('>');
            sb.append(this.getChildrenCanonicalForm());
            sb.append("</");
            sb.append(this.getNodeTypeSymbol());
            sb.append('>');
        } else {
            sb.append(" = .nested_output");
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return Assignment.getDirectiveName(this.scope);
    }

    @Override
    int getParameterCount() {
        return 3;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.varName;
            }
            case 1: {
                return this.scope;
            }
            case 2: {
                return this.namespaceExp;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.ASSIGNMENT_TARGET;
            }
            case 1: {
                return ParameterRole.VARIABLE_SCOPE;
            }
            case 2: {
                return ParameterRole.NAMESPACE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}


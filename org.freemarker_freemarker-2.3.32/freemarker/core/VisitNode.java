/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ListLiteral;
import freemarker.core.NonNodeException;
import freemarker.core.NonSequenceException;
import freemarker.core.ParameterRole;
import freemarker.core.StringLiteral;
import freemarker.core.TemplateElement;
import freemarker.core._MiscTemplateException;
import freemarker.template.ObjectWrapper;
import freemarker.template.SimpleSequence;
import freemarker.template.TemplateException;
import freemarker.template.TemplateModel;
import freemarker.template.TemplateNodeModel;
import freemarker.template.TemplateScalarModel;
import freemarker.template.TemplateSequenceModel;
import freemarker.template._ObjectWrappers;
import java.io.IOException;

final class VisitNode
extends TemplateElement {
    Expression targetNode;
    Expression namespaces;

    VisitNode(Expression targetNode, Expression namespaces) {
        this.targetNode = targetNode;
        this.namespaces = namespaces;
    }

    @Override
    TemplateElement[] accept(Environment env) throws IOException, TemplateException {
        TemplateModel nss;
        TemplateModel node = this.targetNode.eval(env);
        if (!(node instanceof TemplateNodeModel)) {
            throw new NonNodeException(this.targetNode, node, env);
        }
        TemplateModel templateModel = nss = this.namespaces == null ? null : this.namespaces.eval(env);
        if (this.namespaces instanceof StringLiteral) {
            nss = env.importLib(((TemplateScalarModel)nss).getAsString(), null);
        } else if (this.namespaces instanceof ListLiteral) {
            nss = ((ListLiteral)this.namespaces).evaluateStringsToNamespaces(env);
        }
        if (nss != null) {
            if (nss instanceof Environment.Namespace) {
                SimpleSequence ss = new SimpleSequence(1, (ObjectWrapper)_ObjectWrappers.SAFE_OBJECT_WRAPPER);
                ss.add(nss);
                nss = ss;
            } else if (!(nss instanceof TemplateSequenceModel)) {
                if (this.namespaces != null) {
                    throw new NonSequenceException(this.namespaces, nss, env);
                }
                throw new _MiscTemplateException(env, "Expecting a sequence of namespaces after \"using\"");
            }
        }
        env.invokeNodeHandlerFor((TemplateNodeModel)node, (TemplateSequenceModel)nss);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        sb.append(' ');
        sb.append(this.targetNode.getCanonicalForm());
        if (this.namespaces != null) {
            sb.append(" using ");
            sb.append(this.namespaces.getCanonicalForm());
        }
        if (canonical) {
            sb.append("/>");
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#visit";
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.targetNode;
            }
            case 1: {
                return this.namespaces;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.NODE;
            }
            case 1: {
                return ParameterRole.NAMESPACE;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    boolean isNestedBlockRepeater() {
        return true;
    }

    @Override
    boolean isShownInStackTrace() {
        return true;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Assignment;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;
import java.io.IOException;

final class AssignmentInstruction
extends TemplateElement {
    private int scope;
    private Expression namespaceExp;

    AssignmentInstruction(int scope) {
        this.scope = scope;
        this.setChildBufferCapacity(1);
    }

    void addAssignment(Assignment assignment) {
        this.addChild(assignment);
    }

    void setNamespaceExp(Expression namespaceExp) {
        this.namespaceExp = namespaceExp;
        int ln = this.getChildCount();
        for (int i = 0; i < ln; ++i) {
            ((Assignment)this.getChild(i)).setNamespaceExp(namespaceExp);
        }
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        return this.getChildBuffer();
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(Assignment.getDirectiveName(this.scope));
        if (canonical) {
            buf.append(' ');
            int ln = this.getChildCount();
            for (int i = 0; i < ln; ++i) {
                if (i != 0) {
                    buf.append(", ");
                }
                Assignment assignment = (Assignment)this.getChild(i);
                buf.append(assignment.getCanonicalForm());
            }
        } else {
            buf.append("-container");
        }
        if (this.namespaceExp != null) {
            buf.append(" in ");
            buf.append(this.namespaceExp.getCanonicalForm());
        }
        if (canonical) {
            buf.append(">");
        }
        return buf.toString();
    }

    @Override
    int getParameterCount() {
        return 2;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                return this.scope;
            }
            case 1: {
                return this.namespaceExp;
            }
        }
        return null;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                return ParameterRole.VARIABLE_SCOPE;
            }
            case 1: {
                return ParameterRole.NAMESPACE;
            }
        }
        return null;
    }

    @Override
    String getNodeTypeSymbol() {
        return Assignment.getDirectiveName(this.scope);
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}


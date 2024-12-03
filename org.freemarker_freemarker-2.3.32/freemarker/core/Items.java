/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.IteratorBlock;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.core._CoreStringUtils;
import freemarker.core._MiscTemplateException;
import freemarker.template.TemplateException;
import java.io.IOException;

class Items
extends TemplateElement {
    private final String loopVarName;
    private final String loopVar2Name;

    Items(String loopVarName, String loopVar2Name, TemplateElements children) {
        this.loopVarName = loopVarName;
        this.loopVar2Name = loopVar2Name;
        this.setChildren(children);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        IteratorBlock.IterationContext iterCtx = env.findClosestEnclosingIterationContext();
        if (iterCtx == null) {
            throw new _MiscTemplateException(env, this.getNodeTypeSymbol(), " without iteration in context");
        }
        iterCtx.loopForItemsElement(env, this.getChildBuffer(), this.loopVarName, this.loopVar2Name);
        return null;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return true;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder sb = new StringBuilder();
        if (canonical) {
            sb.append('<');
        }
        sb.append(this.getNodeTypeSymbol());
        sb.append(" as ");
        sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVarName));
        if (this.loopVar2Name != null) {
            sb.append(", ");
            sb.append(_CoreStringUtils.toFTLTopLevelIdentifierReference(this.loopVar2Name));
        }
        if (canonical) {
            sb.append('>');
            sb.append(this.getChildrenCanonicalForm());
            sb.append("</");
            sb.append(this.getNodeTypeSymbol());
            sb.append('>');
        }
        return sb.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#items";
    }

    @Override
    int getParameterCount() {
        return this.loopVar2Name != null ? 2 : 1;
    }

    @Override
    Object getParameterValue(int idx) {
        switch (idx) {
            case 0: {
                if (this.loopVarName == null) {
                    throw new IndexOutOfBoundsException();
                }
                return this.loopVarName;
            }
            case 1: {
                if (this.loopVar2Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return this.loopVar2Name;
            }
        }
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        switch (idx) {
            case 0: {
                if (this.loopVarName == null) {
                    throw new IndexOutOfBoundsException();
                }
                return ParameterRole.TARGET_LOOP_VARIABLE;
            }
            case 1: {
                if (this.loopVar2Name == null) {
                    throw new IndexOutOfBoundsException();
                }
                return ParameterRole.TARGET_LOOP_VARIABLE;
            }
        }
        throw new IndexOutOfBoundsException();
    }
}


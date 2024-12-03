/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.Environment;
import freemarker.core.ParameterRole;
import freemarker.core.RecoveryBlock;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateElements;
import freemarker.template.TemplateException;
import java.io.IOException;

final class AttemptBlock
extends TemplateElement {
    private TemplateElement attemptedSection;
    private RecoveryBlock recoverySection;

    AttemptBlock(TemplateElements attemptedSectionChildren, RecoveryBlock recoverySection) {
        TemplateElement attemptedSection;
        this.attemptedSection = attemptedSection = attemptedSectionChildren.asSingleElement();
        this.recoverySection = recoverySection;
        this.setChildBufferCapacity(2);
        this.addChild(attemptedSection);
        this.addChild(recoverySection);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        env.visitAttemptRecover(this, this.attemptedSection, this.recoverySection);
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        if (!canonical) {
            return this.getNodeTypeSymbol();
        }
        StringBuilder buf = new StringBuilder();
        buf.append("<").append(this.getNodeTypeSymbol()).append(">");
        buf.append(this.getChildrenCanonicalForm());
        buf.append("</").append(this.getNodeTypeSymbol()).append(">");
        return buf.toString();
    }

    @Override
    int getParameterCount() {
        return 1;
    }

    @Override
    Object getParameterValue(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return this.recoverySection;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.ERROR_HANDLER;
    }

    @Override
    String getNodeTypeSymbol() {
        return "#attempt";
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}


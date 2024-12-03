/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.BreakOrContinueException;
import freemarker.core.Case;
import freemarker.core.Environment;
import freemarker.core.EvalUtil;
import freemarker.core.Expression;
import freemarker.core.MixedContent;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;
import java.io.IOException;

final class SwitchBlock
extends TemplateElement {
    private Case defaultCase;
    private final Expression searched;
    private int firstCaseIndex;

    SwitchBlock(Expression searched, MixedContent ignoredSectionBeforeFirstCase) {
        this.searched = searched;
        int ignoredCnt = ignoredSectionBeforeFirstCase != null ? ignoredSectionBeforeFirstCase.getChildCount() : 0;
        this.setChildBufferCapacity(ignoredCnt + 4);
        for (int i = 0; i < ignoredCnt; ++i) {
            this.addChild(ignoredSectionBeforeFirstCase.getChild(i));
        }
        this.firstCaseIndex = ignoredCnt;
    }

    void addCase(Case cas) {
        if (cas.condition == null) {
            this.defaultCase = cas;
        }
        this.addChild(cas);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        boolean processedCase = false;
        int ln = this.getChildCount();
        try {
            for (int i = this.firstCaseIndex; i < ln; ++i) {
                Case cas = (Case)this.getChild(i);
                boolean processCase = false;
                if (processedCase) {
                    processCase = true;
                } else if (cas.condition != null) {
                    processCase = EvalUtil.compare(this.searched, 1, "case==", cas.condition, cas.condition, env);
                }
                if (!processCase) continue;
                env.visit(cas);
                processedCase = true;
            }
            if (!processedCase && this.defaultCase != null) {
                env.visit(this.defaultCase);
            }
        }
        catch (BreakOrContinueException breakOrContinueException) {
            // empty catch block
        }
        return null;
    }

    @Override
    protected String dump(boolean canonical) {
        StringBuilder buf = new StringBuilder();
        if (canonical) {
            buf.append('<');
        }
        buf.append(this.getNodeTypeSymbol());
        buf.append(' ');
        buf.append(this.searched.getCanonicalForm());
        if (canonical) {
            buf.append('>');
            int ln = this.getChildCount();
            for (int i = 0; i < ln; ++i) {
                buf.append(this.getChild(i).getCanonicalForm());
            }
            buf.append("</").append(this.getNodeTypeSymbol()).append('>');
        }
        return buf.toString();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#switch";
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
        return this.searched;
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        if (idx != 0) {
            throw new IndexOutOfBoundsException();
        }
        return ParameterRole.VALUE;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override
    TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
        int i;
        TemplateElement result = super.postParseCleanup(stripWhitespace);
        int ln = this.getChildCount();
        for (i = 0; i < ln && !(this.getChild(i) instanceof Case); ++i) {
        }
        this.firstCaseIndex = i;
        return result;
    }
}


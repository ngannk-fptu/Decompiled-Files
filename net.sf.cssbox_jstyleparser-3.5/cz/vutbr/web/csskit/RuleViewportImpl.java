/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleViewport;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;

public class RuleViewportImpl
extends AbstractRuleBlock<Declaration>
implements RuleViewport {
    protected RuleViewportImpl() {
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("@viewport").append(" ");
        sb.append(" {\n");
        sb = OutputUtil.appendList(sb, this.list, "", depth + 1);
        sb.append("}\n");
        return sb.toString();
    }
}


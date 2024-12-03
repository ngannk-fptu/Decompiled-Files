/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.RuleKeyframes;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;

public class RuleKeyframesImpl
extends AbstractRuleBlock<KeyframeBlock>
implements RuleKeyframes {
    private String name;

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RuleKeyframes setName(String name) {
        this.name = new String(name);
        return this;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb.append("@keyframes ");
        sb.append(this.name);
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb.append(" {\n");
        sb = OutputUtil.appendList(sb, this.list, "\n", depth + 1);
        sb.append("}\n");
        return sb.toString();
    }
}


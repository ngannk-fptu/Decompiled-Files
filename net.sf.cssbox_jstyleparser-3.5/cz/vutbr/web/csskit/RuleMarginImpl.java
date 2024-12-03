/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleMargin;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;

public class RuleMarginImpl
extends AbstractRuleBlock<Declaration>
implements RuleMargin {
    private RuleMargin.MarginArea marginArea;

    protected RuleMarginImpl(String area) {
        for (RuleMargin.MarginArea a : RuleMargin.MarginArea.values()) {
            if (!a.value.equals(area)) continue;
            this.marginArea = a;
            break;
        }
        if (this.marginArea == null) {
            throw new IllegalArgumentException("Illegal value for margin area: " + area);
        }
    }

    @Override
    public RuleMargin.MarginArea getMarginArea() {
        return this.marginArea;
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb.append("@").append(this.marginArea.value);
        sb.append(" {\n");
        sb = OutputUtil.appendList(sb, this.list, "\n", depth + 1);
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.marginArea == null ? 0 : this.marginArea.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!super.equals(obj)) {
            return false;
        }
        if (!(obj instanceof RuleMarginImpl)) {
            return false;
        }
        RuleMarginImpl other = (RuleMarginImpl)obj;
        return !(this.marginArea == null ? other.marginArea != null : !this.marginArea.equals((Object)other.marginArea));
    }
}


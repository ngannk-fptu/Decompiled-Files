/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.TermPercent;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.ArrayList;
import java.util.List;

public class KeyframeBlockImpl
extends AbstractRuleBlock<Declaration>
implements KeyframeBlock {
    private List<TermPercent> percentages;

    public KeyframeBlockImpl() {
        this.percentages = new ArrayList<TermPercent>();
    }

    public KeyframeBlockImpl(List<TermPercent> percentages) {
        percentages = new ArrayList<TermPercent>(percentages);
    }

    @Override
    public List<TermPercent> getPercentages() {
        return this.percentages;
    }

    @Override
    public KeyframeBlock setPercentages(List<TermPercent> percentages) {
        this.percentages = new ArrayList<TermPercent>(percentages);
        return this;
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb = OutputUtil.appendList(sb, this.percentages, ", ");
        sb.append(" {\n");
        sb = OutputUtil.appendList(sb, this.list, "", depth + 1);
        sb = OutputUtil.appendTimes(sb, "\t", depth);
        sb.append("}\n");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.percentages == null ? 0 : this.percentages.hashCode());
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
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        KeyframeBlockImpl other = (KeyframeBlockImpl)obj;
        return !(this.percentages == null ? other.percentages != null : !this.percentages.equals(other.percentages));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.RuleSet;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.List;

public class RuleSetImpl
extends AbstractRuleBlock<Declaration>
implements RuleSet {
    protected CombinedSelector[] selectors;

    protected RuleSetImpl() {
        this.selectors = new CombinedSelector[0];
    }

    public RuleSetImpl(CombinedSelector[] selectors) {
        this.selectors = selectors;
    }

    protected RuleSetImpl(RuleSet rs) {
        this.selectors = rs.getSelectors();
        this.replaceAll(rs.asList());
    }

    @Override
    public CombinedSelector[] getSelectors() {
        return this.selectors;
    }

    @Override
    public RuleSet setSelectors(List<CombinedSelector> selectors) {
        this.selectors = selectors.toArray(new CombinedSelector[selectors.size()]);
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
        sb = OutputUtil.appendArray(sb, this.selectors, ", ");
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
        result = 31 * result + (this.selectors == null ? 0 : this.selectors.hashCode());
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
        if (!(obj instanceof RuleSetImpl)) {
            return false;
        }
        RuleSetImpl other = (RuleSetImpl)obj;
        return !(this.selectors == null ? other.selectors != null : !this.selectors.equals(other.selectors));
    }
}


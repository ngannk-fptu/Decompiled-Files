/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.csskit;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.RuleMargin;
import cz.vutbr.web.css.RulePage;
import cz.vutbr.web.css.Selector;
import cz.vutbr.web.csskit.AbstractRuleBlock;
import cz.vutbr.web.csskit.OutputUtil;
import java.util.ArrayList;
import java.util.List;

public class RulePageImpl
extends AbstractRuleBlock<Rule<?>>
implements RulePage {
    protected String name = null;
    protected Selector.PseudoPage pseudo = null;

    protected RulePageImpl() {
        this.replaceAll(new ArrayList());
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public RulePage setName(String name) {
        this.name = name;
        return this;
    }

    @Override
    public Selector.PseudoPage getPseudo() {
        return this.pseudo;
    }

    @Override
    public RulePage setPseudo(Selector.PseudoPage pseudo) {
        this.pseudo = pseudo;
        return this;
    }

    @Override
    public boolean add(Rule<?> element) {
        if (element instanceof Declaration || element instanceof RuleMargin) {
            return super.add(element);
        }
        throw new IllegalArgumentException("Element must be either a Declaration or a RuleMargin");
    }

    @Override
    public String toString() {
        return this.toString(0);
    }

    @Override
    public String toString(int depth) {
        StringBuilder sb = new StringBuilder();
        sb.append("@page");
        if (this.name != null && !"".equals(this.name)) {
            sb.append(" ").append(this.name);
        }
        if (this.pseudo != null) {
            sb.append(this.pseudo.toString());
        }
        sb.append(" {\n");
        List rules = this.list;
        sb = OutputUtil.appendList(sb, rules, "", depth + 1);
        sb.append("}\n").append("");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        int prime = 31;
        int result = super.hashCode();
        result = 31 * result + (this.pseudo == null ? 0 : this.pseudo.hashCode());
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
        if (!(obj instanceof RulePageImpl)) {
            return false;
        }
        RulePageImpl other = (RulePageImpl)obj;
        return !(this.pseudo == null ? other.pseudo != null : !this.pseudo.equals(other.pseudo));
    }
}


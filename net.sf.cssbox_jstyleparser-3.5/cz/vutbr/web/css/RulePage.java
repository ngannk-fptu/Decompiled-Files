/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.Rule;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.Selector;

public interface RulePage
extends RuleBlock<Rule<?>>,
PrettyOutput {
    public String getName();

    public RulePage setName(String var1);

    public Selector.PseudoPage getPseudo();

    public RulePage setPseudo(Selector.PseudoPage var1);
}


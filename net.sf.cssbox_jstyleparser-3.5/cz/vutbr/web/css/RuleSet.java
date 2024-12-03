/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.CombinedSelector;
import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.RuleBlock;
import java.util.List;

public interface RuleSet
extends RuleBlock<Declaration>,
PrettyOutput {
    public CombinedSelector[] getSelectors();

    public RuleSet setSelectors(List<CombinedSelector> var1);
}


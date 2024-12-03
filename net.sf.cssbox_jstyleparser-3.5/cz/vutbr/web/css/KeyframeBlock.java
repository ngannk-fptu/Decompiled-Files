/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.Declaration;
import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.TermPercent;
import java.util.List;

public interface KeyframeBlock
extends RuleBlock<Declaration>,
PrettyOutput {
    public List<TermPercent> getPercentages();

    public KeyframeBlock setPercentages(List<TermPercent> var1);
}


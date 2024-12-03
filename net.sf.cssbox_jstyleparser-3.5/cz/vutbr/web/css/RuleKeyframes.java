/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.KeyframeBlock;
import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.RuleBlock;

public interface RuleKeyframes
extends RuleBlock<KeyframeBlock>,
PrettyOutput {
    public String getName();

    public RuleKeyframes setName(String var1);
}


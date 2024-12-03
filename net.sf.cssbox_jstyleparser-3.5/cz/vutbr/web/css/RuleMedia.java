/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.MediaQuery;
import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.RuleBlock;
import cz.vutbr.web.css.RuleSet;
import java.util.List;

public interface RuleMedia
extends RuleBlock<RuleSet>,
PrettyOutput {
    public List<MediaQuery> getMediaQueries();

    public RuleMedia setMediaQueries(List<MediaQuery> var1);
}


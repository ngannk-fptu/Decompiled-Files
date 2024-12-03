/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.css;

import cz.vutbr.web.css.PrettyOutput;
import cz.vutbr.web.css.RuleBlock;

@Deprecated
public interface RuleImport
extends RuleBlock<String>,
PrettyOutput {
    public String getURI();

    public RuleImport setURI(String var1);
}


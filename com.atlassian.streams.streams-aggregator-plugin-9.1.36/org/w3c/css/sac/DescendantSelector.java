/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

public interface DescendantSelector
extends Selector {
    public Selector getAncestorSelector();

    public SimpleSelector getSimpleSelector();
}


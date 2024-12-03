/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.css.sac.Selector;
import org.w3c.dom.Element;

public interface ExtendedSelector
extends Selector {
    public boolean match(Element var1, String var2);

    public int getSpecificity();

    public void fillAttributeSet(Set var1);
}


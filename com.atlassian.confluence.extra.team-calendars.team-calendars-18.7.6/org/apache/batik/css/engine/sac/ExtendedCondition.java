/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.w3c.css.sac.Condition
 */
package org.apache.batik.css.engine.sac;

import java.util.Set;
import org.w3c.css.sac.Condition;
import org.w3c.dom.Element;

public interface ExtendedCondition
extends Condition {
    public boolean match(Element var1, String var2);

    public int getSpecificity();

    public void fillAttributeSet(Set var1);
}


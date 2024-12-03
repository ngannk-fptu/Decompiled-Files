/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.Selector;
import org.w3c.css.sac.SimpleSelector;

public interface SiblingSelector
extends Selector {
    public static final short ANY_NODE = 201;

    public short getNodeType();

    public Selector getSelector();

    public SimpleSelector getSiblingSelector();
}


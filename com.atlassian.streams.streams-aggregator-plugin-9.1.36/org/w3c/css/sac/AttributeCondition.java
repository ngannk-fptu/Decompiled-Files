/*
 * Decompiled with CFR 0.152.
 */
package org.w3c.css.sac;

import org.w3c.css.sac.Condition;

public interface AttributeCondition
extends Condition {
    public String getNamespaceURI();

    public String getLocalName();

    public boolean getSpecified();

    public String getValue();
}


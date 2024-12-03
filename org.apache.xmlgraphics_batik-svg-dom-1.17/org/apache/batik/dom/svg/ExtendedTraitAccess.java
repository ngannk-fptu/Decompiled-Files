/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.dom.svg;

import org.apache.batik.dom.svg.TraitAccess;

public interface ExtendedTraitAccess
extends TraitAccess {
    public boolean hasProperty(String var1);

    public boolean hasTrait(String var1, String var2);

    public boolean isPropertyAnimatable(String var1);

    public boolean isAttributeAnimatable(String var1, String var2);

    public boolean isPropertyAdditive(String var1);

    public boolean isAttributeAdditive(String var1, String var2);

    public boolean isTraitAnimatable(String var1, String var2);

    public boolean isTraitAdditive(String var1, String var2);

    public int getPropertyType(String var1);

    public int getAttributeType(String var1, String var2);
}


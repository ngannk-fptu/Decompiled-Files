/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import java.util.List;
import org.apache.batik.ext.awt.image.CompositeRule;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface CompositeRable
extends FilterColorInterpolation {
    public void setSources(List var1);

    public void setCompositeRule(CompositeRule var1);

    public CompositeRule getCompositeRule();
}


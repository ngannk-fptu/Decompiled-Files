/*
 * Decompiled with CFR 0.152.
 */
package org.apache.batik.ext.awt.image.renderable;

import org.apache.batik.ext.awt.image.ComponentTransferFunction;
import org.apache.batik.ext.awt.image.renderable.Filter;
import org.apache.batik.ext.awt.image.renderable.FilterColorInterpolation;

public interface ComponentTransferRable
extends FilterColorInterpolation {
    public Filter getSource();

    public void setSource(Filter var1);

    public ComponentTransferFunction getAlphaFunction();

    public void setAlphaFunction(ComponentTransferFunction var1);

    public ComponentTransferFunction getRedFunction();

    public void setRedFunction(ComponentTransferFunction var1);

    public ComponentTransferFunction getGreenFunction();

    public void setGreenFunction(ComponentTransferFunction var1);

    public ComponentTransferFunction getBlueFunction();

    public void setBlueFunction(ComponentTransferFunction var1);
}


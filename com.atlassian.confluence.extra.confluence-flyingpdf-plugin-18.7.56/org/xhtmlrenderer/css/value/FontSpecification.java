/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.css.value;

import java.util.Arrays;
import org.xhtmlrenderer.css.constants.IdentValue;

public class FontSpecification {
    public float size;
    public IdentValue fontWeight;
    public String[] families;
    public IdentValue fontStyle;
    public IdentValue variant;

    public String toString() {
        StringBuffer sb = new StringBuffer("Font specification: ");
        sb.append(" families: " + Arrays.asList(this.families).toString()).append(" size: " + this.size).append(" weight: " + this.fontWeight).append(" style: " + this.fontStyle).append(" variant: " + this.variant);
        return sb.toString();
    }
}


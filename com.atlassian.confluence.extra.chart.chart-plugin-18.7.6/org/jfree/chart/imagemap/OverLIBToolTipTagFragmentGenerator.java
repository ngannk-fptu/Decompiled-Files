/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.imagemap;

import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;

public class OverLIBToolTipTagFragmentGenerator
implements ToolTipTagFragmentGenerator {
    public String generateToolTipFragment(String toolTipText) {
        return " onMouseOver=\"return overlib('" + ImageMapUtilities.javascriptEscape(toolTipText) + "');\" onMouseOut=\"return nd();\"";
    }
}


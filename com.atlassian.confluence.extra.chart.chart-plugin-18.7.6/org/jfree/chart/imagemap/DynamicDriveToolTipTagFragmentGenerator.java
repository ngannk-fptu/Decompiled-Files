/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.imagemap;

import org.jfree.chart.imagemap.ImageMapUtilities;
import org.jfree.chart.imagemap.ToolTipTagFragmentGenerator;

public class DynamicDriveToolTipTagFragmentGenerator
implements ToolTipTagFragmentGenerator {
    protected String title = "";
    protected int style = 1;

    public DynamicDriveToolTipTagFragmentGenerator() {
    }

    public DynamicDriveToolTipTagFragmentGenerator(String title, int style) {
        this.title = title;
        this.style = style;
    }

    public String generateToolTipFragment(String toolTipText) {
        return " onMouseOver=\"return stm(['" + ImageMapUtilities.javascriptEscape(this.title) + "','" + ImageMapUtilities.javascriptEscape(toolTipText) + "'],Style[" + this.style + "]);\"" + " onMouseOut=\"return htm();\"";
    }
}


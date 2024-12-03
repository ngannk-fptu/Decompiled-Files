/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.event.MouseEvent;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.swing.BasicPanel;

public interface FSMouseListener {
    public void onMouseOver(BasicPanel var1, Box var2);

    public void onMouseOut(BasicPanel var1, Box var2);

    public void onMouseUp(BasicPanel var1, Box var2);

    public void onMousePressed(BasicPanel var1, MouseEvent var2);

    public void onMouseDragged(BasicPanel var1, MouseEvent var2);

    public void reset();
}


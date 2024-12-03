/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Cursor;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.DefaultFSMouseListener;

public class CursorListener
extends DefaultFSMouseListener {
    @Override
    public void onMouseOver(BasicPanel panel, Box box) {
        Cursor c = box.getStyle().getCursor();
        if (!panel.getCursor().equals(c)) {
            panel.setCursor(c);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Rectangle;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.layout.PaintingInfo;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.DefaultFSMouseListener;

public class HoverListener
extends DefaultFSMouseListener {
    private Box _previouslyHovered;

    @Override
    public void onMouseOut(BasicPanel panel, Box box) {
    }

    @Override
    public void onMouseOver(BasicPanel panel, Box box) {
        LayoutContext c = panel.getLayoutContext();
        if (c == null) {
            return;
        }
        boolean needRepaint = false;
        Element currentlyHovered = this.getHoveredElement(c.getCss(), box);
        if (currentlyHovered == panel.hovered_element) {
            return;
        }
        panel.hovered_element = currentlyHovered;
        boolean targetedRepaint = true;
        Rectangle repaintRegion = null;
        if (this._previouslyHovered != null) {
            needRepaint = true;
            this._previouslyHovered.restyle(c);
            PaintingInfo paintInfo = this._previouslyHovered.getPaintingInfo();
            if (paintInfo == null) {
                targetedRepaint = false;
            } else {
                repaintRegion = new Rectangle(paintInfo.getAggregateBounds());
            }
            this._previouslyHovered = null;
        }
        if (currentlyHovered != null) {
            needRepaint = true;
            Box target = box.getRestyleTarget();
            target.restyle(c);
            if (targetedRepaint) {
                PaintingInfo paintInfo = target.getPaintingInfo();
                if (paintInfo == null) {
                    targetedRepaint = false;
                } else if (repaintRegion == null) {
                    repaintRegion = new Rectangle(paintInfo.getAggregateBounds());
                } else {
                    repaintRegion.add(paintInfo.getAggregateBounds());
                }
            }
            this._previouslyHovered = target;
        }
        if (needRepaint) {
            if (targetedRepaint) {
                panel.repaint(repaintRegion);
            } else {
                panel.repaint();
            }
        }
    }

    private Element getHoveredElement(StyleReference style, Box ib) {
        if (ib == null) {
            return null;
        }
        Element element = ib.getElement();
        while (element != null && !style.isHoverStyled(element)) {
            Node node = element.getParentNode();
            if (node.getNodeType() == 1) {
                element = (Element)node;
                continue;
            }
            element = null;
        }
        return element;
    }

    @Override
    public void reset() {
        this._previouslyHovered = null;
    }
}


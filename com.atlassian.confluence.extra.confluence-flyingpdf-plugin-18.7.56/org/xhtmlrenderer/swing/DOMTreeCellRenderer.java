/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.Component;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.w3c.dom.Node;

class DOMTreeCellRenderer
extends DefaultTreeCellRenderer {
    private static final long serialVersionUID = 1L;

    DOMTreeCellRenderer() {
    }

    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        Node node = (Node)value;
        if (node.getNodeType() == 1) {
            Node cn;
            String cls = "";
            if (node.hasAttributes() && (cn = node.getAttributes().getNamedItem("class")) != null) {
                cls = " class='" + cn.getNodeValue() + "'";
            }
            value = "<" + node.getNodeName() + cls + ">";
        }
        if (node.getNodeType() == 3 && node.getNodeValue().trim().length() > 0) {
            value = "\"" + node.getNodeValue() + "\"";
        }
        if (node.getNodeType() == 8) {
            value = "<!-- " + node.getNodeValue() + " -->";
        }
        DefaultTreeCellRenderer tcr = (DefaultTreeCellRenderer)super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
        tcr.setOpenIcon(null);
        tcr.setClosedIcon(null);
        return super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
    }
}


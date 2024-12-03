/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import javax.swing.JTree;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.w3c.dom.Node;
import org.xhtmlrenderer.swing.ElementPropertiesPanel;

class DOMSelectionListener
implements TreeSelectionListener {
    private JTree _tree;
    private ElementPropertiesPanel _elemPropPanel;

    DOMSelectionListener(JTree tree, ElementPropertiesPanel panel) {
        this._tree = tree;
        this._elemPropPanel = panel;
    }

    @Override
    public void valueChanged(TreeSelectionEvent e) {
        Node node = (Node)this._tree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        this._elemPropPanel.setForElement(node);
    }
}


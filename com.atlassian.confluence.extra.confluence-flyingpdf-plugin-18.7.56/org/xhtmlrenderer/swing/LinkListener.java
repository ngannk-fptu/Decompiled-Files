/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xhtmlrenderer.render.Box;
import org.xhtmlrenderer.swing.BasicPanel;
import org.xhtmlrenderer.swing.DefaultFSMouseListener;

public class LinkListener
extends DefaultFSMouseListener {
    public void linkClicked(BasicPanel panel, String uri) {
        panel.setDocumentRelative(uri);
    }

    @Override
    public void onMouseUp(BasicPanel panel, Box box) {
        this.checkForLink(panel, box);
    }

    private void checkForLink(BasicPanel panel, Box box) {
        if (box == null || box.getElement() == null) {
            return;
        }
        String uri = this.findLink(panel, box.getElement());
        if (uri != null) {
            this.linkClicked(panel, uri);
        }
    }

    private String findLink(BasicPanel panel, Element e) {
        String uri = null;
        Node node = e;
        while (node.getNodeType() == 1 && (uri = panel.getSharedContext().getNamespaceHandler().getLinkUri((Element)node)) == null) {
            node = node.getParentNode();
        }
        return uri;
    }
}


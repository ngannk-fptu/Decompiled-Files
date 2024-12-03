/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

class DOMTreeModel
implements TreeModel {
    Document doc;
    Node root;
    HashMap displayableNodes;
    List listeners = new ArrayList();

    public DOMTreeModel(Document doc) {
        this.displayableNodes = new HashMap();
        this.doc = doc;
        this.setRoot("body");
    }

    private void setRoot(String rootNodeName) {
        Element tempRoot = this.doc.getDocumentElement();
        NodeList nl = tempRoot.getChildNodes();
        for (int i = 0; i < nl.getLength(); ++i) {
            if (!nl.item(i).getNodeName().toLowerCase().equals(rootNodeName)) continue;
            this.root = nl.item(i);
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener l) {
        this.listeners.add(l);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener l) {
        this.listeners.remove(l);
    }

    @Override
    public void valueForPathChanged(TreePath path, Object newValue) {
    }

    @Override
    public Object getChild(Object parent, int index) {
        Node node = (Node)parent;
        List children = (List)this.displayableNodes.get(parent);
        if (children == null) {
            children = this.addDisplayable(node);
        }
        return (Node)children.get(index);
    }

    @Override
    public int getChildCount(Object parent) {
        Node node = (Node)parent;
        List children = (List)this.displayableNodes.get(parent);
        if (children == null) {
            children = this.addDisplayable(node);
        }
        return children.size();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        Node node = (Node)parent;
        List children = (List)this.displayableNodes.get(parent);
        if (children == null) {
            children = this.addDisplayable(node);
        }
        if (children.contains(child)) {
            return children.indexOf(child);
        }
        return -1;
    }

    @Override
    public Object getRoot() {
        return this.root;
    }

    @Override
    public boolean isLeaf(Object nd) {
        Node node = (Node)nd;
        return !node.hasChildNodes();
    }

    private List addDisplayable(Node parent) {
        ArrayList<Node> children = (ArrayList<Node>)this.displayableNodes.get(parent);
        if (children == null) {
            children = new ArrayList<Node>();
            this.displayableNodes.put(parent, children);
            NodeList nl = parent.getChildNodes();
            int len = nl.getLength();
            for (int i = 0; i < len; ++i) {
                Node child = nl.item(i);
                if (child.getNodeType() != 1 && child.getNodeType() != 8 && (child.getNodeType() != 3 || child.getNodeValue().trim().length() <= 0)) continue;
                children.add(child);
            }
            return children;
        }
        return new ArrayList();
    }
}


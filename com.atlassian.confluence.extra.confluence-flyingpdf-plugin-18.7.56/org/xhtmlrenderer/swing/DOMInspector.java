/*
 * Decompiled with CFR 0.152.
 */
package org.xhtmlrenderer.swing;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTree;
import org.w3c.dom.Document;
import org.xhtmlrenderer.context.StyleReference;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.swing.DOMSelectionListener;
import org.xhtmlrenderer.swing.DOMTreeCellRenderer;
import org.xhtmlrenderer.swing.DOMTreeModel;
import org.xhtmlrenderer.swing.ElementPropertiesPanel;

public class DOMInspector
extends JPanel {
    private static final long serialVersionUID = 1L;
    StyleReference styleReference;
    SharedContext context;
    ElementPropertiesPanel elementPropPanel;
    DOMSelectionListener nodeSelectionListener;
    JSplitPane splitPane;
    Document doc;
    JButton close;
    JTree tree;
    JScrollPane scroll;

    public DOMInspector(Document doc) {
        this(doc, null, null);
    }

    public DOMInspector(Document doc, SharedContext context, StyleReference sr) {
        this.setLayout(new BorderLayout());
        this.tree = new JTree();
        this.tree.getSelectionModel().setSelectionMode(1);
        this.scroll = new JScrollPane(this.tree);
        this.splitPane = null;
        if (sr == null) {
            this.add((Component)this.scroll, "Center");
        } else {
            this.splitPane = new JSplitPane(1);
            this.splitPane.setOneTouchExpandable(true);
            this.splitPane.setDividerLocation(150);
            this.add((Component)this.splitPane, "Center");
            this.splitPane.setLeftComponent(this.scroll);
        }
        this.close = new JButton("close");
        this.add((Component)this.close, "South");
        this.setPreferredSize(new Dimension(300, 300));
        this.setForDocument(doc, context, sr);
        this.close.addActionListener(new ActionListener(){

            @Override
            public void actionPerformed(ActionEvent evt) {
                DOMInspector.this.getFrame(DOMInspector.this).setVisible(false);
            }
        });
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(0, 0, 100, 100);
    }

    public void setForDocument(Document doc) {
        this.setForDocument(doc, null, null);
    }

    public void setForDocument(Document doc, SharedContext context, StyleReference sr) {
        this.doc = doc;
        this.styleReference = sr;
        this.context = context;
        this.initForCurrentDocument();
    }

    public JFrame getFrame(Component comp) {
        if (comp instanceof JFrame) {
            return (JFrame)comp;
        }
        return this.getFrame(comp.getParent());
    }

    private void initForCurrentDocument() {
        DOMTreeModel model = new DOMTreeModel(this.doc);
        this.tree.setModel(model);
        if (!(this.tree.getCellRenderer() instanceof DOMTreeCellRenderer)) {
            this.tree.setCellRenderer(new DOMTreeCellRenderer());
        }
        if (this.styleReference != null) {
            if (this.elementPropPanel != null) {
                this.splitPane.remove(this.elementPropPanel);
            }
            this.elementPropPanel = new ElementPropertiesPanel(this.styleReference);
            this.splitPane.setRightComponent(this.elementPropPanel);
            this.tree.removeTreeSelectionListener(this.nodeSelectionListener);
            this.nodeSelectionListener = new DOMSelectionListener(this.tree, this.elementPropPanel);
            this.tree.addTreeSelectionListener(this.nodeSelectionListener);
        }
    }
}


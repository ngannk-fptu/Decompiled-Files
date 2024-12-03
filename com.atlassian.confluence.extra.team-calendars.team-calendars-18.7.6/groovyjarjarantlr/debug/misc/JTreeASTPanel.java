/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug.misc;

import java.awt.BorderLayout;
import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.TreeModel;

public class JTreeASTPanel
extends JPanel {
    JTree tree;

    public JTreeASTPanel(TreeModel treeModel, TreeSelectionListener treeSelectionListener) {
        this.setLayout(new BorderLayout());
        this.tree = new JTree(treeModel);
        this.tree.putClientProperty("JTree.lineStyle", "Angled");
        if (treeSelectionListener != null) {
            this.tree.addTreeSelectionListener(treeSelectionListener);
        }
        JScrollPane jScrollPane = new JScrollPane();
        jScrollPane.getViewport().add(this.tree);
        this.add((Component)jScrollPane, "Center");
    }
}


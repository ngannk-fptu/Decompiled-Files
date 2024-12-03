/*
 * Decompiled with CFR 0.152.
 */
package groovyjarjarantlr.debug.misc;

import groovyjarjarantlr.collections.AST;
import java.util.NoSuchElementException;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import javax.swing.tree.TreePath;

public class JTreeASTModel
implements TreeModel {
    AST root = null;

    public JTreeASTModel(AST aST) {
        if (aST == null) {
            throw new IllegalArgumentException("root is null");
        }
        this.root = aST;
    }

    public void addTreeModelListener(TreeModelListener treeModelListener) {
    }

    public Object getChild(Object object, int n) {
        if (object == null) {
            return null;
        }
        AST aST = (AST)object;
        AST aST2 = aST.getFirstChild();
        if (aST2 == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        for (int n2 = 0; aST2 != null && n2 < n; aST2 = aST2.getNextSibling(), ++n2) {
        }
        return aST2;
    }

    public int getChildCount(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("root is null");
        }
        AST aST = (AST)object;
        AST aST2 = aST.getFirstChild();
        int n = 0;
        while (aST2 != null) {
            aST2 = aST2.getNextSibling();
            ++n;
        }
        return n;
    }

    public int getIndexOfChild(Object object, Object object2) {
        if (object == null || object2 == null) {
            throw new IllegalArgumentException("root or child is null");
        }
        AST aST = (AST)object;
        AST aST2 = aST.getFirstChild();
        if (aST2 == null) {
            throw new ArrayIndexOutOfBoundsException("node has no children");
        }
        int n = 0;
        while (aST2 != null && aST2 != object2) {
            aST2 = aST2.getNextSibling();
            ++n;
        }
        if (aST2 == object2) {
            return n;
        }
        throw new NoSuchElementException("node is not a child");
    }

    public Object getRoot() {
        return this.root;
    }

    public boolean isLeaf(Object object) {
        if (object == null) {
            throw new IllegalArgumentException("node is null");
        }
        AST aST = (AST)object;
        return aST.getFirstChild() == null;
    }

    public void removeTreeModelListener(TreeModelListener treeModelListener) {
    }

    public void valueForPathChanged(TreePath treePath, Object object) {
        System.out.println("heh, who is calling this mystery method?");
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package cz.vutbr.web.domassign;

import cz.vutbr.web.domassign.GenericTreeWalker;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.TreeWalker;

public abstract class Traversal<T> {
    protected Object source;
    protected TreeWalker walker;

    public Traversal(TreeWalker walker, Object source) {
        this.source = source;
        this.walker = walker;
    }

    public Traversal(Document doc, Object source, int whatToShow) {
        if (doc instanceof DocumentTraversal) {
            DocumentTraversal dt = (DocumentTraversal)((Object)doc);
            this.walker = dt.createTreeWalker(doc.getDocumentElement(), whatToShow, null, false);
        } else {
            this.walker = new GenericTreeWalker(doc.getDocumentElement(), whatToShow);
        }
        this.source = source;
    }

    public void listTraversal(T result) {
        Node checkpoint = null;
        Node current = this.walker.nextNode();
        while (current != null) {
            checkpoint = this.walker.getCurrentNode();
            this.processNode(result, current, this.source);
            this.walker.setCurrentNode(checkpoint);
            current = this.walker.nextNode();
        }
    }

    public void levelTraversal(T result) {
        Node checkpoint = this.walker.getCurrentNode();
        this.processNode(result, checkpoint, this.source);
        this.walker.setCurrentNode(checkpoint);
        Node n = this.walker.firstChild();
        while (n != null) {
            this.levelTraversal(result);
            n = this.walker.nextSibling();
        }
        this.walker.setCurrentNode(checkpoint);
    }

    protected abstract void processNode(T var1, Node var2, Object var3);

    public Traversal<T> reset(TreeWalker walker, Object source) {
        this.walker = walker;
        this.source = source;
        return this;
    }
}


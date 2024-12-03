/*
 * Decompiled with CFR 0.152.
 */
package ognl;

import ognl.SimpleNode;

public class Evaluation {
    private SimpleNode node;
    private Object source;
    private boolean setOperation;
    private Object result;
    private Throwable exception;
    private Evaluation parent;
    private Evaluation next;
    private Evaluation previous;
    private Evaluation firstChild;
    private Evaluation lastChild;

    public Evaluation(SimpleNode node, Object source) {
        this.node = node;
        this.source = source;
    }

    public Evaluation(SimpleNode node, Object source, boolean setOperation) {
        this(node, source);
        this.setOperation = setOperation;
    }

    public SimpleNode getNode() {
        return this.node;
    }

    public void setNode(SimpleNode value) {
        this.node = value;
    }

    public Object getSource() {
        return this.source;
    }

    public void setSource(Object value) {
        this.source = value;
    }

    public boolean isSetOperation() {
        return this.setOperation;
    }

    public void setSetOperation(boolean value) {
        this.setOperation = value;
    }

    public Object getResult() {
        return this.result;
    }

    public void setResult(Object value) {
        this.result = value;
    }

    public Throwable getException() {
        return this.exception;
    }

    public void setException(Throwable value) {
        this.exception = value;
    }

    public Evaluation getParent() {
        return this.parent;
    }

    public Evaluation getNext() {
        return this.next;
    }

    public Evaluation getPrevious() {
        return this.previous;
    }

    public Evaluation getFirstChild() {
        return this.firstChild;
    }

    public Evaluation getLastChild() {
        return this.lastChild;
    }

    public Evaluation getFirstDescendant() {
        if (this.firstChild != null) {
            return this.firstChild.getFirstDescendant();
        }
        return this;
    }

    public Evaluation getLastDescendant() {
        if (this.lastChild != null) {
            return this.lastChild.getLastDescendant();
        }
        return this;
    }

    public void addChild(Evaluation child) {
        if (this.firstChild == null) {
            this.firstChild = this.lastChild = child;
        } else if (this.firstChild == this.lastChild) {
            this.firstChild.next = child;
            this.lastChild = child;
            this.lastChild.previous = this.firstChild;
        } else {
            child.previous = this.lastChild;
            this.lastChild.next = child;
            this.lastChild = child;
        }
        child.parent = this;
    }

    public void init(SimpleNode node, Object source, boolean setOperation) {
        this.node = node;
        this.source = source;
        this.setOperation = setOperation;
        this.result = null;
        this.exception = null;
        this.parent = null;
        this.next = null;
        this.previous = null;
        this.firstChild = null;
        this.lastChild = null;
    }

    public void reset() {
        this.init(null, null, false);
    }

    public String toString(boolean compact, boolean showChildren, String depth) {
        String stringResult;
        if (compact) {
            stringResult = depth + "<" + this.node.getClass().getName() + " " + System.identityHashCode(this) + ">";
        } else {
            String ss = this.source != null ? this.source.getClass().getName() : "null";
            String rs = this.result != null ? this.result.getClass().getName() : "null";
            stringResult = depth + "<" + this.node.getClass().getName() + ": [" + (this.setOperation ? "set" : "get") + "] source = " + ss + ", result = " + this.result + " [" + rs + "]>";
        }
        if (showChildren) {
            Evaluation child = this.firstChild;
            stringResult = stringResult + "\n";
            while (child != null) {
                stringResult = stringResult + child.toString(compact, depth + "  ");
                child = child.next;
            }
        }
        return stringResult;
    }

    public String toString(boolean compact, String depth) {
        return this.toString(compact, true, depth);
    }

    public String toString() {
        return this.toString(false, "");
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.om.impl.llom;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.apache.axiom.om.OMCloneOptions;
import org.apache.axiom.om.OMContainer;
import org.apache.axiom.om.OMException;
import org.apache.axiom.om.OMFactory;
import org.apache.axiom.om.OMInformationItem;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.impl.OMNodeEx;
import org.apache.axiom.om.impl.builder.OMFactoryEx;
import org.apache.axiom.om.impl.llom.IChildNode;
import org.apache.axiom.om.impl.llom.IContainer;
import org.apache.axiom.om.impl.llom.IParentNode;
import org.apache.axiom.om.impl.llom.OMElementImpl;
import org.apache.axiom.om.impl.llom.OMNodeHelper;
import org.apache.axiom.om.impl.llom.OMSerializableImpl;
import org.apache.axiom.om.impl.llom.factory.OMLinkedListImplFactory;

public abstract class OMNodeImpl
extends OMSerializableImpl
implements IChildNode {
    protected IContainer parent;
    protected OMNodeImpl nextSibling;
    protected OMNodeImpl previousSibling;

    public OMNodeImpl(OMFactory factory) {
        super(factory);
    }

    public OMContainer getParent() {
        return this.parent;
    }

    public IParentNode getIParentNode() {
        return this.parent;
    }

    public void setParent(OMContainer element) {
        if (this.parent == element) {
            return;
        }
        if (element != null) {
            if (this.parent != null) {
                this.detach();
            }
            this.parent = (IContainer)element;
        } else {
            this.parent = null;
        }
    }

    public OMNode getNextOMSibling() throws OMException {
        return OMNodeHelper.getNextOMSibling(this);
    }

    public OMNode getNextOMSiblingIfAvailable() {
        return this.nextSibling;
    }

    public void setNextOMSibling(OMNode node) {
        this.nextSibling = node == null || node.getOMFactory() instanceof OMLinkedListImplFactory ? (OMNodeImpl)node : (OMNodeImpl)((OMFactoryEx)this.factory).importNode(node);
        this.nextSibling = (OMNodeImpl)node;
    }

    public OMNode detach() throws OMException {
        if (this.parent == null) {
            throw new OMException("Nodes that don't have a parent can not be detached");
        }
        OMNodeImpl nextSibling = (OMNodeImpl)this.getNextOMSiblingIfAvailable();
        if (this.previousSibling == null) {
            this.parent.setFirstChild(nextSibling);
        } else {
            ((OMNodeEx)this.getPreviousOMSibling()).setNextOMSibling(nextSibling);
        }
        if (nextSibling != null) {
            nextSibling.setPreviousOMSibling(this.getPreviousOMSibling());
        }
        if (this.parent instanceof OMElementImpl && ((OMElementImpl)this.parent).lastChild == this) {
            ((OMElementImpl)this.parent).lastChild = this.getPreviousOMSibling();
        }
        this.previousSibling = null;
        this.nextSibling = null;
        this.parent = null;
        return this;
    }

    public void insertSiblingAfter(OMNode sibling) throws OMException {
        if (this.parent == null) {
            throw new OMException("Parent can not be null");
        }
        if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        ((OMNodeEx)sibling).setParent(this.parent);
        if (sibling instanceof OMNodeImpl) {
            OMNodeImpl siblingImpl = (OMNodeImpl)sibling;
            if (this.nextSibling == null) {
                this.getNextOMSibling();
            }
            siblingImpl.setPreviousOMSibling(this);
            if (this.nextSibling == null) {
                this.parent.setLastChild(sibling);
            } else {
                this.nextSibling.setPreviousOMSibling(sibling);
            }
            ((OMNodeEx)sibling).setNextOMSibling(this.nextSibling);
            this.nextSibling = siblingImpl;
        }
    }

    public void insertSiblingBefore(OMNode sibling) throws OMException {
        if (this.parent == null) {
            throw new OMException("Parent can not be null");
        }
        if (this == sibling) {
            throw new OMException("Inserting self as the sibling is not allowed");
        }
        if (sibling instanceof OMNodeImpl) {
            OMNodeImpl siblingImpl = (OMNodeImpl)sibling;
            if (this.previousSibling == null) {
                this.parent.setFirstChild(siblingImpl);
                siblingImpl.nextSibling = this;
                siblingImpl.previousSibling = null;
            } else {
                siblingImpl.setParent(this.parent);
                siblingImpl.nextSibling = this;
                this.previousSibling.setNextOMSibling(siblingImpl);
                siblingImpl.setPreviousOMSibling(this.previousSibling);
            }
            this.previousSibling = siblingImpl;
        }
    }

    public OMNode getPreviousOMSibling() {
        return this.previousSibling;
    }

    public void setPreviousOMSibling(OMNode previousSibling) {
        this.previousSibling = previousSibling == null || previousSibling.getOMFactory() instanceof OMLinkedListImplFactory ? (OMNodeImpl)previousSibling : (OMNodeImpl)((OMFactoryEx)this.factory).importNode(previousSibling);
    }

    public void buildWithAttachments() {
        if (!this.isComplete()) {
            this.build();
        }
    }

    public void internalSerialize(XMLStreamWriter writer) throws XMLStreamException {
        this.internalSerialize(writer, true);
    }

    public void internalSerializeAndConsume(XMLStreamWriter writer) throws XMLStreamException {
        this.internalSerialize(writer, false);
    }

    public OMInformationItem clone(OMCloneOptions options) {
        return this.clone(options, null);
    }

    abstract OMNode clone(OMCloneOptions var1, OMContainer var2);
}


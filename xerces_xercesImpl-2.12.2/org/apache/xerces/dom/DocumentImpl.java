/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.io.Serializable;
import java.lang.ref.Reference;
import java.lang.ref.ReferenceQueue;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Vector;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.CharacterDataImpl;
import org.apache.xerces.dom.CoreDocumentImpl;
import org.apache.xerces.dom.DOMImplementationImpl;
import org.apache.xerces.dom.DOMMessageFormatter;
import org.apache.xerces.dom.LCount;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.NodeIteratorImpl;
import org.apache.xerces.dom.RangeImpl;
import org.apache.xerces.dom.TreeWalkerImpl;
import org.apache.xerces.dom.events.EventImpl;
import org.apache.xerces.dom.events.MouseEventImpl;
import org.apache.xerces.dom.events.MutationEventImpl;
import org.apache.xerces.dom.events.UIEventImpl;
import org.w3c.dom.Attr;
import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.events.DocumentEvent;
import org.w3c.dom.events.Event;
import org.w3c.dom.events.EventException;
import org.w3c.dom.events.EventListener;
import org.w3c.dom.ranges.DocumentRange;
import org.w3c.dom.ranges.Range;
import org.w3c.dom.traversal.DocumentTraversal;
import org.w3c.dom.traversal.NodeFilter;
import org.w3c.dom.traversal.NodeIterator;
import org.w3c.dom.traversal.TreeWalker;

public class DocumentImpl
extends CoreDocumentImpl
implements DocumentTraversal,
DocumentEvent,
DocumentRange {
    static final long serialVersionUID = 515687835542616694L;
    protected transient List iterators;
    protected transient ReferenceQueue iteratorReferenceQueue;
    protected transient List ranges;
    protected transient ReferenceQueue rangeReferenceQueue;
    protected Hashtable eventListeners;
    protected boolean mutationEvents = false;
    EnclosingAttr savedEnclosingAttr;

    public DocumentImpl() {
    }

    public DocumentImpl(boolean bl) {
        super(bl);
    }

    public DocumentImpl(DocumentType documentType) {
        super(documentType);
    }

    public DocumentImpl(DocumentType documentType, boolean bl) {
        super(documentType, bl);
    }

    @Override
    public Node cloneNode(boolean bl) {
        DocumentImpl documentImpl = new DocumentImpl();
        this.callUserDataHandlers(this, documentImpl, (short)1);
        this.cloneNode(documentImpl, bl);
        documentImpl.mutationEvents = this.mutationEvents;
        return documentImpl;
    }

    @Override
    public DOMImplementation getImplementation() {
        return DOMImplementationImpl.getDOMImplementation();
    }

    public NodeIterator createNodeIterator(Node node, short s, NodeFilter nodeFilter) {
        return this.createNodeIterator(node, s, nodeFilter, true);
    }

    @Override
    public NodeIterator createNodeIterator(Node node, int n, NodeFilter nodeFilter, boolean bl) {
        if (node == null) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, string);
        }
        NodeIteratorImpl nodeIteratorImpl = new NodeIteratorImpl(this, node, n, nodeFilter, bl);
        if (this.iterators == null) {
            this.iterators = new LinkedList();
            this.iteratorReferenceQueue = new ReferenceQueue();
        }
        this.removeStaleIteratorReferences();
        this.iterators.add(new WeakReference<NodeIteratorImpl>(nodeIteratorImpl, this.iteratorReferenceQueue));
        return nodeIteratorImpl;
    }

    public TreeWalker createTreeWalker(Node node, short s, NodeFilter nodeFilter) {
        return this.createTreeWalker(node, s, nodeFilter, true);
    }

    @Override
    public TreeWalker createTreeWalker(Node node, int n, NodeFilter nodeFilter, boolean bl) {
        if (node == null) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
            throw new DOMException(9, string);
        }
        return new TreeWalkerImpl(node, n, nodeFilter, bl);
    }

    void removeNodeIterator(NodeIterator nodeIterator) {
        if (nodeIterator == null) {
            return;
        }
        if (this.iterators == null) {
            return;
        }
        this.removeStaleIteratorReferences();
        Iterator iterator = this.iterators.iterator();
        while (iterator.hasNext()) {
            Object t = ((Reference)iterator.next()).get();
            if (t == nodeIterator) {
                iterator.remove();
                return;
            }
            if (t != null) continue;
            iterator.remove();
        }
    }

    private void removeStaleIteratorReferences() {
        this.removeStaleReferences(this.iteratorReferenceQueue, this.iterators);
    }

    private void removeStaleReferences(ReferenceQueue referenceQueue, List list) {
        Reference reference = referenceQueue.poll();
        int n = 0;
        while (reference != null) {
            ++n;
            reference = referenceQueue.poll();
        }
        if (n > 0) {
            Iterator iterator = list.iterator();
            while (iterator.hasNext()) {
                Object t = ((Reference)iterator.next()).get();
                if (t != null) continue;
                iterator.remove();
                if (--n > 0) continue;
                return;
            }
        }
    }

    @Override
    public Range createRange() {
        if (this.ranges == null) {
            this.ranges = new LinkedList();
            this.rangeReferenceQueue = new ReferenceQueue();
        }
        RangeImpl rangeImpl = new RangeImpl(this);
        this.removeStaleRangeReferences();
        this.ranges.add(new WeakReference<RangeImpl>(rangeImpl, this.rangeReferenceQueue));
        return rangeImpl;
    }

    void removeRange(Range range) {
        if (range == null) {
            return;
        }
        if (this.ranges == null) {
            return;
        }
        this.removeStaleRangeReferences();
        Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            Object t = ((Reference)iterator.next()).get();
            if (t == range) {
                iterator.remove();
                return;
            }
            if (t != null) continue;
            iterator.remove();
        }
    }

    @Override
    void replacedText(CharacterDataImpl characterDataImpl) {
        if (this.ranges != null) {
            this.notifyRangesReplacedText(characterDataImpl);
        }
    }

    private void notifyRangesReplacedText(CharacterDataImpl characterDataImpl) {
        this.removeStaleRangeReferences();
        Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            RangeImpl rangeImpl = (RangeImpl)((Reference)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveReplacedText(characterDataImpl);
                continue;
            }
            iterator.remove();
        }
    }

    @Override
    void deletedText(CharacterDataImpl characterDataImpl, int n, int n2) {
        if (this.ranges != null) {
            this.notifyRangesDeletedText(characterDataImpl, n, n2);
        }
    }

    private void notifyRangesDeletedText(CharacterDataImpl characterDataImpl, int n, int n2) {
        this.removeStaleRangeReferences();
        Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            RangeImpl rangeImpl = (RangeImpl)((Reference)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveDeletedText(characterDataImpl, n, n2);
                continue;
            }
            iterator.remove();
        }
    }

    @Override
    void insertedText(CharacterDataImpl characterDataImpl, int n, int n2) {
        if (this.ranges != null) {
            this.notifyRangesInsertedText(characterDataImpl, n, n2);
        }
    }

    private void notifyRangesInsertedText(CharacterDataImpl characterDataImpl, int n, int n2) {
        this.removeStaleRangeReferences();
        Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            RangeImpl rangeImpl = (RangeImpl)((Reference)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveInsertedText(characterDataImpl, n, n2);
                continue;
            }
            iterator.remove();
        }
    }

    void splitData(Node node, Node node2, int n) {
        if (this.ranges != null) {
            this.notifyRangesSplitData(node, node2, n);
        }
    }

    private void notifyRangesSplitData(Node node, Node node2, int n) {
        this.removeStaleRangeReferences();
        Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            RangeImpl rangeImpl = (RangeImpl)((Reference)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.receiveSplitData(node, node2, n);
                continue;
            }
            iterator.remove();
        }
    }

    private void removeStaleRangeReferences() {
        this.removeStaleReferences(this.rangeReferenceQueue, this.ranges);
    }

    @Override
    public Event createEvent(String string) throws DOMException {
        if (string.equalsIgnoreCase("Events") || "Event".equals(string)) {
            return new EventImpl();
        }
        if (string.equalsIgnoreCase("MutationEvents") || "MutationEvent".equals(string)) {
            return new MutationEventImpl();
        }
        if (string.equalsIgnoreCase("UIEvents") || "UIEvent".equals(string)) {
            return new UIEventImpl();
        }
        if (string.equalsIgnoreCase("MouseEvents") || "MouseEvent".equals(string)) {
            return new MouseEventImpl();
        }
        String string2 = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "NOT_SUPPORTED_ERR", null);
        throw new DOMException(9, string2);
    }

    @Override
    void setMutationEvents(boolean bl) {
        this.mutationEvents = bl;
    }

    @Override
    boolean getMutationEvents() {
        return this.mutationEvents;
    }

    protected void setEventListeners(NodeImpl nodeImpl, Vector vector) {
        if (this.eventListeners == null) {
            this.eventListeners = new Hashtable();
        }
        if (vector == null) {
            this.eventListeners.remove(nodeImpl);
            if (this.eventListeners.isEmpty()) {
                this.mutationEvents = false;
            }
        } else {
            this.eventListeners.put(nodeImpl, vector);
            this.mutationEvents = true;
        }
    }

    protected Vector getEventListeners(NodeImpl nodeImpl) {
        if (this.eventListeners == null) {
            return null;
        }
        return (Vector)this.eventListeners.get(nodeImpl);
    }

    @Override
    protected void addEventListener(NodeImpl nodeImpl, String string, EventListener eventListener, boolean bl) {
        if (string == null || string.length() == 0 || eventListener == null) {
            return;
        }
        this.removeEventListener(nodeImpl, string, eventListener, bl);
        Vector<LEntry> vector = this.getEventListeners(nodeImpl);
        if (vector == null) {
            vector = new Vector<LEntry>();
            this.setEventListeners(nodeImpl, vector);
        }
        vector.addElement(new LEntry(string, eventListener, bl));
        LCount lCount = LCount.lookup(string);
        if (bl) {
            ++lCount.captures;
            ++lCount.total;
        } else {
            ++lCount.bubbles;
            ++lCount.total;
        }
    }

    @Override
    protected void removeEventListener(NodeImpl nodeImpl, String string, EventListener eventListener, boolean bl) {
        if (string == null || string.length() == 0 || eventListener == null) {
            return;
        }
        Vector vector = this.getEventListeners(nodeImpl);
        if (vector == null) {
            return;
        }
        for (int i = vector.size() - 1; i >= 0; --i) {
            LEntry lEntry = (LEntry)vector.elementAt(i);
            if (lEntry.useCapture != bl || lEntry.listener != eventListener || !lEntry.type.equals(string)) continue;
            vector.removeElementAt(i);
            if (vector.size() == 0) {
                this.setEventListeners(nodeImpl, null);
            }
            LCount lCount = LCount.lookup(string);
            if (bl) {
                --lCount.captures;
                --lCount.total;
                break;
            }
            --lCount.bubbles;
            --lCount.total;
            break;
        }
    }

    @Override
    protected void copyEventListeners(NodeImpl nodeImpl, NodeImpl nodeImpl2) {
        Vector vector = this.getEventListeners(nodeImpl);
        if (vector == null) {
            return;
        }
        this.setEventListeners(nodeImpl2, (Vector)vector.clone());
    }

    @Override
    protected boolean dispatchEvent(NodeImpl nodeImpl, Event event) {
        Cloneable cloneable;
        if (event == null) {
            return false;
        }
        EventImpl eventImpl = (EventImpl)event;
        if (!eventImpl.initialized || eventImpl.type == null || eventImpl.type.length() == 0) {
            String string = DOMMessageFormatter.formatMessage("http://www.w3.org/dom/DOMTR", "UNSPECIFIED_EVENT_TYPE_ERR", null);
            throw new EventException(0, string);
        }
        LCount lCount = LCount.lookup(eventImpl.getType());
        if (lCount.total == 0) {
            return eventImpl.preventDefault;
        }
        eventImpl.target = nodeImpl;
        eventImpl.stopPropagation = false;
        eventImpl.preventDefault = false;
        ArrayList<Node> arrayList = new ArrayList<Node>(10);
        Node node = nodeImpl;
        for (Node node2 = node.getParentNode(); node2 != null; node2 = node2.getParentNode()) {
            arrayList.add(node2);
            node = node2;
        }
        if (lCount.captures > 0) {
            eventImpl.eventPhase = 1;
            for (int i = arrayList.size() - 1; i >= 0 && !eventImpl.stopPropagation; --i) {
                cloneable = (NodeImpl)arrayList.get(i);
                eventImpl.currentTarget = cloneable;
                Vector vector = this.getEventListeners((NodeImpl)cloneable);
                if (vector == null) continue;
                Vector vector2 = (Vector)vector.clone();
                int n = vector2.size();
                for (int j = 0; j < n; ++j) {
                    LEntry lEntry = (LEntry)vector2.elementAt(j);
                    if (!lEntry.useCapture || !lEntry.type.equals(eventImpl.type) || !vector.contains(lEntry)) continue;
                    try {
                        lEntry.listener.handleEvent(eventImpl);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
        }
        if (lCount.bubbles > 0) {
            eventImpl.eventPhase = (short)2;
            eventImpl.currentTarget = nodeImpl;
            Vector vector = this.getEventListeners(nodeImpl);
            if (!eventImpl.stopPropagation && vector != null) {
                cloneable = (Vector)vector.clone();
                int n = ((Vector)cloneable).size();
                for (int i = 0; i < n; ++i) {
                    LEntry lEntry = (LEntry)((Vector)cloneable).elementAt(i);
                    if (lEntry.useCapture || !lEntry.type.equals(eventImpl.type) || !vector.contains(lEntry)) continue;
                    try {
                        lEntry.listener.handleEvent(eventImpl);
                        continue;
                    }
                    catch (Exception exception) {
                        // empty catch block
                    }
                }
            }
            if (eventImpl.bubbles) {
                eventImpl.eventPhase = (short)3;
                int n = arrayList.size();
                for (int i = 0; i < n && !eventImpl.stopPropagation; ++i) {
                    NodeImpl nodeImpl2 = (NodeImpl)arrayList.get(i);
                    eventImpl.currentTarget = nodeImpl2;
                    vector = this.getEventListeners(nodeImpl2);
                    if (vector == null) continue;
                    Vector vector3 = (Vector)vector.clone();
                    int n2 = vector3.size();
                    for (int j = 0; j < n2; ++j) {
                        LEntry lEntry = (LEntry)vector3.elementAt(j);
                        if (lEntry.useCapture || !lEntry.type.equals(eventImpl.type) || !vector.contains(lEntry)) continue;
                        try {
                            lEntry.listener.handleEvent(eventImpl);
                            continue;
                        }
                        catch (Exception exception) {
                            // empty catch block
                        }
                    }
                }
            }
        }
        if (lCount.defaults <= 0 || !eventImpl.cancelable || !eventImpl.preventDefault) {
            // empty if block
        }
        return eventImpl.preventDefault;
    }

    protected void dispatchEventToSubtree(Node node, Event event) {
        ((NodeImpl)node).dispatchEvent(event);
        if (node.getNodeType() == 1) {
            NamedNodeMap namedNodeMap = node.getAttributes();
            for (int i = namedNodeMap.getLength() - 1; i >= 0; --i) {
                this.dispatchingEventToSubtree(namedNodeMap.item(i), event);
            }
        }
        this.dispatchingEventToSubtree(node.getFirstChild(), event);
    }

    protected void dispatchingEventToSubtree(Node node, Event event) {
        if (node == null) {
            return;
        }
        ((NodeImpl)node).dispatchEvent(event);
        if (node.getNodeType() == 1) {
            NamedNodeMap namedNodeMap = node.getAttributes();
            for (int i = namedNodeMap.getLength() - 1; i >= 0; --i) {
                this.dispatchingEventToSubtree(namedNodeMap.item(i), event);
            }
        }
        this.dispatchingEventToSubtree(node.getFirstChild(), event);
        this.dispatchingEventToSubtree(node.getNextSibling(), event);
    }

    protected void dispatchAggregateEvents(NodeImpl nodeImpl, EnclosingAttr enclosingAttr) {
        if (enclosingAttr != null) {
            this.dispatchAggregateEvents(nodeImpl, enclosingAttr.node, enclosingAttr.oldvalue, (short)1);
        } else {
            this.dispatchAggregateEvents(nodeImpl, null, null, (short)0);
        }
    }

    protected void dispatchAggregateEvents(NodeImpl nodeImpl, AttrImpl attrImpl, String string, short s) {
        MutationEventImpl mutationEventImpl;
        LCount lCount;
        NodeImpl nodeImpl2 = null;
        if (attrImpl != null) {
            lCount = LCount.lookup("DOMAttrModified");
            nodeImpl2 = (NodeImpl)((Object)attrImpl.getOwnerElement());
            if (lCount.total > 0 && nodeImpl2 != null) {
                mutationEventImpl = new MutationEventImpl();
                mutationEventImpl.initMutationEvent("DOMAttrModified", true, false, attrImpl, string, attrImpl.getNodeValue(), attrImpl.getNodeName(), s);
                nodeImpl2.dispatchEvent(mutationEventImpl);
            }
        }
        lCount = LCount.lookup("DOMSubtreeModified");
        if (lCount.total > 0) {
            mutationEventImpl = new MutationEventImpl();
            mutationEventImpl.initMutationEvent("DOMSubtreeModified", true, false, null, null, null, null, (short)0);
            if (attrImpl != null) {
                this.dispatchEvent(attrImpl, mutationEventImpl);
                if (nodeImpl2 != null) {
                    this.dispatchEvent(nodeImpl2, mutationEventImpl);
                }
            } else {
                this.dispatchEvent(nodeImpl, mutationEventImpl);
            }
        }
    }

    protected void saveEnclosingAttr(NodeImpl nodeImpl) {
        this.savedEnclosingAttr = null;
        LCount lCount = LCount.lookup("DOMAttrModified");
        if (lCount.total > 0) {
            NodeImpl nodeImpl2 = nodeImpl;
            while (true) {
                if (nodeImpl2 == null) {
                    return;
                }
                short s = nodeImpl2.getNodeType();
                if (s == 2) {
                    EnclosingAttr enclosingAttr = new EnclosingAttr();
                    enclosingAttr.node = (AttrImpl)nodeImpl2;
                    enclosingAttr.oldvalue = enclosingAttr.node.getNodeValue();
                    this.savedEnclosingAttr = enclosingAttr;
                    return;
                }
                if (s == 5) {
                    nodeImpl2 = nodeImpl2.parentNode();
                    continue;
                }
                if (s != 3) break;
                nodeImpl2 = nodeImpl2.parentNode();
            }
            return;
        }
    }

    @Override
    void modifyingCharacterData(NodeImpl nodeImpl, boolean bl) {
        if (this.mutationEvents && !bl) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }

    @Override
    void modifiedCharacterData(NodeImpl nodeImpl, String string, String string2, boolean bl) {
        if (this.mutationEvents) {
            this.mutationEventsModifiedCharacterData(nodeImpl, string, string2, bl);
        }
    }

    private void mutationEventsModifiedCharacterData(NodeImpl nodeImpl, String string, String string2, boolean bl) {
        if (!bl) {
            LCount lCount = LCount.lookup("DOMCharacterDataModified");
            if (lCount.total > 0) {
                MutationEventImpl mutationEventImpl = new MutationEventImpl();
                mutationEventImpl.initMutationEvent("DOMCharacterDataModified", true, false, null, string, string2, null, (short)0);
                this.dispatchEvent(nodeImpl, mutationEventImpl);
            }
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }

    @Override
    void replacedCharacterData(NodeImpl nodeImpl, String string, String string2) {
        this.modifiedCharacterData(nodeImpl, string, string2, false);
    }

    @Override
    void insertingNode(NodeImpl nodeImpl, boolean bl) {
        if (this.mutationEvents && !bl) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }

    @Override
    void insertedNode(NodeImpl nodeImpl, NodeImpl nodeImpl2, boolean bl) {
        if (this.mutationEvents) {
            this.mutationEventsInsertedNode(nodeImpl, nodeImpl2, bl);
        }
        if (this.ranges != null) {
            this.notifyRangesInsertedNode(nodeImpl2);
        }
    }

    private void mutationEventsInsertedNode(NodeImpl nodeImpl, NodeImpl nodeImpl2, boolean bl) {
        Object object;
        LCount lCount = LCount.lookup("DOMNodeInserted");
        if (lCount.total > 0) {
            object = new MutationEventImpl();
            ((MutationEventImpl)object).initMutationEvent("DOMNodeInserted", true, false, nodeImpl, null, null, null, (short)0);
            this.dispatchEvent(nodeImpl2, (Event)object);
        }
        lCount = LCount.lookup("DOMNodeInsertedIntoDocument");
        if (lCount.total > 0) {
            object = nodeImpl;
            if (this.savedEnclosingAttr != null) {
                object = (NodeImpl)((Object)this.savedEnclosingAttr.node.getOwnerElement());
            }
            if (object != null) {
                Object object2 = object;
                while (object2 != null) {
                    object = object2;
                    if (((NodeImpl)object2).getNodeType() == 2) {
                        object2 = (NodeImpl)((Object)((AttrImpl)object2).getOwnerElement());
                        continue;
                    }
                    object2 = ((NodeImpl)object2).parentNode();
                }
                if (((NodeImpl)object).getNodeType() == 9) {
                    MutationEventImpl mutationEventImpl = new MutationEventImpl();
                    mutationEventImpl.initMutationEvent("DOMNodeInsertedIntoDocument", false, false, null, null, null, null, (short)0);
                    this.dispatchEventToSubtree(nodeImpl2, mutationEventImpl);
                }
            }
        }
        if (!bl) {
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }

    private void notifyRangesInsertedNode(NodeImpl nodeImpl) {
        this.removeStaleRangeReferences();
        Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            RangeImpl rangeImpl = (RangeImpl)((Reference)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.insertedNodeFromDOM(nodeImpl);
                continue;
            }
            iterator.remove();
        }
    }

    @Override
    void removingNode(NodeImpl nodeImpl, NodeImpl nodeImpl2, boolean bl) {
        if (this.iterators != null) {
            this.notifyIteratorsRemovingNode(nodeImpl2);
        }
        if (this.ranges != null) {
            this.notifyRangesRemovingNode(nodeImpl2);
        }
        if (this.mutationEvents) {
            this.mutationEventsRemovingNode(nodeImpl, nodeImpl2, bl);
        }
    }

    private void notifyIteratorsRemovingNode(NodeImpl nodeImpl) {
        this.removeStaleIteratorReferences();
        Iterator iterator = this.iterators.iterator();
        while (iterator.hasNext()) {
            NodeIteratorImpl nodeIteratorImpl = (NodeIteratorImpl)((Reference)iterator.next()).get();
            if (nodeIteratorImpl != null) {
                nodeIteratorImpl.removeNode(nodeImpl);
                continue;
            }
            iterator.remove();
        }
    }

    private void notifyRangesRemovingNode(NodeImpl nodeImpl) {
        this.removeStaleRangeReferences();
        Iterator iterator = this.ranges.iterator();
        while (iterator.hasNext()) {
            RangeImpl rangeImpl = (RangeImpl)((Reference)iterator.next()).get();
            if (rangeImpl != null) {
                rangeImpl.removeNode(nodeImpl);
                continue;
            }
            iterator.remove();
        }
    }

    private void mutationEventsRemovingNode(NodeImpl nodeImpl, NodeImpl nodeImpl2, boolean bl) {
        Object object;
        if (!bl) {
            this.saveEnclosingAttr(nodeImpl);
        }
        LCount lCount = LCount.lookup("DOMNodeRemoved");
        if (lCount.total > 0) {
            object = new MutationEventImpl();
            ((MutationEventImpl)object).initMutationEvent("DOMNodeRemoved", true, false, nodeImpl, null, null, null, (short)0);
            this.dispatchEvent(nodeImpl2, (Event)object);
        }
        lCount = LCount.lookup("DOMNodeRemovedFromDocument");
        if (lCount.total > 0) {
            object = this;
            if (this.savedEnclosingAttr != null) {
                object = (NodeImpl)((Object)this.savedEnclosingAttr.node.getOwnerElement());
            }
            if (object != null) {
                Object object2;
                for (object2 = ((NodeImpl)object).parentNode(); object2 != null; object2 = ((NodeImpl)object2).parentNode()) {
                    object = object2;
                }
                if (((NodeImpl)object).getNodeType() == 9) {
                    object2 = new MutationEventImpl();
                    ((MutationEventImpl)object2).initMutationEvent("DOMNodeRemovedFromDocument", false, false, null, null, null, null, (short)0);
                    this.dispatchEventToSubtree(nodeImpl2, (Event)object2);
                }
            }
        }
    }

    @Override
    void removedNode(NodeImpl nodeImpl, boolean bl) {
        if (this.mutationEvents && !bl) {
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }

    @Override
    void replacingNode(NodeImpl nodeImpl) {
        if (this.mutationEvents) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }

    @Override
    void replacingData(NodeImpl nodeImpl) {
        if (this.mutationEvents) {
            this.saveEnclosingAttr(nodeImpl);
        }
    }

    @Override
    void replacedNode(NodeImpl nodeImpl) {
        if (this.mutationEvents) {
            this.dispatchAggregateEvents(nodeImpl, this.savedEnclosingAttr);
        }
    }

    @Override
    void modifiedAttrValue(AttrImpl attrImpl, String string) {
        if (this.mutationEvents) {
            this.dispatchAggregateEvents(attrImpl, attrImpl, string, (short)1);
        }
    }

    @Override
    void setAttrNode(AttrImpl attrImpl, AttrImpl attrImpl2) {
        if (this.mutationEvents) {
            if (attrImpl2 == null) {
                this.dispatchAggregateEvents(attrImpl.ownerNode, attrImpl, null, (short)2);
            } else {
                this.dispatchAggregateEvents(attrImpl.ownerNode, attrImpl, attrImpl2.getNodeValue(), (short)1);
            }
        }
    }

    @Override
    void removedAttrNode(AttrImpl attrImpl, NodeImpl nodeImpl, String string) {
        if (this.mutationEvents) {
            this.mutationEventsRemovedAttrNode(attrImpl, nodeImpl, string);
        }
    }

    private void mutationEventsRemovedAttrNode(AttrImpl attrImpl, NodeImpl nodeImpl, String string) {
        LCount lCount = LCount.lookup("DOMAttrModified");
        if (lCount.total > 0) {
            MutationEventImpl mutationEventImpl = new MutationEventImpl();
            mutationEventImpl.initMutationEvent("DOMAttrModified", true, false, attrImpl, attrImpl.getNodeValue(), null, string, (short)3);
            this.dispatchEvent(nodeImpl, mutationEventImpl);
        }
        this.dispatchAggregateEvents(nodeImpl, null, null, (short)0);
    }

    @Override
    void renamedAttrNode(Attr attr, Attr attr2) {
    }

    @Override
    void renamedElement(Element element, Element element2) {
    }

    class EnclosingAttr
    implements Serializable {
        private static final long serialVersionUID = 5208387723391647216L;
        AttrImpl node;
        String oldvalue;

        EnclosingAttr() {
        }
    }

    class LEntry
    implements Serializable {
        private static final long serialVersionUID = -8426757059492421631L;
        String type;
        EventListener listener;
        boolean useCapture;

        LEntry(String string, EventListener eventListener, boolean bl) {
            this.type = string;
            this.listener = eventListener;
            this.useCapture = bl;
        }
    }
}


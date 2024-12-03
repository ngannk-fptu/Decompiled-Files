/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import javax.xml.transform.Source;
import org.apache.xml.dtm.Axis;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMException;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.XMLStringFactory;

public abstract class DTMDefaultBaseTraversers
extends DTMDefaultBase {
    public DTMDefaultBaseTraversers(DTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing);
    }

    public DTMDefaultBaseTraversers(DTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing, int blocksize, boolean usePrevsib, boolean newNameTable) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, blocksize, usePrevsib, newNameTable);
    }

    @Override
    public DTMAxisTraverser getAxisTraverser(int axis) {
        DTMAxisTraverser traverser;
        if (null == this.m_traversers) {
            this.m_traversers = new DTMAxisTraverser[Axis.getNamesLength()];
            traverser = null;
        } else {
            traverser = this.m_traversers[axis];
            if (traverser != null) {
                return traverser;
            }
        }
        switch (axis) {
            case 0: {
                traverser = new AncestorTraverser();
                break;
            }
            case 1: {
                traverser = new AncestorOrSelfTraverser();
                break;
            }
            case 2: {
                traverser = new AttributeTraverser();
                break;
            }
            case 3: {
                traverser = new ChildTraverser();
                break;
            }
            case 4: {
                traverser = new DescendantTraverser();
                break;
            }
            case 5: {
                traverser = new DescendantOrSelfTraverser();
                break;
            }
            case 6: {
                traverser = new FollowingTraverser();
                break;
            }
            case 7: {
                traverser = new FollowingSiblingTraverser();
                break;
            }
            case 9: {
                traverser = new NamespaceTraverser();
                break;
            }
            case 8: {
                traverser = new NamespaceDeclsTraverser();
                break;
            }
            case 10: {
                traverser = new ParentTraverser();
                break;
            }
            case 11: {
                traverser = new PrecedingTraverser();
                break;
            }
            case 12: {
                traverser = new PrecedingSiblingTraverser();
                break;
            }
            case 13: {
                traverser = new SelfTraverser();
                break;
            }
            case 16: {
                traverser = new AllFromRootTraverser();
                break;
            }
            case 14: {
                traverser = new AllFromNodeTraverser();
                break;
            }
            case 15: {
                traverser = new PrecedingAndAncestorTraverser();
                break;
            }
            case 17: {
                traverser = new DescendantFromRootTraverser();
                break;
            }
            case 18: {
                traverser = new DescendantOrSelfFromRootTraverser();
                break;
            }
            case 19: {
                traverser = new RootTraverser();
                break;
            }
            case 20: {
                return null;
            }
            default: {
                throw new DTMException(XMLMessages.createXMLMessage("ER_UNKNOWN_AXIS_TYPE", new Object[]{Integer.toString(axis)}));
            }
        }
        if (null == traverser) {
            throw new DTMException(XMLMessages.createXMLMessage("ER_AXIS_TRAVERSER_NOT_SUPPORTED", new Object[]{Axis.getNames(axis)}));
        }
        this.m_traversers[axis] = traverser;
        return traverser;
    }

    private class DescendantFromRootTraverser
    extends DescendantTraverser {
        private DescendantFromRootTraverser() {
        }

        @Override
        protected int getFirstPotential(int identity) {
            return DTMDefaultBaseTraversers.this._firstch(0);
        }

        @Override
        protected int getSubtreeRoot(int handle) {
            return 0;
        }

        @Override
        public int first(int context) {
            return DTMDefaultBaseTraversers.this.makeNodeHandle(DTMDefaultBaseTraversers.this._firstch(0));
        }

        @Override
        public int first(int context, int expandedTypeID) {
            if (this.isIndexed(expandedTypeID)) {
                int identity = 0;
                int firstPotential = this.getFirstPotential(identity);
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(identity, firstPotential, expandedTypeID));
            }
            int root = DTMDefaultBaseTraversers.this.getDocumentRoot(context);
            return this.next(root, root, expandedTypeID);
        }
    }

    private class DescendantOrSelfFromRootTraverser
    extends DescendantTraverser {
        private DescendantOrSelfFromRootTraverser() {
        }

        @Override
        protected int getFirstPotential(int identity) {
            return identity;
        }

        @Override
        protected int getSubtreeRoot(int handle) {
            return DTMDefaultBaseTraversers.this.makeNodeIdentity(DTMDefaultBaseTraversers.this.getDocument());
        }

        @Override
        public int first(int context) {
            return DTMDefaultBaseTraversers.this.getDocumentRoot(context);
        }

        @Override
        public int first(int context, int expandedTypeID) {
            if (this.isIndexed(expandedTypeID)) {
                int identity = 0;
                int firstPotential = this.getFirstPotential(identity);
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(identity, firstPotential, expandedTypeID));
            }
            int root = this.first(context);
            return this.next(root, root, expandedTypeID);
        }
    }

    private class RootTraverser
    extends AllFromRootTraverser {
        private RootTraverser() {
        }

        @Override
        public int first(int context, int expandedTypeID) {
            int root = DTMDefaultBaseTraversers.this.getDocumentRoot(context);
            return DTMDefaultBaseTraversers.this.getExpandedTypeID(root) == expandedTypeID ? root : -1;
        }

        @Override
        public int next(int context, int current) {
            return -1;
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            return -1;
        }
    }

    private class AllFromRootTraverser
    extends AllFromNodeTraverser {
        private AllFromRootTraverser() {
        }

        @Override
        public int first(int context) {
            return DTMDefaultBaseTraversers.this.getDocumentRoot(context);
        }

        @Override
        public int first(int context, int expandedTypeID) {
            return DTMDefaultBaseTraversers.this.getExpandedTypeID(DTMDefaultBaseTraversers.this.getDocumentRoot(context)) == expandedTypeID ? context : this.next(context, context, expandedTypeID);
        }

        @Override
        public int next(int context, int current) {
            int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            short type = DTMDefaultBaseTraversers.this._type(current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1);
            if (type == -1) {
                return -1;
            }
            return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            int exptype;
            while ((exptype = DTMDefaultBaseTraversers.this._exptype(current)) != -1) {
                if (exptype == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
                ++current;
            }
            return -1;
        }
    }

    private class SelfTraverser
    extends DTMAxisTraverser {
        private SelfTraverser() {
        }

        @Override
        public int first(int context) {
            return context;
        }

        @Override
        public int first(int context, int expandedTypeID) {
            return DTMDefaultBaseTraversers.this.getExpandedTypeID(context) == expandedTypeID ? context : -1;
        }

        @Override
        public int next(int context, int current) {
            return -1;
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            return -1;
        }
    }

    private class PrecedingSiblingTraverser
    extends DTMAxisTraverser {
        private PrecedingSiblingTraverser() {
        }

        @Override
        public int next(int context, int current) {
            return DTMDefaultBaseTraversers.this.getPreviousSibling(current);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            while (-1 != (current = DTMDefaultBaseTraversers.this.getPreviousSibling(current))) {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) continue;
                return current;
            }
            return -1;
        }
    }

    private class PrecedingAndAncestorTraverser
    extends DTMAxisTraverser {
        private PrecedingAndAncestorTraverser() {
        }

        @Override
        public int next(int context, int current) {
            int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                short type = DTMDefaultBaseTraversers.this._type(current);
                if (2 == type || 13 == type) continue;
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
            return -1;
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                int exptype = DTMDefaultBaseTraversers.this.m_exptype.elementAt(current);
                if (exptype != expandedTypeID) continue;
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
            return -1;
        }
    }

    private class PrecedingTraverser
    extends DTMAxisTraverser {
        private PrecedingTraverser() {
        }

        protected boolean isAncestor(int contextIdent, int currentIdent) {
            contextIdent = DTMDefaultBaseTraversers.this.m_parent.elementAt(contextIdent);
            while (-1 != contextIdent) {
                if (contextIdent == currentIdent) {
                    return true;
                }
                contextIdent = DTMDefaultBaseTraversers.this.m_parent.elementAt(contextIdent);
            }
            return false;
        }

        @Override
        public int next(int context, int current) {
            int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                short type = DTMDefaultBaseTraversers.this._type(current);
                if (2 == type || 13 == type || this.isAncestor(subtreeRootIdent, current)) continue;
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
            return -1;
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            for (current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) - 1; current >= 0; --current) {
                int exptype = DTMDefaultBaseTraversers.this.m_exptype.elementAt(current);
                if (exptype != expandedTypeID || this.isAncestor(subtreeRootIdent, current)) continue;
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
            return -1;
        }
    }

    private class ParentTraverser
    extends DTMAxisTraverser {
        private ParentTraverser() {
        }

        @Override
        public int first(int context) {
            return DTMDefaultBaseTraversers.this.getParent(context);
        }

        @Override
        public int first(int current, int expandedTypeID) {
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            while (-1 != (current = DTMDefaultBaseTraversers.this.m_parent.elementAt(current))) {
                if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(current) != expandedTypeID) continue;
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
            return -1;
        }

        @Override
        public int next(int context, int current) {
            return -1;
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            return -1;
        }
    }

    private class NamespaceTraverser
    extends DTMAxisTraverser {
        private NamespaceTraverser() {
        }

        @Override
        public int next(int context, int current) {
            return context == current ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, true) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, true);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int n = current = context == current ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, true) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, true);
            do {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) continue;
                return current;
            } while (-1 != (current = DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, true)));
            return -1;
        }
    }

    private class NamespaceDeclsTraverser
    extends DTMAxisTraverser {
        private NamespaceDeclsTraverser() {
        }

        @Override
        public int next(int context, int current) {
            return context == current ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, false) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, false);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int n = current = context == current ? DTMDefaultBaseTraversers.this.getFirstNamespaceNode(context, false) : DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, false);
            do {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) continue;
                return current;
            } while (-1 != (current = DTMDefaultBaseTraversers.this.getNextNamespaceNode(context, current, false)));
            return -1;
        }
    }

    private class FollowingSiblingTraverser
    extends DTMAxisTraverser {
        private FollowingSiblingTraverser() {
        }

        @Override
        public int next(int context, int current) {
            return DTMDefaultBaseTraversers.this.getNextSibling(current);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            while (-1 != (current = DTMDefaultBaseTraversers.this.getNextSibling(current))) {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) continue;
                return current;
            }
            return -1;
        }
    }

    private class FollowingTraverser
    extends DescendantTraverser {
        private FollowingTraverser() {
        }

        @Override
        public int first(int context) {
            int first;
            short type = DTMDefaultBaseTraversers.this._type(context = DTMDefaultBaseTraversers.this.makeNodeIdentity(context));
            if ((2 == type || 13 == type) && -1 != (first = DTMDefaultBaseTraversers.this._firstch(context = DTMDefaultBaseTraversers.this._parent(context)))) {
                return DTMDefaultBaseTraversers.this.makeNodeHandle(first);
            }
            do {
                if (-1 != (first = DTMDefaultBaseTraversers.this._nextsib(context))) continue;
                context = DTMDefaultBaseTraversers.this._parent(context);
            } while (-1 == first && -1 != context);
            return DTMDefaultBaseTraversers.this.makeNodeHandle(first);
        }

        @Override
        public int first(int context, int expandedTypeID) {
            int first;
            short type = DTMDefaultBaseTraversers.this.getNodeType(context);
            if ((2 == type || 13 == type) && -1 != (first = DTMDefaultBaseTraversers.this.getFirstChild(context = DTMDefaultBaseTraversers.this.getParent(context)))) {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(first) == expandedTypeID) {
                    return first;
                }
                return this.next(context, first, expandedTypeID);
            }
            do {
                if (-1 != (first = DTMDefaultBaseTraversers.this.getNextSibling(context))) {
                    if (DTMDefaultBaseTraversers.this.getExpandedTypeID(first) == expandedTypeID) {
                        return first;
                    }
                    return this.next(context, first, expandedTypeID);
                }
                context = DTMDefaultBaseTraversers.this.getParent(context);
            } while (-1 == first && -1 != context);
            return first;
        }

        @Override
        public int next(int context, int current) {
            short type;
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            do {
                if (-1 != (type = DTMDefaultBaseTraversers.this._type(++current))) continue;
                return -1;
            } while (2 == type || 13 == type);
            return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int etype;
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            do {
                if (-1 != (etype = DTMDefaultBaseTraversers.this._exptype(++current))) continue;
                return -1;
            } while (etype != expandedTypeID);
            return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
        }
    }

    private class AllFromNodeTraverser
    extends DescendantOrSelfTraverser {
        private AllFromNodeTraverser() {
        }

        @Override
        public int next(int context, int current) {
            int subtreeRootIdent = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            DTMDefaultBaseTraversers.this._exptype(current);
            if (!this.isDescendant(subtreeRootIdent, current)) {
                return -1;
            }
            return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
        }
    }

    private class DescendantOrSelfTraverser
    extends DescendantTraverser {
        private DescendantOrSelfTraverser() {
        }

        @Override
        protected int getFirstPotential(int identity) {
            return identity;
        }

        @Override
        public int first(int context) {
            return context;
        }
    }

    private class DescendantTraverser
    extends IndexedDTMAxisTraverser {
        private DescendantTraverser() {
        }

        protected int getFirstPotential(int identity) {
            return identity + 1;
        }

        @Override
        protected boolean axisHasBeenProcessed(int axisRoot) {
            return DTMDefaultBaseTraversers.this.m_nextsib.elementAt(axisRoot) != -2;
        }

        protected int getSubtreeRoot(int handle) {
            return DTMDefaultBaseTraversers.this.makeNodeIdentity(handle);
        }

        protected boolean isDescendant(int subtreeRootIdentity, int identity) {
            return DTMDefaultBaseTraversers.this._parent(identity) >= subtreeRootIdentity;
        }

        @Override
        protected boolean isAfterAxis(int axisRoot, int identity) {
            do {
                if (identity != axisRoot) continue;
                return false;
            } while ((identity = DTMDefaultBaseTraversers.this.m_parent.elementAt(identity)) >= axisRoot);
            return true;
        }

        @Override
        public int first(int context, int expandedTypeID) {
            if (this.isIndexed(expandedTypeID)) {
                int identity = this.getSubtreeRoot(context);
                int firstPotential = this.getFirstPotential(identity);
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(identity, firstPotential, expandedTypeID));
            }
            return this.next(context, context, expandedTypeID);
        }

        @Override
        public int next(int context, int current) {
            int subtreeRootIdent = this.getSubtreeRoot(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            while (true) {
                short type = DTMDefaultBaseTraversers.this._type(current);
                if (!this.isDescendant(subtreeRootIdent, current)) {
                    return -1;
                }
                if (2 != type && 13 != type) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
                ++current;
            }
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int subtreeRootIdent = this.getSubtreeRoot(context);
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current) + 1;
            if (this.isIndexed(expandedTypeID)) {
                return DTMDefaultBaseTraversers.this.makeNodeHandle(this.getNextIndexed(subtreeRootIdent, current, expandedTypeID));
            }
            while (true) {
                int exptype = DTMDefaultBaseTraversers.this._exptype(current);
                if (!this.isDescendant(subtreeRootIdent, current)) {
                    return -1;
                }
                if (exptype == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
                ++current;
            }
        }
    }

    private abstract class IndexedDTMAxisTraverser
    extends DTMAxisTraverser {
        private IndexedDTMAxisTraverser() {
        }

        protected final boolean isIndexed(int expandedTypeID) {
            return DTMDefaultBaseTraversers.this.m_indexing && 1 == DTMDefaultBaseTraversers.this.m_expandedNameTable.getType(expandedTypeID);
        }

        protected abstract boolean isAfterAxis(int var1, int var2);

        protected abstract boolean axisHasBeenProcessed(int var1);

        protected int getNextIndexed(int axisRoot, int nextPotential, int expandedTypeID) {
            int nsIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getNamespaceID(expandedTypeID);
            int lnIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getLocalNameID(expandedTypeID);
            while (true) {
                int next;
                if (-2 != (next = DTMDefaultBaseTraversers.this.findElementFromIndex(nsIndex, lnIndex, nextPotential))) {
                    if (this.isAfterAxis(axisRoot, next)) {
                        return -1;
                    }
                    return next;
                }
                if (this.axisHasBeenProcessed(axisRoot)) break;
                DTMDefaultBaseTraversers.this.nextNode();
            }
            return -1;
        }
    }

    private class ChildTraverser
    extends DTMAxisTraverser {
        private ChildTraverser() {
        }

        protected int getNextIndexed(int axisRoot, int nextPotential, int expandedTypeID) {
            int nsIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getNamespaceID(expandedTypeID);
            int lnIndex = DTMDefaultBaseTraversers.this.m_expandedNameTable.getLocalNameID(expandedTypeID);
            while (true) {
                int nextID;
                if (-2 != (nextID = DTMDefaultBaseTraversers.this.findElementFromIndex(nsIndex, lnIndex, nextPotential))) {
                    int parentID = DTMDefaultBaseTraversers.this.m_parent.elementAt(nextID);
                    if (parentID == axisRoot) {
                        return nextID;
                    }
                    if (parentID < axisRoot) {
                        return -1;
                    }
                    do {
                        if ((parentID = DTMDefaultBaseTraversers.this.m_parent.elementAt(parentID)) >= axisRoot) continue;
                        return -1;
                    } while (parentID > axisRoot);
                    nextPotential = nextID + 1;
                    continue;
                }
                DTMDefaultBaseTraversers.this.nextNode();
                if (DTMDefaultBaseTraversers.this.m_nextsib.elementAt(axisRoot) != -2) break;
            }
            return -1;
        }

        @Override
        public int first(int context) {
            return DTMDefaultBaseTraversers.this.getFirstChild(context);
        }

        @Override
        public int first(int context, int expandedTypeID) {
            int identity = DTMDefaultBaseTraversers.this.makeNodeIdentity(context);
            int firstMatch = this.getNextIndexed(identity, DTMDefaultBaseTraversers.this._firstch(identity), expandedTypeID);
            return DTMDefaultBaseTraversers.this.makeNodeHandle(firstMatch);
        }

        @Override
        public int next(int context, int current) {
            return DTMDefaultBaseTraversers.this.getNextSibling(current);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            current = DTMDefaultBaseTraversers.this._nextsib(DTMDefaultBaseTraversers.this.makeNodeIdentity(current));
            while (-1 != current) {
                if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(current) == expandedTypeID) {
                    return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
                }
                current = DTMDefaultBaseTraversers.this._nextsib(current);
            }
            return -1;
        }
    }

    private class AttributeTraverser
    extends DTMAxisTraverser {
        private AttributeTraverser() {
        }

        @Override
        public int next(int context, int current) {
            return context == current ? DTMDefaultBaseTraversers.this.getFirstAttribute(context) : DTMDefaultBaseTraversers.this.getNextAttribute(current);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            int n = current = context == current ? DTMDefaultBaseTraversers.this.getFirstAttribute(context) : DTMDefaultBaseTraversers.this.getNextAttribute(current);
            do {
                if (DTMDefaultBaseTraversers.this.getExpandedTypeID(current) != expandedTypeID) continue;
                return current;
            } while (-1 != (current = DTMDefaultBaseTraversers.this.getNextAttribute(current)));
            return -1;
        }
    }

    private class AncestorOrSelfTraverser
    extends AncestorTraverser {
        private AncestorOrSelfTraverser() {
        }

        @Override
        public int first(int context) {
            return context;
        }

        @Override
        public int first(int context, int expandedTypeID) {
            return DTMDefaultBaseTraversers.this.getExpandedTypeID(context) == expandedTypeID ? context : this.next(context, context, expandedTypeID);
        }
    }

    private class AncestorTraverser
    extends DTMAxisTraverser {
        private AncestorTraverser() {
        }

        @Override
        public int next(int context, int current) {
            return DTMDefaultBaseTraversers.this.getParent(current);
        }

        @Override
        public int next(int context, int current, int expandedTypeID) {
            current = DTMDefaultBaseTraversers.this.makeNodeIdentity(current);
            while (-1 != (current = DTMDefaultBaseTraversers.this.m_parent.elementAt(current))) {
                if (DTMDefaultBaseTraversers.this.m_exptype.elementAt(current) != expandedTypeID) continue;
                return DTMDefaultBaseTraversers.this.makeNodeHandle(current);
            }
            return -1;
        }
    }
}


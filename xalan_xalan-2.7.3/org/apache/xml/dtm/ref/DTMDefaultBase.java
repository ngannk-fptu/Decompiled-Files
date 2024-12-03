/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Vector;
import javax.xml.transform.Source;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMAxisTraverser;
import org.apache.xml.dtm.DTMException;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.dtm.ref.ExpandedNameTable;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.BoolStack;
import org.apache.xml.utils.SuballocatedIntVector;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public abstract class DTMDefaultBase
implements DTM {
    static final boolean JJK_DEBUG = false;
    public static final int ROOTNODE = 0;
    protected int m_size = 0;
    protected SuballocatedIntVector m_exptype;
    protected SuballocatedIntVector m_firstch;
    protected SuballocatedIntVector m_nextsib;
    protected SuballocatedIntVector m_prevsib;
    protected SuballocatedIntVector m_parent;
    protected Vector m_namespaceDeclSets = null;
    protected SuballocatedIntVector m_namespaceDeclSetElements = null;
    protected int[][][] m_elemIndexes;
    public static final int DEFAULT_BLOCKSIZE = 512;
    public static final int DEFAULT_NUMBLOCKS = 32;
    public static final int DEFAULT_NUMBLOCKS_SMALL = 4;
    protected static final int NOTPROCESSED = -2;
    public DTMManager m_mgr;
    protected DTMManagerDefault m_mgrDefault = null;
    protected SuballocatedIntVector m_dtmIdent;
    protected String m_documentBaseURI;
    protected DTMWSFilter m_wsfilter;
    protected boolean m_shouldStripWS = false;
    protected BoolStack m_shouldStripWhitespaceStack;
    protected XMLStringFactory m_xstrf;
    protected ExpandedNameTable m_expandedNameTable;
    protected boolean m_indexing;
    protected DTMAxisTraverser[] m_traversers;
    private Vector m_namespaceLists = null;

    public DTMDefaultBase(DTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing) {
        this(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, 512, true, false);
    }

    public DTMDefaultBase(DTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing, int blocksize, boolean usePrevsib, boolean newNameTable) {
        int numblocks;
        if (blocksize <= 64) {
            numblocks = 4;
            this.m_dtmIdent = new SuballocatedIntVector(4, 1);
        } else {
            numblocks = 32;
            this.m_dtmIdent = new SuballocatedIntVector(32);
        }
        this.m_exptype = new SuballocatedIntVector(blocksize, numblocks);
        this.m_firstch = new SuballocatedIntVector(blocksize, numblocks);
        this.m_nextsib = new SuballocatedIntVector(blocksize, numblocks);
        this.m_parent = new SuballocatedIntVector(blocksize, numblocks);
        if (usePrevsib) {
            this.m_prevsib = new SuballocatedIntVector(blocksize, numblocks);
        }
        this.m_mgr = mgr;
        if (mgr instanceof DTMManagerDefault) {
            this.m_mgrDefault = (DTMManagerDefault)mgr;
        }
        this.m_documentBaseURI = null != source ? source.getSystemId() : null;
        this.m_dtmIdent.setElementAt(dtmIdentity, 0);
        this.m_wsfilter = whiteSpaceFilter;
        this.m_xstrf = xstringfactory;
        this.m_indexing = doIndexing;
        this.m_expandedNameTable = doIndexing ? new ExpandedNameTable() : this.m_mgrDefault.getExpandedNameTable(this);
        if (null != whiteSpaceFilter) {
            this.m_shouldStripWhitespaceStack = new BoolStack();
            this.pushShouldStripWhitespace(false);
        }
    }

    protected void ensureSizeOfIndex(int namespaceID, int LocalNameID) {
        if (null == this.m_elemIndexes) {
            this.m_elemIndexes = new int[namespaceID + 20][][];
        } else if (this.m_elemIndexes.length <= namespaceID) {
            int[][][] indexes = this.m_elemIndexes;
            this.m_elemIndexes = new int[namespaceID + 20][][];
            System.arraycopy(indexes, 0, this.m_elemIndexes, 0, indexes.length);
        }
        Object localNameIndex = this.m_elemIndexes[namespaceID];
        if (null == localNameIndex) {
            localNameIndex = new int[LocalNameID + 100][];
            this.m_elemIndexes[namespaceID] = localNameIndex;
        } else if (((int[][])localNameIndex).length <= LocalNameID) {
            Object indexes = localNameIndex;
            localNameIndex = new int[LocalNameID + 100][];
            System.arraycopy(indexes, 0, localNameIndex, 0, ((int[][])indexes).length);
            this.m_elemIndexes[namespaceID] = localNameIndex;
        }
        int[] elemHandles = localNameIndex[LocalNameID];
        if (null == elemHandles) {
            elemHandles = new int[128];
            localNameIndex[LocalNameID] = elemHandles;
            elemHandles[0] = 1;
        } else if (elemHandles.length <= elemHandles[0] + 1) {
            int[] indexes = elemHandles;
            elemHandles = new int[elemHandles[0] + 1024];
            System.arraycopy(indexes, 0, elemHandles, 0, indexes.length);
            localNameIndex[LocalNameID] = elemHandles;
        }
    }

    protected void indexNode(int expandedTypeID, int identity) {
        ExpandedNameTable ent = this.m_expandedNameTable;
        short type = ent.getType(expandedTypeID);
        if (1 == type) {
            int namespaceID = ent.getNamespaceID(expandedTypeID);
            int localNameID = ent.getLocalNameID(expandedTypeID);
            this.ensureSizeOfIndex(namespaceID, localNameID);
            int[] index = this.m_elemIndexes[namespaceID][localNameID];
            index[index[0]] = identity;
            index[0] = index[0] + 1;
        }
    }

    protected int findGTE(int[] list, int start, int len, int value) {
        int high;
        int low = start;
        int end = high = start + (len - 1);
        while (low <= high) {
            int mid = low + high >>> 1;
            int c = list[mid];
            if (c > value) {
                high = mid - 1;
                continue;
            }
            if (c < value) {
                low = mid + 1;
                continue;
            }
            return mid;
        }
        return low <= end && list[low] > value ? low : -1;
    }

    int findElementFromIndex(int nsIndex, int lnIndex, int firstPotential) {
        int pos;
        int[] elems;
        int[][] lnIndexs;
        int[][][] indexes = this.m_elemIndexes;
        if (null != indexes && nsIndex < indexes.length && null != (lnIndexs = indexes[nsIndex]) && lnIndex < lnIndexs.length && null != (elems = lnIndexs[lnIndex]) && (pos = this.findGTE(elems, 1, elems[0], firstPotential)) > -1) {
            return elems[pos];
        }
        return -2;
    }

    protected abstract int getNextNodeIdentity(int var1);

    protected abstract boolean nextNode();

    protected abstract int getNumberOfNodes();

    protected short _type(int identity) {
        int info = this._exptype(identity);
        if (-1 != info) {
            return this.m_expandedNameTable.getType(info);
        }
        return -1;
    }

    protected int _exptype(int identity) {
        if (identity == -1) {
            return -1;
        }
        while (identity >= this.m_size) {
            if (this.nextNode() || identity < this.m_size) continue;
            return -1;
        }
        return this.m_exptype.elementAt(identity);
    }

    protected int _level(int identity) {
        while (identity >= this.m_size) {
            boolean isMore = this.nextNode();
            if (isMore || identity < this.m_size) continue;
            return -1;
        }
        int i = 0;
        while (-1 != (identity = this._parent(identity))) {
            ++i;
        }
        return i;
    }

    protected int _firstch(int identity) {
        int info;
        int n = info = identity >= this.m_size ? -2 : this.m_firstch.elementAt(identity);
        while (info == -2) {
            boolean isMore = this.nextNode();
            if (identity >= this.m_size && !isMore) {
                return -1;
            }
            info = this.m_firstch.elementAt(identity);
            if (info != -2 || isMore) continue;
            return -1;
        }
        return info;
    }

    protected int _nextsib(int identity) {
        int info;
        int n = info = identity >= this.m_size ? -2 : this.m_nextsib.elementAt(identity);
        while (info == -2) {
            boolean isMore = this.nextNode();
            if (identity >= this.m_size && !isMore) {
                return -1;
            }
            info = this.m_nextsib.elementAt(identity);
            if (info != -2 || isMore) continue;
            return -1;
        }
        return info;
    }

    protected int _prevsib(int identity) {
        if (identity < this.m_size) {
            return this.m_prevsib.elementAt(identity);
        }
        do {
            boolean isMore = this.nextNode();
            if (identity < this.m_size || isMore) continue;
            return -1;
        } while (identity >= this.m_size);
        return this.m_prevsib.elementAt(identity);
    }

    protected int _parent(int identity) {
        if (identity < this.m_size) {
            return this.m_parent.elementAt(identity);
        }
        do {
            boolean isMore = this.nextNode();
            if (identity < this.m_size || isMore) continue;
            return -1;
        } while (identity >= this.m_size);
        return this.m_parent.elementAt(identity);
    }

    public void dumpDTM(OutputStream os) {
        try {
            if (os == null) {
                File f = new File("DTMDump" + this.hashCode() + ".txt");
                System.err.println("Dumping... " + f.getAbsolutePath());
                os = new FileOutputStream(f);
            }
            PrintStream ps = new PrintStream(os);
            while (this.nextNode()) {
            }
            int nRecords = this.m_size;
            ps.println("Total nodes: " + nRecords);
            for (int index = 0; index < nRecords; ++index) {
                int nextSibling;
                String typestring;
                int i = this.makeNodeHandle(index);
                ps.println("=========== index=" + index + " handle=" + i + " ===========");
                ps.println("NodeName: " + this.getNodeName(i));
                ps.println("NodeNameX: " + this.getNodeNameX(i));
                ps.println("LocalName: " + this.getLocalName(i));
                ps.println("NamespaceURI: " + this.getNamespaceURI(i));
                ps.println("Prefix: " + this.getPrefix(i));
                int exTypeID = this._exptype(index);
                ps.println("Expanded Type ID: " + Integer.toHexString(exTypeID));
                short type = this._type(index);
                switch (type) {
                    case 2: {
                        typestring = "ATTRIBUTE_NODE";
                        break;
                    }
                    case 4: {
                        typestring = "CDATA_SECTION_NODE";
                        break;
                    }
                    case 8: {
                        typestring = "COMMENT_NODE";
                        break;
                    }
                    case 11: {
                        typestring = "DOCUMENT_FRAGMENT_NODE";
                        break;
                    }
                    case 9: {
                        typestring = "DOCUMENT_NODE";
                        break;
                    }
                    case 10: {
                        typestring = "DOCUMENT_NODE";
                        break;
                    }
                    case 1: {
                        typestring = "ELEMENT_NODE";
                        break;
                    }
                    case 6: {
                        typestring = "ENTITY_NODE";
                        break;
                    }
                    case 5: {
                        typestring = "ENTITY_REFERENCE_NODE";
                        break;
                    }
                    case 13: {
                        typestring = "NAMESPACE_NODE";
                        break;
                    }
                    case 12: {
                        typestring = "NOTATION_NODE";
                        break;
                    }
                    case -1: {
                        typestring = "NULL";
                        break;
                    }
                    case 7: {
                        typestring = "PROCESSING_INSTRUCTION_NODE";
                        break;
                    }
                    case 3: {
                        typestring = "TEXT_NODE";
                        break;
                    }
                    default: {
                        typestring = "Unknown!";
                    }
                }
                ps.println("Type: " + typestring);
                int firstChild = this._firstch(index);
                if (-1 == firstChild) {
                    ps.println("First child: DTM.NULL");
                } else if (-2 == firstChild) {
                    ps.println("First child: NOTPROCESSED");
                } else {
                    ps.println("First child: " + firstChild);
                }
                if (this.m_prevsib != null) {
                    int prevSibling = this._prevsib(index);
                    if (-1 == prevSibling) {
                        ps.println("Prev sibling: DTM.NULL");
                    } else if (-2 == prevSibling) {
                        ps.println("Prev sibling: NOTPROCESSED");
                    } else {
                        ps.println("Prev sibling: " + prevSibling);
                    }
                }
                if (-1 == (nextSibling = this._nextsib(index))) {
                    ps.println("Next sibling: DTM.NULL");
                } else if (-2 == nextSibling) {
                    ps.println("Next sibling: NOTPROCESSED");
                } else {
                    ps.println("Next sibling: " + nextSibling);
                }
                int parent = this._parent(index);
                if (-1 == parent) {
                    ps.println("Parent: DTM.NULL");
                } else if (-2 == parent) {
                    ps.println("Parent: NOTPROCESSED");
                } else {
                    ps.println("Parent: " + parent);
                }
                int level = this._level(index);
                ps.println("Level: " + level);
                ps.println("Node Value: " + this.getNodeValue(i));
                ps.println("String Value: " + this.getStringValue(i));
            }
        }
        catch (IOException ioe) {
            ioe.printStackTrace(System.err);
            throw new RuntimeException(ioe.getMessage());
        }
    }

    public String dumpNode(int nodeHandle) {
        String typestring;
        if (nodeHandle == -1) {
            return "[null]";
        }
        switch (this.getNodeType(nodeHandle)) {
            case 2: {
                typestring = "ATTR";
                break;
            }
            case 4: {
                typestring = "CDATA";
                break;
            }
            case 8: {
                typestring = "COMMENT";
                break;
            }
            case 11: {
                typestring = "DOC_FRAG";
                break;
            }
            case 9: {
                typestring = "DOC";
                break;
            }
            case 10: {
                typestring = "DOC_TYPE";
                break;
            }
            case 1: {
                typestring = "ELEMENT";
                break;
            }
            case 6: {
                typestring = "ENTITY";
                break;
            }
            case 5: {
                typestring = "ENT_REF";
                break;
            }
            case 13: {
                typestring = "NAMESPACE";
                break;
            }
            case 12: {
                typestring = "NOTATION";
                break;
            }
            case -1: {
                typestring = "null";
                break;
            }
            case 7: {
                typestring = "PI";
                break;
            }
            case 3: {
                typestring = "TEXT";
                break;
            }
            default: {
                typestring = "Unknown!";
            }
        }
        StringBuffer sb = new StringBuffer();
        sb.append("[" + nodeHandle + ": " + typestring + "(0x" + Integer.toHexString(this.getExpandedTypeID(nodeHandle)) + ") " + this.getNodeNameX(nodeHandle) + " {" + this.getNamespaceURI(nodeHandle) + "}=\"" + this.getNodeValue(nodeHandle) + "\"]");
        return sb.toString();
    }

    @Override
    public void setFeature(String featureId, boolean state) {
    }

    @Override
    public boolean hasChildNodes(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        int firstChild = this._firstch(identity);
        return firstChild != -1;
    }

    public final int makeNodeHandle(int nodeIdentity) {
        if (-1 == nodeIdentity) {
            return -1;
        }
        return this.m_dtmIdent.elementAt(nodeIdentity >>> 16) + (nodeIdentity & 0xFFFF);
    }

    public final int makeNodeIdentity(int nodeHandle) {
        if (-1 == nodeHandle) {
            return -1;
        }
        if (this.m_mgrDefault != null) {
            int whichDTMindex = nodeHandle >>> 16;
            if (this.m_mgrDefault.m_dtms[whichDTMindex] != this) {
                return -1;
            }
            return this.m_mgrDefault.m_dtm_offsets[whichDTMindex] | nodeHandle & 0xFFFF;
        }
        int whichDTMid = this.m_dtmIdent.indexOf(nodeHandle & 0xFFFF0000);
        return whichDTMid == -1 ? -1 : (whichDTMid << 16) + (nodeHandle & 0xFFFF);
    }

    @Override
    public int getFirstChild(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        int firstChild = this._firstch(identity);
        return this.makeNodeHandle(firstChild);
    }

    public int getTypedFirstChild(int nodeHandle, int nodeType) {
        if (nodeType < 14) {
            int firstChild = this._firstch(this.makeNodeIdentity(nodeHandle));
            while (firstChild != -1) {
                int eType = this._exptype(firstChild);
                if (eType == nodeType || eType >= 14 && this.m_expandedNameTable.getType(eType) == nodeType) {
                    return this.makeNodeHandle(firstChild);
                }
                firstChild = this._nextsib(firstChild);
            }
        } else {
            int firstChild = this._firstch(this.makeNodeIdentity(nodeHandle));
            while (firstChild != -1) {
                if (this._exptype(firstChild) == nodeType) {
                    return this.makeNodeHandle(firstChild);
                }
                firstChild = this._nextsib(firstChild);
            }
        }
        return -1;
    }

    @Override
    public int getLastChild(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        int child = this._firstch(identity);
        int lastChild = -1;
        while (child != -1) {
            lastChild = child;
            child = this._nextsib(child);
        }
        return this.makeNodeHandle(lastChild);
    }

    @Override
    public abstract int getAttributeNode(int var1, String var2, String var3);

    @Override
    public int getFirstAttribute(int nodeHandle) {
        int nodeID = this.makeNodeIdentity(nodeHandle);
        return this.makeNodeHandle(this.getFirstAttributeIdentity(nodeID));
    }

    protected int getFirstAttributeIdentity(int identity) {
        short type = this._type(identity);
        if (1 == type) {
            while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                type = this._type(identity);
                if (type == 2) {
                    return identity;
                }
                if (13 == type) continue;
                break;
            }
        }
        return -1;
    }

    protected int getTypedAttribute(int nodeHandle, int attType) {
        short type = this.getNodeType(nodeHandle);
        if (1 == type) {
            int identity = this.makeNodeIdentity(nodeHandle);
            while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                type = this._type(identity);
                if (type == 2) {
                    if (this._exptype(identity) != attType) continue;
                    return this.makeNodeHandle(identity);
                }
                if (13 == type) continue;
                break;
            }
        }
        return -1;
    }

    @Override
    public int getNextSibling(int nodeHandle) {
        if (nodeHandle == -1) {
            return -1;
        }
        return this.makeNodeHandle(this._nextsib(this.makeNodeIdentity(nodeHandle)));
    }

    public int getTypedNextSibling(int nodeHandle, int nodeType) {
        int eType;
        if (nodeHandle == -1) {
            return -1;
        }
        int node = this.makeNodeIdentity(nodeHandle);
        while ((node = this._nextsib(node)) != -1 && (eType = this._exptype(node)) != nodeType && this.m_expandedNameTable.getType(eType) != nodeType) {
        }
        return node == -1 ? -1 : this.makeNodeHandle(node);
    }

    @Override
    public int getPreviousSibling(int nodeHandle) {
        if (nodeHandle == -1) {
            return -1;
        }
        if (this.m_prevsib != null) {
            return this.makeNodeHandle(this._prevsib(this.makeNodeIdentity(nodeHandle)));
        }
        int nodeID = this.makeNodeIdentity(nodeHandle);
        int parent = this._parent(nodeID);
        int node = this._firstch(parent);
        int result = -1;
        while (node != nodeID) {
            result = node;
            node = this._nextsib(node);
        }
        return this.makeNodeHandle(result);
    }

    @Override
    public int getNextAttribute(int nodeHandle) {
        int nodeID = this.makeNodeIdentity(nodeHandle);
        if (this._type(nodeID) == 2) {
            return this.makeNodeHandle(this.getNextAttributeIdentity(nodeID));
        }
        return -1;
    }

    protected int getNextAttributeIdentity(int identity) {
        while (-1 != (identity = this.getNextNodeIdentity(identity))) {
            short type = this._type(identity);
            if (type == 2) {
                return identity;
            }
            if (type == 13) continue;
            break;
        }
        return -1;
    }

    protected void declareNamespaceInContext(int elementNodeIndex, int namespaceNodeIndex) {
        SuballocatedIntVector nsList = null;
        if (this.m_namespaceDeclSets == null) {
            this.m_namespaceDeclSetElements = new SuballocatedIntVector(32);
            this.m_namespaceDeclSetElements.addElement(elementNodeIndex);
            this.m_namespaceDeclSets = new Vector();
            nsList = new SuballocatedIntVector(32);
            this.m_namespaceDeclSets.addElement(nsList);
        } else {
            int last = this.m_namespaceDeclSetElements.size() - 1;
            if (last >= 0 && elementNodeIndex == this.m_namespaceDeclSetElements.elementAt(last)) {
                nsList = (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(last);
            }
        }
        if (nsList == null) {
            this.m_namespaceDeclSetElements.addElement(elementNodeIndex);
            SuballocatedIntVector inherited = this.findNamespaceContext(this._parent(elementNodeIndex));
            if (inherited != null) {
                int isize = inherited.size();
                nsList = new SuballocatedIntVector(Math.max(Math.min(isize + 16, 2048), 32));
                for (int i = 0; i < isize; ++i) {
                    nsList.addElement(inherited.elementAt(i));
                }
            } else {
                nsList = new SuballocatedIntVector(32);
            }
            this.m_namespaceDeclSets.addElement(nsList);
        }
        int newEType = this._exptype(namespaceNodeIndex);
        for (int i = nsList.size() - 1; i >= 0; --i) {
            if (newEType != this.getExpandedTypeID(nsList.elementAt(i))) continue;
            nsList.setElementAt(this.makeNodeHandle(namespaceNodeIndex), i);
            return;
        }
        nsList.addElement(this.makeNodeHandle(namespaceNodeIndex));
    }

    protected SuballocatedIntVector findNamespaceContext(int elementNodeIndex) {
        if (null != this.m_namespaceDeclSetElements) {
            int wouldBeAt = this.findInSortedSuballocatedIntVector(this.m_namespaceDeclSetElements, elementNodeIndex);
            if (wouldBeAt >= 0) {
                return (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(wouldBeAt);
            }
            if (wouldBeAt == -1) {
                return null;
            }
            wouldBeAt = -1 - wouldBeAt;
            int candidate = this.m_namespaceDeclSetElements.elementAt(--wouldBeAt);
            int ancestor = this._parent(elementNodeIndex);
            if (wouldBeAt == 0 && candidate < ancestor) {
                int ch;
                int rootHandle = this.getDocumentRoot(this.makeNodeHandle(elementNodeIndex));
                int rootID = this.makeNodeIdentity(rootHandle);
                int uppermostNSCandidateID = this.getNodeType(rootHandle) == 9 ? ((ch = this._firstch(rootID)) != -1 ? ch : rootID) : rootID;
                if (candidate == uppermostNSCandidateID) {
                    return (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(wouldBeAt);
                }
            }
            while (wouldBeAt >= 0 && ancestor > 0) {
                if (candidate == ancestor) {
                    return (SuballocatedIntVector)this.m_namespaceDeclSets.elementAt(wouldBeAt);
                }
                if (candidate < ancestor) {
                    while (candidate < (ancestor = this._parent(ancestor))) {
                    }
                    continue;
                }
                if (wouldBeAt <= 0) break;
                candidate = this.m_namespaceDeclSetElements.elementAt(--wouldBeAt);
            }
        }
        return null;
    }

    protected int findInSortedSuballocatedIntVector(SuballocatedIntVector vector, int lookfor) {
        int i = 0;
        if (vector != null) {
            int first = 0;
            int last = vector.size() - 1;
            while (first <= last) {
                i = (first + last) / 2;
                int test = lookfor - vector.elementAt(i);
                if (test == 0) {
                    return i;
                }
                if (test < 0) {
                    last = i - 1;
                    continue;
                }
                first = i + 1;
            }
            if (first > i) {
                i = first;
            }
        }
        return -1 - i;
    }

    @Override
    public int getFirstNamespaceNode(int nodeHandle, boolean inScope) {
        if (inScope) {
            int identity = this.makeNodeIdentity(nodeHandle);
            if (this._type(identity) == 1) {
                SuballocatedIntVector nsContext = this.findNamespaceContext(identity);
                if (nsContext == null || nsContext.size() < 1) {
                    return -1;
                }
                return nsContext.elementAt(0);
            }
            return -1;
        }
        int identity = this.makeNodeIdentity(nodeHandle);
        if (this._type(identity) == 1) {
            while (-1 != (identity = this.getNextNodeIdentity(identity))) {
                short type = this._type(identity);
                if (type == 13) {
                    return this.makeNodeHandle(identity);
                }
                if (2 == type) continue;
                break;
            }
            return -1;
        }
        return -1;
    }

    @Override
    public int getNextNamespaceNode(int baseHandle, int nodeHandle, boolean inScope) {
        if (inScope) {
            SuballocatedIntVector nsContext = this.findNamespaceContext(this.makeNodeIdentity(baseHandle));
            if (nsContext == null) {
                return -1;
            }
            int i = 1 + nsContext.indexOf(nodeHandle);
            if (i <= 0 || i == nsContext.size()) {
                return -1;
            }
            return nsContext.elementAt(i);
        }
        int identity = this.makeNodeIdentity(nodeHandle);
        while (-1 != (identity = this.getNextNodeIdentity(identity))) {
            short type = this._type(identity);
            if (type == 13) {
                return this.makeNodeHandle(identity);
            }
            if (type == 2) continue;
            break;
        }
        return -1;
    }

    @Override
    public int getParent(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        if (identity > 0) {
            return this.makeNodeHandle(this._parent(identity));
        }
        return -1;
    }

    @Override
    public int getDocument() {
        return this.m_dtmIdent.elementAt(0);
    }

    @Override
    public int getOwnerDocument(int nodeHandle) {
        if (9 == this.getNodeType(nodeHandle)) {
            return -1;
        }
        return this.getDocumentRoot(nodeHandle);
    }

    @Override
    public int getDocumentRoot(int nodeHandle) {
        return this.getManager().getDTM(nodeHandle).getDocument();
    }

    @Override
    public abstract XMLString getStringValue(int var1);

    @Override
    public int getStringValueChunkCount(int nodeHandle) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return 0;
    }

    @Override
    public char[] getStringValueChunk(int nodeHandle, int chunkIndex, int[] startAndLen) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }

    @Override
    public int getExpandedTypeID(int nodeHandle) {
        int id = this.makeNodeIdentity(nodeHandle);
        if (id == -1) {
            return -1;
        }
        return this._exptype(id);
    }

    @Override
    public int getExpandedTypeID(String namespace, String localName, int type) {
        ExpandedNameTable ent = this.m_expandedNameTable;
        return ent.getExpandedTypeID(namespace, localName, type);
    }

    @Override
    public String getLocalNameFromExpandedNameID(int expandedNameID) {
        return this.m_expandedNameTable.getLocalName(expandedNameID);
    }

    @Override
    public String getNamespaceFromExpandedNameID(int expandedNameID) {
        return this.m_expandedNameTable.getNamespace(expandedNameID);
    }

    public int getNamespaceType(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        int expandedNameID = this._exptype(identity);
        return this.m_expandedNameTable.getNamespaceID(expandedNameID);
    }

    @Override
    public abstract String getNodeName(int var1);

    @Override
    public String getNodeNameX(int nodeHandle) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }

    @Override
    public abstract String getLocalName(int var1);

    @Override
    public abstract String getPrefix(int var1);

    @Override
    public abstract String getNamespaceURI(int var1);

    @Override
    public abstract String getNodeValue(int var1);

    @Override
    public short getNodeType(int nodeHandle) {
        if (nodeHandle == -1) {
            return -1;
        }
        return this.m_expandedNameTable.getType(this._exptype(this.makeNodeIdentity(nodeHandle)));
    }

    @Override
    public short getLevel(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        return (short)(this._level(identity) + 1);
    }

    public int getNodeIdent(int nodeHandle) {
        return this.makeNodeIdentity(nodeHandle);
    }

    public int getNodeHandle(int nodeId) {
        return this.makeNodeHandle(nodeId);
    }

    @Override
    public boolean isSupported(String feature, String version) {
        return false;
    }

    @Override
    public String getDocumentBaseURI() {
        return this.m_documentBaseURI;
    }

    @Override
    public void setDocumentBaseURI(String baseURI) {
        this.m_documentBaseURI = baseURI;
    }

    @Override
    public String getDocumentSystemIdentifier(int nodeHandle) {
        return this.m_documentBaseURI;
    }

    @Override
    public String getDocumentEncoding(int nodeHandle) {
        return "UTF-8";
    }

    @Override
    public String getDocumentStandalone(int nodeHandle) {
        return null;
    }

    @Override
    public String getDocumentVersion(int documentHandle) {
        return null;
    }

    @Override
    public boolean getDocumentAllDeclarationsProcessed() {
        return true;
    }

    @Override
    public abstract String getDocumentTypeDeclarationSystemIdentifier();

    @Override
    public abstract String getDocumentTypeDeclarationPublicIdentifier();

    @Override
    public abstract int getElementById(String var1);

    @Override
    public abstract String getUnparsedEntityURI(String var1);

    @Override
    public boolean supportsPreStripping() {
        return true;
    }

    @Override
    public boolean isNodeAfter(int nodeHandle1, int nodeHandle2) {
        int index1 = this.makeNodeIdentity(nodeHandle1);
        int index2 = this.makeNodeIdentity(nodeHandle2);
        return index1 != -1 && index2 != -1 && index1 <= index2;
    }

    @Override
    public boolean isCharacterElementContentWhitespace(int nodeHandle) {
        return false;
    }

    @Override
    public boolean isDocumentAllDeclarationsProcessed(int documentHandle) {
        return true;
    }

    @Override
    public abstract boolean isAttributeSpecified(int var1);

    @Override
    public abstract void dispatchCharactersEvents(int var1, ContentHandler var2, boolean var3) throws SAXException;

    @Override
    public abstract void dispatchToEvents(int var1, ContentHandler var2) throws SAXException;

    @Override
    public Node getNode(int nodeHandle) {
        return new DTMNodeProxy(this, nodeHandle);
    }

    @Override
    public void appendChild(int newChild, boolean clone, boolean cloneDepth) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    }

    @Override
    public void appendTextChild(String str) {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
    }

    protected void error(String msg) {
        throw new DTMException(msg);
    }

    protected boolean getShouldStripWhitespace() {
        return this.m_shouldStripWS;
    }

    protected void pushShouldStripWhitespace(boolean shouldStrip) {
        this.m_shouldStripWS = shouldStrip;
        if (null != this.m_shouldStripWhitespaceStack) {
            this.m_shouldStripWhitespaceStack.push(shouldStrip);
        }
    }

    protected void popShouldStripWhitespace() {
        if (null != this.m_shouldStripWhitespaceStack) {
            this.m_shouldStripWS = this.m_shouldStripWhitespaceStack.popAndTop();
        }
    }

    protected void setShouldStripWhitespace(boolean shouldStrip) {
        this.m_shouldStripWS = shouldStrip;
        if (null != this.m_shouldStripWhitespaceStack) {
            this.m_shouldStripWhitespaceStack.setTop(shouldStrip);
        }
    }

    @Override
    public void documentRegistration() {
    }

    @Override
    public void documentRelease() {
    }

    @Override
    public void migrateTo(DTMManager mgr) {
        this.m_mgr = mgr;
        if (mgr instanceof DTMManagerDefault) {
            this.m_mgrDefault = (DTMManagerDefault)mgr;
        }
    }

    public DTMManager getManager() {
        return this.m_mgr;
    }

    public SuballocatedIntVector getDTMIDs() {
        if (this.m_mgr == null) {
            return null;
        }
        return this.m_dtmIdent;
    }
}


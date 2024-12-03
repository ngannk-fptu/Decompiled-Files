/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xerces.dom;

import java.util.ArrayList;
import java.util.Hashtable;
import org.apache.xerces.dom.AttrImpl;
import org.apache.xerces.dom.ChildNode;
import org.apache.xerces.dom.DeferredAttrImpl;
import org.apache.xerces.dom.DeferredAttrNSImpl;
import org.apache.xerces.dom.DeferredCDATASectionImpl;
import org.apache.xerces.dom.DeferredCommentImpl;
import org.apache.xerces.dom.DeferredDOMImplementationImpl;
import org.apache.xerces.dom.DeferredDocumentTypeImpl;
import org.apache.xerces.dom.DeferredElementDefinitionImpl;
import org.apache.xerces.dom.DeferredElementImpl;
import org.apache.xerces.dom.DeferredElementNSImpl;
import org.apache.xerces.dom.DeferredEntityImpl;
import org.apache.xerces.dom.DeferredEntityReferenceImpl;
import org.apache.xerces.dom.DeferredNode;
import org.apache.xerces.dom.DeferredNotationImpl;
import org.apache.xerces.dom.DeferredProcessingInstructionImpl;
import org.apache.xerces.dom.DeferredTextImpl;
import org.apache.xerces.dom.DocumentImpl;
import org.apache.xerces.dom.DocumentTypeImpl;
import org.apache.xerces.dom.ElementImpl;
import org.apache.xerces.dom.NodeImpl;
import org.apache.xerces.dom.ParentNode;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class DeferredDocumentImpl
extends DocumentImpl
implements DeferredNode {
    static final long serialVersionUID = 5186323580749626857L;
    private static final boolean DEBUG_PRINT_REF_COUNTS = false;
    private static final boolean DEBUG_PRINT_TABLES = false;
    private static final boolean DEBUG_IDS = false;
    protected static final int CHUNK_SHIFT = 11;
    protected static final int CHUNK_SIZE = 2048;
    protected static final int CHUNK_MASK = 2047;
    protected static final int INITIAL_CHUNK_COUNT = 32;
    protected transient int fNodeCount = 0;
    protected transient int[][] fNodeType;
    protected transient Object[][] fNodeName;
    protected transient Object[][] fNodeValue;
    protected transient int[][] fNodeParent;
    protected transient int[][] fNodeLastChild;
    protected transient int[][] fNodePrevSib;
    protected transient Object[][] fNodeURI;
    protected transient int[][] fNodeExtra;
    protected transient int fIdCount;
    protected transient String[] fIdName;
    protected transient int[] fIdElement;
    protected boolean fNamespacesEnabled = false;
    private final transient StringBuffer fBufferStr = new StringBuffer();
    private final transient ArrayList fStrChunks = new ArrayList();
    private static final int[] INIT_ARRAY = new int[2049];

    public DeferredDocumentImpl() {
        this(false);
    }

    public DeferredDocumentImpl(boolean bl) {
        this(bl, false);
    }

    public DeferredDocumentImpl(boolean bl, boolean bl2) {
        super(bl2);
        this.needsSyncData(true);
        this.needsSyncChildren(true);
        this.fNamespacesEnabled = bl;
    }

    @Override
    public DOMImplementation getImplementation() {
        return DeferredDOMImplementationImpl.getDOMImplementation();
    }

    boolean getNamespacesEnabled() {
        return this.fNamespacesEnabled;
    }

    void setNamespacesEnabled(boolean bl) {
        this.fNamespacesEnabled = bl;
    }

    public int createDeferredDocument() {
        int n = this.createNode((short)9);
        return n;
    }

    public int createDeferredDocumentType(String string, String string2, String string3) {
        int n = this.createNode((short)10);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeName, string, n2, n3);
        this.setChunkValue(this.fNodeValue, string2, n2, n3);
        this.setChunkValue(this.fNodeURI, string3, n2, n3);
        return n;
    }

    public void setInternalSubset(int n, String string) {
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.createNode((short)10);
        int n5 = n4 >> 11;
        int n6 = n4 & 0x7FF;
        this.setChunkIndex(this.fNodeExtra, n4, n2, n3);
        this.setChunkValue(this.fNodeValue, string, n5, n6);
    }

    public int createDeferredNotation(String string, String string2, String string3, String string4) {
        int n = this.createNode((short)12);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.createNode((short)12);
        int n5 = n4 >> 11;
        int n6 = n4 & 0x7FF;
        this.setChunkValue(this.fNodeName, string, n2, n3);
        this.setChunkValue(this.fNodeValue, string2, n2, n3);
        this.setChunkValue(this.fNodeURI, string3, n2, n3);
        this.setChunkIndex(this.fNodeExtra, n4, n2, n3);
        this.setChunkValue(this.fNodeName, string4, n5, n6);
        return n;
    }

    public int createDeferredEntity(String string, String string2, String string3, String string4, String string5) {
        int n = this.createNode((short)6);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.createNode((short)6);
        int n5 = n4 >> 11;
        int n6 = n4 & 0x7FF;
        this.setChunkValue(this.fNodeName, string, n2, n3);
        this.setChunkValue(this.fNodeValue, string2, n2, n3);
        this.setChunkValue(this.fNodeURI, string3, n2, n3);
        this.setChunkIndex(this.fNodeExtra, n4, n2, n3);
        this.setChunkValue(this.fNodeName, string4, n5, n6);
        this.setChunkValue(this.fNodeValue, null, n5, n6);
        this.setChunkValue(this.fNodeURI, null, n5, n6);
        int n7 = this.createNode((short)6);
        int n8 = n7 >> 11;
        int n9 = n7 & 0x7FF;
        this.setChunkIndex(this.fNodeExtra, n7, n5, n6);
        this.setChunkValue(this.fNodeName, string5, n8, n9);
        return n;
    }

    public String getDeferredEntityBaseURI(int n) {
        if (n != -1) {
            int n2 = this.getNodeExtra(n, false);
            n2 = this.getNodeExtra(n2, false);
            return this.getNodeName(n2, false);
        }
        return null;
    }

    public void setEntityInfo(int n, String string, String string2) {
        int n2 = this.getNodeExtra(n, false);
        if (n2 != -1) {
            int n3 = n2 >> 11;
            int n4 = n2 & 0x7FF;
            this.setChunkValue(this.fNodeValue, string, n3, n4);
            this.setChunkValue(this.fNodeURI, string2, n3, n4);
        }
    }

    public void setTypeInfo(int n, Object object) {
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeValue, object, n2, n3);
    }

    public void setInputEncoding(int n, String string) {
        int n2 = this.getNodeExtra(n, false);
        int n3 = this.getNodeExtra(n2, false);
        int n4 = n3 >> 11;
        int n5 = n3 & 0x7FF;
        this.setChunkValue(this.fNodeValue, string, n4, n5);
    }

    public int createDeferredEntityReference(String string, String string2) {
        int n = this.createNode((short)5);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeName, string, n2, n3);
        this.setChunkValue(this.fNodeValue, string2, n2, n3);
        return n;
    }

    public int createDeferredElement(String string, String string2, Object object) {
        int n = this.createNode((short)1);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeName, string2, n2, n3);
        this.setChunkValue(this.fNodeURI, string, n2, n3);
        this.setChunkValue(this.fNodeValue, object, n2, n3);
        return n;
    }

    public int createDeferredElement(String string) {
        return this.createDeferredElement(null, string);
    }

    public int createDeferredElement(String string, String string2) {
        int n = this.createNode((short)1);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeName, string2, n2, n3);
        this.setChunkValue(this.fNodeURI, string, n2, n3);
        return n;
    }

    public int setDeferredAttribute(int n, String string, String string2, String string3, boolean bl, boolean bl2, Object object) {
        int n2 = this.createDeferredAttribute(string, string2, string3, bl);
        int n3 = n2 >> 11;
        int n4 = n2 & 0x7FF;
        this.setChunkIndex(this.fNodeParent, n, n3, n4);
        int n5 = n >> 11;
        int n6 = n & 0x7FF;
        int n7 = this.getChunkIndex(this.fNodeExtra, n5, n6);
        if (n7 != 0) {
            this.setChunkIndex(this.fNodePrevSib, n7, n3, n4);
        }
        this.setChunkIndex(this.fNodeExtra, n2, n5, n6);
        int n8 = this.getChunkIndex(this.fNodeExtra, n3, n4);
        if (bl2) {
            this.setChunkIndex(this.fNodeExtra, n8 |= 0x200, n3, n4);
            String string4 = this.getChunkValue(this.fNodeValue, n3, n4);
            this.putIdentifier(string4, n);
        }
        if (object != null) {
            int n9 = this.createNode((short)20);
            int n10 = n9 >> 11;
            int n11 = n9 & 0x7FF;
            this.setChunkIndex(this.fNodeLastChild, n9, n3, n4);
            this.setChunkValue(this.fNodeValue, object, n10, n11);
        }
        return n2;
    }

    public int setDeferredAttribute(int n, String string, String string2, String string3, boolean bl) {
        int n2 = this.createDeferredAttribute(string, string2, string3, bl);
        int n3 = n2 >> 11;
        int n4 = n2 & 0x7FF;
        this.setChunkIndex(this.fNodeParent, n, n3, n4);
        int n5 = n >> 11;
        int n6 = n & 0x7FF;
        int n7 = this.getChunkIndex(this.fNodeExtra, n5, n6);
        if (n7 != 0) {
            this.setChunkIndex(this.fNodePrevSib, n7, n3, n4);
        }
        this.setChunkIndex(this.fNodeExtra, n2, n5, n6);
        return n2;
    }

    public int createDeferredAttribute(String string, String string2, boolean bl) {
        return this.createDeferredAttribute(string, null, string2, bl);
    }

    public int createDeferredAttribute(String string, String string2, String string3, boolean bl) {
        int n = this.createNode((short)2);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeName, string, n2, n3);
        this.setChunkValue(this.fNodeURI, string2, n2, n3);
        this.setChunkValue(this.fNodeValue, string3, n2, n3);
        int n4 = bl ? 32 : 0;
        this.setChunkIndex(this.fNodeExtra, n4, n2, n3);
        return n;
    }

    public int createDeferredElementDefinition(String string) {
        int n = this.createNode((short)21);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeName, string, n2, n3);
        return n;
    }

    public int createDeferredTextNode(String string, boolean bl) {
        int n = this.createNode((short)3);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeValue, string, n2, n3);
        this.setChunkIndex(this.fNodeExtra, bl ? 1 : 0, n2, n3);
        return n;
    }

    public int createDeferredCDATASection(String string) {
        int n = this.createNode((short)4);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeValue, string, n2, n3);
        return n;
    }

    public int createDeferredProcessingInstruction(String string, String string2) {
        int n = this.createNode((short)7);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeName, string, n2, n3);
        this.setChunkValue(this.fNodeValue, string2, n2, n3);
        return n;
    }

    public int createDeferredComment(String string) {
        int n = this.createNode((short)8);
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        this.setChunkValue(this.fNodeValue, string, n2, n3);
        return n;
    }

    public int cloneNode(int n, boolean bl) {
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.fNodeType[n2][n3];
        int n5 = this.createNode((short)n4);
        int n6 = n5 >> 11;
        int n7 = n5 & 0x7FF;
        this.setChunkValue(this.fNodeName, this.fNodeName[n2][n3], n6, n7);
        this.setChunkValue(this.fNodeValue, this.fNodeValue[n2][n3], n6, n7);
        this.setChunkValue(this.fNodeURI, this.fNodeURI[n2][n3], n6, n7);
        int n8 = this.fNodeExtra[n2][n3];
        if (n8 != -1) {
            if (n4 != 2 && n4 != 3) {
                n8 = this.cloneNode(n8, false);
            }
            this.setChunkIndex(this.fNodeExtra, n8, n6, n7);
        }
        if (bl) {
            int n9 = -1;
            int n10 = this.getLastChild(n, false);
            while (n10 != -1) {
                int n11 = this.cloneNode(n10, bl);
                this.insertBefore(n5, n11, n9);
                n9 = n11;
                n10 = this.getRealPrevSibling(n10, false);
            }
        }
        return n5;
    }

    public void appendChild(int n, int n2) {
        int n3 = n >> 11;
        int n4 = n & 0x7FF;
        int n5 = n2 >> 11;
        int n6 = n2 & 0x7FF;
        this.setChunkIndex(this.fNodeParent, n, n5, n6);
        int n7 = this.getChunkIndex(this.fNodeLastChild, n3, n4);
        this.setChunkIndex(this.fNodePrevSib, n7, n5, n6);
        this.setChunkIndex(this.fNodeLastChild, n2, n3, n4);
    }

    public int setAttributeNode(int n, int n2) {
        String string;
        int n3 = n >> 11;
        int n4 = n & 0x7FF;
        int n5 = n2 >> 11;
        int n6 = n2 & 0x7FF;
        String string2 = this.getChunkValue(this.fNodeName, n5, n6);
        int n7 = this.getChunkIndex(this.fNodeExtra, n3, n4);
        int n8 = -1;
        int n9 = -1;
        int n10 = -1;
        while (n7 != -1 && !(string = this.getChunkValue(this.fNodeName, n9 = n7 >> 11, n10 = n7 & 0x7FF)).equals(string2)) {
            n8 = n7;
            n7 = this.getChunkIndex(this.fNodePrevSib, n9, n10);
        }
        if (n7 != -1) {
            int n11;
            int n12;
            int n13 = this.getChunkIndex(this.fNodePrevSib, n9, n10);
            if (n8 == -1) {
                this.setChunkIndex(this.fNodeExtra, n13, n3, n4);
            } else {
                n12 = n8 >> 11;
                n11 = n8 & 0x7FF;
                this.setChunkIndex(this.fNodePrevSib, n13, n12, n11);
            }
            this.clearChunkIndex(this.fNodeType, n9, n10);
            this.clearChunkValue(this.fNodeName, n9, n10);
            this.clearChunkValue(this.fNodeValue, n9, n10);
            this.clearChunkIndex(this.fNodeParent, n9, n10);
            this.clearChunkIndex(this.fNodePrevSib, n9, n10);
            n12 = this.clearChunkIndex(this.fNodeLastChild, n9, n10);
            n11 = n12 >> 11;
            int n14 = n12 & 0x7FF;
            this.clearChunkIndex(this.fNodeType, n11, n14);
            this.clearChunkValue(this.fNodeValue, n11, n14);
            this.clearChunkIndex(this.fNodeParent, n11, n14);
            this.clearChunkIndex(this.fNodeLastChild, n11, n14);
        }
        int n15 = this.getChunkIndex(this.fNodeExtra, n3, n4);
        this.setChunkIndex(this.fNodeExtra, n2, n3, n4);
        this.setChunkIndex(this.fNodePrevSib, n15, n5, n6);
        return n7;
    }

    public void setIdAttributeNode(int n, int n2) {
        int n3 = n2 >> 11;
        int n4 = n2 & 0x7FF;
        int n5 = this.getChunkIndex(this.fNodeExtra, n3, n4);
        this.setChunkIndex(this.fNodeExtra, n5 |= 0x200, n3, n4);
        String string = this.getChunkValue(this.fNodeValue, n3, n4);
        this.putIdentifier(string, n);
    }

    public void setIdAttribute(int n) {
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.getChunkIndex(this.fNodeExtra, n2, n3);
        this.setChunkIndex(this.fNodeExtra, n4 |= 0x200, n2, n3);
    }

    public int insertBefore(int n, int n2, int n3) {
        if (n3 == -1) {
            this.appendChild(n, n2);
            return n2;
        }
        int n4 = n2 >> 11;
        int n5 = n2 & 0x7FF;
        int n6 = n3 >> 11;
        int n7 = n3 & 0x7FF;
        int n8 = this.getChunkIndex(this.fNodePrevSib, n6, n7);
        this.setChunkIndex(this.fNodePrevSib, n2, n6, n7);
        this.setChunkIndex(this.fNodePrevSib, n8, n4, n5);
        return n2;
    }

    public void setAsLastChild(int n, int n2) {
        int n3 = n >> 11;
        int n4 = n & 0x7FF;
        this.setChunkIndex(this.fNodeLastChild, n2, n3, n4);
    }

    public int getParentNode(int n) {
        return this.getParentNode(n, false);
    }

    public int getParentNode(int n, boolean bl) {
        if (n == -1) {
            return -1;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? this.clearChunkIndex(this.fNodeParent, n2, n3) : this.getChunkIndex(this.fNodeParent, n2, n3);
    }

    public int getLastChild(int n) {
        return this.getLastChild(n, true);
    }

    public int getLastChild(int n, boolean bl) {
        if (n == -1) {
            return -1;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? this.clearChunkIndex(this.fNodeLastChild, n2, n3) : this.getChunkIndex(this.fNodeLastChild, n2, n3);
    }

    public int getPrevSibling(int n) {
        return this.getPrevSibling(n, true);
    }

    public int getPrevSibling(int n, boolean bl) {
        if (n == -1) {
            return -1;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.getChunkIndex(this.fNodeType, n2, n3);
        if (n4 == 3) {
            while ((n = this.getChunkIndex(this.fNodePrevSib, n2, n3)) != -1 && (n4 = this.getChunkIndex(this.fNodeType, n2 = n >> 11, n3 = n & 0x7FF)) == 3) {
            }
        } else {
            n = this.getChunkIndex(this.fNodePrevSib, n2, n3);
        }
        return n;
    }

    public int getRealPrevSibling(int n) {
        return this.getRealPrevSibling(n, true);
    }

    public int getRealPrevSibling(int n, boolean bl) {
        if (n == -1) {
            return -1;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? this.clearChunkIndex(this.fNodePrevSib, n2, n3) : this.getChunkIndex(this.fNodePrevSib, n2, n3);
    }

    public int lookupElementDefinition(String string) {
        if (this.fNodeCount > 1) {
            int n = -1;
            int n2 = 0;
            int n3 = 0;
            int n4 = this.getChunkIndex(this.fNodeLastChild, n2, n3);
            while (n4 != -1) {
                n2 = n4 >> 11;
                n3 = n4 & 0x7FF;
                if (this.getChunkIndex(this.fNodeType, n2, n3) == 10) {
                    n = n4;
                    break;
                }
                n4 = this.getChunkIndex(this.fNodePrevSib, n2, n3);
            }
            if (n == -1) {
                return -1;
            }
            n2 = n >> 11;
            n3 = n & 0x7FF;
            n4 = this.getChunkIndex(this.fNodeLastChild, n2, n3);
            while (n4 != -1) {
                n2 = n4 >> 11;
                n3 = n4 & 0x7FF;
                if (this.getChunkIndex(this.fNodeType, n2, n3) == 21 && this.getChunkValue(this.fNodeName, n2, n3) == string) {
                    return n4;
                }
                n4 = this.getChunkIndex(this.fNodePrevSib, n2, n3);
            }
        }
        return -1;
    }

    public DeferredNode getNodeObject(int n) {
        if (n == -1) {
            return null;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.getChunkIndex(this.fNodeType, n2, n3);
        if (n4 != 3 && n4 != 4) {
            this.clearChunkIndex(this.fNodeType, n2, n3);
        }
        NodeImpl nodeImpl = null;
        switch (n4) {
            case 2: {
                if (this.fNamespacesEnabled) {
                    nodeImpl = new DeferredAttrNSImpl(this, n);
                    break;
                }
                nodeImpl = new DeferredAttrImpl(this, n);
                break;
            }
            case 4: {
                nodeImpl = new DeferredCDATASectionImpl(this, n);
                break;
            }
            case 8: {
                nodeImpl = new DeferredCommentImpl(this, n);
                break;
            }
            case 9: {
                nodeImpl = this;
                break;
            }
            case 10: {
                nodeImpl = new DeferredDocumentTypeImpl(this, n);
                this.docType = (DocumentTypeImpl)nodeImpl;
                break;
            }
            case 1: {
                nodeImpl = this.fNamespacesEnabled ? new DeferredElementNSImpl(this, n) : new DeferredElementImpl(this, n);
                if (this.fIdElement == null) break;
                int n5 = DeferredDocumentImpl.binarySearch(this.fIdElement, 0, this.fIdCount - 1, n);
                while (n5 != -1) {
                    String string = this.fIdName[n5];
                    if (string != null) {
                        this.putIdentifier0(string, (Element)((Object)nodeImpl));
                        this.fIdName[n5] = null;
                    }
                    if (n5 + 1 < this.fIdCount && this.fIdElement[n5 + 1] == n) {
                        ++n5;
                        continue;
                    }
                    n5 = -1;
                }
                break;
            }
            case 6: {
                nodeImpl = new DeferredEntityImpl(this, n);
                break;
            }
            case 5: {
                nodeImpl = new DeferredEntityReferenceImpl(this, n);
                break;
            }
            case 12: {
                nodeImpl = new DeferredNotationImpl(this, n);
                break;
            }
            case 7: {
                nodeImpl = new DeferredProcessingInstructionImpl(this, n);
                break;
            }
            case 3: {
                nodeImpl = new DeferredTextImpl(this, n);
                break;
            }
            case 21: {
                nodeImpl = new DeferredElementDefinitionImpl(this, n);
                break;
            }
            default: {
                throw new IllegalArgumentException("type: " + n4);
            }
        }
        if (nodeImpl != null) {
            return nodeImpl;
        }
        throw new IllegalArgumentException();
    }

    public String getNodeName(int n) {
        return this.getNodeName(n, true);
    }

    public String getNodeName(int n, boolean bl) {
        if (n == -1) {
            return null;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? this.clearChunkValue(this.fNodeName, n2, n3) : this.getChunkValue(this.fNodeName, n2, n3);
    }

    public String getNodeValueString(int n) {
        return this.getNodeValueString(n, true);
    }

    public String getNodeValueString(int n, boolean bl) {
        int n2;
        String string;
        if (n == -1) {
            return null;
        }
        int n3 = n >> 11;
        int n4 = n & 0x7FF;
        String string2 = string = bl ? this.clearChunkValue(this.fNodeValue, n3, n4) : this.getChunkValue(this.fNodeValue, n3, n4);
        if (string == null) {
            return null;
        }
        int n5 = this.getChunkIndex(this.fNodeType, n3, n4);
        if (n5 == 3) {
            int n6 = this.getRealPrevSibling(n);
            if (n6 != -1 && this.getNodeType(n6, false) == 3) {
                this.fStrChunks.add(string);
                do {
                    n3 = n6 >> 11;
                    n4 = n6 & 0x7FF;
                    string = this.getChunkValue(this.fNodeValue, n3, n4);
                    this.fStrChunks.add(string);
                } while ((n6 = this.getChunkIndex(this.fNodePrevSib, n3, n4)) != -1 && this.getNodeType(n6, false) == 3);
                int n7 = this.fStrChunks.size();
                for (int i = n7 - 1; i >= 0; --i) {
                    this.fBufferStr.append((String)this.fStrChunks.get(i));
                }
                string = this.fBufferStr.toString();
                this.fStrChunks.clear();
                this.fBufferStr.setLength(0);
                return string;
            }
        } else if (n5 == 4 && (n2 = this.getLastChild(n, false)) != -1) {
            this.fBufferStr.append(string);
            while (n2 != -1) {
                n3 = n2 >> 11;
                n4 = n2 & 0x7FF;
                string = this.getChunkValue(this.fNodeValue, n3, n4);
                this.fStrChunks.add(string);
                n2 = this.getChunkIndex(this.fNodePrevSib, n3, n4);
            }
            for (int i = this.fStrChunks.size() - 1; i >= 0; --i) {
                this.fBufferStr.append((String)this.fStrChunks.get(i));
            }
            string = this.fBufferStr.toString();
            this.fStrChunks.clear();
            this.fBufferStr.setLength(0);
            return string;
        }
        return string;
    }

    public String getNodeValue(int n) {
        return this.getNodeValue(n, true);
    }

    public Object getTypeInfo(int n) {
        Object object;
        if (n == -1) {
            return null;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        Object object2 = object = this.fNodeValue[n2] != null ? this.fNodeValue[n2][n3] : null;
        if (object != null) {
            this.fNodeValue[n2][n3] = null;
            RefCount refCount = (RefCount)this.fNodeValue[n2][2048];
            --refCount.fCount;
            if (refCount.fCount == 0) {
                this.fNodeValue[n2] = null;
            }
        }
        return object;
    }

    public String getNodeValue(int n, boolean bl) {
        if (n == -1) {
            return null;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? this.clearChunkValue(this.fNodeValue, n2, n3) : this.getChunkValue(this.fNodeValue, n2, n3);
    }

    public int getNodeExtra(int n) {
        return this.getNodeExtra(n, true);
    }

    public int getNodeExtra(int n, boolean bl) {
        if (n == -1) {
            return -1;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? this.clearChunkIndex(this.fNodeExtra, n2, n3) : this.getChunkIndex(this.fNodeExtra, n2, n3);
    }

    public short getNodeType(int n) {
        return this.getNodeType(n, true);
    }

    public short getNodeType(int n, boolean bl) {
        if (n == -1) {
            return -1;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? (short)this.clearChunkIndex(this.fNodeType, n2, n3) : (short)this.getChunkIndex(this.fNodeType, n2, n3);
    }

    public String getAttribute(int n, String string) {
        if (n == -1 || string == null) {
            return null;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        int n4 = this.getChunkIndex(this.fNodeExtra, n2, n3);
        while (n4 != -1) {
            int n5 = n4 >> 11;
            int n6 = n4 & 0x7FF;
            if (this.getChunkValue(this.fNodeName, n5, n6) == string) {
                return this.getChunkValue(this.fNodeValue, n5, n6);
            }
            n4 = this.getChunkIndex(this.fNodePrevSib, n5, n6);
        }
        return null;
    }

    public String getNodeURI(int n) {
        return this.getNodeURI(n, true);
    }

    public String getNodeURI(int n, boolean bl) {
        if (n == -1) {
            return null;
        }
        int n2 = n >> 11;
        int n3 = n & 0x7FF;
        return bl ? this.clearChunkValue(this.fNodeURI, n2, n3) : this.getChunkValue(this.fNodeURI, n2, n3);
    }

    public void putIdentifier(String string, int n) {
        if (this.fIdName == null) {
            this.fIdName = new String[64];
            this.fIdElement = new int[64];
        }
        if (this.fIdCount == this.fIdName.length) {
            String[] stringArray = new String[this.fIdCount * 2];
            System.arraycopy(this.fIdName, 0, stringArray, 0, this.fIdCount);
            this.fIdName = stringArray;
            int[] nArray = new int[stringArray.length];
            System.arraycopy(this.fIdElement, 0, nArray, 0, this.fIdCount);
            this.fIdElement = nArray;
        }
        this.fIdName[this.fIdCount] = string;
        this.fIdElement[this.fIdCount] = n;
        ++this.fIdCount;
    }

    public void print() {
    }

    @Override
    public int getNodeIndex() {
        return 0;
    }

    @Override
    protected void synchronizeData() {
        this.needsSyncData(false);
        if (this.fIdElement != null) {
            IntVector intVector = new IntVector();
            for (int i = 0; i < this.fIdCount; ++i) {
                int n;
                int n2;
                int n3 = this.fIdElement[i];
                String string = this.fIdName[i];
                if (string == null) continue;
                intVector.removeAllElements();
                int n4 = n3;
                do {
                    intVector.addElement(n4);
                } while ((n4 = this.getChunkIndex(this.fNodeParent, n2 = n4 >> 11, n = n4 & 0x7FF)) != -1);
                Node node = this;
                block2: for (n = intVector.size() - 2; n >= 0; --n) {
                    n4 = intVector.elementAt(n);
                    for (Node node2 = node.getLastChild(); node2 != null; node2 = node2.getPreviousSibling()) {
                        int n5;
                        if (!(node2 instanceof DeferredNode) || (n5 = ((DeferredNode)node2).getNodeIndex()) != n4) continue;
                        node = node2;
                        continue block2;
                    }
                }
                Element element = (Element)node;
                this.putIdentifier0(string, element);
                this.fIdName[i] = null;
                while (i + 1 < this.fIdCount && this.fIdElement[i + 1] == n3) {
                    if ((string = this.fIdName[++i]) == null) continue;
                    this.putIdentifier0(string, element);
                }
            }
        }
    }

    @Override
    protected void synchronizeChildren() {
        if (this.needsSyncData()) {
            this.synchronizeData();
            if (!this.needsSyncChildren()) {
                return;
            }
        }
        boolean bl = this.mutationEvents;
        this.mutationEvents = false;
        this.needsSyncChildren(false);
        this.getNodeType(0);
        NodeImpl nodeImpl = null;
        ChildNode childNode = null;
        int n = this.getLastChild(0);
        while (n != -1) {
            ChildNode childNode2 = (ChildNode)((Object)this.getNodeObject(n));
            if (childNode == null) {
                childNode = childNode2;
            } else {
                ((ChildNode)nodeImpl).previousSibling = childNode2;
            }
            childNode2.ownerNode = this;
            childNode2.isOwned(true);
            childNode2.nextSibling = nodeImpl;
            nodeImpl = childNode2;
            short s = childNode2.getNodeType();
            if (s == 1) {
                this.docElement = (ElementImpl)childNode2;
            } else if (s == 10) {
                this.docType = (DocumentTypeImpl)childNode2;
            }
            n = this.getPrevSibling(n);
        }
        if (nodeImpl != null) {
            this.firstChild = nodeImpl;
            nodeImpl.isFirstChild(true);
            this.lastChild(childNode);
        }
        this.mutationEvents = bl;
    }

    protected final void synchronizeChildren(AttrImpl attrImpl, int n) {
        boolean bl = this.getMutationEvents();
        this.setMutationEvents(false);
        attrImpl.needsSyncChildren(false);
        int n2 = this.getLastChild(n);
        int n3 = this.getPrevSibling(n2);
        if (n3 == -1) {
            attrImpl.value = this.getNodeValueString(n);
            attrImpl.hasStringValue(true);
        } else {
            NodeImpl nodeImpl = null;
            ChildNode childNode = null;
            int n4 = n2;
            while (n4 != -1) {
                ChildNode childNode2 = (ChildNode)((Object)this.getNodeObject(n4));
                if (childNode == null) {
                    childNode = childNode2;
                } else {
                    ((ChildNode)nodeImpl).previousSibling = childNode2;
                }
                childNode2.ownerNode = attrImpl;
                childNode2.isOwned(true);
                childNode2.nextSibling = nodeImpl;
                nodeImpl = childNode2;
                n4 = this.getPrevSibling(n4);
            }
            if (childNode != null) {
                attrImpl.value = nodeImpl;
                nodeImpl.isFirstChild(true);
                attrImpl.lastChild(childNode);
            }
            attrImpl.hasStringValue(false);
        }
        this.setMutationEvents(bl);
    }

    protected final void synchronizeChildren(ParentNode parentNode, int n) {
        boolean bl = this.getMutationEvents();
        this.setMutationEvents(false);
        parentNode.needsSyncChildren(false);
        NodeImpl nodeImpl = null;
        ChildNode childNode = null;
        int n2 = this.getLastChild(n);
        while (n2 != -1) {
            ChildNode childNode2 = (ChildNode)((Object)this.getNodeObject(n2));
            if (childNode == null) {
                childNode = childNode2;
            } else {
                ((ChildNode)nodeImpl).previousSibling = childNode2;
            }
            childNode2.ownerNode = parentNode;
            childNode2.isOwned(true);
            childNode2.nextSibling = nodeImpl;
            nodeImpl = childNode2;
            n2 = this.getPrevSibling(n2);
        }
        if (childNode != null) {
            parentNode.firstChild = nodeImpl;
            nodeImpl.isFirstChild(true);
            parentNode.lastChild(childNode);
        }
        this.setMutationEvents(bl);
    }

    protected void ensureCapacity(int n) {
        if (this.fNodeType == null) {
            this.fNodeType = new int[32][];
            this.fNodeName = new Object[32][];
            this.fNodeValue = new Object[32][];
            this.fNodeParent = new int[32][];
            this.fNodeLastChild = new int[32][];
            this.fNodePrevSib = new int[32][];
            this.fNodeURI = new Object[32][];
            this.fNodeExtra = new int[32][];
        } else if (this.fNodeType.length <= n) {
            int n2 = n * 2;
            int[][] nArrayArray = new int[n2][];
            System.arraycopy(this.fNodeType, 0, nArrayArray, 0, n);
            this.fNodeType = nArrayArray;
            Object[][] objectArrayArray = new Object[n2][];
            System.arraycopy(this.fNodeName, 0, objectArrayArray, 0, n);
            this.fNodeName = objectArrayArray;
            objectArrayArray = new Object[n2][];
            System.arraycopy(this.fNodeValue, 0, objectArrayArray, 0, n);
            this.fNodeValue = objectArrayArray;
            nArrayArray = new int[n2][];
            System.arraycopy(this.fNodeParent, 0, nArrayArray, 0, n);
            this.fNodeParent = nArrayArray;
            nArrayArray = new int[n2][];
            System.arraycopy(this.fNodeLastChild, 0, nArrayArray, 0, n);
            this.fNodeLastChild = nArrayArray;
            nArrayArray = new int[n2][];
            System.arraycopy(this.fNodePrevSib, 0, nArrayArray, 0, n);
            this.fNodePrevSib = nArrayArray;
            objectArrayArray = new Object[n2][];
            System.arraycopy(this.fNodeURI, 0, objectArrayArray, 0, n);
            this.fNodeURI = objectArrayArray;
            nArrayArray = new int[n2][];
            System.arraycopy(this.fNodeExtra, 0, nArrayArray, 0, n);
            this.fNodeExtra = nArrayArray;
        } else if (this.fNodeType[n] != null) {
            return;
        }
        this.createChunk(this.fNodeType, n);
        this.createChunk(this.fNodeName, n);
        this.createChunk(this.fNodeValue, n);
        this.createChunk(this.fNodeParent, n);
        this.createChunk(this.fNodeLastChild, n);
        this.createChunk(this.fNodePrevSib, n);
        this.createChunk(this.fNodeURI, n);
        this.createChunk(this.fNodeExtra, n);
    }

    protected int createNode(short s) {
        int n = this.fNodeCount >> 11;
        int n2 = this.fNodeCount & 0x7FF;
        this.ensureCapacity(n);
        this.setChunkIndex(this.fNodeType, s, n, n2);
        return this.fNodeCount++;
    }

    protected static int binarySearch(int[] nArray, int n, int n2, int n3) {
        while (n <= n2) {
            int n4 = n + n2 >>> 1;
            int n5 = nArray[n4];
            if (n5 == n3) {
                while (n4 > 0 && nArray[n4 - 1] == n3) {
                    --n4;
                }
                return n4;
            }
            if (n5 > n3) {
                n2 = n4 - 1;
                continue;
            }
            n = n4 + 1;
        }
        return -1;
    }

    private final void createChunk(int[][] nArray, int n) {
        nArray[n] = new int[2049];
        System.arraycopy(INIT_ARRAY, 0, nArray[n], 0, 2048);
    }

    private final void createChunk(Object[][] objectArray, int n) {
        objectArray[n] = new Object[2049];
        objectArray[n][2048] = new RefCount();
    }

    private final int setChunkIndex(int[][] nArray, int n, int n2, int n3) {
        int n4;
        if (n == -1) {
            return this.clearChunkIndex(nArray, n2, n3);
        }
        int[] nArray2 = nArray[n2];
        if (nArray2 == null) {
            this.createChunk(nArray, n2);
            nArray2 = nArray[n2];
        }
        if ((n4 = nArray2[n3]) == -1) {
            nArray2[2048] = nArray2[2048] + 1;
        }
        nArray2[n3] = n;
        return n4;
    }

    private final String setChunkValue(Object[][] objectArray, Object object, int n, int n2) {
        String string;
        if (object == null) {
            return this.clearChunkValue(objectArray, n, n2);
        }
        Object[] objectArray2 = objectArray[n];
        if (objectArray2 == null) {
            this.createChunk(objectArray, n);
            objectArray2 = objectArray[n];
        }
        if ((string = (String)objectArray2[n2]) == null) {
            RefCount refCount = (RefCount)objectArray2[2048];
            ++refCount.fCount;
        }
        objectArray2[n2] = object;
        return string;
    }

    private final int getChunkIndex(int[][] nArray, int n, int n2) {
        return nArray[n] != null ? nArray[n][n2] : -1;
    }

    private final String getChunkValue(Object[][] objectArray, int n, int n2) {
        return objectArray[n] != null ? (String)objectArray[n][n2] : null;
    }

    private final String getNodeValue(int n, int n2) {
        Object object = this.fNodeValue[n][n2];
        if (object == null) {
            return null;
        }
        if (object instanceof String) {
            return (String)object;
        }
        return object.toString();
    }

    private final int clearChunkIndex(int[][] nArray, int n, int n2) {
        int n3;
        int n4 = n3 = nArray[n] != null ? nArray[n][n2] : -1;
        if (n3 != -1) {
            int[] nArray2 = nArray[n];
            nArray2[2048] = nArray2[2048] - 1;
            nArray[n][n2] = -1;
            if (nArray[n][2048] == 0) {
                nArray[n] = null;
            }
        }
        return n3;
    }

    private final String clearChunkValue(Object[][] objectArray, int n, int n2) {
        String string;
        String string2 = string = objectArray[n] != null ? (String)objectArray[n][n2] : null;
        if (string != null) {
            objectArray[n][n2] = null;
            RefCount refCount = (RefCount)objectArray[n][2048];
            --refCount.fCount;
            if (refCount.fCount == 0) {
                objectArray[n] = null;
            }
        }
        return string;
    }

    private final void putIdentifier0(String string, Element element) {
        if (this.identifiers == null) {
            this.identifiers = new Hashtable();
        }
        this.identifiers.put(string, element);
    }

    private static void print(int[] nArray, int n, int n2, int n3, int n4) {
    }

    static {
        for (int i = 0; i < 2048; ++i) {
            DeferredDocumentImpl.INIT_ARRAY[i] = -1;
        }
    }

    static final class IntVector {
        private int[] data;
        private int size;

        IntVector() {
        }

        public int size() {
            return this.size;
        }

        public int elementAt(int n) {
            return this.data[n];
        }

        public void addElement(int n) {
            this.ensureCapacity(this.size + 1);
            this.data[this.size++] = n;
        }

        public void removeAllElements() {
            this.size = 0;
        }

        private void ensureCapacity(int n) {
            if (this.data == null) {
                this.data = new int[n + 15];
            } else if (n > this.data.length) {
                int[] nArray = new int[n + 15];
                System.arraycopy(this.data, 0, nArray, 0, this.data.length);
                this.data = nArray;
            }
        }
    }

    static final class RefCount {
        int fCount;

        RefCount() {
        }
    }
}


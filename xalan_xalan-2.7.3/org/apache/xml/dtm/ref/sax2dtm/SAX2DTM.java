/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref.sax2dtm;

import java.util.Hashtable;
import java.util.Vector;
import javax.xml.transform.Source;
import javax.xml.transform.SourceLocator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMDefaultBaseIterators;
import org.apache.xml.dtm.ref.DTMManagerDefault;
import org.apache.xml.dtm.ref.DTMStringPool;
import org.apache.xml.dtm.ref.DTMTreeWalker;
import org.apache.xml.dtm.ref.IncrementalSAXSource;
import org.apache.xml.dtm.ref.IncrementalSAXSource_Filter;
import org.apache.xml.dtm.ref.NodeLocator;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.FastStringBuffer;
import org.apache.xml.utils.IntStack;
import org.apache.xml.utils.IntVector;
import org.apache.xml.utils.StringVector;
import org.apache.xml.utils.SuballocatedIntVector;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLString;
import org.apache.xml.utils.XMLStringFactory;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;

public class SAX2DTM
extends DTMDefaultBaseIterators
implements EntityResolver,
DTDHandler,
ContentHandler,
ErrorHandler,
DeclHandler,
LexicalHandler {
    private static final boolean DEBUG = false;
    private IncrementalSAXSource m_incrementalSAXSource = null;
    protected FastStringBuffer m_chars;
    protected SuballocatedIntVector m_data;
    protected transient IntStack m_parents;
    protected transient int m_previous = 0;
    protected transient Vector m_prefixMappings = new Vector();
    protected transient IntStack m_contextIndexes;
    protected transient int m_textType = 3;
    protected transient int m_coalescedTextType = 3;
    protected transient Locator m_locator = null;
    private transient String m_systemId = null;
    protected transient boolean m_insideDTD = false;
    protected DTMTreeWalker m_walker = new DTMTreeWalker();
    protected DTMStringPool m_valuesOrPrefixes;
    protected boolean m_endDocumentOccured = false;
    protected SuballocatedIntVector m_dataOrQName;
    protected Hashtable m_idAttributes = new Hashtable();
    private static final String[] m_fixednames = new String[]{null, null, null, "#text", "#cdata_section", null, null, null, "#comment", "#document", null, "#document-fragment", null};
    private Vector m_entities = null;
    private static final int ENTITY_FIELD_PUBLICID = 0;
    private static final int ENTITY_FIELD_SYSTEMID = 1;
    private static final int ENTITY_FIELD_NOTATIONNAME = 2;
    private static final int ENTITY_FIELD_NAME = 3;
    private static final int ENTITY_FIELDS_PER = 4;
    protected int m_textPendingStart = -1;
    protected boolean m_useSourceLocationProperty = false;
    protected StringVector m_sourceSystemId;
    protected IntVector m_sourceLine;
    protected IntVector m_sourceColumn;
    boolean m_pastFirstElement = false;

    public SAX2DTM(DTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing) {
        this(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, 512, true, false);
    }

    public SAX2DTM(DTMManager mgr, Source source, int dtmIdentity, DTMWSFilter whiteSpaceFilter, XMLStringFactory xstringfactory, boolean doIndexing, int blocksize, boolean usePrevsib, boolean newNameTable) {
        super(mgr, source, dtmIdentity, whiteSpaceFilter, xstringfactory, doIndexing, blocksize, usePrevsib, newNameTable);
        if (blocksize <= 64) {
            this.m_data = new SuballocatedIntVector(blocksize, 4);
            this.m_dataOrQName = new SuballocatedIntVector(blocksize, 4);
            this.m_valuesOrPrefixes = new DTMStringPool(16);
            this.m_chars = new FastStringBuffer(7, 10);
            this.m_contextIndexes = new IntStack(4);
            this.m_parents = new IntStack(4);
        } else {
            this.m_data = new SuballocatedIntVector(blocksize, 32);
            this.m_dataOrQName = new SuballocatedIntVector(blocksize, 32);
            this.m_valuesOrPrefixes = new DTMStringPool();
            this.m_chars = new FastStringBuffer(10, 13);
            this.m_contextIndexes = new IntStack();
            this.m_parents = new IntStack();
        }
        this.m_data.addElement(0);
        this.m_useSourceLocationProperty = mgr.getSource_location();
        this.m_sourceSystemId = this.m_useSourceLocationProperty ? new StringVector() : null;
        this.m_sourceLine = this.m_useSourceLocationProperty ? new IntVector() : null;
        this.m_sourceColumn = this.m_useSourceLocationProperty ? new IntVector() : null;
    }

    public void setUseSourceLocation(boolean useSourceLocation) {
        this.m_useSourceLocationProperty = useSourceLocation;
    }

    protected int _dataOrQName(int identity) {
        if (identity < this.m_size) {
            return this.m_dataOrQName.elementAt(identity);
        }
        do {
            boolean isMore;
            if (isMore = this.nextNode()) continue;
            return -1;
        } while (identity >= this.m_size);
        return this.m_dataOrQName.elementAt(identity);
    }

    public void clearCoRoutine() {
        this.clearCoRoutine(true);
    }

    public void clearCoRoutine(boolean callDoTerminate) {
        if (null != this.m_incrementalSAXSource) {
            if (callDoTerminate) {
                this.m_incrementalSAXSource.deliverMoreNodes(false);
            }
            this.m_incrementalSAXSource = null;
        }
    }

    public void setIncrementalSAXSource(IncrementalSAXSource incrementalSAXSource) {
        this.m_incrementalSAXSource = incrementalSAXSource;
        incrementalSAXSource.setContentHandler(this);
        incrementalSAXSource.setLexicalHandler(this);
        incrementalSAXSource.setDTDHandler(this);
    }

    @Override
    public ContentHandler getContentHandler() {
        if (this.m_incrementalSAXSource instanceof IncrementalSAXSource_Filter) {
            return (ContentHandler)((Object)this.m_incrementalSAXSource);
        }
        return this;
    }

    @Override
    public LexicalHandler getLexicalHandler() {
        if (this.m_incrementalSAXSource instanceof IncrementalSAXSource_Filter) {
            return (LexicalHandler)((Object)this.m_incrementalSAXSource);
        }
        return this;
    }

    @Override
    public EntityResolver getEntityResolver() {
        return this;
    }

    @Override
    public DTDHandler getDTDHandler() {
        return this;
    }

    @Override
    public ErrorHandler getErrorHandler() {
        return this;
    }

    @Override
    public DeclHandler getDeclHandler() {
        return this;
    }

    @Override
    public boolean needsTwoThreads() {
        return null != this.m_incrementalSAXSource;
    }

    @Override
    public void dispatchCharactersEvents(int nodeHandle, ContentHandler ch, boolean normalize) throws SAXException {
        int identity = this.makeNodeIdentity(nodeHandle);
        if (identity == -1) {
            return;
        }
        short type = this._type(identity);
        if (this.isTextType(type)) {
            int dataIndex = this.m_dataOrQName.elementAt(identity);
            int offset = this.m_data.elementAt(dataIndex);
            int length = this.m_data.elementAt(dataIndex + 1);
            if (normalize) {
                this.m_chars.sendNormalizedSAXcharacters(ch, offset, length);
            } else {
                this.m_chars.sendSAXcharacters(ch, offset, length);
            }
        } else {
            int firstChild = this._firstch(identity);
            if (-1 != firstChild) {
                int offset = -1;
                int length = 0;
                int startNode = identity;
                identity = firstChild;
                do {
                    if (!this.isTextType(type = this._type(identity))) continue;
                    int dataIndex = this._dataOrQName(identity);
                    if (-1 == offset) {
                        offset = this.m_data.elementAt(dataIndex);
                    }
                    length += this.m_data.elementAt(dataIndex + 1);
                } while (-1 != (identity = this.getNextNodeIdentity(identity)) && this._parent(identity) >= startNode);
                if (length > 0) {
                    if (normalize) {
                        this.m_chars.sendNormalizedSAXcharacters(ch, offset, length);
                    } else {
                        this.m_chars.sendSAXcharacters(ch, offset, length);
                    }
                }
            } else if (type != 1) {
                int dataIndex = this._dataOrQName(identity);
                if (dataIndex < 0) {
                    dataIndex = -dataIndex;
                    dataIndex = this.m_data.elementAt(dataIndex + 1);
                }
                String str = this.m_valuesOrPrefixes.indexToString(dataIndex);
                if (normalize) {
                    FastStringBuffer.sendNormalizedSAXcharacters(str.toCharArray(), 0, str.length(), ch);
                } else {
                    ch.characters(str.toCharArray(), 0, str.length());
                }
            }
        }
    }

    @Override
    public String getNodeName(int nodeHandle) {
        int expandedTypeID = this.getExpandedTypeID(nodeHandle);
        int namespaceID = this.m_expandedNameTable.getNamespaceID(expandedTypeID);
        if (0 == namespaceID) {
            short type = this.getNodeType(nodeHandle);
            if (type == 13) {
                if (null == this.m_expandedNameTable.getLocalName(expandedTypeID)) {
                    return "xmlns";
                }
                return "xmlns:" + this.m_expandedNameTable.getLocalName(expandedTypeID);
            }
            if (0 == this.m_expandedNameTable.getLocalNameID(expandedTypeID)) {
                return m_fixednames[type];
            }
            return this.m_expandedNameTable.getLocalName(expandedTypeID);
        }
        int qnameIndex = this.m_dataOrQName.elementAt(this.makeNodeIdentity(nodeHandle));
        if (qnameIndex < 0) {
            qnameIndex = -qnameIndex;
            qnameIndex = this.m_data.elementAt(qnameIndex);
        }
        return this.m_valuesOrPrefixes.indexToString(qnameIndex);
    }

    @Override
    public String getNodeNameX(int nodeHandle) {
        int expandedTypeID = this.getExpandedTypeID(nodeHandle);
        int namespaceID = this.m_expandedNameTable.getNamespaceID(expandedTypeID);
        if (0 == namespaceID) {
            String name = this.m_expandedNameTable.getLocalName(expandedTypeID);
            if (name == null) {
                return "";
            }
            return name;
        }
        int qnameIndex = this.m_dataOrQName.elementAt(this.makeNodeIdentity(nodeHandle));
        if (qnameIndex < 0) {
            qnameIndex = -qnameIndex;
            qnameIndex = this.m_data.elementAt(qnameIndex);
        }
        return this.m_valuesOrPrefixes.indexToString(qnameIndex);
    }

    @Override
    public boolean isAttributeSpecified(int attributeHandle) {
        return true;
    }

    @Override
    public String getDocumentTypeDeclarationSystemIdentifier() {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }

    @Override
    protected int getNextNodeIdentity(int identity) {
        ++identity;
        while (identity >= this.m_size) {
            if (null == this.m_incrementalSAXSource) {
                return -1;
            }
            this.nextNode();
        }
        return identity;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public void dispatchToEvents(int nodeHandle, ContentHandler ch) throws SAXException {
        DTMTreeWalker treeWalker = this.m_walker;
        ContentHandler prevCH = treeWalker.getcontentHandler();
        if (null != prevCH) {
            treeWalker = new DTMTreeWalker();
        }
        treeWalker.setcontentHandler(ch);
        treeWalker.setDTM(this);
        try {
            treeWalker.traverse(nodeHandle);
        }
        finally {
            treeWalker.setcontentHandler(null);
        }
    }

    @Override
    public int getNumberOfNodes() {
        return this.m_size;
    }

    @Override
    protected boolean nextNode() {
        if (null == this.m_incrementalSAXSource) {
            return false;
        }
        if (this.m_endDocumentOccured) {
            this.clearCoRoutine();
            return false;
        }
        Object gotMore = this.m_incrementalSAXSource.deliverMoreNodes(true);
        if (!(gotMore instanceof Boolean)) {
            if (gotMore instanceof RuntimeException) {
                throw (RuntimeException)gotMore;
            }
            if (gotMore instanceof Exception) {
                throw new WrappedRuntimeException((Exception)gotMore);
            }
            this.clearCoRoutine();
            return false;
        }
        if (gotMore != Boolean.TRUE) {
            this.clearCoRoutine();
        }
        return true;
    }

    private final boolean isTextType(int type) {
        return 3 == type || 4 == type;
    }

    protected int addNode(int type, int expandedTypeID, int parentIndex, int previousSibling, int dataOrPrefix, boolean canHaveFirstChild) {
        int nodeIndex = this.m_size++;
        if (this.m_dtmIdent.size() == nodeIndex >>> 16) {
            this.addNewDTMID(nodeIndex);
        }
        this.m_firstch.addElement(canHaveFirstChild ? -2 : -1);
        this.m_nextsib.addElement(-2);
        this.m_parent.addElement(parentIndex);
        this.m_exptype.addElement(expandedTypeID);
        this.m_dataOrQName.addElement(dataOrPrefix);
        if (this.m_prevsib != null) {
            this.m_prevsib.addElement(previousSibling);
        }
        if (-1 != previousSibling) {
            this.m_nextsib.setElementAt(nodeIndex, previousSibling);
        }
        if (this.m_locator != null && this.m_useSourceLocationProperty) {
            this.setSourceLocation();
        }
        switch (type) {
            case 13: {
                this.declareNamespaceInContext(parentIndex, nodeIndex);
                break;
            }
            case 2: {
                break;
            }
            default: {
                if (-1 != previousSibling || -1 == parentIndex) break;
                this.m_firstch.setElementAt(nodeIndex, parentIndex);
            }
        }
        return nodeIndex;
    }

    protected void addNewDTMID(int nodeIndex) {
        try {
            if (this.m_mgr == null) {
                throw new ClassCastException();
            }
            DTMManagerDefault mgrD = (DTMManagerDefault)this.m_mgr;
            int id = mgrD.getFirstFreeDTMID();
            mgrD.addDTM(this, id, nodeIndex);
            this.m_dtmIdent.addElement(id << 16);
        }
        catch (ClassCastException e) {
            this.error(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
        }
    }

    @Override
    public void migrateTo(DTMManager manager) {
        super.migrateTo(manager);
        int numDTMs = this.m_dtmIdent.size();
        int dtmId = this.m_mgrDefault.getFirstFreeDTMID();
        int nodeIndex = 0;
        for (int i = 0; i < numDTMs; ++i) {
            this.m_dtmIdent.setElementAt(dtmId << 16, i);
            this.m_mgrDefault.addDTM(this, dtmId, nodeIndex);
            ++dtmId;
            nodeIndex += 65536;
        }
    }

    protected void setSourceLocation() {
        this.m_sourceSystemId.addElement(this.m_locator.getSystemId());
        this.m_sourceLine.addElement(this.m_locator.getLineNumber());
        this.m_sourceColumn.addElement(this.m_locator.getColumnNumber());
        if (this.m_sourceSystemId.size() != this.m_size) {
            String msg = "CODING ERROR in Source Location: " + this.m_size + " != " + this.m_sourceSystemId.size();
            System.err.println(msg);
            throw new RuntimeException(msg);
        }
    }

    @Override
    public String getNodeValue(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        short type = this._type(identity);
        if (this.isTextType(type)) {
            int dataIndex = this._dataOrQName(identity);
            int offset = this.m_data.elementAt(dataIndex);
            int length = this.m_data.elementAt(dataIndex + 1);
            return this.m_chars.getString(offset, length);
        }
        if (1 == type || 11 == type || 9 == type) {
            return null;
        }
        int dataIndex = this._dataOrQName(identity);
        if (dataIndex < 0) {
            dataIndex = -dataIndex;
            dataIndex = this.m_data.elementAt(dataIndex + 1);
        }
        return this.m_valuesOrPrefixes.indexToString(dataIndex);
    }

    @Override
    public String getLocalName(int nodeHandle) {
        return this.m_expandedNameTable.getLocalName(this._exptype(this.makeNodeIdentity(nodeHandle)));
    }

    @Override
    public String getUnparsedEntityURI(String name) {
        String url = "";
        if (null == this.m_entities) {
            return url;
        }
        int n = this.m_entities.size();
        for (int i = 0; i < n; i += 4) {
            String ename = (String)this.m_entities.elementAt(i + 3);
            if (null == ename || !ename.equals(name)) continue;
            String nname = (String)this.m_entities.elementAt(i + 2);
            if (null == nname || null != (url = (String)this.m_entities.elementAt(i + 1))) break;
            url = (String)this.m_entities.elementAt(i + 0);
            break;
        }
        return url;
    }

    @Override
    public String getPrefix(int nodeHandle) {
        int prefixIndex;
        int identity = this.makeNodeIdentity(nodeHandle);
        short type = this._type(identity);
        if (1 == type) {
            int prefixIndex2 = this._dataOrQName(identity);
            if (0 == prefixIndex2) {
                return "";
            }
            String qname = this.m_valuesOrPrefixes.indexToString(prefixIndex2);
            return this.getPrefix(qname, null);
        }
        if (2 == type && (prefixIndex = this._dataOrQName(identity)) < 0) {
            prefixIndex = this.m_data.elementAt(-prefixIndex);
            String qname = this.m_valuesOrPrefixes.indexToString(prefixIndex);
            return this.getPrefix(qname, null);
        }
        return "";
    }

    @Override
    public int getAttributeNode(int nodeHandle, String namespaceURI, String name) {
        int attrH = this.getFirstAttribute(nodeHandle);
        while (-1 != attrH) {
            boolean nsMatch;
            String attrNS = this.getNamespaceURI(attrH);
            String attrName = this.getLocalName(attrH);
            boolean bl = nsMatch = namespaceURI == attrNS || namespaceURI != null && namespaceURI.equals(attrNS);
            if (nsMatch && name.equals(attrName)) {
                return attrH;
            }
            attrH = this.getNextAttribute(attrH);
        }
        return -1;
    }

    @Override
    public String getDocumentTypeDeclarationPublicIdentifier() {
        this.error(XMLMessages.createXMLMessage("ER_METHOD_NOT_SUPPORTED", null));
        return null;
    }

    @Override
    public String getNamespaceURI(int nodeHandle) {
        return this.m_expandedNameTable.getNamespace(this._exptype(this.makeNodeIdentity(nodeHandle)));
    }

    @Override
    public XMLString getStringValue(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        int type = identity == -1 ? -1 : (int)this._type(identity);
        if (this.isTextType(type)) {
            int dataIndex = this._dataOrQName(identity);
            int offset = this.m_data.elementAt(dataIndex);
            int length = this.m_data.elementAt(dataIndex + 1);
            return this.m_xstrf.newstr(this.m_chars, offset, length);
        }
        int firstChild = this._firstch(identity);
        if (-1 != firstChild) {
            int offset = -1;
            int length = 0;
            int startNode = identity;
            identity = firstChild;
            do {
                if (!this.isTextType(type = (int)this._type(identity))) continue;
                int dataIndex = this._dataOrQName(identity);
                if (-1 == offset) {
                    offset = this.m_data.elementAt(dataIndex);
                }
                length += this.m_data.elementAt(dataIndex + 1);
            } while (-1 != (identity = this.getNextNodeIdentity(identity)) && this._parent(identity) >= startNode);
            if (length > 0) {
                return this.m_xstrf.newstr(this.m_chars, offset, length);
            }
        } else if (type != 1) {
            int dataIndex = this._dataOrQName(identity);
            if (dataIndex < 0) {
                dataIndex = -dataIndex;
                dataIndex = this.m_data.elementAt(dataIndex + 1);
            }
            return this.m_xstrf.newstr(this.m_valuesOrPrefixes.indexToString(dataIndex));
        }
        return this.m_xstrf.emptystr();
    }

    public boolean isWhitespace(int nodeHandle) {
        int identity = this.makeNodeIdentity(nodeHandle);
        int type = identity == -1 ? -1 : (int)this._type(identity);
        if (this.isTextType(type)) {
            int dataIndex = this._dataOrQName(identity);
            int offset = this.m_data.elementAt(dataIndex);
            int length = this.m_data.elementAt(dataIndex + 1);
            return this.m_chars.isWhitespace(offset, length);
        }
        return false;
    }

    @Override
    public int getElementById(String elementId) {
        Integer intObj;
        boolean isMore = true;
        do {
            if (null != (intObj = (Integer)this.m_idAttributes.get(elementId))) {
                return this.makeNodeHandle(intObj);
            }
            if (!isMore || this.m_endDocumentOccured) break;
            isMore = this.nextNode();
        } while (null == intObj);
        return -1;
    }

    public String getPrefix(String qname, String uri) {
        String prefix;
        int uriIndex = -1;
        if (null != uri && uri.length() > 0) {
            do {
                ++uriIndex;
            } while (((uriIndex = this.m_prefixMappings.indexOf(uri, uriIndex)) & 1) == 0);
            if (uriIndex >= 0) {
                prefix = (String)this.m_prefixMappings.elementAt(uriIndex - 1);
            } else if (null != qname) {
                int indexOfNSSep = qname.indexOf(58);
                prefix = qname.equals("xmlns") ? "" : (qname.startsWith("xmlns:") ? qname.substring(indexOfNSSep + 1) : (indexOfNSSep > 0 ? qname.substring(0, indexOfNSSep) : null));
            } else {
                prefix = null;
            }
        } else {
            int indexOfNSSep;
            prefix = null != qname ? ((indexOfNSSep = qname.indexOf(58)) > 0 ? (qname.startsWith("xmlns:") ? qname.substring(indexOfNSSep + 1) : qname.substring(0, indexOfNSSep)) : (qname.equals("xmlns") ? "" : null)) : null;
        }
        return prefix;
    }

    public int getIdForNamespace(String uri) {
        return this.m_valuesOrPrefixes.stringToIndex(uri);
    }

    public String getNamespaceURI(String prefix) {
        String uri = "";
        int prefixIndex = this.m_contextIndexes.peek() - 1;
        if (null == prefix) {
            prefix = "";
        }
        do {
            ++prefixIndex;
        } while ((prefixIndex = this.m_prefixMappings.indexOf(prefix, prefixIndex)) >= 0 && (prefixIndex & 1) == 1);
        if (prefixIndex > -1) {
            uri = (String)this.m_prefixMappings.elementAt(prefixIndex + 1);
        }
        return uri;
    }

    public void setIDAttribute(String id, int elem) {
        this.m_idAttributes.put(id, new Integer(elem));
    }

    protected void charactersFlush() {
        if (this.m_textPendingStart >= 0) {
            int length = this.m_chars.size() - this.m_textPendingStart;
            boolean doStrip = false;
            if (this.getShouldStripWhitespace()) {
                doStrip = this.m_chars.isWhitespace(this.m_textPendingStart, length);
            }
            if (doStrip) {
                this.m_chars.setLength(this.m_textPendingStart);
            } else if (length > 0) {
                int exName = this.m_expandedNameTable.getExpandedTypeID(3);
                int dataIndex = this.m_data.size();
                this.m_previous = this.addNode(this.m_coalescedTextType, exName, this.m_parents.peek(), this.m_previous, dataIndex, false);
                this.m_data.addElement(this.m_textPendingStart);
                this.m_data.addElement(length);
            }
            this.m_textPendingStart = -1;
            this.m_coalescedTextType = 3;
            this.m_textType = 3;
        }
    }

    @Override
    public InputSource resolveEntity(String publicId, String systemId) throws SAXException {
        return null;
    }

    @Override
    public void notationDecl(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void unparsedEntityDecl(String name, String publicId, String systemId, String notationName) throws SAXException {
        if (null == this.m_entities) {
            this.m_entities = new Vector();
        }
        try {
            systemId = SystemIDResolver.getAbsoluteURI(systemId, this.getDocumentBaseURI());
        }
        catch (Exception e) {
            throw new SAXException(e);
        }
        this.m_entities.addElement(publicId);
        this.m_entities.addElement(systemId);
        this.m_entities.addElement(notationName);
        this.m_entities.addElement(name);
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        this.m_locator = locator;
        this.m_systemId = locator.getSystemId();
    }

    @Override
    public void startDocument() throws SAXException {
        int doc = this.addNode(9, this.m_expandedNameTable.getExpandedTypeID(9), -1, -1, 0, true);
        this.m_parents.push(doc);
        this.m_previous = -1;
        this.m_contextIndexes.push(this.m_prefixMappings.size());
    }

    @Override
    public void endDocument() throws SAXException {
        this.charactersFlush();
        this.m_nextsib.setElementAt(-1, 0);
        if (this.m_firstch.elementAt(0) == -2) {
            this.m_firstch.setElementAt(-1, 0);
        }
        if (-1 != this.m_previous) {
            this.m_nextsib.setElementAt(-1, this.m_previous);
        }
        this.m_parents = null;
        this.m_prefixMappings = null;
        this.m_contextIndexes = null;
        this.m_endDocumentOccured = true;
        this.m_locator = null;
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (null == prefix) {
            prefix = "";
        }
        this.m_prefixMappings.addElement(prefix);
        this.m_prefixMappings.addElement(uri);
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        if (null == prefix) {
            prefix = "";
        }
        int index = this.m_contextIndexes.peek() - 1;
        do {
            ++index;
        } while ((index = this.m_prefixMappings.indexOf(prefix, index)) >= 0 && (index & 1) == 1);
        if (index > -1) {
            this.m_prefixMappings.setElementAt("%@$#^@#", index);
            this.m_prefixMappings.setElementAt("%@$#^@#", index + 1);
        }
    }

    protected boolean declAlreadyDeclared(String prefix) {
        int startDecls = this.m_contextIndexes.peek();
        Vector prefixMappings = this.m_prefixMappings;
        int nDecls = prefixMappings.size();
        for (int i = startDecls; i < nDecls; i += 2) {
            String prefixDecl = (String)prefixMappings.elementAt(i);
            if (prefixDecl == null || !prefixDecl.equals(prefix)) continue;
            return true;
        }
        return false;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        this.charactersFlush();
        int exName = this.m_expandedNameTable.getExpandedTypeID(uri, localName, 1);
        String prefix = this.getPrefix(qName, uri);
        int prefixIndex = null != prefix ? this.m_valuesOrPrefixes.stringToIndex(qName) : 0;
        int elemNode = this.addNode(1, exName, this.m_parents.peek(), this.m_previous, prefixIndex, true);
        if (this.m_indexing) {
            this.indexNode(exName, elemNode);
        }
        this.m_parents.push(elemNode);
        int startDecls = this.m_contextIndexes.peek();
        int nDecls = this.m_prefixMappings.size();
        int prev = -1;
        if (!this.m_pastFirstElement) {
            prefix = "xml";
            String declURL = "http://www.w3.org/XML/1998/namespace";
            exName = this.m_expandedNameTable.getExpandedTypeID(null, prefix, 13);
            int val = this.m_valuesOrPrefixes.stringToIndex(declURL);
            prev = this.addNode(13, exName, elemNode, prev, val, false);
            this.m_pastFirstElement = true;
        }
        for (int i = startDecls; i < nDecls; i += 2) {
            prefix = (String)this.m_prefixMappings.elementAt(i);
            if (prefix == null) continue;
            String declURL = (String)this.m_prefixMappings.elementAt(i + 1);
            exName = this.m_expandedNameTable.getExpandedTypeID(null, prefix, 13);
            int val = this.m_valuesOrPrefixes.stringToIndex(declURL);
            prev = this.addNode(13, exName, elemNode, prev, val, false);
        }
        int n = attributes.getLength();
        for (int i = 0; i < n; ++i) {
            int nodeType;
            String attrUri = attributes.getURI(i);
            String attrQName = attributes.getQName(i);
            String valString = attributes.getValue(i);
            prefix = this.getPrefix(attrQName, attrUri);
            String attrLocalName = attributes.getLocalName(i);
            if (null != attrQName && (attrQName.equals("xmlns") || attrQName.startsWith("xmlns:"))) {
                if (this.declAlreadyDeclared(prefix)) continue;
                nodeType = 13;
            } else {
                nodeType = 2;
                if (attributes.getType(i).equalsIgnoreCase("ID")) {
                    this.setIDAttribute(valString, elemNode);
                }
            }
            if (null == valString) {
                valString = "";
            }
            int val = this.m_valuesOrPrefixes.stringToIndex(valString);
            if (null != prefix) {
                prefixIndex = this.m_valuesOrPrefixes.stringToIndex(attrQName);
                int dataIndex = this.m_data.size();
                this.m_data.addElement(prefixIndex);
                this.m_data.addElement(val);
                val = -dataIndex;
            }
            exName = this.m_expandedNameTable.getExpandedTypeID(attrUri, attrLocalName, nodeType);
            prev = this.addNode(nodeType, exName, elemNode, prev, val, false);
        }
        if (-1 != prev) {
            this.m_nextsib.setElementAt(-1, prev);
        }
        if (null != this.m_wsfilter) {
            short wsv = this.m_wsfilter.getShouldStripSpace(this.makeNodeHandle(elemNode), this);
            boolean shouldStrip = 3 == wsv ? this.getShouldStripWhitespace() : 2 == wsv;
            this.pushShouldStripWhitespace(shouldStrip);
        }
        this.m_previous = -1;
        this.m_contextIndexes.push(this.m_prefixMappings.size());
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        this.charactersFlush();
        this.m_contextIndexes.quickPop(1);
        int topContextIndex = this.m_contextIndexes.peek();
        if (topContextIndex != this.m_prefixMappings.size()) {
            this.m_prefixMappings.setSize(topContextIndex);
        }
        int lastNode = this.m_previous;
        this.m_previous = this.m_parents.pop();
        if (-1 == lastNode) {
            this.m_firstch.setElementAt(-1, this.m_previous);
        } else {
            this.m_nextsib.setElementAt(-1, lastNode);
        }
        this.popShouldStripWhitespace();
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.m_textPendingStart == -1) {
            this.m_textPendingStart = this.m_chars.size();
            this.m_coalescedTextType = this.m_textType;
        } else if (this.m_textType == 3) {
            this.m_coalescedTextType = 3;
        }
        this.m_chars.append(ch, start, length);
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        this.characters(ch, start, length);
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        this.charactersFlush();
        int exName = this.m_expandedNameTable.getExpandedTypeID(null, target, 7);
        int dataIndex = this.m_valuesOrPrefixes.stringToIndex(data);
        this.m_previous = this.addNode(7, exName, this.m_parents.peek(), this.m_previous, dataIndex, false);
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
    }

    @Override
    public void warning(SAXParseException e) throws SAXException {
        System.err.println(e.getMessage());
    }

    @Override
    public void error(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void fatalError(SAXParseException e) throws SAXException {
        throw e;
    }

    @Override
    public void elementDecl(String name, String model) throws SAXException {
    }

    @Override
    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
    }

    @Override
    public void internalEntityDecl(String name, String value) throws SAXException {
    }

    @Override
    public void externalEntityDecl(String name, String publicId, String systemId) throws SAXException {
    }

    @Override
    public void startDTD(String name, String publicId, String systemId) throws SAXException {
        this.m_insideDTD = true;
    }

    @Override
    public void endDTD() throws SAXException {
        this.m_insideDTD = false;
    }

    @Override
    public void startEntity(String name) throws SAXException {
    }

    @Override
    public void endEntity(String name) throws SAXException {
    }

    @Override
    public void startCDATA() throws SAXException {
        this.m_textType = 4;
    }

    @Override
    public void endCDATA() throws SAXException {
        this.m_textType = 3;
    }

    @Override
    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.m_insideDTD) {
            return;
        }
        this.charactersFlush();
        int exName = this.m_expandedNameTable.getExpandedTypeID(8);
        int dataIndex = this.m_valuesOrPrefixes.stringToIndex(new String(ch, start, length));
        this.m_previous = this.addNode(8, exName, this.m_parents.peek(), this.m_previous, dataIndex, false);
    }

    @Override
    public void setProperty(String property, Object value) {
    }

    @Override
    public SourceLocator getSourceLocatorFor(int node) {
        if (this.m_useSourceLocationProperty) {
            node = this.makeNodeIdentity(node);
            return new NodeLocator(null, this.m_sourceSystemId.elementAt(node), this.m_sourceLine.elementAt(node), this.m_sourceColumn.elementAt(node));
        }
        if (this.m_locator != null) {
            return new NodeLocator(null, this.m_locator.getSystemId(), -1, -1);
        }
        if (this.m_systemId != null) {
            return new NodeLocator(null, this.m_systemId, -1, -1);
        }
        return null;
    }

    public String getFixedNames(int type) {
        return m_fixednames[type];
    }
}


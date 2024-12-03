/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xml.dtm.ref;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamSource;
import org.apache.xml.dtm.DTM;
import org.apache.xml.dtm.DTMException;
import org.apache.xml.dtm.DTMFilter;
import org.apache.xml.dtm.DTMIterator;
import org.apache.xml.dtm.DTMManager;
import org.apache.xml.dtm.DTMWSFilter;
import org.apache.xml.dtm.ref.DTMDefaultBase;
import org.apache.xml.dtm.ref.DTMNodeProxy;
import org.apache.xml.dtm.ref.ExpandedNameTable;
import org.apache.xml.dtm.ref.IncrementalSAXSource;
import org.apache.xml.dtm.ref.IncrementalSAXSource_Filter;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTM;
import org.apache.xml.dtm.ref.dom2dtm.DOM2DTMdefaultNamespaceDeclarationNode;
import org.apache.xml.dtm.ref.sax2dtm.SAX2DTM;
import org.apache.xml.dtm.ref.sax2dtm.SAX2RTFDTM;
import org.apache.xml.res.XMLMessages;
import org.apache.xml.utils.PrefixResolver;
import org.apache.xml.utils.SuballocatedIntVector;
import org.apache.xml.utils.SystemIDResolver;
import org.apache.xml.utils.WrappedRuntimeException;
import org.apache.xml.utils.XMLReaderManager;
import org.apache.xml.utils.XMLStringFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Node;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

public class DTMManagerDefault
extends DTMManager {
    private static final boolean DUMPTREE = false;
    private static final boolean DEBUG = false;
    protected DTM[] m_dtms = new DTM[256];
    int[] m_dtm_offsets = new int[256];
    protected XMLReaderManager m_readerManager = null;
    protected DefaultHandler m_defaultHandler = new DefaultHandler();
    private ExpandedNameTable m_expandedNameTable = new ExpandedNameTable();

    public synchronized void addDTM(DTM dtm, int id) {
        this.addDTM(dtm, id, 0);
    }

    public synchronized void addDTM(DTM dtm, int id, int offset) {
        if (id >= 65536) {
            throw new DTMException(XMLMessages.createXMLMessage("ER_NO_DTMIDS_AVAIL", null));
        }
        int oldlen = this.m_dtms.length;
        if (oldlen <= id) {
            int newlen = Math.min(id + 256, 65536);
            DTM[] new_m_dtms = new DTM[newlen];
            System.arraycopy(this.m_dtms, 0, new_m_dtms, 0, oldlen);
            this.m_dtms = new_m_dtms;
            int[] new_m_dtm_offsets = new int[newlen];
            System.arraycopy(this.m_dtm_offsets, 0, new_m_dtm_offsets, 0, oldlen);
            this.m_dtm_offsets = new_m_dtm_offsets;
        }
        this.m_dtms[id] = dtm;
        this.m_dtm_offsets[id] = offset;
        dtm.documentRegistration();
    }

    public synchronized int getFirstFreeDTMID() {
        int n = this.m_dtms.length;
        for (int i = 1; i < n; ++i) {
            if (null != this.m_dtms[i]) continue;
            return i;
        }
        return n;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public synchronized DTM getDTM(Source source, boolean unique, DTMWSFilter whiteSpaceFilter, boolean incremental, boolean doIndexing) {
        boolean isStreamSource;
        XMLStringFactory xstringFactory = this.m_xsf;
        int dtmPos = this.getFirstFreeDTMID();
        int documentID = dtmPos << 16;
        if (null != source && source instanceof DOMSource) {
            DOM2DTM dtm = new DOM2DTM((DTMManager)this, (DOMSource)source, documentID, whiteSpaceFilter, xstringFactory, doIndexing);
            this.addDTM(dtm, dtmPos, 0);
            return dtm;
        }
        boolean isSAXSource = null != source ? source instanceof SAXSource : true;
        boolean bl = isStreamSource = null != source ? source instanceof StreamSource : false;
        if (isSAXSource || isStreamSource) {
            XMLReader reader = null;
            try {
                boolean haveXercesParser;
                InputSource xmlSource;
                if (null == source) {
                    xmlSource = null;
                } else {
                    reader = this.getXMLReader(source);
                    xmlSource = SAXSource.sourceToInputSource(source);
                    String urlOfSource = xmlSource.getSystemId();
                    if (null != urlOfSource) {
                        try {
                            urlOfSource = SystemIDResolver.getAbsoluteURI(urlOfSource);
                        }
                        catch (Exception e) {
                            System.err.println("Can not absolutize URL: " + urlOfSource);
                        }
                        xmlSource.setSystemId(urlOfSource);
                    }
                }
                SAX2DTM dtm = source == null && unique && !incremental && !doIndexing ? new SAX2RTFDTM(this, source, documentID, whiteSpaceFilter, xstringFactory, doIndexing) : new SAX2DTM(this, source, documentID, whiteSpaceFilter, xstringFactory, doIndexing);
                this.addDTM(dtm, dtmPos, 0);
                boolean bl2 = haveXercesParser = null != reader && reader.getClass().getName().equals("org.apache.xerces.parsers.SAXParser");
                if (haveXercesParser) {
                    incremental = true;
                }
                if (this.m_incremental && incremental) {
                    ErrorHandler filter;
                    IncrementalSAXSource coParser = null;
                    if (haveXercesParser) {
                        try {
                            coParser = (IncrementalSAXSource)Class.forName("org.apache.xml.dtm.ref.IncrementalSAXSource_Xerces").newInstance();
                        }
                        catch (Exception ex) {
                            ex.printStackTrace();
                            coParser = null;
                        }
                    }
                    if (coParser == null) {
                        if (null == reader) {
                            coParser = new IncrementalSAXSource_Filter();
                        } else {
                            filter = new IncrementalSAXSource_Filter();
                            filter.setXMLReader(reader);
                            coParser = filter;
                        }
                    }
                    dtm.setIncrementalSAXSource(coParser);
                    if (null == xmlSource) {
                        filter = dtm;
                        return filter;
                    }
                    if (null == reader.getErrorHandler()) {
                        reader.setErrorHandler(dtm);
                    }
                    reader.setDTDHandler(dtm);
                    try {
                        coParser.startParse(xmlSource);
                    }
                    catch (RuntimeException re) {
                        dtm.clearCoRoutine();
                        throw re;
                    }
                    catch (Exception e) {
                        dtm.clearCoRoutine();
                        throw new WrappedRuntimeException(e);
                    }
                }
                if (null == reader) {
                    SAX2DTM coParser = dtm;
                    return coParser;
                }
                reader.setContentHandler(dtm);
                reader.setDTDHandler(dtm);
                if (null == reader.getErrorHandler()) {
                    reader.setErrorHandler(dtm);
                }
                try {
                    reader.setProperty("http://xml.org/sax/properties/lexical-handler", dtm);
                }
                catch (SAXNotRecognizedException coParser) {
                }
                catch (SAXNotSupportedException coParser) {
                    // empty catch block
                }
                try {
                    reader.parse(xmlSource);
                }
                catch (RuntimeException re) {
                    dtm.clearCoRoutine();
                    throw re;
                }
                catch (Exception e) {
                    dtm.clearCoRoutine();
                    throw new WrappedRuntimeException(e);
                }
                SAX2DTM sAX2DTM = dtm;
                return sAX2DTM;
            }
            finally {
                if (!(reader == null || this.m_incremental && incremental)) {
                    reader.setContentHandler(this.m_defaultHandler);
                    reader.setDTDHandler(this.m_defaultHandler);
                    reader.setErrorHandler(this.m_defaultHandler);
                    try {
                        reader.setProperty("http://xml.org/sax/properties/lexical-handler", null);
                    }
                    catch (Exception exception) {}
                }
                this.releaseXMLReader(reader);
            }
        }
        throw new DTMException(XMLMessages.createXMLMessage("ER_NOT_SUPPORTED", new Object[]{source}));
    }

    @Override
    public synchronized int getDTMHandleFromNode(Node node) {
        int handle;
        Node p;
        if (null == node) {
            throw new IllegalArgumentException(XMLMessages.createXMLMessage("ER_NODE_NON_NULL", null));
        }
        if (node instanceof DTMNodeProxy) {
            return ((DTMNodeProxy)node).getDTMNodeNumber();
        }
        for (DTM thisDTM : this.m_dtms) {
            int handle2;
            if (null == thisDTM || !(thisDTM instanceof DOM2DTM) || (handle2 = ((DOM2DTM)thisDTM).getHandleOfNode(node)) == -1) continue;
            return handle2;
        }
        Node root = node;
        Node node2 = p = root.getNodeType() == 2 ? ((Attr)root).getOwnerElement() : root.getParentNode();
        while (p != null) {
            root = p;
            p = p.getParentNode();
        }
        DOM2DTM dtm = (DOM2DTM)this.getDTM(new DOMSource(root), false, null, true, true);
        if (node instanceof DOM2DTMdefaultNamespaceDeclarationNode) {
            handle = dtm.getHandleOfNode(((Attr)node).getOwnerElement());
            handle = dtm.getAttributeNode(handle, node.getNamespaceURI(), node.getLocalName());
        } else {
            handle = dtm.getHandleOfNode(node);
        }
        if (-1 == handle) {
            throw new RuntimeException(XMLMessages.createXMLMessage("ER_COULD_NOT_RESOLVE_NODE", null));
        }
        return handle;
    }

    public synchronized XMLReader getXMLReader(Source inputSource) {
        try {
            XMLReader reader;
            XMLReader xMLReader = reader = inputSource instanceof SAXSource ? ((SAXSource)inputSource).getXMLReader() : null;
            if (null == reader) {
                if (this.m_readerManager == null) {
                    this.m_readerManager = XMLReaderManager.getInstance();
                }
                reader = this.m_readerManager.getXMLReader();
            }
            return reader;
        }
        catch (SAXException se) {
            throw new DTMException(se.getMessage(), se);
        }
    }

    public synchronized void releaseXMLReader(XMLReader reader) {
        if (this.m_readerManager != null) {
            this.m_readerManager.releaseXMLReader(reader);
        }
    }

    @Override
    public synchronized DTM getDTM(int nodeHandle) {
        try {
            return this.m_dtms[nodeHandle >>> 16];
        }
        catch (ArrayIndexOutOfBoundsException e) {
            if (nodeHandle == -1) {
                return null;
            }
            throw e;
        }
    }

    @Override
    public synchronized int getDTMIdentity(DTM dtm) {
        if (dtm instanceof DTMDefaultBase) {
            DTMDefaultBase dtmdb = (DTMDefaultBase)dtm;
            if (dtmdb.getManager() == this) {
                return dtmdb.getDTMIDs().elementAt(0);
            }
            return -1;
        }
        int n = this.m_dtms.length;
        for (int i = 0; i < n; ++i) {
            DTM tdtm = this.m_dtms[i];
            if (tdtm != dtm || this.m_dtm_offsets[i] != 0) continue;
            return i << 16;
        }
        return -1;
    }

    @Override
    public synchronized boolean release(DTM dtm, boolean shouldHardDelete) {
        if (dtm instanceof SAX2DTM) {
            ((SAX2DTM)dtm).clearCoRoutine();
        }
        if (dtm instanceof DTMDefaultBase) {
            SuballocatedIntVector ids = ((DTMDefaultBase)dtm).getDTMIDs();
            for (int i = ids.size() - 1; i >= 0; --i) {
                this.m_dtms[ids.elementAt((int)i) >>> 16] = null;
            }
        } else {
            int i = this.getDTMIdentity(dtm);
            if (i >= 0) {
                this.m_dtms[i >>> 16] = null;
            }
        }
        dtm.documentRelease();
        return true;
    }

    @Override
    public synchronized DTM createDocumentFragment() {
        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.newDocument();
            DocumentFragment df = doc.createDocumentFragment();
            return this.getDTM(new DOMSource(df), true, null, false, false);
        }
        catch (Exception e) {
            throw new DTMException(e);
        }
    }

    @Override
    public synchronized DTMIterator createDTMIterator(int whatToShow, DTMFilter filter, boolean entityReferenceExpansion) {
        return null;
    }

    @Override
    public synchronized DTMIterator createDTMIterator(String xpathString, PrefixResolver presolver) {
        return null;
    }

    @Override
    public synchronized DTMIterator createDTMIterator(int node) {
        return null;
    }

    @Override
    public synchronized DTMIterator createDTMIterator(Object xpathCompiler, int pos) {
        return null;
    }

    public ExpandedNameTable getExpandedNameTable(DTM dtm) {
        return this.m_expandedNameTable;
    }
}


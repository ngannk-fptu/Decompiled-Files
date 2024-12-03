/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.search;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.search.SearchConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.Namespace;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SearchInfo
implements SearchConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(SearchInfo.class);
    public static final long NRESULTS_UNDEFINED = -1L;
    public static final long OFFSET_UNDEFINED = -1L;
    private static final String LIMIT = "limit";
    private static final String NRESULTS = "nresults";
    private static final String OFFSET = "offset";
    private static final Set<String> IGNORED_NAMESPACES;
    private final String language;
    private final Namespace languageNamespace;
    private final String query;
    private final Map<String, String> namespaces;
    private long nresults = -1L;
    private long offset = -1L;

    public SearchInfo(String language, Namespace languageNamespace, String query, Map<String, String> namespaces) {
        this.language = language;
        this.languageNamespace = languageNamespace;
        this.query = query;
        this.namespaces = Collections.unmodifiableMap(new HashMap<String, String>(namespaces));
    }

    public SearchInfo(String language, Namespace languageNamespace, String query) {
        this(language, languageNamespace, query, Collections.emptyMap());
    }

    public String getLanguageName() {
        return this.language;
    }

    public Namespace getLanguageNameSpace() {
        return this.languageNamespace;
    }

    public String getQuery() {
        return this.query;
    }

    public Map<String, String> getNamespaces() {
        return this.namespaces;
    }

    public long getNumberResults() {
        return this.nresults;
    }

    public void setNumberResults(long nresults) {
        this.nresults = nresults;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    @Override
    public Element toXml(Document document) {
        Element sRequestElem = DomUtil.createElement(document, "searchrequest", NAMESPACE);
        for (String prefix : this.namespaces.keySet()) {
            String uri = this.namespaces.get(prefix);
            DomUtil.setNamespaceAttribute(sRequestElem, prefix, uri);
        }
        DomUtil.addChildElement(sRequestElem, this.language, this.languageNamespace, this.query);
        if (this.nresults != -1L || this.offset != -1L) {
            Element limitE = DomUtil.addChildElement(sRequestElem, LIMIT, NAMESPACE);
            if (this.nresults != -1L) {
                DomUtil.addChildElement(limitE, NRESULTS, NAMESPACE, this.nresults + "");
            }
            if (this.offset != -1L) {
                DomUtil.addChildElement(limitE, OFFSET, Namespace.EMPTY_NAMESPACE, this.offset + "");
            }
        }
        return sRequestElem;
    }

    public static SearchInfo createFromXml(Element searchRequest) throws DavException {
        if (searchRequest == null || !"searchrequest".equals(searchRequest.getLocalName())) {
            log.warn("The root element must be 'searchrequest'.");
            throw new DavException(400);
        }
        Element first = DomUtil.getFirstChildElement(searchRequest);
        Attr[] nsAttributes = DomUtil.getNamespaceAttributes(searchRequest);
        HashMap<String, String> namespaces = new HashMap<String, String>();
        for (Attr nsAttribute : nsAttributes) {
            if (IGNORED_NAMESPACES.contains(nsAttribute.getValue())) continue;
            namespaces.put(nsAttribute.getLocalName(), nsAttribute.getValue());
        }
        if (first == null) {
            log.warn("A single child element is expected with the 'DAV:searchrequest'.");
            throw new DavException(400);
        }
        SearchInfo sInfo = new SearchInfo(first.getLocalName(), DomUtil.getNamespace(first), DomUtil.getText(first), namespaces);
        Element limit = DomUtil.getChildElement(searchRequest, LIMIT, NAMESPACE);
        if (limit != null) {
            String offset;
            String nresults = DomUtil.getChildTextTrim(limit, NRESULTS, NAMESPACE);
            if (nresults != null) {
                try {
                    sInfo.setNumberResults(Long.valueOf(nresults));
                }
                catch (NumberFormatException e) {
                    log.error("DAV:nresults cannot be parsed into a long -> ignore.");
                }
            }
            if ((offset = DomUtil.getChildTextTrim(limit, OFFSET, Namespace.EMPTY_NAMESPACE)) != null) {
                try {
                    sInfo.setOffset(Long.valueOf(offset));
                }
                catch (NumberFormatException e) {
                    log.error("'offset' cannot be parsed into a long -> ignore.");
                }
            }
        }
        return sInfo;
    }

    static {
        HashSet<String> s = new HashSet<String>();
        s.add(Namespace.XMLNS_NAMESPACE.getURI());
        s.add(Namespace.XML_NAMESPACE.getURI());
        s.add(DavConstants.NAMESPACE.getURI());
        IGNORED_NAMESPACES = Collections.unmodifiableSet(s);
    }
}


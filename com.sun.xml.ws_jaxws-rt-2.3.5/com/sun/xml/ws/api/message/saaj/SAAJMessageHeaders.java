/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.soap.SOAPException
 *  javax.xml.soap.SOAPHeader
 *  javax.xml.soap.SOAPHeaderElement
 *  javax.xml.soap.SOAPMessage
 */
package com.sun.xml.ws.api.message.saaj;

import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.binding.SOAPBindingImpl;
import com.sun.xml.ws.message.saaj.SAAJHeader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.soap.SOAPException;
import javax.xml.soap.SOAPHeader;
import javax.xml.soap.SOAPHeaderElement;
import javax.xml.soap.SOAPMessage;
import org.w3c.dom.Node;

public class SAAJMessageHeaders
implements MessageHeaders {
    SOAPMessage sm;
    Map<SOAPHeaderElement, Header> nonSAAJHeaders;
    Map<QName, Integer> notUnderstoodCount;
    SOAPVersion soapVersion;
    private Set<QName> understoodHeaders;

    public SAAJMessageHeaders(SOAPMessage sm, SOAPVersion version) {
        this.sm = sm;
        this.soapVersion = version;
        this.initHeaderUnderstanding();
    }

    private void initHeaderUnderstanding() {
        SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return;
        }
        Iterator allHeaders = soapHeader.examineAllHeaderElements();
        while (allHeaders.hasNext()) {
            SOAPHeaderElement nextHdrElem = (SOAPHeaderElement)allHeaders.next();
            if (nextHdrElem == null || !nextHdrElem.getMustUnderstand()) continue;
            this.notUnderstood(nextHdrElem.getElementQName());
        }
    }

    @Override
    public void understood(Header header) {
        this.understood(header.getNamespaceURI(), header.getLocalPart());
    }

    @Override
    public void understood(String nsUri, String localName) {
        this.understood(new QName(nsUri, localName));
    }

    @Override
    public void understood(QName qName) {
        Integer count;
        if (this.notUnderstoodCount == null) {
            this.notUnderstoodCount = new HashMap<QName, Integer>();
        }
        if ((count = this.notUnderstoodCount.get(qName)) != null && count > 0) {
            if ((count = Integer.valueOf(count - 1)) <= 0) {
                this.notUnderstoodCount.remove(qName);
            } else {
                this.notUnderstoodCount.put(qName, count);
            }
        }
        if (this.understoodHeaders == null) {
            this.understoodHeaders = new HashSet<QName>();
        }
        this.understoodHeaders.add(qName);
    }

    @Override
    public boolean isUnderstood(Header header) {
        return this.isUnderstood(header.getNamespaceURI(), header.getLocalPart());
    }

    @Override
    public boolean isUnderstood(String nsUri, String localName) {
        return this.isUnderstood(new QName(nsUri, localName));
    }

    @Override
    public boolean isUnderstood(QName name) {
        if (this.understoodHeaders == null) {
            return false;
        }
        return this.understoodHeaders.contains(name);
    }

    public boolean isUnderstood(int index) {
        return false;
    }

    @Override
    public Header get(String nsUri, String localName, boolean markAsUnderstood) {
        SOAPHeaderElement h = this.find(nsUri, localName);
        if (h != null) {
            if (markAsUnderstood) {
                this.understood(nsUri, localName);
            }
            return new SAAJHeader(h);
        }
        return null;
    }

    @Override
    public Header get(QName name, boolean markAsUnderstood) {
        return this.get(name.getNamespaceURI(), name.getLocalPart(), markAsUnderstood);
    }

    @Override
    public Iterator<Header> getHeaders(QName headerName, boolean markAsUnderstood) {
        return this.getHeaders(headerName.getNamespaceURI(), headerName.getLocalPart(), markAsUnderstood);
    }

    @Override
    public Iterator<Header> getHeaders(String nsUri, String localName, boolean markAsUnderstood) {
        SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        Iterator allHeaders = soapHeader.examineAllHeaderElements();
        if (markAsUnderstood) {
            ArrayList<SAAJHeader> headers = new ArrayList<SAAJHeader>();
            while (allHeaders.hasNext()) {
                SOAPHeaderElement nextHdr = (SOAPHeaderElement)allHeaders.next();
                if (nextHdr == null || !nextHdr.getNamespaceURI().equals(nsUri) || localName != null && !nextHdr.getLocalName().equals(localName)) continue;
                this.understood(nextHdr.getNamespaceURI(), nextHdr.getLocalName());
                headers.add(new SAAJHeader(nextHdr));
            }
            return headers.iterator();
        }
        return new HeaderReadIterator(allHeaders, nsUri, localName);
    }

    @Override
    public Iterator<Header> getHeaders(String nsUri, boolean markAsUnderstood) {
        return this.getHeaders(nsUri, null, markAsUnderstood);
    }

    @Override
    public boolean add(Header header) {
        try {
            header.writeTo(this.sm);
        }
        catch (SOAPException e) {
            return false;
        }
        this.notUnderstood(new QName(header.getNamespaceURI(), header.getLocalPart()));
        if (this.isNonSAAJHeader(header)) {
            this.addNonSAAJHeader(this.find(header.getNamespaceURI(), header.getLocalPart()), header);
        }
        return true;
    }

    @Override
    public Header remove(QName name) {
        return this.remove(name.getNamespaceURI(), name.getLocalPart());
    }

    @Override
    public Header remove(String nsUri, String localName) {
        QName hdrName;
        SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        SOAPHeaderElement headerElem = this.find(nsUri, localName);
        if (headerElem == null) {
            return null;
        }
        headerElem = (SOAPHeaderElement)soapHeader.removeChild((Node)headerElem);
        this.removeNonSAAJHeader(headerElem);
        QName qName = hdrName = nsUri == null ? new QName(localName) : new QName(nsUri, localName);
        if (this.understoodHeaders != null) {
            this.understoodHeaders.remove(hdrName);
        }
        this.removeNotUnderstood(hdrName);
        return new SAAJHeader(headerElem);
    }

    private void removeNotUnderstood(QName hdrName) {
        if (this.notUnderstoodCount == null) {
            return;
        }
        Integer notUnderstood = this.notUnderstoodCount.get(hdrName);
        if (notUnderstood != null) {
            int intNotUnderstood = notUnderstood;
            if (--intNotUnderstood <= 0) {
                this.notUnderstoodCount.remove(hdrName);
            }
        }
    }

    private SOAPHeaderElement find(QName qName) {
        return this.find(qName.getNamespaceURI(), qName.getLocalPart());
    }

    private SOAPHeaderElement find(String nsUri, String localName) {
        SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        Iterator allHeaders = soapHeader.examineAllHeaderElements();
        while (allHeaders.hasNext()) {
            SOAPHeaderElement nextHdrElem = (SOAPHeaderElement)allHeaders.next();
            if (!nextHdrElem.getNamespaceURI().equals(nsUri) || !nextHdrElem.getLocalName().equals(localName)) continue;
            return nextHdrElem;
        }
        return null;
    }

    private void notUnderstood(QName qName) {
        Integer count;
        if (this.notUnderstoodCount == null) {
            this.notUnderstoodCount = new HashMap<QName, Integer>();
        }
        if ((count = this.notUnderstoodCount.get(qName)) == null) {
            this.notUnderstoodCount.put(qName, 1);
        } else {
            this.notUnderstoodCount.put(qName, count + 1);
        }
        if (this.understoodHeaders != null) {
            this.understoodHeaders.remove(qName);
        }
    }

    private SOAPHeader ensureSOAPHeader() {
        try {
            SOAPHeader header = this.sm.getSOAPPart().getEnvelope().getHeader();
            if (header != null) {
                return header;
            }
            return this.sm.getSOAPPart().getEnvelope().addHeader();
        }
        catch (Exception e) {
            return null;
        }
    }

    private boolean isNonSAAJHeader(Header header) {
        return !(header instanceof SAAJHeader);
    }

    private void addNonSAAJHeader(SOAPHeaderElement headerElem, Header header) {
        if (this.nonSAAJHeaders == null) {
            this.nonSAAJHeaders = new HashMap<SOAPHeaderElement, Header>();
        }
        this.nonSAAJHeaders.put(headerElem, header);
    }

    private void removeNonSAAJHeader(SOAPHeaderElement headerElem) {
        if (this.nonSAAJHeaders != null) {
            this.nonSAAJHeaders.remove(headerElem);
        }
    }

    @Override
    public boolean addOrReplace(Header header) {
        this.remove(header.getNamespaceURI(), header.getLocalPart());
        return this.add(header);
    }

    @Override
    public void replace(Header old, Header header) {
        if (this.remove(old.getNamespaceURI(), old.getLocalPart()) == null) {
            throw new IllegalArgumentException();
        }
        this.add(header);
    }

    @Override
    public Set<QName> getUnderstoodHeaders() {
        return this.understoodHeaders;
    }

    @Override
    public Set<QName> getNotUnderstoodHeaders(Set<String> roles, Set<QName> knownHeaders, WSBinding binding) {
        HashSet<QName> notUnderstoodHeaderNames = new HashSet<QName>();
        if (this.notUnderstoodCount == null) {
            return notUnderstoodHeaderNames;
        }
        for (Map.Entry<QName, Integer> header : this.notUnderstoodCount.entrySet()) {
            SOAPHeaderElement hdrElem;
            QName headerName = header.getKey();
            int count = header.getValue();
            if (count <= 0 || !(hdrElem = this.find(headerName)).getMustUnderstand()) continue;
            SAAJHeader hdr = new SAAJHeader(hdrElem);
            boolean understood = false;
            if (roles != null) {
                boolean bl = understood = !roles.contains(hdr.getRole(this.soapVersion));
            }
            if (understood) continue;
            if (binding != null && binding instanceof SOAPBindingImpl && !(understood = ((SOAPBindingImpl)binding).understandsHeader(headerName)) && knownHeaders != null && knownHeaders.contains(headerName)) {
                understood = true;
            }
            if (understood) continue;
            notUnderstoodHeaderNames.add(headerName);
        }
        return notUnderstoodHeaderNames;
    }

    @Override
    public Iterator<Header> getHeaders() {
        SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return null;
        }
        Iterator allHeaders = soapHeader.examineAllHeaderElements();
        return new HeaderReadIterator(allHeaders, null, null);
    }

    @Override
    public boolean hasHeaders() {
        SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return false;
        }
        Iterator allHeaders = soapHeader.examineAllHeaderElements();
        return allHeaders.hasNext();
    }

    @Override
    public List<Header> asList() {
        SOAPHeader soapHeader = this.ensureSOAPHeader();
        if (soapHeader == null) {
            return Collections.emptyList();
        }
        Iterator allHeaders = soapHeader.examineAllHeaderElements();
        ArrayList<Header> headers = new ArrayList<Header>();
        while (allHeaders.hasNext()) {
            SOAPHeaderElement nextHdr = (SOAPHeaderElement)allHeaders.next();
            headers.add(new SAAJHeader(nextHdr));
        }
        return headers;
    }

    private static class HeaderReadIterator
    implements Iterator<Header> {
        SOAPHeaderElement current;
        Iterator soapHeaders;
        String myNsUri;
        String myLocalName;

        public HeaderReadIterator(Iterator allHeaders, String nsUri, String localName) {
            this.soapHeaders = allHeaders;
            this.myNsUri = nsUri;
            this.myLocalName = localName;
        }

        @Override
        public boolean hasNext() {
            if (this.current == null) {
                this.advance();
            }
            return this.current != null;
        }

        @Override
        public Header next() {
            if (!this.hasNext()) {
                return null;
            }
            if (this.current == null) {
                return null;
            }
            SAAJHeader ret = new SAAJHeader(this.current);
            this.current = null;
            return ret;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException();
        }

        private void advance() {
            while (this.soapHeaders.hasNext()) {
                SOAPHeaderElement nextHdr = (SOAPHeaderElement)this.soapHeaders.next();
                if (nextHdr == null || this.myNsUri != null && !nextHdr.getNamespaceURI().equals(this.myNsUri) || this.myLocalName != null && !nextHdr.getLocalName().equals(this.myLocalName)) continue;
                this.current = nextHdr;
                return;
            }
            this.current = null;
        }
    }
}


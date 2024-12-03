/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  com.sun.istack.Nullable
 */
package com.sun.xml.ws.api.message;

import com.sun.istack.NotNull;
import com.sun.istack.Nullable;
import com.sun.xml.ws.api.SOAPVersion;
import com.sun.xml.ws.api.WSBinding;
import com.sun.xml.ws.api.addressing.AddressingVersion;
import com.sun.xml.ws.api.addressing.WSEndpointReference;
import com.sun.xml.ws.api.message.AddressingUtils;
import com.sun.xml.ws.api.message.Header;
import com.sun.xml.ws.api.message.MessageHeaders;
import com.sun.xml.ws.api.message.Packet;
import com.sun.xml.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.ws.binding.SOAPBindingImpl;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import javax.xml.namespace.QName;

public class HeaderList
extends ArrayList<Header>
implements MessageHeaders {
    private static final long serialVersionUID = -6358045781349627237L;
    private int understoodBits;
    private BitSet moreUnderstoodBits = null;
    private SOAPVersion soapVersion;

    @Deprecated
    public HeaderList() {
    }

    public HeaderList(SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }

    public HeaderList(HeaderList that) {
        super(that);
        this.understoodBits = that.understoodBits;
        if (that.moreUnderstoodBits != null) {
            this.moreUnderstoodBits = (BitSet)that.moreUnderstoodBits.clone();
        }
    }

    public HeaderList(MessageHeaders that) {
        super(that.asList());
        if (that instanceof HeaderList) {
            HeaderList hThat = (HeaderList)that;
            this.understoodBits = hThat.understoodBits;
            if (hThat.moreUnderstoodBits != null) {
                this.moreUnderstoodBits = (BitSet)hThat.moreUnderstoodBits.clone();
            }
        } else {
            Set<QName> understood = that.getUnderstoodHeaders();
            if (understood != null) {
                for (QName qname : understood) {
                    this.understood(qname);
                }
            }
        }
    }

    @Override
    public int size() {
        return super.size();
    }

    @Override
    public boolean hasHeaders() {
        return !this.isEmpty();
    }

    @Deprecated
    public void addAll(Header ... headers) {
        this.addAll(Arrays.asList(headers));
    }

    @Override
    public Header get(int index) {
        return (Header)super.get(index);
    }

    public void understood(int index) {
        if (index >= this.size()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index < 32) {
            this.understoodBits |= 1 << index;
        } else {
            if (this.moreUnderstoodBits == null) {
                this.moreUnderstoodBits = new BitSet();
            }
            this.moreUnderstoodBits.set(index - 32);
        }
    }

    public boolean isUnderstood(int index) {
        if (index >= this.size()) {
            throw new ArrayIndexOutOfBoundsException(index);
        }
        if (index < 32) {
            return this.understoodBits == (this.understoodBits | 1 << index);
        }
        if (this.moreUnderstoodBits == null) {
            return false;
        }
        return this.moreUnderstoodBits.get(index - 32);
    }

    @Override
    public void understood(@NotNull Header header) {
        int sz = this.size();
        for (int i = 0; i < sz; ++i) {
            if (this.get(i) != header) continue;
            this.understood(i);
            return;
        }
        throw new IllegalArgumentException();
    }

    @Override
    @Nullable
    public Header get(@NotNull String nsUri, @NotNull String localName, boolean markAsUnderstood) {
        int len = this.size();
        for (int i = 0; i < len; ++i) {
            Header h = this.get(i);
            if (!h.getLocalPart().equals(localName) || !h.getNamespaceURI().equals(nsUri)) continue;
            if (markAsUnderstood) {
                this.understood(i);
            }
            return h;
        }
        return null;
    }

    public Header get(String nsUri, String localName) {
        return this.get(nsUri, localName, true);
    }

    @Override
    @Nullable
    public Header get(@NotNull QName name, boolean markAsUnderstood) {
        return this.get(name.getNamespaceURI(), name.getLocalPart(), markAsUnderstood);
    }

    @Nullable
    public Header get(@NotNull QName name) {
        return this.get(name, true);
    }

    public Iterator<Header> getHeaders(String nsUri, String localName) {
        return this.getHeaders(nsUri, localName, true);
    }

    @Override
    @NotNull
    public Iterator<Header> getHeaders(final @NotNull String nsUri, final @NotNull String localName, final boolean markAsUnderstood) {
        return new Iterator<Header>(){
            int idx = 0;
            Header next;

            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    this.fetch();
                }
                return this.next != null;
            }

            @Override
            public Header next() {
                if (this.next == null) {
                    this.fetch();
                    if (this.next == null) {
                        throw new NoSuchElementException();
                    }
                }
                if (markAsUnderstood) {
                    assert (HeaderList.this.get(this.idx - 1) == this.next);
                    HeaderList.this.understood(this.idx - 1);
                }
                Header r = this.next;
                this.next = null;
                return r;
            }

            private void fetch() {
                while (this.idx < HeaderList.this.size()) {
                    Header h;
                    if (!(h = HeaderList.this.get(this.idx++)).getLocalPart().equals(localName) || !h.getNamespaceURI().equals(nsUri)) continue;
                    this.next = h;
                    break;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    @Override
    @NotNull
    public Iterator<Header> getHeaders(@NotNull QName headerName, boolean markAsUnderstood) {
        return this.getHeaders(headerName.getNamespaceURI(), headerName.getLocalPart(), markAsUnderstood);
    }

    @NotNull
    public Iterator<Header> getHeaders(@NotNull String nsUri) {
        return this.getHeaders(nsUri, true);
    }

    @Override
    @NotNull
    public Iterator<Header> getHeaders(final @NotNull String nsUri, final boolean markAsUnderstood) {
        return new Iterator<Header>(){
            int idx = 0;
            Header next;

            @Override
            public boolean hasNext() {
                if (this.next == null) {
                    this.fetch();
                }
                return this.next != null;
            }

            @Override
            public Header next() {
                if (this.next == null) {
                    this.fetch();
                    if (this.next == null) {
                        throw new NoSuchElementException();
                    }
                }
                if (markAsUnderstood) {
                    assert (HeaderList.this.get(this.idx - 1) == this.next);
                    HeaderList.this.understood(this.idx - 1);
                }
                Header r = this.next;
                this.next = null;
                return r;
            }

            private void fetch() {
                while (this.idx < HeaderList.this.size()) {
                    Header h;
                    if (!(h = HeaderList.this.get(this.idx++)).getNamespaceURI().equals(nsUri)) continue;
                    this.next = h;
                    break;
                }
            }

            @Override
            public void remove() {
                throw new UnsupportedOperationException();
            }
        };
    }

    public String getTo(AddressingVersion av, SOAPVersion sv) {
        return AddressingUtils.getTo(this, av, sv);
    }

    public String getAction(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        return AddressingUtils.getAction(this, av, sv);
    }

    public WSEndpointReference getReplyTo(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        return AddressingUtils.getReplyTo(this, av, sv);
    }

    public WSEndpointReference getFaultTo(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        return AddressingUtils.getFaultTo(this, av, sv);
    }

    public String getMessageID(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        return AddressingUtils.getMessageID(this, av, sv);
    }

    public String getRelatesTo(@NotNull AddressingVersion av, @NotNull SOAPVersion sv) {
        return AddressingUtils.getRelatesTo(this, av, sv);
    }

    public void fillRequestAddressingHeaders(Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action, boolean mustUnderstand) {
        AddressingUtils.fillRequestAddressingHeaders((MessageHeaders)this, packet, av, sv, oneway, action, mustUnderstand);
    }

    public void fillRequestAddressingHeaders(Packet packet, AddressingVersion av, SOAPVersion sv, boolean oneway, String action) {
        AddressingUtils.fillRequestAddressingHeaders(this, packet, av, sv, oneway, action);
    }

    public void fillRequestAddressingHeaders(WSDLPort wsdlPort, @NotNull WSBinding binding, Packet packet) {
        AddressingUtils.fillRequestAddressingHeaders(this, wsdlPort, binding, packet);
    }

    @Override
    public boolean add(Header header) {
        return super.add(header);
    }

    @Override
    @Nullable
    public Header remove(@NotNull String nsUri, @NotNull String localName) {
        int len = this.size();
        for (int i = 0; i < len; ++i) {
            Header h = this.get(i);
            if (!h.getLocalPart().equals(localName) || !h.getNamespaceURI().equals(nsUri)) continue;
            return this.remove(i);
        }
        return null;
    }

    @Override
    public boolean addOrReplace(Header header) {
        for (int i = 0; i < this.size(); ++i) {
            Header hdr = this.get(i);
            if (!hdr.getNamespaceURI().equals(header.getNamespaceURI()) || !hdr.getLocalPart().equals(header.getLocalPart())) continue;
            this.removeInternal(i);
            this.addInternal(i, header);
            return true;
        }
        return this.add(header);
    }

    @Override
    public void replace(Header old, Header header) {
        for (int i = 0; i < this.size(); ++i) {
            Header hdr = this.get(i);
            if (!hdr.getNamespaceURI().equals(header.getNamespaceURI()) || !hdr.getLocalPart().equals(header.getLocalPart())) continue;
            this.removeInternal(i);
            this.addInternal(i, header);
            return;
        }
        throw new IllegalArgumentException();
    }

    protected void addInternal(int index, Header header) {
        super.add(index, header);
    }

    protected Header removeInternal(int index) {
        return (Header)super.remove(index);
    }

    @Override
    @Nullable
    public Header remove(@NotNull QName name) {
        return this.remove(name.getNamespaceURI(), name.getLocalPart());
    }

    @Override
    public Header remove(int index) {
        this.removeUnderstoodBit(index);
        return (Header)super.remove(index);
    }

    private void removeUnderstoodBit(int index) {
        assert (index < this.size());
        if (index < 32) {
            int shiftedUpperBits = this.understoodBits >>> -31 + index << index;
            int lowerBits = this.understoodBits << -index >>> 31 - index >>> 1;
            this.understoodBits = shiftedUpperBits | lowerBits;
            if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
                if (this.moreUnderstoodBits.get(0)) {
                    this.understoodBits |= Integer.MIN_VALUE;
                }
                this.moreUnderstoodBits.clear(0);
                int i = this.moreUnderstoodBits.nextSetBit(1);
                while (i > 0) {
                    this.moreUnderstoodBits.set(i - 1);
                    this.moreUnderstoodBits.clear(i);
                    i = this.moreUnderstoodBits.nextSetBit(i + 1);
                }
            }
        } else if (this.moreUnderstoodBits != null && this.moreUnderstoodBits.cardinality() > 0) {
            this.moreUnderstoodBits.clear(index -= 32);
            int i = this.moreUnderstoodBits.nextSetBit(index);
            while (i >= 1) {
                this.moreUnderstoodBits.set(i - 1);
                this.moreUnderstoodBits.clear(i);
                i = this.moreUnderstoodBits.nextSetBit(i + 1);
            }
        }
        if (this.size() - 1 <= 33 && this.moreUnderstoodBits != null) {
            this.moreUnderstoodBits = null;
        }
    }

    @Override
    public boolean remove(Object o) {
        if (o != null) {
            for (int index = 0; index < this.size(); ++index) {
                if (!o.equals(this.get(index))) continue;
                this.remove(index);
                return true;
            }
        }
        return false;
    }

    public Header remove(Header h) {
        if (this.remove((Object)h)) {
            return h;
        }
        return null;
    }

    public static HeaderList copy(MessageHeaders original) {
        if (original == null) {
            return null;
        }
        return new HeaderList(original);
    }

    public static HeaderList copy(HeaderList original) {
        return HeaderList.copy((MessageHeaders)original);
    }

    public void readResponseAddressingHeaders(WSDLPort wsdlPort, WSBinding binding) {
    }

    @Override
    public void understood(QName name) {
        this.get(name, true);
    }

    @Override
    public void understood(String nsUri, String localName) {
        this.get(nsUri, localName, true);
    }

    @Override
    public Set<QName> getUnderstoodHeaders() {
        HashSet<QName> understoodHdrs = new HashSet<QName>();
        for (int i = 0; i < this.size(); ++i) {
            if (!this.isUnderstood(i)) continue;
            Header header = this.get(i);
            understoodHdrs.add(new QName(header.getNamespaceURI(), header.getLocalPart()));
        }
        return understoodHdrs;
    }

    @Override
    public boolean isUnderstood(Header header) {
        return this.isUnderstood(header.getNamespaceURI(), header.getLocalPart());
    }

    @Override
    public boolean isUnderstood(String nsUri, String localName) {
        for (int i = 0; i < this.size(); ++i) {
            Header h = this.get(i);
            if (!h.getLocalPart().equals(localName) || !h.getNamespaceURI().equals(nsUri)) continue;
            return this.isUnderstood(i);
        }
        return false;
    }

    @Override
    public boolean isUnderstood(QName name) {
        return this.isUnderstood(name.getNamespaceURI(), name.getLocalPart());
    }

    @Override
    public Set<QName> getNotUnderstoodHeaders(Set<String> roles, Set<QName> knownHeaders, WSBinding binding) {
        HashSet<QName> notUnderstoodHeaders = null;
        if (roles == null) {
            roles = new HashSet<String>();
        }
        SOAPVersion effectiveSoapVersion = this.getEffectiveSOAPVersion(binding);
        roles.add(effectiveSoapVersion.implicitRole);
        for (int i = 0; i < this.size(); ++i) {
            Header header;
            if (this.isUnderstood(i) || (header = this.get(i)).isIgnorable(effectiveSoapVersion, roles)) continue;
            QName qName = new QName(header.getNamespaceURI(), header.getLocalPart());
            if (binding == null) {
                if (notUnderstoodHeaders == null) {
                    notUnderstoodHeaders = new HashSet<QName>();
                }
                notUnderstoodHeaders.add(qName);
                continue;
            }
            if (!(binding instanceof SOAPBindingImpl) || ((SOAPBindingImpl)binding).understandsHeader(qName) || knownHeaders.contains(qName)) continue;
            if (notUnderstoodHeaders == null) {
                notUnderstoodHeaders = new HashSet();
            }
            notUnderstoodHeaders.add(qName);
        }
        return notUnderstoodHeaders;
    }

    private SOAPVersion getEffectiveSOAPVersion(WSBinding binding) {
        SOAPVersion mySOAPVersion;
        SOAPVersion sOAPVersion = mySOAPVersion = this.soapVersion != null ? this.soapVersion : binding.getSOAPVersion();
        if (mySOAPVersion == null) {
            mySOAPVersion = SOAPVersion.SOAP_11;
        }
        return mySOAPVersion;
    }

    public void setSoapVersion(SOAPVersion soapVersion) {
        this.soapVersion = soapVersion;
    }

    @Override
    public Iterator<Header> getHeaders() {
        return this.iterator();
    }

    @Override
    public List<Header> asList() {
        return this;
    }
}


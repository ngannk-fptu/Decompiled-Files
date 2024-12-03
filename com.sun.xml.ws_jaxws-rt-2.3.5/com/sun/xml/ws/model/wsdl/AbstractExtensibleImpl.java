/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.NotNull
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.model.wsdl;

import com.sun.istack.NotNull;
import com.sun.xml.ws.api.model.wsdl.WSDLExtensible;
import com.sun.xml.ws.api.model.wsdl.WSDLExtension;
import com.sun.xml.ws.api.model.wsdl.WSDLObject;
import com.sun.xml.ws.model.wsdl.AbstractObjectImpl;
import com.sun.xml.ws.resources.UtilMessages;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLStreamReader;
import javax.xml.ws.WebServiceException;
import org.xml.sax.Locator;

abstract class AbstractExtensibleImpl
extends AbstractObjectImpl
implements WSDLExtensible {
    protected final Set<WSDLExtension> extensions = new HashSet<WSDLExtension>();
    protected List<UnknownWSDLExtension> notUnderstoodExtensions = new ArrayList<UnknownWSDLExtension>();

    protected AbstractExtensibleImpl(XMLStreamReader xsr) {
        super(xsr);
    }

    protected AbstractExtensibleImpl(String systemId, int lineNumber) {
        super(systemId, lineNumber);
    }

    @Override
    public final Iterable<WSDLExtension> getExtensions() {
        return this.extensions;
    }

    @Override
    public final <T extends WSDLExtension> Iterable<T> getExtensions(Class<T> type) {
        ArrayList<WSDLExtension> r = new ArrayList<WSDLExtension>(this.extensions.size());
        for (WSDLExtension e : this.extensions) {
            if (!type.isInstance(e)) continue;
            r.add((WSDLExtension)type.cast(e));
        }
        return r;
    }

    @Override
    public <T extends WSDLExtension> T getExtension(Class<T> type) {
        for (WSDLExtension e : this.extensions) {
            if (!type.isInstance(e)) continue;
            return (T)((WSDLExtension)type.cast(e));
        }
        return null;
    }

    @Override
    public void addExtension(WSDLExtension ex) {
        if (ex == null) {
            throw new IllegalArgumentException();
        }
        this.extensions.add(ex);
    }

    public List<? extends UnknownWSDLExtension> getNotUnderstoodExtensions() {
        return this.notUnderstoodExtensions;
    }

    @Override
    public void addNotUnderstoodExtension(QName extnEl, Locator locator) {
        this.notUnderstoodExtensions.add(new UnknownWSDLExtension(extnEl, locator));
    }

    @Override
    public boolean areRequiredExtensionsUnderstood() {
        if (this.notUnderstoodExtensions.size() != 0) {
            StringBuilder buf = new StringBuilder("Unknown WSDL extensibility elements:");
            for (UnknownWSDLExtension extn : this.notUnderstoodExtensions) {
                buf.append('\n').append(extn.toString());
            }
            throw new WebServiceException(buf.toString());
        }
        return true;
    }

    protected static class UnknownWSDLExtension
    implements WSDLExtension,
    WSDLObject {
        private final QName extnEl;
        private final Locator locator;

        public UnknownWSDLExtension(QName extnEl, Locator locator) {
            this.extnEl = extnEl;
            this.locator = locator;
        }

        @Override
        public QName getName() {
            return this.extnEl;
        }

        @Override
        @NotNull
        public Locator getLocation() {
            return this.locator;
        }

        public String toString() {
            return this.extnEl + " " + UtilMessages.UTIL_LOCATION(this.locator.getLineNumber(), this.locator.getSystemId());
        }
    }
}


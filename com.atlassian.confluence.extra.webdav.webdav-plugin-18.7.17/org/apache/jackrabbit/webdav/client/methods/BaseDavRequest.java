/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.http.HttpEntity
 *  org.apache.http.HttpResponse
 *  org.apache.http.StatusLine
 *  org.apache.http.client.methods.HttpEntityEnclosingRequestBase
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.client.methods;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.MultiStatus;
import org.apache.jackrabbit.webdav.lock.LockDiscovery;
import org.apache.jackrabbit.webdav.observation.EventDiscovery;
import org.apache.jackrabbit.webdav.observation.ObservationConstants;
import org.apache.jackrabbit.webdav.observation.SubscriptionDiscovery;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public abstract class BaseDavRequest
extends HttpEntityEnclosingRequestBase {
    private static Logger log = LoggerFactory.getLogger(BaseDavRequest.class);

    public BaseDavRequest(URI uri) {
        super.setURI(uri);
    }

    public Document getResponseBodyAsDocument(HttpEntity entity) throws IOException {
        if (entity == null) {
            return null;
        }
        try (InputStream in = entity.getContent();){
            Document document = DomUtil.parseDocument(in);
            return document;
        }
    }

    public MultiStatus getResponseBodyAsMultiStatus(HttpResponse response) throws DavException {
        try {
            Document doc = this.getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            return MultiStatus.createFromXml(doc.getDocumentElement());
        }
        catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), (Throwable)ex);
        }
    }

    public LockDiscovery getResponseBodyAsLockDiscovery(HttpResponse response) throws DavException {
        try {
            Document doc = this.getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            Element root = doc.getDocumentElement();
            if (!DomUtil.matches(root, "prop", DavConstants.NAMESPACE) && DomUtil.hasChildElement(root, "lockdiscovery", DavConstants.NAMESPACE)) {
                throw new DavException(response.getStatusLine().getStatusCode(), "Missing DAV:prop response body in LOCK response.");
            }
            Element lde = DomUtil.getChildElement(root, "lockdiscovery", DavConstants.NAMESPACE);
            if (!DomUtil.hasChildElement(lde, "activelock", DavConstants.NAMESPACE)) {
                throw new DavException(response.getStatusLine().getStatusCode(), "The DAV:lockdiscovery must contain a least a single DAV:activelock in response to a successful LOCK request.");
            }
            return LockDiscovery.createFromXml(lde);
        }
        catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), (Throwable)ex);
        }
    }

    public SubscriptionDiscovery getResponseBodyAsSubscriptionDiscovery(HttpResponse response) throws DavException {
        try {
            Document doc = this.getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            Element root = doc.getDocumentElement();
            if (!DomUtil.matches(root, "prop", DavConstants.NAMESPACE) && DomUtil.hasChildElement(root, ObservationConstants.SUBSCRIPTIONDISCOVERY.getName(), ObservationConstants.SUBSCRIPTIONDISCOVERY.getNamespace())) {
                throw new DavException(response.getStatusLine().getStatusCode(), "Missing DAV:prop response body in SUBSCRIBE response.");
            }
            Element sde = DomUtil.getChildElement(root, ObservationConstants.SUBSCRIPTIONDISCOVERY.getName(), ObservationConstants.SUBSCRIPTIONDISCOVERY.getNamespace());
            SubscriptionDiscovery sd = SubscriptionDiscovery.createFromXml(sde);
            if (sd.getValue().length > 0) {
                return sd;
            }
            throw new DavException(response.getStatusLine().getStatusCode(), "Missing 'subscription' elements in SUBSCRIBE response body. At least a single subscription must be present if SUBSCRIBE was successful.");
        }
        catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), (Throwable)ex);
        }
    }

    public EventDiscovery getResponseBodyAsEventDiscovery(HttpResponse response) throws DavException {
        try {
            Document doc = this.getResponseBodyAsDocument(response.getEntity());
            if (doc == null) {
                throw new DavException(response.getStatusLine().getStatusCode(), "no response body");
            }
            return EventDiscovery.createFromXml(doc.getDocumentElement());
        }
        catch (IOException ex) {
            throw new DavException(response.getStatusLine().getStatusCode(), (Throwable)ex);
        }
    }

    public void checkSuccess(HttpResponse response) throws DavException {
        if (!this.succeeded(response)) {
            throw this.getResponseException(response);
        }
    }

    public DavException getResponseException(HttpResponse response) {
        if (this.succeeded(response)) {
            String msg = "Cannot retrieve exception from successful response.";
            log.warn(msg);
            throw new IllegalStateException(msg);
        }
        StatusLine st = response.getStatusLine();
        Element responseRoot = null;
        try {
            responseRoot = this.getResponseBodyAsDocument(response.getEntity()).getDocumentElement();
        }
        catch (IOException iOException) {
            // empty catch block
        }
        return new DavException(st.getStatusCode(), st.getReasonPhrase(), null, responseRoot);
    }

    public boolean succeeded(HttpResponse response) {
        int status = response.getStatusLine().getStatusCode();
        return status >= 200 && status <= 299;
    }
}


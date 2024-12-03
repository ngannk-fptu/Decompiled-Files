/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.util.LinkedHashMap;
import java.util.Map;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MultiStatus
implements DavConstants,
XmlSerializable {
    private Map<String, MultiStatusResponse> responses = new LinkedHashMap<String, MultiStatusResponse>();
    private String responseDescription;

    public void addResourceProperties(DavResource resource, DavPropertyNameSet propNameSet, int propFindType, int depth) {
        this.addResponse(new MultiStatusResponse(resource, propNameSet, propFindType));
        if (depth > 0 && resource.isCollection()) {
            DavResourceIterator iter = resource.getMembers();
            while (iter.hasNext()) {
                this.addResourceProperties(iter.nextResource(), propNameSet, propFindType, depth - 1);
            }
        }
    }

    public void addResourceProperties(DavResource resource, DavPropertyNameSet propNameSet, int depth) {
        this.addResourceProperties(resource, propNameSet, 0, depth);
    }

    public void addResourceStatus(DavResource resource, int status, int depth) {
        this.addResponse(new MultiStatusResponse(resource.getHref(), status));
        if (depth > 0 && resource.isCollection()) {
            DavResourceIterator iter = resource.getMembers();
            while (iter.hasNext()) {
                this.addResourceStatus(iter.nextResource(), status, depth - 1);
            }
        }
    }

    public synchronized void addResponse(MultiStatusResponse response) {
        this.responses.put(response.getHref(), response);
    }

    public synchronized MultiStatusResponse[] getResponses() {
        return this.responses.values().toArray(new MultiStatusResponse[this.responses.size()]);
    }

    public void setResponseDescription(String responseDescription) {
        this.responseDescription = responseDescription;
    }

    public String getResponseDescription() {
        return this.responseDescription;
    }

    @Override
    public Element toXml(Document document) {
        Element multistatus = DomUtil.createElement(document, "multistatus", NAMESPACE);
        for (MultiStatusResponse resp : this.getResponses()) {
            multistatus.appendChild(resp.toXml(document));
        }
        if (this.responseDescription != null) {
            Element respDesc = DomUtil.createElement(document, "responsedescription", NAMESPACE, this.responseDescription);
            multistatus.appendChild(respDesc);
        }
        return multistatus;
    }

    public static MultiStatus createFromXml(Element multistatusElement) {
        if (!DomUtil.matches(multistatusElement, "multistatus", NAMESPACE)) {
            throw new IllegalArgumentException("DAV:multistatus element expected.");
        }
        MultiStatus multistatus = new MultiStatus();
        ElementIterator it = DomUtil.getChildren(multistatusElement, "response", NAMESPACE);
        while (it.hasNext()) {
            Element respElem = it.nextElement();
            MultiStatusResponse response = MultiStatusResponse.createFromXml(respElem);
            multistatus.addResponse(response);
        }
        multistatus.setResponseDescription(DomUtil.getChildText(multistatusElement, "responsedescription", NAMESPACE));
        return multistatus;
    }
}


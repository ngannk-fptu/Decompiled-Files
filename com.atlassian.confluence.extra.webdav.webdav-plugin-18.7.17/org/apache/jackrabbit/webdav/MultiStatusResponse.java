/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.Status;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.PropContainer;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class MultiStatusResponse
implements XmlSerializable,
DavConstants {
    private static final int TYPE_PROPSTAT = 0;
    private static final int TYPE_HREFSTATUS = 1;
    private final int type;
    private final String href;
    private final String responseDescription;
    private Status status;
    private HashMap<Integer, PropContainer> statusMap;

    private MultiStatusResponse(String href, String responseDescription, int type) {
        this.statusMap = new HashMap();
        if (!MultiStatusResponse.isValidHref(href)) {
            throw new IllegalArgumentException("Invalid href ('" + href + "')");
        }
        this.href = href;
        this.responseDescription = responseDescription;
        this.type = type;
    }

    public MultiStatusResponse(String href, Status status, String responseDescription) {
        this(href, responseDescription, 1);
        if (status == null) {
            throw new IllegalArgumentException("Status must not be null in case of a multistatus reponse that consists of href + status only.");
        }
        this.status = status;
    }

    public MultiStatusResponse(String href, int statusCode) {
        this(href, statusCode, null);
    }

    public MultiStatusResponse(String href, int statusCode, String responseDescription) {
        this(href, new Status(statusCode), responseDescription);
    }

    public MultiStatusResponse(String href, String responseDescription) {
        this(href, responseDescription, 0);
    }

    public MultiStatusResponse(DavResource resource, DavPropertyNameSet propNameSet) {
        this(resource, propNameSet, 0);
    }

    public MultiStatusResponse(DavResource resource, DavPropertyNameSet propNameSet, int propFindType) {
        block9: {
            block8: {
                this(resource.getHref(), null, 0);
                if (propFindType != 2) break block8;
                PropContainer status200 = this.getPropContainer(200, true);
                for (DavPropertyName propName : resource.getPropertyNames()) {
                    status200.addContent(propName);
                }
                break block9;
            }
            PropContainer status200 = this.getPropContainer(200, false);
            HashSet<DavPropertyName> missing = new HashSet<DavPropertyName>(propNameSet.getContent());
            if (propFindType == 0) {
                for (DavPropertyName propName : propNameSet) {
                    DavProperty<?> prop = resource.getProperty(propName);
                    if (prop == null) continue;
                    status200.addContent(prop);
                    missing.remove(propName);
                }
            } else {
                for (DavProperty property : resource.getProperties()) {
                    boolean allDeadPlusRfc4918LiveProperties = propFindType == 1 || propFindType == 3;
                    boolean wasRequested = missing.remove(property.getName());
                    if ((!allDeadPlusRfc4918LiveProperties || property.isInvisibleInAllprop()) && !wasRequested) continue;
                    status200.addContent(property);
                }
                if (propFindType == 3 && !missing.isEmpty()) {
                    for (DavPropertyName propName : new HashSet<DavPropertyName>(missing)) {
                        DavProperty<?> prop = resource.getProperty(propName);
                        if (prop == null) continue;
                        status200.addContent(prop);
                        missing.remove(propName);
                    }
                }
            }
            if (missing.isEmpty() || propFindType == 1) break block9;
            PropContainer status404 = this.getPropContainer(404, true);
            for (DavPropertyName propName : missing) {
                status404.addContent(propName);
            }
        }
    }

    public String getHref() {
        return this.href;
    }

    public String getResponseDescription() {
        return this.responseDescription;
    }

    public Status[] getStatus() {
        Status[] sts;
        if (this.type == 0) {
            sts = new Status[this.statusMap.size()];
            Iterator<Integer> iter = this.statusMap.keySet().iterator();
            int i = 0;
            while (iter.hasNext()) {
                Integer statusKey = iter.next();
                sts[i] = new Status(statusKey);
                ++i;
            }
        } else {
            sts = new Status[]{this.status};
        }
        return sts;
    }

    public boolean isPropStat() {
        return this.type == 0;
    }

    @Override
    public Element toXml(Document document) {
        Element response = DomUtil.createElement(document, "response", NAMESPACE);
        response.appendChild(DomUtil.hrefToXml(this.getHref(), document));
        if (this.type == 0) {
            for (Integer statusKey : this.statusMap.keySet()) {
                Status st = new Status(statusKey);
                PropContainer propCont = this.statusMap.get(statusKey);
                if (propCont.isEmpty()) continue;
                Element propstat = DomUtil.createElement(document, "propstat", NAMESPACE);
                propstat.appendChild(propCont.toXml(document));
                propstat.appendChild(st.toXml(document));
                response.appendChild(propstat);
            }
        } else {
            response.appendChild(this.status.toXml(document));
        }
        String description = this.getResponseDescription();
        if (description != null) {
            Element desc = DomUtil.createElement(document, "responsedescription", NAMESPACE);
            DomUtil.setText(desc, description);
            response.appendChild(desc);
        }
        return response;
    }

    public void add(DavProperty<?> property) {
        this.checkType(0);
        PropContainer status200 = this.getPropContainer(200, false);
        status200.addContent(property);
    }

    public void add(DavPropertyName propertyName) {
        this.checkType(0);
        PropContainer status200 = this.getPropContainer(200, true);
        status200.addContent(propertyName);
    }

    public void add(DavProperty<?> property, int status) {
        this.checkType(0);
        PropContainer propCont = this.getPropContainer(status, false);
        propCont.addContent(property);
    }

    public void add(DavPropertyName propertyName, int status) {
        this.checkType(0);
        PropContainer propCont = this.getPropContainer(status, true);
        propCont.addContent(propertyName);
    }

    private PropContainer getPropContainer(int status, boolean forNames) {
        PropContainer propContainer = this.statusMap.get(status);
        if (propContainer == null) {
            propContainer = forNames ? new DavPropertyNameSet() : new DavPropertySet();
            this.statusMap.put(status, propContainer);
        }
        return propContainer;
    }

    private void checkType(int type) {
        if (this.type != type) {
            throw new IllegalStateException("The given MultiStatusResponse is not of the required type.");
        }
    }

    public DavPropertySet getProperties(int status) {
        PropContainer mapEntry;
        if (this.statusMap.containsKey(status) && (mapEntry = this.statusMap.get(status)) != null && mapEntry instanceof DavPropertySet) {
            return (DavPropertySet)mapEntry;
        }
        return new DavPropertySet();
    }

    public DavPropertyNameSet getPropertyNames(int status) {
        PropContainer mapEntry;
        if (this.statusMap.containsKey(status) && (mapEntry = this.statusMap.get(status)) != null) {
            if (mapEntry instanceof DavPropertySet) {
                DavPropertyNameSet set = new DavPropertyNameSet();
                for (DavPropertyName name : ((DavPropertySet)mapEntry).getPropertyNames()) {
                    set.add(name);
                }
                return set;
            }
            return (DavPropertyNameSet)mapEntry;
        }
        return new DavPropertyNameSet();
    }

    public static MultiStatusResponse createFromXml(Element responseElement) {
        MultiStatusResponse response;
        if (!DomUtil.matches(responseElement, "response", NAMESPACE)) {
            throw new IllegalArgumentException("DAV:response element required.");
        }
        String href = DomUtil.getChildTextTrim(responseElement, "href", NAMESPACE);
        if (href == null) {
            throw new IllegalArgumentException("DAV:response element must contain a DAV:href element expected.");
        }
        String statusLine = DomUtil.getChildText(responseElement, "status", NAMESPACE);
        String responseDescription = DomUtil.getChildText(responseElement, "responsedescription", NAMESPACE);
        if (statusLine != null) {
            Status status = Status.parse(statusLine);
            response = new MultiStatusResponse(href, status, responseDescription);
        } else {
            response = new MultiStatusResponse(href, responseDescription, 0);
            ElementIterator it = DomUtil.getChildren(responseElement, "propstat", NAMESPACE);
            while (it.hasNext()) {
                Element propstat = it.nextElement();
                String propstatus = DomUtil.getChildText(propstat, "status", NAMESPACE);
                Element prop = DomUtil.getChildElement(propstat, "prop", NAMESPACE);
                if (propstatus == null || prop == null) continue;
                int statusCode = Status.parse(propstatus).getStatusCode();
                ElementIterator propIt = DomUtil.getChildren(prop);
                while (propIt.hasNext()) {
                    Element el = propIt.nextElement();
                    DefaultDavProperty<?> property = DefaultDavProperty.createFromXml(el);
                    response.add(property, statusCode);
                }
            }
        }
        return response;
    }

    private static boolean isValidHref(String href) {
        return href != null && !"".equals(href);
    }
}


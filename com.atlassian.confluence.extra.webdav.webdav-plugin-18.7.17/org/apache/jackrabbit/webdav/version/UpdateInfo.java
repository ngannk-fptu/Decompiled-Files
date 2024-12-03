/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.version;

import java.util.ArrayList;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.version.DeltaVConstants;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.ElementIterator;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class UpdateInfo
implements DeltaVConstants,
XmlSerializable {
    private static Logger log = LoggerFactory.getLogger(UpdateInfo.class);
    public static final int UPDATE_BY_VERSION = 0;
    public static final int UPDATE_BY_LABEL = 1;
    public static final int UPDATE_BY_WORKSPACE = 2;
    private Element updateElement;
    private DavPropertyNameSet propertyNameSet = new DavPropertyNameSet();
    private String[] source;
    private int type;

    public UpdateInfo(String[] updateSource, int updateType, DavPropertyNameSet propertyNameSet) {
        String[] stringArray;
        if (updateSource == null || updateSource.length == 0) {
            throw new IllegalArgumentException("Version href array must not be null and have a minimal length of 1.");
        }
        if (updateType < 0 || updateType > 2) {
            throw new IllegalArgumentException("Illegal type of UpdateInfo.");
        }
        this.type = updateType;
        if (updateType == 0) {
            stringArray = updateSource;
        } else {
            String[] stringArray2 = new String[1];
            stringArray = stringArray2;
            stringArray2[0] = updateSource[0];
        }
        this.source = stringArray;
        if (propertyNameSet != null) {
            this.propertyNameSet = propertyNameSet;
        }
    }

    public UpdateInfo(Element updateElement) throws DavException {
        if (!DomUtil.matches(updateElement, "update", NAMESPACE)) {
            log.warn("DAV:update element expected");
            throw new DavException(400);
        }
        boolean done = false;
        if (DomUtil.hasChildElement(updateElement, "version", NAMESPACE)) {
            Element vEl = DomUtil.getChildElement(updateElement, "version", NAMESPACE);
            ElementIterator hrefs = DomUtil.getChildren(vEl, "href", DavConstants.NAMESPACE);
            ArrayList<String> hrefList = new ArrayList<String>();
            while (hrefs.hasNext()) {
                hrefList.add(DomUtil.getText(hrefs.nextElement()));
            }
            this.source = hrefList.toArray(new String[hrefList.size()]);
            this.type = 0;
            done = true;
        }
        if (!done && DomUtil.hasChildElement(updateElement, "label-name", NAMESPACE)) {
            this.source = new String[]{DomUtil.getChildText(updateElement, "label-name", NAMESPACE)};
            this.type = 1;
            done = true;
        }
        if (!done) {
            Element wspElem = DomUtil.getChildElement(updateElement, "workspace", NAMESPACE);
            if (wspElem != null) {
                this.source = new String[]{DomUtil.getChildTextTrim(wspElem, "href", DavConstants.NAMESPACE)};
                this.type = 2;
            } else {
                log.warn("DAV:update element must contain either DAV:version, DAV:label-name or DAV:workspace child element.");
                throw new DavException(400);
            }
        }
        if (DomUtil.hasChildElement(updateElement, "prop", DavConstants.NAMESPACE)) {
            Element propEl = DomUtil.getChildElement(updateElement, "prop", DavConstants.NAMESPACE);
            this.propertyNameSet = new DavPropertyNameSet(propEl);
            updateElement.removeChild(propEl);
        } else {
            this.propertyNameSet = new DavPropertyNameSet();
        }
        this.updateElement = updateElement;
    }

    public String[] getVersionHref() {
        return this.type == 0 ? this.source : null;
    }

    public String[] getLabelName() {
        return this.type == 1 ? this.source : null;
    }

    public String getWorkspaceHref() {
        return this.type == 2 ? this.source[0] : null;
    }

    public DavPropertyNameSet getPropertyNameSet() {
        return this.propertyNameSet;
    }

    public Element getUpdateElement() {
        return this.updateElement;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.updateElement != null ? (Element)document.importNode(this.updateElement, true) : UpdateInfo.createUpdateElement(this.source, this.type, document);
        if (!this.propertyNameSet.isEmpty()) {
            elem.appendChild(this.propertyNameSet.toXml(document));
        }
        return elem;
    }

    public static Element createUpdateElement(String[] updateSource, int updateType, Document factory) {
        if (updateSource == null || updateSource.length == 0) {
            throw new IllegalArgumentException("Update source must specific at least a single resource used to run the update.");
        }
        Element elem = DomUtil.createElement(factory, "update", NAMESPACE);
        switch (updateType) {
            case 0: {
                Element vE = DomUtil.addChildElement(elem, "version", NAMESPACE);
                for (String source : updateSource) {
                    vE.appendChild(DomUtil.hrefToXml(source, factory));
                }
                break;
            }
            case 1: {
                DomUtil.addChildElement(elem, "label-name", NAMESPACE, updateSource[0]);
                break;
            }
            case 2: {
                Element wspEl = DomUtil.addChildElement(elem, "workspace", NAMESPACE, updateSource[0]);
                wspEl.appendChild(DomUtil.hrefToXml(updateSource[0], factory));
                break;
            }
            default: {
                throw new IllegalArgumentException("Invalid update type: " + updateType);
            }
        }
        return elem;
    }
}


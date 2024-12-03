/*
 * Decompiled with CFR 0.152.
 */
package org.apache.jackrabbit.webdav.property;

import java.util.List;
import org.apache.jackrabbit.webdav.DavConstants;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DavPropertySet;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.apache.jackrabbit.webdav.xml.XmlSerializable;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class ProppatchInfo
implements XmlSerializable {
    private final List<? extends PropEntry> changeList;
    private final DavPropertySet setProperties;
    private final DavPropertyNameSet removeProperties;
    private final DavPropertyNameSet propertyNames = new DavPropertyNameSet();

    public ProppatchInfo(List<? extends PropEntry> changeList) {
        if (changeList == null || changeList.isEmpty()) {
            throw new IllegalArgumentException("PROPPATCH cannot be executed without properties to be set or removed.");
        }
        this.changeList = changeList;
        this.setProperties = null;
        this.removeProperties = null;
        for (PropEntry propEntry : changeList) {
            if (propEntry instanceof DavPropertyName) {
                this.propertyNames.add((DavPropertyName)propEntry);
                continue;
            }
            if (propEntry instanceof DavProperty) {
                DavProperty setProperty = (DavProperty)propEntry;
                this.propertyNames.add(setProperty.getName());
                continue;
            }
            throw new IllegalArgumentException("ChangeList may only contain DavPropertyName and DavProperty elements.");
        }
    }

    public ProppatchInfo(DavPropertySet setProperties, DavPropertyNameSet removeProperties) {
        if (setProperties == null || removeProperties == null) {
            throw new IllegalArgumentException("Neither setProperties nor removeProperties must be null.");
        }
        if (setProperties.isEmpty() && removeProperties.isEmpty()) {
            throw new IllegalArgumentException("Either setProperties or removeProperties can be empty; not both of them.");
        }
        this.changeList = null;
        this.setProperties = setProperties;
        this.removeProperties = removeProperties;
        this.propertyNames.addAll(removeProperties);
        for (DavPropertyName setName : setProperties.getPropertyNames()) {
            this.propertyNames.add(setName);
        }
    }

    public DavPropertyNameSet getAffectedProperties() {
        if (this.propertyNames.isEmpty()) {
            throw new IllegalStateException("must be called after toXml()");
        }
        return this.propertyNames;
    }

    @Override
    public Element toXml(Document document) {
        Element proppatch = DomUtil.createElement(document, "propertyupdate", DavConstants.NAMESPACE);
        if (this.changeList != null) {
            Node propElement = null;
            boolean isSet = false;
            for (PropEntry propEntry : this.changeList) {
                if (propEntry instanceof DavPropertyName) {
                    DavPropertyName removeName = (DavPropertyName)propEntry;
                    if (propElement == null || isSet) {
                        isSet = false;
                        propElement = this.getPropElement(proppatch, false);
                    }
                    propElement.appendChild(removeName.toXml(document));
                    continue;
                }
                if (propEntry instanceof DavProperty) {
                    DavProperty setProperty = (DavProperty)propEntry;
                    if (propElement == null || !isSet) {
                        isSet = true;
                        propElement = this.getPropElement(proppatch, true);
                    }
                    propElement.appendChild(setProperty.toXml(document));
                    continue;
                }
                throw new IllegalArgumentException("ChangeList may only contain DavPropertyName and DavProperty elements.");
            }
        } else {
            if (!this.setProperties.isEmpty()) {
                Element set = DomUtil.addChildElement(proppatch, "set", DavConstants.NAMESPACE);
                set.appendChild(this.setProperties.toXml(document));
            }
            if (!this.removeProperties.isEmpty()) {
                Element remove = DomUtil.addChildElement(proppatch, "remove", DavConstants.NAMESPACE);
                remove.appendChild(this.removeProperties.toXml(document));
            }
        }
        return proppatch;
    }

    private Element getPropElement(Element propUpdate, boolean isSet) {
        Element updateEntry = DomUtil.addChildElement(propUpdate, isSet ? "set" : "remove", DavConstants.NAMESPACE);
        return DomUtil.addChildElement(updateEntry, "prop", DavConstants.NAMESPACE);
    }
}


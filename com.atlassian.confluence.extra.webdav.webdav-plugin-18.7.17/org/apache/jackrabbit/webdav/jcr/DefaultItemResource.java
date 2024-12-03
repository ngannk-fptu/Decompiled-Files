/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import javax.jcr.Binary;
import javax.jcr.Item;
import javax.jcr.Property;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.ValueFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.sax.SAXResult;
import org.apache.jackrabbit.commons.webdav.JcrValueType;
import org.apache.jackrabbit.commons.xml.SerializingContentHandler;
import org.apache.jackrabbit.server.io.IOUtil;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.DavResource;
import org.apache.jackrabbit.webdav.DavResourceFactory;
import org.apache.jackrabbit.webdav.DavResourceIterator;
import org.apache.jackrabbit.webdav.DavResourceIteratorImpl;
import org.apache.jackrabbit.webdav.DavResourceLocator;
import org.apache.jackrabbit.webdav.MultiStatusResponse;
import org.apache.jackrabbit.webdav.io.InputContext;
import org.apache.jackrabbit.webdav.io.OutputContext;
import org.apache.jackrabbit.webdav.jcr.AbstractItemResource;
import org.apache.jackrabbit.webdav.jcr.JcrDavException;
import org.apache.jackrabbit.webdav.jcr.JcrDavSession;
import org.apache.jackrabbit.webdav.jcr.property.JcrDavPropertyNameSet;
import org.apache.jackrabbit.webdav.jcr.property.LengthsProperty;
import org.apache.jackrabbit.webdav.jcr.property.ValuesProperty;
import org.apache.jackrabbit.webdav.lock.ActiveLock;
import org.apache.jackrabbit.webdav.lock.Scope;
import org.apache.jackrabbit.webdav.lock.Type;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.property.DavPropertyNameSet;
import org.apache.jackrabbit.webdav.property.DefaultDavProperty;
import org.apache.jackrabbit.webdav.property.PropEntry;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DefaultItemResource
extends AbstractItemResource {
    private static Logger log = LoggerFactory.getLogger(DefaultItemResource.class);

    public DefaultItemResource(DavResourceLocator locator, JcrDavSession session, DavResourceFactory factory, Item item) {
        super(locator, session, factory, item);
    }

    @Override
    public boolean isCollection() {
        return false;
    }

    @Override
    public long getModificationTime() {
        return new Date().getTime();
    }

    @Override
    public void spool(OutputContext outputContext) throws IOException {
        super.spool(outputContext);
        OutputStream out = outputContext.getOutputStream();
        if (out != null && this.exists()) {
            if (this.isMultiple()) {
                this.spoolMultiValued(out);
            } else {
                this.spoolSingleValued(out);
            }
        }
    }

    private void spoolMultiValued(OutputStream out) {
        try {
            Document doc = DomUtil.createDocument();
            doc.appendChild(this.getProperty(JCR_VALUES).toXml(doc));
            DefaultHandler handler = SerializingContentHandler.getSerializer(out);
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new SAXResult(handler));
        }
        catch (SAXException e) {
            log.error("Failed to set up XML serializer for " + this.item, (Throwable)e);
        }
        catch (TransformerConfigurationException e) {
            log.error("Failed to set up XML transformer for " + this.item, (Throwable)e);
        }
        catch (ParserConfigurationException e) {
            log.error("Failed to set up XML document for " + this.item, (Throwable)e);
        }
        catch (TransformerException e) {
            log.error("Failed to serialize the values of " + this.item, (Throwable)e);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void spoolSingleValued(OutputStream out) throws IOException {
        try {
            Binary binary = ((Property)this.item).getBinary();
            try (InputStream in = binary.getStream();){
                IOUtil.spool(in, out);
            }
            finally {
                binary.dispose();
            }
        }
        catch (RepositoryException e) {
            log.error("Cannot obtain stream from " + this.item, (Throwable)e);
        }
    }

    @Override
    public DavProperty<?> getProperty(DavPropertyName name) {
        AbstractDavProperty prop = super.getProperty(name);
        if (prop == null && this.exists()) {
            try {
                Property p = (Property)this.item;
                if (this.isMultiple()) {
                    if (JCR_LENGTHS.equals(name)) {
                        prop = new LengthsProperty(p.getLengths());
                    }
                } else if (JCR_LENGTH.equals(name)) {
                    long length = p.getLength();
                    prop = new DefaultDavProperty<String>(JCR_LENGTH, String.valueOf(length), true);
                } else if (JCR_GET_STRING.equals(name) && p.getType() != 2) {
                    prop = new DefaultDavProperty<String>(JCR_GET_STRING, p.getString(), true);
                }
            }
            catch (RepositoryException e) {
                log.error("Failed to retrieve resource properties: " + e.getMessage());
            }
        }
        return prop;
    }

    @Override
    public void setProperty(DavProperty<?> property) throws DavException {
        this.internalSetProperty(property);
        this.complete();
    }

    private void internalSetProperty(DavProperty<?> property) throws DavException {
        block5: {
            if (!this.exists()) {
                throw new DavException(404);
            }
            try {
                Property prop = (Property)this.item;
                int defaultType = prop.getType();
                ValueFactory vfact = this.getRepositorySession().getValueFactory();
                ValuesProperty vp = new ValuesProperty(property, defaultType, vfact);
                if (property.getName().equals(JCR_VALUE)) {
                    prop.setValue(vp.getJcrValue(vp.getValueType(), vfact));
                    break block5;
                }
                if (property.getName().equals(JCR_VALUES)) {
                    prop.setValue(vp.getJcrValues());
                    break block5;
                }
                throw new DavException(409);
            }
            catch (RepositoryException e) {
                throw new JcrDavException(e);
            }
        }
    }

    @Override
    public void removeProperty(DavPropertyName propertyName) throws DavException {
        if (!this.exists()) {
            throw new DavException(404);
        }
        throw new DavException(403);
    }

    @Override
    public MultiStatusResponse alterProperties(List<? extends PropEntry> changeList) throws DavException {
        for (PropEntry propEntry : changeList) {
            if (propEntry instanceof DavPropertyName) {
                throw new DavException(403);
            }
            if (propEntry instanceof DavProperty) {
                DavProperty prop = (DavProperty)propEntry;
                this.internalSetProperty(prop);
                continue;
            }
            throw new IllegalArgumentException("unknown object in change list: " + propEntry.getClass().getName());
        }
        this.complete();
        return new MultiStatusResponse(this.getHref(), 200);
    }

    @Override
    public void addMember(DavResource resource, InputContext inputContext) throws DavException {
        throw new DavException(405, "Cannot add members to a non-collection resource");
    }

    @Override
    public DavResourceIterator getMembers() {
        log.warn("A non-collection resource never has internal members.");
        List<DavResource> drl = Collections.emptyList();
        return new DavResourceIteratorImpl(drl);
    }

    @Override
    public void removeMember(DavResource member) throws DavException {
        throw new DavException(405, "Cannot remove members from a non-collection resource");
    }

    @Override
    public ActiveLock getLock(Type type, Scope scope) {
        if (Type.WRITE.equals(type)) {
            return this.getCollection().getLock(type, scope);
        }
        return super.getLock(type, scope);
    }

    @Override
    protected void initPropertyNames() {
        super.initPropertyNames();
        if (this.exists()) {
            DavPropertyNameSet propNames = this.isMultiple() ? JcrDavPropertyNameSet.PROPERTY_MV_SET : JcrDavPropertyNameSet.PROPERTY_SET;
            this.names.addAll(propNames);
        }
    }

    @Override
    protected void initProperties() {
        super.initProperties();
        if (this.exists()) {
            try {
                Property prop = (Property)this.item;
                int type = prop.getType();
                String contentType = this.isMultiple() ? IOUtil.buildContentType("text/xml", "utf-8") : IOUtil.buildContentType(JcrValueType.contentTypeFromType(type), "utf-8");
                this.properties.add(new DefaultDavProperty<String>(DavPropertyName.GETCONTENTTYPE, contentType));
                this.properties.add(new DefaultDavProperty<String>(JCR_TYPE, PropertyType.nameFromValue(type)));
                if (this.isMultiple()) {
                    this.properties.add(new ValuesProperty(prop.getValues()));
                } else {
                    this.properties.add(new ValuesProperty(prop.getValue()));
                }
            }
            catch (RepositoryException e) {
                log.error("Failed to retrieve resource properties: " + e.getMessage());
            }
        }
    }

    private boolean isMultiple() {
        try {
            if (this.exists() && ((Property)this.item).isMultiple()) {
                return true;
            }
        }
        catch (RepositoryException e) {
            log.error("Error while retrieving property definition: " + e.getMessage());
        }
        return false;
    }
}


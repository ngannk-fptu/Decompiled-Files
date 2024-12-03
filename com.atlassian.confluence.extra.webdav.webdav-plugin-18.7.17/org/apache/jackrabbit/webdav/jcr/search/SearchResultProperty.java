/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.search;

import java.util.ArrayList;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import org.apache.jackrabbit.commons.webdav.QueryUtil;
import org.apache.jackrabbit.value.ValueHelper;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.apache.jackrabbit.webdav.xml.DomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SearchResultProperty
extends AbstractDavProperty<Value[]>
implements ItemResourceConstants {
    private static Logger log = LoggerFactory.getLogger(SearchResultProperty.class);
    private static final DavPropertyName SEARCH_RESULT_PROPERTY = DavPropertyName.create("search-result-property", ItemResourceConstants.NAMESPACE);
    private final String[] columnNames;
    private final String[] selectorNames;
    private final Value[] values;

    public SearchResultProperty(String[] columnNames, String[] selectorNames, Value[] values) {
        super(SEARCH_RESULT_PROPERTY, true);
        this.columnNames = columnNames;
        this.selectorNames = selectorNames;
        this.values = values;
    }

    public SearchResultProperty(DavProperty<?> property, ValueFactory valueFactory) throws RepositoryException {
        super(property.getName(), true);
        if (!SEARCH_RESULT_PROPERTY.equals(this.getName())) {
            throw new IllegalArgumentException("SearchResultProperty may only be created from a property named " + SEARCH_RESULT_PROPERTY.toString());
        }
        ArrayList<String> colList = new ArrayList<String>();
        ArrayList<String> selList = new ArrayList<String>();
        ArrayList<Value> valList = new ArrayList<Value>();
        QueryUtil.parseResultPropertyValue(property.getValue(), colList, selList, valList, valueFactory);
        this.columnNames = colList.toArray(new String[colList.size()]);
        this.selectorNames = selList.toArray(new String[selList.size()]);
        this.values = valList.toArray(new Value[valList.size()]);
    }

    public String[] getColumnNames() {
        return this.columnNames;
    }

    public String[] getSelectorNames() {
        return this.selectorNames;
    }

    public Value[] getValues() {
        return this.values;
    }

    @Override
    public Value[] getValue() {
        return this.values;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        for (int i = 0; i < this.columnNames.length; ++i) {
            String propertyName = this.columnNames[i];
            String selectorName = this.selectorNames[i];
            Value propertyValue = this.values[i];
            Element columnEl = DomUtil.addChildElement(elem, "column", ItemResourceConstants.NAMESPACE);
            DomUtil.addChildElement(columnEl, JCR_NAME.getName(), JCR_NAME.getNamespace(), propertyName);
            if (propertyValue != null) {
                try {
                    String serializedValue = ValueHelper.serialize(propertyValue, true);
                    Element xmlValue = DomUtil.addChildElement(columnEl, "value", ItemResourceConstants.NAMESPACE, serializedValue);
                    String type = PropertyType.nameFromValue(propertyValue.getType());
                    DomUtil.setAttribute(xmlValue, "type", ItemResourceConstants.NAMESPACE, type);
                }
                catch (RepositoryException e) {
                    log.error(e.toString());
                }
            }
            if (selectorName == null) continue;
            DomUtil.addChildElement(columnEl, JCR_SELECTOR_NAME.getName(), JCR_SELECTOR_NAME.getNamespace(), selectorName);
        }
        return elem;
    }
}


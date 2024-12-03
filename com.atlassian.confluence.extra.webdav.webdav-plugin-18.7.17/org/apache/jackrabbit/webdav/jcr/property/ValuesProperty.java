/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package org.apache.jackrabbit.webdav.jcr.property;

import javax.jcr.RepositoryException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;
import javax.jcr.ValueFormatException;
import org.apache.jackrabbit.commons.webdav.ValueUtil;
import org.apache.jackrabbit.value.ValueHelper;
import org.apache.jackrabbit.webdav.DavException;
import org.apache.jackrabbit.webdav.jcr.ItemResourceConstants;
import org.apache.jackrabbit.webdav.property.AbstractDavProperty;
import org.apache.jackrabbit.webdav.property.DavProperty;
import org.apache.jackrabbit.webdav.property.DavPropertyName;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ValuesProperty
extends AbstractDavProperty<Value[]>
implements ItemResourceConstants {
    private static Logger log = LoggerFactory.getLogger(ValuesProperty.class);
    private final Value[] jcrValues;

    public ValuesProperty(Value value) {
        super(JCR_VALUE, false);
        Value[] valueArray;
        if (value == null) {
            valueArray = new Value[]{};
        } else {
            Value[] valueArray2 = new Value[1];
            valueArray = valueArray2;
            valueArray2[0] = value;
        }
        this.jcrValues = valueArray;
    }

    public ValuesProperty(Value[] values) {
        super(JCR_VALUES, false);
        this.jcrValues = values == null ? new Value[]{} : values;
    }

    public ValuesProperty(DavProperty<?> property, int defaultType, ValueFactory valueFactory) throws RepositoryException, DavException {
        super(property.getName(), false);
        if (!JCR_VALUES.equals(property.getName()) && !JCR_VALUE.equals(this.getName())) {
            throw new DavException(400, "ValuesProperty may only be created with a property that has name=" + JCR_VALUES.getName());
        }
        this.jcrValues = ValueUtil.valuesFromXml(property.getValue(), defaultType, valueFactory);
    }

    private void checkPropertyName(DavPropertyName reqName) throws ValueFormatException {
        if (!reqName.equals(this.getName())) {
            throw new ValueFormatException("Attempt to retrieve multiple values from single property '" + this.getName() + "'.");
        }
    }

    public Value[] getJcrValues(int propertyType, ValueFactory valueFactory) throws ValueFormatException {
        this.checkPropertyName(JCR_VALUES);
        Value[] vs = new Value[this.jcrValues.length];
        for (int i = 0; i < this.jcrValues.length; ++i) {
            vs[i] = ValueHelper.convert(this.jcrValues[i], propertyType, valueFactory);
        }
        return vs;
    }

    public Value[] getJcrValues() throws ValueFormatException {
        this.checkPropertyName(JCR_VALUES);
        return this.jcrValues;
    }

    public Value getJcrValue(int propertyType, ValueFactory valueFactory) throws ValueFormatException {
        this.checkPropertyName(JCR_VALUE);
        return this.jcrValues.length == 0 ? null : ValueHelper.convert(this.jcrValues[0], propertyType, valueFactory);
    }

    public Value getJcrValue() throws ValueFormatException {
        this.checkPropertyName(JCR_VALUE);
        return this.jcrValues.length == 0 ? null : this.jcrValues[0];
    }

    public int getValueType() {
        return this.jcrValues.length > 0 ? this.jcrValues[0].getType() : 0;
    }

    @Override
    public Value[] getValue() {
        return this.jcrValues;
    }

    @Override
    public Element toXml(Document document) {
        Element elem = this.getName().toXml(document);
        try {
            for (Value v : this.jcrValues) {
                Element xmlValue = ValueUtil.valueToXml(v, document);
                elem.appendChild(xmlValue);
            }
        }
        catch (RepositoryException e) {
            log.error("Unexpected Error while converting jcr value to String: " + e.getMessage());
        }
        return elem;
    }
}


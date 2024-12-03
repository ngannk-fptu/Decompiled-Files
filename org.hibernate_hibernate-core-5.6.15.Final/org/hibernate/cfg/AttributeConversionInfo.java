/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.persistence.AttributeConverter
 *  javax.persistence.Convert
 *  org.hibernate.annotations.common.reflection.XAnnotatedElement
 */
package org.hibernate.cfg;

import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import org.hibernate.annotations.common.reflection.XAnnotatedElement;

public class AttributeConversionInfo {
    private final Class<? extends AttributeConverter> converterClass;
    private final boolean conversionDisabled;
    private final String attributeName;
    private final XAnnotatedElement source;

    public AttributeConversionInfo(Class<? extends AttributeConverter> converterClass, boolean conversionDisabled, String attributeName, XAnnotatedElement source) {
        this.converterClass = converterClass;
        this.conversionDisabled = conversionDisabled;
        this.attributeName = attributeName;
        this.source = source;
    }

    public AttributeConversionInfo(Convert convertAnnotation, XAnnotatedElement xAnnotatedElement) {
        this(convertAnnotation.converter(), convertAnnotation.disableConversion(), convertAnnotation.attributeName(), xAnnotatedElement);
    }

    public Class<? extends AttributeConverter> getConverterClass() {
        return this.converterClass;
    }

    public boolean isConversionDisabled() {
        return this.conversionDisabled;
    }

    public String getAttributeName() {
        return this.attributeName;
    }

    public XAnnotatedElement getSource() {
        return this.source;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.builder.EqualsBuilder
 *  org.apache.commons.lang3.builder.HashCodeBuilder
 */
package com.atlassian.confluence.content.render.xhtml.storage.macro.inlinebody;

import java.io.Writer;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

class AttributeWrapper
implements Attribute {
    private final Attribute attribute;

    AttributeWrapper(Attribute attribute) {
        this.attribute = attribute;
    }

    public boolean equals(Object obj) {
        if (obj == this.attribute) {
            return true;
        }
        if (!(obj instanceof Attribute)) {
            return false;
        }
        Attribute that = (Attribute)obj;
        return new EqualsBuilder().append((Object)this.attribute.getName(), (Object)that.getName()).append((Object)this.attribute.getValue(), (Object)that.getValue()).append((Object)this.attribute.getDTDType(), (Object)that.getDTDType()).append(this.attribute.isSpecified(), that.isSpecified()).isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder().append((Object)this.attribute.getName()).append((Object)this.attribute.getValue()).append((Object)this.attribute.getDTDType()).append(this.attribute.isSpecified()).toHashCode();
    }

    @Override
    public QName getName() {
        return this.attribute.getName();
    }

    @Override
    public String getValue() {
        return this.attribute.getValue();
    }

    @Override
    public String getDTDType() {
        return this.attribute.getDTDType();
    }

    @Override
    public boolean isSpecified() {
        return this.attribute.isSpecified();
    }

    @Override
    public int getEventType() {
        return this.attribute.getEventType();
    }

    @Override
    public Location getLocation() {
        return this.attribute.getLocation();
    }

    @Override
    public boolean isStartElement() {
        return this.attribute.isStartElement();
    }

    @Override
    public boolean isAttribute() {
        return this.attribute.isAttribute();
    }

    @Override
    public boolean isNamespace() {
        return this.attribute.isNamespace();
    }

    @Override
    public boolean isEndElement() {
        return this.attribute.isEndElement();
    }

    @Override
    public boolean isEntityReference() {
        return this.attribute.isEntityReference();
    }

    @Override
    public boolean isProcessingInstruction() {
        return this.attribute.isProcessingInstruction();
    }

    @Override
    public boolean isCharacters() {
        return this.attribute.isCharacters();
    }

    @Override
    public boolean isStartDocument() {
        return this.attribute.isStartDocument();
    }

    @Override
    public boolean isEndDocument() {
        return this.attribute.isEndDocument();
    }

    @Override
    public StartElement asStartElement() {
        return this.attribute.asStartElement();
    }

    @Override
    public EndElement asEndElement() {
        return this.attribute.asEndElement();
    }

    @Override
    public Characters asCharacters() {
        return this.attribute.asCharacters();
    }

    @Override
    public QName getSchemaType() {
        return this.attribute.getSchemaType();
    }

    @Override
    public void writeAsEncodedUnicode(Writer writer) throws XMLStreamException {
        this.attribute.writeAsEncodedUnicode(writer);
    }
}


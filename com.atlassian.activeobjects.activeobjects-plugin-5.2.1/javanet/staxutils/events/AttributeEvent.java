/*
 * Decompiled with CFR 0.152.
 */
package javanet.staxutils.events;

import javanet.staxutils.events.AbstractXMLEvent;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.events.Attribute;

public class AttributeEvent
extends AbstractXMLEvent
implements Attribute {
    private boolean specified = true;
    private QName name;
    private String value;
    private String dtdType = "CDATA";

    public AttributeEvent(QName name, String value) {
        this.name = name;
        this.value = value;
    }

    public AttributeEvent(QName name, String value, boolean specified) {
        this.name = name;
        this.value = value;
        this.specified = specified;
    }

    public AttributeEvent(QName name, String value, Location location) {
        super(location);
        this.name = name;
        this.value = value;
    }

    public AttributeEvent(QName name, String value, Location location, QName schemaType) {
        super(location, schemaType);
        this.name = name;
        this.value = value;
    }

    public AttributeEvent(QName name, String value, boolean specified, String dtdType, Location location, QName schemaType) {
        super(location, schemaType);
        this.name = name;
        this.value = value;
        this.specified = specified;
        this.dtdType = dtdType;
    }

    public AttributeEvent(QName name, String value, Attribute that) {
        super(that);
        this.specified = that.isSpecified();
        this.name = name == null ? that.getName() : name;
        this.value = value == null ? that.getValue() : value;
        this.dtdType = that.getDTDType();
    }

    public AttributeEvent(Attribute that) {
        super(that);
        this.specified = that.isSpecified();
        this.name = that.getName();
        this.value = that.getValue();
        this.dtdType = that.getDTDType();
    }

    public int getEventType() {
        return 10;
    }

    public QName getName() {
        return this.name;
    }

    public String getValue() {
        return this.value;
    }

    public boolean isSpecified() {
        return this.specified;
    }

    public String getDTDType() {
        return this.dtdType;
    }
}


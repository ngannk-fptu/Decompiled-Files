/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import java.util.Calendar;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.ResourceEventType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.Types;

@StructuredType(preferedPrefix="stVer", namespace="http://ns.adobe.com/xap/1.0/sType/Version#")
public class VersionType
extends AbstractStructuredType {
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String COMMENTS = "comments";
    @PropertyType(type=Types.ResourceEvent, card=Cardinality.Simple)
    public static final String EVENT = "event";
    @PropertyType(type=Types.ProperName, card=Cardinality.Simple)
    public static final String MODIFIER = "modifier";
    @PropertyType(type=Types.Date, card=Cardinality.Simple)
    public static final String MODIFY_DATE = "modifyDate";
    @PropertyType(type=Types.Text, card=Cardinality.Simple)
    public static final String VERSION = "version";

    public VersionType(XMPMetadata metadata) {
        super(metadata);
        this.addNamespace(this.getNamespace(), this.getPreferedPrefix());
    }

    public String getComments() {
        return this.getPropertyValueAsString(COMMENTS);
    }

    public void setComments(String value) {
        this.addSimpleProperty(COMMENTS, value);
    }

    public ResourceEventType getEvent() {
        return (ResourceEventType)this.getFirstEquivalentProperty(EVENT, ResourceEventType.class);
    }

    public void setEvent(ResourceEventType value) {
        this.addProperty(value);
    }

    public Calendar getModifyDate() {
        return this.getDatePropertyAsCalendar(MODIFY_DATE);
    }

    public void setModifyDate(Calendar value) {
        this.addSimpleProperty(MODIFY_DATE, value);
    }

    public String getVersion() {
        return this.getPropertyValueAsString(VERSION);
    }

    public void setVersion(String value) {
        this.addSimpleProperty(VERSION, value);
    }

    public String getModifier() {
        return this.getPropertyValueAsString(MODIFIER);
    }

    public void setModifier(String value) {
        this.addSimpleProperty(MODIFIER, value);
    }
}


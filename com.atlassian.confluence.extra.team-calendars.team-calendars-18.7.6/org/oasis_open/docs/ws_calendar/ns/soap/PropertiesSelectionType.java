/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertyReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.PropertySelectionType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropertiesSelectionType", propOrder={"property", "remove", "add"})
public class PropertiesSelectionType {
    protected List<PropertySelectionType> property;
    protected List<PropertyReferenceType> remove;
    protected List<PropertyReferenceType> add;

    public List<PropertySelectionType> getProperty() {
        if (this.property == null) {
            this.property = new ArrayList<PropertySelectionType>();
        }
        return this.property;
    }

    public List<PropertyReferenceType> getRemove() {
        if (this.remove == null) {
            this.remove = new ArrayList<PropertyReferenceType>();
        }
        return this.remove;
    }

    public List<PropertyReferenceType> getAdd() {
        if (this.add == null) {
            this.add = new ArrayList<PropertyReferenceType>();
        }
        return this.add;
    }
}


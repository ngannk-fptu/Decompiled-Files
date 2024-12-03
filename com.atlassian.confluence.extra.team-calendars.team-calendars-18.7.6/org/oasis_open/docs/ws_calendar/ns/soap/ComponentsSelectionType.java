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
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ComponentSelectionType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ComponentsSelectionType", propOrder={"component", "remove", "add"})
public class ComponentsSelectionType {
    protected List<ComponentSelectionType> component;
    protected List<ComponentReferenceType> remove;
    protected List<ComponentReferenceType> add;

    public List<ComponentSelectionType> getComponent() {
        if (this.component == null) {
            this.component = new ArrayList<ComponentSelectionType>();
        }
        return this.component;
    }

    public List<ComponentReferenceType> getRemove() {
        if (this.remove == null) {
            this.remove = new ArrayList<ComponentReferenceType>();
        }
        return this.remove;
    }

    public List<ComponentReferenceType> getAdd() {
        if (this.add == null) {
            this.add = new ArrayList<ComponentReferenceType>();
        }
        return this.add;
    }
}


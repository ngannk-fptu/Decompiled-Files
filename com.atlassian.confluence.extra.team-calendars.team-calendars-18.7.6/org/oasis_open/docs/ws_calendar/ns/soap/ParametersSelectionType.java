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
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterReferenceType;
import org.oasis_open.docs.ws_calendar.ns.soap.ParameterSelectionType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="ParametersSelectionType", propOrder={"parameter", "remove", "add"})
public class ParametersSelectionType {
    protected List<ParameterSelectionType> parameter;
    protected List<ParameterReferenceType> remove;
    protected List<ParameterReferenceType> add;

    public List<ParameterSelectionType> getParameter() {
        if (this.parameter == null) {
            this.parameter = new ArrayList<ParameterSelectionType>();
        }
        return this.parameter;
    }

    public List<ParameterReferenceType> getRemove() {
        if (this.remove == null) {
            this.remove = new ArrayList<ParameterReferenceType>();
        }
        return this.remove;
    }

    public List<ParameterReferenceType> getAdd() {
        if (this.add == null) {
            this.add = new ArrayList<ParameterReferenceType>();
        }
        return this.add;
    }
}


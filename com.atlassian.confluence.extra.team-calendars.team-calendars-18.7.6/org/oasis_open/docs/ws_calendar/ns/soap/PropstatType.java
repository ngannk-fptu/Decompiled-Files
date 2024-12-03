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
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatusPropElementType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="PropstatType", propOrder={"prop"})
public class PropstatType
extends BaseResponseType {
    protected List<MultistatusPropElementType> prop;

    public List<MultistatusPropElementType> getProp() {
        if (this.prop == null) {
            this.prop = new ArrayList<MultistatusPropElementType>();
        }
        return this.prop;
    }
}


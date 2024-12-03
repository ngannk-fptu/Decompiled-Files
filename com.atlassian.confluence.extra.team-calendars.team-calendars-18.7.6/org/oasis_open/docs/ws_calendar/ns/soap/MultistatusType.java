/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.CalendarQueryResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.MultistatResponseElementType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="MultistatusType", propOrder={"response"})
@XmlSeeAlso(value={CalendarQueryResponseType.class})
public class MultistatusType
extends BaseResponseType {
    protected List<MultistatResponseElementType> response;

    public List<MultistatResponseElementType> getResponse() {
        if (this.response == null) {
            this.response = new ArrayList<MultistatResponseElementType>();
        }
        return this.response;
    }
}


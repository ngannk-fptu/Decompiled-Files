/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlElements
 *  javax.xml.bind.annotation.XmlType
 */
package org.oasis_open.docs.ws_calendar.ns.soap;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlType;
import org.oasis_open.docs.ws_calendar.ns.soap.BaseResponseType;
import org.oasis_open.docs.ws_calendar.ns.soap.ChildCollectionType;
import org.oasis_open.docs.ws_calendar.ns.soap.CreationDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.DisplayNameType;
import org.oasis_open.docs.ws_calendar.ns.soap.GetPropertiesBasePropertyType;
import org.oasis_open.docs.ws_calendar.ns.soap.LastModifiedDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxAttendeesPerInstanceType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxInstancesType;
import org.oasis_open.docs.ws_calendar.ns.soap.MaxResourceSizeType;
import org.oasis_open.docs.ws_calendar.ns.soap.MinDateTimeType;
import org.oasis_open.docs.ws_calendar.ns.soap.PrincipalHomeType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceDescriptionType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceOwnerType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceTimezoneIdType;
import org.oasis_open.docs.ws_calendar.ns.soap.ResourceTypeType;
import org.oasis_open.docs.ws_calendar.ns.soap.SupportedCalendarComponentSetType;
import org.oasis_open.docs.ws_calendar.ns.soap.SupportedFeaturesType;
import org.oasis_open.docs.ws_calendar.ns.soap.TimezoneServerType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="GetPropertiesResponseType", propOrder={"href", "childCollectionOrCreationDateTimeOrDisplayName"})
public class GetPropertiesResponseType
extends BaseResponseType {
    @XmlElement(required=true)
    protected String href;
    @XmlElements(value={@XmlElement(name="supportedFeatures", type=SupportedFeaturesType.class), @XmlElement(name="maxDateTime", type=MaxDateTimeType.class), @XmlElement(name="maxAttendeesPerInstance", type=MaxAttendeesPerInstanceType.class), @XmlElement(name="resourceDescription", type=ResourceDescriptionType.class), @XmlElement(name="resourceOwner", type=ResourceOwnerType.class), @XmlElement(name="resourceType", type=ResourceTypeType.class), @XmlElement(name="lastModifiedDateTime", type=LastModifiedDateTimeType.class), @XmlElement(name="maxInstances", type=MaxInstancesType.class), @XmlElement(name="creationDateTime", type=CreationDateTimeType.class), @XmlElement(name="displayName", type=DisplayNameType.class), @XmlElement(name="minDateTime", type=MinDateTimeType.class), @XmlElement(name="childCollection", type=ChildCollectionType.class), @XmlElement(name="principalHome", type=PrincipalHomeType.class), @XmlElement(name="timezoneServer", type=TimezoneServerType.class), @XmlElement(name="maxResourceSize", type=MaxResourceSizeType.class), @XmlElement(name="supportedCalendarComponentSet", type=SupportedCalendarComponentSetType.class), @XmlElement(name="resourceTimezoneId", type=ResourceTimezoneIdType.class)})
    protected List<GetPropertiesBasePropertyType> childCollectionOrCreationDateTimeOrDisplayName;

    public String getHref() {
        return this.href;
    }

    public void setHref(String value) {
        this.href = value;
    }

    public List<GetPropertiesBasePropertyType> getChildCollectionOrCreationDateTimeOrDisplayName() {
        if (this.childCollectionOrCreationDateTimeOrDisplayName == null) {
            this.childCollectionOrCreationDateTimeOrDisplayName = new ArrayList<GetPropertiesBasePropertyType>();
        }
        return this.childCollectionOrCreationDateTimeOrDisplayName;
    }
}


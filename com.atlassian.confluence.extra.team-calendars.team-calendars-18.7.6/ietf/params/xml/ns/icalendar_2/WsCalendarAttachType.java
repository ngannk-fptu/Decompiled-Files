/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.JAXBElement
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElementRef
 *  javax.xml.bind.annotation.XmlElementRefs
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BasePropertyType;
import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementRefs;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="WsCalendarAttachType", propOrder={"artifactOrArtifactBaseOrUri"})
public class WsCalendarAttachType
extends BasePropertyType {
    @XmlElementRefs(value={@XmlElementRef(name="artifact", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class), @XmlElementRef(name="uri", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class), @XmlElementRef(name="text", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class), @XmlElementRef(name="artifactBase", namespace="urn:ietf:params:xml:ns:icalendar-2.0", type=JAXBElement.class)})
    protected List<JAXBElement<?>> artifactOrArtifactBaseOrUri;

    public List<JAXBElement<?>> getArtifactOrArtifactBaseOrUri() {
        if (this.artifactOrArtifactBaseOrUri == null) {
            this.artifactOrArtifactBaseOrUri = new ArrayList();
        }
        return this.artifactOrArtifactBaseOrUri;
    }
}


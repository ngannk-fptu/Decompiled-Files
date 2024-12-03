/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlSeeAlso
 *  javax.xml.bind.annotation.XmlType
 */
package ietf.params.xml.ns.icalendar_2;

import ietf.params.xml.ns.icalendar_2.BaseParameterType;
import ietf.params.xml.ns.icalendar_2.CnParamType;
import ietf.params.xml.ns.icalendar_2.CutypeParamType;
import ietf.params.xml.ns.icalendar_2.EncodingParamType;
import ietf.params.xml.ns.icalendar_2.FbtypeParamType;
import ietf.params.xml.ns.icalendar_2.FmttypeParamType;
import ietf.params.xml.ns.icalendar_2.LanguageParamType;
import ietf.params.xml.ns.icalendar_2.PartstatParamType;
import ietf.params.xml.ns.icalendar_2.PublicCommentParameterType;
import ietf.params.xml.ns.icalendar_2.RelatedParamType;
import ietf.params.xml.ns.icalendar_2.ReltypeParamType;
import ietf.params.xml.ns.icalendar_2.RoleParamType;
import ietf.params.xml.ns.icalendar_2.ScheduleAgentParamType;
import ietf.params.xml.ns.icalendar_2.ScheduleForceSendParamType;
import ietf.params.xml.ns.icalendar_2.ScheduleStatusParamType;
import ietf.params.xml.ns.icalendar_2.TzidParamType;
import ietf.params.xml.ns.icalendar_2.XBedeworkLocKeyParamType;
import ietf.params.xml.ns.icalendar_2.XBedeworkUidParamType;
import ietf.params.xml.ns.icalendar_2.XBedeworkWrappedNameParamType;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSeeAlso;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(value=XmlAccessType.FIELD)
@XmlType(name="TextParameterType", propOrder={"text"})
@XmlSeeAlso(value={FmttypeParamType.class, TzidParamType.class, XBedeworkUidParamType.class, PartstatParamType.class, XBedeworkWrappedNameParamType.class, CutypeParamType.class, RoleParamType.class, ScheduleForceSendParamType.class, ReltypeParamType.class, FbtypeParamType.class, ScheduleStatusParamType.class, CnParamType.class, ScheduleAgentParamType.class, LanguageParamType.class, EncodingParamType.class, PublicCommentParameterType.class, RelatedParamType.class, XBedeworkLocKeyParamType.class})
public class TextParameterType
extends BaseParameterType {
    @XmlElement(required=true)
    protected String text;

    public String getText() {
        return this.text;
    }

    public void setText(String value) {
        this.text = value;
    }
}


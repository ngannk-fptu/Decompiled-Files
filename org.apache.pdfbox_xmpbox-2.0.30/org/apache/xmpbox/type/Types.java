/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.type;

import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AgentNameType;
import org.apache.xmpbox.type.BooleanType;
import org.apache.xmpbox.type.CFAPatternType;
import org.apache.xmpbox.type.ChoiceType;
import org.apache.xmpbox.type.DateType;
import org.apache.xmpbox.type.DeviceSettingsType;
import org.apache.xmpbox.type.DimensionsType;
import org.apache.xmpbox.type.FlashType;
import org.apache.xmpbox.type.GUIDType;
import org.apache.xmpbox.type.IntegerType;
import org.apache.xmpbox.type.JobType;
import org.apache.xmpbox.type.LayerType;
import org.apache.xmpbox.type.LocaleType;
import org.apache.xmpbox.type.MIMEType;
import org.apache.xmpbox.type.OECFType;
import org.apache.xmpbox.type.PDFAFieldType;
import org.apache.xmpbox.type.PDFAPropertyType;
import org.apache.xmpbox.type.PDFASchemaType;
import org.apache.xmpbox.type.PDFATypeType;
import org.apache.xmpbox.type.PartType;
import org.apache.xmpbox.type.ProperNameType;
import org.apache.xmpbox.type.RationalType;
import org.apache.xmpbox.type.RealType;
import org.apache.xmpbox.type.RenditionClassType;
import org.apache.xmpbox.type.ResourceEventType;
import org.apache.xmpbox.type.ResourceRefType;
import org.apache.xmpbox.type.TextType;
import org.apache.xmpbox.type.ThumbnailType;
import org.apache.xmpbox.type.URIType;
import org.apache.xmpbox.type.URLType;
import org.apache.xmpbox.type.VersionType;
import org.apache.xmpbox.type.XPathType;

public enum Types {
    Structured(false, null, null),
    DefinedType(false, null, null),
    Text(true, null, TextType.class),
    Date(true, null, DateType.class),
    Boolean(true, null, BooleanType.class),
    Integer(true, null, IntegerType.class),
    Real(true, null, RealType.class),
    GPSCoordinate(true, Text, TextType.class),
    ProperName(true, Text, ProperNameType.class),
    Locale(true, Text, LocaleType.class),
    AgentName(true, Text, AgentNameType.class),
    GUID(true, Text, GUIDType.class),
    XPath(true, Text, XPathType.class),
    Part(true, Text, PartType.class),
    URL(true, Text, URLType.class),
    URI(true, Text, URIType.class),
    Choice(true, Text, ChoiceType.class),
    MIMEType(true, Text, MIMEType.class),
    LangAlt(true, Text, TextType.class),
    RenditionClass(true, Text, RenditionClassType.class),
    Rational(true, Text, RationalType.class),
    Layer(false, Structured, LayerType.class),
    Thumbnail(false, Structured, ThumbnailType.class),
    ResourceEvent(false, Structured, ResourceEventType.class),
    ResourceRef(false, Structured, ResourceRefType.class),
    Version(false, Structured, VersionType.class),
    PDFASchema(false, Structured, PDFASchemaType.class),
    PDFAField(false, Structured, PDFAFieldType.class),
    PDFAProperty(false, Structured, PDFAPropertyType.class),
    PDFAType(false, Structured, PDFATypeType.class),
    Job(false, Structured, JobType.class),
    OECF(false, Structured, OECFType.class),
    CFAPattern(false, Structured, CFAPatternType.class),
    DeviceSettings(false, Structured, DeviceSettingsType.class),
    Flash(false, Structured, FlashType.class),
    Dimensions(false, Structured, DimensionsType.class);

    private final boolean simple;
    private final Types basic;
    private final Class<? extends AbstractField> clz;

    private Types(boolean s, Types b, Class<? extends AbstractField> c) {
        this.simple = s;
        this.basic = b;
        this.clz = c;
    }

    public boolean isSimple() {
        return this.simple;
    }

    public boolean isBasic() {
        return this.basic == null;
    }

    public boolean isStructured() {
        return this.basic == Structured;
    }

    public boolean isDefined() {
        return this == DefinedType;
    }

    public Types getBasic() {
        return this.basic;
    }

    public Class<? extends AbstractField> getImplementingClass() {
        return this.clz;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xdgf.usermodel.section;

import com.microsoft.schemas.office.visio.x2012.main.SectionType;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.poi.ooxml.POIXMLException;
import org.apache.poi.util.Internal;
import org.apache.poi.xdgf.usermodel.XDGFSheet;
import org.apache.poi.xdgf.usermodel.section.CharacterSection;
import org.apache.poi.xdgf.usermodel.section.GenericSection;
import org.apache.poi.xdgf.usermodel.section.GeometrySection;
import org.apache.poi.xdgf.usermodel.section.XDGFSection;

@Internal
enum XDGFSectionTypes {
    LINE_GRADIENT("LineGradient", GenericSection::new),
    FILL_GRADIENT("FillGradient", GenericSection::new),
    CHARACTER("Character", CharacterSection::new),
    PARAGRAPH("Paragraph", GenericSection::new),
    TABS("Tabs", GenericSection::new),
    SCRATCH("Scratch", GenericSection::new),
    CONNECTION("Connection", GenericSection::new),
    CONNECTION_ABCD("ConnectionABCD", GenericSection::new),
    FIELD("Field", GenericSection::new),
    CONTROL("Control", GenericSection::new),
    GEOMETRY("Geometry", GeometrySection::new),
    ACTIONS("Actions", GenericSection::new),
    LAYER("Layer", GenericSection::new),
    USER("User", GenericSection::new),
    PROPERTY("Property", GenericSection::new),
    HYPERLINK("Hyperlink", GenericSection::new),
    REVIEWER("Reviewer", GenericSection::new),
    ANNOTATION("Annotation", GenericSection::new),
    ACTION_TAG("ActionTag", GenericSection::new);

    private final String sectionType;
    private final BiFunction<SectionType, XDGFSheet, ? extends XDGFSection> constructor;
    private static final Map<String, XDGFSectionTypes> LOOKUP;

    private XDGFSectionTypes(String sectionType, BiFunction<SectionType, XDGFSheet, ? extends XDGFSection> constructor) {
        this.sectionType = sectionType;
        this.constructor = constructor;
    }

    public String getSectionType() {
        return this.sectionType;
    }

    public static XDGFSection load(SectionType section, XDGFSheet containingSheet) {
        String name = section.getN();
        XDGFSectionTypes l = LOOKUP.get(name);
        if (l == null) {
            String typeName = section.schemaType().getName().getLocalPart();
            throw new POIXMLException("Invalid '" + typeName + "' name '" + name + "'");
        }
        return l.constructor.apply(section, containingSheet);
    }

    static {
        LOOKUP = Stream.of(XDGFSectionTypes.values()).collect(Collectors.toMap(XDGFSectionTypes::getSectionType, Function.identity()));
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox.xml;

import java.util.List;
import java.util.Map;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.PDFAExtensionSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.schema.XMPSchemaFactory;
import org.apache.xmpbox.type.AbstractField;
import org.apache.xmpbox.type.AbstractStructuredType;
import org.apache.xmpbox.type.ArrayProperty;
import org.apache.xmpbox.type.Cardinality;
import org.apache.xmpbox.type.DefinedStructuredType;
import org.apache.xmpbox.type.PDFAFieldType;
import org.apache.xmpbox.type.PDFAPropertyType;
import org.apache.xmpbox.type.PDFASchemaType;
import org.apache.xmpbox.type.PDFATypeType;
import org.apache.xmpbox.type.PropertiesDescription;
import org.apache.xmpbox.type.PropertyType;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TypeMapping;
import org.apache.xmpbox.type.Types;
import org.apache.xmpbox.xml.XmpParsingException;
import org.w3c.dom.Attr;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

public final class PdfaExtensionHelper {
    public static final String CLOSED_CHOICE = "closed Choice of ";
    public static final String OPEN_CHOICE = "open Choice of ";

    private PdfaExtensionHelper() {
    }

    public static void validateNaming(XMPMetadata meta, Element description) throws XmpParsingException {
        NamedNodeMap nnm = description.getAttributes();
        for (int i = 0; i < nnm.getLength(); ++i) {
            Attr attr = (Attr)nnm.item(i);
            PdfaExtensionHelper.checkNamespaceDeclaration(attr, PDFAExtensionSchema.class);
            PdfaExtensionHelper.checkNamespaceDeclaration(attr, PDFAFieldType.class);
            PdfaExtensionHelper.checkNamespaceDeclaration(attr, PDFAPropertyType.class);
            PdfaExtensionHelper.checkNamespaceDeclaration(attr, PDFASchemaType.class);
            PdfaExtensionHelper.checkNamespaceDeclaration(attr, PDFATypeType.class);
        }
    }

    private static void checkNamespaceDeclaration(Attr attr, Class<? extends AbstractStructuredType> clz) throws XmpParsingException {
        String prefix = attr.getLocalName();
        String namespace = attr.getValue();
        String cprefix = clz.getAnnotation(StructuredType.class).preferedPrefix();
        String cnamespace = clz.getAnnotation(StructuredType.class).namespace();
        if (cprefix.equals(prefix) && !cnamespace.equals(namespace)) {
            throw new XmpParsingException(XmpParsingException.ErrorType.InvalidPdfaSchema, "Invalid PDF/A namespace definition, prefix: " + prefix + ", namespace: " + namespace);
        }
        if (cnamespace.equals(namespace) && !cprefix.equals(prefix)) {
            throw new XmpParsingException(XmpParsingException.ErrorType.InvalidPdfaSchema, "Invalid PDF/A namespace definition, prefix: " + prefix + ", namespace: " + namespace);
        }
    }

    public static void populateSchemaMapping(XMPMetadata meta) throws XmpParsingException {
        List<XMPSchema> schems = meta.getAllSchemas();
        TypeMapping tm = meta.getTypeMapping();
        StructuredType stPdfaExt = PDFAExtensionSchema.class.getAnnotation(StructuredType.class);
        for (XMPSchema xmpSchema : schems) {
            if (!xmpSchema.getNamespace().equals(stPdfaExt.namespace())) continue;
            if (!xmpSchema.getPrefix().equals(stPdfaExt.preferedPrefix())) {
                throw new XmpParsingException(XmpParsingException.ErrorType.InvalidPrefix, "Found invalid prefix for PDF/A extension, found '" + xmpSchema.getPrefix() + "', should be '" + stPdfaExt.preferedPrefix() + "'");
            }
            PDFAExtensionSchema pes = (PDFAExtensionSchema)xmpSchema;
            ArrayProperty sp = pes.getSchemasProperty();
            for (AbstractField af : sp.getAllProperties()) {
                if (!(af instanceof PDFASchemaType)) continue;
                PdfaExtensionHelper.populatePDFASchemaType(meta, (PDFASchemaType)af, tm);
            }
        }
    }

    private static void populatePDFASchemaType(XMPMetadata meta, PDFASchemaType st, TypeMapping tm) throws XmpParsingException {
        String namespaceUri = st.getNamespaceURI();
        if (namespaceUri == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.RequiredProperty, "Missing pdfaSchema:namespaceURI in type definition");
        }
        namespaceUri = namespaceUri.trim();
        String prefix = st.getPrefixValue();
        ArrayProperty properties = st.getProperty();
        ArrayProperty valueTypes = st.getValueType();
        XMPSchemaFactory xsf = tm.getSchemaFactory(namespaceUri);
        if (xsf == null) {
            tm.addNewNameSpace(namespaceUri, prefix);
            xsf = tm.getSchemaFactory(namespaceUri);
        }
        if (valueTypes != null) {
            for (AbstractField af2 : valueTypes.getAllProperties()) {
                if (!(af2 instanceof PDFATypeType)) continue;
                PdfaExtensionHelper.populatePDFAType(meta, (PDFATypeType)af2, tm);
            }
        }
        if (properties == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.RequiredProperty, "Missing pdfaSchema:property in type definition");
        }
        for (AbstractField af2 : properties.getAllProperties()) {
            if (!(af2 instanceof PDFAPropertyType)) continue;
            PdfaExtensionHelper.populatePDFAPropertyType((PDFAPropertyType)af2, tm, xsf);
        }
    }

    private static void populatePDFAPropertyType(PDFAPropertyType property, TypeMapping tm, XMPSchemaFactory xsf) throws XmpParsingException {
        String pname = property.getName();
        String ptype = property.getValueType();
        String pdescription = property.getDescription();
        String pCategory = property.getCategory();
        if (pname == null || ptype == null || pdescription == null || pCategory == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.RequiredProperty, "Missing field in property definition");
        }
        PropertyType pt = PdfaExtensionHelper.transformValueType(tm, ptype);
        if (pt == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoValueType, "Unknown property value type : " + ptype);
        }
        if (pt.type() == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoValueType, "Type not defined : " + ptype);
        }
        if (!pt.type().isSimple() && !pt.type().isStructured() && pt.type() != Types.DefinedType) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoValueType, "Type not defined : " + ptype);
        }
        xsf.getPropertyDefinition().addNewProperty(pname, pt);
    }

    private static void populatePDFAType(XMPMetadata meta, PDFATypeType type, TypeMapping tm) throws XmpParsingException {
        String ttype = type.getType();
        String tns = type.getNamespaceURI();
        String tprefix = type.getPrefixValue();
        String tdescription = type.getDescription();
        ArrayProperty fields = type.getFields();
        if (ttype == null || tns == null || tprefix == null || tdescription == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.RequiredProperty, "Missing field in type definition");
        }
        DefinedStructuredType structuredType = new DefinedStructuredType(meta, tns, tprefix, null);
        if (fields != null) {
            List<AbstractField> definedFields = fields.getAllProperties();
            for (AbstractField abstractField : definedFields) {
                if (!(abstractField instanceof PDFAFieldType)) continue;
                PdfaExtensionHelper.populatePDFAFieldType((PDFAFieldType)abstractField, structuredType);
            }
        }
        PropertiesDescription pm = new PropertiesDescription();
        for (Map.Entry entry : structuredType.getDefinedProperties().entrySet()) {
            pm.addNewProperty((String)entry.getKey(), (PropertyType)entry.getValue());
        }
        tm.addToDefinedStructuredTypes(ttype, tns, pm);
    }

    private static void populatePDFAFieldType(PDFAFieldType field, DefinedStructuredType structuredType) throws XmpParsingException {
        String fName = field.getName();
        String fDescription = field.getDescription();
        String fValueType = field.getValueType();
        if (fName == null || fDescription == null || fValueType == null) {
            throw new XmpParsingException(XmpParsingException.ErrorType.RequiredProperty, "Missing field in field definition");
        }
        try {
            Types fValue = Types.valueOf(fValueType);
            structuredType.addProperty(fName, TypeMapping.createPropertyType(fValue, Cardinality.Simple));
        }
        catch (IllegalArgumentException e) {
            throw new XmpParsingException(XmpParsingException.ErrorType.NoValueType, "Type not defined : " + fValueType, e);
        }
    }

    private static PropertyType transformValueType(TypeMapping tm, String valueType) {
        Types type;
        Cardinality card;
        block13: {
            if ("Lang Alt".equals(valueType)) {
                return TypeMapping.createPropertyType(Types.LangAlt, Cardinality.Simple);
            }
            if (valueType.startsWith(CLOSED_CHOICE)) {
                valueType = valueType.substring(CLOSED_CHOICE.length());
            } else if (valueType.startsWith(OPEN_CHOICE)) {
                valueType = valueType.substring(OPEN_CHOICE.length());
            }
            int pos = valueType.indexOf(32);
            card = Cardinality.Simple;
            if (pos > 0) {
                String scard = valueType.substring(0, pos);
                if ("seq".equals(scard)) {
                    card = Cardinality.Seq;
                } else if ("bag".equals(scard)) {
                    card = Cardinality.Bag;
                } else if ("alt".equals(scard)) {
                    card = Cardinality.Alt;
                } else {
                    return null;
                }
            }
            String vt = valueType.substring(pos + 1);
            type = null;
            try {
                type = pos < 0 ? Types.valueOf(valueType) : Types.valueOf(vt);
            }
            catch (IllegalArgumentException e) {
                if (!tm.isDefinedType(vt)) break block13;
                type = Types.DefinedType;
            }
        }
        return TypeMapping.createPropertyType(type, card);
    }
}


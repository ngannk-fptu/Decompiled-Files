/*
 * Decompiled with CFR 0.152.
 */
package org.apache.xmpbox;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.xmpbox.XmpConstants;
import org.apache.xmpbox.schema.AdobePDFSchema;
import org.apache.xmpbox.schema.DublinCoreSchema;
import org.apache.xmpbox.schema.PDFAExtensionSchema;
import org.apache.xmpbox.schema.PDFAIdentificationSchema;
import org.apache.xmpbox.schema.PhotoshopSchema;
import org.apache.xmpbox.schema.XMPBasicJobTicketSchema;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.schema.XMPMediaManagementSchema;
import org.apache.xmpbox.schema.XMPRightsManagementSchema;
import org.apache.xmpbox.schema.XMPSchema;
import org.apache.xmpbox.schema.XmpSchemaException;
import org.apache.xmpbox.type.StructuredType;
import org.apache.xmpbox.type.TypeMapping;

public class XMPMetadata {
    private String xpacketId = null;
    private String xpacketBegin = null;
    private String xpacketBytes = null;
    private String xpacketEncoding = null;
    private String xpacketEndData = "w";
    private List<XMPSchema> schemas = new ArrayList<XMPSchema>();
    private TypeMapping typeMapping = new TypeMapping(this);

    protected XMPMetadata() {
        this("\ufeff", "W5M0MpCehiHzreSzNTczkc9d", XmpConstants.DEFAULT_XPACKET_BYTES, "UTF-8");
    }

    protected XMPMetadata(String xpacketBegin, String xpacketId, String xpacketBytes, String xpacketEncoding) {
        this.xpacketBegin = xpacketBegin;
        this.xpacketId = xpacketId;
        this.xpacketBytes = xpacketBytes;
        this.xpacketEncoding = xpacketEncoding;
    }

    public static XMPMetadata createXMPMetadata() {
        return new XMPMetadata();
    }

    public static XMPMetadata createXMPMetadata(String xpacketBegin, String xpacketId, String xpacketBytes, String xpacketEncoding) {
        return new XMPMetadata(xpacketBegin, xpacketId, xpacketBytes, xpacketEncoding);
    }

    public TypeMapping getTypeMapping() {
        return this.typeMapping;
    }

    public String getXpacketBytes() {
        return this.xpacketBytes;
    }

    public String getXpacketEncoding() {
        return this.xpacketEncoding;
    }

    public String getXpacketBegin() {
        return this.xpacketBegin;
    }

    public String getXpacketId() {
        return this.xpacketId;
    }

    public List<XMPSchema> getAllSchemas() {
        ArrayList<XMPSchema> schem = new ArrayList<XMPSchema>();
        for (XMPSchema schema : this.schemas) {
            schem.add(schema);
        }
        return schem;
    }

    public void setEndXPacket(String data) {
        this.xpacketEndData = data;
    }

    public String getEndXPacket() {
        return this.xpacketEndData;
    }

    public XMPSchema getSchema(String nsURI) {
        for (XMPSchema tmp : this.schemas) {
            if (!tmp.getNamespace().equals(nsURI)) continue;
            return tmp;
        }
        return null;
    }

    public XMPSchema getSchema(Class<? extends XMPSchema> clz) {
        StructuredType st = clz.getAnnotation(StructuredType.class);
        return this.getSchema(st.namespace());
    }

    public XMPSchema getSchema(String prefix, String nsURI) {
        for (XMPSchema tmp : this.getAllSchemas()) {
            if (!tmp.getNamespace().equals(nsURI) || !tmp.getPrefix().equals(prefix)) continue;
            return tmp;
        }
        return null;
    }

    public XMPSchema createAndAddDefaultSchema(String nsPrefix, String nsURI) {
        XMPSchema schem = new XMPSchema(this, nsURI, nsPrefix);
        schem.setAboutAsSimple("");
        this.addSchema(schem);
        return schem;
    }

    public PDFAExtensionSchema createAndAddPDFAExtensionSchemaWithDefaultNS() {
        PDFAExtensionSchema pdfAExt = new PDFAExtensionSchema(this);
        pdfAExt.setAboutAsSimple("");
        this.addSchema(pdfAExt);
        return pdfAExt;
    }

    public PDFAExtensionSchema createAndAddPDFAExtensionSchemaWithNS(Map<String, String> namespaces) throws XmpSchemaException {
        PDFAExtensionSchema pdfAExt = new PDFAExtensionSchema(this);
        pdfAExt.setAboutAsSimple("");
        this.addSchema(pdfAExt);
        return pdfAExt;
    }

    public PDFAExtensionSchema getPDFExtensionSchema() {
        return (PDFAExtensionSchema)this.getSchema(PDFAExtensionSchema.class);
    }

    @Deprecated
    public PDFAIdentificationSchema createAndAddPFAIdentificationSchema() {
        return this.createAndAddPDFAIdentificationSchema();
    }

    public PDFAIdentificationSchema createAndAddPDFAIdentificationSchema() {
        PDFAIdentificationSchema pdfAId = new PDFAIdentificationSchema(this);
        pdfAId.setAboutAsSimple("");
        this.addSchema(pdfAId);
        return pdfAId;
    }

    public PDFAIdentificationSchema getPDFIdentificationSchema() {
        return (PDFAIdentificationSchema)this.getSchema(PDFAIdentificationSchema.class);
    }

    public DublinCoreSchema createAndAddDublinCoreSchema() {
        DublinCoreSchema dc = new DublinCoreSchema(this);
        dc.setAboutAsSimple("");
        this.addSchema(dc);
        return dc;
    }

    public DublinCoreSchema getDublinCoreSchema() {
        return (DublinCoreSchema)this.getSchema(DublinCoreSchema.class);
    }

    public XMPBasicJobTicketSchema createAndAddBasicJobTicketSchema() {
        XMPBasicJobTicketSchema sc = new XMPBasicJobTicketSchema(this);
        sc.setAboutAsSimple("");
        this.addSchema(sc);
        return sc;
    }

    public XMPBasicJobTicketSchema getBasicJobTicketSchema() {
        return (XMPBasicJobTicketSchema)this.getSchema(XMPBasicJobTicketSchema.class);
    }

    public XMPRightsManagementSchema createAndAddXMPRightsManagementSchema() {
        XMPRightsManagementSchema rights = new XMPRightsManagementSchema(this);
        rights.setAboutAsSimple("");
        this.addSchema(rights);
        return rights;
    }

    public XMPRightsManagementSchema getXMPRightsManagementSchema() {
        return (XMPRightsManagementSchema)this.getSchema(XMPRightsManagementSchema.class);
    }

    public XMPBasicSchema createAndAddXMPBasicSchema() {
        XMPBasicSchema xmpB = new XMPBasicSchema(this);
        xmpB.setAboutAsSimple("");
        this.addSchema(xmpB);
        return xmpB;
    }

    public XMPBasicSchema getXMPBasicSchema() {
        return (XMPBasicSchema)this.getSchema(XMPBasicSchema.class);
    }

    public XMPMediaManagementSchema createAndAddXMPMediaManagementSchema() {
        XMPMediaManagementSchema xmpMM = new XMPMediaManagementSchema(this);
        xmpMM.setAboutAsSimple("");
        this.addSchema(xmpMM);
        return xmpMM;
    }

    public PhotoshopSchema createAndAddPhotoshopSchema() {
        PhotoshopSchema photoshop = new PhotoshopSchema(this);
        photoshop.setAboutAsSimple("");
        this.addSchema(photoshop);
        return photoshop;
    }

    public PhotoshopSchema getPhotoshopSchema() {
        return (PhotoshopSchema)this.getSchema(PhotoshopSchema.class);
    }

    public XMPMediaManagementSchema getXMPMediaManagementSchema() {
        return (XMPMediaManagementSchema)this.getSchema(XMPMediaManagementSchema.class);
    }

    public AdobePDFSchema createAndAddAdobePDFSchema() {
        AdobePDFSchema pdf = new AdobePDFSchema(this);
        pdf.setAboutAsSimple("");
        this.addSchema(pdf);
        return pdf;
    }

    public AdobePDFSchema getAdobePDFSchema() {
        return (AdobePDFSchema)this.getSchema(AdobePDFSchema.class);
    }

    public void addSchema(XMPSchema obj) {
        this.schemas.add(obj);
    }

    public void removeSchema(XMPSchema schema) {
        this.schemas.remove(schema);
    }

    public void clearSchemas() {
        this.schemas.clear();
    }
}


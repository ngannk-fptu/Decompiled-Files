/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ooxml.extractor;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.apache.poi.extractor.ExtractorFactory;
import org.apache.poi.extractor.ExtractorProvider;
import org.apache.poi.extractor.POITextExtractor;
import org.apache.poi.hssf.record.crypto.Biff8EncryptionKey;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.poifs.crypt.Decryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.poifs.filesystem.FileMagic;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.xdgf.extractor.XDGFVisioExtractor;
import org.apache.poi.xslf.extractor.XSLFExtractor;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.xssf.extractor.XSSFBEventBasedExcelExtractor;
import org.apache.poi.xssf.extractor.XSSFEventBasedExcelExtractor;
import org.apache.poi.xssf.extractor.XSSFExcelExtractor;
import org.apache.poi.xssf.usermodel.XSSFRelation;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.xmlbeans.XmlException;

public final class POIXMLExtractorFactory
implements ExtractorProvider {
    private static final String CORE_DOCUMENT_REL = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";
    private static final String VISIO_DOCUMENT_REL = "http://schemas.microsoft.com/visio/2010/relationships/document";
    private static final String STRICT_DOCUMENT_REL = "http://purl.oclc.org/ooxml/officeDocument/relationships/officeDocument";
    private static final List<XSLFRelation> SUPPORTED_XSLF_TYPES = Collections.unmodifiableList(Arrays.asList(XSLFRelation.MAIN, XSLFRelation.MACRO, XSLFRelation.MACRO_TEMPLATE, XSLFRelation.PRESENTATIONML, XSLFRelation.PRESENTATIONML_TEMPLATE, XSLFRelation.PRESENTATION_MACRO));

    @Override
    public boolean accepts(FileMagic fm) {
        return fm == FileMagic.OOXML;
    }

    public static boolean getThreadPrefersEventExtractors() {
        return ExtractorFactory.getThreadPrefersEventExtractors();
    }

    public static Boolean getAllThreadsPreferEventExtractors() {
        return ExtractorFactory.getAllThreadsPreferEventExtractors();
    }

    public static void setThreadPrefersEventExtractors(boolean preferEventExtractors) {
        ExtractorFactory.setThreadPrefersEventExtractors(preferEventExtractors);
    }

    public static void setAllThreadsPreferEventExtractors(Boolean preferEventExtractors) {
        ExtractorFactory.setAllThreadsPreferEventExtractors(preferEventExtractors);
    }

    public static boolean getPreferEventExtractor() {
        return ExtractorFactory.getPreferEventExtractor();
    }

    @Override
    public POITextExtractor create(File f, String password) throws IOException {
        if (FileMagic.valueOf(f) != FileMagic.OOXML) {
            return ExtractorFactory.createExtractor(f, password);
        }
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(f.toString(), PackageAccess.READ);
            POIXMLTextExtractor ex = this.create(pkg);
            if (ex == null) {
                pkg.revert();
            }
            return ex;
        }
        catch (InvalidFormatException ife) {
            throw new IOException(ife);
        }
        catch (IOException e) {
            if (pkg != null) {
                pkg.revert();
            }
            throw e;
        }
    }

    @Override
    public POITextExtractor create(InputStream inp, String password) throws IOException {
        InputStream is = FileMagic.prepareToCheckMagic(inp);
        if (FileMagic.valueOf(is) != FileMagic.OOXML) {
            return ExtractorFactory.createExtractor(is, password);
        }
        OPCPackage pkg = null;
        try {
            pkg = OPCPackage.open(is);
            POIXMLTextExtractor ex = this.create(pkg);
            if (ex == null) {
                pkg.revert();
            }
            return ex;
        }
        catch (InvalidFormatException e) {
            throw new IOException(e);
        }
        catch (IOException | RuntimeException e) {
            if (pkg != null) {
                pkg.revert();
            }
            throw e;
        }
    }

    public POIXMLTextExtractor create(OPCPackage pkg) throws IOException {
        try {
            PackageRelationshipCollection core = pkg.getRelationshipsByType(CORE_DOCUMENT_REL);
            if (core.isEmpty()) {
                core = pkg.getRelationshipsByType(STRICT_DOCUMENT_REL);
            }
            if (core.isEmpty() && (core = pkg.getRelationshipsByType(VISIO_DOCUMENT_REL)).size() == 1) {
                return new XDGFVisioExtractor(pkg);
            }
            if (core.size() != 1) {
                throw new IllegalArgumentException("Invalid OOXML Package received - expected 1 core document, found " + core.size());
            }
            PackagePart corePart = pkg.getPart(core.getRelationship(0));
            String contentType = corePart.getContentType();
            for (XSSFRelation xSSFRelation : XSSFExcelExtractor.SUPPORTED_TYPES) {
                if (!xSSFRelation.getContentType().equals(contentType)) continue;
                if (POIXMLExtractorFactory.getPreferEventExtractor()) {
                    return new XSSFEventBasedExcelExtractor(pkg);
                }
                return new XSSFExcelExtractor(pkg);
            }
            for (XWPFRelation xWPFRelation : XWPFWordExtractor.SUPPORTED_TYPES) {
                if (!xWPFRelation.getContentType().equals(contentType)) continue;
                return new XWPFWordExtractor(pkg);
            }
            for (XSLFRelation xSLFRelation : SUPPORTED_XSLF_TYPES) {
                if (!xSLFRelation.getContentType().equals(contentType)) continue;
                return new XSLFExtractor(new XMLSlideShow(pkg));
            }
            if (XSLFRelation.THEME_MANAGER.getContentType().equals(contentType)) {
                return new XSLFExtractor(new XMLSlideShow(pkg));
            }
            for (XSSFRelation xSSFRelation : XSSFBEventBasedExcelExtractor.SUPPORTED_TYPES) {
                if (!xSSFRelation.getContentType().equals(contentType)) continue;
                return new XSSFBEventBasedExcelExtractor(pkg);
            }
            return null;
        }
        catch (Error | RuntimeException | OpenXML4JException | XmlException e) {
            throw new IOException(e);
        }
    }

    public POITextExtractor create(POIFSFileSystem fs) throws IOException {
        return this.create(fs.getRoot(), Biff8EncryptionKey.getCurrentUserPassword());
    }

    /*
     * Loose catch block
     * Enabled aggressive exception aggregation
     */
    @Override
    public POITextExtractor create(DirectoryNode poifsDir, String password) throws IOException {
        if (poifsDir.hasEntry("Package")) {
            try (DocumentInputStream is = poifsDir.createDocumentInputStream("Package");){
                POITextExtractor pOITextExtractor = this.create(is, password);
                return pOITextExtractor;
            }
        }
        if (poifsDir.hasEntry("EncryptedPackage")) {
            EncryptionInfo ei = new EncryptionInfo(poifsDir);
            Decryptor dec = ei.getDecryptor();
            try {
                if (!dec.verifyPassword(password)) {
                    throw new IOException("Invalid password specified");
                }
                try {
                    try (InputStream is = dec.getDataStream(poifsDir);){
                        POITextExtractor pOITextExtractor = this.create(is, password);
                        return pOITextExtractor;
                    }
                    {
                        catch (Throwable throwable) {
                            throw throwable;
                        }
                    }
                }
                finally {
                    POIFSFileSystem fs = poifsDir.getFileSystem();
                    if (fs != null) {
                        fs.close();
                    }
                }
            }
            catch (IOException | RuntimeException e) {
                throw e;
            }
            catch (Exception e) {
                throw new IOException(e);
            }
        }
        throw new IOException("The OLE2 file neither contained a plain OOXML package node (\"Package\") nor an encrypted one (\"EncryptedPackage\").");
    }
}


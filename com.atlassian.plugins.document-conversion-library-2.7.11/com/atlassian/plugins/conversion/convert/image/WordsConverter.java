/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.aspose.words.Document
 *  com.aspose.words.FontInfoCollection
 *  com.aspose.words.FontSettings
 *  com.aspose.words.IResourceLoadingCallback
 *  com.aspose.words.IWarningCallback
 *  com.aspose.words.ImageSaveOptions
 *  com.aspose.words.LoadOptions
 *  com.aspose.words.PageInfo
 *  com.aspose.words.PageSet
 *  com.aspose.words.PdfSaveOptions
 *  com.aspose.words.SaveOptions
 *  com.aspose.words.WarningInfo
 *  org.apache.pdfbox.pdmodel.PDDocument
 *  org.apache.pdfbox.pdmodel.common.PDMetadata
 *  org.apache.xmpbox.XMPMetadata
 *  org.apache.xmpbox.schema.XMPBasicSchema
 *  org.apache.xmpbox.xml.XmpSerializer
 */
package com.atlassian.plugins.conversion.convert.image;

import com.aspose.words.Document;
import com.aspose.words.FontInfoCollection;
import com.aspose.words.FontSettings;
import com.aspose.words.IResourceLoadingCallback;
import com.aspose.words.IWarningCallback;
import com.aspose.words.ImageSaveOptions;
import com.aspose.words.LoadOptions;
import com.aspose.words.PageInfo;
import com.aspose.words.PageSet;
import com.aspose.words.PdfSaveOptions;
import com.aspose.words.SaveOptions;
import com.aspose.words.WarningInfo;
import com.atlassian.plugins.conversion.convert.ConversionException;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.bean.BeanFile;
import com.atlassian.plugins.conversion.convert.bean.BeanResult;
import com.atlassian.plugins.conversion.convert.html.word.RestrictiveResourceLoadingCallback;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.store.ConversionStore;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.common.PDMetadata;
import org.apache.xmpbox.XMPMetadata;
import org.apache.xmpbox.schema.XMPBasicSchema;
import org.apache.xmpbox.xml.XmpSerializer;

public class WordsConverter
extends AbstractConverter {
    private static final Pattern fontPattern;

    @Override
    public BeanResult convert(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, ConversionStore conversionStore, String fileName, Collection<Integer> pageNumbers) throws Exception {
        BeanResult result = new BeanResult();
        Document doc = this.createDocument(inStream);
        result.numPages = doc.getPageCount();
        switch (outFileFormat) {
            case PDF: {
                UUID uuid = UUID.randomUUID();
                OutputStream outputStream = conversionStore.createFile(uuid);
                doc.save(outputStream, 40);
                outputStream.close();
                result.result = Collections.singletonList(new BeanFile(uuid, -1, "", outFileFormat));
                break;
            }
            case JPG: 
            case PNG: {
                result.result = new ArrayList<BeanFile>();
                ImageSaveOptions saveOptions = new ImageSaveOptions(outFileFormat == FileFormat.PNG ? 101 : 104);
                for (int j = 0; j < doc.getPageCount(); ++j) {
                    saveOptions.setPageSet(new PageSet(j));
                    UUID uuid = UUID.randomUUID();
                    OutputStream outputStream = conversionStore.createFile(uuid);
                    doc.save(outputStream, (SaveOptions)saveOptions);
                    outputStream.close();
                    result.result.add(new BeanFile(uuid, j, "", outFileFormat));
                }
                break;
            }
            default: {
                throw new ConversionException("Unknown format");
            }
        }
        return result;
    }

    @Override
    public void convertDocDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream) throws Exception {
        if (outFileFormat == FileFormat.PDF) {
            ConvertibleByteArrayOutputStream bais = new ConvertibleByteArrayOutputStream();
            Document doc = this.createDocument(inStream);
            AsposeWarningHandler warningHandler = new AsposeWarningHandler();
            doc.setWarningCallback((IWarningCallback)warningHandler);
            FontInfoCollection fontInfos = doc.getFontInfos();
            PdfSaveOptions saveOptions = new PdfSaveOptions();
            saveOptions.setUseCoreFonts(true);
            saveOptions.setFontEmbeddingMode(1);
            doc.save((OutputStream)bais, (SaveOptions)saveOptions);
            XMPMetadata xmpMetadata = XMPMetadata.createXMPMetadata();
            XMPBasicSchema schemaBasic = xmpMetadata.createAndAddXMPBasicSchema();
            for (int j = 0; j < fontInfos.getCount(); ++j) {
                schemaBasic.addBagValueAsSimple("DT-Fonts", fontInfos.get(j).getName());
            }
            for (String font : warningHandler.getMissingFonts()) {
                schemaBasic.addBagValueAsSimple("DT-MissingFonts", font);
            }
            try (PDDocument pddoc = PDDocument.load((InputStream)bais.getInputStream());){
                PDMetadata metadataStream = new PDMetadata(pddoc);
                ByteArrayOutputStream xmpOutputStream = new ByteArrayOutputStream();
                new XmpSerializer().serialize(xmpMetadata, (OutputStream)xmpOutputStream, true);
                metadataStream.importXMPMetadata(xmpOutputStream.toByteArray());
                pddoc.getDocumentCatalog().setMetadata(metadataStream);
                pddoc.save(outStream);
            }
        } else {
            throw new ConversionException("Unknown format");
        }
    }

    @Override
    public void generateThumbnailDirect(FileFormat inFileFormat, FileFormat outFileFormat, InputStream inStream, OutputStream outStream, int pageNumber, double maxWidth, double maxHeight) throws ConversionException {
        try {
            int saveFormat;
            Document doc = this.createDocument(inStream);
            int numPages = doc.getPageCount();
            if (pageNumber < 1 || pageNumber > numPages) {
                throw new ConversionException("Invalid page number (" + pageNumber + " out of 1-" + numPages + ")");
            }
            switch (outFileFormat) {
                case PNG: {
                    saveFormat = 101;
                    break;
                }
                case JPG: {
                    saveFormat = 104;
                    break;
                }
                case TIF: {
                    saveFormat = 100;
                    break;
                }
                default: {
                    throw new ConversionException("Unsupported image format (" + (Object)((Object)outFileFormat) + ")");
                }
            }
            ImageSaveOptions saveOptions = new ImageSaveOptions(saveFormat);
            int newPageNumber = pageNumber - 1;
            saveOptions.setPageSet(new PageSet(newPageNumber));
            PageInfo pageInfo = doc.getPageInfo(newPageNumber);
            saveOptions.setScale((float)WordsConverter.findRatio(pageInfo.getWidthInPoints(), pageInfo.getHeightInPoints(), maxWidth, maxHeight));
            doc.save(outStream, (SaveOptions)saveOptions);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new ConversionException("Unknown error: " + e.getMessage());
        }
    }

    @Override
    public boolean handlesFileFormat(FileFormat inFileFormat) {
        return inFileFormat == FileFormat.TXT || inFileFormat == FileFormat.DOC || inFileFormat == FileFormat.DOCX || inFileFormat == FileFormat.HTML;
    }

    @Override
    public FileFormat getBestOutputFormat(FileFormat inFileFormat) {
        return this.handlesFileFormat(inFileFormat) ? FileFormat.PDF : null;
    }

    private Document createDocument(InputStream inputStream) throws Exception {
        LoadOptions restrictiveLoadOptions = new LoadOptions();
        restrictiveLoadOptions.setResourceLoadingCallback((IResourceLoadingCallback)new RestrictiveResourceLoadingCallback());
        return new Document(inputStream, restrictiveLoadOptions);
    }

    static {
        FontSettings.getDefaultInstance().getSubstitutionSettings().getTableSubstitution().addSubstitutes("Calibri", new String[]{"Carlito"});
        FontSettings.getDefaultInstance().getSubstitutionSettings().getTableSubstitution().addSubstitutes("Cambria", new String[]{"Caladea"});
        FontSettings.getDefaultInstance().getSubstitutionSettings().getTableSubstitution().addSubstitutes("Courier New", new String[]{"Cousine"});
        fontPattern = Pattern.compile(".+?'(.+?)'.*");
    }

    private static class AsposeWarningHandler
    implements IWarningCallback {
        private final Set<String> missingFonts = new HashSet<String>();

        private AsposeWarningHandler() {
        }

        public void warning(WarningInfo warningInfo) {
            if (warningInfo.getWarningType() == 131072) {
                Matcher matcher = fontPattern.matcher(warningInfo.getDescription());
                matcher.matches();
                this.missingFonts.add(matcher.group(1));
            }
        }

        public Set<String> getMissingFonts() {
            return this.missingFonts;
        }
    }

    private static class ConvertibleByteArrayOutputStream
    extends ByteArrayOutputStream {
        private ConvertibleByteArrayOutputStream() {
        }

        public InputStream getInputStream() {
            return new ByteArrayInputStream(this.buf);
        }

        public byte[] getBuf() {
            return this.buf;
        }
    }
}


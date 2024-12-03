/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nonnull
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.ExceptionConverter;
import com.lowagie.text.pdf.PRIndirectReference;
import com.lowagie.text.pdf.PRStream;
import com.lowagie.text.pdf.PRTokeniser;
import com.lowagie.text.pdf.PdfArray;
import com.lowagie.text.pdf.PdfContentParser;
import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfLiteral;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.parser.MarkedUpTextAssembler;
import com.lowagie.text.pdf.parser.PdfContentStreamHandler;
import com.lowagie.text.pdf.parser.TextAssembler;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import javax.annotation.Nonnull;

public class PdfTextExtractor {
    private final PdfReader reader;
    private final TextAssembler renderListener;

    public PdfTextExtractor(PdfReader reader) {
        this(reader, new MarkedUpTextAssembler(reader));
    }

    public PdfTextExtractor(PdfReader reader, boolean usePdfMarkupElements) {
        this(reader, new MarkedUpTextAssembler(reader, usePdfMarkupElements));
    }

    public PdfTextExtractor(PdfReader reader, TextAssembler renderListener) {
        this.reader = reader;
        this.renderListener = renderListener;
    }

    private byte[] getContentBytesForPage(int pageNum) throws IOException {
        try (RandomAccessFileOrArray ignored = this.reader.getSafeFile();){
            PdfDictionary pageDictionary = this.reader.getPageN(pageNum);
            PdfObject contentObject = pageDictionary.get(PdfName.CONTENTS);
            byte[] byArray = this.getContentBytesFromContentObject(contentObject);
            return byArray;
        }
    }

    private byte[] getContentBytesFromContentObject(PdfObject contentObject) throws IOException {
        byte[] result;
        switch (contentObject.type()) {
            case 10: {
                PRIndirectReference ref = (PRIndirectReference)contentObject;
                PdfObject directObject = PdfReader.getPdfObject(ref);
                result = this.getContentBytesFromContentObject(directObject);
                break;
            }
            case 7: {
                PRStream stream = (PRStream)PdfReader.getPdfObject(contentObject);
                result = PdfReader.getStreamBytes(stream);
                break;
            }
            case 5: {
                ByteArrayOutputStream allBytes = new ByteArrayOutputStream();
                PdfArray contentArray = (PdfArray)contentObject;
                for (PdfObject pdfObject : contentArray.getElements()) {
                    allBytes.write(this.getContentBytesFromContentObject(pdfObject));
                }
                result = allBytes.toByteArray();
                break;
            }
            default: {
                throw new IllegalStateException("Unable to handle Content of type " + contentObject.getClass());
            }
        }
        return result;
    }

    @Nonnull
    public String getTextFromPage(int page) throws IOException {
        return this.getTextFromPage(page, false);
    }

    @Nonnull
    public String getTextFromPage(int page, boolean useContainerMarkup) throws IOException {
        PdfDictionary pageDict = this.reader.getPageN(page);
        if (pageDict == null) {
            return "";
        }
        PdfDictionary resources = pageDict.getAsDict(PdfName.RESOURCES);
        this.renderListener.reset();
        this.renderListener.setPage(page);
        PdfContentStreamHandler handler = new PdfContentStreamHandler(this.renderListener);
        this.processContent(this.getContentBytesForPage(page), resources, handler);
        return handler.getResultantText();
    }

    public void processContent(byte[] contentBytes, PdfDictionary resources, PdfContentStreamHandler handler) {
        handler.pushContext("div class='t-extracted-page'");
        try {
            PdfContentParser ps = new PdfContentParser(new PRTokeniser(contentBytes));
            ArrayList<PdfObject> operands = new ArrayList<PdfObject>();
            while (ps.parse(operands).size() > 0) {
                PdfLiteral operator = (PdfLiteral)operands.get(operands.size() - 1);
                handler.invokeOperator(operator, operands, resources);
            }
        }
        catch (Exception e) {
            throw new ExceptionConverter(e);
        }
        handler.popContext();
    }
}


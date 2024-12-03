/*
 * Decompiled with CFR 0.152.
 */
package com.lowagie.text.pdf.parser;

import com.lowagie.text.pdf.PdfDictionary;
import com.lowagie.text.pdf.PdfName;
import com.lowagie.text.pdf.PdfObject;
import com.lowagie.text.pdf.PdfReader;
import com.lowagie.text.pdf.RandomAccessFileOrArray;
import com.lowagie.text.pdf.parser.MarkedUpTextAssembler;
import com.lowagie.text.pdf.parser.PdfTextExtractor;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;

public class PdfContentReaderTool {
    public static String getDictionaryDetail(PdfDictionary dic) {
        return PdfContentReaderTool.getDictionaryDetail(dic, 0);
    }

    public static String getDictionaryDetail(PdfDictionary dic, int depth) {
        StringBuilder builder = new StringBuilder();
        builder.append('(');
        ArrayList<PdfName> subDictionaries = new ArrayList<PdfName>();
        for (PdfName key : dic.getKeys()) {
            PdfObject pdfObject = dic.getDirectObject(key);
            if (pdfObject.isDictionary()) {
                subDictionaries.add(key);
            }
            builder.append(key);
            builder.append('=');
            builder.append(pdfObject);
            builder.append(", ");
        }
        builder.setLength(builder.length() - 2);
        builder.append(')');
        for (Object e : subDictionaries) {
            PdfName pdfSubDictionaryName = (PdfName)e;
            builder.append('\n');
            for (int i = 0; i < depth + 1; ++i) {
                builder.append('\t');
            }
            builder.append("Subdictionary ");
            builder.append(pdfSubDictionaryName);
            builder.append(" = ");
            builder.append(PdfContentReaderTool.getDictionaryDetail(dic.getAsDict(pdfSubDictionaryName), depth + 1));
        }
        return builder.toString();
    }

    public static void listContentStreamForPage(PdfReader reader, int pageNum, PrintWriter out) throws IOException {
        int ch;
        out.println("==============Page " + pageNum + "====================");
        out.println("- - - - - Dictionary - - - - - -");
        PdfDictionary pageDictionary = reader.getPageN(pageNum);
        out.println(PdfContentReaderTool.getDictionaryDetail(pageDictionary));
        out.println("- - - - - Content Stream - - - - - -");
        RandomAccessFileOrArray f = reader.getSafeFile();
        byte[] contentBytes = reader.getPageContent(pageNum, f);
        f.close();
        ByteArrayInputStream is = new ByteArrayInputStream(contentBytes);
        while ((ch = ((InputStream)is).read()) != -1) {
            out.print((char)ch);
        }
        out.println("- - - - - Text Extraction - - - - - -");
        PdfTextExtractor extractor = new PdfTextExtractor(reader, new MarkedUpTextAssembler(reader));
        String extractedText = extractor.getTextFromPage(pageNum);
        if (extractedText.length() != 0) {
            out.println(extractedText);
        } else {
            out.println("No text found on page " + pageNum);
        }
        out.println();
    }

    public static void listContentStream(File pdfFile, PrintWriter out) throws IOException {
        PdfReader reader = new PdfReader(pdfFile.getCanonicalPath());
        int maxPageNum = reader.getNumberOfPages();
        for (int pageNum = 1; pageNum <= maxPageNum; ++pageNum) {
            PdfContentReaderTool.listContentStreamForPage(reader, pageNum, out);
        }
    }

    public static void listContentStream(File pdfFile, int pageNum, PrintWriter out) throws IOException {
        PdfReader reader = new PdfReader(pdfFile.getCanonicalPath());
        PdfContentReaderTool.listContentStreamForPage(reader, pageNum, out);
    }

    public static void main(String[] args) {
        try {
            if (args.length < 1 || args.length > 3) {
                System.out.println("Usage:  PdfContentReaderTool <pdf file> [<output file>|stdout] [<page num>]");
                return;
            }
            PrintWriter writer = new PrintWriter(System.out);
            if (args.length >= 2 && args[1].compareToIgnoreCase("stdout") != 0) {
                System.out.println("Writing PDF content to " + args[1]);
                writer = new PrintWriter(new FileOutputStream(new File(args[1])));
            }
            int pageNum = -1;
            if (args.length >= 3) {
                pageNum = Integer.parseInt(args[2]);
            }
            if (pageNum == -1) {
                PdfContentReaderTool.listContentStream(new File(args[0]), writer);
            } else {
                PdfContentReaderTool.listContentStream(new File(args[0]), pageNum, writer);
            }
            writer.flush();
            if (args.length >= 2) {
                writer.close();
                System.out.println("Finished writing content to " + args[1]);
            }
        }
        catch (Exception e) {
            e.printStackTrace(System.err);
        }
    }
}


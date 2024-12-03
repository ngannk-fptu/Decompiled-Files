/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.data.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jfree.data.category.CategoryDataset;
import org.jfree.data.general.PieDataset;
import org.jfree.data.xml.CategoryDatasetHandler;
import org.jfree.data.xml.PieDatasetHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class DatasetReader {
    public static PieDataset readPieDatasetFromXML(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        return DatasetReader.readPieDatasetFromXML(in);
    }

    public static PieDataset readPieDatasetFromXML(InputStream in) throws IOException {
        PieDataset result = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            PieDatasetHandler handler = new PieDatasetHandler();
            parser.parse(in, (DefaultHandler)handler);
            result = handler.getDataset();
        }
        catch (SAXException e) {
            System.out.println(e.getMessage());
        }
        catch (ParserConfigurationException e2) {
            System.out.println(e2.getMessage());
        }
        return result;
    }

    public static CategoryDataset readCategoryDatasetFromXML(File file) throws IOException {
        FileInputStream in = new FileInputStream(file);
        return DatasetReader.readCategoryDatasetFromXML(in);
    }

    public static CategoryDataset readCategoryDatasetFromXML(InputStream in) throws IOException {
        CategoryDataset result = null;
        SAXParserFactory factory = SAXParserFactory.newInstance();
        try {
            SAXParser parser = factory.newSAXParser();
            CategoryDatasetHandler handler = new CategoryDatasetHandler();
            parser.parse(in, (DefaultHandler)handler);
            result = handler.getDataset();
        }
        catch (SAXException e) {
            System.out.println(e.getMessage());
        }
        catch (ParserConfigurationException e2) {
            System.out.println(e2.getMessage());
        }
        return result;
    }
}


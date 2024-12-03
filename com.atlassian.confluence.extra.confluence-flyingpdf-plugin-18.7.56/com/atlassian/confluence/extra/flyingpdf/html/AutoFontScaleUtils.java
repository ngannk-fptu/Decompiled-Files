/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.extra.flyingpdf.html;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jsoup.Jsoup;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xhtmlrenderer.css.constants.CSSName;

public class AutoFontScaleUtils {
    private static final double FULL_SIZE = 1.0;
    private static final int MIN_COLUMN_TO_APPLY_CHARACTER_COUNTING = 5;
    private static final int EMPTY = 0;
    private static final String ELEMENT_TABLE = "table";
    private static final String ELEMENT_TR = "tr";
    private static final String ELEMENT_TD = "td";
    private static final String ELEMENT_TH = "th";
    private static final String ELEMENT_IMG = "img";
    private static final String ATTRIBUTE_CLASS = "class";
    private static final String ATTRIBUTE_STYLE = "style";
    private static final String CSS_CLASS_NAME_CONFLUENCE_TABLE = "confluenceTable";
    private static final Logger LOG = LoggerFactory.getLogger(AutoFontScaleUtils.class);

    public static void applyTableScalingLogic(Document xhtmlDocument) {
        if (xhtmlDocument == null) {
            return;
        }
        long start = System.currentTimeMillis();
        NodeList tables = xhtmlDocument.getElementsByTagName(ELEMENT_TABLE);
        for (int i = 0; i < tables.getLength(); ++i) {
            Element tableElement;
            Node table = tables.item(i);
            if (table.getNodeType() != 1 || !(tableElement = (Element)table).getAttribute(ATTRIBUTE_CLASS).contains(CSS_CLASS_NAME_CONFLUENCE_TABLE)) continue;
            AutoFontScaleUtils.scaleTableBaseOnNumberOfColumnsAndCharacter(tableElement);
        }
        LOG.debug("Total scaling time : " + (System.currentTimeMillis() - start));
    }

    public static void scaleTableBaseOnNumberOfColumnsAndCharacter(Element tableElement) {
        if (tableElement == null) {
            return;
        }
        long time = System.currentTimeMillis();
        int numOfColumn = AutoFontScaleUtils.detectNumberOfColumnsFromElement(tableElement);
        double colScaleFontSize = 1.0;
        if (numOfColumn > 0) {
            colScaleFontSize = FontRangeHelper.getColumnCountInstance().getFontSize(numOfColumn).doubleValue();
        }
        LOG.debug("column count processing time : " + (System.currentTimeMillis() - time));
        time = System.currentTimeMillis();
        int noOfTRCharacters = AutoFontScaleUtils.detectCrossTableMaxCharacters(tableElement);
        double chaScaleFontSize = 1.0;
        if (numOfColumn >= 5) {
            chaScaleFontSize = FontRangeHelper.getCharacterCountInstance().getFontSize(noOfTRCharacters).doubleValue();
        }
        LOG.debug("character count processing time : " + (System.currentTimeMillis() - time));
        double finalRatio = Math.min(colScaleFontSize, chaScaleFontSize);
        AutoFontScaleUtils.setStyleToElement(tableElement, CSSName.FONT_SIZE.toString(), String.valueOf(finalRatio) + "em");
        AutoFontScaleUtils.adjustTablePadding(tableElement, finalRatio);
        LOG.debug("noOfTRCharacters " + noOfTRCharacters);
        LOG.debug("numOfColumn " + numOfColumn);
        LOG.debug("colScaleFontSize " + colScaleFontSize);
        LOG.debug("chaScaleFontSize " + chaScaleFontSize);
        LOG.debug("final " + Math.min(colScaleFontSize, chaScaleFontSize));
    }

    public static void scaleTableBaseOnNumberOfColumns(Element tableElement) {
        if (tableElement == null) {
            return;
        }
        int numOfColumn = AutoFontScaleUtils.detectNumberOfColumnsFromElement(tableElement);
        if (numOfColumn == 0) {
            return;
        }
        double scaleRatio = FontRangeHelper.getColumnCountInstance().getFontSize(numOfColumn).doubleValue();
        AutoFontScaleUtils.setStyleToElement(tableElement, CSSName.FONT_SIZE.toString(), String.valueOf(scaleRatio) + "em");
    }

    public static void scaleTableBaseOnNumberOfCharacters(Element tableElement) {
        if (tableElement == null) {
            return;
        }
        int noOfTRCharacters = AutoFontScaleUtils.detectCrossTableMaxCharacters(tableElement);
        double scaleRatio = FontRangeHelper.getCharacterCountInstance().getFontSize(noOfTRCharacters).doubleValue();
        AutoFontScaleUtils.setStyleToElement(tableElement, CSSName.FONT_SIZE.toString(), String.valueOf(scaleRatio) + "em");
    }

    public static String getStringFromDocument(Node doc) {
        try {
            DOMSource domSource = new DOMSource(doc);
            StringWriter writer = new StringWriter();
            StreamResult result = new StreamResult(writer);
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        }
        catch (TransformerException ex) {
            LOG.debug("Failed to get string from DOM", (Throwable)ex);
            return "";
        }
    }

    private static void adjustTablePadding(Element tableElement, double finalRatio) {
        Collection<Element> tds = AutoFontScaleUtils.findElementsByTagName(tableElement, ELEMENT_TD);
        for (Element tdElement : tds) {
            AutoFontScaleUtils.setStyleToElement(tdElement, CSSName.PADDING_SHORTHAND.toString(), finalRatio / 2.0 + "em");
        }
    }

    private static Collection<Element> findElementsByTagName(Element parentElement, String tagName) {
        NodeList nodeList = parentElement.getElementsByTagName(tagName);
        if (nodeList.getLength() == 0) {
            return Collections.emptySet();
        }
        HashSet<Element> elements = new HashSet<Element>(nodeList.getLength());
        for (int i = 0; i < nodeList.getLength(); ++i) {
            Node node = nodeList.item(i);
            if (node.getNodeType() != 1) continue;
            elements.add((Element)node);
        }
        return elements;
    }

    private static void setStyleToElement(Element e, String styleName, String styleValue) {
        String currentStyle = e.getAttribute(ATTRIBUTE_STYLE);
        e.setAttribute(ATTRIBUTE_STYLE, currentStyle + "; " + styleName + ":" + styleValue);
    }

    private static int detectCrossTableMaxCharacters(Element tableElement) {
        NodeList rows = tableElement.getElementsByTagName(ELEMENT_TR);
        if (rows.getLength() == 0) {
            LOG.info("Processing table if empty row");
            return 0;
        }
        Element firstRow = (Element)rows.item(0);
        NodeList columns = firstRow.getElementsByTagName(ELEMENT_TH);
        if (columns.getLength() == 0) {
            columns = firstRow.getElementsByTagName(ELEMENT_TD);
        }
        Integer[] largestCellByColumns = AutoFontScaleUtils.calculateLargestCellByColumns(rows, columns);
        int countAggregatedLargestCell = 0;
        for (int i = 0; i < largestCellByColumns.length; ++i) {
            countAggregatedLargestCell += largestCellByColumns[i].intValue();
        }
        return countAggregatedLargestCell;
    }

    private static Integer[] calculateLargestCellByColumns(NodeList rows, NodeList columns) {
        Integer[] largestCellByColumns = new Integer[columns.getLength()];
        for (int i = 0; i < largestCellByColumns.length; ++i) {
            largestCellByColumns[i] = 0;
        }
        for (int tr = 0; tr < rows.getLength(); ++tr) {
            Element trElement = (Element)rows.item(tr);
            NodeList tds = trElement.getElementsByTagName(ELEMENT_TD);
            if (tds.getLength() == 0) {
                tds = trElement.getElementsByTagName(ELEMENT_TH);
            }
            for (int td = 0; td < columns.getLength(); ++td) {
                Element tdElement = (Element)tds.item(td);
                String content = AutoFontScaleUtils.getStringFromDocument(tdElement);
                if ((content = Jsoup.parseBodyFragment(content).text()).length() <= largestCellByColumns[td]) continue;
                largestCellByColumns[td] = content.length();
                LOG.debug(content.length() + " " + content + "\n");
            }
        }
        return largestCellByColumns;
    }

    private static int detectNumberOfColumnsFromElement(Element tableElement) {
        NodeList trs = tableElement.getElementsByTagName(ELEMENT_TR);
        if (trs.getLength() == 0) {
            return 0;
        }
        int noOfColumns = 0;
        for (int i = 0; i < trs.getLength(); ++i) {
            int columnsPerRow = ((Element)trs.item(i)).getChildNodes().getLength();
            if (columnsPerRow <= noOfColumns) continue;
            noOfColumns = columnsPerRow;
        }
        return noOfColumns;
    }

    private static class FontRangeHelper<E extends Number> {
        private Map<Integer[], E> internalRangeMap = new HashMap<Integer[], E>();
        private static FontRangeHelper<Double> columnCountInstance = null;
        private static FontRangeHelper<Double> characterCountInstance = null;

        private static void initialize() {
            FontRangeHelper.getColumnCountInstance();
            FontRangeHelper.getCharacterCountInstance();
        }

        static FontRangeHelper<Double> getColumnCountInstance() {
            if (null == columnCountInstance) {
                columnCountInstance = new FontRangeHelper();
                columnCountInstance.setRange(1, 3, 1.0).setRange(4, 7, 0.9).setRange(8, 12, 0.8).setRange(13, Integer.MAX_VALUE, 0.7);
            }
            return columnCountInstance;
        }

        static FontRangeHelper<Double> getCharacterCountInstance() {
            if (null == characterCountInstance) {
                characterCountInstance = new FontRangeHelper();
                characterCountInstance.setRange(1, 600, 1.0).setRange(601, 1000, 0.9).setRange(1001, 2000, 0.8).setRange(2001, Integer.MAX_VALUE, 0.7);
            }
            return characterCountInstance;
        }

        private FontRangeHelper() {
        }

        private FontRangeHelper<E> setRange(int start, int end, E d) {
            Integer[] range = new Integer[]{start, end};
            this.internalRangeMap.put(range, d);
            return this;
        }

        Number getFontSize(int numOfColumn) {
            for (Map.Entry<Integer[], E> entry : this.internalRangeMap.entrySet()) {
                Integer[] range = entry.getKey();
                if (numOfColumn < range[0] || numOfColumn > range[1]) continue;
                return (Number)entry.getValue();
            }
            return 1.0;
        }

        static {
            FontRangeHelper.initialize();
        }
    }
}


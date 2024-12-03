/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.extractor;

import java.io.IOException;
import java.io.StringReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Unbox;
import org.apache.poi.ooxml.util.DocumentHelper;
import org.apache.poi.ooxml.util.XPathHelper;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFMap;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFTable;
import org.apache.poi.xssf.usermodel.XSSFTableColumn;
import org.apache.poi.xssf.usermodel.helpers.XSSFSingleXmlCell;
import org.apache.poi.xssf.usermodel.helpers.XSSFXmlColumnPr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XSSFImportFromXML {
    private final XSSFMap _map;
    private static final Logger LOG = LogManager.getLogger(XSSFImportFromXML.class);

    public XSSFImportFromXML(XSSFMap map) {
        this._map = map;
    }

    public void importFromXML(String xmlInputString) throws SAXException, XPathExpressionException, IOException {
        DocumentBuilder builder = DocumentHelper.newDocumentBuilder();
        Document doc = builder.parse(new InputSource(new StringReader(xmlInputString.trim())));
        List<XSSFSingleXmlCell> singleXmlCells = this._map.getRelatedSingleXMLCell();
        List<XSSFTable> tables = this._map.getRelatedTables();
        XPath xpath = XPathHelper.getFactory().newXPath();
        xpath.setNamespaceContext(new DefaultNamespaceContext(doc));
        for (XSSFSingleXmlCell singleXmlCell : singleXmlCells) {
            String xmlDataType = singleXmlCell.getXmlDataType();
            String xpathString = singleXmlCell.getXpath();
            Node result = (Node)xpath.evaluate(xpathString, doc, XPathConstants.NODE);
            if (result == null) continue;
            String textContent = result.getTextContent();
            LOG.atDebug().log("Extracting with xpath {} : value is '{}'", (Object)xpathString, (Object)textContent);
            XSSFCell cell = singleXmlCell.getReferencedCell();
            LOG.atDebug().log("Setting '{}' to cell {}-{} in sheet {}", (Object)textContent, (Object)Unbox.box(cell.getColumnIndex()), (Object)Unbox.box(cell.getRowIndex()), (Object)cell.getSheet().getSheetName());
            this.setCellValue(textContent, cell, xmlDataType);
        }
        for (XSSFTable table : tables) {
            String commonXPath = table.getCommonXpath();
            NodeList result = (NodeList)xpath.evaluate(commonXPath, doc, XPathConstants.NODESET);
            int rowOffset = table.getStartCellReference().getRow() + table.getHeaderRowCount();
            short columnOffset = table.getStartCellReference().getCol();
            table.setDataRowCount(result.getLength());
            for (int i = 0; i < result.getLength(); ++i) {
                Node singleNode = result.item(i).cloneNode(true);
                for (XSSFTableColumn tableColumn : table.getColumns()) {
                    XSSFCell cell;
                    XSSFXmlColumnPr xmlColumnPr = tableColumn.getXmlColumnPr();
                    if (xmlColumnPr == null) continue;
                    int rowId = rowOffset + i;
                    int columnId = columnOffset + tableColumn.getColumnIndex();
                    String localXPath = xmlColumnPr.getLocalXPath();
                    localXPath = localXPath.substring(localXPath.indexOf(47, 1) + 1);
                    String value = (String)xpath.evaluate(localXPath, singleNode, XPathConstants.STRING);
                    LOG.atDebug().log("Extracting with xpath {} : value is '{}'", (Object)localXPath, (Object)value);
                    XSSFRow row = table.getXSSFSheet().getRow(rowId);
                    if (row == null) {
                        row = table.getXSSFSheet().createRow(rowId);
                    }
                    if ((cell = row.getCell(columnId)) == null) {
                        cell = row.createCell(columnId);
                    }
                    LOG.atDebug().log("Setting '{}' to cell {}-{} in sheet {}", (Object)value, (Object)Unbox.box(cell.getColumnIndex()), (Object)Unbox.box(cell.getRowIndex()), (Object)table.getXSSFSheet().getSheetName());
                    this.setCellValue(value, cell, xmlColumnPr.getXmlDataType());
                }
            }
        }
    }

    private void setCellValue(String value, XSSFCell cell, String xmlDataType) {
        block10: {
            DataType type = DataType.getDataType(xmlDataType);
            try {
                if (value.isEmpty() || type == null) {
                    cell.setCellValue((String)null);
                    break block10;
                }
                switch (type) {
                    case BOOLEAN: {
                        cell.setCellValue(Boolean.parseBoolean(value));
                        break;
                    }
                    case DOUBLE: {
                        cell.setCellValue(Double.parseDouble(value));
                        break;
                    }
                    case INTEGER: {
                        cell.setCellValue(Integer.parseInt(value));
                        break;
                    }
                    case DATE: {
                        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", LocaleUtil.getUserLocale());
                        Date date = sdf.parse(value);
                        cell.setCellValue(date);
                        if (!DateUtil.isValidExcelDate(cell.getNumericCellValue())) {
                            cell.setCellValue(value);
                        }
                        break;
                    }
                    default: {
                        cell.setCellValue(value.trim());
                    }
                }
            }
            catch (IllegalArgumentException | ParseException e) {
                throw new IllegalArgumentException(String.format(LocaleUtil.getUserLocale(), "Unable to format value '%s' as %s for cell %s", new Object[]{value, type, new CellReference(cell).formatAsString()}));
            }
        }
    }

    private static final class DefaultNamespaceContext
    implements NamespaceContext {
        private final Element _docElem;

        public DefaultNamespaceContext(Document doc) {
            this._docElem = doc.getDocumentElement();
        }

        @Override
        public String getNamespaceURI(String prefix) {
            return this.getNamespaceForPrefix(prefix);
        }

        private String getNamespaceForPrefix(String prefix) {
            if (prefix.equals("xml")) {
                return "http://www.w3.org/XML/1998/namespace";
            }
            Node parent = this._docElem;
            while (parent != null) {
                short type = parent.getNodeType();
                if (type == 1) {
                    if (parent.getNodeName().startsWith(prefix + ":")) {
                        return parent.getNamespaceURI();
                    }
                    NamedNodeMap nnm = parent.getAttributes();
                    for (int i = 0; i < nnm.getLength(); ++i) {
                        String p;
                        Node attr = nnm.item(i);
                        String aname = attr.getNodeName();
                        boolean isPrefix = aname.startsWith("xmlns:");
                        if (!isPrefix && !aname.equals("xmlns")) continue;
                        int index = aname.indexOf(58);
                        String string = p = isPrefix ? aname.substring(index + 1) : "";
                        if (!p.equals(prefix)) continue;
                        return attr.getNodeValue();
                    }
                } else {
                    if (type != 5) break;
                    continue;
                }
                parent = parent.getParentNode();
            }
            return null;
        }

        @Override
        public Iterator<String> getPrefixes(String val) {
            return null;
        }

        @Override
        public String getPrefix(String uri) {
            return null;
        }
    }

    private static enum DataType {
        BOOLEAN("boolean"),
        DOUBLE("double"),
        INTEGER("int", "unsignedInt", "integer"),
        STRING("string"),
        DATE("date");

        private Set<String> xmlDataTypes;

        private DataType(String ... xmlDataTypes) {
            this.xmlDataTypes = new HashSet<String>(Arrays.asList(xmlDataTypes));
        }

        public static DataType getDataType(String xmlDataType) {
            for (DataType dataType : DataType.values()) {
                if (!dataType.xmlDataTypes.contains(xmlDataType)) continue;
                return dataType;
            }
            return null;
        }
    }
}


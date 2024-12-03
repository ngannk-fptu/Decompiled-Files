/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.apache.poi.ooxml.POIXMLFactory;
import org.apache.poi.ooxml.POIXMLRelation;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xssf.usermodel.XSSFGraphicFrame;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPageMargins;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPrintSettings;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStrRef;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTx;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;

public final class XSSFChart
extends XDDFChart {
    private XSSFGraphicFrame frame;

    protected XSSFChart() {
        this.createChart();
    }

    protected XSSFChart(PackagePart part) throws IOException, XmlException {
        super(part);
    }

    @Override
    protected POIXMLRelation getChartRelation() {
        return null;
    }

    @Override
    protected POIXMLRelation getChartWorkbookRelation() {
        return null;
    }

    @Override
    protected POIXMLFactory getChartFactory() {
        return null;
    }

    private void createChart() {
        CTPlotArea plotArea = this.getCTPlotArea();
        plotArea.addNewLayout();
        this.getCTChart().addNewPlotVisOnly().setVal(true);
        CTPrintSettings printSettings = this.chartSpace.addNewPrintSettings();
        printSettings.addNewHeaderFooter();
        CTPageMargins pageMargins = printSettings.addNewPageMargins();
        pageMargins.setB(0.75);
        pageMargins.setL(0.7);
        pageMargins.setR(0.7);
        pageMargins.setT(0.75);
        pageMargins.setHeader(0.3);
        pageMargins.setFooter(0.3);
        printSettings.addNewPageSetup();
    }

    @Override
    protected void commit() throws IOException {
        XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTChartSpace.type.getName().getNamespaceURI(), "chartSpace", "c"));
        PackagePart part = this.getPackagePart();
        try (OutputStream out = part.getOutputStream();){
            this.chartSpace.save(out, xmlOptions);
        }
    }

    public XSSFGraphicFrame getGraphicFrame() {
        return this.frame;
    }

    protected void setGraphicFrame(XSSFGraphicFrame frame) {
        this.frame = frame;
    }

    public XSSFRichTextString getTitleText() {
        XmlObject[] t;
        if (!this.getCTChart().isSetTitle()) {
            return null;
        }
        CTTitle title = this.getCTChart().getTitle();
        StringBuilder text = new StringBuilder(64);
        for (XmlObject element : t = title.selectPath("declare namespace a='http://schemas.openxmlformats.org/drawingml/2006/main' .//a:t")) {
            NodeList kids = element.getDomNode().getChildNodes();
            int count = kids.getLength();
            for (int n = 0; n < count; ++n) {
                Node kid = kids.item(n);
                if (!(kid instanceof Text)) continue;
                text.append(kid.getNodeValue());
            }
        }
        return new XSSFRichTextString(text.toString());
    }

    public String getTitleFormula() {
        if (!this.getCTChart().isSetTitle()) {
            return null;
        }
        CTTitle title = this.getCTChart().getTitle();
        if (!title.isSetTx()) {
            return null;
        }
        CTTx tx = title.getTx();
        if (!tx.isSetStrRef()) {
            return null;
        }
        return tx.getStrRef().getF();
    }

    public void setTitleFormula(String formula) {
        CTTitle ctTitle = this.getCTChart().isSetTitle() ? this.getCTChart().getTitle() : this.getCTChart().addNewTitle();
        CTTx tx = ctTitle.isSetTx() ? ctTitle.getTx() : ctTitle.addNewTx();
        if (tx.isSetRich()) {
            tx.unsetRich();
        }
        CTStrRef strRef = tx.isSetStrRef() ? tx.getStrRef() : tx.addNewStrRef();
        strRef.setF(formula);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.usermodel;

import java.util.HashMap;
import java.util.Map;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFColor;
import org.apache.poi.xssf.usermodel.XSSFDataFormat;
import org.apache.poi.xssf.usermodel.XSSFFormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFHyperlink;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTColor;

public class XSSFCreationHelper
implements CreationHelper {
    private final XSSFWorkbook workbook;
    private final Map<String, Workbook> referencedWorkbooks;

    @Internal
    public XSSFCreationHelper(XSSFWorkbook wb) {
        this.workbook = wb;
        this.referencedWorkbooks = new HashMap<String, Workbook>();
    }

    @Override
    public XSSFRichTextString createRichTextString(String text) {
        XSSFRichTextString rt = new XSSFRichTextString(text);
        rt.setStylesTableReference(this.workbook.getStylesSource());
        return rt;
    }

    @Override
    public XSSFDataFormat createDataFormat() {
        return this.workbook.createDataFormat();
    }

    @Override
    public XSSFColor createExtendedColor() {
        return XSSFColor.from(CTColor.Factory.newInstance(), this.workbook.getStylesSource().getIndexedColors());
    }

    @Override
    public XSSFHyperlink createHyperlink(HyperlinkType type) {
        return new XSSFHyperlink(type);
    }

    @Override
    public XSSFFormulaEvaluator createFormulaEvaluator() {
        XSSFFormulaEvaluator evaluator = new XSSFFormulaEvaluator(this.workbook);
        HashMap<String, FormulaEvaluator> evaluatorMap = new HashMap<String, FormulaEvaluator>();
        evaluatorMap.put("", evaluator);
        this.referencedWorkbooks.forEach((name, otherWorkbook) -> evaluatorMap.put((String)name, otherWorkbook.getCreationHelper().createFormulaEvaluator()));
        evaluator.setupReferencedWorkbooks(evaluatorMap);
        return evaluator;
    }

    @Override
    public XSSFClientAnchor createClientAnchor() {
        return new XSSFClientAnchor();
    }

    @Override
    public AreaReference createAreaReference(String reference) {
        return new AreaReference(reference, this.workbook.getSpreadsheetVersion());
    }

    @Override
    public AreaReference createAreaReference(CellReference topLeft, CellReference bottomRight) {
        return new AreaReference(topLeft, bottomRight, this.workbook.getSpreadsheetVersion());
    }

    protected Map<String, Workbook> getReferencedWorkbooks() {
        return this.referencedWorkbooks;
    }

    protected void addExternalWorkbook(String name, Workbook workbook) {
        this.referencedWorkbooks.put(name, workbook);
    }
}


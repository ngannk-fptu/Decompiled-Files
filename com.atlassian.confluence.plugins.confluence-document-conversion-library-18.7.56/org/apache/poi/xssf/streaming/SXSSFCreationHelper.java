/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.xssf.streaming;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.common.usermodel.HyperlinkType;
import org.apache.poi.ss.usermodel.ClientAnchor;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.DataFormat;
import org.apache.poi.ss.usermodel.ExtendedColor;
import org.apache.poi.ss.usermodel.Hyperlink;
import org.apache.poi.ss.util.AreaReference;
import org.apache.poi.ss.util.CellReference;
import org.apache.poi.util.Internal;
import org.apache.poi.xssf.streaming.SXSSFFormulaEvaluator;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFCreationHelper;
import org.apache.poi.xssf.usermodel.XSSFRichTextString;

public class SXSSFCreationHelper
implements CreationHelper {
    private static final Logger LOG = LogManager.getLogger(SXSSFCreationHelper.class);
    private final SXSSFWorkbook wb;
    private final XSSFCreationHelper helper;

    @Internal
    public SXSSFCreationHelper(SXSSFWorkbook workbook) {
        this.helper = new XSSFCreationHelper(workbook.getXSSFWorkbook());
        this.wb = workbook;
    }

    @Override
    public XSSFRichTextString createRichTextString(String text) {
        return new XSSFRichTextString(text);
    }

    @Override
    public SXSSFFormulaEvaluator createFormulaEvaluator() {
        return new SXSSFFormulaEvaluator(this.wb);
    }

    @Override
    public DataFormat createDataFormat() {
        return this.helper.createDataFormat();
    }

    @Override
    public Hyperlink createHyperlink(HyperlinkType type) {
        return this.helper.createHyperlink(type);
    }

    @Override
    public ExtendedColor createExtendedColor() {
        return this.helper.createExtendedColor();
    }

    @Override
    public ClientAnchor createClientAnchor() {
        return this.helper.createClientAnchor();
    }

    @Override
    public AreaReference createAreaReference(String reference) {
        return new AreaReference(reference, this.wb.getSpreadsheetVersion());
    }

    @Override
    public AreaReference createAreaReference(CellReference topLeft, CellReference bottomRight) {
        return new AreaReference(topLeft, bottomRight, this.wb.getSpreadsheetVersion());
    }
}


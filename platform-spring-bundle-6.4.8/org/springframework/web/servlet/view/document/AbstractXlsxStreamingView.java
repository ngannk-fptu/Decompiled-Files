/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.poi.ss.usermodel.Workbook
 *  org.apache.poi.xssf.streaming.SXSSFWorkbook
 */
package org.springframework.web.servlet.view.document;

import java.io.IOException;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.springframework.web.servlet.view.document.AbstractXlsxView;

public abstract class AbstractXlsxStreamingView
extends AbstractXlsxView {
    protected SXSSFWorkbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new SXSSFWorkbook();
    }

    @Override
    protected void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
        super.renderWorkbook(workbook, response);
        ((SXSSFWorkbook)workbook).dispose();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletOutputStream
 *  javax.servlet.http.HttpServletRequest
 *  javax.servlet.http.HttpServletResponse
 *  org.apache.poi.hssf.usermodel.HSSFWorkbook
 *  org.apache.poi.ss.usermodel.Workbook
 */
package org.springframework.web.servlet.view.document;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Map;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.web.servlet.view.AbstractView;

public abstract class AbstractXlsView
extends AbstractView {
    public AbstractXlsView() {
        this.setContentType("application/vnd.ms-excel");
    }

    @Override
    protected boolean generatesDownloadContent() {
        return true;
    }

    @Override
    protected final void renderMergedOutputModel(Map<String, Object> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
        Workbook workbook = this.createWorkbook(model, request);
        this.buildExcelDocument(model, workbook, request, response);
        response.setContentType(this.getContentType());
        this.renderWorkbook(workbook, response);
    }

    protected Workbook createWorkbook(Map<String, Object> model, HttpServletRequest request) {
        return new HSSFWorkbook();
    }

    protected void renderWorkbook(Workbook workbook, HttpServletResponse response) throws IOException {
        ServletOutputStream out = response.getOutputStream();
        workbook.write((OutputStream)out);
        workbook.close();
    }

    protected abstract void buildExcelDocument(Map<String, Object> var1, Workbook var2, HttpServletRequest var3, HttpServletResponse var4) throws Exception;
}


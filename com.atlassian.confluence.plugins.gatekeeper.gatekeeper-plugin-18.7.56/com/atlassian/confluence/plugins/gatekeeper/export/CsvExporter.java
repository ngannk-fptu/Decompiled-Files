/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.csv.CSVFormat
 *  org.apache.commons.csv.CSVPrinter
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.gatekeeper.export;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.export.AbstractExporter;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExportSettings;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.PreEvaluationResult;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.StringUtils;

public class CsvExporter
extends AbstractExporter {
    private CSVFormat csvFormat;
    private CSVPrinter printer;

    public CsvExporter(ExportSettings exportSettings) {
        super(exportSettings);
        String delimiter = exportSettings.getCsvDelimiter();
        if ("custom".equals(delimiter)) {
            delimiter = exportSettings.getCsvCustomDelimiter();
        }
        if ("tab".equals(delimiter)) {
            delimiter = "\t";
        }
        char delimiterChar = StringUtils.isEmpty((CharSequence)delimiter) ? (char)',' : (char)delimiter.charAt(0);
        this.csvFormat = CSVFormat.DEFAULT.withDelimiter(delimiterChar);
    }

    @Override
    public void export(Evaluator evaluator, PreEvaluationResult preEvaluationResult, OutputStream outputStream) throws Exception {
        outputStream.write(239);
        outputStream.write(187);
        outputStream.write(191);
        OutputStreamWriter out = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8);
        this.printer = new CSVPrinter((Appendable)out, this.csvFormat);
        this.process(evaluator, preEvaluationResult);
        this.printer.close();
        outputStream.close();
    }

    @Override
    protected void afterRow() throws Exception {
        this.printer.println();
    }

    @Override
    protected void writeCell(Object value) throws Exception {
        this.printer.print(value);
    }
}


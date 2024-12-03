/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.supercsv.io.CsvBeanWriter
 *  org.supercsv.io.CsvListWriter
 *  org.supercsv.prefs.CsvPreference
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.migration.agent.service.check.UnableToWriteCsvException;
import com.atlassian.migration.agent.service.check.csv.AbstractCheckResultCSVBean;
import com.atlassian.migration.agent.service.check.csv.CheckResultCSVContainer;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.List;
import org.supercsv.io.CsvBeanWriter;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CheckResultCSVWriter {
    public <T extends AbstractCheckResultCSVBean> void writeResultsInStream(OutputStream outputStream, CheckResultCSVContainer<T> container) {
        try (CsvBeanWriter beanWriter = new CsvBeanWriter((Writer)new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), CsvPreference.STANDARD_PREFERENCE);){
            beanWriter.writeHeader(container.headers());
            for (AbstractCheckResultCSVBean bean : container.beans()) {
                beanWriter.write((Object)bean, container.fieldMappings());
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Unable to write beans to csv.");
        }
    }

    public void writeListResultsInStream(OutputStream outputStream, String[] headers, Collection<List<String>> beans) {
        try (CsvListWriter beanWriter = new CsvListWriter((Writer)new OutputStreamWriter(outputStream, StandardCharsets.UTF_8), CsvPreference.STANDARD_PREFERENCE);){
            beanWriter.writeHeader(headers);
            for (List<String> bean : beans) {
                beanWriter.write(bean);
            }
        }
        catch (Exception e) {
            throw new UnableToWriteCsvException("Unable to write beans to csv.", e);
        }
    }
}


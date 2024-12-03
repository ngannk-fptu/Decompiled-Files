/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.ws.rs.core.StreamingOutput
 *  org.supercsv.encoder.CsvEncoder
 *  org.supercsv.io.CsvListWriter
 *  org.supercsv.prefs.CsvPreference$Builder
 */
package com.atlassian.migration.agent.service.guardrails.util;

import com.atlassian.migration.agent.service.guardrails.GuardrailsCsvEncoder;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import javax.ws.rs.core.StreamingOutput;
import org.supercsv.encoder.CsvEncoder;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvBuilder<T> {
    private final Map<String, Function<T, Object>> mappings = new LinkedHashMap<String, Function<T, Object>>();

    public CsvBuilder<T> addColumn(String name, Object value) {
        return this.addColumn(name, (T t) -> value);
    }

    public CsvBuilder<T> addColumn(String name, Function<T, Object> getter) {
        this.mappings.put(name, getter);
        return this;
    }

    public Path build(Path csvFilePath, List<T> records) throws IOException {
        StreamingOutput csvStreamingOutput = output -> {
            try (CsvListWriter csvListWriter = new CsvListWriter((Writer)new OutputStreamWriter(output, StandardCharsets.UTF_8), new CsvPreference.Builder('\"', 44, "\r\n").useEncoder((CsvEncoder)new GuardrailsCsvEncoder()).build());){
                csvListWriter.writeHeader(this.mappings.keySet().toArray(new String[0]));
                for (Object t : records) {
                    List columns = this.mappings.values().stream().map(f -> f.apply(t)).collect(Collectors.toList());
                    csvListWriter.write(columns);
                }
            }
            catch (IOException e) {
                throw new IOException("Unable to generate CSV file for instance assessment", e);
            }
        };
        Files.createDirectories(csvFilePath.getParent(), new FileAttribute[0]);
        Files.deleteIfExists(csvFilePath);
        Files.createFile(csvFilePath, new FileAttribute[0]);
        csvStreamingOutput.write(Files.newOutputStream(csvFilePath, new OpenOption[0]));
        return csvFilePath;
    }
}


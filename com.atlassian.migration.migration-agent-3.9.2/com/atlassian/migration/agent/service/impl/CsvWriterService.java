/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  lombok.Generated
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.supercsv.io.CsvListWriter
 *  org.supercsv.prefs.CsvPreference
 */
package com.atlassian.migration.agent.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.function.Function;
import lombok.Generated;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.supercsv.io.CsvListWriter;
import org.supercsv.prefs.CsvPreference;

public class CsvWriterService {
    @Generated
    private static final Logger log = LoggerFactory.getLogger(CsvWriterService.class);

    public File createEmptyCsvFile(Path fileDirectory, String fileName) throws IOException {
        File csvFile;
        boolean successfullyCreatedFile;
        Path csvPath;
        if (!Files.exists(fileDirectory, new LinkOption[0])) {
            Files.createDirectories(fileDirectory, new FileAttribute[0]);
        }
        if (Files.exists(csvPath = fileDirectory.resolve(fileName + ".csv"), new LinkOption[0])) {
            Files.delete(csvPath);
        }
        if (!(successfullyCreatedFile = (csvFile = new File(csvPath.toString())).createNewFile())) {
            throw new IOException("Could not create CSV file");
        }
        csvFile.deleteOnExit();
        log.debug("Successfully created CSV file: " + csvFile.getName());
        return csvFile;
    }

    public <T> void writeToCsvFile(File csvFile, String[] headers, List<T> data, Function<T, String[]> objectToCsvRow) throws IOException {
        try (CsvListWriter listWriter = this.csvListWriterSupplier(csvFile);){
            listWriter.writeHeader(headers);
            for (T object : data) {
                listWriter.write(objectToCsvRow.apply(object));
            }
            log.debug("Successfully written data to csv file: " + csvFile.getName());
        }
        catch (Exception e) {
            log.error("Error writing to CSV file: " + e.getMessage(), (Throwable)e);
            throw new IOException("Could not write CSV file", e);
        }
    }

    public void deleteCsvFile(File csvFile) {
        if (csvFile != null) {
            try {
                Files.delete(csvFile.toPath());
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    protected CsvListWriter csvListWriterSupplier(File csvFile) {
        try {
            return new CsvListWriter((Writer)new OutputStreamWriter((OutputStream)new FileOutputStream(csvFile), StandardCharsets.UTF_8), CsvPreference.STANDARD_PREFERENCE);
        }
        catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }
}


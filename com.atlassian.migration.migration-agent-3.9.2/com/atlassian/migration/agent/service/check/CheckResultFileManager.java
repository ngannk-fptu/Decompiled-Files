/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.cmpt.check.base.CheckResult
 *  com.atlassian.confluence.setup.BootstrapManager
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.codehaus.jackson.type.TypeReference
 *  org.slf4j.Logger
 */
package com.atlassian.migration.agent.service.check;

import com.atlassian.cmpt.check.base.CheckResult;
import com.atlassian.confluence.setup.BootstrapManager;
import com.atlassian.migration.agent.json.Jsons;
import com.atlassian.migration.agent.logging.ContextLoggerFactory;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.FileAttribute;
import java.util.List;
import java.util.UUID;
import javax.annotation.ParametersAreNonnullByDefault;
import org.codehaus.jackson.type.TypeReference;
import org.slf4j.Logger;

@ParametersAreNonnullByDefault
public class CheckResultFileManager {
    private static final Logger log = ContextLoggerFactory.getLogger(CheckResultFileManager.class);
    private final BootstrapManager bootstrapManager;

    public CheckResultFileManager(BootstrapManager bootstrapManager) {
        this.bootstrapManager = bootstrapManager;
    }

    public void saveToFile(String fileName, CheckResult checkResult) {
        try (FileOutputStream fileOutputStream = new FileOutputStream(this.getPreflightCheckFile(fileName).toFile());
             BufferedOutputStream bufferedOutputStream = new BufferedOutputStream(fileOutputStream);
             ObjectOutputStream objectOutputStream = new ObjectOutputStream(bufferedOutputStream);){
            objectOutputStream.writeObject(checkResult);
        }
        catch (IOException e) {
            throw new RuntimeException("Can't write object to file", e);
        }
    }

    /*
     * Exception decompiling
     */
    public CheckResult readFromFile(String file) {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 5 blocks at once
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.getStartingBlocks(Op04StructuredStatement.java:412)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op04StructuredStatement.buildNestedBlocks(Op04StructuredStatement.java:487)
         *     at org.benf.cfr.reader.bytecode.analysis.opgraph.Op03SimpleStatement.createInitialStructuredBlock(Op03SimpleStatement.java:736)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisInner(CodeAnalyser.java:850)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysisOrWrapFail(CodeAnalyser.java:278)
         *     at org.benf.cfr.reader.bytecode.CodeAnalyser.getAnalysis(CodeAnalyser.java:201)
         *     at org.benf.cfr.reader.entities.attributes.AttributeCode.analyse(AttributeCode.java:94)
         *     at org.benf.cfr.reader.entities.Method.analyse(Method.java:531)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseMid(ClassFile.java:1055)
         *     at org.benf.cfr.reader.entities.ClassFile.analyseTop(ClassFile.java:942)
         *     at org.benf.cfr.reader.Driver.doJarVersionTypes(Driver.java:257)
         *     at org.benf.cfr.reader.Driver.doJar(Driver.java:139)
         *     at org.benf.cfr.reader.CfrDriverImpl.analyse(CfrDriverImpl.java:76)
         *     at org.benf.cfr.reader.Main.main(Main.java:54)
         */
        throw new IllegalStateException("Decompilation failed");
    }

    public <T> String writeToJsonFile(String prefix, List<T> records) throws IOException {
        File file = this.getPreflightCheckFile(prefix + "-" + UUID.randomUUID()).toFile();
        Jsons.valueAsJsonFile(file, records);
        log.info("Saved  records to file: {}", (Object)file.getPath());
        log.debug("Records: {} ", records);
        return file.getPath();
    }

    public <T> List<T> readFromJsonFile(String path, TypeReference<List<T>> typeReference) throws IOException {
        File file = new File(path);
        return (List)Jsons.readValue(file, typeReference);
    }

    public void deleteFile(String file) {
        try {
            Files.delete(this.getPreflightCheckFile(file));
        }
        catch (IOException e) {
            log.error("Failed to clean up file. Reason: {}", (Object)e.getMessage(), (Object)e);
        }
    }

    private Path getPreflightCheckFile(String file) {
        return this.getPreflightCheckPath().resolve(file);
    }

    private Path getPreflightCheckPath() {
        Path path = Paths.get(this.bootstrapManager.getSharedHome().getAbsolutePath(), "migration", "check");
        if (!Files.exists(path, new LinkOption[0])) {
            try {
                Files.createDirectories(path, new FileAttribute[0]);
            }
            catch (IOException e) {
                new RuntimeException("Unable to create check directory", e);
            }
        }
        return path;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.velocity.exception.ResourceNotFoundException
 *  org.apache.velocity.runtime.resource.loader.ResourceLoader
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.Assert
 */
package com.atlassian.confluence.util.velocity;

import com.atlassian.confluence.util.velocity.ResourceLoaderWrapper;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.Charset;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.loader.ResourceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

final class Velocity13CompatibleResourceLoader
extends ResourceLoaderWrapper {
    private static final Logger log = LoggerFactory.getLogger(Velocity13CompatibleResourceLoader.class);
    private final Charset charset;

    public Velocity13CompatibleResourceLoader(ResourceLoader wrappedLoader, String charsetName) {
        super(wrappedLoader);
        Assert.notNull((Object)charsetName, (String)"charsetName must not be null");
        this.charset = Charset.forName(charsetName);
    }

    /*
     * Exception decompiling
     */
    @Override
    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        /*
         * This method has failed to decompile.  When submitting a bug report, please provide this stack trace, and (if you hold appropriate legal rights) the relevant class file.
         * 
         * org.benf.cfr.reader.util.ConfusedCFRException: Started 3 blocks at once
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

    private static class Velocity13TemplateConverter {
        private static final Pattern regex = Pattern.compile("(#[a-zA-Z0-9]*\\s*\\()(\\s*)([^'\" !$\\)\\(]{1,})");
        private final Reader resourceReader;
        private final String resourceName;

        private Velocity13TemplateConverter(String resourceName, Reader resourceReader) {
            this.resourceName = resourceName;
            this.resourceReader = resourceReader;
        }

        public void convert(Writer destinationWriter) throws IOException {
            try (BufferedWriter bufferedWriter = new BufferedWriter(destinationWriter);
                 BufferedReader bufferedReader = new BufferedReader(this.resourceReader);){
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    Matcher matcher = regex.matcher(line);
                    if (matcher.find() && !matcher.group(1).startsWith("#macro")) {
                        if (log.isInfoEnabled()) {
                            log.info("Found incompatible Velocity 1.5 syntax in resource: " + this.resourceName + "; " + matcher.group());
                        }
                        bufferedWriter.write(matcher.replaceAll("$1$2\"$3\""));
                    } else {
                        bufferedWriter.write(line);
                    }
                    bufferedWriter.newLine();
                }
            }
        }
    }
}


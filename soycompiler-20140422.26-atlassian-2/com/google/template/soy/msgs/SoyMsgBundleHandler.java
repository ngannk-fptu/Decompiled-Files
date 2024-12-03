/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Charsets
 *  com.google.common.io.FileWriteMode
 *  com.google.common.io.Files
 *  com.google.common.io.Resources
 *  com.google.inject.Inject
 */
package com.google.template.soy.msgs;

import com.google.common.base.Charsets;
import com.google.common.io.FileWriteMode;
import com.google.common.io.Files;
import com.google.common.io.Resources;
import com.google.inject.Inject;
import com.google.template.soy.base.internal.BaseUtils;
import com.google.template.soy.msgs.SoyBidirectionalMsgPlugin;
import com.google.template.soy.msgs.SoyMsgBundle;
import com.google.template.soy.msgs.SoyMsgException;
import com.google.template.soy.msgs.SoyMsgPlugin;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.regex.Pattern;

public class SoyMsgBundleHandler {
    private static final Pattern FIRST_WORD_IS_EN_PATTERN = Pattern.compile("^en[^A-Za-z].*");
    private final SoyMsgPlugin msgPlugin;

    @Inject
    public SoyMsgBundleHandler(SoyMsgPlugin msgPlugin) {
        this.msgPlugin = msgPlugin;
    }

    public SoyMsgBundle createFromFile(File inputFile) throws IOException, SoyMsgException {
        if (!inputFile.exists() && FIRST_WORD_IS_EN_PATTERN.matcher(inputFile.getName()).matches()) {
            return SoyMsgBundle.EMPTY;
        }
        try {
            String inputFileContent = Files.asCharSource((File)inputFile, (Charset)Charsets.UTF_8).read();
            return this.msgPlugin.parseTranslatedMsgsFile(inputFileContent);
        }
        catch (SoyMsgException sme) {
            sme.setFileOrResourceName(inputFile.toString());
            throw sme;
        }
    }

    public SoyMsgBundle createFromResource(URL inputResource) throws IOException, SoyMsgException {
        try {
            String inputFileContent = Resources.toString((URL)inputResource, (Charset)Charsets.UTF_8);
            return this.msgPlugin.parseTranslatedMsgsFile(inputFileContent);
        }
        catch (SoyMsgException sme) {
            sme.setFileOrResourceName(inputResource.toString());
            throw sme;
        }
    }

    public void writeToExtractedMsgsFile(SoyMsgBundle msgBundle, OutputFileOptions options, File outputFile) throws IOException, SoyMsgException {
        CharSequence cs = this.msgPlugin.generateExtractedMsgsFile(msgBundle, options);
        BaseUtils.ensureDirsExistInPath(outputFile.getPath());
        Files.asCharSink((File)outputFile, (Charset)Charsets.UTF_8, (FileWriteMode[])new FileWriteMode[0]).write(cs);
    }

    public void writeToTranslatedMsgsFile(SoyMsgBundle msgBundle, OutputFileOptions options, File outputFile) throws IOException, SoyMsgException {
        if (!(this.msgPlugin instanceof SoyBidirectionalMsgPlugin)) {
            throw new SoyMsgException("writeToTranslatedMsgsFile() only works if using a SoyBidirectionalMsgPlugin.");
        }
        SoyBidirectionalMsgPlugin msgPluginCast = (SoyBidirectionalMsgPlugin)this.msgPlugin;
        CharSequence cs = msgPluginCast.generateTranslatedMsgsFile(msgBundle, options);
        BaseUtils.ensureDirsExistInPath(outputFile.getPath());
        Files.asCharSink((File)outputFile, (Charset)Charsets.UTF_8, (FileWriteMode[])new FileWriteMode[0]).write(cs);
    }

    public static class OutputFileOptions {
        private String sourceLocaleString = "en";
        private String targetLocaleString = null;

        public void setSourceLocaleString(String sourceLocaleString) {
            this.sourceLocaleString = sourceLocaleString;
        }

        public String getSourceLocaleString() {
            return this.sourceLocaleString;
        }

        public void setTargetLocaleString(String targetLocaleString) {
            this.targetLocaleString = targetLocaleString;
        }

        public String getTargetLocaleString() {
            return this.targetLocaleString;
        }
    }
}


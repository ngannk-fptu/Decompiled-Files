/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.JarPluginArtifact
 *  com.atlassian.plugin.PluginArtifact
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringEscapeUtils
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.ext.code.languages.impl;

import com.atlassian.confluence.ext.code.languages.Language;
import com.atlassian.confluence.ext.code.languages.impl.PluginGenerator;
import com.atlassian.plugin.JarPluginArtifact;
import com.atlassian.plugin.PluginArtifact;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringEscapeUtils;
import org.springframework.stereotype.Component;

@Component
public class PluginGeneratorImpl
implements PluginGenerator {
    private static final Random RANDOMIZER = new Random();

    @Override
    public PluginArtifact createPluginForLanguage(Language languageToRegister, Reader scriptContents) throws IOException {
        return new JarPluginArtifact(ZipBuilder.buildZip(PluginGeneratorImpl.getFileName(languageToRegister), builder -> {
            builder.addFile("javascript/shLang.js", scriptContents);
            builder.addFile("atlassian-plugin.xml", PluginGeneratorImpl.getPluginXml(languageToRegister));
        }));
    }

    private static String getFileName(Language languageToRegister) {
        return "install-language-" + languageToRegister.getName() + "-" + RANDOMIZER.nextLong();
    }

    private static String getPluginXml(Language languageToRegister) throws IOException {
        InputStream templateResource = null;
        String templateContents = "";
        try {
            templateResource = PluginGeneratorImpl.class.getClassLoader().getResourceAsStream("templates/generator/atlassian-plugin-template.xml");
            templateContents = IOUtils.toString((InputStream)templateResource, (Charset)StandardCharsets.UTF_8);
        }
        catch (Throwable throwable) {
            IOUtils.closeQuietly(templateResource);
            throw throwable;
        }
        IOUtils.closeQuietly((InputStream)templateResource);
        templateContents = templateContents.replace("${language}", StringEscapeUtils.escapeXml11((String)languageToRegister.getName()));
        templateContents = templateContents.replace("${friendlyName}", StringEscapeUtils.escapeXml11((String)languageToRegister.getFriendlyName()));
        templateContents = templateContents.replace("${randomId}", StringEscapeUtils.escapeXml11((String)String.valueOf(RANDOMIZER.nextLong())));
        return templateContents;
    }

    private static final class ZipBuilder {
        private final ZipOutputStream zout;

        ZipBuilder(ZipOutputStream zout) {
            this.zout = zout;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        static File buildZip(String identifier, ZipHandler handler) throws IOException {
            File tmpFile;
            ZipOutputStream zout = null;
            try {
                tmpFile = ZipBuilder.createExtractableTempFile(identifier, ".jar");
                zout = new ZipOutputStream(new FileOutputStream(tmpFile));
                ZipBuilder builder = new ZipBuilder(zout);
                handler.build(builder);
            }
            catch (Throwable throwable) {
                IOUtils.closeQuietly(zout);
                throw throwable;
            }
            IOUtils.closeQuietly((OutputStream)zout);
            return tmpFile;
        }

        /*
         * WARNING - Removed try catching itself - possible behaviour change.
         */
        void addFile(String path, Reader contents) throws IOException {
            try {
                ZipEntry entry = new ZipEntry(path);
                this.zout.putNextEntry(entry);
                IOUtils.copy((Reader)contents, (OutputStream)this.zout, (Charset)StandardCharsets.UTF_8);
            }
            finally {
                IOUtils.closeQuietly((Reader)contents);
            }
        }

        void addFile(String path, String contents) throws IOException {
            ZipEntry entry = new ZipEntry(path);
            this.zout.putNextEntry(entry);
            IOUtils.copy((Reader)new StringReader(contents), (OutputStream)this.zout);
        }

        static File createExtractableTempFile(String key, String suffix) throws IOException {
            return File.createTempFile(key, suffix);
        }
    }

    static interface ZipHandler {
        public void build(ZipBuilder var1) throws IOException;
    }
}


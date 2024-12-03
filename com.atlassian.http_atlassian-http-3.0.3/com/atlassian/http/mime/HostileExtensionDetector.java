/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.ImmutableSet
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.http.mime;

import com.atlassian.http.mime.StringUtils;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableSet;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HostileExtensionDetector {
    private static final Logger log = LoggerFactory.getLogger(HostileExtensionDetector.class);
    private static final String DELIMITER_REGEX = "\\s+";
    private static final String KEY_EXECUTABLE_CONTENT_TYPES = "executable.mime.types";
    private static final String KEY_EXECUTABLE_IF_NO_EXT_NO_MIME = "executable.if.no.extension.no.mime";
    private static final String KEY_EXECUTABLE_FILE_EXTENSIONS = "executable.file.extensions";
    private static final String KEY_TEXT_FILE_EXTENSIONS = "text.file.extensions";
    private static final String KEY_TEXT_FILE_CONTENT_TYPES = "text.file.mime.types";
    private static final String KEY_SAFE_CONTENT_TYPES = "safe.file.mime.types";
    private static final String CONFIG_FILE = "hostile-attachments-config.properties";
    private static final Set<String> DEFAULT_EXECUTABLE_FILE_EXTENSIONS = ImmutableSet.of((Object)".htm", (Object)".html", (Object)".xhtml", (Object)".xml", (Object)".shtml", (Object)".svg", (Object[])new String[]{".swf", ".cab", ".flv", ".f4v", ".f4p", ".f4a", ".f4b"});
    private static final Set<String> DEFAULT_TEXT_FILE_EXTENSIONS = ImmutableSet.of((Object)".txt");
    private static final Set<String> DEFAULT_TEXT_FILE_CONTENT_TYPES = ImmutableSet.of((Object)"text/plain");
    private static final Pattern VALID_MIME_TYPE = Pattern.compile("([a-z0-9_-]+/[^;\\s]+)+.*", 42);
    private static final Set<String> DEFAULT_EXECUTABLE_CONTENT_TYPES = ImmutableSet.of((Object)"text/html", (Object)"text/html-sandboxed", (Object)"text/xhtml", (Object)"application/xhtml+xml", (Object)"text/xml", (Object)"application/xml", (Object[])new String[]{"text/xml-external-parsed-entity", "application/xml-external-parsed-entity", "application/xml-dtd", "application/x-shockwave-flash", "image/svg+xml", "image/svg-xml", "application/futuresplash", "application/x-cab", "video/x-flv", "application/octet-stream", "application/pdf", "message/rfc822", "text/vnd.wap.wml", "application/atom+xml", "text/webviewhtml", "application/rdf+xml", "application/mathml+xml", "multipart/x-mixed-replace", "application/vnd.wap.xhtml+xml", "text/rdf", "text/xsl", "text/vtt"});
    private Set<String> executableFileExtensions;
    private Set<String> textfileExtensions;
    private Set<String> textfileContentTypes;
    private Set<String> executableContentTypes;
    private Set<String> safeContentTypes;

    public HostileExtensionDetector() {
        this.loadConfiguration();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private void loadConfiguration() {
        Properties config = new Properties();
        InputStream in = this.getClass().getResourceAsStream(CONFIG_FILE);
        if (in != null) {
            try {
                config.load(in);
            }
            catch (IOException e) {
                log.warn("Unable to load config from 'hostile-attachments-config.properties' falling back to defaults ");
            }
            finally {
                try {
                    in.close();
                }
                catch (IOException iOException) {}
            }
        } else {
            log.warn("Unable to load config from 'hostile-attachments-config.properties' falling back to defaults ");
        }
        this.parseConfiguration(config);
    }

    private void parseConfiguration(Properties config) {
        this.executableFileExtensions = this.getProperty(config, KEY_EXECUTABLE_FILE_EXTENSIONS, DEFAULT_EXECUTABLE_FILE_EXTENSIONS);
        this.executableContentTypes = this.getProperty(config, KEY_EXECUTABLE_CONTENT_TYPES, DEFAULT_EXECUTABLE_CONTENT_TYPES);
        this.textfileExtensions = this.getProperty(config, KEY_TEXT_FILE_EXTENSIONS, DEFAULT_TEXT_FILE_EXTENSIONS);
        this.textfileContentTypes = this.getProperty(config, KEY_TEXT_FILE_CONTENT_TYPES, DEFAULT_TEXT_FILE_CONTENT_TYPES);
        this.safeContentTypes = this.getProperty(config, KEY_SAFE_CONTENT_TYPES, Collections.emptySet());
    }

    private Set<String> getProperty(Properties config, String key, Set<String> defaultValue) {
        String extensions = config.getProperty(key);
        if (log.isDebugEnabled()) {
            log.debug("Configured executable file extensions: '" + extensions + "'");
        }
        if (!StringUtils.isBlank(extensions)) {
            return ImmutableSet.copyOf((Object[])extensions.toLowerCase(Locale.US).trim().split(DELIMITER_REGEX));
        }
        return new HashSet<String>(defaultValue);
    }

    public boolean isExecutableFileExtension(String name) {
        boolean isExecutableFileExtension = false;
        if (!StringUtils.isBlank(name)) {
            isExecutableFileExtension = this.executableFileExtensions.contains(this.getFileExtension(name));
        }
        return isExecutableFileExtension;
    }

    private String getFileExtension(String name) {
        return name.contains(".") ? name.substring(name.lastIndexOf("."), name.length()).toLowerCase(Locale.US) : "";
    }

    public boolean isExecutableContentType(String contentType) {
        boolean isExecutableContentType = false;
        if (StringUtils.isBlank(contentType)) {
            return true;
        }
        if (!VALID_MIME_TYPE.matcher(contentType.toLowerCase()).matches()) {
            return true;
        }
        for (String executableContentType : this.executableContentTypes) {
            if (!contentType.toLowerCase(Locale.US).contains(executableContentType)) continue;
            isExecutableContentType = true;
            break;
        }
        return isExecutableContentType;
    }

    public boolean isSafeContentType(String contentType) {
        return !StringUtils.isBlank(contentType) && this.safeContentTypes.contains(contentType);
    }

    public boolean isTextExtension(String fileName) {
        boolean isTextFileExtension = false;
        if (!StringUtils.isBlank(fileName)) {
            isTextFileExtension = this.textfileExtensions.contains(this.getFileExtension(fileName));
        }
        return isTextFileExtension;
    }

    public boolean isTextContentType(String contentType) {
        boolean isTextContentType = false;
        if (!StringUtils.isBlank(contentType)) {
            isTextContentType = this.textfileContentTypes.contains(contentType.toLowerCase(Locale.US));
        }
        return isTextContentType;
    }

    public boolean isExecutableContent(String fileName, String contentType) {
        return this.isExecutableFileExtension(fileName) || this.isExecutableContentType(contentType);
    }

    public boolean isTextContent(String fileName, String contentType) {
        return this.isTextExtension(fileName) && this.isTextContentType(contentType);
    }

    @VisibleForTesting
    Set<String> getSafeContentTypes() {
        return this.safeContentTypes;
    }

    @VisibleForTesting
    Set<String> getExecutableContentTypes() {
        return this.executableContentTypes;
    }

    @VisibleForTesting
    Set<String> getDefaultExecutableContentTypes() {
        return DEFAULT_EXECUTABLE_CONTENT_TYPES;
    }

    @VisibleForTesting
    Set<String> getExecutableFileExtensions() {
        return this.executableFileExtensions;
    }
}


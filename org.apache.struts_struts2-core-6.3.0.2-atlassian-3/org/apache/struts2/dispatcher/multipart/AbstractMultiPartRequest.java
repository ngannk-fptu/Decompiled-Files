/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package org.apache.struts2.dispatcher.multipart;

import com.opensymphony.xwork2.LocaleProviderFactory;
import com.opensymphony.xwork2.inject.Inject;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import javax.servlet.http.HttpServletRequest;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequest;

public abstract class AbstractMultiPartRequest
implements MultiPartRequest {
    private static final Logger LOG = LogManager.getLogger(AbstractMultiPartRequest.class);
    public static final int BUFFER_SIZE = 10240;
    protected List<LocalizedMessage> errors = new ArrayList<LocalizedMessage>();
    protected Long maxSize;
    protected Long maxFiles;
    protected Long maxStringLength;
    protected Long maxFileSize;
    protected int bufferSize = 10240;
    protected String defaultEncoding;
    protected Locale defaultLocale = Locale.ENGLISH;

    @Inject(value="struts.multipart.bufferSize", required=false)
    public void setBufferSize(String bufferSize) {
        this.bufferSize = Integer.parseInt(bufferSize);
    }

    @Inject(value="struts.i18n.encoding")
    public void setDefaultEncoding(String enc) {
        this.defaultEncoding = enc;
    }

    @Inject(value="struts.multipart.maxSize")
    public void setMaxSize(String maxSize) {
        this.maxSize = Long.parseLong(maxSize);
    }

    @Inject(value="struts.multipart.maxFiles")
    public void setMaxFiles(String maxFiles) {
        this.maxFiles = Long.parseLong(maxFiles);
    }

    @Inject(value="struts.multipart.maxFileSize", required=false)
    public void setMaxFileSize(String maxFileSize) {
        this.maxFileSize = Long.parseLong(maxFileSize);
    }

    @Inject(value="struts.multipart.maxStringLength")
    public void setMaxStringLength(String maxStringLength) {
        this.maxStringLength = Long.parseLong(maxStringLength);
    }

    @Inject
    public void setLocaleProviderFactory(LocaleProviderFactory localeProviderFactory) {
        this.defaultLocale = localeProviderFactory.createLocaleProvider().getLocale();
    }

    protected void setLocale(HttpServletRequest request) {
        if (this.defaultLocale == null) {
            this.defaultLocale = request.getLocale();
        }
    }

    protected LocalizedMessage buildErrorMessage(Throwable e, Object[] args) {
        String errorKey = "struts.messages.upload.error." + e.getClass().getSimpleName();
        LOG.debug("Preparing error message for key: [{}]", (Object)errorKey);
        return new LocalizedMessage(this.getClass(), errorKey, e.getMessage(), args);
    }

    @Override
    public List<LocalizedMessage> getErrors() {
        return this.errors;
    }

    protected String getCanonicalName(String originalFileName) {
        String fileName = originalFileName;
        int forwardSlash = fileName.lastIndexOf(47);
        int backwardSlash = fileName.lastIndexOf(92);
        fileName = forwardSlash != -1 && forwardSlash > backwardSlash ? fileName.substring(forwardSlash + 1) : fileName.substring(backwardSlash + 1);
        return fileName;
    }
}


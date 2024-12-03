/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.servlet.ServletContext
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.fileupload.FileItemFactory
 *  org.apache.commons.fileupload.FileUpload
 *  org.apache.commons.fileupload.FileUploadBase$FileSizeLimitExceededException
 *  org.apache.commons.fileupload.FileUploadBase$SizeLimitExceededException
 *  org.apache.commons.fileupload.FileUploadException
 *  org.apache.commons.fileupload.servlet.ServletFileUpload
 */
package org.springframework.web.multipart.commons;

import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.FileUploadBase;
import org.apache.commons.fileupload.FileUploadException;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.springframework.util.Assert;
import org.springframework.web.context.ServletContextAware;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.commons.CommonsFileUploadSupport;
import org.springframework.web.multipart.support.AbstractMultipartHttpServletRequest;
import org.springframework.web.multipart.support.DefaultMultipartHttpServletRequest;
import org.springframework.web.util.WebUtils;

public class CommonsMultipartResolver
extends CommonsFileUploadSupport
implements MultipartResolver,
ServletContextAware {
    private boolean resolveLazily = false;

    public CommonsMultipartResolver() {
    }

    public CommonsMultipartResolver(ServletContext servletContext) {
        this();
        this.setServletContext(servletContext);
    }

    public void setResolveLazily(boolean resolveLazily) {
        this.resolveLazily = resolveLazily;
    }

    @Override
    protected FileUpload newFileUpload(FileItemFactory fileItemFactory) {
        return new ServletFileUpload(fileItemFactory);
    }

    @Override
    public void setServletContext(ServletContext servletContext) {
        if (!this.isUploadTempDirSpecified()) {
            this.getFileItemFactory().setRepository(WebUtils.getTempDir(servletContext));
        }
    }

    @Override
    public boolean isMultipart(HttpServletRequest request) {
        return ServletFileUpload.isMultipartContent((HttpServletRequest)request);
    }

    @Override
    public MultipartHttpServletRequest resolveMultipart(final HttpServletRequest request) throws MultipartException {
        Assert.notNull((Object)request, "Request must not be null");
        if (this.resolveLazily) {
            return new DefaultMultipartHttpServletRequest(request){

                @Override
                protected void initializeMultipart() {
                    CommonsFileUploadSupport.MultipartParsingResult parsingResult = CommonsMultipartResolver.this.parseRequest(request);
                    this.setMultipartFiles(parsingResult.getMultipartFiles());
                    this.setMultipartParameters(parsingResult.getMultipartParameters());
                    this.setMultipartParameterContentTypes(parsingResult.getMultipartParameterContentTypes());
                }
            };
        }
        CommonsFileUploadSupport.MultipartParsingResult parsingResult = this.parseRequest(request);
        return new DefaultMultipartHttpServletRequest(request, parsingResult.getMultipartFiles(), parsingResult.getMultipartParameters(), parsingResult.getMultipartParameterContentTypes());
    }

    protected CommonsFileUploadSupport.MultipartParsingResult parseRequest(HttpServletRequest request) throws MultipartException {
        String encoding = this.determineEncoding(request);
        FileUpload fileUpload = this.prepareFileUpload(encoding);
        try {
            List fileItems = ((ServletFileUpload)fileUpload).parseRequest(request);
            return this.parseFileItems(fileItems, encoding);
        }
        catch (FileUploadBase.SizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getSizeMax(), (Throwable)ex);
        }
        catch (FileUploadBase.FileSizeLimitExceededException ex) {
            throw new MaxUploadSizeExceededException(fileUpload.getFileSizeMax(), (Throwable)ex);
        }
        catch (FileUploadException ex) {
            throw new MultipartException("Failed to parse multipart servlet request", ex);
        }
    }

    protected String determineEncoding(HttpServletRequest request) {
        String encoding = request.getCharacterEncoding();
        if (encoding == null) {
            encoding = this.getDefaultEncoding();
        }
        return encoding;
    }

    @Override
    public void cleanupMultipart(MultipartHttpServletRequest request) {
        if (!(request instanceof AbstractMultipartHttpServletRequest) || ((AbstractMultipartHttpServletRequest)request).isResolved()) {
            try {
                this.cleanupFileItems(request.getMultiFileMap());
            }
            catch (Throwable ex) {
                this.logger.warn("Failed to perform multipart cleanup for servlet request", ex);
            }
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.core.ConfluenceActionSupport
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.PermissionManager
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.FileUploadUtils$UploadedFile
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  org.apache.commons.io.FilenameUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.core.io.FileSystemResource
 *  org.springframework.core.io.Resource
 */
package com.atlassian.confluence.extra.flyingpdf.config;

import com.atlassian.confluence.core.ConfluenceActionSupport;
import com.atlassian.confluence.extra.flyingpdf.config.FontManager;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.PermissionManager;
import com.atlassian.xwork.FileUploadUtils;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

public class ConfigurePDFLanguageSupportAction
extends ConfluenceActionSupport {
    private static final Logger log = LoggerFactory.getLogger(ConfigurePDFLanguageSupportAction.class);
    private FontManager pdfExportFontManager;
    private boolean installFontSuccess = false;

    public String execute() throws Exception {
        final FileUploadUtils.UploadedFile uploadedFile = FileUploadUtils.getSingleUploadedFile();
        if (uploadedFile == null) {
            this.addActionError(this.getText("pdf.font.error.nofile"));
            return "error";
        }
        try {
            String extension = FilenameUtils.getExtension((String)uploadedFile.getFileName());
            if (!extension.equals("ttf") && !extension.equals("ttc")) {
                this.addActionError(this.getText("pdf.error.file.extension"));
                return "error";
            }
            if (!uploadedFile.getContentType().equals("font/ttf") && !uploadedFile.getContentType().equals("application/octet-stream")) {
                this.addActionError(this.getText("pdf.font.error.contenttype"));
                return "error";
            }
            if (!this.isValidFileContentType(uploadedFile.getFile())) {
                return "error";
            }
            this.pdfExportFontManager.installFont((Resource)new FileSystemResource(uploadedFile.getFile()){

                public String getFilename() {
                    return uploadedFile.getFileName();
                }
            });
        }
        catch (Exception ex) {
            log.error("Unable to install language font.", (Throwable)ex);
            this.addActionError(ex.getMessage());
            return "error";
        }
        return "success";
    }

    private boolean isValidFileContentType(File file) throws Exception {
        try (FileInputStream inputStream = new FileInputStream(file);){
            AutoDetectParser parser = new AutoDetectParser();
            BodyContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            ParseContext context = new ParseContext();
            parser.parse(inputStream, handler, metadata, context);
            if (!metadata.get("Content-Type").equals("application/x-font-ttf") && !metadata.get("Content-Type").equals("application/octet-stream")) {
                this.addActionError(this.getText("pdf.font.error.content"));
                boolean bl = false;
                return bl;
            }
        }
        return true;
    }

    @PermittedMethods(value={HttpMethod.GET, HttpMethod.POST})
    public String doRestoreDefault() {
        try {
            this.pdfExportFontManager.removeInstalledFont();
        }
        catch (IOException ex) {
            log.error("Unable to remove custom font.", (Throwable)ex);
            this.addActionError(ex.getMessage());
            return "error";
        }
        return "success";
    }

    public boolean isPermitted() {
        return this.permissionManager.hasPermission(this.getRemoteUser(), Permission.ADMINISTER, PermissionManager.TARGET_SYSTEM);
    }

    public String getInstalledFontName() {
        FileSystemResource fontResource = this.pdfExportFontManager.getInstalledFont();
        if (fontResource != null) {
            return fontResource.getFilename();
        }
        return null;
    }

    public boolean isInstallFontSuccess() {
        return this.installFontSuccess;
    }

    public void setInstallFontSuccess(boolean installFontSuccess) {
        this.installFontSuccess = installFontSuccess;
    }

    public void setPdfExportFontManager(FontManager pdfExportFontManager) {
        this.pdfExportFontManager = pdfExportFontManager;
    }
}


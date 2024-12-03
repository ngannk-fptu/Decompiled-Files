/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.fileupload.FileItem
 *  org.apache.commons.fileupload.FileItemFactory
 *  org.apache.commons.fileupload.FileUpload
 *  org.apache.commons.fileupload.disk.DiskFileItemFactory
 *  org.apache.commons.logging.Log
 *  org.apache.commons.logging.LogFactory
 *  org.springframework.core.io.Resource
 *  org.springframework.core.log.LogFormatUtils
 *  org.springframework.lang.Nullable
 *  org.springframework.util.LinkedMultiValueMap
 *  org.springframework.util.MultiValueMap
 *  org.springframework.util.StringUtils
 */
package org.springframework.web.multipart.commons;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.FileUpload;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.log.LogFormatUtils;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;

public abstract class CommonsFileUploadSupport {
    protected final Log logger = LogFactory.getLog(this.getClass());
    private final DiskFileItemFactory fileItemFactory = this.newFileItemFactory();
    private final FileUpload fileUpload = this.newFileUpload((FileItemFactory)this.getFileItemFactory());
    private boolean uploadTempDirSpecified = false;
    private boolean preserveFilename = false;

    public DiskFileItemFactory getFileItemFactory() {
        return this.fileItemFactory;
    }

    public FileUpload getFileUpload() {
        return this.fileUpload;
    }

    public void setMaxUploadSize(long maxUploadSize) {
        this.fileUpload.setSizeMax(maxUploadSize);
    }

    public void setMaxUploadSizePerFile(long maxUploadSizePerFile) {
        this.fileUpload.setFileSizeMax(maxUploadSizePerFile);
    }

    public void setMaxInMemorySize(int maxInMemorySize) {
        this.fileItemFactory.setSizeThreshold(maxInMemorySize);
    }

    public void setDefaultEncoding(String defaultEncoding) {
        this.fileUpload.setHeaderEncoding(defaultEncoding);
    }

    protected String getDefaultEncoding() {
        String encoding = this.getFileUpload().getHeaderEncoding();
        if (encoding == null) {
            encoding = "ISO-8859-1";
        }
        return encoding;
    }

    public void setUploadTempDir(Resource uploadTempDir) throws IOException {
        if (!uploadTempDir.exists() && !uploadTempDir.getFile().mkdirs()) {
            throw new IllegalArgumentException("Given uploadTempDir [" + uploadTempDir + "] could not be created");
        }
        this.fileItemFactory.setRepository(uploadTempDir.getFile());
        this.uploadTempDirSpecified = true;
    }

    protected boolean isUploadTempDirSpecified() {
        return this.uploadTempDirSpecified;
    }

    public void setPreserveFilename(boolean preserveFilename) {
        this.preserveFilename = preserveFilename;
    }

    protected DiskFileItemFactory newFileItemFactory() {
        return new DiskFileItemFactory();
    }

    protected abstract FileUpload newFileUpload(FileItemFactory var1);

    protected FileUpload prepareFileUpload(@Nullable String encoding) {
        FileUpload fileUpload;
        FileUpload actualFileUpload = fileUpload = this.getFileUpload();
        if (encoding != null && !encoding.equals(fileUpload.getHeaderEncoding())) {
            actualFileUpload = this.newFileUpload((FileItemFactory)this.getFileItemFactory());
            actualFileUpload.setSizeMax(fileUpload.getSizeMax());
            actualFileUpload.setFileSizeMax(fileUpload.getFileSizeMax());
            actualFileUpload.setHeaderEncoding(encoding);
        }
        return actualFileUpload;
    }

    protected MultipartParsingResult parseFileItems(List<FileItem> fileItems, String encoding) {
        LinkedMultiValueMap multipartFiles = new LinkedMultiValueMap();
        HashMap<String, String[]> multipartParameters = new HashMap<String, String[]>();
        HashMap<String, String> multipartParameterContentTypes = new HashMap<String, String>();
        for (FileItem fileItem : fileItems) {
            if (fileItem.isFormField()) {
                String value;
                String partEncoding = this.determineEncoding(fileItem.getContentType(), encoding);
                try {
                    value = fileItem.getString(partEncoding);
                }
                catch (UnsupportedEncodingException ex) {
                    if (this.logger.isWarnEnabled()) {
                        this.logger.warn((Object)("Could not decode multipart item '" + fileItem.getFieldName() + "' with encoding '" + partEncoding + "': using platform default"));
                    }
                    value = fileItem.getString();
                }
                String[] curParam = (String[])multipartParameters.get(fileItem.getFieldName());
                if (curParam == null) {
                    multipartParameters.put(fileItem.getFieldName(), new String[]{value});
                } else {
                    String[] newParam = StringUtils.addStringToArray((String[])curParam, (String)value);
                    multipartParameters.put(fileItem.getFieldName(), newParam);
                }
                multipartParameterContentTypes.put(fileItem.getFieldName(), fileItem.getContentType());
                continue;
            }
            CommonsMultipartFile file = this.createMultipartFile(fileItem);
            multipartFiles.add((Object)file.getName(), (Object)file);
            LogFormatUtils.traceDebug((Log)this.logger, traceOn -> "Part '" + file.getName() + "', size " + file.getSize() + " bytes, filename='" + file.getOriginalFilename() + "'" + (traceOn != false ? ", storage=" + file.getStorageDescription() : ""));
        }
        return new MultipartParsingResult((MultiValueMap<String, MultipartFile>)multipartFiles, multipartParameters, multipartParameterContentTypes);
    }

    protected CommonsMultipartFile createMultipartFile(FileItem fileItem) {
        CommonsMultipartFile multipartFile = new CommonsMultipartFile(fileItem);
        multipartFile.setPreserveFilename(this.preserveFilename);
        return multipartFile;
    }

    protected void cleanupFileItems(MultiValueMap<String, MultipartFile> multipartFiles) {
        for (List files : multipartFiles.values()) {
            for (MultipartFile file : files) {
                if (!(file instanceof CommonsMultipartFile)) continue;
                CommonsMultipartFile cmf = (CommonsMultipartFile)file;
                cmf.getFileItem().delete();
                LogFormatUtils.traceDebug((Log)this.logger, traceOn -> "Cleaning up part '" + cmf.getName() + "', filename '" + cmf.getOriginalFilename() + "'" + (traceOn != false ? ", stored " + cmf.getStorageDescription() : ""));
            }
        }
    }

    private String determineEncoding(String contentTypeHeader, String defaultEncoding) {
        if (!StringUtils.hasText((String)contentTypeHeader)) {
            return defaultEncoding;
        }
        MediaType contentType = MediaType.parseMediaType(contentTypeHeader);
        Charset charset = contentType.getCharset();
        return charset != null ? charset.name() : defaultEncoding;
    }

    protected static class MultipartParsingResult {
        private final MultiValueMap<String, MultipartFile> multipartFiles;
        private final Map<String, String[]> multipartParameters;
        private final Map<String, String> multipartParameterContentTypes;

        public MultipartParsingResult(MultiValueMap<String, MultipartFile> mpFiles, Map<String, String[]> mpParams, Map<String, String> mpParamContentTypes) {
            this.multipartFiles = mpFiles;
            this.multipartParameters = mpParams;
            this.multipartParameterContentTypes = mpParamContentTypes;
        }

        public MultiValueMap<String, MultipartFile> getMultipartFiles() {
            return this.multipartFiles;
        }

        public Map<String, String[]> getMultipartParameters() {
            return this.multipartParameters;
        }

        public Map<String, String> getMultipartParameterContentTypes() {
            return this.multipartParameterContentTypes;
        }
    }
}


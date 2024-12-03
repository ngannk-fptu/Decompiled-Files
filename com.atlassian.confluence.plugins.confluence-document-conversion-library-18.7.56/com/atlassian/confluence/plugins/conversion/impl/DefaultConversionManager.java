/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.pages.Attachment
 *  com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService
 *  com.atlassian.plugins.conversion.convert.FileFormat
 *  com.atlassian.plugins.conversion.convert.image.AbstractConverter
 *  com.atlassian.plugins.conversion.convert.image.CellsConverter
 *  com.atlassian.plugins.conversion.convert.image.ImagingConverter
 *  com.atlassian.plugins.conversion.convert.image.SlidesConverter
 *  com.atlassian.plugins.conversion.convert.image.WordsConverter
 *  com.sun.jersey.api.model.AbstractResource
 *  com.sun.jersey.api.model.AbstractSubResourceMethod
 *  com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.confluence.plugins.conversion.impl;

import com.atlassian.confluence.pages.Attachment;
import com.atlassian.confluence.plugins.conversion.annotation.ConversionPath;
import com.atlassian.confluence.plugins.conversion.api.ConversionManager;
import com.atlassian.confluence.plugins.conversion.api.ConversionResult;
import com.atlassian.confluence.plugins.conversion.api.ConversionResultSupplier;
import com.atlassian.confluence.plugins.conversion.api.ConversionType;
import com.atlassian.confluence.plugins.conversion.impl.ConversionManagerInternal;
import com.atlassian.confluence.plugins.conversion.impl.LocalFileSystemConversionResultSupplier;
import com.atlassian.confluence.plugins.conversion.rest.ConfluenceConversionServiceResource;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugins.conversion.convert.FileFormat;
import com.atlassian.plugins.conversion.convert.image.AbstractConverter;
import com.atlassian.plugins.conversion.convert.image.CellsConverter;
import com.atlassian.plugins.conversion.convert.image.ImagingConverter;
import com.atlassian.plugins.conversion.convert.image.SlidesConverter;
import com.atlassian.plugins.conversion.convert.image.WordsConverter;
import com.sun.jersey.api.model.AbstractResource;
import com.sun.jersey.api.model.AbstractSubResourceMethod;
import com.sun.jersey.server.impl.modelapi.annotation.IntrospectionModeller;
import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@ExportAsService(value={ConversionManager.class})
@Component(value="conversionManager")
public class DefaultConversionManager
implements ConversionManagerInternal {
    private static final Logger log = LoggerFactory.getLogger(DefaultConversionManager.class);
    private static final String BASE_REST_PATH = "/rest/documentConversion/latest/";
    private static final Map<ConversionType, String> URL_PREFIXES = new HashMap<ConversionType, String>();
    private static final Map<ConversionType, String> BATCH_URL_PREFIXES = new HashMap<ConversionType, String>();
    private AbstractConverter[] converters = null;
    private final ConversionResultSupplier localFileSystemConversionResultSupplier;

    @Autowired
    public DefaultConversionManager(LocalFileSystemConversionResultSupplier localFileSystemConversionResultSupplier) {
        this.localFileSystemConversionResultSupplier = Objects.requireNonNull(localFileSystemConversionResultSupplier);
        localFileSystemConversionResultSupplier.setConversionManager(this);
    }

    @Override
    public void init() {
        Instant start = Instant.now();
        this.getConverters();
        Instant end = Instant.now();
        Duration loadTime = Duration.between(start, end);
        if (loadTime.minus(Duration.ofMinutes(1L)).isNegative()) {
            log.debug("ConversionManager initialised in {}s", (Object)loadTime.getSeconds());
        } else {
            log.info("ConversionManager initialised in {}m {}s", (Object)loadTime.toMinutes(), (Object)(loadTime.getSeconds() % 60L));
        }
    }

    @Override
    public boolean isConvertible(FileFormat fileFormat) {
        AbstractConverter[] converters;
        if (fileFormat == null) {
            return false;
        }
        for (AbstractConverter converter : converters = this.getConverters()) {
            if (!converter.handlesFileFormat(fileFormat)) continue;
            return true;
        }
        return false;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public AbstractConverter[] getConverters() {
        DefaultConversionManager defaultConversionManager = this;
        synchronized (defaultConversionManager) {
            if (this.converters == null) {
                this.converters = new AbstractConverter[]{new ImagingConverter(), new SlidesConverter(), new WordsConverter(), new CellsConverter()};
            }
        }
        return this.converters;
    }

    @Override
    public FileFormat getFileFormat(Attachment attachment) {
        if (attachment == null) {
            return null;
        }
        String contentType = attachment.getMediaType();
        if (contentType.isEmpty()) {
            return null;
        }
        String lowerContentType = contentType.toLowerCase();
        FileFormat tempFileFormat = FileFormat.fromMimeType((String)contentType.toLowerCase());
        if (tempFileFormat == null) {
            if (lowerContentType.equals("application/octet-stream") || lowerContentType.equals("application/x-upload-data")) {
                tempFileFormat = FileFormat.fromFileName((String)attachment.getFileName());
            }
            if (tempFileFormat == null) {
                return null;
            }
        }
        return tempFileFormat;
    }

    @Override
    public ConversionResult getConversionResult(Attachment attachment, ConversionType conversionType) {
        return this.localFileSystemConversionResultSupplier.getConversionResult(attachment, conversionType);
    }

    @Override
    public String getConversionUrl(long attachmentId, int version, ConversionType conversionType) {
        return URL_PREFIXES.get((Object)conversionType) + attachmentId + "/" + version;
    }

    @Override
    public String getBatchConversionUrl(ConversionType conversionType) {
        return BATCH_URL_PREFIXES.get((Object)conversionType);
    }

    static {
        AbstractResource resource = IntrospectionModeller.createResource(ConfluenceConversionServiceResource.class);
        String baseMethodPath = resource.getPath().getValue();
        for (AbstractSubResourceMethod method : resource.getSubResourceMethods()) {
            ConversionPath conversionPath = (ConversionPath)method.getAnnotation(ConversionPath.class);
            if (conversionPath == null) continue;
            ConversionType conversionType = conversionPath.value();
            String path = method.getPath().getValue();
            if (method.getHttpMethod().equals("GET")) {
                URL_PREFIXES.put(conversionType, BASE_REST_PATH + baseMethodPath + "/" + path.substring(0, path.indexOf(123)));
                continue;
            }
            if (!method.getHttpMethod().equals("POST")) continue;
            BATCH_URL_PREFIXES.put(conversionType, BASE_REST_PATH + baseMethodPath + "/" + path);
        }
    }
}


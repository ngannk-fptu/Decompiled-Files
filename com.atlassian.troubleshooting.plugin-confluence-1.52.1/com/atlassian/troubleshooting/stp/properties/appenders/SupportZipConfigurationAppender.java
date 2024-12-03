/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.properties.appenders;

import com.atlassian.troubleshooting.api.supportzip.SupportZipBundle;
import com.atlassian.troubleshooting.stp.request.SupportZipContext;
import com.atlassian.troubleshooting.stp.request.SupportZipCreationRequest;
import com.atlassian.troubleshooting.stp.salext.bundle.SupportZipBundleAccessor;
import com.atlassian.troubleshooting.stp.spi.RootLevelSupportDataAppender;
import com.atlassian.troubleshooting.stp.spi.SupportDataBuilder;
import com.atlassian.troubleshooting.stp.spi.SupportDataDetail;
import java.util.List;
import java.util.Objects;
import org.springframework.beans.factory.annotation.Autowired;

public class SupportZipConfigurationAppender
extends RootLevelSupportDataAppender {
    private static final String UNLIMITED = "Unlimited";
    private static final String ZIP_CONFIGURATION = "stp.properties.zip.configuration";
    private static final String ZIP_FILE_LIMIT = "stp.properties.zip.configuration.file.size.limit";
    private static final String FILE_CONSTRAINT_LASTMODIFIED = "stp.properties.zip.configuration.file.constraint.lastModified";
    private static final String BUNDLE_OPTIONS = "stp.properties.zip.configuration.bundle.options";
    private final SupportZipBundleAccessor bundleAccessor;

    @Autowired
    public SupportZipConfigurationAppender(SupportZipBundleAccessor bundleAccessor) {
        this.bundleAccessor = bundleAccessor;
    }

    @Override
    protected void addSupportData(SupportDataBuilder builder) {
        if (!SupportDataDetail.FULL.equals((Object)builder.getBuilderContext().getRequestDetail())) {
            return;
        }
        SupportZipCreationRequest request = SupportZipContext.getSupportZipRequest().orElseThrow(() -> new IllegalStateException("Can't add zip creation options to application.xml. Support zip request context should exist."));
        SupportDataBuilder requestOptions = builder.addCategory(ZIP_CONFIGURATION);
        this.addFileSizeLimit(requestOptions, request);
        this.addFileConstrainLastModified(requestOptions, request);
        this.addAllBundles(requestOptions, request, this.bundleAccessor.getBundles());
    }

    private void addFileSizeLimit(SupportDataBuilder builder, SupportZipCreationRequest request) {
        String fileLimit = request.getMaxMegaBytesPerFile().map(Objects::toString).orElse(UNLIMITED);
        builder.addValue(ZIP_FILE_LIMIT, fileLimit);
    }

    private void addFileConstrainLastModified(SupportDataBuilder builder, SupportZipCreationRequest request) {
        String fileConstraintLastModified = request.getFileConstraintLastModified().map(Object::toString).orElse(UNLIMITED);
        builder.addValue(FILE_CONSTRAINT_LASTMODIFIED, fileConstraintLastModified);
    }

    private void addAllBundles(SupportDataBuilder builder, SupportZipCreationRequest request, List<SupportZipBundle> allBundles) {
        SupportDataBuilder bundleOptions = builder.addCategory(BUNDLE_OPTIONS);
        for (SupportZipBundle bundle : allBundles) {
            bundleOptions.addValue(bundle.getKey(), String.valueOf(this.isBundleInRequest(request, bundle)));
        }
    }

    private boolean isBundleInRequest(SupportZipCreationRequest request, SupportZipBundle bundle) {
        return request.getBundles().stream().anyMatch(b -> b.getKey().equals(bundle.getKey()));
    }
}


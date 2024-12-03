/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.springframework.util.ObjectUtils
 */
package com.atlassian.business.insights.core.rest.validation.validators.bodyparam;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.business.insights.core.rest.model.ConfigExportPathRequest;
import com.atlassian.business.insights.core.rest.validation.RequestBodyValidator;
import com.atlassian.business.insights.core.rest.validation.ValidationResult;
import java.nio.file.InvalidPathException;
import java.nio.file.Paths;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.util.ObjectUtils;

public class ExportPathValidator
implements RequestBodyValidator {
    @VisibleForTesting
    public static final String INVALID_DIRECTORY_KEY = "data-pipeline.api.rest.config.error.invalid.root.directory.path";
    @VisibleForTesting
    public static final String INVALID_REQUEST_BODY_KEY = "data-pipeline.api.rest.config.request.body.export.path.invalid";
    @VisibleForTesting
    public static final String REQUIRED_REQUEST_BODY_KEY = "data-pipeline.api.rest.config.request.body.export.path.required";
    @VisibleForTesting
    public static final String REQUIRED_REQUEST_BODY_PATH_LOCATION_KEY = "data-pipeline.api.rest.config.request.body.export.path.location.required";
    @VisibleForTesting
    public static final String INVALID_EXPORT_PATH_SHOULD_BE_ABSOLUTE_KEY = "data-pipeline.api.rest.config.request.body.export.path.should.be.absolute";

    @Override
    public void validate(@Nullable Object[] bodyContent, @Nonnull ValidationResult validationResult) {
        if (bodyContent == null || ExportPathValidator.isBodyContentEmpty(bodyContent)) {
            validationResult.add(REQUIRED_REQUEST_BODY_KEY);
        } else {
            try {
                ConfigExportPathRequest exportPathRequest = (ConfigExportPathRequest)new ObjectMapper().readValue((String)bodyContent[0], ConfigExportPathRequest.class);
                this.validateExportPathLocation(validationResult, exportPathRequest);
            }
            catch (InvalidPathException e) {
                validationResult.add(INVALID_DIRECTORY_KEY);
            }
            catch (Exception e) {
                validationResult.add(INVALID_REQUEST_BODY_KEY);
            }
        }
    }

    static boolean isBodyContentEmpty(Object[] bodyContent) {
        return ArrayUtils.isEmpty((Object[])bodyContent) || ObjectUtils.isEmpty((Object)bodyContent[0]) || StringUtils.isBlank((CharSequence)String.valueOf(bodyContent[0]));
    }

    private void validateExportPathLocation(ValidationResult validationResult, ConfigExportPathRequest exportPathRequest) {
        if (StringUtils.isBlank((CharSequence)exportPathRequest.getPath())) {
            validationResult.add(REQUIRED_REQUEST_BODY_PATH_LOCATION_KEY);
        } else if (this.isRelativePath(exportPathRequest)) {
            validationResult.add(INVALID_EXPORT_PATH_SHOULD_BE_ABSOLUTE_KEY);
        }
    }

    private boolean isRelativePath(ConfigExportPathRequest exportPathRequest) throws InvalidPathException {
        return !Paths.get(exportPathRequest.getPath(), new String[0]).isAbsolute();
    }
}


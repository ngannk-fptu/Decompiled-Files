/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.net.NonMarshallingRequestFactory
 *  com.atlassian.sal.api.net.Request
 *  com.atlassian.sal.api.net.Request$MethodType
 *  com.atlassian.sal.api.net.ResponseException
 *  javax.annotation.Nonnull
 *  javax.annotation.Nullable
 *  javax.annotation.ParametersAreNonnullByDefault
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.io.IOUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.stp.hercules.regex.cacheables;

import com.atlassian.sal.api.net.NonMarshallingRequestFactory;
import com.atlassian.sal.api.net.Request;
import com.atlassian.sal.api.net.ResponseException;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.ResultWithFallback;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResource;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

@ParametersAreNonnullByDefault
public class SavedExternalResourceService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SavedExternalResourceService.class);
    private static final String TESTING_HERCULES_REGEX_URL = "hercules.regex.url";
    private static final String DIRECTORY_FOR_OFFLINE_FILES = "xml/files-saved-for-offline-access";
    public static final String RESOURCES_DIRECTORY_LOCATION_RELATIVE_TO_ATST_ROOT = "common/src/main/resources/xml/files-saved-for-offline-access";
    private final NonMarshallingRequestFactory<? extends Request<?, ?>> factory;
    private final ExternalResourceHelper externalResourceHelper;

    @Autowired
    public SavedExternalResourceService(NonMarshallingRequestFactory<? extends Request<?, ?>> httpClientRequestFactory, ExternalResourceHelper externalResourceHelper) {
        this.factory = Objects.requireNonNull(httpClientRequestFactory);
        this.externalResourceHelper = Objects.requireNonNull(externalResourceHelper);
    }

    @Nonnull
    public ResultWithFallback<String> resolve(SavedExternalResource savedExternalResource) {
        Objects.requireNonNull(savedExternalResource);
        String url = this.externalResourceHelper.returnTestingRegexUrlOrSupplied(savedExternalResource.getCachedUrl().toString());
        try {
            return new ResultWithFallback<Object>(false, this.factory.createRequest(Request.MethodType.GET, url).executeAndReturn(savedExternalResource::parseResponse));
        }
        catch (ResponseException e) {
            return new ResultWithFallback<String>(true, this.resolveFromClassloader(savedExternalResource));
        }
    }

    @Nonnull
    public String resolveFromClassloader(SavedExternalResource savedExternalResource) {
        Objects.requireNonNull(savedExternalResource);
        try {
            return this.externalResourceHelper.getResourceAsStreamAndLoad("xml/files-saved-for-offline-access/" + savedExternalResource.getLocalFilename());
        }
        catch (IOException e1) {
            throw new RuntimeException(e1);
        }
    }

    public Optional<String> save(SavedExternalResource savedExternalResource, @Nullable Consumer<File> postProcessor) {
        File savedExternalResourceDirectory = this.externalResourceHelper.newFile(RESOURCES_DIRECTORY_LOCATION_RELATIVE_TO_ATST_ROOT);
        if (!savedExternalResourceDirectory.exists()) {
            throw new RuntimeException("You've probably run the program in the wrong directory, The working directory must be the root of the ATST project");
        }
        Objects.requireNonNull(savedExternalResource);
        try {
            File dataFile = new File("common/src/main/resources/xml/files-saved-for-offline-access/" + savedExternalResource.getLocalFilename());
            this.externalResourceHelper.copyUrlToFile(new URL(savedExternalResource.getCachedUrl().toString()), dataFile);
            if (postProcessor != null) {
                postProcessor.accept(dataFile);
            }
            return Optional.empty();
        }
        catch (IOException e) {
            String errorMessage = String.format("Unable to update copy of %s, perhaps it has moved?", savedExternalResource.getCachedUrl().toString());
            LOGGER.error(errorMessage, (Throwable)e);
            return Optional.of(errorMessage);
        }
    }

    public static class ExternalResourceHelper {
        File newFile(String filename) {
            return new File(filename);
        }

        void copyUrlToFile(URL url, File file) throws IOException {
            FileUtils.copyURLToFile((URL)url, (File)file);
        }

        String getResourceAsStreamAndLoad(String filename) throws IOException {
            ClassLoader classLoader = this.getClass().getClassLoader();
            return IOUtils.toString((InputStream)classLoader.getResourceAsStream(filename));
        }

        String returnTestingRegexUrlOrSupplied(String supplied) {
            return System.getProperty(SavedExternalResourceService.TESTING_HERCULES_REGEX_URL, supplied);
        }
    }
}


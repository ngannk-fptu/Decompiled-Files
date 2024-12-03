/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.dc.filestore.api.FileStore$Path
 *  com.google.common.annotations.VisibleForTesting
 *  io.atlassian.util.concurrent.ResettableLazyReference
 *  org.apache.commons.collections.ExtendedProperties
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core.actions;

import com.atlassian.dc.filestore.api.FileStore;
import com.google.common.annotations.VisibleForTesting;
import io.atlassian.util.concurrent.ResettableLazyReference;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import org.apache.commons.collections.ExtendedProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LoginExemptionHelper {
    private static final String FOUR_OH_FOUR_EXEMPTION_PROPERTIES_FILE = "404-login-exemption.properties";
    private static final String FOUR_OH_FOUR_EXEMPTION_KEY = "fourohfour.allow.url.without.login";
    private static final Logger log = LoggerFactory.getLogger(LoginExemptionHelper.class);
    private final FileStore.Path sharedHome;
    private final ResettableLazyReference<Set<String>> exemptedUrlsRef = new ResettableLazyReference<Set<String>>(){

        protected Set<String> create() {
            return LoginExemptionHelper.this.buildExemptedUrlSet();
        }
    };

    public LoginExemptionHelper(FileStore.Path sharedHome) {
        this.sharedHome = Objects.requireNonNull(sharedHome);
    }

    boolean isUrlPathExempted(String urlPath) {
        return ((Set)this.exemptedUrlsRef.get()).contains(urlPath);
    }

    @VisibleForTesting
    public void reset() {
        this.exemptedUrlsRef.reset();
    }

    private Set<String> buildExemptedUrlSet() {
        HashSet<String> exemptedUrlSet = new HashSet<String>();
        FileStore.Path sharedConfigDir = this.sharedHome.path(new String[]{"config"});
        String[] exemptedUrlArray = this.readPropertiesFileFromDir(sharedConfigDir);
        if (!Objects.isNull(exemptedUrlArray)) {
            Collections.addAll(exemptedUrlSet, exemptedUrlArray);
        }
        return exemptedUrlSet;
    }

    private String[] readPropertiesFileFromDir(FileStore.Path sharedConfigDir) {
        ExtendedProperties properties = new ExtendedProperties();
        FileStore.Path exemptionFile = sharedConfigDir.path(new String[]{FOUR_OH_FOUR_EXEMPTION_PROPERTIES_FILE});
        try {
            if (exemptionFile.fileExists()) {
                exemptionFile.fileReader().consume(arg_0 -> ((ExtendedProperties)properties).load(arg_0));
            }
        }
        catch (IOException e) {
            log.error("Cannot load 404 login exemption list", (Throwable)e);
        }
        return properties.getStringArray(FOUR_OH_FOUR_EXEMPTION_KEY);
    }
}


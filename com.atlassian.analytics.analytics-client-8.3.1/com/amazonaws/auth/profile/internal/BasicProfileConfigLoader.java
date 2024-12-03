/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.auth.profile.internal;

import com.amazonaws.SdkClientException;
import com.amazonaws.annotation.SdkInternalApi;
import com.amazonaws.auth.profile.internal.AbstractProfilesConfigFileScanner;
import com.amazonaws.auth.profile.internal.AllProfiles;
import com.amazonaws.auth.profile.internal.BasicProfile;
import com.amazonaws.util.StringUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Scanner;

@SdkInternalApi
public class BasicProfileConfigLoader {
    public static final BasicProfileConfigLoader INSTANCE = new BasicProfileConfigLoader();

    private BasicProfileConfigLoader() {
    }

    public AllProfiles loadProfiles(File file) {
        if (file == null) {
            throw new IllegalArgumentException("Unable to load AWS profiles: specified file is null.");
        }
        if (!file.exists() || !file.isFile()) {
            throw new IllegalArgumentException("AWS credential profiles file not found in the given path: " + file.getAbsolutePath());
        }
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            AllProfiles allProfiles = this.loadProfiles(fis);
            return allProfiles;
        }
        catch (IOException ioe) {
            throw new SdkClientException("Unable to load AWS credential profiles file at: " + file.getAbsolutePath(), ioe);
        }
        finally {
            if (fis != null) {
                try {
                    fis.close();
                }
                catch (IOException iOException) {}
            }
        }
    }

    private AllProfiles loadProfiles(InputStream is) throws IOException {
        ProfilesConfigFileLoaderHelper helper = new ProfilesConfigFileLoaderHelper();
        Map<String, Map<String, String>> allProfileProperties = helper.parseProfileProperties(new Scanner(is, StringUtils.UTF8.name()));
        LinkedHashMap<String, BasicProfile> profilesByName = new LinkedHashMap<String, BasicProfile>();
        for (Map.Entry<String, Map<String, String>> entry : allProfileProperties.entrySet()) {
            String profileName = entry.getKey();
            Map<String, String> properties = entry.getValue();
            this.assertParameterNotEmpty(profileName, "Unable to load properties from profile: Profile name is empty.");
            profilesByName.put(profileName, new BasicProfile(profileName, properties));
        }
        return new AllProfiles(profilesByName);
    }

    private void assertParameterNotEmpty(String parameterValue, String errorMessage) {
        if (StringUtils.isNullOrEmpty(parameterValue)) {
            throw new SdkClientException(errorMessage);
        }
    }

    private static class ProfilesConfigFileLoaderHelper
    extends AbstractProfilesConfigFileScanner {
        protected final Map<String, Map<String, String>> allProfileProperties = new LinkedHashMap<String, Map<String, String>>();

        private ProfilesConfigFileLoaderHelper() {
        }

        public Map<String, Map<String, String>> parseProfileProperties(Scanner scanner) {
            this.allProfileProperties.clear();
            this.run(scanner);
            return new LinkedHashMap<String, Map<String, String>>(this.allProfileProperties);
        }

        @Override
        protected void onEmptyOrCommentLine(String profileName, String line) {
        }

        @Override
        protected void onProfileStartingLine(String newProfileName, String line) {
            this.allProfileProperties.put(newProfileName, new HashMap());
        }

        @Override
        protected void onProfileEndingLine(String prevProfileName) {
        }

        @Override
        protected void onProfileProperty(String profileName, String propertyKey, String propertyValue, boolean isSupportedProperty, String line) {
            Map<String, String> properties = this.allProfileProperties.get(profileName);
            if (properties.containsKey(propertyKey)) {
                throw new IllegalArgumentException("Duplicate property values for [" + propertyKey + "].");
            }
            properties.put(propertyKey, propertyValue);
        }

        @Override
        protected void onEndOfFile() {
        }
    }
}


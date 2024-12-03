/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.crowd.service.client;

import com.atlassian.crowd.service.client.BaseResourceLocator;
import java.io.File;

public class ClientResourceLocator
extends BaseResourceLocator {
    public ClientResourceLocator(String resourceName) {
        this(resourceName, null);
    }

    public ClientResourceLocator(String resourceName, String configurationDir) {
        super(resourceName);
        this.propertyFileLocation = this.findPropertyFileLocation(configurationDir);
    }

    private String findPropertyFileLocation(String directory) {
        String location = this.getResourceLocationFromSystemProperty();
        if (location == null) {
            location = this.getResourceLocationFromDirectory(directory);
        }
        if (location == null) {
            location = this.getResourceLocationFromClassPath();
        }
        return location;
    }

    private String getResourceLocationFromDirectory(String directory) {
        if (directory == null) {
            return null;
        }
        String fileLocation = new File(directory, this.getResourceName()).getPath();
        return this.formatFileLocation(fileLocation, false);
    }
}


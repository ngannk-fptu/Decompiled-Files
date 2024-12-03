/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.io.Resource
 *  org.springframework.util.DigestUtils
 *  org.springframework.util.FileCopyUtils
 */
package org.springframework.web.servlet.resource;

import java.io.IOException;
import java.io.InputStream;
import org.springframework.core.io.Resource;
import org.springframework.util.DigestUtils;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.resource.AbstractVersionStrategy;

public class ContentVersionStrategy
extends AbstractVersionStrategy {
    public ContentVersionStrategy() {
        super(new AbstractVersionStrategy.FileNameVersionPathStrategy());
    }

    @Override
    public String getResourceVersion(Resource resource) {
        try {
            byte[] content = FileCopyUtils.copyToByteArray((InputStream)resource.getInputStream());
            return DigestUtils.md5DigestAsHex((byte[])content);
        }
        catch (IOException ex) {
            throw new IllegalStateException("Failed to calculate hash for " + resource, ex);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import com.atlassian.confluence.api.impl.service.audit.uri.ResourceUriGenerator;
import com.atlassian.confluence.api.impl.service.audit.uri.UriGeneratorHelper;
import com.atlassian.confluence.internal.pages.AttachmentManagerInternal;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;

public class AttachmentUriGenerator
implements ResourceUriGenerator {
    private AttachmentManagerInternal attachmentManager;

    public AttachmentUriGenerator(AttachmentManagerInternal attachmentManager) {
        this.attachmentManager = attachmentManager;
    }

    @Override
    public Map<String, URI> generate(URI baseUrl, Set<String> identifiers) {
        return identifiers.stream().filter(NumberUtils::isParsable).mapToLong(Long::valueOf).mapToObj(this.attachmentManager::getAttachment).filter(Objects::nonNull).filter(attachment -> attachment.getContainer() != null).collect(Collectors.toMap(attachment -> String.valueOf(attachment.getId()), attachment -> UriGeneratorHelper.contentUri(baseUrl, attachment)));
    }
}


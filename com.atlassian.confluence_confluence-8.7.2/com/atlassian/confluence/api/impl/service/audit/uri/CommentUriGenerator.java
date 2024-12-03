/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import com.atlassian.confluence.api.impl.service.audit.uri.ResourceUriGenerator;
import com.atlassian.confluence.api.impl.service.audit.uri.UriGeneratorHelper;
import com.atlassian.confluence.internal.pages.CommentManagerInternal;
import java.net.URI;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;

public class CommentUriGenerator
implements ResourceUriGenerator {
    private CommentManagerInternal commentManager;

    public CommentUriGenerator(CommentManagerInternal commentManager) {
        this.commentManager = commentManager;
    }

    @Override
    public Map<String, URI> generate(URI baseUrl, Set<String> identifiers) {
        return identifiers.stream().filter(NumberUtils::isParsable).mapToLong(Long::valueOf).mapToObj(this.commentManager::getComment).filter(Objects::nonNull).collect(Collectors.toMap(comment -> String.valueOf(comment.getId()), comment -> UriGeneratorHelper.contentUri(baseUrl, comment)));
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.pagination.LimitedRequestImpl
 *  org.apache.commons.lang3.math.NumberUtils
 */
package com.atlassian.confluence.api.impl.service.audit.uri;

import com.atlassian.confluence.api.impl.service.audit.uri.ResourceUriGenerator;
import com.atlassian.confluence.api.impl.service.audit.uri.UriGeneratorHelper;
import com.atlassian.confluence.api.model.pagination.LimitedRequestImpl;
import com.atlassian.confluence.internal.spaces.SpaceManagerInternal;
import com.atlassian.confluence.spaces.SpacesQuery;
import java.net.URI;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.apache.commons.lang3.math.NumberUtils;

public class SpaceUriGenerator
implements ResourceUriGenerator {
    private final SpaceManagerInternal spaceManager;

    public SpaceUriGenerator(SpaceManagerInternal spaceManager) {
        this.spaceManager = spaceManager;
    }

    @Override
    public Map<String, URI> generate(URI baseUrl, Set<String> identifiers) {
        Set<Long> parsedIds = identifiers.stream().filter(NumberUtils::isParsable).map(Long::valueOf).collect(Collectors.toSet());
        return this.spaceManager.getSpaces(SpacesQuery.newQuery().withSpaceIds(parsedIds).build(), LimitedRequestImpl.create((int)parsedIds.size()), x -> true).getResults().stream().collect(Collectors.toMap(space -> String.valueOf(space.getId()), space -> UriGeneratorHelper.contentUri(baseUrl, space)));
    }
}


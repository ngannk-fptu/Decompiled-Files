/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansions
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 */
package com.atlassian.confluence.plugins.files.notifications;

import com.atlassian.confluence.api.model.Expansions;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;

public class FileContentExpansions {
    public static final Expansions FILE_CONTENT_EXPANSIONS = ExpansionsParser.parse((String[])new String[]{"container.space", "version", "metadata"});
    public static final Expansions DESCENDANT_CONTENT_EXPANSIONS = ExpansionsParser.parse((String[])new String[]{"body." + ContentRepresentation.VIEW, "ancestors.body." + ContentRepresentation.VIEW, "ancestors.history", "history"});
    public static final Expansions CONTAINER_CONTENT_EXPANSIONS = ExpansionsParser.parseAsExpansions((String)"space");
}


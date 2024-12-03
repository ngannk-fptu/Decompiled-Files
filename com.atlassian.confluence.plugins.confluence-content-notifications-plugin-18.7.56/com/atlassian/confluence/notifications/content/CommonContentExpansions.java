/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.Expansion
 *  com.atlassian.confluence.api.model.content.ContentRepresentation
 *  com.atlassian.confluence.rest.api.model.ExpansionsParser
 */
package com.atlassian.confluence.notifications.content;

import com.atlassian.confluence.api.model.Expansion;
import com.atlassian.confluence.api.model.content.ContentRepresentation;
import com.atlassian.confluence.rest.api.model.ExpansionsParser;

public final class CommonContentExpansions {
    public static final Expansion SPACE = ExpansionsParser.parseSingle((String)"space");
    public static final Expansion ANON_EXPORT_BODY = ExpansionsParser.parseSingle((String)("body." + ContentRepresentation.ANONYMOUS_EXPORT_VIEW.getRepresentation()));
    public static final Expansion EXPORT_BODY = ExpansionsParser.parseSingle((String)("body." + ContentRepresentation.EXPORT_VIEW.getRepresentation()));
    public static final Expansion CONTAINER = ExpansionsParser.parseSingle((String)"container");
    public static final Expansion ANCESTORS = ExpansionsParser.parseSingle((String)"ancestors");
    public static final Expansion HISTORY = ExpansionsParser.parseSingle((String)"history");
    public static final Expansion METADATA = ExpansionsParser.parseSingle((String)"metadata");
    public static final Expansion VERSION = ExpansionsParser.parseSingle((String)"version");

    private CommonContentExpansions() {
    }
}


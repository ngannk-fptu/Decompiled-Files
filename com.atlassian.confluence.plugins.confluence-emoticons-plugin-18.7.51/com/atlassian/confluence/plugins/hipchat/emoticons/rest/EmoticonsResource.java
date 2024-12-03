/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugins.rest.common.security.AnonymousAllowed
 *  com.google.common.collect.ImmutableMap
 *  javax.ws.rs.GET
 *  javax.ws.rs.Path
 *  javax.ws.rs.Produces
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.beans.factory.annotation.Qualifier
 */
package com.atlassian.confluence.plugins.hipchat.emoticons.rest;

import com.atlassian.confluence.plugins.hipchat.emoticons.rest.AtlaskitEmoticonModel;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.ConfluenceEmoticonService;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.CustomEmoticonService;
import com.atlassian.confluence.plugins.hipchat.emoticons.service.TwitterEmoticonService;
import com.atlassian.plugins.rest.common.security.AnonymousAllowed;
import com.google.common.collect.ImmutableMap;
import java.util.ArrayList;
import java.util.Map;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;

@Path(value="/")
public class EmoticonsResource {
    private static final Logger log = LoggerFactory.getLogger(EmoticonsResource.class);
    private final ConfluenceEmoticonService confluenceEmoticonService;
    private final CustomEmoticonService customEmoticonService;
    private final TwitterEmoticonService twitterEmoticonService;
    private static final String INVALID_FILE_TYPE = "INVALID_FILE_TYPE";

    public EmoticonsResource(@Qualifier(value="customEmoticonService") CustomEmoticonService customEmoticonService, @Qualifier(value="twitterEmoticonService") TwitterEmoticonService twitterEmoticonService, ConfluenceEmoticonService confluenceEmoticonService) {
        this.customEmoticonService = customEmoticonService;
        this.twitterEmoticonService = twitterEmoticonService;
        this.confluenceEmoticonService = confluenceEmoticonService;
    }

    @GET
    @AnonymousAllowed
    @Produces(value={"application/json"})
    public Map<String, Object> get() {
        ArrayList<AtlaskitEmoticonModel> atlaskitEmoticonModels = new ArrayList<AtlaskitEmoticonModel>();
        atlaskitEmoticonModels.addAll(this.confluenceEmoticonService.orderedList());
        atlaskitEmoticonModels.addAll(this.twitterEmoticonService.list());
        return ImmutableMap.builder().put((Object)"emojis", (Object)atlaskitEmoticonModels.toArray()).build();
    }
}


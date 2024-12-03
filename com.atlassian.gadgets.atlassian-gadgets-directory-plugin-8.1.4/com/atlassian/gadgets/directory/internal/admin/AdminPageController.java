/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpec
 *  com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed
 *  com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.soy.renderer.SoyException
 *  com.atlassian.soy.renderer.SoyTemplateRenderer
 *  com.google.common.base.Function
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Lists
 *  com.google.common.collect.Maps
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.stereotype.Component
 */
package com.atlassian.gadgets.directory.internal.admin;

import com.atlassian.gadgets.directory.spi.ExternalGadgetSpec;
import com.atlassian.gadgets.directory.spi.ExternalGadgetSpecStore;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeed;
import com.atlassian.gadgets.directory.spi.SubscribedGadgetFeedStore;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.soy.renderer.SoyException;
import com.atlassian.soy.renderer.SoyTemplateRenderer;
import com.google.common.base.Function;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.Writer;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component(value="adminPageController")
public class AdminPageController {
    private static final String FEED_SUFFIX = "rest/gadgets/1.0/g/feed";
    static final String TEMPLATE_RESOURCE_KEY = "com.atlassian.gadgets.directory:gadget-directory-admin-server-templates";
    static final String ADMIN_TEMPLATE_NAME = "Gadgets.Templates.Directory.Admin.main";
    static final String ERROR_TEMPLATE_NAME = "Gadgets.Templates.Directory.Admin.unauthorisedErrorPage";
    private final SoyTemplateRenderer soyRenderer;
    private final ExternalGadgetSpecStore externalGadgetSpecStore;
    private final SubscribedGadgetFeedStore subscribedGadgetFeedStore;

    @Autowired
    public AdminPageController(@ComponentImport SoyTemplateRenderer soyRenderer, @ComponentImport(value="externalGadgetSpecStore") ExternalGadgetSpecStore externalGadgetSpecStore, @ComponentImport SubscribedGadgetFeedStore subscribedGadgetFeedStore) {
        this.soyRenderer = soyRenderer;
        this.externalGadgetSpecStore = externalGadgetSpecStore;
        this.subscribedGadgetFeedStore = subscribedGadgetFeedStore;
    }

    public void renderAdminPage(Writer writer) throws IOException {
        HashMap data = Maps.newHashMap();
        data.put("gadgets", this.getExternalGadgets());
        data.put("subscriptions", this.getSubscribedFeeds());
        this.render(TEMPLATE_RESOURCE_KEY, ADMIN_TEMPLATE_NAME, data, writer);
    }

    public void renderErrorPage(Writer writer) throws IOException {
        this.render(TEMPLATE_RESOURCE_KEY, ERROR_TEMPLATE_NAME, Collections.emptyMap(), writer);
    }

    private void render(String templateResourceKey, String templateName, Map<String, Object> data, Writer writer) throws IOException {
        String html;
        try {
            html = this.soyRenderer.render(templateResourceKey, templateName, data);
        }
        catch (SoyException e) {
            throw new RuntimeException(e);
        }
        writer.write(html);
    }

    private List<Map<String, Object>> getExternalGadgets() {
        return Lists.newArrayList((Iterable)Iterables.transform((Iterable)this.externalGadgetSpecStore.entries(), (Function)new Function<ExternalGadgetSpec, Map<String, Object>>(){

            public Map<String, Object> apply(ExternalGadgetSpec externalGadgetSpec) {
                HashMap map = Maps.newHashMap();
                map.put("uri", externalGadgetSpec.getSpecUri());
                map.put("id", externalGadgetSpec.getId().value());
                return map;
            }
        }));
    }

    private List<Map<String, Object>> getSubscribedFeeds() {
        return Lists.newArrayList((Iterable)Iterables.transform((Iterable)this.subscribedGadgetFeedStore.getAllFeeds(), (Function)new Function<SubscribedGadgetFeed, Map<String, Object>>(){

            public Map<String, Object> apply(SubscribedGadgetFeed subscribedGadgetFeed) {
                HashMap map = Maps.newHashMap();
                String feedUri = subscribedGadgetFeed.getUri().toString();
                map.put("uri", StringUtils.removeEnd((String)feedUri, (String)AdminPageController.FEED_SUFFIX));
                map.put("id", subscribedGadgetFeed.getId());
                return map;
            }
        }));
    }
}


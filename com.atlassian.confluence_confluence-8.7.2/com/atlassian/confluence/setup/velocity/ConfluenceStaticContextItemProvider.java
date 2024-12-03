/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.core.util.FileUtils
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.opensymphony.util.TextUtils
 *  edu.umd.cs.findbugs.annotations.SuppressFBWarnings
 *  io.atlassian.util.concurrent.LazyReference
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.setup.velocity;

import com.atlassian.confluence.content.render.xhtml.PolicyConfiguredCleaner;
import com.atlassian.confluence.core.ConfluenceSystemProperties;
import com.atlassian.confluence.impl.util.DecoratorUtil;
import com.atlassian.confluence.setup.BuildInformation;
import com.atlassian.confluence.setup.settings.ConfluenceFlavour;
import com.atlassian.confluence.setup.velocity.VelocityContextItemProvider;
import com.atlassian.confluence.spaces.SpaceUtils;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.SafeGeneralUtil;
import com.atlassian.confluence.util.SeraphUtils;
import com.atlassian.core.util.FileUtils;
import com.google.common.collect.ImmutableMap;
import com.opensymphony.util.TextUtils;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import io.atlassian.util.concurrent.LazyReference;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.function.Supplier;
import org.apache.commons.lang3.StringUtils;

public final class ConfluenceStaticContextItemProvider
implements VelocityContextItemProvider {
    private static final Supplier<Map<String, Object>> mapRef = new LazyReference<Map<String, Object>>(){

        protected Map<String, Object> create() throws Exception {
            return ContextItems.createMap();
        }
    };

    @Override
    @SuppressFBWarnings(value={"NP_NULL_ON_SOME_PATH_FROM_RETURN_VALUE"})
    public Map<String, Object> getContextMap() {
        return mapRef.get();
    }

    public static enum ContextItems {
        HTML_UTIL("htmlUtil", () -> HtmlUtil.INSTANCE),
        GENERAL_UTIL("generalUtil", () -> SafeGeneralUtil.INSTANCE),
        TEXT_UTIL("textUtil", TextUtils::new),
        FILE_UTIL("fileUtil", FileUtils::new),
        SERAPH_UTIL("seraph", SeraphUtils::new),
        STRING_UTILS("stringUtils", StringUtils::new),
        SPACE_UTILS("spaceUtils", SpaceUtils::new),
        SYSTEM_PROPERTIES("systemProperties", ConfluenceSystemProperties::new),
        BUILD_INFO("buildInfo", () -> BuildInformation.INSTANCE),
        DECORATOR_UTIL("decoratorUtil", DecoratorUtil::new),
        RENDERED_CONTENT_CLEANER("renderedContentCleaner", PolicyConfiguredCleaner::createRenderedContentCleaner),
        FLAVOUR("confluenceFlavour", () -> ConfluenceFlavour.VANILLA),
        WEBWORK("webwork", () -> HtmlUtil.INSTANCE);

        private final String key;
        private final Callable<?> factory;

        private ContextItems(String key, Callable<?> factory) {
            this.key = key;
            this.factory = factory;
        }

        public String getKey() {
            return this.key;
        }

        static Map<String, Object> createMap() throws Exception {
            ImmutableMap.Builder builder = ImmutableMap.builder();
            for (ContextItems item : ContextItems.values()) {
                builder.put((Object)item.key, item.factory.call());
            }
            return builder.build();
        }
    }
}


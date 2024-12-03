/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.util.ClassLoaderUtils
 *  com.google.common.base.CharMatcher
 *  com.google.common.base.Splitter
 *  com.google.common.collect.ImmutableMap
 *  com.google.common.collect.ImmutableMap$Builder
 *  com.google.common.io.LineProcessor
 *  com.google.common.io.Resources
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.core;

import com.atlassian.confluence.core.MimetypesExtensionTranslationMapFactory;
import com.atlassian.plugin.util.ClassLoaderUtils;
import com.google.common.base.CharMatcher;
import com.google.common.base.Splitter;
import com.google.common.collect.ImmutableMap;
import com.google.common.io.LineProcessor;
import com.google.common.io.Resources;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Iterator;
import java.util.Map;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DefaultMimetypesExtensionTranslationMapFactory
implements MimetypesExtensionTranslationMapFactory {
    private static final Logger log = LoggerFactory.getLogger(DefaultMimetypesExtensionTranslationMapFactory.class);
    private static final Splitter SPLITTER = Splitter.on((CharMatcher)CharMatcher.whitespace()).omitEmptyStrings().trimResults();
    private static final LineProcessor<ImmutableMap<String, String>> LINE_PROCESSOR = new LineProcessor<ImmutableMap<String, String>>(){
        final ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();

        public boolean processLine(String line) {
            if (StringUtils.isBlank((CharSequence)line) || line.startsWith("#")) {
                return true;
            }
            Iterator iter = SPLITTER.split((CharSequence)line).iterator();
            if (iter.hasNext()) {
                String key = (String)iter.next();
                if (iter.hasNext()) {
                    this.builder.put((Object)key, (Object)((String)iter.next()));
                }
            }
            return true;
        }

        public ImmutableMap<String, String> getResult() {
            return this.builder.build();
        }
    };

    @Override
    public Map<String, String> getMimetypeExtensionTranslationMap() {
        URL url = ClassLoaderUtils.getResource((String)"mime.types", this.getClass());
        Object map = ImmutableMap.of();
        try {
            map = (Map)Resources.readLines((URL)url, (Charset)Charset.forName("UTF-8"), LINE_PROCESSOR);
        }
        catch (IOException e) {
            log.error("Unable to parse mime.types", (Throwable)e);
        }
        return map;
    }
}


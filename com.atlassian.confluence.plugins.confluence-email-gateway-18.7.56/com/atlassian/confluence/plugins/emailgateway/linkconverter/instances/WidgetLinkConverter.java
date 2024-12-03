/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier
 *  com.atlassian.confluence.xhtml.api.LinkBody
 *  com.atlassian.confluence.xhtml.api.MacroDefinition
 *  com.google.common.collect.ImmutableList
 */
package com.atlassian.confluence.plugins.emailgateway.linkconverter.instances;

import com.atlassian.confluence.content.render.xhtml.migration.UrlResourceIdentifier;
import com.atlassian.confluence.plugins.emailgateway.api.LinkConverter;
import com.atlassian.confluence.xhtml.api.LinkBody;
import com.atlassian.confluence.xhtml.api.MacroDefinition;
import com.google.common.collect.ImmutableList;
import java.net.URL;
import java.util.Collections;
import java.util.List;
import java.util.regex.Pattern;

public class WidgetLinkConverter
implements LinkConverter<Object, MacroDefinition> {
    private final List<Pattern> patterns = ImmutableList.of((Object)Pattern.compile("youtube.com/watch\\?"), (Object)Pattern.compile("vimeo.com/[0-9]+"), (Object)Pattern.compile("maps.google(.[a-z]+)+/maps"));

    @Override
    public MacroDefinition convert(URL link, LinkBody<Object> linkBody) {
        String href = link.toExternalForm();
        for (Pattern pattern : this.patterns) {
            if (!pattern.matcher(href).find()) continue;
            return MacroDefinition.builder().withName("widget").withStorageVersion("2").withParameters(Collections.singletonMap("url", href)).withTypedParameters(Collections.singletonMap("url", new UrlResourceIdentifier(href))).build();
        }
        return null;
    }

    @Override
    public boolean isFinal() {
        return true;
    }

    @Override
    public Class<MacroDefinition> getConversionClass() {
        return MacroDefinition.class;
    }
}


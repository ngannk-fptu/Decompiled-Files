/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.annotation.Nullable
 *  org.apache.commons.io.IOUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.analytics.client.eventfilter.parser;

import com.atlassian.analytics.client.eventfilter.reader.FilterListReader;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SimpleListParser {
    private static final Logger LOG = LoggerFactory.getLogger(SimpleListParser.class);
    private final FilterListReader filterListReader;

    public SimpleListParser(FilterListReader filterListReader) {
        this.filterListReader = filterListReader;
    }

    /*
     * Enabled aggressive block sorting
     * Enabled unnecessary exception pruning
     * Enabled aggressive exception aggregation
     */
    @Nullable
    public Set<String> readSimpleFilterList(String filterListName) {
        try (InputStream listInputStream = this.filterListReader.readFilterList(filterListName);){
            if (listInputStream != null) {
                LOG.debug("Reading filter list resource, {}", (Object)filterListName);
                Set<String> set = IOUtils.readLines((InputStream)listInputStream, (String)StandardCharsets.UTF_8.name()).stream().filter(StringUtils::isNotBlank).map(String::trim).filter(line -> !line.startsWith("#")).collect(Collectors.toSet());
                return set;
            }
            LOG.debug("Couldn't find filter list resource, {}", (Object)filterListName);
            return null;
        }
        catch (IOException e) {
            LOG.debug("Failed reading filter list resource, {}", (Object)filterListName);
        }
        return null;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.annotations.VisibleForTesting
 *  com.google.common.collect.Iterables
 *  org.codehaus.jackson.map.ObjectMapper
 *  org.codehaus.jackson.type.TypeReference
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.healthcheck.checks.vuln;

import com.atlassian.troubleshooting.healthcheck.checks.vuln.CveProvider;
import com.atlassian.troubleshooting.healthcheck.checks.vuln.model.CveRecord;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.ResultWithFallback;
import com.atlassian.troubleshooting.stp.hercules.regex.cacheables.SavedExternalResourceService;
import com.atlassian.troubleshooting.stp.spi.Version;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.type.TypeReference;
import org.springframework.beans.factory.annotation.Autowired;

public class CveValidator {
    @VisibleForTesting
    static final ObjectMapper MAPPER = new ObjectMapper();
    private final SavedExternalResourceService savedExternalResourceService;
    private List<CveProvider> cveProviders;

    @Autowired
    public CveValidator(SavedExternalResourceService savedExternalResourceService, List<CveProvider> cveProviders) {
        this.savedExternalResourceService = Objects.requireNonNull(savedExternalResourceService);
        this.cveProviders = Objects.requireNonNull(cveProviders);
    }

    public ResultWithFallback<Iterable<CveRecord>> validateCves() {
        return ResultWithFallback.allOf(this.cveProviders.stream().map(this::validateCves).collect(Collectors.toList())).map(Iterables::concat);
    }

    private ResultWithFallback<List<CveRecord>> validateCves(CveProvider provider) {
        return provider.getCpeVersion().map(version -> this.savedExternalResourceService.resolve(provider.getResource()).map(CveValidator::parseJson).map(cves -> this.filter((List<CveRecord>)cves, (String)version))).orElse(new ResultWithFallback(false, Collections.emptyList()));
    }

    private List<CveRecord> filter(List<CveRecord> records, String versionToMatch) {
        Version version = Version.of(versionToMatch);
        return records.stream().filter(r -> r.matchesVersion(version)).collect(Collectors.toList());
    }

    static List<CveRecord> parseJson(String json) {
        try {
            return (List)MAPPER.readValue(json, (TypeReference)new TypeReference<List<CveRecord>>(){});
        }
        catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}


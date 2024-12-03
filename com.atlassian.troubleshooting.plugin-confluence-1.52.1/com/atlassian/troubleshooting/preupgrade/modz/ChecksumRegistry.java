/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import com.atlassian.troubleshooting.preupgrade.modz.ChecksumResource;
import com.atlassian.troubleshooting.preupgrade.modz.VersionChecksumsDto;
import java.io.IOException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.beans.factory.annotation.Autowired;

public class ChecksumRegistry {
    private final ChecksumResource resource;

    @Autowired
    public ChecksumRegistry(ChecksumResource resource) {
        this.resource = Objects.requireNonNull(resource);
    }

    public List<String> getVersionSpecificChecksums(String applicationVersion) {
        return this.resource.getChecksums().stream().filter(versionModz -> applicationVersion.equals(versionModz.getVersion())).findFirst().map(VersionChecksumsDto::getChecksums).orElse(Collections.emptyList());
    }

    public List<VersionChecksumsDto> getAllVersions() {
        return this.resource.getChecksums().stream().sorted(Comparator.reverseOrder()).collect(Collectors.toList());
    }

    public void addChecksums(List<VersionChecksumsDto> versions) throws IOException {
        this.resource.writeChecksums(Stream.concat(this.resource.getChecksums().stream(), versions.stream()).distinct().collect(Collectors.toList()));
    }
}


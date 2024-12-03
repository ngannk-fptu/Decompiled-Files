/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.beans.factory.annotation.Autowired
 *  org.springframework.context.ApplicationContext
 */
package com.atlassian.troubleshooting.preupgrade.modz;

import com.atlassian.troubleshooting.preupgrade.modz.Modification;
import com.atlassian.troubleshooting.preupgrade.modz.Modifications;
import com.atlassian.troubleshooting.preupgrade.modz.ModzDetection;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

public class ModzDetectorService {
    private final ApplicationContext context;

    @Autowired
    public ModzDetectorService(ApplicationContext context) {
        this.context = Objects.requireNonNull(context);
    }

    private Collection<ModzDetection> getDetectors() {
        return this.context.getBeansOfType(ModzDetection.class).values();
    }

    public Optional<Modifications> getModifications() {
        return Optional.of(new Modifications(this.getModzFromAllDetectors(ModzDetection::getModifiedFiles), this.getModzFromAllDetectors(ModzDetection::getRemovedFiles)));
    }

    private List<Modification> getModzFromAllDetectors(Function<ModzDetection, List<Modification>> mapper) {
        return this.getDetectors().stream().map(mapper).flatMap(Collection::stream).distinct().collect(Collectors.toList());
    }
}


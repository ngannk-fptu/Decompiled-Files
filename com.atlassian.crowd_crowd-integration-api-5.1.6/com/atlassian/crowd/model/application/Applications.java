/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.google.common.base.Function
 *  com.google.common.base.Predicate
 */
package com.atlassian.crowd.model.application;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.model.application.Application;
import com.atlassian.crowd.model.application.ApplicationDirectoryMapping;
import com.google.common.base.Function;
import com.google.common.base.Predicate;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public final class Applications {
    @Deprecated
    public static final Function<Application, String> NAME_FUNCTION = Application::getName;
    @Deprecated
    public static final Predicate<Application> ACTIVE_FILTER = Application::isActive;

    private Applications() {
    }

    public static Iterable<String> namesOf(Iterable<? extends Application> applications) {
        return StreamSupport.stream(applications.spliterator(), false).map(Application::getName).collect(Collectors.toList());
    }

    public static List<Directory> getActiveDirectories(Application application) {
        return application.getApplicationDirectoryMappings().stream().map(ApplicationDirectoryMapping::getDirectory).filter(Directory::isActive).collect(Collectors.toList());
    }
}


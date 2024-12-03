/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 *  com.atlassian.annotations.nullability.ParametersAreNonnullByDefault
 *  com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault
 *  org.apache.commons.lang3.tuple.Pair
 */
package com.atlassian.confluence.util.tomcat;

import com.atlassian.annotations.Internal;
import com.atlassian.annotations.nullability.ParametersAreNonnullByDefault;
import com.atlassian.annotations.nullability.ReturnValuesAreNonnullByDefault;
import java.io.File;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.tuple.Pair;

@ParametersAreNonnullByDefault
@ReturnValuesAreNonnullByDefault
@Internal
public interface TomcatConfigHelper {
    public Optional<String> getConnectorPort();

    public Optional<Pair<String, String>> getDatasourceCredentials();

    public Optional<String> getDatasourceUrl(File var1);

    public String getJavaRuntimeDirectory();

    @Deprecated
    public Optional<Integer> getMaxHttpThreads();

    public List<Optional<Integer>> getAllMaxHttpThreads();

    public List<File> getPotentialDatasourceLocations();

    public Optional<Integer> getProxyPort();

    public boolean isStandardPort(int var1);
}


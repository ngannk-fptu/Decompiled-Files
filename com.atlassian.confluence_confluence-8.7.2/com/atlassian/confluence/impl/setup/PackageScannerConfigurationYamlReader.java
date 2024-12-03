/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.plugin.osgi.container.impl.DefaultPackageScannerConfiguration
 *  org.apache.commons.lang3.StringUtils
 *  org.springframework.core.io.Resource
 *  org.yaml.snakeyaml.Yaml
 */
package com.atlassian.confluence.impl.setup;

import com.atlassian.plugin.osgi.container.impl.DefaultPackageScannerConfiguration;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.io.Resource;
import org.yaml.snakeyaml.Yaml;

final class PackageScannerConfigurationYamlReader {
    PackageScannerConfigurationYamlReader() {
    }

    static void populatePackages(DefaultPackageScannerConfiguration config, Resource packageExportsYaml, Resource publicApiYaml) throws IOException {
        Map<String, List<String>> exports = PackageScannerConfigurationYamlReader.readYaml(packageExportsYaml);
        Map<String, List<String>> publicApi = PackageScannerConfigurationYamlReader.readYaml(publicApiYaml);
        config.setPackageIncludes(PackageScannerConfigurationYamlReader.extractList(exports, "includes"));
        config.setPackageExcludes(PackageScannerConfigurationYamlReader.extractList(exports, "excludes"));
        config.setOsgiPublicPackages(PackageScannerConfigurationYamlReader.extractSet(publicApi, "includes"));
        config.setOsgiPublicPackagesExcludes(PackageScannerConfigurationYamlReader.extractSet(publicApi, "excludes"));
        config.setDeprecatedPackages(PackageScannerConfigurationYamlReader.extractSet(publicApi, "deprecated"));
        config.setApplicationBundledInternalPlugins(PackageScannerConfigurationYamlReader.extractSet(publicApi, "internal-bundles"));
    }

    private static List<String> extractList(Map<String, List<String>> exports, String key) {
        return PackageScannerConfigurationYamlReader.getValues(exports, key).collect(Collectors.toList());
    }

    private static Set<String> extractSet(Map<String, List<String>> exports, String key) {
        return PackageScannerConfigurationYamlReader.getValues(exports, key).collect(Collectors.toSet());
    }

    private static Stream<String> getValues(Map<String, List<String>> exports, String key) {
        return Objects.requireNonNull(exports.get(key), key).stream().filter(StringUtils::isNotBlank);
    }

    private static Map<String, List<String>> readYaml(Resource resource) throws IOException {
        try (InputStreamReader reader = new InputStreamReader(resource.getInputStream(), StandardCharsets.UTF_8);){
            Map map = (Map)new Yaml().load((Reader)reader);
            return map;
        }
    }
}


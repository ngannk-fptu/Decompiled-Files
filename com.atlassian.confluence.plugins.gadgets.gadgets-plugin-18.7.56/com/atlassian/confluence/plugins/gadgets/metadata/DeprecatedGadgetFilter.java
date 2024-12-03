/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gadgets.metadata;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;

public class DeprecatedGadgetFilter {
    private final Collection<GadgetDeprecationInfo> deprecatedGadgets = this.loadDeprecatedGadgets();

    public boolean isGadgetDeprecated(URI gadgetURI) {
        return this.deprecatedGadgets.stream().anyMatch(gadgetDeprecationInfo -> {
            String path = gadgetURI.getPath();
            return path != null && path.contains(gadgetDeprecationInfo.fromPackage) && path.contains(gadgetDeprecationInfo.gadgedXml);
        });
    }

    private Collection<GadgetDeprecationInfo> loadDeprecatedGadgets() {
        return new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/gadgets/deprecated-gadget.txt"))).lines().map(line -> {
            String[] deprecationLine = line.split(":");
            if (deprecationLine != null && deprecationLine.length == 2) {
                return Optional.of(new GadgetDeprecationInfo(deprecationLine[0], deprecationLine[1]));
            }
            return Optional.empty();
        }).filter(optionalResult -> optionalResult.isPresent()).map(optionalResult -> (GadgetDeprecationInfo)optionalResult.get()).collect(Collectors.toSet());
    }

    private class GadgetDeprecationInfo {
        private final String fromPackage;
        private final String gadgedXml;

        public GadgetDeprecationInfo(String fromPackage, String gadgedXml) {
            this.fromPackage = fromPackage;
            this.gadgedXml = gadgedXml;
        }
    }
}


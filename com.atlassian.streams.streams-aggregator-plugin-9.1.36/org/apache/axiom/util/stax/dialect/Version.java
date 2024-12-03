/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axiom.util.stax.dialect;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

final class Version {
    private static final Pattern pattern = Pattern.compile("([0-9]+(\\.[0-9]+)*)([\\.-].*)?");
    private final int[] components;

    Version(String versionString) {
        Matcher matcher = pattern.matcher(versionString);
        if (matcher.matches()) {
            String[] componentStrings = matcher.group(1).split("\\.");
            int l = componentStrings.length;
            this.components = new int[l];
            for (int i = 0; i < l; ++i) {
                this.components[i] = Integer.parseInt(componentStrings[i]);
            }
        } else {
            this.components = new int[0];
        }
    }

    int getComponent(int idx) {
        return idx < this.components.length ? this.components[idx] : 0;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.chart.imagemap;

import org.jfree.chart.imagemap.URLTagFragmentGenerator;

public class StandardURLTagFragmentGenerator
implements URLTagFragmentGenerator {
    public String generateURLFragment(String urlText) {
        return " href=\"" + urlText + "\"";
    }
}


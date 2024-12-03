/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugin.descriptor.aboutpage;

import com.atlassian.confluence.plugin.descriptor.aboutpage.Material;
import java.util.List;

public final class PluginAndMaterials
implements Comparable<PluginAndMaterials> {
    private final String pluginVersion;
    private final String pluginName;
    private final String introduction;
    private final String conclusion;
    private final List<Material> materials;

    public PluginAndMaterials(String pluginVersion, String pluginName, String introduction, String conclusion, List<Material> materials) {
        this.pluginVersion = pluginVersion;
        this.pluginName = pluginName;
        this.introduction = introduction;
        this.conclusion = conclusion;
        this.materials = materials;
    }

    public String getPluginVersion() {
        return this.pluginVersion;
    }

    public String getPluginName() {
        return this.pluginName;
    }

    public String getIntroductionHtml() {
        return this.introduction;
    }

    public String getConclusionHtml() {
        return this.conclusion;
    }

    public List<Material> getMaterials() {
        return this.materials;
    }

    public boolean isEntries() {
        return this.materials != null && this.materials.size() > 0;
    }

    @Override
    public int compareTo(PluginAndMaterials pluginAndMaterials) {
        if (pluginAndMaterials == null) {
            return 1;
        }
        int compare = this.getPluginName().compareTo(pluginAndMaterials.getPluginName());
        if (compare != 0) {
            return compare;
        }
        compare = this.getIntroductionHtml().compareTo(pluginAndMaterials.getIntroductionHtml());
        if (compare != 0) {
            return compare;
        }
        compare = this.getConclusionHtml().compareTo(pluginAndMaterials.getConclusionHtml());
        if (compare != 0) {
            return compare;
        }
        return this.getMaterials().size() - pluginAndMaterials.getMaterials().size();
    }
}


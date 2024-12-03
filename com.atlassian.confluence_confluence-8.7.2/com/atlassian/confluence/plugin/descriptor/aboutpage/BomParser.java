/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.ExperimentalApi
 */
package com.atlassian.confluence.plugin.descriptor.aboutpage;

import com.atlassian.annotations.ExperimentalApi;
import com.atlassian.confluence.plugin.descriptor.aboutpage.Material;
import java.util.List;

@ExperimentalApi
public interface BomParser {
    public List<Material> extractLgplMaterials(String var1);
}


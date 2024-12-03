/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugin.descriptor.aboutpage;

import com.atlassian.confluence.plugin.descriptor.aboutpage.BomParser;
import com.atlassian.confluence.plugin.descriptor.aboutpage.Material;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BomParserImpl
implements BomParser {
    private static final Logger log = LoggerFactory.getLogger(BomParserImpl.class);

    @Override
    public List<Material> extractLgplMaterials(String bomContents) {
        ArrayList<Material> materials = new ArrayList<Material>();
        bomContents = bomContents.replaceAll("\\r", "\n");
        String[] materialLines = bomContents.split("\\n");
        for (String materialLine : materialLines = StringUtils.stripAll((String[])materialLines)) {
            String url;
            String mavenInfo;
            if (materialLine.startsWith("#") || !materialLine.contains("GNU Lesser General Public License")) continue;
            String[] materialInfo = materialLine.split(",", -1);
            if (materialInfo.length != 5) {
                log.info(String.format("Could not parse license info line: %s", materialLine));
                continue;
            }
            String libraryName = (materialInfo = StringUtils.stripAll((String[])materialInfo))[0];
            if (StringUtils.isEmpty((CharSequence)(libraryName + (mavenInfo = materialInfo[1]) + (url = materialInfo[3])))) continue;
            String license = materialInfo[2];
            String artifactType = materialInfo[4];
            materials.add(new Material(libraryName, mavenInfo, license, url, artifactType));
        }
        return materials;
    }
}


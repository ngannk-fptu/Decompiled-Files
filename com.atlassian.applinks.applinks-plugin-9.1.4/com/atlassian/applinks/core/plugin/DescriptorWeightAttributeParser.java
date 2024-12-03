/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 */
package com.atlassian.applinks.core.plugin;

import org.dom4j.Element;

public class DescriptorWeightAttributeParser {
    public static final int DEFAULT_WEIGHT = 1000;

    public static int getWeight(Element moduleDescriptorElement) {
        try {
            return Integer.parseInt(moduleDescriptorElement.attributeValue("weight"));
        }
        catch (NumberFormatException e) {
            return 1000;
        }
    }
}


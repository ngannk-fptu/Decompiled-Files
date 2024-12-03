/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.dom4j.Element
 */
package com.atlassian.plugin.osgi.factory.transform.stage;

import com.atlassian.plugin.osgi.factory.transform.TransformContext;
import com.atlassian.plugin.osgi.factory.transform.TransformStage;
import java.util.List;
import org.dom4j.Element;

public class AddBundleOverridesStage
implements TransformStage {
    @Override
    public void execute(TransformContext context) {
        Element instructionRoot;
        Element pluginInfo = context.getDescriptorDocument().getRootElement().element("plugin-info");
        if (pluginInfo != null && (instructionRoot = pluginInfo.element("bundle-instructions")) != null) {
            List instructionsElement = instructionRoot.elements();
            for (Element instructionElement : instructionsElement) {
                String name = instructionElement.getName();
                String value = instructionElement.getTextTrim();
                context.getBndInstructions().put(name, value);
            }
        }
    }
}


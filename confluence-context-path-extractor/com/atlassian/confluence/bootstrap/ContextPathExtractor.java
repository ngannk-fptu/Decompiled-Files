/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.bootstrap;

import com.atlassian.confluence.bootstrap.XmlUtils;
import java.io.File;

public class ContextPathExtractor {
    public static void main(String[] args) {
        String catalinaHome = args[0];
        File serverXmlFile = new File(catalinaHome + File.separator + "conf" + File.separator + "server.xml");
        String confluenceContextPath = XmlUtils.getAttributeFromXmlFile(serverXmlFile, "path", "//Context[@path]").orElse("");
        System.out.print(confluenceContextPath);
    }
}


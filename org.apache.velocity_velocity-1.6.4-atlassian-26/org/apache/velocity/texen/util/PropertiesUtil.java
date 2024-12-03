/*
 * Decompiled with CFR 0.152.
 */
package org.apache.velocity.texen.util;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.StringTokenizer;
import org.apache.velocity.texen.Generator;

public class PropertiesUtil {
    public Properties load(String propertiesFile) {
        Properties properties = null;
        String templatePath = Generator.getInstance().getTemplatePath();
        try {
            properties = templatePath != null ? this.loadFromTemplatePath(propertiesFile) : this.loadFromClassPath(propertiesFile);
        }
        catch (RuntimeException e) {
            throw e;
        }
        catch (Exception e) {
            throw new RuntimeException("Could not load properties: " + e.getMessage());
        }
        return properties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Properties loadFromTemplatePath(String propertiesFile) throws Exception {
        Properties properties = new Properties();
        String templatePath = Generator.getInstance().getTemplatePath();
        StringTokenizer st = new StringTokenizer(templatePath, ",");
        if (st.hasMoreTokens()) {
            String templateDir = st.nextToken();
            try (InputStream stream = null;){
                String fullPath = propertiesFile;
                if (!fullPath.startsWith(templateDir)) {
                    fullPath = templateDir + "/" + propertiesFile;
                }
                stream = new FileInputStream(fullPath);
                properties.load(stream);
            }
        }
        return properties;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected Properties loadFromClassPath(String propertiesName) throws Exception {
        Properties properties = new Properties();
        ClassLoader classLoader = this.getClass().getClassLoader();
        try (InputStream inputStream = null;){
            String propertiesFile = propertiesName.startsWith("$generator") ? propertiesName.substring("$generator.templatePath/".length()) : propertiesName;
            inputStream = classLoader.getResourceAsStream(propertiesFile);
            properties.load(inputStream);
        }
        return properties;
    }
}


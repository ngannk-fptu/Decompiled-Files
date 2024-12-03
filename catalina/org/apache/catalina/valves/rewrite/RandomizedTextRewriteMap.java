/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.file.ConfigFileLoader
 *  org.apache.tomcat.util.file.ConfigurationSource$Resource
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.catalina.valves.rewrite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.apache.catalina.valves.rewrite.RewriteMap;
import org.apache.tomcat.util.file.ConfigFileLoader;
import org.apache.tomcat.util.file.ConfigurationSource;
import org.apache.tomcat.util.res.StringManager;

public class RandomizedTextRewriteMap
implements RewriteMap {
    protected static final StringManager sm = StringManager.getManager(RandomizedTextRewriteMap.class);
    private static final Random random = new Random();
    private final Map<String, String[]> map = new HashMap<String, String[]>();

    public RandomizedTextRewriteMap(String txtFilePath, boolean useRandom) {
        try (ConfigurationSource.Resource txtResource = ConfigFileLoader.getSource().getResource(txtFilePath);
             BufferedReader reader = new BufferedReader(new InputStreamReader(txtResource.getInputStream()));){
            String line;
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("#") || line.isEmpty()) continue;
                String[] keyValuePair = line.split(" ", 2);
                if (keyValuePair.length > 1) {
                    String key = keyValuePair[0];
                    String value = keyValuePair[1];
                    String[] possibleValues = null;
                    possibleValues = useRandom && value.contains("|") ? value.split("\\|") : new String[]{value};
                    this.map.put(key, possibleValues);
                    continue;
                }
                throw new IllegalArgumentException(sm.getString("rewriteMap.txtInvalidLine", new Object[]{line, txtFilePath}));
            }
        }
        catch (IOException e) {
            throw new IllegalArgumentException(sm.getString("rewriteMap.txtReadError", new Object[]{txtFilePath}), e);
        }
    }

    @Override
    public String setParameters(String params) {
        throw new IllegalArgumentException(StringManager.getManager(RewriteMap.class).getString("rewriteMap.tooManyParameters"));
    }

    @Override
    public String lookup(String key) {
        String[] possibleValues = this.map.get(key);
        if (possibleValues != null) {
            if (possibleValues.length > 1) {
                return possibleValues[random.nextInt(possibleValues.length)];
            }
            return possibleValues[0];
        }
        return null;
    }
}


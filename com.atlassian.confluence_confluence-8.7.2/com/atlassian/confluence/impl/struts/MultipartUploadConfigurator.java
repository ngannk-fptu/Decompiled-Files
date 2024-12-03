/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.Internal
 */
package com.atlassian.confluence.impl.struts;

import com.atlassian.annotations.Internal;
import com.atlassian.confluence.impl.struts.ConfluenceJakartaMultiPartRequest;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

@Internal
public class MultipartUploadConfigurator {
    private final Map<String, Set<Pattern>> patternsByPluginModuleKey = new HashMap<String, Set<Pattern>>();
    private final Set<Pattern> unauthorisedAllowedPatterns = new HashSet<Pattern>(COMPILED_UNAUTHORISED_ALLOWED_PATTERNS);
    private static final String UNAUTHORISED_ALLOWED_PATTERNS = System.getProperty("multipart.unauthorised.allowed.patterns", "");
    private static final List<Pattern> COMPILED_UNAUTHORISED_ALLOWED_PATTERNS = ConfluenceJakartaMultiPartRequest.buildPatternsList(UNAUTHORISED_ALLOWED_PATTERNS, ",");

    public synchronized void registerPluginPatterns(String pluginModuleKey, Set<Pattern> patterns) {
        Set<Pattern> existingPatterns = this.patternsByPluginModuleKey.get(pluginModuleKey);
        if (existingPatterns != null) {
            throw new IllegalArgumentException("Plugin module key " + pluginModuleKey + " already registered");
        }
        this.patternsByPluginModuleKey.put(pluginModuleKey, new HashSet<Pattern>(patterns));
        this.unauthorisedAllowedPatterns.addAll(patterns);
    }

    public synchronized void clearPluginPatterns(String pluginModuleKey) {
        Set<Pattern> patterns = this.patternsByPluginModuleKey.get(pluginModuleKey);
        if (patterns == null) {
            throw new IllegalArgumentException("Plugin module key " + pluginModuleKey + " not registered");
        }
        this.unauthorisedAllowedPatterns.removeAll(patterns);
        this.patternsByPluginModuleKey.remove(pluginModuleKey);
    }

    public Set<Pattern> getUnauthorisedAllowedPatterns() {
        return Collections.unmodifiableSet(this.unauthorisedAllowedPatterns);
    }
}


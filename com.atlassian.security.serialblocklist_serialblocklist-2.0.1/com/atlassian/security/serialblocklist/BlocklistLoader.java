/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.yaml.snakeyaml.LoaderOptions
 *  org.yaml.snakeyaml.Yaml
 *  org.yaml.snakeyaml.constructor.BaseConstructor
 *  org.yaml.snakeyaml.constructor.Constructor
 */
package com.atlassian.security.serialblocklist;

import com.atlassian.security.serialblocklist.BlockedPattern;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.BaseConstructor;
import org.yaml.snakeyaml.constructor.Constructor;

class BlocklistLoader {
    private final Set<String> blockedClasses = new HashSet<String>();
    private final List<BlockedPattern> blockedPatterns = new ArrayList<BlockedPattern>();

    BlocklistLoader() {
    }

    public void load() {
        this.loadBlockedClasses();
        this.loadBlockedPatterns();
    }

    private void loadBlockedClasses() {
        Yaml yaml = new Yaml();
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("classes.yaml");
        Map obj = (Map)yaml.load(inputStream);
        this.blockedClasses.addAll(new HashSet((List)obj.get("blocked-classes")));
    }

    private void loadBlockedPatterns() {
        Yaml yaml = new Yaml((BaseConstructor)new Constructor(BlockedPattern.class, new LoaderOptions()));
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream("patterns.yaml");
        yaml.loadAll(inputStream).forEach(x -> this.blockedPatterns.add((BlockedPattern)x));
    }

    Set<String> getBlockedClasses() {
        return this.blockedClasses;
    }

    List<BlockedPattern> getBlockedPatterns() {
        return this.blockedPatterns;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.typesafe.config.impl;

import com.typesafe.config.ConfigException;
import com.typesafe.config.ConfigOrigin;
import com.typesafe.config.impl.AbstractConfigObject;
import com.typesafe.config.impl.AbstractConfigValue;
import com.typesafe.config.impl.ConfigImpl;
import com.typesafe.config.impl.ConfigString;
import com.typesafe.config.impl.FromMapMode;
import com.typesafe.config.impl.Path;
import com.typesafe.config.impl.ResolveStatus;
import com.typesafe.config.impl.SimpleConfigObject;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

final class PropertiesParser {
    PropertiesParser() {
    }

    static AbstractConfigObject parse(Reader reader, ConfigOrigin origin) throws IOException {
        Properties props = new Properties();
        props.load(reader);
        return PropertiesParser.fromProperties(origin, props);
    }

    static String lastElement(String path) {
        int i = path.lastIndexOf(46);
        if (i < 0) {
            return path;
        }
        return path.substring(i + 1);
    }

    static String exceptLastElement(String path) {
        int i = path.lastIndexOf(46);
        if (i < 0) {
            return null;
        }
        return path.substring(0, i);
    }

    static Path pathFromPropertyKey(String key) {
        String last = PropertiesParser.lastElement(key);
        String exceptLast = PropertiesParser.exceptLastElement(key);
        Path path = new Path(last, null);
        while (exceptLast != null) {
            last = PropertiesParser.lastElement(exceptLast);
            exceptLast = PropertiesParser.exceptLastElement(exceptLast);
            path = new Path(last, path);
        }
        return path;
    }

    static AbstractConfigObject fromProperties(ConfigOrigin origin, Properties props) {
        return PropertiesParser.fromEntrySet(origin, props.entrySet());
    }

    private static <K, V> AbstractConfigObject fromEntrySet(ConfigOrigin origin, Set<Map.Entry<K, V>> entries) {
        Map<Path, Object> pathMap = PropertiesParser.getPathMap(entries);
        return PropertiesParser.fromPathMap(origin, pathMap, true);
    }

    private static <K, V> Map<Path, Object> getPathMap(Set<Map.Entry<K, V>> entries) {
        HashMap<Path, Object> pathMap = new HashMap<Path, Object>();
        for (Map.Entry<K, V> entry : entries) {
            K key = entry.getKey();
            if (!(key instanceof String)) continue;
            Path path = PropertiesParser.pathFromPropertyKey((String)key);
            pathMap.put(path, entry.getValue());
        }
        return pathMap;
    }

    static AbstractConfigObject fromStringMap(ConfigOrigin origin, Map<String, String> stringMap) {
        return PropertiesParser.fromEntrySet(origin, stringMap.entrySet());
    }

    static AbstractConfigObject fromPathMap(ConfigOrigin origin, Map<?, ?> pathExpressionMap) {
        HashMap<Path, Object> pathMap = new HashMap<Path, Object>();
        for (Map.Entry<?, ?> entry : pathExpressionMap.entrySet()) {
            Object keyObj = entry.getKey();
            if (!(keyObj instanceof String)) {
                throw new ConfigException.BugOrBroken("Map has a non-string as a key, expecting a path expression as a String");
            }
            Path path = Path.newPath((String)keyObj);
            pathMap.put(path, entry.getValue());
        }
        return PropertiesParser.fromPathMap(origin, pathMap, false);
    }

    private static AbstractConfigObject fromPathMap(ConfigOrigin origin, Map<Path, Object> pathMap, boolean convertedFromProperties) {
        HashSet<Path> scopePaths = new HashSet<Path>();
        HashSet<Path> valuePaths = new HashSet<Path>();
        for (Path path : pathMap.keySet()) {
            valuePaths.add(path);
            for (Iterator next = path.parent(); next != null; next = ((Path)((Object)next)).parent()) {
                scopePaths.add((Path)((Object)next));
            }
        }
        if (convertedFromProperties) {
            valuePaths.removeAll(scopePaths);
        } else {
            for (Path path : valuePaths) {
                if (!scopePaths.contains(path)) continue;
                throw new ConfigException.BugOrBroken("In the map, path '" + path.render() + "' occurs as both the parent object of a value and as a value. Because Map has no defined ordering, this is a broken situation.");
            }
        }
        HashMap<String, AbstractConfigValue> root = new HashMap<String, AbstractConfigValue>();
        HashMap scopes = new HashMap();
        for (Path path : scopePaths) {
            HashMap scope = new HashMap();
            scopes.put(path, scope);
        }
        for (Path path : valuePaths) {
            Path parentPath = path.parent();
            HashMap<String, AbstractConfigValue> parent = parentPath != null ? (Map)scopes.get(parentPath) : root;
            String last = path.last();
            Object rawValue = pathMap.get(path);
            AbstractConfigValue value = convertedFromProperties ? (rawValue instanceof String ? new ConfigString.Quoted(origin, (String)rawValue) : null) : ConfigImpl.fromAnyRef(pathMap.get(path), origin, FromMapMode.KEYS_ARE_PATHS);
            if (value == null) continue;
            parent.put(last, value);
        }
        ArrayList<Path> sortedScopePaths = new ArrayList<Path>();
        sortedScopePaths.addAll(scopePaths);
        Collections.sort(sortedScopePaths, new Comparator<Path>(){

            @Override
            public int compare(Path a, Path b) {
                return b.length() - a.length();
            }
        });
        for (Path scopePath : sortedScopePaths) {
            Map scope = (Map)scopes.get(scopePath);
            Path parentPath = scopePath.parent();
            HashMap<String, AbstractConfigValue> parent = parentPath != null ? (Map)scopes.get(parentPath) : root;
            SimpleConfigObject o = new SimpleConfigObject(origin, scope, ResolveStatus.RESOLVED, false);
            parent.put(scopePath.last(), o);
        }
        return new SimpleConfigObject(origin, root, ResolveStatus.RESOLVED, false);
    }
}


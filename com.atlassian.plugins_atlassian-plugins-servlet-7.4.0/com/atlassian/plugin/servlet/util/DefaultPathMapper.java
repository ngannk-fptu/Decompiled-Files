/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.plugin.servlet.util;

import com.atlassian.plugin.servlet.util.PathMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.regex.Pattern;

public class DefaultPathMapper
implements Serializable,
PathMapper {
    private static final Pattern REDUNDANT_SLASHES = Pattern.compile("//+");
    private static final String[] DEFAULT_KEYS = new String[]{"/", "*", "/*"};
    private final Map<String, Collection<String>> mappings = new HashMap<String, Collection<String>>();
    private final Set<String> complexPaths = new HashSet<String>();
    private final KeyMatcher matcher = new KeyMatcher();
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public void put(String key, String pattern) {
        this.lock.writeLock().lock();
        try {
            if (pattern == null) {
                this.removeMappingsForKey(key);
                return;
            }
            this.addMapping(pattern, key);
            if (pattern.indexOf(63) > -1 || pattern.contains("*") && pattern.length() > 1) {
                this.complexPaths.add(pattern);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private void addMapping(String pattern, String key) {
        this.mappings.computeIfAbsent(pattern, k -> new LinkedHashSet()).add(key);
    }

    private void removeMappingsForKey(String key) {
        Iterator<Map.Entry<String, Collection<String>>> it = this.mappings.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, Collection<String>> entry = it.next();
            if (!entry.getValue().remove(key) || !entry.getValue().isEmpty()) continue;
            this.complexPaths.remove(entry.getKey());
            it.remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String get(String path) {
        path = this.removeRedundantSlashes(path);
        this.lock.readLock().lock();
        try {
            String mapped;
            if (path == null) {
                path = "/";
            }
            if ((mapped = this.matcher.findKey(path, this.mappings, this.complexPaths)) == null) {
                String string = null;
                return string;
            }
            Collection<String> keys = this.mappings.get(mapped);
            if (keys.isEmpty()) {
                String string = null;
                return string;
            }
            String string = keys.iterator().next();
            return string;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public Collection<String> getAll(String path) {
        path = this.removeRedundantSlashes(path);
        this.lock.readLock().lock();
        try {
            if (path == null) {
                path = "/";
            }
            LinkedHashSet<String> matches = new LinkedHashSet<String>();
            String exactKey = this.matcher.findExactKey(path, this.mappings);
            if (exactKey != null) {
                matches.addAll(this.mappings.get(exactKey));
            }
            for (String mapped : this.matcher.findComplexKeys(path, this.complexPaths)) {
                if (!this.mappings.containsKey(mapped)) continue;
                matches.addAll(this.mappings.get(mapped));
            }
            for (String mapped : this.matcher.findDefaultKeys(this.mappings)) {
                matches.addAll(this.mappings.get(mapped));
            }
            Collection collection = Collections.unmodifiableCollection(matches);
            return collection;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    protected String removeRedundantSlashes(String path) {
        return path == null ? null : REDUNDANT_SLASHES.matcher(path).replaceAll("/");
    }

    public String toString() {
        StringBuilder sb = new StringBuilder(30 * (this.mappings.size() + this.complexPaths.size()));
        sb.append("Mappings:\n");
        for (Map.Entry<String, Collection<String>> entry : this.mappings.entrySet()) {
            sb.append(entry.getKey()).append("=").append(entry.getValue()).append("\n");
        }
        sb.append("Complex Paths:\n");
        for (String path : this.complexPaths) {
            sb.append(path).append("\n");
        }
        return sb.toString();
    }

    private final class KeyMatcher {
        private KeyMatcher() {
        }

        String findKey(String path, Map<String, Collection<String>> mappings, Set<String> keys) {
            String result = this.findExactKey(path, mappings);
            if (result == null) {
                result = this.findComplexKey(path, keys);
            }
            if (result == null) {
                result = this.findDefaultKey(mappings);
            }
            return result;
        }

        String findExactKey(String path, Map<String, Collection<String>> mappings) {
            if (mappings.containsKey(path)) {
                return path;
            }
            return null;
        }

        String findComplexKey(String path, Set<String> complexPaths) {
            String matchedKey = null;
            int keyLength = 0;
            for (String key : complexPaths) {
                if (!this.match(key, path, false) || key.length() <= keyLength) continue;
                keyLength = key.length();
                matchedKey = key;
            }
            return matchedKey;
        }

        Collection<String> findComplexKeys(String path, Set<String> complexPaths) {
            ArrayList<String> matches = new ArrayList<String>();
            for (String key : complexPaths) {
                if (!this.match(key, path, false)) continue;
                matches.add(key);
            }
            return matches;
        }

        String findDefaultKey(Map<String, Collection<String>> mappings) {
            for (int i = 0; i < DEFAULT_KEYS.length; ++i) {
                if (!mappings.containsKey(DEFAULT_KEYS[i])) continue;
                return DEFAULT_KEYS[i];
            }
            return null;
        }

        Collection<String> findDefaultKeys(Map<String, Collection<String>> mappings) {
            ArrayList<String> matches = new ArrayList<String>();
            for (int i = 0; i < DEFAULT_KEYS.length; ++i) {
                if (!mappings.containsKey(DEFAULT_KEYS[i])) continue;
                matches.add(DEFAULT_KEYS[i]);
            }
            return matches;
        }

        boolean match(String pattern, String str, boolean isCaseSensitive) {
            char ch;
            int i;
            char[] patArr = pattern.toCharArray();
            char[] strArr = str.toCharArray();
            int patIdxStart = 0;
            int patIdxEnd = patArr.length - 1;
            int strIdxStart = 0;
            int strIdxEnd = strArr.length - 1;
            boolean containsStar = false;
            for (i = 0; i < patArr.length; ++i) {
                if (patArr[i] != '*') continue;
                containsStar = true;
                break;
            }
            if (!containsStar) {
                if (patIdxEnd != strIdxEnd) {
                    return false;
                }
                for (i = 0; i <= patIdxEnd; ++i) {
                    char ch2 = patArr[i];
                    if (ch2 == '?') continue;
                    if (isCaseSensitive && ch2 != strArr[i]) {
                        return false;
                    }
                    if (isCaseSensitive || Character.toUpperCase(ch2) == Character.toUpperCase(strArr[i])) continue;
                    return false;
                }
                return true;
            }
            if (patIdxEnd == 0) {
                return true;
            }
            while ((ch = patArr[patIdxStart]) != '*' && strIdxStart <= strIdxEnd) {
                if (ch != '?') {
                    if (isCaseSensitive && ch != strArr[strIdxStart]) {
                        return false;
                    }
                    if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart])) {
                        return false;
                    }
                }
                ++patIdxStart;
                ++strIdxStart;
            }
            if (strIdxStart > strIdxEnd) {
                for (i = patIdxStart; i <= patIdxEnd; ++i) {
                    if (patArr[i] == '*') continue;
                    return false;
                }
                return true;
            }
            while ((ch = patArr[patIdxEnd]) != '*' && strIdxStart <= strIdxEnd) {
                if (ch != '?') {
                    if (isCaseSensitive && ch != strArr[strIdxEnd]) {
                        return false;
                    }
                    if (!isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxEnd])) {
                        return false;
                    }
                }
                --patIdxEnd;
                --strIdxEnd;
            }
            if (strIdxStart > strIdxEnd) {
                for (i = patIdxStart; i <= patIdxEnd; ++i) {
                    if (patArr[i] == '*') continue;
                    return false;
                }
                return true;
            }
            while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                int patIdxTmp = -1;
                for (int i2 = patIdxStart + 1; i2 <= patIdxEnd; ++i2) {
                    if (patArr[i2] != '*') continue;
                    patIdxTmp = i2;
                    break;
                }
                if (patIdxTmp == patIdxStart + 1) {
                    ++patIdxStart;
                    continue;
                }
                int patLength = patIdxTmp - patIdxStart - 1;
                int strLength = strIdxEnd - strIdxStart + 1;
                int foundIdx = -1;
                block8: for (int i3 = 0; i3 <= strLength - patLength; ++i3) {
                    for (int j = 0; j < patLength; ++j) {
                        ch = patArr[patIdxStart + j + 1];
                        if (ch != '?' && (isCaseSensitive && ch != strArr[strIdxStart + i3 + j] || !isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart + i3 + j]))) continue block8;
                    }
                    foundIdx = strIdxStart + i3;
                    break;
                }
                if (foundIdx == -1) {
                    return false;
                }
                patIdxStart = patIdxTmp;
                strIdxStart = foundIdx + patLength;
            }
            for (i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
    }
}


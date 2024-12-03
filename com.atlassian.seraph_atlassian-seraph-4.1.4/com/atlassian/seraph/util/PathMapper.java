/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.seraph.util;

import com.atlassian.seraph.util.IPathMapper;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class PathMapper
implements Serializable,
IPathMapper {
    private static final String[] DEFAULT_KEYS = new String[]{"/", "*", "/*"};
    private final Map<String, String> mappings = new HashMap<String, String>();
    private final List<String> complexPaths = new ArrayList<String>();
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
            this.mappings.put(pattern, key);
            if (pattern.indexOf(63) > -1 || pattern.indexOf("*") > -1 && pattern.length() > 1) {
                this.complexPaths.add(pattern);
            }
        }
        finally {
            this.lock.writeLock().unlock();
        }
    }

    private void removeMappingsForKey(String key) {
        Iterator<Map.Entry<String, String>> it = this.mappings.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> entry = it.next();
            if (!entry.getValue().equals(key)) continue;
            this.complexPaths.remove(entry.getKey());
            it.remove();
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Override
    public String get(String path) {
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
            String string = this.mappings.get(mapped);
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
        this.lock.readLock().lock();
        try {
            String mapped;
            String element;
            if (path == null) {
                path = "/";
            }
            ArrayList<String> matches = new ArrayList<String>();
            String exactKey = this.matcher.findExactKey(path, this.mappings);
            if (exactKey != null) {
                matches.add(this.mappings.get(exactKey));
            }
            Object object = this.matcher.findComplexKeys(path, this.complexPaths).iterator();
            while (object.hasNext()) {
                mapped = element = object.next();
                matches.add(this.mappings.get(mapped));
            }
            object = this.matcher.findDefaultKeys(this.mappings).iterator();
            while (object.hasNext()) {
                mapped = element = object.next();
                matches.add(this.mappings.get(mapped));
            }
            object = Collections.unmodifiableCollection(matches);
            return object;
        }
        finally {
            this.lock.readLock().unlock();
        }
    }

    public String toString() {
        String element;
        StringBuffer sb = new StringBuffer(30 * (this.mappings.size() + this.complexPaths.size()));
        sb.append("Mappings:\n");
        Iterator<String> iterator = this.mappings.keySet().iterator();
        while (iterator.hasNext()) {
            String key = element = iterator.next();
            sb.append(key).append("=").append(this.mappings.get(key)).append("\n");
        }
        sb.append("Complex Paths:\n");
        iterator = this.complexPaths.iterator();
        while (iterator.hasNext()) {
            String path = element = iterator.next();
            sb.append(path).append("\n");
        }
        return sb.toString();
    }

    private static final class KeyMatcher {
        private KeyMatcher() {
        }

        String findKey(String path, Map<String, ?> mappings, List<String> keys) {
            String result = this.findExactKey(path, mappings);
            if (result == null) {
                result = this.findComplexKey(path, keys);
            }
            if (result == null) {
                result = this.findDefaultKey(mappings);
            }
            return result;
        }

        String findExactKey(String path, Map<String, ?> mappings) {
            if (mappings.containsKey(path)) {
                return path;
            }
            return null;
        }

        String findComplexKey(String path, List<String> complexPaths) {
            int size = complexPaths.size();
            for (int i = 0; i < size; ++i) {
                String key = complexPaths.get(i);
                if (!this.match(key, path, false)) continue;
                return key;
            }
            return null;
        }

        Collection<String> findComplexKeys(String path, List<String> complexPaths) {
            ArrayList<String> matches = new ArrayList<String>();
            for (String key : complexPaths) {
                if (!this.match(key, path, false)) continue;
                matches.add(key);
            }
            return matches;
        }

        String findDefaultKey(Map<String, ?> mappings) {
            for (String element : DEFAULT_KEYS) {
                if (!mappings.containsKey(element)) continue;
                return element;
            }
            return null;
        }

        Collection<String> findDefaultKeys(Map<String, ?> mappings) {
            ArrayList<String> matches = new ArrayList<String>();
            for (String element : DEFAULT_KEYS) {
                if (!mappings.containsKey(element)) continue;
                matches.add(element);
            }
            return matches;
        }

        boolean match(String pattern, String str, boolean isCaseSensitive) {
            char ch;
            char[] patArr = pattern.toCharArray();
            char[] strArr = str.toCharArray();
            int patIdxStart = 0;
            int patIdxEnd = patArr.length - 1;
            int strIdxStart = 0;
            int strIdxEnd = strArr.length - 1;
            boolean containsStar = false;
            for (char element : patArr) {
                if (element != '*') continue;
                containsStar = true;
                break;
            }
            if (!containsStar) {
                if (patIdxEnd != strIdxEnd) {
                    return false;
                }
                for (int i = 0; i <= patIdxEnd; ++i) {
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
                for (int i = patIdxStart; i <= patIdxEnd; ++i) {
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
                for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                    if (patArr[i] == '*') continue;
                    return false;
                }
                return true;
            }
            while (patIdxStart != patIdxEnd && strIdxStart <= strIdxEnd) {
                int patIdxTmp = -1;
                for (int i = patIdxStart + 1; i <= patIdxEnd; ++i) {
                    if (patArr[i] != '*') continue;
                    patIdxTmp = i;
                    break;
                }
                if (patIdxTmp == patIdxStart + 1) {
                    ++patIdxStart;
                    continue;
                }
                int patLength = patIdxTmp - patIdxStart - 1;
                int strLength = strIdxEnd - strIdxStart + 1;
                int foundIdx = -1;
                block8: for (int i = 0; i <= strLength - patLength; ++i) {
                    for (int j = 0; j < patLength; ++j) {
                        ch = patArr[patIdxStart + j + 1];
                        if (ch != '?' && (isCaseSensitive && ch != strArr[strIdxStart + i + j] || !isCaseSensitive && Character.toUpperCase(ch) != Character.toUpperCase(strArr[strIdxStart + i + j]))) continue block8;
                    }
                    foundIdx = strIdxStart + i;
                    break;
                }
                if (foundIdx == -1) {
                    return false;
                }
                patIdxStart = patIdxTmp;
                strIdxStart = foundIdx + patLength;
            }
            for (int i = patIdxStart; i <= patIdxEnd; ++i) {
                if (patArr[i] == '*') continue;
                return false;
            }
            return true;
        }
    }
}


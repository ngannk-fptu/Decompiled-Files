/*
 * Decompiled with CFR 0.152.
 */
package org.osgi.framework;

import java.security.AccessController;
import java.util.AbstractMap;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.ServiceLoader;
import java.util.Set;
import javax.security.auth.x500.X500Principal;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleReference;
import org.osgi.framework.Filter;
import org.osgi.framework.FilterImpl;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.connect.FrameworkUtilHelper;

public class FrameworkUtil {
    private static final List<FrameworkUtilHelper> helpers;

    private FrameworkUtil() {
    }

    public static Filter createFilter(String filter) throws InvalidSyntaxException {
        return FilterImpl.createFilter(filter);
    }

    public static boolean matchDistinguishedNameChain(String matchPattern, List<String> dnChain) {
        return DNChainMatching.match(matchPattern, dnChain);
    }

    public static Optional<Bundle> getBundle(ClassLoader bundleClassLoader) {
        Objects.requireNonNull(bundleClassLoader);
        return Optional.ofNullable(bundleClassLoader instanceof BundleReference ? ((BundleReference)((Object)bundleClassLoader)).getBundle() : null);
    }

    public static Bundle getBundle(Class<?> classFromBundle) {
        Optional<ClassLoader> cl = Optional.ofNullable(AccessController.doPrivileged(() -> classFromBundle.getClassLoader()));
        return cl.flatMap(FrameworkUtil::getBundle).orElseGet(() -> helpers.stream().map(helper -> helper.getBundle(classFromBundle)).filter(Optional::isPresent).map(Optional::get).findFirst().orElse(null));
    }

    public static <K, V> Map<K, V> asMap(Dictionary<? extends K, ? extends V> dictionary) {
        if (dictionary instanceof Map) {
            Map coerced = (Map)((Object)dictionary);
            return coerced;
        }
        return new DictionaryAsMap<K, V>(dictionary);
    }

    public static <K, V> Dictionary<K, V> asDictionary(Map<? extends K, ? extends V> map) {
        if (map instanceof Dictionary) {
            Dictionary coerced = (Dictionary)((Object)map);
            return coerced;
        }
        return new MapAsDictionary<K, V>(map);
    }

    static {
        ArrayList l = new ArrayList();
        try {
            ServiceLoader helperLoader = AccessController.doPrivileged(() -> ServiceLoader.load(FrameworkUtilHelper.class, FrameworkUtilHelper.class.getClassLoader()));
            helperLoader.forEach(l::add);
        }
        catch (Throwable error) {
            try {
                Thread t = Thread.currentThread();
                t.getUncaughtExceptionHandler().uncaughtException(t, error);
            }
            catch (Throwable throwable) {
                // empty catch block
            }
        }
        helpers = Collections.unmodifiableList(l);
    }

    private static class MapAsDictionary<K, V>
    extends Dictionary<K, V> {
        private final Map<K, V> map;

        MapAsDictionary(Map<? extends K, ? extends V> map) {
            boolean nullValue;
            boolean nullKey;
            this.map = Objects.requireNonNull(map);
            try {
                nullKey = map.containsKey(null);
            }
            catch (NullPointerException e) {
                nullKey = false;
            }
            if (nullKey) {
                throw new NullPointerException("a Dictionary cannot contain a null key");
            }
            try {
                nullValue = map.containsValue(null);
            }
            catch (NullPointerException e) {
                nullValue = false;
            }
            if (nullValue) {
                throw new NullPointerException("a Dictionary cannot contain a null value");
            }
        }

        @Override
        public int size() {
            return this.map.size();
        }

        @Override
        public boolean isEmpty() {
            return this.map.isEmpty();
        }

        @Override
        public Enumeration<K> keys() {
            return Collections.enumeration(this.map.keySet());
        }

        @Override
        public Enumeration<V> elements() {
            return Collections.enumeration(this.map.values());
        }

        @Override
        public V get(Object key) {
            if (key == null) {
                return null;
            }
            return this.map.get(key);
        }

        @Override
        public V put(K key, V value) {
            return this.map.put(Objects.requireNonNull(key, "a Dictionary cannot contain a null key"), Objects.requireNonNull(value, "a Dictionary cannot contain a null value"));
        }

        @Override
        public V remove(Object key) {
            if (key == null) {
                return null;
            }
            return this.map.remove(key);
        }

        public String toString() {
            return this.map.toString();
        }
    }

    private static class DictionaryAsMap<K, V>
    extends AbstractMap<K, V> {
        private final Dictionary<K, V> dict;

        DictionaryAsMap(Dictionary<? extends K, ? extends V> dict) {
            this.dict = Objects.requireNonNull(dict);
        }

        Iterator<K> keys() {
            ArrayList<K> keys = new ArrayList<K>(this.dict.size());
            Enumeration<K> e = this.dict.keys();
            while (e.hasMoreElements()) {
                keys.add(e.nextElement());
            }
            return keys.iterator();
        }

        @Override
        public int size() {
            return this.dict.size();
        }

        @Override
        public boolean isEmpty() {
            return this.dict.isEmpty();
        }

        @Override
        public boolean containsKey(Object key) {
            if (key == null) {
                return false;
            }
            return this.dict.get(key) != null;
        }

        @Override
        public V get(Object key) {
            if (key == null) {
                return null;
            }
            return this.dict.get(key);
        }

        @Override
        public V put(K key, V value) {
            return this.dict.put(Objects.requireNonNull(key, "a Dictionary cannot contain a null key"), Objects.requireNonNull(value, "a Dictionary cannot contain a null value"));
        }

        @Override
        public V remove(Object key) {
            if (key == null) {
                return null;
            }
            return this.dict.remove(key);
        }

        @Override
        public void clear() {
            Iterator<K> iter = this.keys();
            while (iter.hasNext()) {
                this.dict.remove(iter.next());
            }
        }

        @Override
        public Set<K> keySet() {
            return new KeySet();
        }

        @Override
        public Set<Map.Entry<K, V>> entrySet() {
            return new EntrySet();
        }

        @Override
        public String toString() {
            return this.dict.toString();
        }

        final class Entry
        extends AbstractMap.SimpleEntry<K, V> {
            private static final long serialVersionUID = 1L;

            Entry(K key) {
                super(key, DictionaryAsMap.this.get(key));
            }

            @Override
            public V setValue(V value) {
                DictionaryAsMap.this.put(this.getKey(), value);
                return super.setValue(value);
            }
        }

        final class EntryIterator
        implements Iterator<Map.Entry<K, V>> {
            private final Iterator<K> keys;
            private K key;

            EntryIterator() {
                this.keys = DictionaryAsMap.this.keys();
                this.key = null;
            }

            @Override
            public boolean hasNext() {
                return this.keys.hasNext();
            }

            @Override
            public Map.Entry<K, V> next() {
                this.key = this.keys.next();
                return new Entry(this.key);
            }

            @Override
            public void remove() {
                if (this.key == null) {
                    throw new IllegalStateException();
                }
                DictionaryAsMap.this.remove(this.key);
                this.key = null;
            }
        }

        final class EntrySet
        extends AbstractSet<Map.Entry<K, V>> {
            EntrySet() {
            }

            @Override
            public Iterator<Map.Entry<K, V>> iterator() {
                return new EntryIterator();
            }

            @Override
            public int size() {
                return DictionaryAsMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return DictionaryAsMap.this.isEmpty();
            }

            @Override
            public boolean contains(Object o) {
                if (o instanceof Map.Entry) {
                    Map.Entry e = (Map.Entry)o;
                    return this.containsEntry(e);
                }
                return false;
            }

            private boolean containsEntry(Map.Entry<?, ?> e) {
                Object key = e.getKey();
                if (key == null) {
                    return false;
                }
                Object value = e.getValue();
                if (value == null) {
                    return false;
                }
                return Objects.equals(DictionaryAsMap.this.get(key), value);
            }

            @Override
            public boolean remove(Object o) {
                Map.Entry e;
                if (o instanceof Map.Entry && this.containsEntry(e = (Map.Entry)o)) {
                    DictionaryAsMap.this.remove(e.getKey());
                    return true;
                }
                return false;
            }

            @Override
            public void clear() {
                DictionaryAsMap.this.clear();
            }
        }

        final class KeyIterator
        implements Iterator<K> {
            private final Iterator<K> keys;
            private K key;

            KeyIterator() {
                this.keys = DictionaryAsMap.this.keys();
                this.key = null;
            }

            @Override
            public boolean hasNext() {
                return this.keys.hasNext();
            }

            @Override
            public K next() {
                this.key = this.keys.next();
                return this.key;
            }

            @Override
            public void remove() {
                if (this.key == null) {
                    throw new IllegalStateException();
                }
                DictionaryAsMap.this.remove(this.key);
                this.key = null;
            }
        }

        final class KeySet
        extends AbstractSet<K> {
            KeySet() {
            }

            @Override
            public Iterator<K> iterator() {
                return new KeyIterator();
            }

            @Override
            public int size() {
                return DictionaryAsMap.this.size();
            }

            @Override
            public boolean isEmpty() {
                return DictionaryAsMap.this.isEmpty();
            }

            @Override
            public boolean contains(Object key) {
                return DictionaryAsMap.this.containsKey(key);
            }

            @Override
            public boolean remove(Object key) {
                return DictionaryAsMap.this.remove(key) != null;
            }

            @Override
            public void clear() {
                DictionaryAsMap.this.clear();
            }
        }
    }

    private static final class DNChainMatching {
        private static final String MINUS_WILDCARD = "-";
        private static final String STAR_WILDCARD = "*";

        private DNChainMatching() {
        }

        private static boolean rdnmatch(List<?> rdn, List<?> rdnPattern) {
            if (rdn.size() != rdnPattern.size()) {
                return false;
            }
            for (int i = 0; i < rdn.size(); ++i) {
                int patNameEnd;
                String rdnNameValue = (String)rdn.get(i);
                String patNameValue = (String)rdnPattern.get(i);
                int rdnNameEnd = rdnNameValue.indexOf(61);
                if (rdnNameEnd != (patNameEnd = patNameValue.indexOf(61)) || !rdnNameValue.regionMatches(0, patNameValue, 0, rdnNameEnd)) {
                    return false;
                }
                String patValue = patNameValue.substring(patNameEnd);
                String rdnValue = rdnNameValue.substring(rdnNameEnd);
                if (rdnValue.equals(patValue) || patValue.equals("=*") || patValue.equals("=#16012a")) continue;
                return false;
            }
            return true;
        }

        private static boolean dnmatch(List<?> dn, List<?> dnPattern) {
            int dnStart = 0;
            int patStart = 0;
            int patLen = dnPattern.size();
            if (patLen == 0) {
                return false;
            }
            if (dnPattern.get(0).equals(STAR_WILDCARD)) {
                patStart = 1;
                --patLen;
            }
            if (dn.size() < patLen) {
                return false;
            }
            if (dn.size() > patLen) {
                if (!dnPattern.get(0).equals(STAR_WILDCARD)) {
                    return false;
                }
                dnStart = dn.size() - patLen;
            }
            for (int i = 0; i < patLen; ++i) {
                if (DNChainMatching.rdnmatch((List)dn.get(i + dnStart), (List)dnPattern.get(i + patStart))) continue;
                return false;
            }
            return true;
        }

        /*
         * Enabled aggressive block sorting
         */
        private static List<Object> parseDNchainPattern(String pattern) {
            if (pattern == null) {
                throw new IllegalArgumentException("The pattern must not be null.");
            }
            ArrayList<Object> parsed = new ArrayList<Object>();
            int length = pattern.length();
            int c = 59;
            int startIndex = DNChainMatching.skipSpaces(pattern, 0);
            while (true) {
                int cursor;
                if (startIndex >= length) {
                    if (c == 59) {
                        throw new IllegalArgumentException("empty pattern");
                    }
                    break;
                }
                int endIndex = startIndex;
                boolean inQuote = false;
                block6: for (cursor = startIndex; cursor < length; ++cursor) {
                    c = pattern.charAt(cursor);
                    switch (c) {
                        case 34: {
                            inQuote = !inQuote;
                            break;
                        }
                        case 92: {
                            if (++cursor != length) break;
                            throw new IllegalArgumentException("unterminated escape");
                        }
                        case 59: {
                            if (!inQuote) break block6;
                        }
                    }
                    if (c == 32) continue;
                    endIndex = cursor + 1;
                }
                parsed.add(pattern.substring(startIndex, endIndex));
                startIndex = DNChainMatching.skipSpaces(pattern, cursor + 1);
            }
            int i = 0;
            while (i < parsed.size()) {
                String dn = (String)parsed.get(i);
                if (!dn.equals(STAR_WILDCARD) && !dn.equals(MINUS_WILDCARD)) {
                    ArrayList<Object> rdns = new ArrayList<Object>();
                    if (dn.charAt(0) == '*') {
                        int index = DNChainMatching.skipSpaces(dn, 1);
                        if (dn.charAt(index) != ',') {
                            throw new IllegalArgumentException("invalid wildcard prefix");
                        }
                        rdns.add(STAR_WILDCARD);
                        dn = new X500Principal(dn.substring(index + 1)).getName("CANONICAL");
                    } else {
                        dn = new X500Principal(dn).getName("CANONICAL");
                    }
                    DNChainMatching.parseDN(dn, rdns);
                    parsed.set(i, rdns);
                }
                ++i;
            }
            return parsed;
        }

        private static List<Object> parseDNchain(List<String> chain) {
            if (chain == null) {
                throw new IllegalArgumentException("DN chain must not be null.");
            }
            ArrayList<Object> result = new ArrayList<Object>(chain.size());
            for (String dn : chain) {
                dn = new X500Principal(dn).getName("CANONICAL");
                ArrayList<Object> rdns = new ArrayList<Object>();
                DNChainMatching.parseDN(dn, rdns);
                result.add(rdns);
            }
            if (result.size() == 0) {
                throw new IllegalArgumentException("empty DN chain");
            }
            return result;
        }

        private static int skipSpaces(String dnChain, int startIndex) {
            while (startIndex < dnChain.length() && dnChain.charAt(startIndex) == ' ') {
                ++startIndex;
            }
            return startIndex;
        }

        private static void parseDN(String dn, List<Object> rdn) {
            int startIndex = 0;
            char c = '\u0000';
            ArrayList<String> nameValues = new ArrayList<String>();
            while (startIndex < dn.length()) {
                int endIndex;
                for (endIndex = startIndex; endIndex < dn.length() && (c = dn.charAt(endIndex)) != ',' && c != '+'; ++endIndex) {
                    if (c != '\\') continue;
                    ++endIndex;
                }
                if (endIndex > dn.length()) {
                    throw new IllegalArgumentException("unterminated escape " + dn);
                }
                nameValues.add(dn.substring(startIndex, endIndex));
                if (c != '+') {
                    rdn.add(nameValues);
                    nameValues = endIndex != dn.length() ? new ArrayList() : null;
                }
                startIndex = endIndex + 1;
            }
            if (nameValues != null) {
                throw new IllegalArgumentException("improperly terminated DN " + dn);
            }
        }

        private static int skipWildCards(List<Object> dnChainPattern, int dnChainPatternIndex) {
            int i;
            for (i = dnChainPatternIndex; i < dnChainPattern.size(); ++i) {
                Object dnPattern = dnChainPattern.get(i);
                if (dnPattern instanceof String) {
                    if (dnPattern.equals(STAR_WILDCARD) || dnPattern.equals(MINUS_WILDCARD)) continue;
                    throw new IllegalArgumentException("expected wildcard in DN pattern");
                }
                if (dnPattern instanceof List) break;
                throw new IllegalArgumentException("expected String or List in DN Pattern");
            }
            return i;
        }

        private static boolean dnChainMatch(List<Object> dnChain, int dnChainIndex, List<Object> dnChainPattern, int dnChainPatternIndex) throws IllegalArgumentException {
            if (dnChainIndex >= dnChain.size()) {
                return false;
            }
            if (dnChainPatternIndex >= dnChainPattern.size()) {
                return false;
            }
            Object dnPattern = dnChainPattern.get(dnChainPatternIndex);
            if (dnPattern instanceof String) {
                if (!dnPattern.equals(STAR_WILDCARD) && !dnPattern.equals(MINUS_WILDCARD)) {
                    throw new IllegalArgumentException("expected wildcard in DN pattern");
                }
                dnChainPatternIndex = dnPattern.equals(MINUS_WILDCARD) ? DNChainMatching.skipWildCards(dnChainPattern, dnChainPatternIndex) : ++dnChainPatternIndex;
                if (dnChainPatternIndex >= dnChainPattern.size()) {
                    return dnPattern.equals(MINUS_WILDCARD) ? true : dnChain.size() - 1 == dnChainIndex;
                }
                if (dnPattern.equals(STAR_WILDCARD)) {
                    return DNChainMatching.dnChainMatch(dnChain, dnChainIndex, dnChainPattern, dnChainPatternIndex) || DNChainMatching.dnChainMatch(dnChain, dnChainIndex + 1, dnChainPattern, dnChainPatternIndex);
                }
                for (int i = dnChainIndex; i < dnChain.size(); ++i) {
                    if (!DNChainMatching.dnChainMatch(dnChain, i, dnChainPattern, dnChainPatternIndex)) continue;
                    return true;
                }
            } else {
                if (dnPattern instanceof List) {
                    do {
                        if (!DNChainMatching.dnmatch((List)dnChain.get(dnChainIndex), (List)dnPattern)) {
                            return false;
                        }
                        if (++dnChainIndex >= dnChain.size() && ++dnChainPatternIndex >= dnChainPattern.size()) {
                            return true;
                        }
                        if (dnChainIndex >= dnChain.size()) {
                            return (dnChainPatternIndex = DNChainMatching.skipWildCards(dnChainPattern, dnChainPatternIndex)) >= dnChainPattern.size();
                        }
                        if (dnChainPatternIndex >= dnChainPattern.size()) {
                            return false;
                        }
                        dnPattern = dnChainPattern.get(dnChainPatternIndex);
                        if (!(dnPattern instanceof String)) continue;
                        if (!dnPattern.equals(STAR_WILDCARD) && !dnPattern.equals(MINUS_WILDCARD)) {
                            throw new IllegalArgumentException("expected wildcard in DN pattern");
                        }
                        return DNChainMatching.dnChainMatch(dnChain, dnChainIndex, dnChainPattern, dnChainPatternIndex);
                    } while (dnPattern instanceof List);
                    throw new IllegalArgumentException("expected String or List in DN Pattern");
                }
                throw new IllegalArgumentException("expected String or List in DN Pattern");
            }
            return false;
        }

        static boolean match(String pattern, List<String> dnChain) {
            List<Object> parsedDNPattern;
            List<Object> parsedDNChain;
            try {
                parsedDNChain = DNChainMatching.parseDNchain(dnChain);
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException("Invalid DN chain: " + DNChainMatching.toString(dnChain), e);
            }
            try {
                parsedDNPattern = DNChainMatching.parseDNchainPattern(pattern);
            }
            catch (RuntimeException e) {
                throw new IllegalArgumentException("Invalid match pattern: " + pattern, e);
            }
            return DNChainMatching.dnChainMatch(parsedDNChain, 0, parsedDNPattern, 0);
        }

        private static String toString(List<?> dnChain) {
            if (dnChain == null) {
                return null;
            }
            StringBuilder sb = new StringBuilder();
            Iterator<?> iChain = dnChain.iterator();
            while (iChain.hasNext()) {
                sb.append(iChain.next());
                if (!iChain.hasNext()) continue;
                sb.append("; ");
            }
            return sb.toString();
        }
    }
}


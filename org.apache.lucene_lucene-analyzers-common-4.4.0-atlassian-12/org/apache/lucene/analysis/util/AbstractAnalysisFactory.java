/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.lucene.util.IOUtils
 *  org.apache.lucene.util.Version
 */
package org.apache.lucene.analysis.util;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.util.CharArraySet;
import org.apache.lucene.analysis.util.ResourceLoader;
import org.apache.lucene.analysis.util.WordlistLoader;
import org.apache.lucene.util.IOUtils;
import org.apache.lucene.util.Version;

public abstract class AbstractAnalysisFactory {
    public static final String LUCENE_MATCH_VERSION_PARAM = "luceneMatchVersion";
    private final Map<String, String> originalArgs;
    protected final Version luceneMatchVersion;
    private boolean isExplicitLuceneMatchVersion = false;
    private static final Pattern ITEM_PATTERN = Pattern.compile("[^,\\s]+");
    private static final String CLASS_NAME = "class";

    protected AbstractAnalysisFactory(Map<String, String> args) {
        this.originalArgs = Collections.unmodifiableMap(new HashMap<String, String>(args));
        String version = this.get(args, LUCENE_MATCH_VERSION_PARAM);
        this.luceneMatchVersion = version == null ? null : Version.parseLeniently((String)version);
        args.remove(CLASS_NAME);
    }

    public final Map<String, String> getOriginalArgs() {
        return this.originalArgs;
    }

    protected final void assureMatchVersion() {
        if (this.luceneMatchVersion == null) {
            throw new IllegalArgumentException("Configuration Error: Factory '" + this.getClass().getName() + "' needs a 'luceneMatchVersion' parameter");
        }
    }

    public final Version getLuceneMatchVersion() {
        return this.luceneMatchVersion;
    }

    public String require(Map<String, String> args, String name) {
        String s = args.remove(name);
        if (s == null) {
            throw new IllegalArgumentException("Configuration Error: missing parameter '" + name + "'");
        }
        return s;
    }

    public String require(Map<String, String> args, String name, Collection<String> allowedValues) {
        return this.require(args, name, allowedValues, true);
    }

    public String require(Map<String, String> args, String name, Collection<String> allowedValues, boolean caseSensitive) {
        String s = args.remove(name);
        if (s == null) {
            throw new IllegalArgumentException("Configuration Error: missing parameter '" + name + "'");
        }
        for (String allowedValue : allowedValues) {
            if (!(caseSensitive ? s.equals(allowedValue) : s.equalsIgnoreCase(allowedValue))) continue;
            return s;
        }
        throw new IllegalArgumentException("Configuration Error: '" + name + "' value must be one of " + allowedValues);
    }

    public String get(Map<String, String> args, String name) {
        return args.remove(name);
    }

    public String get(Map<String, String> args, String name, String defaultVal) {
        String s = args.remove(name);
        return s == null ? defaultVal : s;
    }

    public String get(Map<String, String> args, String name, Collection<String> allowedValues) {
        return this.get(args, name, allowedValues, null);
    }

    public String get(Map<String, String> args, String name, Collection<String> allowedValues, String defaultVal) {
        return this.get(args, name, allowedValues, defaultVal, true);
    }

    public String get(Map<String, String> args, String name, Collection<String> allowedValues, String defaultVal, boolean caseSensitive) {
        String s = args.remove(name);
        if (s == null) {
            return defaultVal;
        }
        for (String allowedValue : allowedValues) {
            if (!(caseSensitive ? s.equals(allowedValue) : s.equalsIgnoreCase(allowedValue))) continue;
            return s;
        }
        throw new IllegalArgumentException("Configuration Error: '" + name + "' value must be one of " + allowedValues);
    }

    protected final int requireInt(Map<String, String> args, String name) {
        return Integer.parseInt(this.require(args, name));
    }

    protected final int getInt(Map<String, String> args, String name, int defaultVal) {
        String s = args.remove(name);
        return s == null ? defaultVal : Integer.parseInt(s);
    }

    protected final boolean requireBoolean(Map<String, String> args, String name) {
        return Boolean.parseBoolean(this.require(args, name));
    }

    protected final boolean getBoolean(Map<String, String> args, String name, boolean defaultVal) {
        String s = args.remove(name);
        return s == null ? defaultVal : Boolean.parseBoolean(s);
    }

    protected final float requireFloat(Map<String, String> args, String name) {
        return Float.parseFloat(this.require(args, name));
    }

    protected final float getFloat(Map<String, String> args, String name, float defaultVal) {
        String s = args.remove(name);
        return s == null ? defaultVal : Float.parseFloat(s);
    }

    public char requireChar(Map<String, String> args, String name) {
        return this.require(args, name).charAt(0);
    }

    public char getChar(Map<String, String> args, String name, char defaultValue) {
        String s = args.remove(name);
        if (s == null) {
            return defaultValue;
        }
        if (s.length() != 1) {
            throw new IllegalArgumentException(name + " should be a char. \"" + s + "\" is invalid");
        }
        return s.charAt(0);
    }

    public Set<String> getSet(Map<String, String> args, String name) {
        String s = args.remove(name);
        if (s == null) {
            return null;
        }
        HashSet<String> set = null;
        Matcher matcher = ITEM_PATTERN.matcher(s);
        if (matcher.find()) {
            set = new HashSet<String>();
            set.add(matcher.group(0));
            while (matcher.find()) {
                set.add(matcher.group(0));
            }
        }
        return set;
    }

    protected final Pattern getPattern(Map<String, String> args, String name) {
        try {
            return Pattern.compile(this.require(args, name));
        }
        catch (PatternSyntaxException e) {
            throw new IllegalArgumentException("Configuration Error: '" + name + "' can not be parsed in " + this.getClass().getSimpleName(), e);
        }
    }

    protected final CharArraySet getWordSet(ResourceLoader loader, String wordFiles, boolean ignoreCase) throws IOException {
        this.assureMatchVersion();
        List<String> files = this.splitFileNames(wordFiles);
        CharArraySet words = null;
        if (files.size() > 0) {
            words = new CharArraySet(this.luceneMatchVersion, files.size() * 10, ignoreCase);
            for (String file : files) {
                List<String> wlist = this.getLines(loader, file.trim());
                words.addAll(StopFilter.makeStopSet(this.luceneMatchVersion, wlist, ignoreCase));
            }
        }
        return words;
    }

    protected final List<String> getLines(ResourceLoader loader, String resource) throws IOException {
        return WordlistLoader.getLines(loader.openResource(resource), IOUtils.CHARSET_UTF_8);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    protected final CharArraySet getSnowballWordSet(ResourceLoader loader, String wordFiles, boolean ignoreCase) throws IOException {
        this.assureMatchVersion();
        List<String> files = this.splitFileNames(wordFiles);
        CharArraySet words = null;
        if (files.size() > 0) {
            words = new CharArraySet(this.luceneMatchVersion, files.size() * 10, ignoreCase);
            for (String file : files) {
                InputStream stream = null;
                InputStreamReader reader = null;
                try {
                    stream = loader.openResource(file.trim());
                    CharsetDecoder decoder = IOUtils.CHARSET_UTF_8.newDecoder().onMalformedInput(CodingErrorAction.REPORT).onUnmappableCharacter(CodingErrorAction.REPORT);
                    reader = new InputStreamReader(stream, decoder);
                    WordlistLoader.getSnowballWordSet((Reader)reader, words);
                }
                catch (Throwable throwable) {
                    IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{reader, stream});
                    throw throwable;
                }
                IOUtils.closeWhileHandlingException((Closeable[])new Closeable[]{reader, stream});
            }
        }
        return words;
    }

    protected final List<String> splitFileNames(String fileNames) {
        if (fileNames == null) {
            return Collections.emptyList();
        }
        ArrayList<String> result = new ArrayList<String>();
        for (String file : fileNames.split("(?<!\\\\),")) {
            result.add(file.replaceAll("\\\\(?=,)", ""));
        }
        return result;
    }

    public String getClassArg() {
        String className;
        if (null != this.originalArgs && null != (className = this.originalArgs.get(CLASS_NAME))) {
            return className;
        }
        return this.getClass().getName();
    }

    public boolean isExplicitLuceneMatchVersion() {
        return this.isExplicitLuceneMatchVersion;
    }

    public void setExplicitLuceneMatchVersion(boolean isExplicitLuceneMatchVersion) {
        this.isExplicitLuceneMatchVersion = isExplicitLuceneMatchVersion;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util;

import com.opensymphony.xwork2.util.PatternMatcher;
import com.opensymphony.xwork2.util.WildcardHelper;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collections;
import java.util.HashMap;
import java.util.Vector;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import org.apache.struts2.StrutsException;

public class ClassPathFinder {
    private String pattern;
    private int[] compiledPattern;
    private PatternMatcher<int[]> patternMatcher = new WildcardHelper();
    private Vector<String> compared = new Vector();

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public Vector<String> findMatches() {
        Vector<String> matches = new Vector<String>();
        URL[] parentUrls = this.getClassLoaderURLs();
        this.compiledPattern = this.patternMatcher.compilePattern(this.pattern);
        for (URL url : parentUrls) {
            URI entryURI;
            if (!"file".equals(url.getProtocol())) continue;
            try {
                entryURI = url.toURI();
            }
            catch (URISyntaxException e) {
                continue;
            }
            if (entryURI.getRawQuery() != null) {
                throw new StrutsException("Currently URI with query component isn't supported: " + entryURI.toString());
            }
            File entry = new File(entryURI);
            if (entry.isFile() && entry.toString().endsWith(".jar")) {
                try (ZipInputStream zip = new ZipInputStream(new FileInputStream(entry));){
                    ZipEntry zipEntry = zip.getNextEntry();
                    while (zipEntry != null) {
                        boolean doesMatch = this.patternMatcher.match(new HashMap<String, String>(), zipEntry.getName(), this.compiledPattern);
                        if (doesMatch) {
                            matches.add(zipEntry.getName());
                        }
                        zipEntry = zip.getNextEntry();
                    }
                }
                catch (IOException e) {
                    e.printStackTrace();
                }
                continue;
            }
            Vector<String> results = this.checkEntries(entry.list(), entry, "");
            if (results == null) continue;
            matches.addAll(results);
        }
        return matches;
    }

    private Vector<String> checkEntries(String[] entries, File parent, String prefix) {
        if (entries == null) {
            return null;
        }
        Vector<String> matches = new Vector<String>();
        for (String listEntry : entries) {
            File tempFile = !"".equals(prefix) ? new File(parent, prefix + "/" + listEntry) : new File(parent, listEntry);
            if (tempFile.isDirectory() && !".".equals(listEntry) && !"..".equals(listEntry)) {
                if (!"".equals(prefix)) {
                    matches.addAll(this.checkEntries(tempFile.list(), parent, prefix + "/" + listEntry));
                    continue;
                }
                matches.addAll(this.checkEntries(tempFile.list(), parent, listEntry));
                continue;
            }
            String entryToCheck = "".equals(prefix) ? listEntry : prefix + "/" + listEntry;
            if (this.compared.contains(entryToCheck)) continue;
            this.compared.add(entryToCheck);
            boolean doesMatch = this.patternMatcher.match(new HashMap<String, String>(), entryToCheck, this.compiledPattern);
            if (!doesMatch) continue;
            matches.add(entryToCheck);
        }
        return matches;
    }

    public void setPatternMatcher(PatternMatcher<int[]> patternMatcher) {
        this.patternMatcher = patternMatcher;
    }

    private URL[] getClassLoaderURLs() {
        URL[] urls;
        ClassLoader loader = Thread.currentThread().getContextClassLoader();
        if (!(loader instanceof URLClassLoader)) {
            loader = ClassPathFinder.class.getClassLoader();
        }
        if (loader instanceof URLClassLoader) {
            urls = ((URLClassLoader)loader).getURLs();
        } else {
            try {
                urls = Collections.list(loader.getResources("")).toArray(new URL[0]);
            }
            catch (IOException e) {
                throw new StrutsException("unable to get ClassLoader URLs", e);
            }
        }
        return urls;
    }
}


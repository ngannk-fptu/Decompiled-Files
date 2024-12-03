/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework.util.manifestparser;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.felix.framework.util.manifestparser.ManifestParser;

public class NativeLibrary {
    private String m_libraryFile;
    private String[] m_osnames;
    private String[] m_processors;
    private String[] m_osversions;
    private String[] m_languages;
    private String m_selectionFilter;

    public NativeLibrary(String libraryFile, String[] osnames, String[] processors, String[] osversions, String[] languages, String selectionFilter) throws Exception {
        this.m_libraryFile = libraryFile;
        this.m_osnames = osnames;
        this.m_processors = processors;
        this.m_osversions = osversions;
        this.m_languages = languages;
        this.m_selectionFilter = selectionFilter;
    }

    public String getEntryName() {
        return this.m_libraryFile;
    }

    public String[] getOSNames() {
        return this.m_osnames;
    }

    public String[] getProcessors() {
        return this.m_processors;
    }

    public String[] getOSVersions() {
        return this.m_osversions;
    }

    public String[] getLanguages() {
        return this.m_languages;
    }

    public String getSelectionFilter() {
        return this.m_selectionFilter;
    }

    public boolean match(Map configMap, String name) {
        boolean matched = false;
        if (this.m_libraryFile.equals(name) || this.m_libraryFile.endsWith("/" + name)) {
            matched = true;
        }
        String libname = System.mapLibraryName(name);
        List<String> exts = ManifestParser.parseDelimitedString((String)configMap.get("org.osgi.framework.library.extensions"), ",");
        if (exts == null) {
            exts = new ArrayList<String>();
        }
        if (libname.endsWith(".jnilib") && this.m_libraryFile.endsWith(".dylib")) {
            exts.add("dylib");
        }
        if (libname.endsWith(".dylib") && this.m_libraryFile.endsWith(".jnilib")) {
            exts.add("jnilib");
        }
        int extIdx = -1;
        while (!matched && extIdx < exts.size()) {
            if (this.m_libraryFile.equals(libname) || this.m_libraryFile.endsWith("/" + libname)) {
                matched = true;
            }
            if (matched || ++extIdx >= exts.size()) continue;
            int idx = libname.lastIndexOf(".");
            libname = idx < 0 ? libname + "." + exts.get(extIdx) : libname.substring(0, idx + 1) + exts.get(extIdx);
        }
        return matched;
    }

    public String toString() {
        if (this.m_libraryFile != null) {
            int i;
            StringBuilder sb = new StringBuilder();
            sb.append(this.m_libraryFile);
            for (i = 0; this.m_osnames != null && i < this.m_osnames.length; ++i) {
                sb.append(';');
                sb.append("osname");
                sb.append('=');
                sb.append(this.m_osnames[i]);
            }
            for (i = 0; this.m_processors != null && i < this.m_processors.length; ++i) {
                sb.append(';');
                sb.append("processor");
                sb.append('=');
                sb.append(this.m_processors[i]);
            }
            for (i = 0; this.m_osversions != null && i < this.m_osversions.length; ++i) {
                sb.append(';');
                sb.append("osversion");
                sb.append('=');
                sb.append(this.m_osversions[i]);
            }
            for (i = 0; this.m_languages != null && i < this.m_languages.length; ++i) {
                sb.append(';');
                sb.append("language");
                sb.append('=');
                sb.append(this.m_languages[i]);
            }
            if (this.m_selectionFilter != null) {
                sb.append(';');
                sb.append("selection-filter");
                sb.append('=');
                sb.append('\'');
                sb.append(this.m_selectionFilter);
            }
            return sb.toString();
        }
        return "*";
    }
}


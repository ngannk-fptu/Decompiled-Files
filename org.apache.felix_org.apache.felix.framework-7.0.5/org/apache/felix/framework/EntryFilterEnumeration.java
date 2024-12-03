/*
 * Decompiled with CFR 0.152.
 */
package org.apache.felix.framework;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Set;
import org.apache.felix.framework.BundleRevisionImpl;
import org.apache.felix.framework.capabilityset.SimpleFilter;
import org.apache.felix.framework.util.Util;
import org.osgi.framework.wiring.BundleRevision;

class EntryFilterEnumeration
implements Enumeration {
    private final BundleRevision m_revision;
    private final List<Enumeration> m_enumerations;
    private final List<BundleRevision> m_revisions;
    private int m_revisionIndex = 0;
    private final String m_path;
    private final List<String> m_filePattern;
    private final boolean m_recurse;
    private final boolean m_isURLValues;
    private final Set<String> m_dirEntries = new HashSet<String>();
    private final List<Object> m_nextEntries = new ArrayList<Object>(2);

    public EntryFilterEnumeration(BundleRevision revision, boolean includeFragments, String path, String filePattern, boolean recurse, boolean isURLValues) {
        this.m_revision = revision;
        List<BundleRevision> fragments = Util.getFragments(revision.getWiring());
        this.m_revisions = includeFragments && !fragments.isEmpty() ? fragments : new ArrayList<BundleRevision>(1);
        this.m_revisions.add(0, this.m_revision);
        this.m_enumerations = new ArrayList<Enumeration>(this.m_revisions.size());
        for (int i = 0; i < this.m_revisions.size(); ++i) {
            this.m_enumerations.add(((BundleRevisionImpl)this.m_revisions.get(i)).getContent() != null ? ((BundleRevisionImpl)this.m_revisions.get(i)).getContent().getEntries() : null);
        }
        this.m_recurse = recurse;
        this.m_isURLValues = isURLValues;
        if (path == null) {
            throw new IllegalArgumentException("The path for findEntries() cannot be null.");
        }
        if (path.length() > 0 && path.charAt(0) == '/') {
            path = path.substring(1);
        }
        if (path.length() > 0 && path.charAt(path.length() - 1) != '/') {
            path = path + "/";
        }
        this.m_path = path;
        filePattern = filePattern == null ? "*" : filePattern;
        this.m_filePattern = SimpleFilter.parseSubstring(filePattern);
        this.findNext();
    }

    @Override
    public synchronized boolean hasMoreElements() {
        return !this.m_nextEntries.isEmpty();
    }

    public synchronized Object nextElement() {
        if (this.m_nextEntries.isEmpty()) {
            throw new NoSuchElementException("No more entries.");
        }
        Object last = this.m_nextEntries.remove(0);
        this.findNext();
        return last;
    }

    private void findNext() {
        if (this.m_enumerations == null) {
            return;
        }
        while (this.m_revisionIndex < this.m_enumerations.size() && this.m_nextEntries.isEmpty()) {
            while (this.m_enumerations.get(this.m_revisionIndex) != null && this.m_enumerations.get(this.m_revisionIndex).hasMoreElements() && this.m_nextEntries.isEmpty()) {
                String entryName = (String)this.m_enumerations.get(this.m_revisionIndex).nextElement();
                if (entryName.equals(this.m_path) || !entryName.startsWith(this.m_path)) continue;
                URL entryURL = null;
                int dirSlashIdx = entryName.indexOf(47, this.m_path.length());
                if (dirSlashIdx >= 0) {
                    int subDirSlashIdx = dirSlashIdx;
                    do {
                        String dir;
                        if (!this.m_dirEntries.contains(dir = entryName.substring(0, subDirSlashIdx + 1))) {
                            this.m_dirEntries.add(dir);
                            if (SimpleFilter.compareSubstring(this.m_filePattern, EntryFilterEnumeration.getLastPathElement(dir))) {
                                if (this.m_isURLValues) {
                                    entryURL = entryURL == null ? ((BundleRevisionImpl)this.m_revisions.get(this.m_revisionIndex)).getEntry(entryName) : entryURL;
                                    try {
                                        this.m_nextEntries.add(new URL(entryURL, "/" + dir));
                                    }
                                    catch (MalformedURLException malformedURLException) {}
                                } else {
                                    this.m_nextEntries.add(dir);
                                }
                            }
                        }
                        subDirSlashIdx = entryName.indexOf(47, dir.length());
                    } while (this.m_recurse && subDirSlashIdx >= 0);
                }
                if (this.m_dirEntries.contains(entryName) || !this.m_recurse && dirSlashIdx >= 0 && dirSlashIdx != entryName.length() - 1 || !SimpleFilter.compareSubstring(this.m_filePattern, EntryFilterEnumeration.getLastPathElement(entryName))) continue;
                if (this.m_isURLValues) {
                    entryURL = entryURL == null ? ((BundleRevisionImpl)this.m_revisions.get(this.m_revisionIndex)).getEntry(entryName) : entryURL;
                    this.m_nextEntries.add(entryURL);
                    continue;
                }
                this.m_nextEntries.add(entryName);
            }
            if (!this.m_nextEntries.isEmpty()) continue;
            ++this.m_revisionIndex;
            this.m_dirEntries.clear();
        }
    }

    private static String getLastPathElement(String entryName) {
        int endIdx = entryName.charAt(entryName.length() - 1) == '/' ? entryName.length() - 1 : entryName.length();
        int startIdx = entryName.charAt(entryName.length() - 1) == '/' ? entryName.lastIndexOf(47, endIdx - 1) + 1 : entryName.lastIndexOf(47, endIdx) + 1;
        return entryName.substring(startIdx, endIdx);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.resources;

import java.io.File;
import java.util.Collections;
import java.util.Iterator;
import java.util.Vector;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.Resource;
import org.apache.tools.ant.types.ResourceCollection;
import org.apache.tools.ant.types.resources.FileResourceIterator;
import org.apache.tools.ant.types.selectors.AbstractSelectorContainer;
import org.apache.tools.ant.types.selectors.FileSelector;

public class Files
extends AbstractSelectorContainer
implements ResourceCollection {
    private PatternSet defaultPatterns = new PatternSet();
    private Vector<PatternSet> additionalPatterns = new Vector();
    private boolean useDefaultExcludes = true;
    private boolean caseSensitive = true;
    private boolean followSymlinks = true;
    private DirectoryScanner ds = null;

    public Files() {
    }

    protected Files(Files f) {
        this.defaultPatterns = f.defaultPatterns;
        this.additionalPatterns = f.additionalPatterns;
        this.useDefaultExcludes = f.useDefaultExcludes;
        this.caseSensitive = f.caseSensitive;
        this.followSymlinks = f.followSymlinks;
        this.ds = f.ds;
        this.setProject(f.getProject());
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.hasPatterns(this.defaultPatterns)) {
            throw this.tooManyAttributes();
        }
        if (!this.additionalPatterns.isEmpty()) {
            throw this.noChildrenAllowed();
        }
        if (this.hasSelectors()) {
            throw this.noChildrenAllowed();
        }
        super.setRefid(r);
    }

    public synchronized PatternSet createPatternSet() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        PatternSet patterns = new PatternSet();
        this.additionalPatterns.addElement(patterns);
        this.ds = null;
        this.setChecked(false);
        return patterns;
    }

    public synchronized PatternSet.NameEntry createInclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createInclude();
    }

    public synchronized PatternSet.NameEntry createIncludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createIncludesFile();
    }

    public synchronized PatternSet.NameEntry createExclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createExclude();
    }

    public synchronized PatternSet.NameEntry createExcludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.ds = null;
        return this.defaultPatterns.createExcludesFile();
    }

    public synchronized void setIncludes(String includes) {
        this.checkAttributesAllowed();
        this.defaultPatterns.setIncludes(includes);
        this.ds = null;
    }

    public synchronized void appendIncludes(String[] includes) {
        this.checkAttributesAllowed();
        if (includes != null) {
            for (String include : includes) {
                this.defaultPatterns.createInclude().setName(include);
            }
            this.ds = null;
        }
    }

    public synchronized void setExcludes(String excludes) {
        this.checkAttributesAllowed();
        this.defaultPatterns.setExcludes(excludes);
        this.ds = null;
    }

    public synchronized void appendExcludes(String[] excludes) {
        this.checkAttributesAllowed();
        if (excludes != null) {
            for (String exclude : excludes) {
                this.defaultPatterns.createExclude().setName(exclude);
            }
            this.ds = null;
        }
    }

    public synchronized void setIncludesfile(File incl) throws BuildException {
        this.checkAttributesAllowed();
        this.defaultPatterns.setIncludesfile(incl);
        this.ds = null;
    }

    public synchronized void setExcludesfile(File excl) throws BuildException {
        this.checkAttributesAllowed();
        this.defaultPatterns.setExcludesfile(excl);
        this.ds = null;
    }

    public synchronized void setDefaultexcludes(boolean useDefaultExcludes) {
        this.checkAttributesAllowed();
        this.useDefaultExcludes = useDefaultExcludes;
        this.ds = null;
    }

    public synchronized boolean getDefaultexcludes() {
        return this.isReference() ? this.getRef().getDefaultexcludes() : this.useDefaultExcludes;
    }

    public synchronized void setCaseSensitive(boolean caseSensitive) {
        this.checkAttributesAllowed();
        this.caseSensitive = caseSensitive;
        this.ds = null;
    }

    public synchronized boolean isCaseSensitive() {
        return this.isReference() ? this.getRef().isCaseSensitive() : this.caseSensitive;
    }

    public synchronized void setFollowSymlinks(boolean followSymlinks) {
        this.checkAttributesAllowed();
        this.followSymlinks = followSymlinks;
        this.ds = null;
    }

    public synchronized boolean isFollowSymlinks() {
        return this.isReference() ? this.getRef().isFollowSymlinks() : this.followSymlinks;
    }

    @Override
    public synchronized Iterator<Resource> iterator() {
        if (this.isReference()) {
            return this.getRef().iterator();
        }
        this.ensureDirectoryScannerSetup();
        this.ds.scan();
        int fct = this.ds.getIncludedFilesCount();
        int dct = this.ds.getIncludedDirsCount();
        if (fct + dct == 0) {
            return Collections.emptyIterator();
        }
        FileResourceIterator result = new FileResourceIterator(this.getProject());
        if (fct > 0) {
            result.addFiles(this.ds.getIncludedFiles());
        }
        if (dct > 0) {
            result.addFiles(this.ds.getIncludedDirectories());
        }
        return result;
    }

    @Override
    public synchronized int size() {
        if (this.isReference()) {
            return this.getRef().size();
        }
        this.ensureDirectoryScannerSetup();
        this.ds.scan();
        return this.ds.getIncludedFilesCount() + this.ds.getIncludedDirsCount();
    }

    public synchronized boolean hasPatterns() {
        if (this.isReference()) {
            return this.getRef().hasPatterns();
        }
        this.dieOnCircularReference();
        return this.hasPatterns(this.defaultPatterns) || this.additionalPatterns.stream().anyMatch(this::hasPatterns);
    }

    @Override
    public synchronized void appendSelector(FileSelector selector) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        super.appendSelector(selector);
        this.ds = null;
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef().toString();
        }
        return this.isEmpty() ? "" : this.stream().map(Object::toString).collect(Collectors.joining(File.pathSeparator));
    }

    @Override
    public synchronized Object clone() {
        if (this.isReference()) {
            return this.getRef().clone();
        }
        Files f = (Files)super.clone();
        f.defaultPatterns = (PatternSet)this.defaultPatterns.clone();
        f.additionalPatterns = new Vector(this.additionalPatterns.size());
        for (PatternSet ps : this.additionalPatterns) {
            f.additionalPatterns.add((PatternSet)ps.clone());
        }
        return f;
    }

    public String[] mergeIncludes(Project p) {
        return this.mergePatterns(p).getIncludePatterns(p);
    }

    public String[] mergeExcludes(Project p) {
        return this.mergePatterns(p).getExcludePatterns(p);
    }

    public synchronized PatternSet mergePatterns(Project p) {
        if (this.isReference()) {
            return this.getRef().mergePatterns(p);
        }
        this.dieOnCircularReference();
        PatternSet ps = new PatternSet();
        ps.append(this.defaultPatterns, p);
        this.additionalPatterns.forEach((? super E pat) -> ps.append((PatternSet)pat, p));
        return ps;
    }

    @Override
    public boolean isFilesystemOnly() {
        return true;
    }

    protected Files getRef() {
        return this.getCheckedRef(Files.class);
    }

    private synchronized void ensureDirectoryScannerSetup() {
        this.dieOnCircularReference();
        if (this.ds == null) {
            this.ds = new DirectoryScanner();
            PatternSet ps = this.mergePatterns(this.getProject());
            this.ds.setIncludes(ps.getIncludePatterns(this.getProject()));
            this.ds.setExcludes(ps.getExcludePatterns(this.getProject()));
            this.ds.setSelectors(this.getSelectors(this.getProject()));
            if (this.useDefaultExcludes) {
                this.ds.addDefaultExcludes();
            }
            this.ds.setCaseSensitive(this.caseSensitive);
            this.ds.setFollowSymlinks(this.followSymlinks);
        }
    }

    private boolean hasPatterns(PatternSet ps) {
        String[] includePatterns = ps.getIncludePatterns(this.getProject());
        String[] excludePatterns = ps.getExcludePatterns(this.getProject());
        return includePatterns != null && includePatterns.length > 0 || excludePatterns != null && excludePatterns.length > 0;
    }
}


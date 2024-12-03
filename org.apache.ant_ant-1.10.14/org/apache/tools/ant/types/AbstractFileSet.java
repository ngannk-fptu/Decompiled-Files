/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.FileScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.PatternSet;
import org.apache.tools.ant.types.Reference;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.DifferentSelector;
import org.apache.tools.ant.types.selectors.ExecutableSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.OwnedBySelector;
import org.apache.tools.ant.types.selectors.PosixGroupSelector;
import org.apache.tools.ant.types.selectors.PosixPermissionsSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.ReadableSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SelectorContainer;
import org.apache.tools.ant.types.selectors.SelectorScanner;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.SymlinkSelector;
import org.apache.tools.ant.types.selectors.TypeSelector;
import org.apache.tools.ant.types.selectors.WritableSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;

public abstract class AbstractFileSet
extends DataType
implements Cloneable,
SelectorContainer {
    private PatternSet defaultPatterns = new PatternSet();
    private List<PatternSet> additionalPatterns = new ArrayList<PatternSet>();
    private List<FileSelector> selectors = new ArrayList<FileSelector>();
    private File dir;
    private boolean fileAttributeUsed;
    private boolean useDefaultExcludes = true;
    private boolean caseSensitive = true;
    private boolean followSymlinks = true;
    private boolean errorOnMissingDir = true;
    private int maxLevelsOfSymlinks = 5;
    private DirectoryScanner directoryScanner = null;

    public AbstractFileSet() {
    }

    protected AbstractFileSet(AbstractFileSet fileset) {
        this.dir = fileset.dir;
        this.defaultPatterns = fileset.defaultPatterns;
        this.additionalPatterns = fileset.additionalPatterns;
        this.selectors = fileset.selectors;
        this.useDefaultExcludes = fileset.useDefaultExcludes;
        this.caseSensitive = fileset.caseSensitive;
        this.followSymlinks = fileset.followSymlinks;
        this.errorOnMissingDir = fileset.errorOnMissingDir;
        this.maxLevelsOfSymlinks = fileset.maxLevelsOfSymlinks;
        this.setProject(fileset.getProject());
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (this.dir != null || this.defaultPatterns.hasPatterns(this.getProject())) {
            throw this.tooManyAttributes();
        }
        if (!this.additionalPatterns.isEmpty()) {
            throw this.noChildrenAllowed();
        }
        if (!this.selectors.isEmpty()) {
            throw this.noChildrenAllowed();
        }
        super.setRefid(r);
    }

    public synchronized void setDir(File dir) throws BuildException {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.fileAttributeUsed && !this.getDir().equals(dir)) {
            throw this.dirAndFileAreMutuallyExclusive();
        }
        this.dir = dir;
        this.directoryScanner = null;
    }

    public File getDir() {
        return this.getDir(this.getProject());
    }

    public synchronized File getDir(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getDir(p);
        }
        this.dieOnCircularReference();
        return this.dir;
    }

    public synchronized PatternSet createPatternSet() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        PatternSet patterns = new PatternSet();
        this.additionalPatterns.add(patterns);
        this.directoryScanner = null;
        return patterns;
    }

    public synchronized PatternSet.NameEntry createInclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createInclude();
    }

    public synchronized PatternSet.NameEntry createIncludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createIncludesFile();
    }

    public synchronized PatternSet.NameEntry createExclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createExclude();
    }

    public synchronized PatternSet.NameEntry createExcludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.directoryScanner = null;
        return this.defaultPatterns.createExcludesFile();
    }

    public synchronized void setFile(File file) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (this.fileAttributeUsed) {
            String[] includes;
            if (this.getDir().equals(file.getParentFile()) && (includes = this.defaultPatterns.getIncludePatterns(this.getProject())).length == 1 && includes[0].equals(file.getName())) {
                return;
            }
            throw new BuildException("setFile cannot be called twice with different arguments");
        }
        if (this.getDir() != null) {
            throw this.dirAndFileAreMutuallyExclusive();
        }
        this.setDir(file.getParentFile());
        this.fileAttributeUsed = true;
        this.createInclude().setName(file.getName());
    }

    public synchronized void setIncludes(String includes) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.defaultPatterns.setIncludes(includes);
        this.directoryScanner = null;
    }

    public synchronized void appendIncludes(String[] includes) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (includes != null) {
            for (String include : includes) {
                this.defaultPatterns.createInclude().setName(include);
            }
            this.directoryScanner = null;
        }
    }

    public synchronized void setExcludes(String excludes) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.defaultPatterns.setExcludes(excludes);
        this.directoryScanner = null;
    }

    public synchronized void appendExcludes(String[] excludes) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (excludes != null) {
            for (String exclude : excludes) {
                this.defaultPatterns.createExclude().setName(exclude);
            }
            this.directoryScanner = null;
        }
    }

    public synchronized void setIncludesfile(File incl) throws BuildException {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.defaultPatterns.setIncludesfile(incl);
        this.directoryScanner = null;
    }

    public synchronized void setExcludesfile(File excl) throws BuildException {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.defaultPatterns.setExcludesfile(excl);
        this.directoryScanner = null;
    }

    public synchronized void setDefaultexcludes(boolean useDefaultExcludes) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.useDefaultExcludes = useDefaultExcludes;
        this.directoryScanner = null;
    }

    public synchronized boolean getDefaultexcludes() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).getDefaultexcludes();
        }
        this.dieOnCircularReference();
        return this.useDefaultExcludes;
    }

    public synchronized void setCaseSensitive(boolean caseSensitive) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.caseSensitive = caseSensitive;
        this.directoryScanner = null;
    }

    public synchronized boolean isCaseSensitive() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).isCaseSensitive();
        }
        this.dieOnCircularReference();
        return this.caseSensitive;
    }

    public synchronized void setFollowSymlinks(boolean followSymlinks) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.followSymlinks = followSymlinks;
        this.directoryScanner = null;
    }

    public synchronized boolean isFollowSymlinks() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).isCaseSensitive();
        }
        this.dieOnCircularReference();
        return this.followSymlinks;
    }

    public void setMaxLevelsOfSymlinks(int max) {
        this.maxLevelsOfSymlinks = max;
    }

    public int getMaxLevelsOfSymlinks() {
        return this.maxLevelsOfSymlinks;
    }

    public void setErrorOnMissingDir(boolean errorOnMissingDir) {
        this.errorOnMissingDir = errorOnMissingDir;
    }

    public boolean getErrorOnMissingDir() {
        return this.errorOnMissingDir;
    }

    public DirectoryScanner getDirectoryScanner() {
        return this.getDirectoryScanner(this.getProject());
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public DirectoryScanner getDirectoryScanner(Project p) {
        DirectoryScanner ds;
        if (this.isReference()) {
            return this.getRef(p).getDirectoryScanner(p);
        }
        this.dieOnCircularReference();
        AbstractFileSet abstractFileSet = this;
        synchronized (abstractFileSet) {
            if (this.directoryScanner != null && p == this.getProject()) {
                ds = this.directoryScanner;
            } else {
                if (this.dir == null) {
                    throw new BuildException("No directory specified for %s.", this.getDataTypeName());
                }
                if (!this.dir.exists() && this.errorOnMissingDir) {
                    throw new BuildException(this.dir.getAbsolutePath() + " does not exist.");
                }
                if (!this.dir.isDirectory() && this.dir.exists()) {
                    throw new BuildException("%s is not a directory.", this.dir.getAbsolutePath());
                }
                ds = new DirectoryScanner();
                this.setupDirectoryScanner(ds, p);
                ds.setFollowSymlinks(this.followSymlinks);
                ds.setErrorOnMissingDir(this.errorOnMissingDir);
                ds.setMaxLevelsOfSymlinks(this.maxLevelsOfSymlinks);
                this.directoryScanner = p == this.getProject() ? ds : this.directoryScanner;
            }
        }
        ds.scan();
        return ds;
    }

    public void setupDirectoryScanner(FileScanner ds) {
        this.setupDirectoryScanner(ds, this.getProject());
    }

    public synchronized void setupDirectoryScanner(FileScanner ds, Project p) {
        if (this.isReference()) {
            this.getRef(p).setupDirectoryScanner(ds, p);
            return;
        }
        this.dieOnCircularReference(p);
        if (ds == null) {
            throw new IllegalArgumentException("ds cannot be null");
        }
        ds.setBasedir(this.dir);
        PatternSet ps = this.mergePatterns(p);
        p.log(this.getDataTypeName() + ": Setup scanner in dir " + this.dir + " with " + ps, 4);
        ds.setIncludes(ps.getIncludePatterns(p));
        ds.setExcludes(ps.getExcludePatterns(p));
        if (ds instanceof SelectorScanner) {
            SelectorScanner ss = (SelectorScanner)((Object)ds);
            ss.setSelectors(this.getSelectors(p));
        }
        if (this.useDefaultExcludes) {
            ds.addDefaultExcludes();
        }
        ds.setCaseSensitive(this.caseSensitive);
    }

    protected AbstractFileSet getRef(Project p) {
        return this.getCheckedRef(AbstractFileSet.class, this.getDataTypeName(), p);
    }

    @Override
    public synchronized boolean hasSelectors() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).hasSelectors();
        }
        this.dieOnCircularReference();
        return !this.selectors.isEmpty();
    }

    public synchronized boolean hasPatterns() {
        if (this.isReference() && this.getProject() != null) {
            return this.getRef(this.getProject()).hasPatterns();
        }
        this.dieOnCircularReference();
        return this.defaultPatterns.hasPatterns(this.getProject()) || this.additionalPatterns.stream().anyMatch(ps -> ps.hasPatterns(this.getProject()));
    }

    @Override
    public synchronized int selectorCount() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).selectorCount();
        }
        this.dieOnCircularReference();
        return this.selectors.size();
    }

    @Override
    public synchronized FileSelector[] getSelectors(Project p) {
        if (this.isReference()) {
            return this.getRef(this.getProject()).getSelectors(p);
        }
        this.dieOnCircularReference(p);
        return this.selectors.toArray(new FileSelector[0]);
    }

    @Override
    public synchronized Enumeration<FileSelector> selectorElements() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).selectorElements();
        }
        this.dieOnCircularReference();
        return Collections.enumeration(this.selectors);
    }

    @Override
    public synchronized void appendSelector(FileSelector selector) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.selectors.add(selector);
        this.directoryScanner = null;
        this.setChecked(false);
    }

    @Override
    public void addSelector(SelectSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addAnd(AndSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addOr(OrSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addNot(NotSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addNone(NoneSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addMajority(MajoritySelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDate(DateSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addSize(SizeSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDifferent(DifferentSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addFilename(FilenameSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addType(TypeSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addCustom(ExtendSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addContains(ContainsSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addPresent(PresentSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDepth(DepthSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addDepend(DependSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addContainsRegexp(ContainsRegexpSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public void addModified(ModifiedSelector selector) {
        this.appendSelector(selector);
    }

    public void addReadable(ReadableSelector r) {
        this.appendSelector(r);
    }

    public void addWritable(WritableSelector w) {
        this.appendSelector(w);
    }

    public void addExecutable(ExecutableSelector e) {
        this.appendSelector(e);
    }

    public void addSymlink(SymlinkSelector e) {
        this.appendSelector(e);
    }

    public void addOwnedBy(OwnedBySelector o) {
        this.appendSelector(o);
    }

    public void addPosixGroup(PosixGroupSelector o) {
        this.appendSelector(o);
    }

    public void addPosixPermissions(PosixPermissionsSelector o) {
        this.appendSelector(o);
    }

    @Override
    public void add(FileSelector selector) {
        this.appendSelector(selector);
    }

    @Override
    public String toString() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).toString();
        }
        this.dieOnCircularReference();
        return String.join((CharSequence)";", this.getDirectoryScanner().getIncludedFiles());
    }

    @Override
    public synchronized Object clone() {
        if (this.isReference()) {
            return this.getRef(this.getProject()).clone();
        }
        try {
            AbstractFileSet fs = (AbstractFileSet)super.clone();
            fs.defaultPatterns = (PatternSet)this.defaultPatterns.clone();
            fs.additionalPatterns = this.additionalPatterns.stream().map(PatternSet::clone).map(PatternSet.class::cast).collect(Collectors.toList());
            fs.selectors = new ArrayList<FileSelector>(this.selectors);
            return fs;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public String[] mergeIncludes(Project p) {
        return this.mergePatterns(p).getIncludePatterns(p);
    }

    public String[] mergeExcludes(Project p) {
        return this.mergePatterns(p).getExcludePatterns(p);
    }

    public synchronized PatternSet mergePatterns(Project p) {
        if (this.isReference()) {
            return this.getRef(p).mergePatterns(p);
        }
        this.dieOnCircularReference();
        PatternSet ps = (PatternSet)this.defaultPatterns.clone();
        this.additionalPatterns.forEach(pat -> ps.append((PatternSet)pat, p));
        return ps;
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            this.selectors.stream().filter(DataType.class::isInstance).map(DataType.class::cast).forEach(type -> AbstractFileSet.pushAndInvokeCircularReferenceCheck(type, stk, p));
            this.additionalPatterns.forEach(ps -> AbstractFileSet.pushAndInvokeCircularReferenceCheck(ps, stk, p));
            this.setChecked(true);
        }
    }

    private BuildException dirAndFileAreMutuallyExclusive() {
        return new BuildException("you can only specify one of the dir and file attributes");
    }
}


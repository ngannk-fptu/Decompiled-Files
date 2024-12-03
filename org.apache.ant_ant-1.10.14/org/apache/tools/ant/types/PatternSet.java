/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.StringTokenizer;
import java.util.function.Predicate;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.PropertyHelper;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

public class PatternSet
extends DataType
implements Cloneable {
    private List<NameEntry> includeList = new ArrayList<NameEntry>();
    private List<NameEntry> excludeList = new ArrayList<NameEntry>();
    private List<PatternFileNameEntry> includesFileList = new ArrayList<PatternFileNameEntry>();
    private List<PatternFileNameEntry> excludesFileList = new ArrayList<PatternFileNameEntry>();

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (!this.includeList.isEmpty() || !this.excludeList.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    public void addConfiguredPatternset(PatternSet p) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        String[] nestedIncludes = p.getIncludePatterns(this.getProject());
        String[] nestedExcludes = p.getExcludePatterns(this.getProject());
        if (nestedIncludes != null) {
            for (String nestedInclude : nestedIncludes) {
                this.createInclude().setName(nestedInclude);
            }
        }
        if (nestedExcludes != null) {
            for (String nestedExclude : nestedExcludes) {
                this.createExclude().setName(nestedExclude);
            }
        }
    }

    public NameEntry createInclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        return this.addPatternToList(this.includeList);
    }

    public NameEntry createIncludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        return this.addPatternFileToList(this.includesFileList);
    }

    public NameEntry createExclude() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        return this.addPatternToList(this.excludeList);
    }

    public NameEntry createExcludesFile() {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        return this.addPatternFileToList(this.excludesFileList);
    }

    public void setIncludes(String includes) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (includes != null && !includes.isEmpty()) {
            StringTokenizer tok = new StringTokenizer(includes, ", ", false);
            while (tok.hasMoreTokens()) {
                this.createInclude().setName(tok.nextToken());
            }
        }
    }

    public void setExcludes(String excludes) {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        if (excludes != null && !excludes.isEmpty()) {
            StringTokenizer tok = new StringTokenizer(excludes, ", ", false);
            while (tok.hasMoreTokens()) {
                this.createExclude().setName(tok.nextToken());
            }
        }
    }

    private NameEntry addPatternToList(List<NameEntry> list) {
        NameEntry result = new NameEntry();
        list.add(result);
        return result;
    }

    private PatternFileNameEntry addPatternFileToList(List<PatternFileNameEntry> list) {
        PatternFileNameEntry result = new PatternFileNameEntry();
        list.add(result);
        return result;
    }

    public void setIncludesfile(File includesFile) throws BuildException {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createIncludesFile().setName(includesFile.getAbsolutePath());
    }

    public void setExcludesfile(File excludesFile) throws BuildException {
        if (this.isReference()) {
            throw this.tooManyAttributes();
        }
        this.createExcludesFile().setName(excludesFile.getAbsolutePath());
    }

    private void readPatterns(File patternfile, String encoding, List<NameEntry> patternlist, Project p) throws BuildException {
        try (InputStreamReader r = encoding == null ? new FileReader(patternfile) : new InputStreamReader((InputStream)new FileInputStream(patternfile), encoding);
             BufferedReader patternReader = new BufferedReader(r);){
            patternReader.lines().filter(((Predicate<String>)String::isEmpty).negate()).map(p::replaceProperties).forEach(line -> this.addPatternToList(patternlist).setName((String)line));
        }
        catch (IOException ioe) {
            throw new BuildException("An error occurred while reading from pattern file: " + patternfile, ioe);
        }
    }

    public void append(PatternSet other, Project p) {
        String[] excl;
        if (this.isReference()) {
            throw new BuildException("Cannot append to a reference");
        }
        this.dieOnCircularReference(p);
        String[] incl = other.getIncludePatterns(p);
        if (incl != null) {
            for (String include : incl) {
                this.createInclude().setName(include);
            }
        }
        if ((excl = other.getExcludePatterns(p)) != null) {
            for (String exclude : excl) {
                this.createExclude().setName(exclude);
            }
        }
    }

    public String[] getIncludePatterns(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getIncludePatterns(p);
        }
        this.dieOnCircularReference(p);
        this.readFiles(p);
        return this.makeArray(this.includeList, p);
    }

    public String[] getExcludePatterns(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getExcludePatterns(p);
        }
        this.dieOnCircularReference(p);
        this.readFiles(p);
        return this.makeArray(this.excludeList, p);
    }

    public boolean hasPatterns(Project p) {
        if (this.isReference()) {
            return this.getRef(p).hasPatterns(p);
        }
        this.dieOnCircularReference(p);
        return !this.includesFileList.isEmpty() || !this.excludesFileList.isEmpty() || !this.includeList.isEmpty() || !this.excludeList.isEmpty();
    }

    private PatternSet getRef(Project p) {
        return this.getCheckedRef(PatternSet.class, this.getDataTypeName(), p);
    }

    private String[] makeArray(List<NameEntry> list, Project p) {
        if (list.isEmpty()) {
            return null;
        }
        return (String[])list.stream().map(ne -> ne.evalName(p)).filter(Objects::nonNull).filter(pattern -> !pattern.isEmpty()).toArray(String[]::new);
    }

    private void readFiles(Project p) {
        String fileName;
        if (!this.includesFileList.isEmpty()) {
            for (PatternFileNameEntry ne : this.includesFileList) {
                fileName = ne.evalName(p);
                if (fileName == null) continue;
                File inclFile = p.resolveFile(fileName);
                if (!inclFile.exists()) {
                    throw new BuildException("Includesfile " + inclFile.getAbsolutePath() + " not found.");
                }
                this.readPatterns(inclFile, ne.getEncoding(), this.includeList, p);
            }
            this.includesFileList.clear();
        }
        if (!this.excludesFileList.isEmpty()) {
            for (PatternFileNameEntry ne : this.excludesFileList) {
                fileName = ne.evalName(p);
                if (fileName == null) continue;
                File exclFile = p.resolveFile(fileName);
                if (!exclFile.exists()) {
                    throw new BuildException("Excludesfile " + exclFile.getAbsolutePath() + " not found.");
                }
                this.readPatterns(exclFile, ne.getEncoding(), this.excludeList, p);
            }
            this.excludesFileList.clear();
        }
    }

    @Override
    public String toString() {
        return String.format("patternSet{ includes: %s excludes: %s }", this.includeList, this.excludeList);
    }

    @Override
    public Object clone() {
        try {
            PatternSet ps = (PatternSet)super.clone();
            ps.includeList = new ArrayList<NameEntry>(this.includeList);
            ps.excludeList = new ArrayList<NameEntry>(this.excludeList);
            ps.includesFileList = new ArrayList<PatternFileNameEntry>(this.includesFileList);
            ps.excludesFileList = new ArrayList<PatternFileNameEntry>(this.excludesFileList);
            return ps;
        }
        catch (CloneNotSupportedException e) {
            throw new BuildException(e);
        }
    }

    public void addConfiguredInvert(PatternSet p) {
        this.addConfiguredPatternset(new InvertedPatternSet(p));
    }

    public class NameEntry {
        private String name;
        private Object ifCond;
        private Object unlessCond;

        public void setName(String name) {
            this.name = name;
        }

        public void setIf(Object cond) {
            this.ifCond = cond;
        }

        public void setIf(String cond) {
            this.setIf((Object)cond);
        }

        public void setUnless(Object cond) {
            this.unlessCond = cond;
        }

        public void setUnless(String cond) {
            this.setUnless((Object)cond);
        }

        public String getName() {
            return this.name;
        }

        public String evalName(Project p) {
            return this.valid(p) ? this.name : null;
        }

        private boolean valid(Project p) {
            PropertyHelper ph = PropertyHelper.getPropertyHelper(p);
            return ph.testIfCondition(this.ifCond) && ph.testUnlessCondition(this.unlessCond);
        }

        public String toString() {
            StringBuilder buf = new StringBuilder();
            if (this.name == null) {
                buf.append("noname");
            } else {
                buf.append(this.name);
            }
            if (this.ifCond != null || this.unlessCond != null) {
                buf.append(":");
                String connector = "";
                if (this.ifCond != null) {
                    buf.append("if->");
                    buf.append(this.ifCond);
                    connector = ";";
                }
                if (this.unlessCond != null) {
                    buf.append(connector);
                    buf.append("unless->");
                    buf.append(this.unlessCond);
                }
            }
            return buf.toString();
        }
    }

    public class PatternFileNameEntry
    extends NameEntry {
        private String encoding;

        public final void setEncoding(String encoding) {
            this.encoding = encoding;
        }

        public final String getEncoding() {
            return this.encoding;
        }

        @Override
        public String toString() {
            String baseString = super.toString();
            return this.encoding == null ? baseString : baseString + ";encoding->" + this.encoding;
        }
    }

    private static final class InvertedPatternSet
    extends PatternSet {
        private InvertedPatternSet(PatternSet p) {
            this.setProject(p.getProject());
            this.addConfiguredPatternset(p);
        }

        @Override
        public String[] getIncludePatterns(Project p) {
            return super.getExcludePatterns(p);
        }

        @Override
        public String[] getExcludePatterns(Project p) {
            return super.getIncludePatterns(p);
        }
    }
}


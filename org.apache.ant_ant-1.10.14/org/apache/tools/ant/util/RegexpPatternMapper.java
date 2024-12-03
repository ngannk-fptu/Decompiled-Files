/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.util;

import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.util.FileNameMapper;
import org.apache.tools.ant.util.regexp.RegexpMatcher;
import org.apache.tools.ant.util.regexp.RegexpMatcherFactory;
import org.apache.tools.ant.util.regexp.RegexpUtil;

public class RegexpPatternMapper
implements FileNameMapper {
    private static final int DECIMAL = 10;
    protected RegexpMatcher reg = null;
    protected char[] to = null;
    protected StringBuffer result = new StringBuffer();
    private boolean handleDirSep = false;
    private int regexpOptions = 0;

    public RegexpPatternMapper() throws BuildException {
        this.reg = new RegexpMatcherFactory().newRegexpMatcher();
    }

    public void setHandleDirSep(boolean handleDirSep) {
        this.handleDirSep = handleDirSep;
    }

    public void setCaseSensitive(boolean caseSensitive) {
        this.regexpOptions = RegexpUtil.asOptions(caseSensitive);
    }

    @Override
    public void setFrom(String from) throws BuildException {
        if (from == null) {
            throw new BuildException("this mapper requires a 'from' attribute");
        }
        try {
            this.reg.setPattern(from);
        }
        catch (NoClassDefFoundError e) {
            throw new BuildException("Cannot load regular expression matcher", e);
        }
    }

    @Override
    public void setTo(String to) {
        if (to == null) {
            throw new BuildException("this mapper requires a 'to' attribute");
        }
        this.to = to.toCharArray();
    }

    @Override
    public String[] mapFileName(String sourceFileName) {
        if (sourceFileName == null) {
            return null;
        }
        if (this.handleDirSep && sourceFileName.contains("\\")) {
            sourceFileName = sourceFileName.replace('\\', '/');
        }
        if (this.reg == null || this.to == null || !this.reg.matches(sourceFileName, this.regexpOptions)) {
            return null;
        }
        return new String[]{this.replaceReferences(sourceFileName)};
    }

    protected String replaceReferences(String source) {
        Vector<String> v = this.reg.getGroups(source, this.regexpOptions);
        this.result.setLength(0);
        for (int i = 0; i < this.to.length; ++i) {
            if (this.to[i] == '\\') {
                int nextCharIndex = i + 1;
                if (nextCharIndex < this.to.length) {
                    int value = Character.digit(this.to[nextCharIndex], 10);
                    if (value > -1) {
                        ++i;
                        this.result.append((String)v.get(value));
                        continue;
                    }
                    this.result.append(this.to[i]);
                    continue;
                }
                this.result.append('\\');
                continue;
            }
            this.result.append(this.to[i]);
        }
        return this.result.substring(0);
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.filters.ChainableReader;
import org.apache.tools.ant.filters.ClassConstants;
import org.apache.tools.ant.filters.EscapeUnicode;
import org.apache.tools.ant.filters.ExpandProperties;
import org.apache.tools.ant.filters.HeadFilter;
import org.apache.tools.ant.filters.LineContains;
import org.apache.tools.ant.filters.LineContainsRegExp;
import org.apache.tools.ant.filters.PrefixLines;
import org.apache.tools.ant.filters.ReplaceTokens;
import org.apache.tools.ant.filters.StripJavaComments;
import org.apache.tools.ant.filters.StripLineBreaks;
import org.apache.tools.ant.filters.StripLineComments;
import org.apache.tools.ant.filters.SuffixLines;
import org.apache.tools.ant.filters.TabsToSpaces;
import org.apache.tools.ant.filters.TailFilter;
import org.apache.tools.ant.filters.TokenFilter;
import org.apache.tools.ant.types.AntFilterReader;
import org.apache.tools.ant.types.DataType;
import org.apache.tools.ant.types.Reference;

public class FilterChain
extends DataType {
    private Vector<Object> filterReaders = new Vector();

    public void addFilterReader(AntFilterReader filterReader) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filterReader);
    }

    public Vector<Object> getFilterReaders() {
        if (this.isReference()) {
            return this.getRef().getFilterReaders();
        }
        this.dieOnCircularReference();
        return this.filterReaders;
    }

    public void addClassConstants(ClassConstants classConstants) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(classConstants);
    }

    public void addExpandProperties(ExpandProperties expandProperties) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(expandProperties);
    }

    public void addHeadFilter(HeadFilter headFilter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(headFilter);
    }

    public void addLineContains(LineContains lineContains) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(lineContains);
    }

    public void addLineContainsRegExp(LineContainsRegExp lineContainsRegExp) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(lineContainsRegExp);
    }

    public void addPrefixLines(PrefixLines prefixLines) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(prefixLines);
    }

    public void addSuffixLines(SuffixLines suffixLines) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(suffixLines);
    }

    public void addReplaceTokens(ReplaceTokens replaceTokens) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(replaceTokens);
    }

    public void addStripJavaComments(StripJavaComments stripJavaComments) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(stripJavaComments);
    }

    public void addStripLineBreaks(StripLineBreaks stripLineBreaks) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(stripLineBreaks);
    }

    public void addStripLineComments(StripLineComments stripLineComments) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(stripLineComments);
    }

    public void addTabsToSpaces(TabsToSpaces tabsToSpaces) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(tabsToSpaces);
    }

    public void addTailFilter(TailFilter tailFilter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(tailFilter);
    }

    public void addEscapeUnicode(EscapeUnicode escapeUnicode) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(escapeUnicode);
    }

    public void addTokenFilter(TokenFilter tokenFilter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(tokenFilter);
    }

    public void addDeleteCharacters(TokenFilter.DeleteCharacters filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filter);
    }

    public void addContainsRegex(TokenFilter.ContainsRegex filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filter);
    }

    public void addReplaceRegex(TokenFilter.ReplaceRegex filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filter);
    }

    public void addTrim(TokenFilter.Trim filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filter);
    }

    public void addReplaceString(TokenFilter.ReplaceString filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filter);
    }

    public void addIgnoreBlank(TokenFilter.IgnoreBlank filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filter);
    }

    @Override
    public void setRefid(Reference r) throws BuildException {
        if (!this.filterReaders.isEmpty()) {
            throw this.tooManyAttributes();
        }
        super.setRefid(r);
    }

    public void add(ChainableReader filter) {
        if (this.isReference()) {
            throw this.noChildrenAllowed();
        }
        this.setChecked(false);
        this.filterReaders.addElement(filter);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) throws BuildException {
        if (this.isChecked()) {
            return;
        }
        if (this.isReference()) {
            super.dieOnCircularReference(stk, p);
        } else {
            for (Object o : this.filterReaders) {
                if (!(o instanceof DataType)) continue;
                FilterChain.pushAndInvokeCircularReferenceCheck((DataType)o, stk, p);
            }
            this.setChecked(true);
        }
    }

    private FilterChain getRef() {
        return this.getCheckedRef(FilterChain.class);
    }
}


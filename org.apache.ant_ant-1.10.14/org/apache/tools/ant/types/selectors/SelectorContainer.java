/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.selectors;

import java.util.Enumeration;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.selectors.AndSelector;
import org.apache.tools.ant.types.selectors.ContainsRegexpSelector;
import org.apache.tools.ant.types.selectors.ContainsSelector;
import org.apache.tools.ant.types.selectors.DateSelector;
import org.apache.tools.ant.types.selectors.DependSelector;
import org.apache.tools.ant.types.selectors.DepthSelector;
import org.apache.tools.ant.types.selectors.DifferentSelector;
import org.apache.tools.ant.types.selectors.ExtendSelector;
import org.apache.tools.ant.types.selectors.FileSelector;
import org.apache.tools.ant.types.selectors.FilenameSelector;
import org.apache.tools.ant.types.selectors.MajoritySelector;
import org.apache.tools.ant.types.selectors.NoneSelector;
import org.apache.tools.ant.types.selectors.NotSelector;
import org.apache.tools.ant.types.selectors.OrSelector;
import org.apache.tools.ant.types.selectors.PresentSelector;
import org.apache.tools.ant.types.selectors.SelectSelector;
import org.apache.tools.ant.types.selectors.SizeSelector;
import org.apache.tools.ant.types.selectors.TypeSelector;
import org.apache.tools.ant.types.selectors.modifiedselector.ModifiedSelector;

public interface SelectorContainer {
    public boolean hasSelectors();

    public int selectorCount();

    public FileSelector[] getSelectors(Project var1);

    public Enumeration<FileSelector> selectorElements();

    public void appendSelector(FileSelector var1);

    public void addSelector(SelectSelector var1);

    public void addAnd(AndSelector var1);

    public void addOr(OrSelector var1);

    public void addNot(NotSelector var1);

    public void addNone(NoneSelector var1);

    public void addMajority(MajoritySelector var1);

    public void addDate(DateSelector var1);

    public void addSize(SizeSelector var1);

    public void addFilename(FilenameSelector var1);

    public void addCustom(ExtendSelector var1);

    public void addContains(ContainsSelector var1);

    public void addPresent(PresentSelector var1);

    public void addDepth(DepthSelector var1);

    public void addDepend(DependSelector var1);

    public void addContainsRegexp(ContainsRegexpSelector var1);

    public void addType(TypeSelector var1);

    public void addDifferent(DifferentSelector var1);

    public void addModified(ModifiedSelector var1);

    public void add(FileSelector var1);
}


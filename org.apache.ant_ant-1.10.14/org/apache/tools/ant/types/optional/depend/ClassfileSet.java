/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.optional.depend;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.Vector;
import org.apache.tools.ant.DirectoryScanner;
import org.apache.tools.ant.Project;
import org.apache.tools.ant.types.FileSet;
import org.apache.tools.ant.types.optional.depend.DependScanner;
import org.apache.tools.ant.util.StringUtils;

public class ClassfileSet
extends FileSet {
    private List<String> rootClasses = new ArrayList<String>();
    private List<FileSet> rootFileSets = new ArrayList<FileSet>();

    public ClassfileSet() {
    }

    protected ClassfileSet(ClassfileSet s) {
        super(s);
        this.rootClasses.addAll(s.rootClasses);
    }

    public void addRootFileset(FileSet rootFileSet) {
        this.rootFileSets.add(rootFileSet);
        this.setChecked(false);
    }

    public void setRootClass(String rootClass) {
        this.rootClasses.add(rootClass);
    }

    @Override
    public DirectoryScanner getDirectoryScanner(Project p) {
        if (this.isReference()) {
            return this.getRef(p).getDirectoryScanner(p);
        }
        this.dieOnCircularReference(p);
        DirectoryScanner parentScanner = super.getDirectoryScanner(p);
        DependScanner scanner = new DependScanner(parentScanner);
        Vector<String> allRootClasses = new Vector<String>(this.rootClasses);
        for (FileSet additionalRootSet : this.rootFileSets) {
            DirectoryScanner additionalScanner = additionalRootSet.getDirectoryScanner(p);
            for (String file : additionalScanner.getIncludedFiles()) {
                if (!file.endsWith(".class")) continue;
                String classFilePath = StringUtils.removeSuffix(file, ".class");
                String className = classFilePath.replace('/', '.').replace('\\', '.');
                allRootClasses.addElement(className);
            }
            scanner.addBasedir(additionalRootSet.getDir(p));
        }
        scanner.setBasedir(this.getDir(p));
        scanner.setRootClasses(allRootClasses);
        scanner.scan();
        return scanner;
    }

    public void addConfiguredRoot(ClassRoot root) {
        this.rootClasses.add(root.getClassname());
    }

    @Override
    public Object clone() {
        return new ClassfileSet(this.isReference() ? this.getRef() : this);
    }

    @Override
    protected synchronized void dieOnCircularReference(Stack<Object> stk, Project p) {
        if (this.isChecked()) {
            return;
        }
        super.dieOnCircularReference(stk, p);
        if (!this.isReference()) {
            for (FileSet additionalRootSet : this.rootFileSets) {
                ClassfileSet.pushAndInvokeCircularReferenceCheck(additionalRootSet, stk, p);
            }
            this.setChecked(true);
        }
    }

    private ClassfileSet getRef() {
        return this.getCheckedRef(ClassfileSet.class);
    }

    public static class ClassRoot {
        private String rootClass;

        public void setClassname(String name) {
            this.rootClass = name;
        }

        public String getClassname() {
            return this.rootClass;
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.groovydoc.GroovyRootDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyDoc;

public class SimpleGroovyRootDoc
extends SimpleGroovyDoc
implements GroovyRootDoc {
    private Map<String, GroovyPackageDoc> packageDocs = new LinkedHashMap<String, GroovyPackageDoc>();
    private List<GroovyPackageDoc> packageDocValues = null;
    private Map<String, GroovyClassDoc> classDocs = new LinkedHashMap<String, GroovyClassDoc>();
    private List<GroovyClassDoc> classDocValues = null;
    private String description = "";

    public SimpleGroovyRootDoc(String name) {
        super(name);
    }

    @Override
    public GroovyClassDoc classNamed(GroovyClassDoc groovyClassDoc, String name) {
        for (Map.Entry<String, GroovyClassDoc> entry : this.classDocs.entrySet()) {
            boolean hasPackage;
            String key = entry.getKey();
            if (key.equals(name)) {
                return entry.getValue();
            }
            int lastSlashIdx = key.lastIndexOf(47);
            if (lastSlashIdx <= 0) continue;
            String shortKey = key.substring(lastSlashIdx + 1);
            String fullPathName = groovyClassDoc != null ? groovyClassDoc.getFullPathName() : null;
            boolean bl = hasPackage = fullPathName != null && fullPathName.lastIndexOf(47) > 0;
            if (hasPackage) {
                fullPathName = fullPathName.substring(0, fullPathName.lastIndexOf(47));
            }
            if (!shortKey.equals(name) || hasPackage && !key.startsWith(fullPathName)) continue;
            return entry.getValue();
        }
        return null;
    }

    public GroovyClassDoc classNamedExact(String name) {
        for (Map.Entry<String, GroovyClassDoc> entry : this.classDocs.entrySet()) {
            String key = entry.getKey();
            if (!key.equals(name)) continue;
            return entry.getValue();
        }
        return null;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String description() {
        return this.description;
    }

    public String summary() {
        return SimpleGroovyDoc.calculateFirstSentence(this.description);
    }

    @Override
    public GroovyClassDoc[] classes() {
        if (this.classDocValues == null) {
            this.classDocValues = new ArrayList<GroovyClassDoc>(this.classDocs.values());
            Collections.sort(this.classDocValues);
        }
        return this.classDocValues.toArray(new GroovyClassDoc[this.classDocValues.size()]);
    }

    @Override
    public String[][] options() {
        return null;
    }

    @Override
    public GroovyPackageDoc packageNamed(String packageName) {
        return this.packageDocs.get(packageName);
    }

    public void putAllClasses(Map<String, GroovyClassDoc> classes) {
        this.classDocs.putAll(classes);
        this.classDocValues = null;
    }

    public void put(String packageName, GroovyPackageDoc packageDoc) {
        this.packageDocs.put(packageName, packageDoc);
        this.packageDocValues = null;
    }

    @Override
    public GroovyClassDoc[] specifiedClasses() {
        return null;
    }

    @Override
    public GroovyPackageDoc[] specifiedPackages() {
        if (this.packageDocValues == null) {
            this.packageDocValues = new ArrayList<GroovyPackageDoc>(this.packageDocs.values());
            Collections.sort(this.packageDocValues);
        }
        return this.packageDocValues.toArray(new GroovyPackageDoc[this.packageDocValues.size()]);
    }

    @Override
    public Map<String, GroovyClassDoc> getVisibleClasses(List importedClassesAndPackages) {
        LinkedHashMap<String, GroovyClassDoc> visibleClasses = new LinkedHashMap<String, GroovyClassDoc>();
        for (Map.Entry<String, GroovyClassDoc> entry : this.classDocs.entrySet()) {
            String fullClassName = entry.getKey();
            String equivalentPackageImport = fullClassName.replaceAll("[^/]+$", "*");
            if (!importedClassesAndPackages.contains(fullClassName) && !importedClassesAndPackages.contains(equivalentPackageImport)) continue;
            GroovyClassDoc classDoc = entry.getValue();
            visibleClasses.put(classDoc.name(), classDoc);
        }
        return visibleClasses;
    }

    @Override
    public void printError(String arg0) {
    }

    @Override
    public void printNotice(String arg0) {
    }

    @Override
    public void printWarning(String arg0) {
    }

    public void resolve() {
        for (GroovyClassDoc groovyClassDoc : this.classDocs.values()) {
            SimpleGroovyClassDoc classDoc = (SimpleGroovyClassDoc)groovyClassDoc;
            classDoc.resolve(this);
        }
    }
}


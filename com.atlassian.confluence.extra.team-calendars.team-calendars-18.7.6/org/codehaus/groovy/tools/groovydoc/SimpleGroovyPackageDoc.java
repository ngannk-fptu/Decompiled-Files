/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.TreeMap;
import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyPackageDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyProgramElementDoc;

public class SimpleGroovyPackageDoc
extends SimpleGroovyDoc
implements GroovyPackageDoc {
    private static final char FS = '/';
    final Map<String, GroovyClassDoc> classDocs = new TreeMap<String, GroovyClassDoc>();
    private String description = "";
    private String summary = "";

    public SimpleGroovyPackageDoc(String name) {
        super(name);
    }

    @Override
    public GroovyClassDoc[] allClasses() {
        return this.classDocs.values().toArray(new GroovyClassDoc[this.classDocs.values().size()]);
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public void putAll(Map<String, GroovyClassDoc> classes) {
        for (Map.Entry<String, GroovyClassDoc> docEntry : classes.entrySet()) {
            GroovyClassDoc classDoc = docEntry.getValue();
            this.classDocs.put(docEntry.getKey(), classDoc);
            SimpleGroovyProgramElementDoc programElement = (SimpleGroovyProgramElementDoc)((Object)classDoc);
            programElement.setContainingPackage(this);
        }
    }

    @Override
    public String nameWithDots() {
        return this.name().replace('/', '.');
    }

    @Override
    public GroovyClassDoc[] allClasses(boolean arg0) {
        ArrayList<GroovyClassDoc> classDocValues = new ArrayList<GroovyClassDoc>(this.classDocs.values());
        return classDocValues.toArray(new GroovyClassDoc[classDocValues.size()]);
    }

    @Override
    public GroovyClassDoc[] enums() {
        ArrayList<GroovyClassDoc> result = new ArrayList<GroovyClassDoc>(this.classDocs.values().size());
        for (GroovyClassDoc doc : this.classDocs.values()) {
            if (!doc.isEnum()) continue;
            result.add(doc);
        }
        return result.toArray(new GroovyClassDoc[result.size()]);
    }

    @Override
    public GroovyClassDoc[] errors() {
        ArrayList<GroovyClassDoc> result = new ArrayList<GroovyClassDoc>(this.classDocs.values().size());
        for (GroovyClassDoc doc : this.classDocs.values()) {
            if (!doc.isError()) continue;
            result.add(doc);
        }
        return result.toArray(new GroovyClassDoc[result.size()]);
    }

    @Override
    public GroovyClassDoc[] exceptions() {
        ArrayList<GroovyClassDoc> result = new ArrayList<GroovyClassDoc>(this.classDocs.values().size());
        for (GroovyClassDoc doc : this.classDocs.values()) {
            if (!doc.isException()) continue;
            result.add(doc);
        }
        return result.toArray(new GroovyClassDoc[result.size()]);
    }

    @Override
    public GroovyClassDoc findClass(String arg0) {
        return null;
    }

    @Override
    public GroovyClassDoc[] interfaces() {
        ArrayList<GroovyClassDoc> result = new ArrayList<GroovyClassDoc>(this.classDocs.values().size());
        for (GroovyClassDoc doc : this.classDocs.values()) {
            if (!doc.isInterface()) continue;
            result.add(doc);
        }
        return result.toArray(new GroovyClassDoc[result.size()]);
    }

    @Override
    public GroovyClassDoc[] ordinaryClasses() {
        ArrayList<GroovyClassDoc> result = new ArrayList<GroovyClassDoc>(this.classDocs.values().size());
        for (GroovyClassDoc doc : this.classDocs.values()) {
            if (!doc.isOrdinaryClass()) continue;
            result.add(doc);
        }
        return result.toArray(new GroovyClassDoc[result.size()]);
    }

    @Override
    public String description() {
        return this.description;
    }

    @Override
    public String summary() {
        return this.summary;
    }

    @Override
    public String getRelativeRootPath() {
        StringTokenizer tokenizer = new StringTokenizer(this.name(), "/");
        StringBuilder sb = new StringBuilder();
        while (tokenizer.hasMoreTokens()) {
            tokenizer.nextToken();
            sb.append("../");
        }
        return sb.toString();
    }
}


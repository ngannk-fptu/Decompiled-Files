/*
 * Decompiled with CFR 0.152.
 */
package org.jboss.jandex;

import java.io.File;
import java.util.List;
import org.jboss.jandex.AnnotationInstance;
import org.jboss.jandex.ClassInfo;
import org.jboss.jandex.Index;

public final class Result {
    private final Index index;
    private final int annotations;
    private final int instances;
    private final int classes;
    private final int usages;
    private final int bytes;
    private final String name;
    private final File outputFile;

    Result(Index index, String name, int bytes, File outputFile) {
        this.index = index;
        this.annotations = index.annotations.size();
        this.instances = this.countInstances(index);
        this.classes = index.classes.size();
        int usages = 0;
        for (List<ClassInfo> usagesForOneClass : index.users.values()) {
            usages += usagesForOneClass.size();
        }
        this.usages = usages;
        this.bytes = bytes;
        this.name = name;
        this.outputFile = outputFile;
    }

    private int countInstances(Index index) {
        int c = 0;
        for (List<AnnotationInstance> list : index.annotations.values()) {
            c += list.size();
        }
        return c;
    }

    public Index getIndex() {
        return this.index;
    }

    public int getAnnotations() {
        return this.annotations;
    }

    public int getBytes() {
        return this.bytes;
    }

    public int getClasses() {
        return this.classes;
    }

    public int getInstances() {
        return this.instances;
    }

    public String getName() {
        return this.name;
    }

    public File getOutputFile() {
        return this.outputFile;
    }

    public int getUsages() {
        return this.usages;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types.optional;

import java.util.ArrayList;
import org.apache.tools.ant.types.optional.AbstractScriptComponent;
import org.apache.tools.ant.util.FileNameMapper;

public class ScriptMapper
extends AbstractScriptComponent
implements FileNameMapper {
    private ArrayList<String> files;

    @Override
    public void setFrom(String from) {
    }

    @Override
    public void setTo(String to) {
    }

    public void clear() {
        this.files = new ArrayList(1);
    }

    public void addMappedName(String mapping) {
        this.files.add(mapping);
    }

    @Override
    public String[] mapFileName(String sourceFileName) {
        this.initScriptRunner();
        this.getRunner().addBean("source", sourceFileName);
        this.clear();
        this.executeScript("ant_mapper");
        if (this.files.isEmpty()) {
            return null;
        }
        return this.files.toArray(new String[0]);
    }
}


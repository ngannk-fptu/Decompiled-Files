/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.taskdefs;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.Task;
import org.apache.tools.ant.property.LocalProperties;

public class Local
extends Task {
    private String name;
    private final Set<Name> nameElements = new LinkedHashSet<Name>();

    public void setName(String name) {
        this.name = name;
    }

    public Name createName() {
        Name result = new Name();
        this.nameElements.add(result);
        return result;
    }

    @Override
    public void execute() {
        if (this.name == null && this.nameElements.isEmpty()) {
            throw new BuildException("Found no configured local property names");
        }
        LocalProperties localProperties = LocalProperties.get(this.getProject());
        if (this.name != null) {
            localProperties.addLocal(this.name);
        }
        this.nameElements.forEach(n -> n.accept(localProperties));
    }

    public static class Name
    implements Consumer<LocalProperties> {
        private String text;

        public void addText(String text) {
            this.text = text;
        }

        @Override
        public void accept(LocalProperties localProperties) {
            if (this.text == null) {
                throw new BuildException("nested name element is missing text");
            }
            localProperties.addLocal(this.text);
        }
    }
}


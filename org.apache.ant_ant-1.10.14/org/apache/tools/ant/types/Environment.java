/*
 * Decompiled with CFR 0.152.
 */
package org.apache.tools.ant.types;

import java.io.File;
import java.util.Vector;
import org.apache.tools.ant.BuildException;
import org.apache.tools.ant.types.Path;

public class Environment {
    protected Vector<Variable> variables = new Vector();

    public void addVariable(Variable var) {
        this.variables.addElement(var);
    }

    public String[] getVariables() throws BuildException {
        if (this.variables.isEmpty()) {
            return null;
        }
        return (String[])this.variables.stream().map(Variable::getContent).toArray(String[]::new);
    }

    public Vector<Variable> getVariablesVector() {
        return this.variables;
    }

    public static class Variable {
        private String key;
        private String value;

        public void setKey(String key) {
            this.key = key;
        }

        public void setValue(String value) {
            this.value = value;
        }

        public String getKey() {
            return this.key;
        }

        public String getValue() {
            return this.value;
        }

        public void setPath(Path path) {
            this.value = path.toString();
        }

        public void setFile(File file) {
            this.value = file.getAbsolutePath();
        }

        public String getContent() throws BuildException {
            this.validate();
            return this.key.trim() + "=" + this.value.trim();
        }

        public void validate() {
            if (this.key == null || this.value == null) {
                throw new BuildException("key and value must be specified for environment variables.");
            }
        }
    }
}


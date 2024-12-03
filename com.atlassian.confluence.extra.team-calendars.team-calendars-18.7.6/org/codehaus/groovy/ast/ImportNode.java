/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.ast;

import groovyjarjarasm.asm.Opcodes;
import org.codehaus.groovy.ast.AnnotatedNode;
import org.codehaus.groovy.ast.ClassNode;
import org.codehaus.groovy.ast.GroovyCodeVisitor;

public class ImportNode
extends AnnotatedNode
implements Opcodes {
    private final ClassNode type;
    private final String alias;
    private final String fieldName;
    private final String packageName;
    private final boolean isStar;
    private final boolean isStatic;

    public ImportNode(ClassNode type, String alias) {
        this.type = type;
        this.alias = alias;
        this.isStar = false;
        this.isStatic = false;
        this.packageName = null;
        this.fieldName = null;
    }

    public ImportNode(String packageName) {
        this.type = null;
        this.alias = null;
        this.isStar = true;
        this.isStatic = false;
        this.packageName = packageName;
        this.fieldName = null;
    }

    public ImportNode(ClassNode type) {
        this.type = type;
        this.alias = null;
        this.isStar = true;
        this.isStatic = true;
        this.packageName = null;
        this.fieldName = null;
    }

    public ImportNode(ClassNode type, String fieldName, String alias) {
        this.type = type;
        this.alias = alias;
        this.isStar = false;
        this.isStatic = true;
        this.packageName = null;
        this.fieldName = fieldName;
    }

    @Override
    public String getText() {
        String typeName = this.getClassName();
        if (this.isStar && !this.isStatic) {
            return "import " + this.packageName + "*";
        }
        if (this.isStar) {
            return "import static " + typeName + ".*";
        }
        if (this.isStatic) {
            if (this.alias != null && this.alias.length() != 0 && !this.alias.equals(this.fieldName)) {
                return "import static " + typeName + "." + this.fieldName + " as " + this.alias;
            }
            return "import static " + typeName + "." + this.fieldName;
        }
        if (this.alias == null || this.alias.length() == 0) {
            return "import " + typeName;
        }
        return "import " + typeName + " as " + this.alias;
    }

    public String getPackageName() {
        return this.packageName;
    }

    public String getFieldName() {
        return this.fieldName;
    }

    public boolean isStar() {
        return this.isStar;
    }

    public boolean isStatic() {
        return this.isStatic;
    }

    public String getAlias() {
        return this.alias;
    }

    public ClassNode getType() {
        return this.type;
    }

    public String getClassName() {
        return this.type == null ? null : this.type.getName();
    }

    @Override
    public void visit(GroovyCodeVisitor visitor) {
    }
}


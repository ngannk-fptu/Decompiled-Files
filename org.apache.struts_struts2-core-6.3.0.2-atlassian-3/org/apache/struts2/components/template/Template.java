/*
 * Decompiled with CFR 0.152.
 */
package org.apache.struts2.components.template;

import java.util.ArrayList;
import java.util.List;
import org.apache.struts2.components.template.TemplateEngine;

public class Template
implements Cloneable {
    String dir;
    String theme;
    String name;

    public Template(String dir, String theme, String name) {
        this.dir = dir;
        this.theme = theme;
        this.name = name;
    }

    public String getDir() {
        return this.dir;
    }

    public String getTheme() {
        return this.theme;
    }

    public String getName() {
        return this.name;
    }

    public List<Template> getPossibleTemplates(TemplateEngine engine) {
        String parentTheme;
        ArrayList<Template> list = new ArrayList<Template>(3);
        Template template = this;
        list.add(template);
        while ((parentTheme = (String)engine.getThemeProps(template).get("parent")) != null) {
            try {
                template = (Template)template.clone();
                template.theme = parentTheme;
                list.add(template);
            }
            catch (CloneNotSupportedException cloneNotSupportedException) {}
        }
        return list;
    }

    public String toString() {
        return "/" + this.dir + "/" + this.theme + "/" + this.name;
    }

    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || this.getClass() != o.getClass()) {
            return false;
        }
        Template template = (Template)o;
        if (this.dir != null ? !this.dir.equals(template.dir) : template.dir != null) {
            return false;
        }
        if (this.name != null ? !this.name.equals(template.name) : template.name != null) {
            return false;
        }
        return !(this.theme != null ? !this.theme.equals(template.theme) : template.theme != null);
    }

    public int hashCode() {
        int result = this.dir != null ? this.dir.hashCode() : 0;
        result = 31 * result + (this.theme != null ? this.theme.hashCode() : 0);
        result = 31 * result + (this.name != null ? this.name.hashCode() : 0);
        return result;
    }
}


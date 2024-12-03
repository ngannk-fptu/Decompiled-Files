/*
 * Decompiled with CFR 0.152.
 */
package org.apache.lucene.search;

import java.util.ArrayList;

public class Explanation {
    private float value;
    private String description;
    private ArrayList<Explanation> details;

    public Explanation() {
    }

    public Explanation(float value, String description) {
        this.value = value;
        this.description = description;
    }

    public boolean isMatch() {
        return 0.0f < this.getValue();
    }

    public float getValue() {
        return this.value;
    }

    public void setValue(float value) {
        this.value = value;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    protected String getSummary() {
        return this.getValue() + " = " + this.getDescription();
    }

    public Explanation[] getDetails() {
        if (this.details == null) {
            return null;
        }
        return this.details.toArray(new Explanation[0]);
    }

    public void addDetail(Explanation detail) {
        if (this.details == null) {
            this.details = new ArrayList();
        }
        this.details.add(detail);
    }

    public String toString() {
        return this.toString(0);
    }

    protected String toString(int depth) {
        StringBuilder buffer = new StringBuilder();
        for (int i = 0; i < depth; ++i) {
            buffer.append("  ");
        }
        buffer.append(this.getSummary());
        buffer.append("\n");
        Explanation[] details = this.getDetails();
        if (details != null) {
            for (int i = 0; i < details.length; ++i) {
                buffer.append(details[i].toString(depth + 1));
            }
        }
        return buffer.toString();
    }

    public String toHtml() {
        StringBuilder buffer = new StringBuilder();
        buffer.append("<ul>\n");
        buffer.append("<li>");
        buffer.append(this.getSummary());
        buffer.append("<br />\n");
        Explanation[] details = this.getDetails();
        if (details != null) {
            for (int i = 0; i < details.length; ++i) {
                buffer.append(details[i].toHtml());
            }
        }
        buffer.append("</li>\n");
        buffer.append("</ul>\n");
        return buffer.toString();
    }
}


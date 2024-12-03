/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2.input.stax;

import org.jdom2.Namespace;
import org.jdom2.input.stax.StAXFilter;

public class DefaultStAXFilter
implements StAXFilter {
    public boolean includeDocType() {
        return true;
    }

    public boolean includeElement(int depth, String name, Namespace ns) {
        return true;
    }

    public String includeComment(int depth, String comment) {
        return comment;
    }

    public boolean includeEntityRef(int depth, String name) {
        return true;
    }

    public String includeCDATA(int depth, String text) {
        return text;
    }

    public String includeText(int depth, String text) {
        return text;
    }

    public boolean includeProcessingInstruction(int depth, String target) {
        return true;
    }

    public boolean pruneElement(int depth, String name, Namespace ns) {
        return false;
    }

    public String pruneComment(int depth, String comment) {
        return comment;
    }

    public boolean pruneEntityRef(int depth, String name) {
        return false;
    }

    public String pruneCDATA(int depth, String text) {
        return text;
    }

    public String pruneText(int depth, String text) {
        return text;
    }

    public boolean pruneProcessingInstruction(int depth, String target) {
        return false;
    }
}


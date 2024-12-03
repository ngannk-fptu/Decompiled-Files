/*
 * Decompiled with CFR 0.152.
 */
package org.jdom2;

import org.jdom2.Attribute;
import org.jdom2.CDATA;
import org.jdom2.Comment;
import org.jdom2.DocType;
import org.jdom2.Element;
import org.jdom2.EntityRef;
import org.jdom2.Namespace;
import org.jdom2.ProcessingInstruction;
import org.jdom2.Text;

public class IllegalAddException
extends IllegalArgumentException {
    private static final long serialVersionUID = 200L;

    IllegalAddException(Element base, Attribute added, String reason) {
        super("The attribute \"" + added.getQualifiedName() + "\" could not be added to the element \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(Element base, Element added, String reason) {
        super("The element \"" + added.getQualifiedName() + "\" could not be added as a child of \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(Element added, String reason) {
        super("The element \"" + added.getQualifiedName() + "\" could not be added as the root of the document: " + reason);
    }

    IllegalAddException(Element base, ProcessingInstruction added, String reason) {
        super("The PI \"" + added.getTarget() + "\" could not be added as content to \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(ProcessingInstruction added, String reason) {
        super("The PI \"" + added.getTarget() + "\" could not be added to the top level of the document: " + reason);
    }

    IllegalAddException(Element base, Comment added, String reason) {
        super("The comment \"" + added.getText() + "\" could not be added as content to \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(Element base, CDATA added, String reason) {
        super("The CDATA \"" + added.getText() + "\" could not be added as content to \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(Element base, Text added, String reason) {
        super("The Text \"" + added.getText() + "\" could not be added as content to \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(Comment added, String reason) {
        super("The comment \"" + added.getText() + "\" could not be added to the top level of the document: " + reason);
    }

    IllegalAddException(Element base, EntityRef added, String reason) {
        super("The entity reference\"" + added.getName() + "\" could not be added as content to \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(Element base, Namespace added, String reason) {
        super("The namespace xmlns" + (added.getPrefix().equals("") ? "=" : ":" + added.getPrefix() + "=") + "\"" + added.getURI() + "\" could not be added as a namespace to \"" + base.getQualifiedName() + "\": " + reason);
    }

    IllegalAddException(DocType added, String reason) {
        super("The DOCTYPE " + added.toString() + " could not be added to the document: " + reason);
    }

    public IllegalAddException(String reason) {
        super(reason);
    }
}


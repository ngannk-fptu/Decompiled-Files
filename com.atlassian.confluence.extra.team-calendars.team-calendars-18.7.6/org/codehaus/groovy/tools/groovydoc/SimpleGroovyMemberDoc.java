/*
 * Decompiled with CFR 0.152.
 */
package org.codehaus.groovy.tools.groovydoc;

import org.codehaus.groovy.groovydoc.GroovyClassDoc;
import org.codehaus.groovy.groovydoc.GroovyMemberDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyAbstractableElementDoc;
import org.codehaus.groovy.tools.groovydoc.SimpleGroovyClassDoc;

public class SimpleGroovyMemberDoc
extends SimpleGroovyAbstractableElementDoc
implements GroovyMemberDoc {
    protected GroovyClassDoc belongsToClass;

    public SimpleGroovyMemberDoc(String name, GroovyClassDoc belongsToClass) {
        super(name);
        this.belongsToClass = belongsToClass;
    }

    @Override
    public boolean isSynthetic() {
        return false;
    }

    @Override
    public String firstSentenceCommentText() {
        if (super.firstSentenceCommentText() == null) {
            SimpleGroovyClassDoc classDoc = (SimpleGroovyClassDoc)this.belongsToClass;
            this.setFirstSentenceCommentText(classDoc.replaceTags(SimpleGroovyMemberDoc.calculateFirstSentence(this.getRawCommentText())));
        }
        return super.firstSentenceCommentText();
    }

    @Override
    public String commentText() {
        if (super.commentText() == null) {
            SimpleGroovyClassDoc classDoc = (SimpleGroovyClassDoc)this.belongsToClass;
            this.setCommentText(classDoc.replaceTags(this.getRawCommentText()));
        }
        return super.commentText();
    }
}


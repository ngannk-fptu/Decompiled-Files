/*
 * Decompiled with CFR 0.152.
 */
package freemarker.ext.dom;

import freemarker.ext.dom.NodeModel;
import freemarker.template.TemplateScalarModel;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Comment;

class CharacterDataNodeModel
extends NodeModel
implements TemplateScalarModel {
    public CharacterDataNodeModel(CharacterData text) {
        super(text);
    }

    @Override
    public String getAsString() {
        return ((CharacterData)this.node).getData();
    }

    @Override
    public String getNodeName() {
        return this.node instanceof Comment ? "@comment" : "@text";
    }

    @Override
    public boolean isEmpty() {
        return true;
    }
}


/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ElseOfList;
import freemarker.core.Environment;
import freemarker.core.IteratorBlock;
import freemarker.core.ParameterRole;
import freemarker.core.TemplateElement;
import freemarker.template.TemplateException;
import java.io.IOException;

class ListElseContainer
extends TemplateElement {
    private final IteratorBlock listPart;
    private final ElseOfList elsePart;

    public ListElseContainer(IteratorBlock listPart, ElseOfList elsePart) {
        this.setChildBufferCapacity(2);
        this.addChild(listPart);
        this.addChild(elsePart);
        this.listPart = listPart;
        this.elsePart = elsePart;
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        if (!this.listPart.acceptWithResult(env)) {
            return this.elsePart.accept(env);
        }
        return null;
    }

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            StringBuilder buf = new StringBuilder();
            int ln = this.getChildCount();
            for (int i = 0; i < ln; ++i) {
                TemplateElement element = this.getChild(i);
                buf.append(element.dump(canonical));
            }
            buf.append("</#list>");
            return buf.toString();
        }
        return this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#list-#else-container";
    }

    @Override
    int getParameterCount() {
        return 0;
    }

    @Override
    Object getParameterValue(int idx) {
        throw new IndexOutOfBoundsException();
    }

    @Override
    ParameterRole getParameterRole(int idx) {
        throw new IndexOutOfBoundsException();
    }
}


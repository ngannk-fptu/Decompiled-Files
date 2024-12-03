/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.ConditionalBlock;
import freemarker.core.Environment;
import freemarker.core.Expression;
import freemarker.core.ParameterRole;
import freemarker.core.ParseException;
import freemarker.core.TemplateElement;
import freemarker.core.TemplateObject;
import freemarker.template.TemplateException;
import java.io.IOException;

final class IfBlock
extends TemplateElement {
    IfBlock(ConditionalBlock block) {
        this.setChildBufferCapacity(1);
        this.addBlock(block);
    }

    void addBlock(ConditionalBlock block) {
        this.addChild(block);
    }

    @Override
    TemplateElement[] accept(Environment env) throws TemplateException, IOException {
        int ln = this.getChildCount();
        for (int i = 0; i < ln; ++i) {
            ConditionalBlock cblock = (ConditionalBlock)this.getChild(i);
            Expression condition = cblock.condition;
            env.replaceElementStackTop(cblock);
            if (condition != null && !condition.evalToBoolean(env)) continue;
            return cblock.getChildBuffer();
        }
        return null;
    }

    @Override
    TemplateElement postParseCleanup(boolean stripWhitespace) throws ParseException {
        if (this.getChildCount() == 1) {
            ConditionalBlock cblock = (ConditionalBlock)this.getChild(0);
            cblock.setLocation(this.getTemplate(), (TemplateObject)cblock, (TemplateObject)this);
            return cblock.postParseCleanup(stripWhitespace);
        }
        return super.postParseCleanup(stripWhitespace);
    }

    @Override
    protected String dump(boolean canonical) {
        if (canonical) {
            StringBuilder buf = new StringBuilder();
            int ln = this.getChildCount();
            for (int i = 0; i < ln; ++i) {
                ConditionalBlock cblock = (ConditionalBlock)this.getChild(i);
                buf.append(cblock.dump(canonical));
            }
            buf.append("</#if>");
            return buf.toString();
        }
        return this.getNodeTypeSymbol();
    }

    @Override
    String getNodeTypeSymbol() {
        return "#if-#elseif-#else-container";
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

    @Override
    boolean isNestedBlockRepeater() {
        return false;
    }
}


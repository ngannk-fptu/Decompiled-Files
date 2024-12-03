/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.digester.Digester
 *  org.apache.commons.digester.Rule
 */
package org.apache.velocity.tools.view.servlet;

import org.apache.commons.digester.Digester;
import org.apache.commons.digester.Rule;
import org.apache.velocity.tools.view.ToolboxRuleSet;
import org.apache.velocity.tools.view.servlet.ServletToolInfo;
import org.apache.velocity.tools.view.servlet.ServletToolboxManager;

@Deprecated
public class ServletToolboxRuleSet
extends ToolboxRuleSet {
    @Override
    public void addRuleInstances(Digester digester) {
        digester.addRule("toolbox/create-session", (Rule)new CreateSessionRule());
        digester.addRule("toolbox/xhtml", (Rule)new XhtmlRule());
        super.addRuleInstances(digester);
    }

    @Override
    protected void addToolRules(Digester digester) {
        super.addToolRules(digester);
        digester.addBeanPropertySetter("toolbox/tool/scope", "scope");
        digester.addBeanPropertySetter("toolbox/tool/request-path", "requestPath");
    }

    @Override
    protected Class getToolInfoClass() {
        return ServletToolInfo.class;
    }

    protected final class XhtmlRule
    extends BooleanConfigRule {
        protected XhtmlRule() {
        }

        @Override
        public void setBoolean(Object obj, Boolean b) throws Exception {
            ((ServletToolboxManager)obj).setXhtml(b);
        }
    }

    protected final class CreateSessionRule
    extends BooleanConfigRule {
        protected CreateSessionRule() {
        }

        @Override
        public void setBoolean(Object obj, Boolean b) throws Exception {
            ((ServletToolboxManager)obj).setCreateSession(b);
        }
    }

    protected abstract class BooleanConfigRule
    extends Rule {
        protected BooleanConfigRule() {
        }

        public void body(String ns, String name, String text) throws Exception {
            Object parent = this.digester.peek();
            if ("yes".equalsIgnoreCase(text)) {
                this.setBoolean(parent, Boolean.TRUE);
            } else {
                this.setBoolean(parent, Boolean.valueOf(text));
            }
        }

        public abstract void setBoolean(Object var1, Boolean var2) throws Exception;
    }
}


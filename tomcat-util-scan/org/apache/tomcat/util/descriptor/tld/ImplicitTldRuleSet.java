/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.res.StringManager
 */
package org.apache.tomcat.util.descriptor.tld;

import org.apache.tomcat.util.descriptor.tld.TaglibXml;
import org.apache.tomcat.util.digester.Digester;
import org.apache.tomcat.util.digester.Rule;
import org.apache.tomcat.util.digester.RuleSet;
import org.apache.tomcat.util.res.StringManager;
import org.xml.sax.Attributes;

public class ImplicitTldRuleSet
implements RuleSet {
    private static final StringManager sm = StringManager.getManager(ImplicitTldRuleSet.class);
    private static final String PREFIX = "taglib";
    private static final String VALIDATOR_PREFIX = "taglib/validator";
    private static final String TAG_PREFIX = "taglib/tag";
    private static final String TAGFILE_PREFIX = "taglib/tag-file";
    private static final String FUNCTION_PREFIX = "taglib/function";

    @Override
    public void addRuleInstances(Digester digester) {
        digester.addCallMethod("taglib/tlibversion", "setTlibVersion", 0);
        digester.addCallMethod("taglib/tlib-version", "setTlibVersion", 0);
        digester.addCallMethod("taglib/jspversion", "setJspVersion", 0);
        digester.addCallMethod("taglib/jsp-version", "setJspVersion", 0);
        digester.addRule(PREFIX, new Rule(){

            @Override
            public void begin(String namespace, String name, Attributes attributes) {
                TaglibXml taglibXml = (TaglibXml)this.digester.peek();
                taglibXml.setJspVersion(attributes.getValue("version"));
                StringBuilder code = this.digester.getGeneratedCode();
                if (code != null) {
                    code.append(this.digester.toVariableName(taglibXml)).append(".setJspVersion(\"");
                    code.append(attributes.getValue("version")).append("\");");
                    code.append(System.lineSeparator());
                }
            }
        });
        digester.addCallMethod("taglib/shortname", "setShortName", 0);
        digester.addCallMethod("taglib/short-name", "setShortName", 0);
        digester.addRule("taglib/uri", new ElementNotAllowedRule());
        digester.addRule("taglib/info", new ElementNotAllowedRule());
        digester.addRule("taglib/description", new ElementNotAllowedRule());
        digester.addRule("taglib/listener/listener-class", new ElementNotAllowedRule());
        digester.addRule(VALIDATOR_PREFIX, new ElementNotAllowedRule());
        digester.addRule(TAG_PREFIX, new ElementNotAllowedRule());
        digester.addRule(TAGFILE_PREFIX, new ElementNotAllowedRule());
        digester.addRule(FUNCTION_PREFIX, new ElementNotAllowedRule());
    }

    private static class ElementNotAllowedRule
    extends Rule {
        private ElementNotAllowedRule() {
        }

        @Override
        public void begin(String namespace, String name, Attributes attributes) throws Exception {
            throw new IllegalArgumentException(sm.getString("implicitTldRule.elementNotAllowed", new Object[]{name}));
        }
    }
}


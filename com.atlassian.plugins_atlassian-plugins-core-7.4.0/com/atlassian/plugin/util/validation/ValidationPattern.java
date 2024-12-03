/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Preconditions
 *  org.dom4j.Node
 */
package com.atlassian.plugin.util.validation;

import com.atlassian.plugin.util.validation.ValidationException;
import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.List;
import org.dom4j.Node;

public class ValidationPattern {
    private final List<Rule> rules = new ArrayList<Rule>();

    private ValidationPattern() {
    }

    public static ValidationPattern createPattern() {
        return new ValidationPattern();
    }

    public ValidationPattern rule(String context, RuleTest ... tests) {
        this.rules.add(new Rule(context, tests));
        return this;
    }

    public ValidationPattern rule(RuleTest ... tests) {
        this.rules.add(new Rule(".", tests));
        return this;
    }

    public void evaluate(Node node) {
        ArrayList<String> errors = new ArrayList<String>();
        for (Rule rule : this.rules) {
            rule.evaluate(node, errors);
        }
        if (!errors.isEmpty()) {
            if (errors.size() == 1) {
                throw new ValidationException((String)errors.get(0), errors);
            }
            StringBuilder sb = new StringBuilder();
            sb.append("There were validation errors:\n");
            for (String msg : errors) {
                sb.append("\t- ").append(msg).append("\n");
            }
            throw new ValidationException(sb.toString(), errors);
        }
    }

    public static RuleTest test(String xpath) {
        return new RuleTest(xpath);
    }

    public static class Rule {
        private final String contextPattern;
        private final RuleTest[] tests;

        private Rule(String contextPattern, RuleTest[] tests) {
            this.contextPattern = (String)Preconditions.checkNotNull((Object)contextPattern);
            this.tests = (RuleTest[])Preconditions.checkNotNull((Object)tests);
        }

        private void evaluate(Node e, List<String> errors) {
            List contexts = e.selectNodes(this.contextPattern);
            if (contexts != null && contexts.size() > 0) {
                for (Node ctxNode : contexts) {
                    for (RuleTest test : this.tests) {
                        test.evaluate(ctxNode, errors);
                    }
                }
            }
        }
    }

    public static class RuleTest {
        private final String xpath;
        private String errorMessage;

        private RuleTest(String xpath) {
            this.xpath = (String)Preconditions.checkNotNull((Object)xpath);
        }

        public RuleTest withError(String msg) {
            this.errorMessage = msg;
            return this;
        }

        private void evaluate(Node ctxNode, List<String> errors) {
            Object obj = ctxNode.selectObject(this.xpath);
            if (obj == null) {
                errors.add(this.errorMessage + ": " + ctxNode.asXML());
            } else if (obj instanceof Boolean && !((Boolean)obj).booleanValue()) {
                errors.add(this.errorMessage + ": " + ctxNode.asXML());
            } else if (obj instanceof List && ((List)obj).isEmpty()) {
                errors.add(this.errorMessage + ": " + ctxNode.asXML());
            }
        }
    }
}


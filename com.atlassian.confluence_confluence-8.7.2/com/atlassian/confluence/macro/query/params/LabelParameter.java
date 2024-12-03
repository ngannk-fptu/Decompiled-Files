/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.macro.query.params;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.labels.LabelParser;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.query.SearchQueryInterpreter;
import com.atlassian.confluence.macro.query.SearchQueryInterpreterException;
import com.atlassian.confluence.macro.query.params.BooleanQueryFactoryParameter;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.spring.container.ContainerManager;
import java.util.List;

public class LabelParameter
extends BooleanQueryFactoryParameter {
    private static final String[] DEFAULT_PARAM_NAMES = new String[]{"label", "labels"};
    private LabelManager labelManager;

    public LabelParameter() {
        this(null);
    }

    public LabelParameter(String defaultValue) {
        super(DEFAULT_PARAM_NAMES, defaultValue);
        ContainerManager.autowireComponent((Object)this);
    }

    public LabelParameter(List<String> names, String defaultValue) {
        super(names, defaultValue);
        ContainerManager.autowireComponent((Object)this);
    }

    @Override
    protected SearchQueryInterpreter createSearchQueryInterpreter(MacroExecutionContext ctx) {
        Interpreter interpreter = new Interpreter();
        interpreter.setShouldValidate(this.shouldValidate);
        interpreter.setLabelManager(this.labelManager);
        return interpreter;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    static class Interpreter
    implements SearchQueryInterpreter {
        private boolean shouldValidate;
        private LabelManager labelManager;

        Interpreter() {
        }

        public void setShouldValidate(boolean shouldValidate) {
            this.shouldValidate = shouldValidate;
        }

        public void setLabelManager(LabelManager labelManager) {
            this.labelManager = labelManager;
        }

        @Override
        public SearchQuery createSearchQuery(String value) throws SearchQueryInterpreterException {
            if (this.shouldValidate && this.labelManager.getLabel(value) == null) {
                throw new SearchQueryInterpreterException("'" + value + "' is not an existing label");
            }
            if (LabelParser.parse(value) == null) {
                throw new SearchQueryInterpreterException("'" + value + "' is an invalid label.");
            }
            return new LabelQuery(value);
        }
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.spring.container.ContainerManager
 */
package com.atlassian.confluence.macro.query.params;

import com.atlassian.confluence.labels.LabelManager;
import com.atlassian.confluence.macro.MacroExecutionContext;
import com.atlassian.confluence.macro.query.SearchQueryInterpreter;
import com.atlassian.confluence.macro.query.SearchQueryInterpreterException;
import com.atlassian.confluence.macro.query.params.BooleanQueryFactoryParameter;
import com.atlassian.confluence.search.service.SpaceCategoryEnum;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.SpaceCategoryQuery;
import com.atlassian.confluence.spaces.SpaceManager;
import com.atlassian.spring.container.ContainerManager;
import java.util.List;

public class SpaceKeyParameter
extends BooleanQueryFactoryParameter {
    private static final String[] DEFAULT_PARAM_NAMES = new String[]{"space", "spaces"};
    private SpaceManager spaceManager;
    private LabelManager labelManager;

    public SpaceKeyParameter() {
        this(null);
    }

    public SpaceKeyParameter(String defaultValue) {
        super(DEFAULT_PARAM_NAMES, defaultValue);
        ContainerManager.autowireComponent((Object)this);
    }

    public SpaceKeyParameter(List<String> names, String defaultValue) {
        super(names, defaultValue);
        ContainerManager.autowireComponent((Object)this);
    }

    @Override
    protected SearchQueryInterpreter createSearchQueryInterpreter(MacroExecutionContext ctx) {
        Interpreter interpreter = new Interpreter(ctx, this.labelManager);
        interpreter.setShouldValidate(this.shouldValidate);
        interpreter.setSpaceManager(this.spaceManager);
        return interpreter;
    }

    public void setSpaceManager(SpaceManager spaceManager) {
        this.spaceManager = spaceManager;
    }

    public void setLabelManager(LabelManager labelManager) {
        this.labelManager = labelManager;
    }

    static class Interpreter
    implements SearchQueryInterpreter {
        private static final String SELF_SPACE = "@self";
        private static final String PERSONAL_SPACE = "@personal";
        private static final String GLOBAL_SPACE = "@global";
        private static final String FAVORITE_SPACE = "@favorite";
        private static final String FAVOURITE_SPACE = "@favourite";
        private static final String ALL_SPACE = "@all";
        private final MacroExecutionContext ctx;
        private final LabelManager labelManager;
        private boolean shouldValidate;
        private SpaceManager spaceManager;

        public Interpreter(MacroExecutionContext ctx, LabelManager labelManager) {
            this.ctx = ctx;
            this.labelManager = labelManager;
        }

        public void setShouldValidate(boolean shouldValidate) {
            this.shouldValidate = shouldValidate;
        }

        public void setSpaceManager(SpaceManager spaceManager) {
            this.spaceManager = spaceManager;
        }

        @Override
        public SearchQuery createSearchQuery(String value) throws SearchQueryInterpreterException {
            if (value.startsWith("@")) {
                SpaceCategoryEnum category;
                if (value.equals(SELF_SPACE)) {
                    String spaceKey = this.ctx.getPageContext().getSpaceKey();
                    return new InSpaceQuery(spaceKey);
                }
                if (value.equals(PERSONAL_SPACE)) {
                    category = SpaceCategoryEnum.PERSONAL;
                } else if (value.equals(GLOBAL_SPACE)) {
                    category = SpaceCategoryEnum.GLOBAL;
                } else if (value.equals(FAVORITE_SPACE) || value.equals(FAVOURITE_SPACE)) {
                    category = SpaceCategoryEnum.FAVOURITES;
                } else if (value.equals(ALL_SPACE)) {
                    category = SpaceCategoryEnum.ALL;
                } else {
                    throw new SearchQueryInterpreterException("'" + value + "' is not a recognized space category");
                }
                return new SpaceCategoryQuery(category, this.labelManager);
            }
            if (value.equals("*")) {
                return new SpaceCategoryQuery(SpaceCategoryEnum.ALL, this.labelManager);
            }
            if (this.shouldValidate && this.spaceManager.getSpace(value) == null) {
                throw new SearchQueryInterpreterException("'" + value + "' is not an existing space's key");
            }
            return new InSpaceQuery(value);
        }
    }
}


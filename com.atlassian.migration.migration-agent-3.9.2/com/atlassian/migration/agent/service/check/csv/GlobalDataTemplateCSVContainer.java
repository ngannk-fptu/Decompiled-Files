/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.migration.agent.service.check.csv;

import com.atlassian.migration.agent.service.check.csv.CheckResultCSVContainer;
import com.atlassian.migration.agent.service.check.csv.GlobalDataTemplateCSVBean;
import java.util.Collection;
import java.util.Collections;

public class GlobalDataTemplateCSVContainer
implements CheckResultCSVContainer<GlobalDataTemplateCSVBean> {
    private final Collection<GlobalDataTemplateCSVBean> beans;

    public GlobalDataTemplateCSVContainer() {
        this.beans = Collections.emptyList();
    }

    public GlobalDataTemplateCSVContainer(Collection<GlobalDataTemplateCSVBean> beans) {
        this.beans = beans;
    }

    @Override
    public Collection<GlobalDataTemplateCSVBean> beans() {
        return this.beans;
    }

    @Override
    public String[] headers() {
        return new String[]{"Template type", "Name on server", "Name on cloud", "Module key on server", "Module key on cloud"};
    }

    @Override
    public String[] fieldMappings() {
        return new String[]{"globalTemplateType", "serverTemplateName", "cloudTemplateName", "templateModuleKey", "templateModuleKey"};
    }
}


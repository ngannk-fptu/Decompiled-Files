/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.google.common.base.Joiner
 *  com.google.common.base.Splitter
 *  com.google.common.collect.Lists
 *  org.apache.commons.lang3.StringUtils
 */
package com.atlassian.confluence.plugins.tasklist.macro.validator;

import com.atlassian.confluence.plugins.tasklist.macro.ColumnNameMapper;
import com.atlassian.confluence.plugins.tasklist.macro.validator.AbstractValidator;
import com.atlassian.confluence.plugins.tasklist.macro.validator.ValidatedErrorType;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class DisplayColumnsValidator
extends AbstractValidator {
    public DisplayColumnsValidator(String fieldName, String input) {
        super(fieldName);
        this.input = StringUtils.isBlank((CharSequence)input) ? "" : input;
    }

    public DisplayColumnsValidator(String fieldName, List<String> columns) {
        super(fieldName);
        this.input = Joiner.on((String)",").join(columns);
    }

    @Override
    public boolean validate() {
        if (StringUtils.isBlank((CharSequence)this.input)) {
            return true;
        }
        Iterable names = Splitter.on((char)',').trimResults().split((CharSequence)this.input);
        ArrayList errorColumns = Lists.newArrayList();
        for (String column : names) {
            if (ColumnNameMapper.COLUMNS.contains(column)) continue;
            errorColumns.add(column);
        }
        for (int i = 0; i < errorColumns.size(); ++i) {
            errorColumns.set(i, "[" + (String)errorColumns.get(i) + "]");
        }
        if (errorColumns.size() == 1) {
            this.error = new ValidatedErrorType(this.fieldNameCode, "com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report-macro.param.columns.error", new String[]{Joiner.on((String)", ").join((Iterable)errorColumns)});
        } else if (errorColumns.size() > 1) {
            this.error = new ValidatedErrorType(this.fieldNameCode, "com.atlassian.confluence.plugins.confluence-inline-tasks.tasks-report-macro.param.columns.multi.error", new String[]{Joiner.on((String)", ").join((Iterable)errorColumns)});
        }
        return errorColumns.isEmpty();
    }
}


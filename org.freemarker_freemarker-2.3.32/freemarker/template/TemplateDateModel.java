/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModel;
import freemarker.template.TemplateModelException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public interface TemplateDateModel
extends TemplateModel {
    public static final int UNKNOWN = 0;
    public static final int TIME = 1;
    public static final int DATE = 2;
    public static final int DATETIME = 3;
    public static final List TYPE_NAMES = Collections.unmodifiableList(Arrays.asList("UNKNOWN", "TIME", "DATE", "DATETIME"));

    public Date getAsDate() throws TemplateModelException;

    public int getDateType();
}


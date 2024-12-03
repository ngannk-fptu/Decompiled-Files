/*
 * Decompiled with CFR 0.152.
 */
package freemarker.template;

import freemarker.template.TemplateModelException;
import java.io.IOException;

public interface TransformControl {
    public static final int REPEAT_EVALUATION = 0;
    public static final int END_EVALUATION = 1;
    public static final int SKIP_BODY = 0;
    public static final int EVALUATE_BODY = 1;

    public int onStart() throws TemplateModelException, IOException;

    public int afterBody() throws TemplateModelException, IOException;

    public void onError(Throwable var1) throws Throwable;
}


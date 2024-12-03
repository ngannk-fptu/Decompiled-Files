/*
 * Decompiled with CFR 0.152.
 */
package freemarker.core;

import freemarker.core.TemplateNumberFormat;
import freemarker.core.UnformattableValueException;

abstract class BackwardCompatibleTemplateNumberFormat
extends TemplateNumberFormat {
    BackwardCompatibleTemplateNumberFormat() {
    }

    abstract String format(Number var1) throws UnformattableValueException;
}


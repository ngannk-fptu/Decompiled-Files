/*
 * Decompiled with CFR 0.152.
 */
package org.apache.poi.ss.formula;

import org.apache.poi.ss.formula.WorkbookEvaluator;
import org.apache.poi.util.Internal;

@Internal
public interface WorkbookEvaluatorProvider {
    public WorkbookEvaluator _getWorkbookEvaluator();
}


/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.confluence.plugins.gatekeeper.export;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.PreEvaluationResult;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import java.io.OutputStream;

public interface Exporter {
    public void export(Evaluator var1, PreEvaluationResult var2, OutputStream var3) throws Exception;

    public void processRow(TinyOwner var1, PermissionSet[] var2, boolean var3) throws Exception;
}


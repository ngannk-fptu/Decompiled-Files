/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.util.velocity.VelocityUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.plugins.gatekeeper.export;

import com.atlassian.confluence.plugins.gatekeeper.evaluator.Evaluator;
import com.atlassian.confluence.plugins.gatekeeper.export.CsvExporter;
import com.atlassian.confluence.plugins.gatekeeper.export.Exporter;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.ExportSettings;
import com.atlassian.confluence.plugins.gatekeeper.model.evaluation.result.PreEvaluationResult;
import com.atlassian.confluence.plugins.gatekeeper.model.owner.TinyOwner;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permission;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.PermissionSet;
import com.atlassian.confluence.plugins.gatekeeper.model.permission.Permissions;
import com.atlassian.confluence.plugins.gatekeeper.model.space.TinySpace;
import com.atlassian.confluence.util.velocity.VelocityUtils;
import java.util.HashMap;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public abstract class AbstractExporter
implements Exporter {
    private static final Logger logger = LoggerFactory.getLogger(CsvExporter.class);
    private ExportSettings exportSettings;
    private String spaceFormat;

    public AbstractExporter(ExportSettings exportSettings) {
        this.exportSettings = exportSettings;
    }

    protected void process(Evaluator evaluator, PreEvaluationResult preEvaluationResult) throws Exception {
        List<TinySpace> spaceList = preEvaluationResult.getSpaces();
        this.initSpaceFormat();
        logger.debug("Space format: " + this.spaceFormat);
        this.beforeRow();
        if (this.exportSettings.isHideFixColumnHeaders()) {
            this.writeCell("");
            this.writeCell("");
            this.writeCell("");
        } else {
            this.writeCell("Username");
            this.writeCell("Display name");
            this.writeCell("Permission");
        }
        for (TinySpace space : spaceList) {
            this.writeCell(this.formatSpace(space));
        }
        this.afterRow();
        evaluator.export(this);
    }

    @Override
    public void processRow(TinyOwner owner, PermissionSet[] permissionSets, boolean allowPartialPermissions) throws Exception {
        for (Permission permission : Permissions.ALL_PERMISSIONS) {
            if (!permission.isSupported()) continue;
            this.beforeRow();
            this.writeCell(owner.getName());
            this.writeCell(owner.getDisplayName());
            this.writeCell(permission.getLabel());
            for (PermissionSet permissionSet : permissionSets) {
                if (permissionSet.isPartiallyPermitted(permission)) {
                    this.writeCell(allowPartialPermissions ? "P" : "F");
                    continue;
                }
                if (permissionSet.isPermitted(permission)) {
                    this.writeCell("T");
                    continue;
                }
                this.writeCell("F");
            }
            this.afterRow();
        }
    }

    protected void beforeRow() {
    }

    protected void afterRow() throws Exception {
    }

    protected abstract void writeCell(Object var1) throws Exception;

    private void initSpaceFormat() {
        switch (this.exportSettings.getSpaceDetailsFormat()) {
            case "key": {
                this.spaceFormat = "${key}";
                break;
            }
            case "name": {
                this.spaceFormat = "${name}";
                break;
            }
            case "key-name": {
                this.spaceFormat = "${key} - ${name}";
                break;
            }
            case "key-name-desc": {
                this.spaceFormat = "${key} - ${name}#if(\"$!desc\" != \"\") (${desc})#end";
                break;
            }
            case "custom": {
                this.spaceFormat = this.exportSettings.getCustomSpaceDetailsFormat();
            }
        }
    }

    private String formatSpace(TinySpace space) {
        HashMap<String, String> context = new HashMap<String, String>();
        context.put("key", space.getKey());
        context.put("name", space.getName());
        return VelocityUtils.getRenderedContent((CharSequence)this.spaceFormat, context);
    }
}


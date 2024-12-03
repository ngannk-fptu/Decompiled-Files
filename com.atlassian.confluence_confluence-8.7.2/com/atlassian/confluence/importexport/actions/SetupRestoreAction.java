/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.sal.api.websudo.WebSudoRequired
 *  com.atlassian.xwork.HttpMethod
 *  com.atlassian.xwork.PermittedMethods
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.atlassian.xwork.XsrfTokenGenerator
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.atlassian.confluence.importexport.actions;

import com.atlassian.confluence.impl.security.SystemAdminOnly;
import com.atlassian.confluence.importexport.ImportExportException;
import com.atlassian.confluence.importexport.actions.RestoreAction;
import com.atlassian.confluence.importexport.actions.SetupRestoreHelper;
import com.atlassian.confluence.importexport.impl.ExportDescriptor;
import com.atlassian.confluence.importexport.impl.ExportScope;
import com.atlassian.confluence.importexport.impl.UnexpectedImportZipFileContents;
import com.atlassian.confluence.util.GeneralUtil;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.sal.api.websudo.WebSudoRequired;
import com.atlassian.xwork.HttpMethod;
import com.atlassian.xwork.PermittedMethods;
import com.atlassian.xwork.RequireSecurityToken;
import com.atlassian.xwork.XsrfTokenGenerator;
import java.io.File;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Deprecated
@WebSudoRequired
@SystemAdminOnly
public class SetupRestoreAction
extends RestoreAction {
    private static final Logger log = LoggerFactory.getLogger(SetupRestoreAction.class);
    private XsrfTokenGenerator xsrfTokenGenerator;

    @Override
    @PermittedMethods(value={HttpMethod.POST})
    @RequireSecurityToken(value=true)
    public String execute() throws Exception {
        SetupRestoreHelper.prepareForRestore();
        String result = super.execute();
        if ("success".equals(result)) {
            SetupRestoreHelper.postRestoreSteps();
        }
        return result;
    }

    @Override
    public boolean isPermitted() {
        return !GeneralUtil.isSetupComplete();
    }

    @Override
    public void validate() {
        File upload = null;
        try {
            upload = this.getRestoreFileFromUpload();
            ExportScope exportScope = ExportDescriptor.getExportDescriptor(upload).getScope();
            if (exportScope != ExportScope.ALL) {
                this.addActionError(this.getText("error.trying.to.restore.space.export"));
            }
        }
        catch (ImportExportException e) {
            log.error("Could not locate the backup you wish to restore: ", (Throwable)e);
        }
        catch (ExportScope.IllegalExportScopeException e) {
            this.addActionError("error.could.not.determine.export.type");
        }
        catch (Exception e) {
            log.error("Could not unzip uploaded file: [" + (upload != null ? upload.getName() : "") + "]. ", (Throwable)e);
        }
        catch (UnexpectedImportZipFileContents unexpectedImportZipFileContents) {
            this.addActionError(HtmlUtil.htmlEncode(unexpectedImportZipFileContents.getMessage()));
        }
    }

    public XsrfTokenGenerator getXsrfTokenGenerator() {
        return this.xsrfTokenGenerator;
    }

    public void setXsrfTokenGenerator(XsrfTokenGenerator xsrfTokenGenerator) {
        this.xsrfTokenGenerator = xsrfTokenGenerator;
    }
}


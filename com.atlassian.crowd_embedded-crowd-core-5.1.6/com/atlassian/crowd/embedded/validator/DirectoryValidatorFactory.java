/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.crowd.embedded.api.Directory
 *  com.atlassian.crowd.embedded.api.DirectoryType
 *  com.atlassian.crowd.validator.DirectoryValidationContext
 */
package com.atlassian.crowd.embedded.validator;

import com.atlassian.crowd.embedded.api.Directory;
import com.atlassian.crowd.embedded.api.DirectoryType;
import com.atlassian.crowd.embedded.validator.Validator;
import com.atlassian.crowd.validator.DirectoryValidationContext;
import java.util.EnumSet;

public interface DirectoryValidatorFactory {
    public Validator<Directory> getValidator(DirectoryType var1, EnumSet<DirectoryValidationContext> var2);
}


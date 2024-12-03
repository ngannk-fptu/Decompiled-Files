/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.ccev.EmailValidator
 *  com.atlassian.ccev.EmailValidator$Result
 *  com.atlassian.crowd.validator.EmailAddressValidator
 */
package com.atlassian.crowd.validator;

import com.atlassian.ccev.EmailValidator;
import com.atlassian.crowd.validator.EmailAddressValidator;
import java.util.List;
import java.util.function.Function;

public class CloudCompatibleEmailValidator
implements EmailAddressValidator {
    private final EmailValidator emailValidator = new EmailValidator();

    public boolean isValidSyntax(String emailAddress) {
        return this.emailValidator.validate(emailAddress);
    }

    public long validateSyntax(List<String> emailAddresses) {
        return this.emailValidator.validate(emailAddresses, Function.identity()).stream().filter(result -> !result.isValid()).count();
    }

    public long findDuplicates(List<String> emailAddresses) {
        return this.emailValidator.validate(emailAddresses, Function.identity()).stream().filter(EmailValidator.Result::isDuplicated).count();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.fugue.Function2
 *  com.google.common.base.Supplier
 */
package com.atlassian.confluence.api.model.validation;

import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.api.service.exceptions.BadRequestException;
import com.atlassian.confluence.api.service.exceptions.ConflictException;
import com.atlassian.confluence.api.service.exceptions.GoneException;
import com.atlassian.confluence.api.service.exceptions.LicenseUnavailableException;
import com.atlassian.confluence.api.service.exceptions.NotAuthenticatedException;
import com.atlassian.confluence.api.service.exceptions.NotFoundException;
import com.atlassian.confluence.api.service.exceptions.PermissionException;
import com.atlassian.confluence.api.service.exceptions.ServiceException;
import com.atlassian.confluence.api.service.exceptions.unchecked.NotImplementedServiceException;
import com.atlassian.fugue.Function2;
import com.google.common.base.Supplier;
import java.util.function.BiFunction;

public class ServiceExceptionSupplier {
    @Deprecated
    public static Function2<String, ValidationResult, ? extends NotFoundException> notFoundException() {
        return NotFoundException::new;
    }

    public static BiFunction<String, ValidationResult, ? extends NotFoundException> notFoundExceptionSupplier() {
        return NotFoundException::new;
    }

    @Deprecated
    public static Supplier<NotFoundException> notFoundException(String errorMsg) {
        return () -> new NotFoundException(errorMsg);
    }

    public static java.util.function.Supplier<NotFoundException> notFound(String message) {
        return () -> new NotFoundException(message);
    }

    @Deprecated
    public static Function2<String, ValidationResult, ? extends GoneException> goneException() {
        return GoneException::new;
    }

    public static BiFunction<String, ValidationResult, ? extends GoneException> goneExceptionSupplier() {
        return GoneException::new;
    }

    @Deprecated
    public static Supplier<GoneException> goneException(String errorMsg) {
        return () -> new GoneException(errorMsg);
    }

    public static java.util.function.Supplier<GoneException> goneExceptionSupplier(String errorMsg) {
        return () -> new GoneException(errorMsg);
    }

    @Deprecated
    public static Function2<String, ValidationResult, ? extends ConflictException> conflictException() {
        return ConflictException::new;
    }

    public static BiFunction<String, ValidationResult, ? extends ConflictException> conflictExceptionSupplier() {
        return ConflictException::new;
    }

    @Deprecated
    public static Function2<String, ValidationResult, ? extends NotImplementedServiceException> notImplemented() {
        return NotImplementedServiceException::new;
    }

    public static BiFunction<String, ValidationResult, ? extends NotImplementedServiceException> notImplementedSupplier() {
        return NotImplementedServiceException::new;
    }

    @Deprecated
    public static Function2<String, ValidationResult, ? extends BadRequestException> badRequestException() {
        return BadRequestException::new;
    }

    public static BiFunction<String, ValidationResult, ? extends BadRequestException> badRequestExceptionSupplier() {
        return BadRequestException::new;
    }

    @Deprecated
    public static Function2<String, ValidationResult, ? extends PermissionException> permissionExceptionException() {
        return PermissionException::new;
    }

    public static BiFunction<String, ValidationResult, ? extends PermissionException> permissionExceptionExceptionSupplier() {
        return PermissionException::new;
    }

    public static BiFunction<String, ValidationResult, LicenseUnavailableException> licenseUnavailableExceptionSupplier() {
        return LicenseUnavailableException::new;
    }

    public static BiFunction<String, ValidationResult, ServiceException> notAuthenticatedExceptionSupplier() {
        return NotAuthenticatedException::new;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.api.model.validation.SimpleValidationResult
 *  com.atlassian.confluence.api.model.validation.ValidationResult
 *  com.atlassian.fugue.Either
 *  io.atlassian.fugue.Either
 */
package com.atlassian.confluence.validation;

import com.atlassian.confluence.api.model.validation.SimpleValidationResult;
import com.atlassian.confluence.api.model.validation.ValidationResult;
import com.atlassian.confluence.util.FugueConversionUtil;
import io.atlassian.fugue.Either;
import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.function.Function;

public class Validation<T>
implements Serializable {
    private static final long serialVersionUID = -1L;
    private final Either<ValidationResult, T> either;

    public static <T> Validation<T> success(T success) {
        return new Validation<T>(Either.right(success));
    }

    public static <T> Validation<T> fail(ValidationResult fail) {
        return new Validation<T>(Either.left((Object)fail));
    }

    private Validation(Either<ValidationResult, T> either) {
        this.either = either;
    }

    public T success() {
        if (this.isSuccess()) {
            return (T)this.either.right().get();
        }
        throw new NoSuchElementException();
    }

    public ValidationResult fail() {
        if (this.isFail()) {
            return (ValidationResult)this.either.left().get();
        }
        throw new NoSuchElementException();
    }

    public boolean isFail() {
        return this.either.isLeft();
    }

    public boolean isSuccess() {
        return this.either.isRight();
    }

    @Deprecated
    public com.atlassian.fugue.Either<ValidationResult, T> toEither() {
        return FugueConversionUtil.toComEither(this.either);
    }

    public Either<ValidationResult, T> asEither() {
        return FugueConversionUtil.toIoEither(this.toEither());
    }

    public <A> Validation<A> flatMap(Function<T, Validation<A>> f) {
        return this.isSuccess() ? f.apply(this.success()) : Validation.fail(this.fail());
    }

    public <A> Validation<A> applyValidation(Function<T, Validation<A>> f) {
        return this.flatMap(f);
    }

    public ValidationResult getValidationResult() {
        return this.isSuccess() ? SimpleValidationResult.VALID : this.fail();
    }
}


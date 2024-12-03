/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.springframework.core.MethodParameter
 *  org.springframework.validation.BindException
 *  org.springframework.validation.BindingResult
 *  org.springframework.validation.ObjectError
 */
package org.springframework.web.bind;

import org.springframework.core.MethodParameter;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;

public class MethodArgumentNotValidException
extends BindException {
    private final MethodParameter parameter;

    public MethodArgumentNotValidException(MethodParameter parameter, BindingResult bindingResult) {
        super(bindingResult);
        this.parameter = parameter;
    }

    public final MethodParameter getParameter() {
        return this.parameter;
    }

    public String getMessage() {
        StringBuilder sb = new StringBuilder("Validation failed for argument [").append(this.parameter.getParameterIndex()).append("] in ").append(this.parameter.getExecutable().toGenericString());
        BindingResult bindingResult = this.getBindingResult();
        if (bindingResult.getErrorCount() > 1) {
            sb.append(" with ").append(bindingResult.getErrorCount()).append(" errors");
        }
        sb.append(": ");
        for (ObjectError error : bindingResult.getAllErrors()) {
            sb.append('[').append(error).append("] ");
        }
        return sb.toString();
    }
}


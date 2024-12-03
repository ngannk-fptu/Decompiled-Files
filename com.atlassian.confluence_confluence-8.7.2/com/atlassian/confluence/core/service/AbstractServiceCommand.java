/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.user.User
 */
package com.atlassian.confluence.core.service;

import com.atlassian.confluence.core.service.DefaultServiceCommandValidator;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.core.service.NotValidException;
import com.atlassian.confluence.core.service.ServiceCommand;
import com.atlassian.confluence.core.service.ServiceCommandValidator;
import com.atlassian.confluence.core.service.ValidationError;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.user.User;
import java.util.Collection;

public abstract class AbstractServiceCommand
implements ServiceCommand {
    private ServiceCommandState state = ServiceCommandState.NEW;
    private final ServiceCommandValidator validator = new DefaultServiceCommandValidator();

    @Override
    public final boolean isValid() {
        if (this.state == ServiceCommandState.VALID || this.state == ServiceCommandState.COMPLETE) {
            return true;
        }
        if (this.state == ServiceCommandState.NOT_VALID) {
            return false;
        }
        this.state = this.state.validate(this);
        return this.state.isValid();
    }

    @Override
    public Collection<ValidationError> getValidationErrors() {
        return this.validator.getValidationErrors();
    }

    @Override
    public final boolean isAuthorized() {
        if (this.state == ServiceCommandState.NEW) {
            this.state = this.state.authorize(this);
        }
        return this.state.isAuthorized();
    }

    @Override
    public final void execute() {
        this.state = this.state.execute(this);
    }

    private String getCurrentUsername() {
        return this.getCurrentUser() == null ? null : this.getCurrentUser().getName();
    }

    protected final User getCurrentUser() {
        return AuthenticatedUserThreadLocal.get();
    }

    protected abstract void validateInternal(ServiceCommandValidator var1);

    protected abstract boolean isAuthorizedInternal();

    protected abstract void executeInternal();

    private static abstract class ServiceCommandState {
        private static final ServiceCommandState COMPLETE = new ServiceCommandState(){

            @Override
            ServiceCommandState validate(AbstractServiceCommand command) {
                throw new IllegalStateException("Command already executed");
            }

            @Override
            ServiceCommandState authorize(AbstractServiceCommand command) {
                throw new IllegalStateException("Command already executed");
            }

            @Override
            ServiceCommandState execute(AbstractServiceCommand command) {
                throw new IllegalStateException("Command already executed");
            }

            @Override
            boolean isValid() {
                return true;
            }

            @Override
            boolean isAuthorized() {
                return true;
            }

            public String toString() {
                return "COMPLETE";
            }
        };
        private static final ServiceCommandState VALID = new ServiceCommandState(){

            @Override
            ServiceCommandState validate(AbstractServiceCommand command) {
                return this;
            }

            @Override
            ServiceCommandState authorize(AbstractServiceCommand command) {
                return this;
            }

            @Override
            ServiceCommandState execute(AbstractServiceCommand command) {
                command.executeInternal();
                return COMPLETE;
            }

            @Override
            boolean isValid() {
                return true;
            }

            @Override
            boolean isAuthorized() {
                return true;
            }

            public String toString() {
                return "VALID";
            }
        };
        private static final ServiceCommandState NOT_VALID = new ServiceCommandState(){

            @Override
            ServiceCommandState validate(AbstractServiceCommand command) {
                return this;
            }

            @Override
            ServiceCommandState authorize(AbstractServiceCommand command) {
                return this;
            }

            @Override
            ServiceCommandState execute(AbstractServiceCommand command) {
                throw new NotValidException("" + command.getValidationErrors());
            }

            @Override
            boolean isValid() {
                return false;
            }

            @Override
            boolean isAuthorized() {
                return true;
            }

            public String toString() {
                return "NOT_VALID";
            }
        };
        private static final ServiceCommandState AUTHORIZED = new ServiceCommandState(){

            @Override
            ServiceCommandState validate(AbstractServiceCommand command) {
                command.validateInternal(command.validator);
                return command.getValidationErrors().isEmpty() ? VALID : NOT_VALID;
            }

            @Override
            ServiceCommandState authorize(AbstractServiceCommand command) {
                return this;
            }

            @Override
            ServiceCommandState execute(AbstractServiceCommand command) {
                command.state = this.validate(command);
                return command.state.execute(command);
            }

            @Override
            boolean isValid() {
                throw new IllegalStateException("Command is not yet validated");
            }

            @Override
            boolean isAuthorized() {
                return true;
            }

            public String toString() {
                return "AUTHORIZED";
            }
        };
        private static final ServiceCommandState NOT_AUTHORIZED = new ServiceCommandState(){

            @Override
            ServiceCommandState validate(AbstractServiceCommand command) {
                throw new NotAuthorizedException(command.getCurrentUsername());
            }

            @Override
            ServiceCommandState authorize(AbstractServiceCommand command) {
                return this;
            }

            @Override
            ServiceCommandState execute(AbstractServiceCommand command) {
                throw new NotAuthorizedException(command.getCurrentUsername());
            }

            @Override
            boolean isValid() {
                throw new IllegalStateException("Command is not yet validated");
            }

            @Override
            boolean isAuthorized() {
                return false;
            }

            public String toString() {
                return "NOT_AUTHORIZED";
            }
        };
        private static final ServiceCommandState NEW = new ServiceCommandState(){

            @Override
            ServiceCommandState validate(AbstractServiceCommand command) {
                command.state = this.authorize(command);
                return command.state.validate(command);
            }

            @Override
            ServiceCommandState authorize(AbstractServiceCommand command) {
                return command.isAuthorizedInternal() ? AUTHORIZED : NOT_AUTHORIZED;
            }

            @Override
            ServiceCommandState execute(AbstractServiceCommand command) {
                command.state = this.authorize(command);
                command.state = command.state.validate(command);
                return command.state.execute(command);
            }

            @Override
            boolean isValid() {
                throw new IllegalStateException("Command is not yet validated");
            }

            @Override
            boolean isAuthorized() {
                throw new IllegalStateException("Command is not yet authorized");
            }

            public String toString() {
                return "NEW";
            }
        };

        private ServiceCommandState() {
        }

        abstract ServiceCommandState validate(AbstractServiceCommand var1);

        abstract ServiceCommandState authorize(AbstractServiceCommand var1);

        abstract ServiceCommandState execute(AbstractServiceCommand var1);

        abstract boolean isValid();

        abstract boolean isAuthorized();
    }
}


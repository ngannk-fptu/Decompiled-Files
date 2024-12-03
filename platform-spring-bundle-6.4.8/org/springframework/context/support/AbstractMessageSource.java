/*
 * Decompiled with CFR 0.152.
 */
package org.springframework.context.support;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Properties;
import org.springframework.context.HierarchicalMessageSource;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.context.support.MessageSourceSupport;
import org.springframework.lang.Nullable;
import org.springframework.util.ObjectUtils;

public abstract class AbstractMessageSource
extends MessageSourceSupport
implements HierarchicalMessageSource {
    @Nullable
    private MessageSource parentMessageSource;
    @Nullable
    private Properties commonMessages;
    private boolean useCodeAsDefaultMessage = false;

    @Override
    public void setParentMessageSource(@Nullable MessageSource parent) {
        this.parentMessageSource = parent;
    }

    @Override
    @Nullable
    public MessageSource getParentMessageSource() {
        return this.parentMessageSource;
    }

    public void setCommonMessages(@Nullable Properties commonMessages) {
        this.commonMessages = commonMessages;
    }

    @Nullable
    protected Properties getCommonMessages() {
        return this.commonMessages;
    }

    public void setUseCodeAsDefaultMessage(boolean useCodeAsDefaultMessage) {
        this.useCodeAsDefaultMessage = useCodeAsDefaultMessage;
    }

    protected boolean isUseCodeAsDefaultMessage() {
        return this.useCodeAsDefaultMessage;
    }

    @Override
    public final String getMessage(String code, @Nullable Object[] args, @Nullable String defaultMessage, Locale locale) {
        String msg = this.getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        if (defaultMessage == null) {
            return this.getDefaultMessage(code);
        }
        return this.renderDefaultMessage(defaultMessage, args, locale);
    }

    @Override
    public final String getMessage(String code, @Nullable Object[] args, Locale locale) throws NoSuchMessageException {
        String msg = this.getMessageInternal(code, args, locale);
        if (msg != null) {
            return msg;
        }
        String fallback = this.getDefaultMessage(code);
        if (fallback != null) {
            return fallback;
        }
        throw new NoSuchMessageException(code, locale);
    }

    @Override
    public final String getMessage(MessageSourceResolvable resolvable, Locale locale) throws NoSuchMessageException {
        String defaultMessage;
        Object[] codes = resolvable.getCodes();
        if (codes != null) {
            for (Object code : codes) {
                String message = this.getMessageInternal((String)code, resolvable.getArguments(), locale);
                if (message == null) continue;
                return message;
            }
        }
        if ((defaultMessage = this.getDefaultMessage(resolvable, locale)) != null) {
            return defaultMessage;
        }
        throw new NoSuchMessageException((String)(!ObjectUtils.isEmpty(codes) ? codes[codes.length - 1] : ""), locale);
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    protected String getMessageInternal(@Nullable String code, @Nullable Object[] args, @Nullable Locale locale) {
        String commonMessage;
        Properties commonMessages;
        if (code == null) {
            return null;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        Object[] argsToUse = args;
        if (!this.isAlwaysUseMessageFormat() && ObjectUtils.isEmpty(args)) {
            String message = this.resolveCodeWithoutArguments(code, locale);
            if (message != null) {
                return message;
            }
        } else {
            argsToUse = this.resolveArguments(args, locale);
            MessageFormat messageFormat = this.resolveCode(code, locale);
            if (messageFormat != null) {
                MessageFormat messageFormat2 = messageFormat;
                synchronized (messageFormat2) {
                    return messageFormat.format(argsToUse);
                }
            }
        }
        if ((commonMessages = this.getCommonMessages()) != null && (commonMessage = commonMessages.getProperty(code)) != null) {
            return this.formatMessage(commonMessage, args, locale);
        }
        return this.getMessageFromParent(code, argsToUse, locale);
    }

    @Nullable
    protected String getMessageFromParent(String code, @Nullable Object[] args, Locale locale) {
        MessageSource parent = this.getParentMessageSource();
        if (parent != null) {
            if (parent instanceof AbstractMessageSource) {
                return ((AbstractMessageSource)parent).getMessageInternal(code, args, locale);
            }
            return parent.getMessage(code, args, null, locale);
        }
        return null;
    }

    @Nullable
    protected String getDefaultMessage(MessageSourceResolvable resolvable, Locale locale) {
        String defaultMessage = resolvable.getDefaultMessage();
        Object[] codes = resolvable.getCodes();
        if (defaultMessage != null) {
            if (resolvable instanceof DefaultMessageSourceResolvable && !((DefaultMessageSourceResolvable)resolvable).shouldRenderDefaultMessage()) {
                return defaultMessage;
            }
            if (!ObjectUtils.isEmpty(codes) && defaultMessage.equals(codes[0])) {
                return defaultMessage;
            }
            return this.renderDefaultMessage(defaultMessage, resolvable.getArguments(), locale);
        }
        return !ObjectUtils.isEmpty(codes) ? this.getDefaultMessage((String)codes[0]) : null;
    }

    @Nullable
    protected String getDefaultMessage(String code) {
        if (this.isUseCodeAsDefaultMessage()) {
            return code;
        }
        return null;
    }

    @Override
    protected Object[] resolveArguments(@Nullable Object[] args, Locale locale) {
        if (ObjectUtils.isEmpty(args)) {
            return super.resolveArguments(args, locale);
        }
        ArrayList<Object> resolvedArgs = new ArrayList<Object>(args.length);
        for (Object arg : args) {
            if (arg instanceof MessageSourceResolvable) {
                resolvedArgs.add(this.getMessage((MessageSourceResolvable)arg, locale));
                continue;
            }
            resolvedArgs.add(arg);
        }
        return resolvedArgs.toArray();
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @Nullable
    protected String resolveCodeWithoutArguments(String code, Locale locale) {
        MessageFormat messageFormat = this.resolveCode(code, locale);
        if (messageFormat != null) {
            MessageFormat messageFormat2 = messageFormat;
            synchronized (messageFormat2) {
                return messageFormat.format(new Object[0]);
            }
        }
        return null;
    }

    @Nullable
    protected abstract MessageFormat resolveCode(String var1, Locale var2);
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.hibernate.validator.spi.resourceloading.ResourceBundleLocator
 *  org.springframework.util.Assert
 */
package org.springframework.validation.beanvalidation;

import java.util.Locale;
import java.util.ResourceBundle;
import org.hibernate.validator.spi.resourceloading.ResourceBundleLocator;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceResourceBundle;
import org.springframework.util.Assert;

public class MessageSourceResourceBundleLocator
implements ResourceBundleLocator {
    private final MessageSource messageSource;

    public MessageSourceResourceBundleLocator(MessageSource messageSource) {
        Assert.notNull((Object)messageSource, (String)"MessageSource must not be null");
        this.messageSource = messageSource;
    }

    public ResourceBundle getResourceBundle(Locale locale) {
        return new MessageSourceResourceBundle(this.messageSource, locale);
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.sun.istack.localization.Localizable
 *  com.sun.istack.localization.LocalizableMessage
 *  com.sun.istack.localization.LocalizableMessageFactory
 *  com.sun.istack.localization.LocalizableMessageFactory$ResourceBundleSupplier
 *  com.sun.istack.localization.Localizer
 *  com.sun.istack.localization.NullLocalizable
 *  javax.xml.ws.WebServiceException
 */
package com.sun.xml.ws.util.exception;

import com.sun.istack.localization.Localizable;
import com.sun.istack.localization.LocalizableMessage;
import com.sun.istack.localization.LocalizableMessageFactory;
import com.sun.istack.localization.Localizer;
import com.sun.istack.localization.NullLocalizable;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Locale;
import java.util.ResourceBundle;
import javax.xml.ws.WebServiceException;

public abstract class JAXWSExceptionBase
extends WebServiceException
implements Localizable,
LocalizableMessageFactory.ResourceBundleSupplier {
    private static final long serialVersionUID = 1L;
    private transient Localizable msg;

    @Deprecated
    protected JAXWSExceptionBase(String key, Object ... args) {
        super(JAXWSExceptionBase.findNestedException(args));
        this.msg = new LocalizableMessage(this.getDefaultResourceBundleName(), (LocalizableMessageFactory.ResourceBundleSupplier)this, key, args);
    }

    protected JAXWSExceptionBase(String message) {
        this((Localizable)new NullLocalizable(message));
    }

    protected JAXWSExceptionBase(Throwable throwable) {
        this((Localizable)new NullLocalizable(throwable.toString()), throwable);
    }

    protected JAXWSExceptionBase(Localizable msg) {
        this.msg = msg;
    }

    protected JAXWSExceptionBase(Localizable msg, Throwable cause) {
        super(cause);
        this.msg = msg;
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.msg.getResourceBundleName());
        out.writeObject(this.msg.getKey());
        Object[] args = this.msg.getArguments();
        if (args == null) {
            out.writeInt(-1);
            return;
        }
        out.writeInt(args.length);
        for (int i = 0; i < args.length; ++i) {
            if (args[i] == null || args[i] instanceof Serializable) {
                out.writeObject(args[i]);
                continue;
            }
            out.writeObject(args[i].toString());
        }
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        Object[] args;
        in.defaultReadObject();
        String resourceBundleName = (String)in.readObject();
        String key = (String)in.readObject();
        int len = in.readInt();
        if (len == -1) {
            args = null;
        } else {
            args = new Object[len];
            for (int i = 0; i < args.length; ++i) {
                args[i] = in.readObject();
            }
        }
        this.msg = new LocalizableMessageFactory(resourceBundleName).getMessage(key, args);
    }

    private static Throwable findNestedException(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object o : args) {
            if (!(o instanceof Throwable)) continue;
            return (Throwable)o;
        }
        return null;
    }

    public String getMessage() {
        Localizer localizer = new Localizer();
        return localizer.localize((Localizable)this);
    }

    protected abstract String getDefaultResourceBundleName();

    public final String getKey() {
        return this.msg.getKey();
    }

    public final Object[] getArguments() {
        return this.msg.getArguments();
    }

    public final String getResourceBundleName() {
        return this.msg.getResourceBundleName();
    }

    public ResourceBundle getResourceBundle(Locale locale) {
        return ResourceBundle.getBundle(this.getDefaultResourceBundleName(), locale);
    }
}


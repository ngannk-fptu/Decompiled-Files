/*
 * Decompiled with CFR 0.152.
 */
package org.apache.axis.i18n;

import java.util.Locale;
import java.util.ResourceBundle;
import org.apache.axis.i18n.ProjectResourceBundle;

public class MessagesConstants {
    public static final String projectName = "org.apache.axis".intern();
    public static final String resourceName = "resource".intern();
    public static final Locale locale = null;
    public static final String rootPackageName = "org.apache.axis.i18n".intern();
    public static final ResourceBundle rootBundle = ProjectResourceBundle.getBundle(projectName, rootPackageName, resourceName, locale, (class$org$apache$axis$i18n$MessagesConstants == null ? (class$org$apache$axis$i18n$MessagesConstants = MessagesConstants.class$("org.apache.axis.i18n.MessagesConstants")) : class$org$apache$axis$i18n$MessagesConstants).getClassLoader(), null);
    static /* synthetic */ Class class$org$apache$axis$i18n$MessagesConstants;

    static /* synthetic */ Class class$(String x0) {
        try {
            return Class.forName(x0);
        }
        catch (ClassNotFoundException x1) {
            throw new NoClassDefFoundError(x1.getMessage());
        }
    }
}


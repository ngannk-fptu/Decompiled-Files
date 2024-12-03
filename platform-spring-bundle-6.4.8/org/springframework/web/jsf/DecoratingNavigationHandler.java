/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.faces.application.NavigationHandler
 *  javax.faces.context.FacesContext
 */
package org.springframework.web.jsf;

import javax.faces.application.NavigationHandler;
import javax.faces.context.FacesContext;
import org.springframework.lang.Nullable;

public abstract class DecoratingNavigationHandler
extends NavigationHandler {
    @Nullable
    private NavigationHandler decoratedNavigationHandler;

    protected DecoratingNavigationHandler() {
    }

    protected DecoratingNavigationHandler(NavigationHandler originalNavigationHandler) {
        this.decoratedNavigationHandler = originalNavigationHandler;
    }

    @Nullable
    public final NavigationHandler getDecoratedNavigationHandler() {
        return this.decoratedNavigationHandler;
    }

    public final void handleNavigation(FacesContext facesContext, String fromAction, String outcome) {
        this.handleNavigation(facesContext, fromAction, outcome, this.decoratedNavigationHandler);
    }

    public abstract void handleNavigation(FacesContext var1, @Nullable String var2, @Nullable String var3, @Nullable NavigationHandler var4);

    protected final void callNextHandlerInChain(FacesContext facesContext, @Nullable String fromAction, @Nullable String outcome, @Nullable NavigationHandler originalNavigationHandler) {
        NavigationHandler decoratedNavigationHandler = this.getDecoratedNavigationHandler();
        if (decoratedNavigationHandler instanceof DecoratingNavigationHandler) {
            DecoratingNavigationHandler decHandler = (DecoratingNavigationHandler)decoratedNavigationHandler;
            decHandler.handleNavigation(facesContext, fromAction, outcome, originalNavigationHandler);
        } else if (decoratedNavigationHandler != null) {
            decoratedNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        } else if (originalNavigationHandler != null) {
            originalNavigationHandler.handleNavigation(facesContext, fromAction, outcome);
        }
    }
}


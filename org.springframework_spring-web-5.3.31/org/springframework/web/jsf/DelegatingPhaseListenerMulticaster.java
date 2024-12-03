/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.faces.context.FacesContext
 *  javax.faces.event.PhaseEvent
 *  javax.faces.event.PhaseId
 *  javax.faces.event.PhaseListener
 *  org.springframework.beans.factory.BeanFactoryUtils
 *  org.springframework.beans.factory.ListableBeanFactory
 */
package org.springframework.web.jsf;

import java.util.Collection;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.ListableBeanFactory;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.jsf.FacesContextUtils;

public class DelegatingPhaseListenerMulticaster
implements PhaseListener {
    public PhaseId getPhaseId() {
        return PhaseId.ANY_PHASE;
    }

    public void beforePhase(PhaseEvent event) {
        for (PhaseListener listener : this.getDelegates(event.getFacesContext())) {
            listener.beforePhase(event);
        }
    }

    public void afterPhase(PhaseEvent event) {
        for (PhaseListener listener : this.getDelegates(event.getFacesContext())) {
            listener.afterPhase(event);
        }
    }

    protected Collection<PhaseListener> getDelegates(FacesContext facesContext) {
        ListableBeanFactory bf = this.getBeanFactory(facesContext);
        return BeanFactoryUtils.beansOfTypeIncludingAncestors((ListableBeanFactory)bf, PhaseListener.class, (boolean)true, (boolean)false).values();
    }

    protected ListableBeanFactory getBeanFactory(FacesContext facesContext) {
        return this.getWebApplicationContext(facesContext);
    }

    protected WebApplicationContext getWebApplicationContext(FacesContext facesContext) {
        return FacesContextUtils.getRequiredWebApplicationContext(facesContext);
    }
}


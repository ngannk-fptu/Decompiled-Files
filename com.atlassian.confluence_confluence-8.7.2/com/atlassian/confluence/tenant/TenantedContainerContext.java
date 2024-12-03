/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.config.spring.BootstrappedContainerContext
 *  com.atlassian.util.concurrent.ResettableLazyReference
 */
package com.atlassian.confluence.tenant;

import com.atlassian.config.spring.BootstrappedContainerContext;
import com.atlassian.confluence.tenant.TenantTicketCollector;
import com.atlassian.util.concurrent.ResettableLazyReference;

@Deprecated(forRemoval=true)
public class TenantedContainerContext
extends BootstrappedContainerContext {
    private ResettableLazyReference<TenantTicketCollector> ticketCollector = new ResettableLazyReference<TenantTicketCollector>(){

        protected TenantTicketCollector create() throws Exception {
            return (TenantTicketCollector)TenantedContainerContext.this.getApplicationContext().getBean("ticketCollector", TenantTicketCollector.class);
        }
    };

    public synchronized void refresh() {
        super.refresh();
        this.ticketCollector.reset();
    }

    public boolean isSetup() {
        return super.isSetup() && ((TenantTicketCollector)this.ticketCollector.get()).checkTicket();
    }
}


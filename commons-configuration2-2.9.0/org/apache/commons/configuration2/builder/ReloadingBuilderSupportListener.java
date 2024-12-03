/*
 * Decompiled with CFR 0.152.
 */
package org.apache.commons.configuration2.builder;

import org.apache.commons.configuration2.builder.BasicConfigurationBuilder;
import org.apache.commons.configuration2.builder.ConfigurationBuilderResultCreatedEvent;
import org.apache.commons.configuration2.event.Event;
import org.apache.commons.configuration2.event.EventListener;
import org.apache.commons.configuration2.reloading.ReloadingController;
import org.apache.commons.configuration2.reloading.ReloadingEvent;

final class ReloadingBuilderSupportListener
implements EventListener<Event> {
    private final BasicConfigurationBuilder<?> builder;
    private final ReloadingController reloadingController;

    private ReloadingBuilderSupportListener(BasicConfigurationBuilder<?> configBuilder, ReloadingController controller) {
        this.builder = configBuilder;
        this.reloadingController = controller;
    }

    public static ReloadingBuilderSupportListener connect(BasicConfigurationBuilder<?> configBuilder, ReloadingController controller) {
        ReloadingBuilderSupportListener listener = new ReloadingBuilderSupportListener(configBuilder, controller);
        controller.addEventListener(ReloadingEvent.ANY, listener);
        configBuilder.installEventListener(ConfigurationBuilderResultCreatedEvent.RESULT_CREATED, listener);
        return listener;
    }

    @Override
    public void onEvent(Event event) {
        if (ConfigurationBuilderResultCreatedEvent.RESULT_CREATED.equals(event.getEventType())) {
            this.reloadingController.resetReloadingState();
        } else {
            this.builder.resetResult();
        }
    }
}


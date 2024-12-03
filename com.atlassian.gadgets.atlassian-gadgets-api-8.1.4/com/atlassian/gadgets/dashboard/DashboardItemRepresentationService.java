/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.PublicApi
 *  io.atlassian.fugue.Option
 */
package com.atlassian.gadgets.dashboard;

import com.atlassian.annotations.PublicApi;
import com.atlassian.gadgets.DashboardItemState;
import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.dashboard.DashboardId;
import com.atlassian.gadgets.dashboard.DashboardItemRepresentation;
import com.atlassian.gadgets.dashboard.DashboardState;
import io.atlassian.fugue.Option;

@PublicApi
public interface DashboardItemRepresentationService {
    public Option<DashboardItemRepresentation> getRepresentation(DashboardItemState var1, RenderingContext var2);

    public static final class RenderingContext {
        private final GadgetRequestContext requestContext;
        private final DashboardId dashboardId;
        private final DashboardState.ColumnIndex columnIndex;
        private final boolean writable;

        private RenderingContext(GadgetRequestContext requestContext, DashboardId dashboardId, DashboardState.ColumnIndex columnIndex, boolean writable) {
            this.requestContext = requestContext;
            this.dashboardId = dashboardId;
            this.columnIndex = columnIndex;
            this.writable = writable;
        }

        public static RenderingContext readOnly(GadgetRequestContext requestContext, DashboardId dashboardId, DashboardState.ColumnIndex columnIndex) {
            return new RenderingContext(requestContext, dashboardId, columnIndex, false);
        }

        public static RenderingContext editable(GadgetRequestContext requestContext, DashboardId dashboardId, DashboardState.ColumnIndex columnIndex) {
            return new RenderingContext(requestContext, dashboardId, columnIndex, true);
        }

        public GadgetRequestContext getRequestContext() {
            return this.requestContext;
        }

        public DashboardId getDashboardId() {
            return this.dashboardId;
        }

        public DashboardState.ColumnIndex getColumnIndex() {
            return this.columnIndex;
        }

        public boolean isWritable() {
            return this.writable;
        }
    }
}


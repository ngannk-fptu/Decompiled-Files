/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.activeobjects.external;

public final class NoDataSourceException
extends IllegalStateException {
    public NoDataSourceException() {
        super("No data source is available, indicating that there is no tenant available to the application. Your plugin is probably performing tenant specific operations too eagerly.\ncom.atlassian.activeobjects.external.ActiveObjectsModuleMetaData contains utility methods that can assist you in this situation.\nisDataSourcePresent will safely indicate the availability of the data source.");
    }
}


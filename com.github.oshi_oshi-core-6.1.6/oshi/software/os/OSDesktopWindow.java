/*
 * Decompiled with CFR 0.152.
 */
package oshi.software.os;

import java.awt.Rectangle;
import oshi.annotation.concurrent.Immutable;

@Immutable
public class OSDesktopWindow {
    private final long windowId;
    private final String title;
    private final String command;
    private final Rectangle locAndSize;
    private final long owningProcessId;
    private final int order;
    private final boolean visible;

    public OSDesktopWindow(long windowId, String title, String command, Rectangle locAndSize, long owningProcessId, int order, boolean visible) {
        this.windowId = windowId;
        this.title = title;
        this.command = command;
        this.locAndSize = locAndSize;
        this.owningProcessId = owningProcessId;
        this.order = order;
        this.visible = visible;
    }

    public long getWindowId() {
        return this.windowId;
    }

    public String getTitle() {
        return this.title;
    }

    public String getCommand() {
        return this.command;
    }

    public Rectangle getLocAndSize() {
        return this.locAndSize;
    }

    public long getOwningProcessId() {
        return this.owningProcessId;
    }

    public int getOrder() {
        return this.order;
    }

    public boolean isVisible() {
        return this.visible;
    }

    public String toString() {
        return "OSDesktopWindow [windowId=" + this.windowId + ", title=" + this.title + ", command=" + this.command + ", locAndSize=" + this.locAndSize.toString() + ", owningProcessId=" + this.owningProcessId + ", order=" + this.order + ", visible=" + this.visible + "]";
    }
}


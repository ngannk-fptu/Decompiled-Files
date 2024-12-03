/*
 * Decompiled with CFR 0.152.
 */
package org.aspectj.bridge;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.aspectj.bridge.IMessage;
import org.aspectj.bridge.ISourceLocation;
import org.aspectj.bridge.MessageUtil;

public class Message
implements IMessage {
    private final String message;
    private final IMessage.Kind kind;
    private final Throwable thrown;
    private final ISourceLocation sourceLocation;
    private final String details;
    private final List<ISourceLocation> extraSourceLocations;
    private final boolean declared;
    private final int id;
    private final int sourceStart;
    private final int sourceEnd;

    public Message(String message, ISourceLocation location, boolean isError) {
        this(message, isError ? IMessage.ERROR : IMessage.WARNING, null, location);
    }

    public Message(String message, ISourceLocation location, boolean isError, ISourceLocation[] extraSourceLocations) {
        this(message, "", isError ? IMessage.ERROR : IMessage.WARNING, location, null, (ISourceLocation[])(extraSourceLocations.length > 0 ? extraSourceLocations : null));
    }

    public Message(String message, String details, IMessage.Kind kind, ISourceLocation sourceLocation, Throwable thrown, ISourceLocation[] extraSourceLocations) {
        this(message, details, kind, sourceLocation, thrown, extraSourceLocations, false, 0, -1, -1);
    }

    public Message(String message, String details, IMessage.Kind kind, ISourceLocation sLoc, Throwable thrown, ISourceLocation[] otherLocs, boolean declared, int id, int sourcestart, int sourceend) {
        this.details = details;
        this.id = id;
        this.sourceStart = sourcestart;
        this.sourceEnd = sourceend;
        this.message = message != null ? message : (thrown == null ? null : thrown.getMessage());
        this.kind = kind;
        this.sourceLocation = sLoc;
        this.thrown = thrown;
        this.extraSourceLocations = otherLocs != null ? Collections.unmodifiableList(Arrays.asList(otherLocs)) : Collections.emptyList();
        if (null == this.kind) {
            throw new IllegalArgumentException("null kind");
        }
        if (null == this.message) {
            throw new IllegalArgumentException("null message");
        }
        this.declared = declared;
    }

    public Message(String message, IMessage.Kind kind, Throwable thrown, ISourceLocation sourceLocation) {
        this(message, "", kind, sourceLocation, thrown, null);
    }

    @Override
    public IMessage.Kind getKind() {
        return this.kind;
    }

    @Override
    public boolean isError() {
        return this.kind == IMessage.ERROR;
    }

    @Override
    public boolean isWarning() {
        return this.kind == IMessage.WARNING;
    }

    @Override
    public boolean isDebug() {
        return this.kind == IMessage.DEBUG;
    }

    @Override
    public boolean isTaskTag() {
        return this.kind == IMessage.TASKTAG;
    }

    @Override
    public boolean isInfo() {
        return this.kind == IMessage.INFO;
    }

    @Override
    public boolean isAbort() {
        return this.kind == IMessage.ABORT;
    }

    @Override
    public boolean getDeclared() {
        return this.declared;
    }

    @Override
    public boolean isFailed() {
        return this.kind == IMessage.FAIL;
    }

    @Override
    public final String getMessage() {
        return this.message;
    }

    @Override
    public final Throwable getThrown() {
        return this.thrown;
    }

    @Override
    public final ISourceLocation getSourceLocation() {
        return this.sourceLocation;
    }

    public String toString() {
        return MessageUtil.renderMessage(this, false);
    }

    @Override
    public String getDetails() {
        return this.details;
    }

    @Override
    public List<ISourceLocation> getExtraSourceLocations() {
        return this.extraSourceLocations;
    }

    @Override
    public int getID() {
        return this.id;
    }

    @Override
    public int getSourceStart() {
        return this.sourceStart;
    }

    @Override
    public int getSourceEnd() {
        return this.sourceEnd;
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  kotlin.Metadata
 *  kotlin.jvm.internal.Intrinsics
 *  org.jetbrains.annotations.NotNull
 *  org.jetbrains.annotations.Nullable
 */
package com.addonengine.addons.analytics.service.confluence.model;

import java.net.URL;
import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(mv={1, 9, 0}, k=1, xi=48, d1={"\u0000.\n\u0002\u0018\u0002\n\u0002\u0010\u0000\n\u0000\n\u0002\u0010\t\n\u0000\n\u0002\u0010\u000e\n\u0000\n\u0002\u0018\u0002\n\u0002\b\f\n\u0002\u0010\u000b\n\u0002\b\u0002\n\u0002\u0010\b\n\u0002\b\u0002\b\u0086\b\u0018\u00002\u00020\u0001B\u001d\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\bJ\t\u0010\u000f\u001a\u00020\u0003H\u00c6\u0003J\t\u0010\u0010\u001a\u00020\u0005H\u00c6\u0003J\t\u0010\u0011\u001a\u00020\u0007H\u00c6\u0003J'\u0010\u0012\u001a\u00020\u00002\b\b\u0002\u0010\u0002\u001a\u00020\u00032\b\b\u0002\u0010\u0004\u001a\u00020\u00052\b\b\u0002\u0010\u0006\u001a\u00020\u0007H\u00c6\u0001J\u0013\u0010\u0013\u001a\u00020\u00142\b\u0010\u0015\u001a\u0004\u0018\u00010\u0001H\u00d6\u0003J\t\u0010\u0016\u001a\u00020\u0017H\u00d6\u0001J\t\u0010\u0018\u001a\u00020\u0005H\u00d6\u0001R\u0011\u0010\u0002\u001a\u00020\u0003\u00a2\u0006\b\n\u0000\u001a\u0004\b\t\u0010\nR\u0011\u0010\u0006\u001a\u00020\u0007\u00a2\u0006\b\n\u0000\u001a\u0004\b\u000b\u0010\fR\u0011\u0010\u0004\u001a\u00020\u0005\u00a2\u0006\b\n\u0000\u001a\u0004\b\r\u0010\u000e\u00a8\u0006\u0019"}, d2={"Lcom/addonengine/addons/analytics/service/confluence/model/Attachment;", "", "id", "", "name", "", "link", "Ljava/net/URL;", "(JLjava/lang/String;Ljava/net/URL;)V", "getId", "()J", "getLink", "()Ljava/net/URL;", "getName", "()Ljava/lang/String;", "component1", "component2", "component3", "copy", "equals", "", "other", "hashCode", "", "toString", "analytics"})
public final class Attachment {
    private final long id;
    @NotNull
    private final String name;
    @NotNull
    private final URL link;

    public Attachment(long id, @NotNull String name, @NotNull URL link) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        this.id = id;
        this.name = name;
        this.link = link;
    }

    public final long getId() {
        return this.id;
    }

    @NotNull
    public final String getName() {
        return this.name;
    }

    @NotNull
    public final URL getLink() {
        return this.link;
    }

    public final long component1() {
        return this.id;
    }

    @NotNull
    public final String component2() {
        return this.name;
    }

    @NotNull
    public final URL component3() {
        return this.link;
    }

    @NotNull
    public final Attachment copy(long id, @NotNull String name, @NotNull URL link) {
        Intrinsics.checkNotNullParameter((Object)name, (String)"name");
        Intrinsics.checkNotNullParameter((Object)link, (String)"link");
        return new Attachment(id, name, link);
    }

    public static /* synthetic */ Attachment copy$default(Attachment attachment, long l, String string, URL uRL, int n, Object object) {
        if ((n & 1) != 0) {
            l = attachment.id;
        }
        if ((n & 2) != 0) {
            string = attachment.name;
        }
        if ((n & 4) != 0) {
            uRL = attachment.link;
        }
        return attachment.copy(l, string, uRL);
    }

    @NotNull
    public String toString() {
        return "Attachment(id=" + this.id + ", name=" + this.name + ", link=" + this.link + ')';
    }

    public int hashCode() {
        int result = Long.hashCode(this.id);
        result = result * 31 + this.name.hashCode();
        result = result * 31 + this.link.hashCode();
        return result;
    }

    public boolean equals(@Nullable Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Attachment)) {
            return false;
        }
        Attachment attachment = (Attachment)other;
        if (this.id != attachment.id) {
            return false;
        }
        if (!Intrinsics.areEqual((Object)this.name, (Object)attachment.name)) {
            return false;
        }
        return Intrinsics.areEqual((Object)this.link, (Object)attachment.link);
    }
}


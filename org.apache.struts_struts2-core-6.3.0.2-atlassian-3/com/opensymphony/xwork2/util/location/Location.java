/*
 * Decompiled with CFR 0.152.
 */
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.location.LocationImpl;
import java.util.List;

public interface Location {
    public static final Location UNKNOWN = LocationImpl.UNKNOWN;

    public String getDescription();

    public String getURI();

    public int getLineNumber();

    public int getColumnNumber();

    public List<String> getSnippet(int var1);
}


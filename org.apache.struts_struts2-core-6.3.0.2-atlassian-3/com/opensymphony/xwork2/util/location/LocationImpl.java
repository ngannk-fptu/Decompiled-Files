/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.commons.lang3.StringUtils
 */
package com.opensymphony.xwork2.util.location;

import com.opensymphony.xwork2.util.location.Location;
import com.opensymphony.xwork2.util.location.LocationUtils;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.lang3.StringUtils;

public class LocationImpl
implements Location,
Serializable {
    private final String uri;
    private final int line;
    private final int column;
    private final String description;
    static final LocationImpl UNKNOWN = new LocationImpl(null, null, -1, -1);

    public LocationImpl(String description, String uri) {
        this(description, uri, -1, -1);
    }

    public LocationImpl(String description, String uri, int line, int column) {
        if (StringUtils.isEmpty((CharSequence)uri)) {
            this.uri = null;
            this.line = -1;
            this.column = -1;
        } else {
            this.uri = uri;
            this.line = line;
            this.column = column;
        }
        this.description = StringUtils.trimToNull((String)description);
    }

    public LocationImpl(Location location) {
        this(location.getDescription(), location.getURI(), location.getLineNumber(), location.getColumnNumber());
    }

    public LocationImpl(String description, Location location) {
        this(description, location.getURI(), location.getLineNumber(), location.getColumnNumber());
    }

    public static LocationImpl get(Location location) {
        if (location instanceof LocationImpl) {
            return (LocationImpl)location;
        }
        if (location == null) {
            return UNKNOWN;
        }
        return new LocationImpl(location);
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public String getURI() {
        return this.uri;
    }

    @Override
    public int getLineNumber() {
        return this.line;
    }

    @Override
    public int getColumnNumber() {
        return this.column;
    }

    @Override
    public List<String> getSnippet(int padding) {
        ArrayList<String> snippet = new ArrayList<String>();
        if (this.getLineNumber() > 0) {
            try (InputStream in = new URL(this.getURI()).openStream();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(in));){
                String line;
                int lineno = 0;
                int errno = this.getLineNumber();
                while ((line = reader.readLine()) != null) {
                    if (++lineno < errno - padding || lineno > errno + padding) continue;
                    snippet.add(line);
                }
            }
            catch (Exception exception) {
                // empty catch block
            }
        }
        return snippet;
    }

    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }
        if (obj instanceof Location) {
            Location other = (Location)obj;
            return this.line == other.getLineNumber() && this.column == other.getColumnNumber() && this.testEquals(this.uri, other.getURI()) && this.testEquals(this.description, other.getDescription());
        }
        return false;
    }

    public int hashCode() {
        int hash = this.line ^ this.column;
        if (this.uri != null) {
            hash ^= this.uri.hashCode();
        }
        if (this.description != null) {
            hash ^= this.description.hashCode();
        }
        return hash;
    }

    public String toString() {
        return LocationUtils.toString(this);
    }

    private Object readResolve() {
        return this.equals(Location.UNKNOWN) ? Location.UNKNOWN : this;
    }

    private boolean testEquals(Object object1, Object object2) {
        if (object1 == object2) {
            return true;
        }
        if (object1 == null || object2 == null) {
            return false;
        }
        return object1.equals(object2);
    }
}


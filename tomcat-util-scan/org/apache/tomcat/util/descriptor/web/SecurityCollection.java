/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.apache.tomcat.util.buf.UDecoder
 */
package org.apache.tomcat.util.descriptor.web;

import java.io.Serializable;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import org.apache.tomcat.util.buf.UDecoder;
import org.apache.tomcat.util.descriptor.web.XmlEncodingBase;

public class SecurityCollection
extends XmlEncodingBase
implements Serializable {
    private static final long serialVersionUID = 1L;
    private String description = null;
    private String[] methods = new String[0];
    private String[] omittedMethods = new String[0];
    private String name = null;
    private String[] patterns = new String[0];
    private boolean isFromDescriptor = true;

    public SecurityCollection() {
        this(null, null);
    }

    public SecurityCollection(String name, String description) {
        this.setName(name);
        this.setDescription(description);
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isFromDescriptor() {
        return this.isFromDescriptor;
    }

    public void setFromDescriptor(boolean isFromDescriptor) {
        this.isFromDescriptor = isFromDescriptor;
    }

    public void addMethod(String method) {
        if (method == null) {
            return;
        }
        String[] results = Arrays.copyOf(this.methods, this.methods.length + 1);
        results[this.methods.length] = method;
        this.methods = results;
    }

    public void addOmittedMethod(String method) {
        if (method == null) {
            return;
        }
        String[] results = Arrays.copyOf(this.omittedMethods, this.omittedMethods.length + 1);
        results[this.omittedMethods.length] = method;
        this.omittedMethods = results;
    }

    public void addPattern(String pattern) {
        this.addPatternDecoded(UDecoder.URLDecode((String)pattern, (Charset)StandardCharsets.UTF_8));
    }

    public void addPatternDecoded(String pattern) {
        if (pattern == null) {
            return;
        }
        String decodedPattern = UDecoder.URLDecode((String)pattern, (Charset)this.getCharset());
        String[] results = Arrays.copyOf(this.patterns, this.patterns.length + 1);
        results[this.patterns.length] = decodedPattern;
        this.patterns = results;
    }

    public boolean findMethod(String method) {
        if (this.methods.length == 0 && this.omittedMethods.length == 0) {
            return true;
        }
        if (this.methods.length > 0) {
            for (String s : this.methods) {
                if (!s.equals(method)) continue;
                return true;
            }
            return false;
        }
        if (this.omittedMethods.length > 0) {
            for (String omittedMethod : this.omittedMethods) {
                if (!omittedMethod.equals(method)) continue;
                return false;
            }
        }
        return true;
    }

    public String[] findMethods() {
        return this.methods;
    }

    public String[] findOmittedMethods() {
        return this.omittedMethods;
    }

    public boolean findPattern(String pattern) {
        for (String s : this.patterns) {
            if (!s.equals(pattern)) continue;
            return true;
        }
        return false;
    }

    public String[] findPatterns() {
        return this.patterns;
    }

    public void removeMethod(String method) {
        if (method == null) {
            return;
        }
        int n = -1;
        for (int i = 0; i < this.methods.length; ++i) {
            if (!this.methods[i].equals(method)) continue;
            n = i;
            break;
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.methods.length - 1];
            for (int i = 0; i < this.methods.length; ++i) {
                if (i == n) continue;
                results[j++] = this.methods[i];
            }
            this.methods = results;
        }
    }

    public void removeOmittedMethod(String method) {
        if (method == null) {
            return;
        }
        int n = -1;
        for (int i = 0; i < this.omittedMethods.length; ++i) {
            if (!this.omittedMethods[i].equals(method)) continue;
            n = i;
            break;
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.omittedMethods.length - 1];
            for (int i = 0; i < this.omittedMethods.length; ++i) {
                if (i == n) continue;
                results[j++] = this.omittedMethods[i];
            }
            this.omittedMethods = results;
        }
    }

    public void removePattern(String pattern) {
        if (pattern == null) {
            return;
        }
        int n = -1;
        for (int i = 0; i < this.patterns.length; ++i) {
            if (!this.patterns[i].equals(pattern)) continue;
            n = i;
            break;
        }
        if (n >= 0) {
            int j = 0;
            String[] results = new String[this.patterns.length - 1];
            for (int i = 0; i < this.patterns.length; ++i) {
                if (i == n) continue;
                results[j++] = this.patterns[i];
            }
            this.patterns = results;
        }
    }

    public String toString() {
        StringBuilder sb = new StringBuilder("SecurityCollection[");
        sb.append(this.name);
        if (this.description != null) {
            sb.append(", ");
            sb.append(this.description);
        }
        sb.append(']');
        return sb.toString();
    }
}


/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.mozilla.javascript.GeneratedClassLoader
 */
package org.apache.batik.script.rhino;

import java.io.File;
import java.io.FilePermission;
import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.security.AccessControlContext;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.ProtectionDomain;
import java.security.cert.Certificate;
import org.mozilla.javascript.GeneratedClassLoader;

public class RhinoClassLoader
extends URLClassLoader
implements GeneratedClassLoader {
    protected URL documentURL;
    protected CodeSource codeSource;
    protected AccessControlContext rhinoAccessControlContext;

    public RhinoClassLoader(URL documentURL, ClassLoader parent) {
        URL[] uRLArray;
        if (documentURL != null) {
            URL[] uRLArray2 = new URL[1];
            uRLArray = uRLArray2;
            uRLArray2[0] = documentURL;
        } else {
            uRLArray = new URL[]{};
        }
        super(uRLArray, parent);
        this.documentURL = documentURL;
        if (documentURL != null) {
            this.codeSource = new CodeSource(documentURL, (Certificate[])null);
        }
        ProtectionDomain rhinoProtectionDomain = new ProtectionDomain(this.codeSource, this.getPermissions(this.codeSource));
        this.rhinoAccessControlContext = new AccessControlContext(new ProtectionDomain[]{rhinoProtectionDomain});
    }

    static URL[] getURL(ClassLoader parent) {
        if (parent instanceof RhinoClassLoader) {
            URL documentURL = ((RhinoClassLoader)parent).documentURL;
            if (documentURL != null) {
                return new URL[]{documentURL};
            }
            return new URL[0];
        }
        return new URL[0];
    }

    public Class defineClass(String name, byte[] data) {
        return super.defineClass(name, data, 0, data.length, this.codeSource);
    }

    public void linkClass(Class clazz) {
        super.resolveClass(clazz);
    }

    public AccessControlContext getAccessControlContext() {
        return this.rhinoAccessControlContext;
    }

    @Override
    protected PermissionCollection getPermissions(CodeSource codesource) {
        PermissionCollection perms = null;
        if (codesource != null) {
            perms = super.getPermissions(codesource);
        }
        if (this.documentURL != null && perms != null) {
            int dirEnd;
            String path;
            Permission p = null;
            FilePermission dirPerm = null;
            try {
                p = this.documentURL.openConnection().getPermission();
            }
            catch (IOException e) {
                p = null;
            }
            if (p instanceof FilePermission && !(path = p.getName()).endsWith(File.separator) && (dirEnd = path.lastIndexOf(File.separator)) != -1) {
                path = path.substring(0, dirEnd + 1);
                path = path + "-";
                dirPerm = new FilePermission(path, "read");
                perms.add(dirPerm);
            }
        }
        return perms;
    }
}


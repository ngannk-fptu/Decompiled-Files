/*
 * Decompiled with CFR 0.152.
 */
package org.jfree.base;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import org.jfree.base.Library;
import org.jfree.util.ObjectUtilities;

public class BasicProjectInfo
extends Library {
    private String copyright;
    private List libraries = new ArrayList();
    private List optionalLibraries = new ArrayList();

    public BasicProjectInfo() {
    }

    public BasicProjectInfo(String name, String version, String licence, String info) {
        this();
        this.setName(name);
        this.setVersion(version);
        this.setLicenceName(licence);
        this.setInfo(info);
    }

    public BasicProjectInfo(String name, String version, String info, String copyright, String licenceName) {
        this(name, version, licenceName, info);
        this.setCopyright(copyright);
    }

    public String getCopyright() {
        return this.copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public void setInfo(String info) {
        super.setInfo(info);
    }

    public void setLicenceName(String licence) {
        super.setLicenceName(licence);
    }

    public void setName(String name) {
        super.setName(name);
    }

    public void setVersion(String version) {
        super.setVersion(version);
    }

    public Library[] getLibraries() {
        return this.libraries.toArray(new Library[this.libraries.size()]);
    }

    public void addLibrary(Library library) {
        if (library == null) {
            throw new NullPointerException();
        }
        this.libraries.add(library);
    }

    public Library[] getOptionalLibraries() {
        ArrayList<Library> libraries = new ArrayList<Library>();
        for (int i = 0; i < this.optionalLibraries.size(); ++i) {
            OptionalLibraryHolder holder = (OptionalLibraryHolder)this.optionalLibraries.get(i);
            Library l = holder.getLibrary();
            if (l == null) continue;
            libraries.add(l);
        }
        return libraries.toArray(new Library[libraries.size()]);
    }

    public void addOptionalLibrary(String libraryClass) {
        if (libraryClass == null) {
            throw new NullPointerException("Library classname must be given.");
        }
        this.optionalLibraries.add(new OptionalLibraryHolder(libraryClass));
    }

    public void addOptionalLibrary(Library library) {
        if (library == null) {
            throw new NullPointerException("Library must be given.");
        }
        this.optionalLibraries.add(new OptionalLibraryHolder(library));
    }

    private static class OptionalLibraryHolder {
        private String libraryClass;
        private transient Library library;

        public OptionalLibraryHolder(String libraryClass) {
            if (libraryClass == null) {
                throw new NullPointerException("LibraryClass must not be null.");
            }
            this.libraryClass = libraryClass;
        }

        public OptionalLibraryHolder(Library library) {
            if (library == null) {
                throw new NullPointerException("Library must not be null.");
            }
            this.library = library;
            this.libraryClass = library.getClass().getName();
        }

        public String getLibraryClass() {
            return this.libraryClass;
        }

        public Library getLibrary() {
            if (this.library == null) {
                this.library = this.loadLibrary(this.libraryClass);
            }
            return this.library;
        }

        protected Library loadLibrary(String classname) {
            if (classname == null) {
                return null;
            }
            try {
                Class<?> c = ObjectUtilities.getClassLoader(this.getClass()).loadClass(classname);
                try {
                    Method m = c.getMethod("getInstance", null);
                    return (Library)m.invoke(null, (Object[])null);
                }
                catch (Exception exception) {
                    return (Library)c.newInstance();
                }
            }
            catch (Exception e) {
                return null;
            }
        }
    }
}


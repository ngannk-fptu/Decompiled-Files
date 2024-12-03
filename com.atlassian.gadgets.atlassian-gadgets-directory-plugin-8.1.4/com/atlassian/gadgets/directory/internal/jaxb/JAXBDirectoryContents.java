/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.gadgets.GadgetRequestContext
 *  com.atlassian.gadgets.directory.Category
 *  com.atlassian.gadgets.directory.Directory
 *  com.atlassian.gadgets.directory.Directory$Entry
 *  com.google.common.base.Function
 *  com.google.common.collect.ImmutableList
 *  com.google.common.collect.Iterables
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 */
package com.atlassian.gadgets.directory.internal.jaxb;

import com.atlassian.gadgets.GadgetRequestContext;
import com.atlassian.gadgets.directory.Category;
import com.atlassian.gadgets.directory.Directory;
import com.atlassian.gadgets.directory.internal.jaxb.JAXBCategory;
import com.atlassian.gadgets.directory.internal.jaxb.JAXBDirectoryEntry;
import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import java.util.Collection;
import java.util.EnumSet;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class JAXBDirectoryContents {
    @XmlElement
    private final Collection<JAXBCategory> categories;
    @XmlElement(name="gadgets")
    private final Collection<JAXBDirectoryEntry> entries;

    private JAXBDirectoryContents() {
        this.categories = null;
        this.entries = null;
    }

    private JAXBDirectoryContents(Iterable<JAXBCategory> categories, Iterable<JAXBDirectoryEntry> entries) {
        this.categories = ImmutableList.copyOf(categories);
        this.entries = ImmutableList.copyOf(entries);
    }

    public static JAXBDirectoryContents getDirectoryContents(Directory directory, GadgetRequestContext gadgetRequestContext) {
        Iterable categories = Iterables.transform(EnumSet.allOf(Category.class), (Function)CategoryToJAXBCategory.FUNCTION);
        Iterable entries = Iterables.transform((Iterable)directory.getEntries(gadgetRequestContext), (Function)DirectoryEntryToJAXBDirectoryEntry.FUNCTION);
        return new JAXBDirectoryContents(categories, entries);
    }

    private static enum DirectoryEntryToJAXBDirectoryEntry implements Function<Directory.Entry, JAXBDirectoryEntry>
    {
        FUNCTION;


        public JAXBDirectoryEntry apply(Directory.Entry entry) {
            return new JAXBDirectoryEntry(entry);
        }
    }

    private static enum CategoryToJAXBCategory implements Function<Category, JAXBCategory>
    {
        FUNCTION;


        public JAXBCategory apply(Category category) {
            return new JAXBCategory(category);
        }
    }
}


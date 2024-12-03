/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  aQute.bnd.osgi.AbstractResource
 *  aQute.bnd.osgi.ClassDataCollector
 *  aQute.bnd.osgi.Clazz
 *  aQute.bnd.osgi.Descriptors$TypeRef
 *  com.google.common.collect.Collections2
 *  com.google.common.collect.ImmutableSet$Builder
 *  com.google.common.collect.Sets
 *  org.apache.commons.io.IOUtils
 */
package com.atlassian.plugin.osgi.util;

import aQute.bnd.osgi.AbstractResource;
import aQute.bnd.osgi.ClassDataCollector;
import aQute.bnd.osgi.Clazz;
import aQute.bnd.osgi.Descriptors;
import com.google.common.collect.Collections2;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import org.apache.commons.io.IOUtils;

public class ClassBinaryScanner {
    public static ScanResult scanClassBinary(Clazz clazz) throws IOException {
        final ImmutableSet.Builder allReferredClasses = new ImmutableSet.Builder();
        final String[] superClassName = new String[]{null};
        try {
            clazz.parseClassFileWithCollector(new ClassDataCollector(){

                public void extendsClass(Descriptors.TypeRef ref) {
                    superClassName[0] = ref.getBinary();
                    allReferredClasses.add((Object)ref.getBinary());
                }

                public void implementsInterfaces(Descriptors.TypeRef[] refs) {
                    for (Descriptors.TypeRef ref : refs) {
                        allReferredClasses.add((Object)ref.getBinary());
                    }
                }

                public void addReference(Descriptors.TypeRef ref) {
                    allReferredClasses.add((Object)ref.getBinary());
                }
            });
        }
        catch (Exception e) {
            throw new IOException("Error parsing class file", e);
        }
        HashSet referredPackages = Sets.newHashSet((Iterable)Collections2.transform((Collection)clazz.getReferred(), packageRef -> {
            if (packageRef != null) {
                return packageRef.getFQN();
            }
            return null;
        }));
        return new ScanResult((Set<String>)allReferredClasses.build(), referredPackages, superClassName[0]);
    }

    public static class InputStreamResource
    extends AbstractResource
    implements Closeable {
        private InputStream inputStream;

        public InputStreamResource(InputStream inputStream) {
            super(-1L);
            this.inputStream = inputStream;
        }

        protected byte[] getBytes() throws Exception {
            return IOUtils.toByteArray((InputStream)this.inputStream);
        }

        @Override
        public void close() throws IOException {
            this.inputStream.close();
        }
    }

    public static class ScanResult {
        private Set<String> referredClasses;
        private Set<String> referredPackages;
        private String superClass;

        public ScanResult(Set<String> referredClasses, Set<String> referredPackages, String superClass) {
            this.referredClasses = referredClasses;
            this.referredPackages = referredPackages;
            this.superClass = superClass;
        }

        public Set<String> getReferredClasses() {
            return this.referredClasses;
        }

        public Set<String> getReferredPackages() {
            return this.referredPackages;
        }

        public String getSuperClass() {
            return this.superClass;
        }
    }
}


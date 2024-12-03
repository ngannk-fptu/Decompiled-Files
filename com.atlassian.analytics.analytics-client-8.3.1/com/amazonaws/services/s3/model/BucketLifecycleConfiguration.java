/*
 * Decompiled with CFR 0.152.
 */
package com.amazonaws.services.s3.model;

import com.amazonaws.services.s3.model.AbortIncompleteMultipartUpload;
import com.amazonaws.services.s3.model.StorageClass;
import com.amazonaws.services.s3.model.lifecycle.LifecycleFilter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

public class BucketLifecycleConfiguration
implements Serializable {
    public static final String ENABLED = "Enabled";
    public static final String DISABLED = "Disabled";
    private List<Rule> rules;

    public List<Rule> getRules() {
        return this.rules;
    }

    public void setRules(List<Rule> rules) {
        this.rules = rules;
    }

    public BucketLifecycleConfiguration withRules(List<Rule> rules) {
        this.setRules(rules);
        return this;
    }

    public BucketLifecycleConfiguration withRules(Rule ... rules) {
        this.setRules(Arrays.asList(rules));
        return this;
    }

    public BucketLifecycleConfiguration(List<Rule> rules) {
        this.rules = rules;
    }

    public BucketLifecycleConfiguration() {
    }

    public static class NoncurrentVersionExpiration
    implements Serializable {
        private int days = -1;
        private int newerNoncurrentVersions = -1;

        public void setDays(int days) {
            this.days = days;
        }

        public int getDays() {
            return this.days;
        }

        public NoncurrentVersionExpiration withDays(int noncurrentDays) {
            this.days = noncurrentDays;
            return this;
        }

        public void setNewerNoncurrentVersions(int newerNoncurrentVersions) {
            this.newerNoncurrentVersions = newerNoncurrentVersions;
        }

        public int getNewerNoncurrentVersions() {
            return this.newerNoncurrentVersions;
        }

        public NoncurrentVersionExpiration withNewerNoncurrentVersions(int newerNoncurrentVersions) {
            this.newerNoncurrentVersions = newerNoncurrentVersions;
            return this;
        }
    }

    public static class NoncurrentVersionTransition
    implements Serializable {
        private int days = -1;
        private String storageClass;
        private int newerNoncurrentVersions = -1;

        public void setDays(int expirationInDays) {
            this.days = expirationInDays;
        }

        public int getDays() {
            return this.days;
        }

        public NoncurrentVersionTransition withDays(int expirationInDays) {
            this.days = expirationInDays;
            return this;
        }

        public void setStorageClass(StorageClass storageClass) {
            if (storageClass == null) {
                this.setStorageClass((String)null);
            } else {
                this.setStorageClass(storageClass.toString());
            }
        }

        public void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        @Deprecated
        public StorageClass getStorageClass() {
            try {
                return StorageClass.fromValue(this.storageClass);
            }
            catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        public String getStorageClassAsString() {
            return this.storageClass;
        }

        public NoncurrentVersionTransition withStorageClass(StorageClass storageClass) {
            this.setStorageClass(storageClass);
            return this;
        }

        public NoncurrentVersionTransition withStorageClass(String storageClass) {
            this.setStorageClass(storageClass);
            return this;
        }

        public void setNewerNoncurrentVersions(int newerNoncurrentVersions) {
            this.newerNoncurrentVersions = newerNoncurrentVersions;
        }

        public int getNewerNoncurrentVersions() {
            return this.newerNoncurrentVersions;
        }

        public NoncurrentVersionTransition withNewerNoncurrentVersions(int newerNoncurrentVersions) {
            this.newerNoncurrentVersions = newerNoncurrentVersions;
            return this;
        }
    }

    public static class Transition
    implements Serializable {
        private int days = -1;
        private Date date;
        private String storageClass;

        public void setDays(int expirationInDays) {
            this.days = expirationInDays;
        }

        public int getDays() {
            return this.days;
        }

        public Transition withDays(int expirationInDays) {
            this.days = expirationInDays;
            return this;
        }

        public void setStorageClass(StorageClass storageClass) {
            if (storageClass == null) {
                this.setStorageClass((String)null);
            } else {
                this.setStorageClass(storageClass.toString());
            }
        }

        public void setStorageClass(String storageClass) {
            this.storageClass = storageClass;
        }

        @Deprecated
        public StorageClass getStorageClass() {
            try {
                return StorageClass.fromValue(this.storageClass);
            }
            catch (IllegalArgumentException ignored) {
                return null;
            }
        }

        public String getStorageClassAsString() {
            return this.storageClass;
        }

        public Transition withStorageClass(StorageClass storageClass) {
            this.setStorageClass(storageClass);
            return this;
        }

        public Transition withStorageClass(String storageClass) {
            this.setStorageClass(storageClass);
            return this;
        }

        public void setDate(Date expirationDate) {
            this.date = expirationDate;
        }

        public Date getDate() {
            return this.date;
        }

        public Transition withDate(Date expirationDate) {
            this.date = expirationDate;
            return this;
        }
    }

    public static class Rule
    implements Serializable {
        private String id;
        private String prefix;
        private String status;
        private LifecycleFilter filter;
        private int expirationInDays = -1;
        private boolean expiredObjectDeleteMarker = false;
        private Date expirationDate;
        private List<Transition> transitions;
        private List<NoncurrentVersionTransition> noncurrentVersionTransitions;
        private NoncurrentVersionExpiration noncurrentVersionExpiration;
        private AbortIncompleteMultipartUpload abortIncompleteMultipartUpload;

        public void setId(String id) {
            this.id = id;
        }

        @Deprecated
        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }

        public void setExpirationInDays(int expirationInDays) {
            this.expirationInDays = expirationInDays;
        }

        @Deprecated
        public void setNoncurrentVersionExpirationInDays(int value) {
            NoncurrentVersionExpiration ncve = this.noncurrentVersionExpiration;
            if (ncve != null) {
                ncve.setDays(value);
            } else {
                this.noncurrentVersionExpiration = new NoncurrentVersionExpiration().withDays(value);
            }
        }

        public String getId() {
            return this.id;
        }

        public Rule withId(String id) {
            this.id = id;
            return this;
        }

        @Deprecated
        public String getPrefix() {
            return this.prefix;
        }

        @Deprecated
        public Rule withPrefix(String prefix) {
            this.prefix = prefix;
            return this;
        }

        public int getExpirationInDays() {
            return this.expirationInDays;
        }

        public Rule withExpirationInDays(int expirationInDays) {
            this.expirationInDays = expirationInDays;
            return this;
        }

        @Deprecated
        public int getNoncurrentVersionExpirationInDays() {
            NoncurrentVersionExpiration ncve = this.noncurrentVersionExpiration;
            return ncve != null ? ncve.getDays() : -1;
        }

        @Deprecated
        public Rule withNoncurrentVersionExpirationInDays(int value) {
            this.setNoncurrentVersionExpirationInDays(value);
            return this;
        }

        public String getStatus() {
            return this.status;
        }

        public void setStatus(String status) {
            this.status = status;
        }

        public Rule withStatus(String status) {
            this.setStatus(status);
            return this;
        }

        public void setExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
        }

        public Date getExpirationDate() {
            return this.expirationDate;
        }

        public Rule withExpirationDate(Date expirationDate) {
            this.expirationDate = expirationDate;
            return this;
        }

        @Deprecated
        public void setTransition(Transition transition) {
            this.setTransitions(Arrays.asList(transition));
        }

        @Deprecated
        public Transition getTransition() {
            List<Transition> transitions = this.getTransitions();
            return transitions != null && !transitions.isEmpty() ? transitions.get(transitions.size() - 1) : null;
        }

        @Deprecated
        public Rule withTransition(Transition transition) {
            this.setTransitions(Arrays.asList(transition));
            return this;
        }

        @Deprecated
        public void setNoncurrentVersionTransition(NoncurrentVersionTransition nonCurrentVersionTransition) {
            this.setNoncurrentVersionTransitions(Arrays.asList(nonCurrentVersionTransition));
        }

        @Deprecated
        public NoncurrentVersionTransition getNoncurrentVersionTransition() {
            List<NoncurrentVersionTransition> transitions = this.getNoncurrentVersionTransitions();
            return transitions != null && !transitions.isEmpty() ? transitions.get(transitions.size() - 1) : null;
        }

        @Deprecated
        public Rule withNoncurrentVersionTransition(NoncurrentVersionTransition nonCurrentVersionTransition) {
            this.setNoncurrentVersionTransitions(Arrays.asList(nonCurrentVersionTransition));
            return this;
        }

        public List<Transition> getTransitions() {
            return this.transitions;
        }

        public void setTransitions(List<Transition> transitions) {
            this.transitions = new ArrayList<Transition>(transitions);
        }

        public Rule withTransitions(List<Transition> transitions) {
            this.setTransitions(transitions);
            return this;
        }

        public Rule addTransition(Transition transition) {
            if (transition == null) {
                throw new IllegalArgumentException("Transition cannot be null.");
            }
            if (this.transitions == null) {
                this.transitions = new ArrayList<Transition>();
            }
            this.transitions.add(transition);
            return this;
        }

        public List<NoncurrentVersionTransition> getNoncurrentVersionTransitions() {
            return this.noncurrentVersionTransitions;
        }

        public void setNoncurrentVersionTransitions(List<NoncurrentVersionTransition> noncurrentVersionTransitions) {
            this.noncurrentVersionTransitions = new ArrayList<NoncurrentVersionTransition>(noncurrentVersionTransitions);
        }

        public Rule withNoncurrentVersionTransitions(List<NoncurrentVersionTransition> noncurrentVersionTransitions) {
            this.setNoncurrentVersionTransitions(noncurrentVersionTransitions);
            return this;
        }

        public Rule addNoncurrentVersionTransition(NoncurrentVersionTransition noncurrentVersionTransition) {
            if (noncurrentVersionTransition == null) {
                throw new IllegalArgumentException("NoncurrentVersionTransition cannot be null.");
            }
            if (this.noncurrentVersionTransitions == null) {
                this.noncurrentVersionTransitions = new ArrayList<NoncurrentVersionTransition>();
            }
            this.noncurrentVersionTransitions.add(noncurrentVersionTransition);
            return this;
        }

        public NoncurrentVersionExpiration getNoncurrentVersionExpiration() {
            return this.noncurrentVersionExpiration;
        }

        public void setNoncurrentVersionExpiration(NoncurrentVersionExpiration noncurrentVersionExpiration) {
            this.noncurrentVersionExpiration = noncurrentVersionExpiration;
        }

        public Rule withNoncurrentVersionExpiration(NoncurrentVersionExpiration noncurrentVersionExpiration) {
            this.setNoncurrentVersionExpiration(noncurrentVersionExpiration);
            return this;
        }

        public AbortIncompleteMultipartUpload getAbortIncompleteMultipartUpload() {
            return this.abortIncompleteMultipartUpload;
        }

        public void setAbortIncompleteMultipartUpload(AbortIncompleteMultipartUpload abortIncompleteMultipartUpload) {
            this.abortIncompleteMultipartUpload = abortIncompleteMultipartUpload;
        }

        public Rule withAbortIncompleteMultipartUpload(AbortIncompleteMultipartUpload abortIncompleteMultipartUpload) {
            this.setAbortIncompleteMultipartUpload(abortIncompleteMultipartUpload);
            return this;
        }

        public boolean isExpiredObjectDeleteMarker() {
            return this.expiredObjectDeleteMarker;
        }

        public void setExpiredObjectDeleteMarker(boolean expiredObjectDeleteMarker) {
            this.expiredObjectDeleteMarker = expiredObjectDeleteMarker;
        }

        public Rule withExpiredObjectDeleteMarker(boolean expiredObjectDeleteMarker) {
            this.expiredObjectDeleteMarker = expiredObjectDeleteMarker;
            return this;
        }

        public LifecycleFilter getFilter() {
            return this.filter;
        }

        public void setFilter(LifecycleFilter filter) {
            this.filter = filter;
        }

        public Rule withFilter(LifecycleFilter filter) {
            this.setFilter(filter);
            return this;
        }
    }
}


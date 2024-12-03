/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  javax.xml.bind.annotation.XmlAccessType
 *  javax.xml.bind.annotation.XmlAccessorType
 *  javax.xml.bind.annotation.XmlElement
 *  javax.xml.bind.annotation.XmlRootElement
 *  org.codehaus.jackson.annotate.JsonIgnoreProperties
 */
package com.atlassian.pats.rest;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

@XmlRootElement
@XmlAccessorType(value=XmlAccessType.FIELD)
@JsonIgnoreProperties(ignoreUnknown=true)
public class RestTokenSearchRequest {
    @XmlElement
    private List<String> userKeys;
    @XmlElement
    private String name;
    @XmlElement
    private String tokenNameFilterExpression;
    @XmlElement
    private String tokenCreatedDateFrom;
    @XmlElement
    private String tokenCreatedDateTo;
    @XmlElement
    private String tokenExpiryDateFrom;
    @XmlElement
    private String tokenExpiryDateTo;
    @XmlElement
    private String lastAuthenticatedDateFrom;
    @XmlElement
    private String lastAuthenticatedDateTo;
    @XmlElement
    private Boolean isUsed;
    @XmlElement
    private String sortBy;
    @XmlElement
    private String orderBy;
    @XmlElement
    private int page;
    @XmlElement
    private int limit;

    private static String $default$sortBy() {
        return "name";
    }

    private static String $default$orderBy() {
        return "asc";
    }

    private static int $default$page() {
        return 0;
    }

    private static int $default$limit() {
        return 100;
    }

    public static RestTokenSearchRequestBuilder builder() {
        return new RestTokenSearchRequestBuilder();
    }

    public List<String> getUserKeys() {
        return this.userKeys;
    }

    public String getName() {
        return this.name;
    }

    public String getTokenNameFilterExpression() {
        return this.tokenNameFilterExpression;
    }

    public String getTokenCreatedDateFrom() {
        return this.tokenCreatedDateFrom;
    }

    public String getTokenCreatedDateTo() {
        return this.tokenCreatedDateTo;
    }

    public String getTokenExpiryDateFrom() {
        return this.tokenExpiryDateFrom;
    }

    public String getTokenExpiryDateTo() {
        return this.tokenExpiryDateTo;
    }

    public String getLastAuthenticatedDateFrom() {
        return this.lastAuthenticatedDateFrom;
    }

    public String getLastAuthenticatedDateTo() {
        return this.lastAuthenticatedDateTo;
    }

    public Boolean getIsUsed() {
        return this.isUsed;
    }

    public String getSortBy() {
        return this.sortBy;
    }

    public String getOrderBy() {
        return this.orderBy;
    }

    public int getPage() {
        return this.page;
    }

    public int getLimit() {
        return this.limit;
    }

    public void setUserKeys(List<String> userKeys) {
        this.userKeys = userKeys;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setTokenNameFilterExpression(String tokenNameFilterExpression) {
        this.tokenNameFilterExpression = tokenNameFilterExpression;
    }

    public void setTokenCreatedDateFrom(String tokenCreatedDateFrom) {
        this.tokenCreatedDateFrom = tokenCreatedDateFrom;
    }

    public void setTokenCreatedDateTo(String tokenCreatedDateTo) {
        this.tokenCreatedDateTo = tokenCreatedDateTo;
    }

    public void setTokenExpiryDateFrom(String tokenExpiryDateFrom) {
        this.tokenExpiryDateFrom = tokenExpiryDateFrom;
    }

    public void setTokenExpiryDateTo(String tokenExpiryDateTo) {
        this.tokenExpiryDateTo = tokenExpiryDateTo;
    }

    public void setLastAuthenticatedDateFrom(String lastAuthenticatedDateFrom) {
        this.lastAuthenticatedDateFrom = lastAuthenticatedDateFrom;
    }

    public void setLastAuthenticatedDateTo(String lastAuthenticatedDateTo) {
        this.lastAuthenticatedDateTo = lastAuthenticatedDateTo;
    }

    public void setIsUsed(Boolean isUsed) {
        this.isUsed = isUsed;
    }

    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }

    public void setOrderBy(String orderBy) {
        this.orderBy = orderBy;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RestTokenSearchRequest)) {
            return false;
        }
        RestTokenSearchRequest other = (RestTokenSearchRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        if (this.getPage() != other.getPage()) {
            return false;
        }
        if (this.getLimit() != other.getLimit()) {
            return false;
        }
        Boolean this$isUsed = this.getIsUsed();
        Boolean other$isUsed = other.getIsUsed();
        if (this$isUsed == null ? other$isUsed != null : !((Object)this$isUsed).equals(other$isUsed)) {
            return false;
        }
        List<String> this$userKeys = this.getUserKeys();
        List<String> other$userKeys = other.getUserKeys();
        if (this$userKeys == null ? other$userKeys != null : !((Object)this$userKeys).equals(other$userKeys)) {
            return false;
        }
        String this$name = this.getName();
        String other$name = other.getName();
        if (this$name == null ? other$name != null : !this$name.equals(other$name)) {
            return false;
        }
        String this$tokenNameFilterExpression = this.getTokenNameFilterExpression();
        String other$tokenNameFilterExpression = other.getTokenNameFilterExpression();
        if (this$tokenNameFilterExpression == null ? other$tokenNameFilterExpression != null : !this$tokenNameFilterExpression.equals(other$tokenNameFilterExpression)) {
            return false;
        }
        String this$tokenCreatedDateFrom = this.getTokenCreatedDateFrom();
        String other$tokenCreatedDateFrom = other.getTokenCreatedDateFrom();
        if (this$tokenCreatedDateFrom == null ? other$tokenCreatedDateFrom != null : !this$tokenCreatedDateFrom.equals(other$tokenCreatedDateFrom)) {
            return false;
        }
        String this$tokenCreatedDateTo = this.getTokenCreatedDateTo();
        String other$tokenCreatedDateTo = other.getTokenCreatedDateTo();
        if (this$tokenCreatedDateTo == null ? other$tokenCreatedDateTo != null : !this$tokenCreatedDateTo.equals(other$tokenCreatedDateTo)) {
            return false;
        }
        String this$tokenExpiryDateFrom = this.getTokenExpiryDateFrom();
        String other$tokenExpiryDateFrom = other.getTokenExpiryDateFrom();
        if (this$tokenExpiryDateFrom == null ? other$tokenExpiryDateFrom != null : !this$tokenExpiryDateFrom.equals(other$tokenExpiryDateFrom)) {
            return false;
        }
        String this$tokenExpiryDateTo = this.getTokenExpiryDateTo();
        String other$tokenExpiryDateTo = other.getTokenExpiryDateTo();
        if (this$tokenExpiryDateTo == null ? other$tokenExpiryDateTo != null : !this$tokenExpiryDateTo.equals(other$tokenExpiryDateTo)) {
            return false;
        }
        String this$lastAuthenticatedDateFrom = this.getLastAuthenticatedDateFrom();
        String other$lastAuthenticatedDateFrom = other.getLastAuthenticatedDateFrom();
        if (this$lastAuthenticatedDateFrom == null ? other$lastAuthenticatedDateFrom != null : !this$lastAuthenticatedDateFrom.equals(other$lastAuthenticatedDateFrom)) {
            return false;
        }
        String this$lastAuthenticatedDateTo = this.getLastAuthenticatedDateTo();
        String other$lastAuthenticatedDateTo = other.getLastAuthenticatedDateTo();
        if (this$lastAuthenticatedDateTo == null ? other$lastAuthenticatedDateTo != null : !this$lastAuthenticatedDateTo.equals(other$lastAuthenticatedDateTo)) {
            return false;
        }
        String this$sortBy = this.getSortBy();
        String other$sortBy = other.getSortBy();
        if (this$sortBy == null ? other$sortBy != null : !this$sortBy.equals(other$sortBy)) {
            return false;
        }
        String this$orderBy = this.getOrderBy();
        String other$orderBy = other.getOrderBy();
        return !(this$orderBy == null ? other$orderBy != null : !this$orderBy.equals(other$orderBy));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RestTokenSearchRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        result = result * 59 + this.getPage();
        result = result * 59 + this.getLimit();
        Boolean $isUsed = this.getIsUsed();
        result = result * 59 + ($isUsed == null ? 43 : ((Object)$isUsed).hashCode());
        List<String> $userKeys = this.getUserKeys();
        result = result * 59 + ($userKeys == null ? 43 : ((Object)$userKeys).hashCode());
        String $name = this.getName();
        result = result * 59 + ($name == null ? 43 : $name.hashCode());
        String $tokenNameFilterExpression = this.getTokenNameFilterExpression();
        result = result * 59 + ($tokenNameFilterExpression == null ? 43 : $tokenNameFilterExpression.hashCode());
        String $tokenCreatedDateFrom = this.getTokenCreatedDateFrom();
        result = result * 59 + ($tokenCreatedDateFrom == null ? 43 : $tokenCreatedDateFrom.hashCode());
        String $tokenCreatedDateTo = this.getTokenCreatedDateTo();
        result = result * 59 + ($tokenCreatedDateTo == null ? 43 : $tokenCreatedDateTo.hashCode());
        String $tokenExpiryDateFrom = this.getTokenExpiryDateFrom();
        result = result * 59 + ($tokenExpiryDateFrom == null ? 43 : $tokenExpiryDateFrom.hashCode());
        String $tokenExpiryDateTo = this.getTokenExpiryDateTo();
        result = result * 59 + ($tokenExpiryDateTo == null ? 43 : $tokenExpiryDateTo.hashCode());
        String $lastAuthenticatedDateFrom = this.getLastAuthenticatedDateFrom();
        result = result * 59 + ($lastAuthenticatedDateFrom == null ? 43 : $lastAuthenticatedDateFrom.hashCode());
        String $lastAuthenticatedDateTo = this.getLastAuthenticatedDateTo();
        result = result * 59 + ($lastAuthenticatedDateTo == null ? 43 : $lastAuthenticatedDateTo.hashCode());
        String $sortBy = this.getSortBy();
        result = result * 59 + ($sortBy == null ? 43 : $sortBy.hashCode());
        String $orderBy = this.getOrderBy();
        result = result * 59 + ($orderBy == null ? 43 : $orderBy.hashCode());
        return result;
    }

    public String toString() {
        return "RestTokenSearchRequest(userKeys=" + this.getUserKeys() + ", name=" + this.getName() + ", tokenNameFilterExpression=" + this.getTokenNameFilterExpression() + ", tokenCreatedDateFrom=" + this.getTokenCreatedDateFrom() + ", tokenCreatedDateTo=" + this.getTokenCreatedDateTo() + ", tokenExpiryDateFrom=" + this.getTokenExpiryDateFrom() + ", tokenExpiryDateTo=" + this.getTokenExpiryDateTo() + ", lastAuthenticatedDateFrom=" + this.getLastAuthenticatedDateFrom() + ", lastAuthenticatedDateTo=" + this.getLastAuthenticatedDateTo() + ", isUsed=" + this.getIsUsed() + ", sortBy=" + this.getSortBy() + ", orderBy=" + this.getOrderBy() + ", page=" + this.getPage() + ", limit=" + this.getLimit() + ")";
    }

    public RestTokenSearchRequest() {
        this.sortBy = RestTokenSearchRequest.$default$sortBy();
        this.orderBy = RestTokenSearchRequest.$default$orderBy();
        this.page = RestTokenSearchRequest.$default$page();
        this.limit = RestTokenSearchRequest.$default$limit();
    }

    public RestTokenSearchRequest(List<String> userKeys, String name, String tokenNameFilterExpression, String tokenCreatedDateFrom, String tokenCreatedDateTo, String tokenExpiryDateFrom, String tokenExpiryDateTo, String lastAuthenticatedDateFrom, String lastAuthenticatedDateTo, Boolean isUsed, String sortBy, String orderBy, int page, int limit) {
        this.userKeys = userKeys;
        this.name = name;
        this.tokenNameFilterExpression = tokenNameFilterExpression;
        this.tokenCreatedDateFrom = tokenCreatedDateFrom;
        this.tokenCreatedDateTo = tokenCreatedDateTo;
        this.tokenExpiryDateFrom = tokenExpiryDateFrom;
        this.tokenExpiryDateTo = tokenExpiryDateTo;
        this.lastAuthenticatedDateFrom = lastAuthenticatedDateFrom;
        this.lastAuthenticatedDateTo = lastAuthenticatedDateTo;
        this.isUsed = isUsed;
        this.sortBy = sortBy;
        this.orderBy = orderBy;
        this.page = page;
        this.limit = limit;
    }

    public static class RestTokenSearchRequestBuilder {
        private List<String> userKeys;
        private String name;
        private String tokenNameFilterExpression;
        private String tokenCreatedDateFrom;
        private String tokenCreatedDateTo;
        private String tokenExpiryDateFrom;
        private String tokenExpiryDateTo;
        private String lastAuthenticatedDateFrom;
        private String lastAuthenticatedDateTo;
        private Boolean isUsed;
        private boolean sortBy$set;
        private String sortBy$value;
        private boolean orderBy$set;
        private String orderBy$value;
        private boolean page$set;
        private int page$value;
        private boolean limit$set;
        private int limit$value;

        RestTokenSearchRequestBuilder() {
        }

        public RestTokenSearchRequestBuilder userKeys(List<String> userKeys) {
            this.userKeys = userKeys;
            return this;
        }

        public RestTokenSearchRequestBuilder name(String name) {
            this.name = name;
            return this;
        }

        public RestTokenSearchRequestBuilder tokenNameFilterExpression(String tokenNameFilterExpression) {
            this.tokenNameFilterExpression = tokenNameFilterExpression;
            return this;
        }

        public RestTokenSearchRequestBuilder tokenCreatedDateFrom(String tokenCreatedDateFrom) {
            this.tokenCreatedDateFrom = tokenCreatedDateFrom;
            return this;
        }

        public RestTokenSearchRequestBuilder tokenCreatedDateTo(String tokenCreatedDateTo) {
            this.tokenCreatedDateTo = tokenCreatedDateTo;
            return this;
        }

        public RestTokenSearchRequestBuilder tokenExpiryDateFrom(String tokenExpiryDateFrom) {
            this.tokenExpiryDateFrom = tokenExpiryDateFrom;
            return this;
        }

        public RestTokenSearchRequestBuilder tokenExpiryDateTo(String tokenExpiryDateTo) {
            this.tokenExpiryDateTo = tokenExpiryDateTo;
            return this;
        }

        public RestTokenSearchRequestBuilder lastAuthenticatedDateFrom(String lastAuthenticatedDateFrom) {
            this.lastAuthenticatedDateFrom = lastAuthenticatedDateFrom;
            return this;
        }

        public RestTokenSearchRequestBuilder lastAuthenticatedDateTo(String lastAuthenticatedDateTo) {
            this.lastAuthenticatedDateTo = lastAuthenticatedDateTo;
            return this;
        }

        public RestTokenSearchRequestBuilder isUsed(Boolean isUsed) {
            this.isUsed = isUsed;
            return this;
        }

        public RestTokenSearchRequestBuilder sortBy(String sortBy) {
            this.sortBy$value = sortBy;
            this.sortBy$set = true;
            return this;
        }

        public RestTokenSearchRequestBuilder orderBy(String orderBy) {
            this.orderBy$value = orderBy;
            this.orderBy$set = true;
            return this;
        }

        public RestTokenSearchRequestBuilder page(int page) {
            this.page$value = page;
            this.page$set = true;
            return this;
        }

        public RestTokenSearchRequestBuilder limit(int limit) {
            this.limit$value = limit;
            this.limit$set = true;
            return this;
        }

        public RestTokenSearchRequest build() {
            String sortBy$value = this.sortBy$value;
            if (!this.sortBy$set) {
                sortBy$value = RestTokenSearchRequest.$default$sortBy();
            }
            String orderBy$value = this.orderBy$value;
            if (!this.orderBy$set) {
                orderBy$value = RestTokenSearchRequest.$default$orderBy();
            }
            int page$value = this.page$value;
            if (!this.page$set) {
                page$value = RestTokenSearchRequest.$default$page();
            }
            int limit$value = this.limit$value;
            if (!this.limit$set) {
                limit$value = RestTokenSearchRequest.$default$limit();
            }
            return new RestTokenSearchRequest(this.userKeys, this.name, this.tokenNameFilterExpression, this.tokenCreatedDateFrom, this.tokenCreatedDateTo, this.tokenExpiryDateFrom, this.tokenExpiryDateTo, this.lastAuthenticatedDateFrom, this.lastAuthenticatedDateTo, this.isUsed, sortBy$value, orderBy$value, page$value, limit$value);
        }

        public String toString() {
            return "RestTokenSearchRequest.RestTokenSearchRequestBuilder(userKeys=" + this.userKeys + ", name=" + this.name + ", tokenNameFilterExpression=" + this.tokenNameFilterExpression + ", tokenCreatedDateFrom=" + this.tokenCreatedDateFrom + ", tokenCreatedDateTo=" + this.tokenCreatedDateTo + ", tokenExpiryDateFrom=" + this.tokenExpiryDateFrom + ", tokenExpiryDateTo=" + this.tokenExpiryDateTo + ", lastAuthenticatedDateFrom=" + this.lastAuthenticatedDateFrom + ", lastAuthenticatedDateTo=" + this.lastAuthenticatedDateTo + ", isUsed=" + this.isUsed + ", sortBy$value=" + this.sortBy$value + ", orderBy$value=" + this.orderBy$value + ", page$value=" + this.page$value + ", limit$value=" + this.limit$value + ")";
        }
    }
}


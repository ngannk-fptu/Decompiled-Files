/*
 * Decompiled with CFR 0.152.
 */
package com.atlassian.ratelimiting.history;

import com.atlassian.ratelimiting.history.RateLimitingReportOrder;
import com.atlassian.ratelimiting.page.PageRequest;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Objects;

public class RateLimitingReportSearchRequest {
    private final List<String> userFilterList;
    private final RateLimitingReportOrder sortOrder;
    private final ZonedDateTime startTime;
    private final ZonedDateTime finishTime;
    private final PageRequest pageRequest;

    public boolean isDateRangeSearchQuery() {
        return Objects.nonNull(this.startTime) || Objects.nonNull(this.finishTime);
    }

    public boolean isUserFilterSearchQuery() {
        return Objects.nonNull(this.userFilterList) && !this.userFilterList.isEmpty();
    }

    public boolean isFrequencySortOrder() {
        return RateLimitingReportOrder.FREQUENCY.equals((Object)this.sortOrder);
    }

    private static RateLimitingReportOrder $default$sortOrder() {
        return RateLimitingReportOrder.FREQUENCY;
    }

    private static PageRequest $default$pageRequest() {
        return new PageRequest(0, 20);
    }

    RateLimitingReportSearchRequest(List<String> userFilterList, RateLimitingReportOrder sortOrder, ZonedDateTime startTime, ZonedDateTime finishTime, PageRequest pageRequest) {
        this.userFilterList = userFilterList;
        this.sortOrder = sortOrder;
        this.startTime = startTime;
        this.finishTime = finishTime;
        this.pageRequest = pageRequest;
    }

    public static RateLimitingReportSearchRequestBuilder builder() {
        return new RateLimitingReportSearchRequestBuilder();
    }

    public String toString() {
        return "RateLimitingReportSearchRequest(userFilterList=" + this.getUserFilterList() + ", sortOrder=" + (Object)((Object)this.getSortOrder()) + ", startTime=" + this.getStartTime() + ", finishTime=" + this.getFinishTime() + ", pageRequest=" + this.getPageRequest() + ")";
    }

    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof RateLimitingReportSearchRequest)) {
            return false;
        }
        RateLimitingReportSearchRequest other = (RateLimitingReportSearchRequest)o;
        if (!other.canEqual(this)) {
            return false;
        }
        List<String> this$userFilterList = this.getUserFilterList();
        List<String> other$userFilterList = other.getUserFilterList();
        if (this$userFilterList == null ? other$userFilterList != null : !((Object)this$userFilterList).equals(other$userFilterList)) {
            return false;
        }
        RateLimitingReportOrder this$sortOrder = this.getSortOrder();
        RateLimitingReportOrder other$sortOrder = other.getSortOrder();
        if (this$sortOrder == null ? other$sortOrder != null : !((Object)((Object)this$sortOrder)).equals((Object)other$sortOrder)) {
            return false;
        }
        ZonedDateTime this$startTime = this.getStartTime();
        ZonedDateTime other$startTime = other.getStartTime();
        if (this$startTime == null ? other$startTime != null : !((Object)this$startTime).equals(other$startTime)) {
            return false;
        }
        ZonedDateTime this$finishTime = this.getFinishTime();
        ZonedDateTime other$finishTime = other.getFinishTime();
        if (this$finishTime == null ? other$finishTime != null : !((Object)this$finishTime).equals(other$finishTime)) {
            return false;
        }
        PageRequest this$pageRequest = this.getPageRequest();
        PageRequest other$pageRequest = other.getPageRequest();
        return !(this$pageRequest == null ? other$pageRequest != null : !((Object)this$pageRequest).equals(other$pageRequest));
    }

    protected boolean canEqual(Object other) {
        return other instanceof RateLimitingReportSearchRequest;
    }

    public int hashCode() {
        int PRIME = 59;
        int result = 1;
        List<String> $userFilterList = this.getUserFilterList();
        result = result * 59 + ($userFilterList == null ? 43 : ((Object)$userFilterList).hashCode());
        RateLimitingReportOrder $sortOrder = this.getSortOrder();
        result = result * 59 + ($sortOrder == null ? 43 : ((Object)((Object)$sortOrder)).hashCode());
        ZonedDateTime $startTime = this.getStartTime();
        result = result * 59 + ($startTime == null ? 43 : ((Object)$startTime).hashCode());
        ZonedDateTime $finishTime = this.getFinishTime();
        result = result * 59 + ($finishTime == null ? 43 : ((Object)$finishTime).hashCode());
        PageRequest $pageRequest = this.getPageRequest();
        result = result * 59 + ($pageRequest == null ? 43 : ((Object)$pageRequest).hashCode());
        return result;
    }

    public List<String> getUserFilterList() {
        return this.userFilterList;
    }

    public RateLimitingReportOrder getSortOrder() {
        return this.sortOrder;
    }

    public ZonedDateTime getStartTime() {
        return this.startTime;
    }

    public ZonedDateTime getFinishTime() {
        return this.finishTime;
    }

    public PageRequest getPageRequest() {
        return this.pageRequest;
    }

    public static class RateLimitingReportSearchRequestBuilder {
        private List<String> userFilterList;
        private boolean sortOrder$set;
        private RateLimitingReportOrder sortOrder$value;
        private ZonedDateTime startTime;
        private ZonedDateTime finishTime;
        private boolean pageRequest$set;
        private PageRequest pageRequest$value;

        RateLimitingReportSearchRequestBuilder() {
        }

        public RateLimitingReportSearchRequestBuilder userFilterList(List<String> userFilterList) {
            this.userFilterList = userFilterList;
            return this;
        }

        public RateLimitingReportSearchRequestBuilder sortOrder(RateLimitingReportOrder sortOrder) {
            this.sortOrder$value = sortOrder;
            this.sortOrder$set = true;
            return this;
        }

        public RateLimitingReportSearchRequestBuilder startTime(ZonedDateTime startTime) {
            this.startTime = startTime;
            return this;
        }

        public RateLimitingReportSearchRequestBuilder finishTime(ZonedDateTime finishTime) {
            this.finishTime = finishTime;
            return this;
        }

        public RateLimitingReportSearchRequestBuilder pageRequest(PageRequest pageRequest) {
            this.pageRequest$value = pageRequest;
            this.pageRequest$set = true;
            return this;
        }

        public RateLimitingReportSearchRequest build() {
            RateLimitingReportOrder sortOrder$value = this.sortOrder$value;
            if (!this.sortOrder$set) {
                sortOrder$value = RateLimitingReportSearchRequest.$default$sortOrder();
            }
            PageRequest pageRequest$value = this.pageRequest$value;
            if (!this.pageRequest$set) {
                pageRequest$value = RateLimitingReportSearchRequest.$default$pageRequest();
            }
            return new RateLimitingReportSearchRequest(this.userFilterList, sortOrder$value, this.startTime, this.finishTime, pageRequest$value);
        }

        public String toString() {
            return "RateLimitingReportSearchRequest.RateLimitingReportSearchRequestBuilder(userFilterList=" + this.userFilterList + ", sortOrder$value=" + (Object)((Object)this.sortOrder$value) + ", startTime=" + this.startTime + ", finishTime=" + this.finishTime + ", pageRequest$value=" + this.pageRequest$value + ")";
        }
    }
}


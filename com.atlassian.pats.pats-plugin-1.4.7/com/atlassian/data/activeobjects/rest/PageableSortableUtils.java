/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 *  org.springframework.util.CollectionUtils
 *  org.springframework.util.StringUtils
 */
package com.atlassian.data.activeobjects.rest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

public final class PageableSortableUtils {
    private static final Logger log = LoggerFactory.getLogger(PageableSortableUtils.class);
    private static final String DELIMITER = ",";
    private static final int DEFAULT_MAX_PAGE_SIZE = 2000;
    private static final Pageable DEFAULT_PAGE_REQUEST = PageRequest.of(0, 20);

    private PageableSortableUtils() {
    }

    public static Pageable extractPageable(Integer page, Integer size, List<String> sort) {
        Optional<Integer> newPage = PageableSortableUtils.parseAndApplyBoundaries(page, Integer.MAX_VALUE);
        Optional<Integer> newPageSize = PageableSortableUtils.parseAndApplyBoundaries(size, 2000);
        if (!newPage.isPresent() || !newPageSize.isPresent()) {
            return Pageable.unpaged();
        }
        int p = newPage.orElseGet(DEFAULT_PAGE_REQUEST::getPageNumber);
        int ps = newPageSize.orElseGet(DEFAULT_PAGE_REQUEST::getPageSize);
        ps = ps < 1 ? DEFAULT_PAGE_REQUEST.getPageSize() : ps;
        ps = ps > 2000 ? 2000 : ps;
        Sort newSort = PageableSortableUtils.extractSort(sort);
        return PageRequest.of(p, ps, newSort.isSorted() ? newSort : DEFAULT_PAGE_REQUEST.getSort());
    }

    private static Optional<Integer> parseAndApplyBoundaries(Integer parameter, int upper) {
        if (Objects.isNull(parameter)) {
            return Optional.empty();
        }
        return Optional.of(parameter < 0 ? 0 : (parameter > upper ? upper : parameter));
    }

    public static Sort extractSort(List<String> sort) {
        log.debug("Got sort: [{}]", sort);
        if (CollectionUtils.isEmpty(sort)) {
            return Sort.unsorted();
        }
        return PageableSortableUtils.parseParameterIntoSort(sort, DELIMITER);
    }

    private static Sort parseParameterIntoSort(List<String> sort, String delimiter) {
        ArrayList<Sort.Order> allOrders = new ArrayList<Sort.Order>();
        for (String part : sort) {
            if (part == null) continue;
            String[] elements = (String[])Arrays.stream(part.split(delimiter)).filter(PageableSortableUtils::notOnlyDots).toArray(String[]::new);
            Optional<Sort.Direction> direction = elements.length == 0 ? Optional.empty() : Sort.Direction.fromOptionalString(elements[elements.length - 1]);
            int lastIndex = direction.map(it -> elements.length - 1).orElseGet(() -> elements.length);
            for (int i = 0; i < lastIndex; ++i) {
                PageableSortableUtils.toOrder(elements[i], direction).ifPresent(allOrders::add);
            }
        }
        Sort parsedSort = allOrders.isEmpty() ? Sort.unsorted() : Sort.by(allOrders);
        log.debug("Got sorted: [{}]", (Object)parsedSort);
        return parsedSort;
    }

    private static boolean notOnlyDots(String source) {
        return StringUtils.hasText((String)source.replace(".", ""));
    }

    private static Optional<Sort.Order> toOrder(String property, Optional<Sort.Direction> direction) {
        return !StringUtils.hasText((String)property) ? Optional.empty() : Optional.of(direction.map(it -> new Sort.Order((Sort.Direction)((Object)it), property)).orElseGet(() -> Sort.Order.by(property)));
    }
}


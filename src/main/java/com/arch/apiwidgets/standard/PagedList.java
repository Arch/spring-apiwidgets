/*
 * Copyright (c) 2018 yingtingxu(徐应庭). All rights reserved.
 */

package com.arch.apiwidgets.standard;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

/**
 * Provides the paged list for any type.
 * @param <T> the type for paging.
 */
public class PagedList<T>  {
    private int indexFrom;
    private int pageIndex;
    private int pageSize = 20;
    private int totalCount;
    private List<T> items;

    private static final PagedList EMPTY = new PagedList();

    public PagedList() {
        items = new ArrayList<>();
    }

    public int getIndexFrom() {
        return indexFrom;
    }

    public void setIndexFrom(int indexFrom) {
        this.indexFrom = indexFrom;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public void setPageIndex(int pageIndex) {
        this.pageIndex = pageIndex;
    }

    public int getPageSize() {
        return pageSize;
    }

    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }

    public int getTotalCount() {
        return totalCount;
    }

    public void setTotalCount(int totalCount) {
        this.totalCount = totalCount;
    }

    public int getTotalPages() {
        return (int)Math.ceil((totalCount / (double)pageSize));
    }

    public boolean getHasPreviousPage() {
        return pageIndex - indexFrom > 0;
    }

    public boolean getHasNextPage() {
        return pageIndex - indexFrom + 1 < getTotalPages();
    }

    public List<T> getItems() {
        return items;
    }

    public void setItems(List<T> items) {
        this.items = items;
    }

    public static<T, R> PagedList<R> from(PagedList<T> list, Function<T, R> converter) {
        PagedList<R> pagedList = new PagedList<>();
        pagedList.setIndexFrom(list.getIndexFrom());
        pagedList.setPageIndex(list.getPageIndex());
        pagedList.setPageSize(list.getPageSize());
        pagedList.setTotalCount(list.getTotalCount());
        for (T item : list.getItems()) {
            pagedList.items.add(converter.apply(item));
        }
        return pagedList;
    }

    public static<T> PagedList<T> emptyPagedList() {
        return EMPTY;
    }
}
/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.confluence.content.render.xhtml.ConversionContext
 *  com.atlassian.confluence.content.render.xhtml.DefaultConversionContext
 *  com.atlassian.confluence.content.render.xhtml.XhtmlException
 *  com.atlassian.confluence.core.ContentEntityObject
 *  com.atlassian.confluence.internal.ContentPermissionManagerInternal
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.ChildPositionComparator
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.PageManager
 *  com.atlassian.confluence.pages.actions.ChildrenAction
 *  com.atlassian.confluence.renderer.PageContext
 *  com.atlassian.confluence.security.PermissionCheckExemptions
 *  com.atlassian.confluence.spaces.Space
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.util.ContentCreationComparator
 *  com.atlassian.confluence.util.ContentEntityObjectTitleComparator
 *  com.atlassian.confluence.util.ContentModificationComparator
 *  com.atlassian.confluence.util.ExcerptHelper
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.xhtml.api.XhtmlContent
 *  com.atlassian.renderer.RenderContext
 *  com.atlassian.renderer.v2.RenderMode
 *  com.atlassian.user.User
 *  org.apache.commons.collections.CollectionUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.adaptavist.confluence.naturalchildren;

import com.adaptavist.confluence.naturalchildren.HasMoreInfo;
import com.adaptavist.confluence.naturalchildren.LoadMoreHelper;
import com.adaptavist.confluence.naturalchildren.NaturalPageComparator;
import com.adaptavist.confluence.naturalchildren.PageList;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.DefaultConversionContext;
import com.atlassian.confluence.content.render.xhtml.XhtmlException;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.internal.ContentPermissionManagerInternal;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.ChildPositionComparator;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.PageManager;
import com.atlassian.confluence.pages.actions.ChildrenAction;
import com.atlassian.confluence.renderer.PageContext;
import com.atlassian.confluence.security.PermissionCheckExemptions;
import com.atlassian.confluence.spaces.Space;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.util.ContentCreationComparator;
import com.atlassian.confluence.util.ContentEntityObjectTitleComparator;
import com.atlassian.confluence.util.ContentModificationComparator;
import com.atlassian.confluence.util.ExcerptHelper;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.xhtml.api.XhtmlContent;
import com.atlassian.renderer.RenderContext;
import com.atlassian.renderer.v2.RenderMode;
import com.atlassian.user.User;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NaturalChildrenAction
extends ChildrenAction {
    private static final Logger log = LoggerFactory.getLogger(NaturalChildrenAction.class);
    static final int BATCH_SIZE = Integer.getInteger("page-tree.partial-loading-batch-size", 200);
    private static final boolean DISABLE_PARTIAL_LOADING = Boolean.getBoolean("page-tree.partial-loading.disable");
    private static final String EXCERPT_KEY = "confluence.excerpt";
    private XhtmlContent xhtmlContent;
    private PageManager pageManager;
    private ExcerptHelper excerptHelper;
    private Boolean excerpt;
    private Boolean reverse;
    private String sort;
    private String treeId;
    private Long treePageId;
    private String[] ancestors;
    private Set<Long> mustBeDisplayedItems = new HashSet<Long>();
    private String spaceKey;
    private int startDepth;
    private boolean hasRoot;
    private boolean disableLinks;
    private boolean mobile;
    private boolean expandCurrent;
    private String placement;
    private Long elementsAfter;
    private Long elementsBefore;
    private List<Long> idsToExpand = new ArrayList<Long>();
    private boolean exempt;
    final List<HasMoreInfo> hasMoreStack = new ArrayList<HasMoreInfo>();
    public static final String SORT_BITWISE = "bitwise";
    public static final String SORT_CREATION = "creation";
    public static final String SORT_MODIFIED = "modified";
    public static final String SORT_NATURAL = "natural";
    public static final String SORT_POSITION = "position";
    public static final String SORT_DEFAULT = "position";
    private ContentPermissionManagerInternal contentPermissionManager;
    private PermissionCheckExemptions permissionCheckExemptions;

    public void setXhtmlContent(XhtmlContent xhtmlContent) {
        this.xhtmlContent = xhtmlContent;
    }

    public void setPageManager(PageManager pageManager) {
        super.setPageManager(pageManager);
        this.pageManager = pageManager;
    }

    public void setExcerptHelper(ExcerptHelper excerptHelper) {
        this.excerptHelper = excerptHelper;
    }

    public String getPageExcerptHtml(Page page) {
        if ((page = this.pageManager.getPage(page.getId())) == null) {
            return "";
        }
        String excerpt = this.excerptHelper.getExcerpt((ContentEntityObject)page);
        if (StringUtils.isBlank((CharSequence)excerpt)) {
            return "";
        }
        PageContext renderContext = page.toPageContext();
        renderContext.pushRenderMode(RenderMode.suppress((long)256L));
        DefaultConversionContext excerptConversionContext = new DefaultConversionContext((RenderContext)renderContext);
        try {
            excerpt = this.xhtmlContent.convertStorageToView(excerpt, (ConversionContext)excerptConversionContext);
        }
        catch (XhtmlException | XMLStreamException e) {
            excerpt = this.handleError(excerpt, (Exception)e);
        }
        renderContext.popRenderMode();
        return excerpt;
    }

    private String handleError(String excerpt, Exception exception) {
        log.warn("Error rendering wiki link: [" + excerpt + "]", (Throwable)exception);
        return String.format("<span class=\"error\">%s</span>", HtmlUtil.htmlEncode((String)excerpt));
    }

    public void setExcerpt(Boolean bool) {
        this.excerpt = bool;
    }

    public String doDefault() throws Exception {
        if (!super.isPermitted()) {
            return "error";
        }
        if (DISABLE_PARTIAL_LOADING) {
            log.debug("Partial page tree loading is disabled");
        } else {
            log.debug("Partial page tree loading is enabled, batch size: {}", (Object)BATCH_SIZE);
        }
        this.exempt = this.permissionCheckExemptions.isExempt((User)AuthenticatedUserThreadLocal.get());
        if (this.expandCurrent) {
            this.idsToExpand.add(this.treePageId);
        }
        return super.doDefault();
    }

    public boolean isPermitted() {
        return true;
    }

    public Boolean getExcerpt() {
        return this.excerpt;
    }

    public void setTreeId(String tid) {
        this.treeId = tid;
    }

    public String getTreeId() {
        return this.treeId;
    }

    public void setAncestors(String[] ancestors) {
        for (String ancestor : ancestors) {
            try {
                this.mustBeDisplayedItems.add(Long.parseLong(ancestor));
            }
            catch (Exception e) {
                log.warn("Unable to parse ancestor: " + e.getMessage());
            }
        }
        this.ancestors = ancestors;
    }

    public String[] getAncestors() {
        return this.ancestors;
    }

    public void setStartDepth(int startDepth) {
        this.startDepth = startDepth;
    }

    public int getStartDepth() {
        return this.startDepth;
    }

    public void setReverse(Boolean bool) {
        this.reverse = bool;
    }

    public void setSort(String sortOrder) {
        this.sort = sortOrder;
    }

    public String getSpaceKey() {
        return this.spaceKey;
    }

    public void setSpaceKey(String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public boolean hasRoot() {
        return this.hasRoot;
    }

    public void setHasRoot(boolean hasRoot) {
        this.hasRoot = hasRoot;
    }

    public boolean getDisableLinks() {
        return this.disableLinks;
    }

    public void setDisableLinks(boolean disableLinks) {
        this.disableLinks = disableLinks;
    }

    public boolean isMobile() {
        return this.mobile;
    }

    public void setMobile(boolean mobile) {
        this.mobile = mobile;
    }

    public void setTreePageId(Long treePageId) {
        if (treePageId != null) {
            this.mustBeDisplayedItems.add(treePageId);
        }
        this.treePageId = treePageId;
    }

    public void setExpandCurrent(boolean expandCurrent) {
        this.expandCurrent = expandCurrent;
    }

    public List<Long> getIdsToExpand() {
        return this.idsToExpand;
    }

    public List getPermittedChildren(Page page) {
        if (page == null) {
            return Collections.EMPTY_LIST;
        }
        AbstractPage original = super.getPage();
        super.setPage((AbstractPage)page);
        List children = page.getChildren();
        if (!this.exempt) {
            children = this.contentPermissionManager.getPermittedPagesIgnoreInheritedPermissions(children, this.getAuthenticatedUser(), "View");
        }
        ArrayList<Page> sortedChildren = new ArrayList<Page>(children);
        this.sortList(sortedChildren, this.sort, this.reverse);
        super.setPage(original);
        return sortedChildren;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public PageList getLimitedPermittedChildren(Page page) {
        if (page == null) {
            return new PageList();
        }
        AbstractPage original = super.getPage();
        super.setPage((AbstractPage)page);
        List<Page> sortedChildren = this.sortPages(page.getChildren());
        try {
            PageList pageList = this.limitBeforeAndAfter(sortedChildren);
            return pageList;
        }
        finally {
            super.setPage(original);
        }
    }

    private PageList limitBeforeAndAfter(List<Page> sortedChildren) {
        List<Page> limitedPageList = this.limitByBeforeAndAfterFilter(sortedChildren, this.elementsBefore, this.elementsAfter);
        LoadMoreHelper loadMoreHelper = new LoadMoreHelper(this.contentPermissionManager, this.getAuthenticatedUser(), this.exempt);
        boolean skipLimit = this.elementsAfter != null || this.elementsBefore != null;
        boolean sidebarPlacement = "sidebar".equals(this.placement);
        return skipLimit || DISABLE_PARTIAL_LOADING || !sidebarPlacement ? loadMoreHelper.getAllPermittedElements(limitedPageList) : loadMoreHelper.getSublistWithLoadMoreSupport(limitedPageList, BATCH_SIZE, this.mustBeDisplayedItems);
    }

    private List<Page> limitByBeforeAndAfterFilter(List<Page> pages, Long elementsBefore, Long elementsAfter) {
        if (elementsAfter != null) {
            for (int i = 0; i < pages.size(); ++i) {
                if (!elementsAfter.equals(pages.get(i).getId())) continue;
                return pages.subList(i + 1, pages.size());
            }
            return Collections.emptyList();
        }
        if (elementsBefore != null) {
            for (int i = 0; i < pages.size(); ++i) {
                if (!elementsBefore.equals(pages.get(i).getId())) continue;
                return pages.subList(0, i);
            }
            return Collections.emptyList();
        }
        return pages;
    }

    private List<Page> sortPages(List<Page> children) {
        ArrayList<Page> sortedChildren = new ArrayList<Page>(children);
        this.sortList(sortedChildren, this.sort, this.reverse);
        return sortedChildren;
    }

    public boolean hasPermittedChildren(Page page) {
        if (this.exempt) {
            return page.getChildren().size() > 0;
        }
        return this.contentPermissionManager.hasVisibleChildren(page, AuthenticatedUserThreadLocal.get());
    }

    public boolean isAncestorPage(Page page) {
        String pageId = page.getIdAsString();
        if (StringUtils.isEmpty((CharSequence)pageId) || this.ancestors == null || this.ancestors.length <= 0) {
            return false;
        }
        for (int i = 0; i < this.ancestors.length; ++i) {
            if (!pageId.equals(this.ancestors[i])) continue;
            return true;
        }
        return false;
    }

    public List getAllPermittedStartPages(String spaceKey) {
        if (StringUtils.isEmpty((CharSequence)spaceKey)) {
            return Collections.EMPTY_LIST;
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return Collections.EMPTY_LIST;
        }
        List topLevelPages = this.pageManager.getTopLevelPages(space);
        if (CollectionUtils.isEmpty((Collection)topLevelPages)) {
            return Collections.EMPTY_LIST;
        }
        List visibleTopLevelPages = this.contentPermissionManager.getPermittedPagesIgnoreInheritedPermissions(topLevelPages, AuthenticatedUserThreadLocal.get(), "View");
        this.sortList(visibleTopLevelPages, this.sort, this.reverse);
        return visibleTopLevelPages;
    }

    public PageList getLimitedPermittedStartPages(String spaceKey) {
        if (StringUtils.isEmpty((CharSequence)spaceKey)) {
            return new PageList();
        }
        Space space = this.spaceManager.getSpace(spaceKey);
        if (space == null) {
            return new PageList();
        }
        List topLevelPages = this.pageManager.getTopLevelPages(space);
        if (CollectionUtils.isEmpty((Collection)topLevelPages)) {
            return new PageList();
        }
        List<Page> sortedPages = this.sortPages(topLevelPages);
        return this.limitBeforeAndAfter(sortedPages);
    }

    private void sortList(List<Page> pageList, String sortType, Boolean isReverse) {
        if (SORT_BITWISE.equals(sortType)) {
            pageList.sort((Comparator<Page>)ContentEntityObjectTitleComparator.getInstance());
        } else if (SORT_CREATION.equals(sortType)) {
            pageList.sort((Comparator<Page>)new ContentCreationComparator());
        } else if (SORT_MODIFIED.equals(sortType)) {
            pageList.sort((Comparator<Page>)new ContentModificationComparator());
        } else if (SORT_NATURAL.equals(sortType)) {
            pageList.sort(new NaturalPageComparator());
        } else if ("position".equals(sortType)) {
            pageList.sort((Comparator<Page>)new ChildPositionComparator());
        }
        if (isReverse == Boolean.TRUE) {
            Collections.reverse(pageList);
        }
    }

    public void setContentPermissionManager(ContentPermissionManagerInternal contentPermissionManager) {
        this.contentPermissionManager = contentPermissionManager;
    }

    public void setPermissionCheckExemptions(PermissionCheckExemptions permissionCheckExemptions) {
        this.permissionCheckExemptions = permissionCheckExemptions;
    }

    public Long getElementsAfter() {
        return this.elementsAfter;
    }

    public void setElementsAfter(Long elementsAfter) {
        this.elementsAfter = elementsAfter;
    }

    public Long getElementsBefore() {
        return this.elementsBefore;
    }

    public void setElementsBefore(Long elementsBefore) {
        this.elementsBefore = elementsBefore;
    }

    public void push(Object id, boolean hasMore, Object nextPrevId) {
        this.hasMoreStack.add(new HasMoreInfo(hasMore, id, nextPrevId));
    }

    public HasMoreInfo pop() {
        return this.hasMoreStack.remove(this.hasMoreStack.size() - 1);
    }

    public String getPlacement() {
        return this.placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }
}


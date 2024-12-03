/*
 * Decompiled with CFR 0.152.
 * 
 * Could not load the following classes:
 *  com.atlassian.annotations.VisibleForTesting
 *  com.atlassian.annotations.security.XsrfProtectionExcluded
 *  com.atlassian.confluence.pages.AbstractPage
 *  com.atlassian.confluence.pages.AttachmentManager
 *  com.atlassian.confluence.pages.Page
 *  com.atlassian.confluence.pages.actions.ViewPageAction
 *  com.atlassian.confluence.plugins.conversion.dom.DefaultImportContext
 *  com.atlassian.confluence.plugins.conversion.dom.DefaultSplitImportContext
 *  com.atlassian.confluence.security.Permission
 *  com.atlassian.confluence.security.service.XsrfTokenService
 *  com.atlassian.confluence.user.AuthenticatedUserThreadLocal
 *  com.atlassian.confluence.user.ConfluenceUser
 *  com.atlassian.confluence.util.HtmlUtil
 *  com.atlassian.confluence.util.StrutsUtil
 *  com.atlassian.confluence.util.i18n.Message
 *  com.atlassian.core.util.FileSize
 *  com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport
 *  com.atlassian.plugins.conversion.confluence.dom.ImageSizeException
 *  com.atlassian.plugins.conversion.confluence.dom.ImportContext
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode
 *  com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.SplitImportContext
 *  com.atlassian.plugins.conversion.confluence.importing.WordImport
 *  com.atlassian.plugins.conversion.confluence.importing.WordImport$NormalizationResult
 *  com.atlassian.renderer.v2.components.HtmlEscaper
 *  com.atlassian.user.User
 *  com.atlassian.velocity.htmlsafe.HtmlSafe
 *  com.atlassian.xwork.FileUploadUtils
 *  com.atlassian.xwork.RequireSecurityToken
 *  com.benryan.components.OcSettingsManager
 *  javax.servlet.http.HttpServletRequest
 *  org.apache.commons.io.FileUtils
 *  org.apache.commons.lang3.ArrayUtils
 *  org.apache.commons.lang3.StringUtils
 *  org.apache.struts2.action.ServletRequestAware
 *  org.apache.struts2.action.SessionAware
 *  org.apache.struts2.dispatcher.LocalizedMessage
 *  org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper
 *  org.slf4j.Logger
 *  org.slf4j.LoggerFactory
 */
package com.benryan.webwork;

import com.atlassian.annotations.VisibleForTesting;
import com.atlassian.annotations.security.XsrfProtectionExcluded;
import com.atlassian.confluence.extra.office.canary.aspose.words.WordImportCanaryExecutor;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.AttachmentManager;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.pages.actions.ViewPageAction;
import com.atlassian.confluence.plugins.conversion.dom.DefaultImportContext;
import com.atlassian.confluence.plugins.conversion.dom.DefaultSplitImportContext;
import com.atlassian.confluence.security.Permission;
import com.atlassian.confluence.security.service.XsrfTokenService;
import com.atlassian.confluence.user.AuthenticatedUserThreadLocal;
import com.atlassian.confluence.user.ConfluenceUser;
import com.atlassian.confluence.util.HtmlUtil;
import com.atlassian.confluence.util.StrutsUtil;
import com.atlassian.confluence.util.i18n.Message;
import com.atlassian.core.util.FileSize;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.plugins.conversion.confluence.dom.ImageSizeException;
import com.atlassian.plugins.conversion.confluence.dom.ImportContext;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.BookmarkInfo;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.DocumentTreeNode;
import com.atlassian.plugins.conversion.confluence.dom.doc2wiki.splitter.SplitImportContext;
import com.atlassian.plugins.conversion.confluence.importing.WordImport;
import com.atlassian.renderer.v2.components.HtmlEscaper;
import com.atlassian.user.User;
import com.atlassian.velocity.htmlsafe.HtmlSafe;
import com.atlassian.xwork.FileUploadUtils;
import com.atlassian.xwork.RequireSecurityToken;
import com.benryan.components.AutoCloseableSemaphore;
import com.benryan.components.ImporterSemaphore;
import com.benryan.components.OcSettingsManager;
import com.benryan.dom.ImportTitleResolver;
import com.benryan.webwork.WordImportInfo;
import com.benryan.webwork.util.PageNames;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.struts2.action.ServletRequestAware;
import org.apache.struts2.action.SessionAware;
import org.apache.struts2.dispatcher.LocalizedMessage;
import org.apache.struts2.dispatcher.multipart.MultiPartRequestWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WordImportAction
extends ViewPageAction
implements ServletRequestAware,
SessionAware {
    private static final Logger log = LoggerFactory.getLogger(WordImportAction.class);
    private static final String FILE_KEY = "wordImportFile";
    private static final String BOOKMARKS_KEY = "wordImportBookmarks";
    private static final String TREE_KEY = "wordImportRoot";
    private static final String NODES_KEY = "wordImportNodes";
    public static final String WARNING = "warning";
    private static final long ONE_MB = 0x100000L;
    private static final long MAX_DOC_FILE_SIZE = Long.parseLong(System.getProperty("confluence.word.import.maxsize", "20"));
    private static final String STRUTS_FILE_SIZE_LIMIT_KEY = "struts.messages.upload.error.FileSizeLimitExceededException";
    private HttpServletRequest _request;
    private Map _session;
    private DocumentTreeNode<Page> _treeRoot;
    private BookmarkInfo _bookmarks;
    private String _pageTitle;
    private int _treeDepth;
    private WordImportInfo _importInfo = new WordImportInfo();
    private List<DocumentTreeNode<Page>> _orderedNodes;
    private AttachmentManager _attachmentManager;
    private List<String> _pagesBeingDeleted;
    private List<String> _pagesBeingOverwritten;
    private List<String> _pagesBeingCreated;
    private boolean _advanced;
    private OcSettingsManager ocSettingsManager;
    private XsrfTokenService xsrfTokenService;
    private WordImportCanaryExecutor canaryCage;
    private ImporterSemaphore importerSemaphore;

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    @XsrfProtectionExcluded
    public String executeFileUpload() throws Exception {
        MultiPartRequestWrapper request = FileUploadUtils.unwrapMultiPartRequest((HttpServletRequest)this._request);
        File file = null;
        try {
            String fileName;
            Collection msgErrors = request.getErrors();
            msgErrors.stream().filter(msgError -> STRUTS_FILE_SIZE_LIMIT_KEY.equals(msgError.getTextKey())).map(LocalizedMessage::getArgs).findFirst().ifPresent(args -> this.addActionError("office.connector.docimport.error.doc.too.large", new Object[]{FileSize.format((Long)((Long)args[1]))}));
            if (this.hasActionErrors()) {
                String string = "error";
                return string;
            }
            if (!msgErrors.isEmpty()) {
                StrutsUtil.localizeMultipartErrorMessages((MultiPartRequestWrapper)request).forEach(arg_0 -> ((WordImportAction)this).addActionError(arg_0));
                String string = "error";
                return string;
            }
            Optional xsrfValidationResult = this.xsrfTokenService.validateToken((HttpServletRequest)request);
            if (xsrfValidationResult.isPresent()) {
                Message xsrfValidationErrorMessage = (Message)xsrfValidationResult.get();
                this.addActionError(this.getText(xsrfValidationErrorMessage.getKey(), xsrfValidationErrorMessage.getArguments()));
                String string = "error";
                return string;
            }
            Object[] files = request.getFiles("filename");
            if (ArrayUtils.isEmpty((Object[])files)) {
                this.addActionError(this.getText("office.connector.docimport.error.no.files.uploaded"));
                String string = "error";
                return string;
            }
            file = (File)files[0].getContent();
            this._pageTitle = fileName = request.getFileNames("filename")[0];
            if (fileName.lastIndexOf(46) != -1) {
                this._pageTitle = fileName.substring(0, fileName.lastIndexOf(46));
            }
            if (!this.canaryCage.verify(file, fileName)) {
                this.addActionError(this.getText("office.connector.docimport.error.unsafe.data"));
                String string = "error";
                return string;
            }
            long actualFileSize = Math.max(this.getActualFileSize(file), file.length());
            if (actualFileSize > MAX_DOC_FILE_SIZE * 0x100000L) {
                this.addActionError("office.connector.docimport.error.doc.uncompressed.too.large", new Object[]{MAX_DOC_FILE_SIZE});
                String string = "error";
                return string;
            }
            log.debug("Import word document [ {} ] ", (Object)fileName);
            byte[] docBuf = this.buildDocumentTree(file, this._pageTitle);
            this._importInfo.setTreeDepth(this._treeDepth);
            this._session.put(FILE_KEY, docBuf);
            this._session.put(BOOKMARKS_KEY, this._bookmarks);
            this._session.put(NODES_KEY, this._orderedNodes);
            this._session.put(TREE_KEY, this._treeRoot);
        }
        catch (Exception e) {
            this.addActionError(this.getText("office.connector.docimport.error.parsing"));
            log.debug("Failed to parse file: ", (Throwable)e);
            String string = "error";
            return string;
        }
        finally {
            if (file != null && file.isFile() && !file.delete()) {
                log.warn("Failed to delete uploaded file " + file.getAbsolutePath());
            }
        }
        String submitType = this._request.getParameter("submit");
        if (submitType != null && submitType.equals("Import")) {
            this._importInfo.setTitle(this.getPage().getTitle());
            this._importInfo.setConflict(0);
            this._importInfo.setLvl(0);
            this._importInfo.setImportSpace(false);
            return this.executeFileImport();
        }
        this._importInfo.setTitle(this._pageTitle);
        return "input";
    }

    @RequireSecurityToken(value=true)
    public String executeFileImport() throws Exception {
        try (AutoCloseableSemaphore semaphore = this.importerSemaphore.acquire();){
            String string = this.doExecuteFileImport();
            return string;
        }
    }

    private String doExecuteFileImport() throws Exception {
        if (!this._session.containsKey(FILE_KEY)) {
            this.addActionError(this.getText("office.connector.docimport.error.doc.not.found"));
            return "error";
        }
        this._bookmarks = (BookmarkInfo)this._session.get(BOOKMARKS_KEY);
        this._orderedNodes = (ArrayList)this._session.get(NODES_KEY);
        this._treeRoot = (DocumentTreeNode)this._session.get(TREE_KEY);
        this._pagesBeingDeleted = new ArrayList<String>();
        this._pagesBeingOverwritten = new ArrayList<String>();
        this._pagesBeingCreated = new ArrayList<String>();
        User currentUser = AuthenticatedUserThreadLocal.getUser();
        boolean failFast = this.permissionChecks(currentUser);
        if (failFast) {
            return "error";
        }
        boolean showWarning = this.shouldShowWarning();
        if (showWarning) {
            return WARNING;
        }
        if (!this.isValidTitle()) {
            return "error";
        }
        return this.doFileImport();
    }

    private boolean isValidTitle() {
        if (StringUtils.isBlank((CharSequence)this._importInfo.getTitle())) {
            this.addFieldError("docTitle", this.getText("page.title.empty"));
            return false;
        }
        return true;
    }

    public boolean isPermitted() {
        return this.permissionManager.hasPermission((User)this.getAuthenticatedUser(), Permission.EDIT, (Object)this.getPage());
    }

    @HtmlSafe
    public String getExplanationHtml() {
        if (this._importInfo.getConflict() == 0) {
            this.conflictVersionCheck();
        }
        this.createCheck();
        StringBuilder warnBuilder = new StringBuilder("Warning! You are about to ");
        if (!this._importInfo.isImportSpace()) {
            warnBuilder.append("overwrite the contents of this page (");
            warnBuilder.append(HtmlEscaper.escapeAll((String)this.getPage().getTitle(), (boolean)true));
            warnBuilder.append(")");
            if (!this._importInfo.getTitle().equals(this.getPage().getTitle())) {
                String newTitle = this._importInfo.getTitle();
                if (this.pageManager.getPage(this.getSpaceKey(), this._importInfo.getTitle()) != null) {
                    newTitle = ImportTitleResolver.getUniquePageName(newTitle, new HashSet(), this.pageManager, this.getSpaceKey());
                }
                warnBuilder.append(" and change the title from \"");
                warnBuilder.append(HtmlEscaper.escapeAll((String)this.getPage().getTitle(), (boolean)true));
                warnBuilder.append("\" to \"");
                warnBuilder.append(HtmlEscaper.escapeAll((String)newTitle, (boolean)true));
                warnBuilder.append("\"");
            }
            if (this._pagesBeingCreated.size() > 0 || this._pagesBeingDeleted.size() > 0 || this._pagesBeingOverwritten.size() > 0) {
                warnBuilder.append(" and ");
            }
        }
        if (this._pagesBeingDeleted.size() > 0) {
            warnBuilder.append(" delete the following pages");
            this.updateListContent(warnBuilder, this._pagesBeingDeleted);
            if (this._pagesBeingOverwritten.size() > 0 || this._pagesBeingCreated.size() > 0) {
                warnBuilder.append("and ");
            }
        }
        if (this._pagesBeingOverwritten.size() > 0) {
            warnBuilder.append("overwrite the following pages");
            this.updateListContent(warnBuilder, this._pagesBeingOverwritten);
            if (this._pagesBeingCreated.size() > 0) {
                warnBuilder.append(" and ");
            }
        }
        if (this._pagesBeingCreated.size() > 0) {
            warnBuilder.append("create the following pages");
            this.updateListContent(warnBuilder, this._pagesBeingCreated);
        }
        return warnBuilder.toString();
    }

    private void updateListContent(StringBuilder warnBuilder, List<String> titles) {
        warnBuilder.append("<ul>");
        titles.forEach(title -> {
            warnBuilder.append("<li>");
            warnBuilder.append(HtmlEscaper.escapeAll((String)title, (boolean)true));
            warnBuilder.append("</li>");
        });
        warnBuilder.append("</ul>");
    }

    private void createCheck() {
        try {
            DocumentTreeNode root = (DocumentTreeNode)this._treeRoot.clone();
            ArrayList<DocumentTreeNode<Page>> nodes = new ArrayList<DocumentTreeNode<Page>>(this._orderedNodes.size());
            for (DocumentTreeNode<Page> _orderedNode : this._orderedNodes) {
                nodes.add((DocumentTreeNode<Page>)((DocumentTreeNode)_orderedNode.clone()));
            }
            this.resolveTitleConflicts(false, (DocumentTreeNode<Page>)root, nodes);
            if (this._importInfo.isImportSpace()) {
                this._pagesBeingCreated.add(root.getText());
            }
            for (DocumentTreeNode<Page> node : nodes) {
                if (node.getOldPage() != null || node.getLvl() > this._importInfo.getLvl()) continue;
                this._pagesBeingCreated.add(node.getText());
            }
        }
        catch (CloneNotSupportedException cloneNotSupportedException) {
            // empty catch block
        }
    }

    private void conflictVersionCheck() {
        for (DocumentTreeNode<Page> node : this._orderedNodes) {
            String text = PageNames.fixPageTitle(node.getText());
            Page page = this.pageManager.getPage(this.getSpaceKey(), text);
            if (page == null || node.getLvl() > this._importInfo.getLvl()) continue;
            this._pagesBeingOverwritten.add(page.getTitle());
        }
    }

    @RequireSecurityToken(value=true)
    public String processWarning() throws Exception {
        String submitType = this._request.getParameter("submit");
        if (submitType != null && submitType.equals("Continue")) {
            return this.doFileImport();
        }
        if (this._advanced) {
            this._bookmarks = (BookmarkInfo)this._session.get(BOOKMARKS_KEY);
            this._orderedNodes = (ArrayList)this._session.get(NODES_KEY);
            this._treeRoot = (DocumentTreeNode)this._session.get(TREE_KEY);
            this._pagesBeingDeleted = new ArrayList<String>();
            return "input";
        }
        return "success";
    }

    private String doFileImport() throws Exception {
        Page rootPage;
        byte[] docBuf = (byte[])this._session.remove(FILE_KEY);
        if (docBuf == null) {
            this.addActionError(this.getText("office.connector.docimport.error.doc.not.found"));
            return "error";
        }
        this._bookmarks = (BookmarkInfo)this._session.remove(BOOKMARKS_KEY);
        this._orderedNodes = (ArrayList)this._session.remove(NODES_KEY);
        this._treeRoot = (DocumentTreeNode)this._session.remove(TREE_KEY);
        this._pagesBeingDeleted = new ArrayList<String>();
        ConfluenceUser currentUser = AuthenticatedUserThreadLocal.get();
        if (!this._importInfo.isImportSpace()) {
            Page page = (Page)this.getPage();
            this._treeRoot.setOldPage((Object)page);
            if (this._importInfo.getOverwriteAll()) {
                this.recursiveTrash(page);
            }
        }
        this.resolveTitleConflicts(true, this._treeRoot, this._orderedNodes);
        Page oldPage = (Page)this._treeRoot.getOldPage();
        if (oldPage != null) {
            rootPage = oldPage;
            oldPage = (Page)rootPage.clone();
            this._treeRoot.setOldPage((Object)oldPage);
        } else {
            rootPage = new Page();
            rootPage.setSpace(this.getSpace());
            Page homePage = this.getSpace().getHomePage();
            if (homePage != null) {
                homePage.addChild(rootPage);
            }
            rootPage.setCreator(currentUser);
        }
        rootPage.setTitle(this._treeRoot.getText());
        this._importInfo.setTitle(this._treeRoot.getText());
        try {
            if (this._importInfo.getLvl() > 0) {
                WordImport.doImportSplit((InputStream)new ByteArrayInputStream(docBuf), (SplitImportContext)new DefaultSplitImportContext(this.pageManager, rootPage, (Page)this._treeRoot.getOldPage(), this._attachmentManager, this._bookmarks, this._orderedNodes, this._importInfo.getLvl(), this.ocSettingsManager.getMaxImportImageSize()), (boolean)this.ocSettingsManager.isDoFootnotes());
            } else {
                WordImport.doImport((InputStream)new ByteArrayInputStream(docBuf), (ImportContext)new DefaultImportContext(this.pageManager, (AbstractPage)rootPage, (AbstractPage)this._treeRoot.getOldPage(), this._attachmentManager, this._bookmarks, this.ocSettingsManager.getMaxImportImageSize()), (boolean)this.ocSettingsManager.isDoFootnotes());
            }
        }
        catch (ImageSizeException ex) {
            this.addActionError("office.connector.docimport.error.imagesize", new Object[]{ex.getImgSize().width, ex.getImgSize().height, ex.getMaxSize().width, ex.getMaxSize().height});
            return "error";
        }
        return "success";
    }

    private boolean shouldShowWarning() {
        return this.ocSettingsManager.isShowWarning();
    }

    private boolean permissionChecks(User currentUser) {
        boolean failFast = false;
        if (this._importInfo.isImportSpace()) {
            if (!this.permissionManager.hasCreatePermission(currentUser, (Object)this.getSpace(), Page.class)) {
                this.addActionError("You don't have permission to create pages");
                failFast = true;
            }
        } else {
            AbstractPage currentPage = this.getPage();
            if (!this.permissionManager.hasPermission(currentUser, Permission.EDIT, (Object)this.getPage())) {
                this.addActionError("You don't have permission to edit this page");
                failFast = true;
            }
            if (this._importInfo.getOverwriteAll()) {
                failFast |= this.recursiveDeleteCheck(currentPage, currentUser);
            }
        }
        if (this._importInfo.getConflict() == 2) {
            failFast |= this.conflictDeleteCheck(currentUser);
        }
        return failFast;
    }

    private boolean conflictDeleteCheck(User currentUser) {
        boolean failFast = false;
        if (this._importInfo.isImportSpace()) {
            String text = PageNames.fixPageTitle(this._importInfo.getTitle());
            Page page = this.pageManager.getPage(this.getSpaceKey(), text);
            if (page != null) {
                this._pagesBeingDeleted.add(page.getTitle());
                if (!this.permissionManager.hasPermission(currentUser, Permission.REMOVE, (Object)page)) {
                    this.addActionError("You don't have permission to delete the page \"" + page.getTitle() + "\"");
                    failFast = true;
                }
            }
        }
        for (int x = 0; x < this._orderedNodes.size(); ++x) {
            DocumentTreeNode<Page> node = this._orderedNodes.get(x);
            String text = PageNames.fixPageTitle(node.getText());
            Page page = this.pageManager.getPage(this.getSpaceKey(), text);
            if (page == null || node.getLvl() > this._importInfo.getLvl()) continue;
            this._pagesBeingDeleted.add(page.getTitle());
            if (this.permissionManager.hasPermission(currentUser, Permission.REMOVE, (Object)page)) continue;
            this.addActionError("You don't have permission to delete the page \"" + page.getTitle() + "\"");
            failFast = true;
        }
        return failFast;
    }

    private void recursiveTrash(Page rootPage) {
        List children = rootPage.getChildren();
        for (int x = children.size() - 1; x >= 0; --x) {
            Page childPage = (Page)children.get(x);
            this.recursiveTrash(childPage);
            childPage.trash();
        }
    }

    public String getViewUrl() throws Exception {
        return "/display/" + this.getSpaceKey() + "/" + HtmlUtil.urlEncode((String)this._importInfo.getTitle());
    }

    public String getPageTitle() {
        return this._pageTitle;
    }

    public void setPageTitle(String pageTitle) {
        this._pageTitle = pageTitle;
    }

    public List<String> getPagesBeingDeleted() {
        return this._pagesBeingDeleted;
    }

    public boolean getAdvanced() {
        return this._advanced;
    }

    public void setAdvanced(boolean advanced) {
        this._advanced = advanced;
    }

    public DocumentTreeNode<Page> getTreeRoot() {
        return this._treeRoot;
    }

    public boolean isImportSpace() {
        return this._importInfo.isImportSpace();
    }

    public void setImportSpace(boolean space) {
        this._importInfo.setImportSpace(space);
    }

    public int getLvl() {
        return this._importInfo.getLvl();
    }

    public void setLvl(int lvl) {
        this._importInfo.setLvl(lvl);
    }

    public String getDocTitle() {
        return this._importInfo.getTitle();
    }

    public void setDocTitle(String title) {
        this._importInfo.setTitle(title);
    }

    public void setConflict(int conflict) {
        this._importInfo.setConflict(conflict);
    }

    public int getConflict() {
        return this._importInfo.getConflict();
    }

    public void setOverwriteAll(boolean overwrite) {
        this._importInfo.setOverwriteAll(overwrite);
    }

    public boolean getOverwriteAll() {
        return this._importInfo.getOverwriteAll();
    }

    public void setTreeDepth(int depth) {
        this._treeDepth = depth;
    }

    public int getTreeDepth() {
        return this._treeDepth;
    }

    public void setCanaryCage(WordImportCanaryExecutor canaryCage) {
        this.canaryCage = canaryCage;
    }

    public void setAttachmentManager(@ComponentImport AttachmentManager manager) {
        this._attachmentManager = manager;
    }

    private byte[] buildDocumentTree(File file, String pageTitle) throws Exception {
        byte[] docBuf = FileUtils.readFileToByteArray((File)file);
        WordImport.NormalizationResult normalizationResult = WordImport.parseAndNormalize((InputStream)new ByteArrayInputStream(docBuf), (String)pageTitle);
        this._treeRoot = normalizationResult.treeRoot;
        this._orderedNodes = normalizationResult.orderedNodes;
        this._bookmarks = normalizationResult.bookmarks;
        this._treeDepth = normalizationResult.treeDepth;
        return docBuf;
    }

    public void withServletRequest(HttpServletRequest req) {
        this._request = req;
    }

    public void withSession(Map session) {
        this._session = session;
    }

    private void resolveTitleConflicts(boolean doDeletes, DocumentTreeNode<Page> treeRoot, List<DocumentTreeNode<Page>> orderedNodes) {
        ImportTitleResolver resolver = new ImportTitleResolver(this._importInfo, this.pageManager, this.getSpaceKey());
        treeRoot.setText(this._importInfo.getTitle());
        resolver.resolveTitle(treeRoot, true);
        for (DocumentTreeNode<Page> orderedNode : orderedNodes) {
            resolver.resolveTitle(orderedNode, false);
        }
        if (doDeletes) {
            resolver.doDeletes();
        }
    }

    public void setOcSettingsManager(OcSettingsManager settingsManager) {
        this.ocSettingsManager = settingsManager;
    }

    private boolean recursiveDeleteCheck(AbstractPage currentPage, User currentUser) {
        boolean failFast = false;
        if (currentPage instanceof Page) {
            List children = ((Page)currentPage).getChildren();
            for (AbstractPage nextChild : children) {
                this._pagesBeingDeleted.add(nextChild.getTitle());
                if (!this.permissionManager.hasPermission(currentUser, Permission.REMOVE, (Object)nextChild) && this.isPagePermitted(currentUser, nextChild)) {
                    this.addActionError("You don't have permission to delete the page \"" + nextChild.getTitle() + "\"");
                    failFast = true;
                }
                failFast |= this.recursiveDeleteCheck(nextChild, currentUser);
            }
        }
        return failFast;
    }

    @VisibleForTesting
    public void setImporterSemaphore(ImporterSemaphore importerSemaphore) {
        this.importerSemaphore = importerSemaphore;
    }

    public boolean isPageRequired() {
        return false;
    }

    public void setXsrfTokenService(XsrfTokenService xsrfTokenService) {
        this.xsrfTokenService = xsrfTokenService;
    }

    @VisibleForTesting
    public void setImportInfo(WordImportInfo _importInfo) {
        this._importInfo = _importInfo;
    }

    private long getActualFileSize(File file) {
        long totalSize = 0L;
        try {
            ZipEntry zipEntry;
            ZipInputStream zipInputStream = new ZipInputStream(Files.newInputStream(file.toPath(), new OpenOption[0]));
            while ((zipEntry = zipInputStream.getNextEntry()) != null) {
                totalSize += zipEntry.getSize();
            }
        }
        catch (IOException e) {
            log.error("Import file is not zip file");
        }
        return totalSize;
    }

    private boolean isPagePermitted(User user, AbstractPage nextChild) {
        Optional<AbstractPage> targetPage = this.getTargetObject(nextChild);
        return targetPage.map(page -> super.isPermitted() && this.permissionManager.hasPermission(user, Permission.EDIT, page)).orElse(false);
    }

    private Optional<AbstractPage> getTargetObject(AbstractPage page) {
        if (page != null) {
            return Optional.of(page.getLatestVersion());
        }
        return Optional.empty();
    }
}


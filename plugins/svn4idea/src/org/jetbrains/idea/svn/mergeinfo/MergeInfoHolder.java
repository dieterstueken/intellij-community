// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.idea.svn.mergeinfo;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.vcs.changes.committed.CommittedChangeListsListener;
import com.intellij.openapi.vcs.changes.committed.DecoratorManager;
import com.intellij.openapi.vcs.versionBrowser.CommittedChangeList;
import com.intellij.util.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.idea.svn.api.Url;
import org.jetbrains.idea.svn.dialogs.WCInfo;
import org.jetbrains.idea.svn.dialogs.WCInfoWithBranches;
import org.jetbrains.idea.svn.history.RootsAndBranches;
import org.jetbrains.idea.svn.history.SvnChangeList;
import org.jetbrains.idea.svn.history.SvnMergeInfoRootPanelManual;

import java.util.HashMap;
import java.util.Map;

public class MergeInfoHolder {

  private final @NotNull DecoratorManager myManager;
  private final @NotNull SvnMergeInfoCache myMergeInfoCache;
  private final @NotNull RootsAndBranches myMainPanel;
  private final @NotNull SvnMergeInfoRootPanelManual myPanel;

  // used ONLY when refresh is triggered
  private final @NotNull Map<Pair<WCInfo, Url>, MergeInfoCached> myCachedMap;

  public MergeInfoHolder(@NotNull Project project,
                         @NotNull DecoratorManager manager,
                         @NotNull RootsAndBranches mainPanel,
                         @NotNull SvnMergeInfoRootPanelManual panel) {
    myManager = manager;
    myMainPanel = mainPanel;
    myPanel = panel;
    myMergeInfoCache = SvnMergeInfoCache.getInstance(project);
    myCachedMap = new HashMap<>();
  }

  private @NotNull Pair<WCInfo, Url> getCacheKey() {
    return Pair.create(myPanel.getWcInfo(), myPanel.getBranch().getUrl());
  }

  private @Nullable MergeInfoCached getCurrentCache() {
    return myCachedMap.get(getCacheKey());
  }

  private boolean isEnabledAndConfigured(boolean ignoreEnabled) {
    return (ignoreEnabled || myMainPanel.isHighlightingOn() && myPanel.isEnabled()) &&
           myPanel.getBranch() != null &&
           myPanel.getLocalBranch() != null;
  }

  public boolean refreshEnabled(boolean ignoreEnabled) {
    return isEnabledAndConfigured(ignoreEnabled) && getCurrentCache() == null;
  }

  public @NotNull ListMergeStatus refresh(final boolean ignoreEnabled) {
    final CommittedChangeListsListener refresher = createRefresher(ignoreEnabled);
    if (refresher != null) {
      myManager.reportLoadedLists(refresher);
    }
    myManager.repaintTree();

    return ListMergeStatus.REFRESHING;
  }

  public @Nullable CommittedChangeListsListener createRefresher(boolean ignoreEnabled) {
    CommittedChangeListsListener result = null;

    if (refreshEnabled(ignoreEnabled)) {
      // on awt thread
      final MergeInfoCached state = myMergeInfoCache.getCachedState(myPanel.getWcInfo(), myPanel.getLocalBranch());
      myCachedMap.put(getCacheKey(), state != null ? state.copy() : new MergeInfoCached());
      myMergeInfoCache.clear(myPanel.getWcInfo(), myPanel.getLocalBranch());

      result = new MyRefresher();
    }

    return result;
  }

  private final class MyRefresher implements CommittedChangeListsListener {

    private final @NotNull WCInfoWithBranches myRefreshedRoot;
    private final WCInfoWithBranches.Branch myRefreshedBranch;
    private final String myBranchPath;

    private MyRefresher() {
      myRefreshedRoot = myPanel.getWcInfo();
      myRefreshedBranch = myPanel.getBranch();
      myBranchPath = myPanel.getLocalBranch();
    }

    @Override
    public void onBeforeStartReport() {
    }

    @Override
    public boolean report(@NotNull CommittedChangeList list) {
      if (list instanceof SvnChangeList) {
        final MergeCheckResult checkState =
          myMergeInfoCache.getState(myRefreshedRoot, (SvnChangeList)list, myRefreshedBranch, myBranchPath);
        // todo make batches - by 10
        final long number = list.getNumber();
        ApplicationManager.getApplication().invokeLater(() -> {
          final MergeInfoCached cachedState = myCachedMap.get(getCacheKey());
          if (cachedState != null) {
            cachedState.getMap().put(number, checkState);
          }
          myManager.repaintTree();
        });
      }
      return true;
    }

    @Override
    public void onAfterEndReport() {
      ApplicationManager.getApplication().invokeLater(() -> {
        myCachedMap.remove(getCacheKey());
        updateMixedRevisionsForPanel();
        myManager.repaintTree();
      });
    }

    private @NotNull Pair<WCInfo, Url> getCacheKey() {
      return Pair.create(myRefreshedRoot, myRefreshedBranch.getUrl());
    }
  }

  public @NotNull ListMergeStatus check(final CommittedChangeList list, final boolean ignoreEnabled) {
    ListMergeStatus result;

    if (!isEnabledAndConfigured(ignoreEnabled) || !(list instanceof SvnChangeList)) {
      result = ListMergeStatus.ALIEN;
    }
    else {
      MergeInfoCached cachedState = getCurrentCache();
      MergeInfoCached state = myMergeInfoCache.getCachedState(myPanel.getWcInfo(), myPanel.getLocalBranch());

      result = cachedState != null ? check(list, cachedState, true) : state != null ? check(list, state, false) : refresh(ignoreEnabled);
    }

    return result;
  }

  public @NotNull ListMergeStatus check(@NotNull CommittedChangeList list, @NotNull MergeInfoCached state, boolean isCached) {
    MergeCheckResult mergeCheckResult = state.getMap().get(list.getNumber());
    ListMergeStatus result = state.copiedAfter(list) ? ListMergeStatus.COMMON : ListMergeStatus.from(mergeCheckResult);

    return ObjectUtils.notNull(result, isCached ? ListMergeStatus.REFRESHING : ListMergeStatus.ALIEN);
  }

  public void updateMixedRevisionsForPanel() {
    myPanel.setMixedRevisions(myMergeInfoCache.isMixedRevisions(myPanel.getWcInfo(), myPanel.getLocalBranch()));
  }
}

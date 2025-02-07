// Copyright 2000-2024 JetBrains s.r.o. and contributors. Use of this source code is governed by the Apache 2.0 license.
package org.jetbrains.plugins.gradle.settings;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.externalSystem.model.DataNode;
import com.intellij.openapi.externalSystem.model.project.ModuleData;
import com.intellij.openapi.util.Pair;
import org.gradle.tooling.model.idea.IdeaModule;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.plugins.gradle.service.project.GradleProjectResolverUtil;
import org.jetbrains.plugins.gradle.service.project.ProjectResolverContext;

import java.io.File;
import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Vladislav.Soroka
 */
public class GradleExecutionWorkspace implements Serializable {
  private static final long serialVersionUID = 1L;
  private static final Logger LOG = Logger.getInstance(GradleExecutionWorkspace.class);

  private final @NotNull List<GradleBuildParticipant> myBuildParticipants = new ArrayList<>();
  private Map<String, List<Pair<DataNode<ModuleData>, IdeaModule>>> myModuleNameIndex = Collections.emptyMap();
  private Map<String, Pair<DataNode<ModuleData>, IdeaModule>> myModuleIdIndex;

  public void addBuildParticipant(GradleBuildParticipant participant) {
    myBuildParticipants.add(participant);
  }

  public @NotNull List<GradleBuildParticipant> getBuildParticipants() {
    return Collections.unmodifiableList(myBuildParticipants);
  }

  public @Nullable ModuleData findModuleDataByArtifacts(Collection<File> artifacts) {
    ModuleData result = null;
    for (GradleBuildParticipant buildParticipant : myBuildParticipants) {
      result = buildParticipant.findModuleDataByArtifacts(artifacts);
      if (result != null) break;
    }
    return result;
  }

  public @Nullable ModuleData findModuleDataByGradleModuleName(@NotNull String moduleName) {
    ModuleData result = null;

    List<Pair<DataNode<ModuleData>, IdeaModule>> possiblePairs = myModuleNameIndex.get(moduleName);

    if (possiblePairs != null) {
      if (possiblePairs.size() == 1) {
        return possiblePairs.get(0).first.getData();
      }
      else if (possiblePairs.size() > 1) {
        LOG.warn("Detected duplicate idea module names during import. Use Gradle 4.0+ or consider renaming in Gradle: " + possiblePairs);
        return possiblePairs.get(0).first.getData();
      }
    }

    for (GradleBuildParticipant buildParticipant : myBuildParticipants) {
      result = buildParticipant.findModuleDataByName(moduleName);
      if (result != null) break;
    }
    return result;
  }

  @SuppressWarnings("unused")
  @ApiStatus.Experimental
  public @Nullable ModuleData findModuleDataByModuleId(@NotNull String moduleId) {
    final Pair<DataNode<ModuleData>, IdeaModule> pair = myModuleIdIndex.get(moduleId);
    if (pair != null) {
      return pair.first.getData();
    }
    return null;
  }

  public @Nullable ModuleData findModuleDataByModule(@NotNull ProjectResolverContext resolverContext,
                                                     @NotNull IdeaModule dependencyModule) {
    final String id = GradleProjectResolverUtil.getModuleId(resolverContext, dependencyModule);
    final Pair<DataNode<ModuleData>, IdeaModule> pair = myModuleIdIndex.get(id);
    if (pair != null) {
      return pair.first.getData();
    }
    return null;
  }

  public void setModuleIdIndex(Map<String, Pair<DataNode<ModuleData>, IdeaModule>> moduleIdIndex) {
      myModuleIdIndex = moduleIdIndex;
      myModuleNameIndex = moduleIdIndex.values().stream().collect(Collectors.groupingBy((Pair<DataNode<ModuleData>, IdeaModule> val) -> val.second.getName()));
  }
}

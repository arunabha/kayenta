/*
 * Copyright 2017 Google, Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License")
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.netflix.kayenta.canary.orca;

import com.netflix.kayenta.canary.CanaryConfig;
import com.netflix.kayenta.security.AccountCredentials;
import com.netflix.kayenta.security.AccountCredentialsRepository;
import com.netflix.kayenta.storage.ObjectType;
import com.netflix.kayenta.storage.StorageService;
import com.netflix.kayenta.storage.StorageServiceRepository;
import com.netflix.spinnaker.orca.api.pipeline.RetryableTask;
import com.netflix.spinnaker.orca.api.pipeline.TaskResult;
import com.netflix.spinnaker.orca.api.pipeline.models.ExecutionStatus;
import com.netflix.spinnaker.orca.api.pipeline.models.StageExecution;
import java.time.Duration;
import java.util.Collections;
import java.util.Map;
import javax.annotation.Nonnull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SetupCanaryTask implements RetryableTask {

  private final AccountCredentialsRepository accountCredentialsRepository;
  private final StorageServiceRepository storageServiceRepository;

  @Autowired
  public SetupCanaryTask(
      AccountCredentialsRepository accountCredentialsRepository,
      StorageServiceRepository storageServiceRepository) {
    this.accountCredentialsRepository = accountCredentialsRepository;
    this.storageServiceRepository = storageServiceRepository;
  }

  @Override
  public long getBackoffPeriod() {
    // TODO(duftler): Externalize this configuration.
    return Duration.ofSeconds(2).toMillis();
  }

  @Override
  public long getTimeout() {
    // TODO(duftler): Externalize this configuration.
    return Duration.ofMinutes(2).toMillis();
  }

  @Nonnull
  @Override
  public TaskResult execute(@Nonnull StageExecution stage) {

    Map<String, Object> context = stage.getContext();
    Map<String, ?> outputs;

    if (context.containsKey("canaryConfig")) {
      Map<String, ?> canaryConfigMap = (Map<String, ?>) context.get("canaryConfig");
      outputs = Collections.singletonMap("canaryConfig", canaryConfigMap);
    } else {
      String canaryConfigId = (String) context.get("canaryConfigId");
      String configurationAccountName = (String) context.get("configurationAccountName");
      String resolvedConfigurationAccountName =
          accountCredentialsRepository
              .getRequiredOneBy(
                  configurationAccountName, AccountCredentials.Type.CONFIGURATION_STORE)
              .getName();
      StorageService configurationService =
          storageServiceRepository.getRequiredOne(resolvedConfigurationAccountName);
      CanaryConfig canaryConfig =
          configurationService.loadObject(
              resolvedConfigurationAccountName, ObjectType.CANARY_CONFIG, canaryConfigId);
      outputs = Collections.singletonMap("canaryConfig", canaryConfig);
    }

    return TaskResult.builder(ExecutionStatus.SUCCEEDED).outputs(outputs).build();
  }
}

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

package com.netflix.kayenta.canary;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.netflix.kayenta.canary.providers.AtlasCanaryMetricSetQueryConfig;
import com.netflix.kayenta.canary.providers.PrometheusCanaryMetricSetQueryConfig;
import com.netflix.kayenta.canary.providers.StackdriverCanaryMetricSetQueryConfig;

@JsonTypeInfo(use= JsonTypeInfo.Id.NAME, include= JsonTypeInfo.As.PROPERTY, property = "type")
@JsonSubTypes({@JsonSubTypes.Type(value = AtlasCanaryMetricSetQueryConfig.class, name = "atlas"),
               @JsonSubTypes.Type(value = PrometheusCanaryMetricSetQueryConfig.class, name = "prometheus"),
               @JsonSubTypes.Type(value = StackdriverCanaryMetricSetQueryConfig.class, name = "stackdriver")})
@JsonInclude(JsonInclude.Include.NON_NULL)
public interface CanaryMetricSetQueryConfig {}
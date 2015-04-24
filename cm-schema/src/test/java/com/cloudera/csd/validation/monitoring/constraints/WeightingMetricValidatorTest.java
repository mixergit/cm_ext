// Licensed to Cloudera, Inc. under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  Cloudera, Inc. licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
package com.cloudera.csd.validation.monitoring.constraints;

import com.cloudera.csd.descriptors.MetricDescriptor;

import com.google.common.collect.ImmutableList;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WeightingMetricValidatorTest
    extends AbstractMonitoringValidatorBaseTest {

  private WeightingMetricValidator validator;

  @Before
  public void setUpCounterMetricNameValidatorTest() {
    validator = new WeightingMetricValidator(serviceDescriptor);
  }

  @Test
  public void testNoWeightingMetric() {
    setName("bytes_read");
    setWeightingMetric(null);
    assertTrue(validator.validate(metric, root).isEmpty());
    setWeightingMetric("");
    assertTrue(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testWeightingMetricInDefinedMetrics() {
    setName("bytes_read");
    setServiceMetrics(ImmutableList.of(newWeightingMetric("foobar")));
    validator = new WeightingMetricValidator(serviceDescriptor);
    setWeightingMetric("foobar");
    assertTrue(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testNoWeightingMetricInDefinedMetrics() {
    setName("bytes_read");
    setServiceMetrics(ImmutableList.of(newWeightingMetric("foobar_2")));
    // We need to create a new validator as the validator generates the list
    // of metrics on instantiation.
    validator = new WeightingMetricValidator(serviceDescriptor);
    setWeightingMetric("foobar");
    assertFalse(validator.validate(metric, root).isEmpty());
  }

  @Test
  public void testWeightingMetricReferringToSelf() {
    setName("bytes_read");
    setServiceMetrics(ImmutableList.of(metric));
    // We need to create a new validator as the validator generates the list
    // of metrics on instantiation.
    validator = new WeightingMetricValidator(serviceDescriptor);
    setWeightingMetric("bytes_read");
    assertFalse(validator.validate(metric, root).isEmpty());
  }

  private MetricDescriptor newWeightingMetric(String name) {
    MetricDescriptor ret = Mockito.mock(MetricDescriptor.class);
    Mockito.doReturn(name).when(ret).getName();
    return ret;
  }
}

/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.provisionr.amazon.activities;

import com.amazonaws.services.ec2.model.Instance;
import org.apache.provisionr.amazon.ProcessVariables;
import org.apache.provisionr.amazon.core.ProviderClientCache;
import com.google.common.base.Predicate;

public class CheckAllInstancesAreRunning extends AllInstancesMatchPredicate {

    public static class InstanceIsRunning implements Predicate<Instance> {
        @Override
        public boolean apply(Instance instance) {
            return instance.getState().getName().equalsIgnoreCase("running");
        }

        @Override
        public String toString() {
            return "InstanceIsRunning{}";
        }
    }

    public CheckAllInstancesAreRunning(ProviderClientCache cache) {
        super(cache, ProcessVariables.ALL_INSTANCES_RUNNING, new InstanceIsRunning());
    }
}

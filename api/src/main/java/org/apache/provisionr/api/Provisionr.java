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

package org.apache.provisionr.api;

import com.google.common.base.Optional;
import java.util.List;
import org.apache.provisionr.api.pool.Machine;
import org.apache.provisionr.api.pool.Pool;
import org.apache.provisionr.api.provider.Provider;

public interface Provisionr {

    /**
     * Get unique ID for this provisionr
     */
    public String getId();


    /**
     * Return the default provider configured for this bundle using the
     * Blueprint configuration mechanism or something else
     *
     * @see Provider
     */
    public Optional<Provider> getDefaultProvider();

    /**
     * Start a provisioning process based on the pool description
     * <p/>
     * This process will run until the pool is destroyed
     *
     * @param businessKey external process ID (e.g. job ID, business key)
     * @param pool        pool description
     * @return internal process ID
     */
    String startPoolManagementProcess(String businessKey, Pool pool);


    /**
     * Retrieve the list of machines for a pool or an empty list
     *
     * @param businessKey external pool ID (e.g. job ID)
     * @return list of running machines or empty
     */
    List<Machine> getMachines(String businessKey);

    /**
     * Get current pool status
     *
     * @param businessKey external process ID (e.g. job ID)
     * @return internal status information
     */
    String getStatus(String businessKey);

    /**
     * Destroy all the machines from the pool with that id
     *
     * @param businessKey external pool ID (e.g. job ID, business key)
     */
    void destroyPool(String businessKey);
}

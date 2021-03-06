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

package org.apache.provisionr.core.activities;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import org.activiti.engine.delegate.DelegateExecution;
import org.activiti.engine.delegate.JavaDelegate;
import org.apache.provisionr.api.pool.Machine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Check that we can connect to a specific port on a remote machine
 * <p/>
 * This activity expects to find an environment variable named 'machine'
 */
public class IsMachinePortOpen implements JavaDelegate {

    private static final Logger LOG = LoggerFactory.getLogger(IsMachinePortOpen.class);

    public static final String MACHINE = "machine";
    public static final int TIMEOUT_IN_MILLISECONDS = 1000;

    private final String resultVariable;
    private final int port;

    public IsMachinePortOpen(String resultVariable, int port) {
        checkArgument(port > 0, "invalid port number");
        this.resultVariable = checkNotNull(resultVariable, "resultVariable is null");
        this.port = port;
    }

    @Override
    public void execute(DelegateExecution execution) {
        Machine machine = (Machine) execution.getVariable(MACHINE);
        checkNotNull(machine, "expecting a process variable named machine (multi-instance?)");

        if (isPortOpen(machine, port)) {
            LOG.info("<< Port {} is OPEN on {}", port, machine.getPublicDnsName());
            execution.setVariable(resultVariable, true);

        } else {
            LOG.info("<< Port {} is CLOSED on {}", port, machine.getPublicDnsName());
            execution.setVariable(resultVariable, false);
        }
    }

    private boolean isPortOpen(Machine machine, int port) {
        InetSocketAddress socketAddress = new InetSocketAddress(machine.getPublicDnsName(), port);

        Socket socket = null;
        try {
            socket = new Socket();
            socket.setReuseAddress(false);
            socket.setSoLinger(false, 1);
            socket.setSoTimeout(TIMEOUT_IN_MILLISECONDS);
            socket.connect(socketAddress, TIMEOUT_IN_MILLISECONDS);

        } catch (IOException e) {
            return false;
        } finally {
            if (socket != null) {
                try {
                    socket.close();
                } catch (IOException ioe) {
                    // no work to do
                }
            }
        }
        return true;
    }
}

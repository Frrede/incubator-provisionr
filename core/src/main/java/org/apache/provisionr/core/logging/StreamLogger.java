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

package org.apache.provisionr.core.logging;

import com.google.common.base.Charsets;
import static com.google.common.base.Preconditions.checkNotNull;
import com.google.common.base.Throwables;
import com.google.common.io.Closeables;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import org.slf4j.Logger;
import org.slf4j.Marker;

public abstract class StreamLogger extends Thread {

    private final InputStream inputStream;
    private final Logger logger;
    private final Marker marker;

    public StreamLogger(InputStream inputStream, Logger logger, Marker marker) {
        this.inputStream = checkNotNull(inputStream, "inputStream is null");
        this.logger = checkNotNull(logger, "logger is null");
        this.marker = checkNotNull(marker, "marker is null");

        setName(marker.getName());
        setDaemon(true);
    }

    /**
     * Write the log message at a specific level including the marker
     *
     * @param logger reference to external logger
     * @param marker log message marker
     * @param line   line collected from the input stream
     */
    protected abstract void log(Logger logger, Marker marker, String line);


    @Override
    public void run() {
        BufferedReader bufferedReader = null;
        InputStreamReader inputStreamReader = null;
        try {
            inputStreamReader = new InputStreamReader(inputStream, Charsets.UTF_8);
            bufferedReader = new BufferedReader(inputStreamReader);

            String line;
            while ((line = bufferedReader.readLine()) != null) {
                if (!line.isEmpty()) {
                    log(logger, marker, line);
                }
            }
        } catch (IOException e) {
            throw Throwables.propagate(e);

        } finally {
            Closeables.closeQuietly(bufferedReader);
            Closeables.closeQuietly(inputStreamReader);
        }
    }
}

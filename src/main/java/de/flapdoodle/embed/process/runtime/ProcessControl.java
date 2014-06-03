/**
 * Copyright (C) 2011
 *   Michael Mosmann <michael@mosmann.de>
 *   Martin Jöhren <m.joehren@googlemail.com>
 *
 * with contributions from
 * 	konstantin-ba@github,Archimedes Trajano (trajano@github)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.flapdoodle.embed.process.runtime;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

import de.flapdoodle.embed.process.config.ISupportConfig;
import de.flapdoodle.embed.process.config.process.ProcessConfig;
import de.flapdoodle.embed.process.io.Processors;

/**
 *
 */
public class ProcessControl {

    private static Logger logger = Logger.getLogger(ProcessControl.class.getName());
    private static final int SLEEPT_TIMEOUT = 10;

    private Process process;

    private InputStreamReader reader;
    private InputStreamReader error;

    private Integer pid;

    private ISupportConfig runtime;

    public ProcessControl(ISupportConfig runtime, Process process) {

        this.process = process;
        this.runtime = runtime;
        reader = new InputStreamReader(this.process.getInputStream());
        error = new InputStreamReader(this.process.getErrorStream());
        pid = Processes.processId(this.process);
    }

    public Reader getReader() {

        return reader;
    }

    public InputStreamReader getError() {

        return error;
    }

    public int stop() {

        return waitForProcessGotKilled();
    }

    private Integer stopOrDestroyProcess() {

        if (process.isAlive()) {
            for (int timeout : Arrays.asList(100, 1000, 2000)) {
                try {
                    process.waitFor(timeout, TimeUnit.MILLISECONDS);
                    if (!process.isAlive()) {
                        break;
                    }
                } catch (InterruptedException e) {
                }
            }
            if (process.isAlive()) {
                process = process.destroyForcibly();
            }
        }
        return process.exitValue();
    }

    //CHECKSTYLE:OFF

    /**
     * It may happen in tests, that the process is currently using some files in
     * the temp directory, e.g. journal files (journal/j._0) and got killed at
     * that time, so it takes a bit longer to kill the process. So we just wait
     * for a second (in 10 ms steps) that the process got really killed.
     */
    private int waitForProcessGotKilled() {

        Integer retCode = stopOrDestroyProcess();
        if (retCode == null) {
            String message = "\n\n" + "----------------------------------------------------\n"
                    + "Something bad happend. We couldn't kill " + runtime.getName() + " process, and tried a lot.\n"
                    + "If you want this problem solved you can help us if you open a new issue.\n" + "\n"
                    + "Follow this link:\n" + runtime.getSupportUrl() +
                    "\n"
                    + "Thank you:)\n" + "----------------------------------------------------\n\n";
            throw new IllegalStateException("Couldn't kill " + runtime.getName() + " process!" + message);
        }
        return retCode;
    }

    //CHECKSTYLE:ON
    public static ProcessControl fromCommandLine(ISupportConfig runtime, List<String> commandLine, boolean redirectErrorStream)
            throws IOException {

        ProcessBuilder processBuilder = newProcessBuilder(commandLine, redirectErrorStream);
        return start(runtime, processBuilder);
    }

    public static ProcessControl start(ISupportConfig runtime, ProcessBuilder processBuilder) throws IOException {

        return new ProcessControl(runtime, processBuilder.start());
    }

    public static ProcessBuilder newProcessBuilder(List<String> commandLine, boolean redirectErrorStream) {

        return newProcessBuilder(commandLine, new HashMap<String, String>(), redirectErrorStream);
    }

    public static ProcessBuilder newProcessBuilder(List<String> commandLine, Map<String, String> environment, boolean redirectErrorStream) {

        ProcessBuilder processBuilder = new ProcessBuilder(commandLine);
        if (!environment.isEmpty()) {
            processBuilder.environment().putAll(environment);
        }
        if (redirectErrorStream) {
            processBuilder.redirectErrorStream();
        }
        return processBuilder;
    }

    public static boolean executeCommandLine(ISupportConfig support, String label, ProcessConfig processConfig) {

        boolean ret = false;

        List<String> commandLine = processConfig.getCommandLine();
        try {
            ProcessControl process = fromCommandLine(support, processConfig.getCommandLine(), processConfig.getError() == null);
            Processors.connect(process.getReader(), processConfig.getOutput());
            Thread.sleep(SLEEPT_TIMEOUT);
            ret = process.stop() == 0;
            logger.info("execSuccess: " + ret + " " + commandLine);
            return ret;
        } catch (IOException e) {
            logger.log(Level.SEVERE, "" + commandLine, e);
        } catch (InterruptedException e) {
            logger.log(Level.SEVERE, "" + commandLine, e);
        }
        return false;
    }

    public int waitFor() throws InterruptedException {

        return process.waitFor();
    }

    public static void addShutdownHook(Runnable runable) {

        Runtime.getRuntime().addShutdownHook(new Thread(runable));
    }

    public Integer getPid() {

        return pid;
    }

    public boolean isProcessAlive() {

        return process != null && process.isAlive();
    }

}

package com.salazart;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.json.JsonObject;



/**
 * This class simulate serverswork with single clients
 * @author home
 *
 */
public class ClientSimulator {
	private static final Logger LOG = Logger.getLogger(ClientSimulator.class.getName());
	public String executePerSeconds(final String command, int seconds, final File file, final JsonObject... json) {
    	ExecutorService es = Executors.newSingleThreadExecutor();
        try {
            final Process process = Runtime.getRuntime()
                    .exec(command, null, file);
            
            Future<String> future = es.submit(new Callable<String>() {
                @Override
                public String call() throws Exception {
                    return execute(process, json);
                }
            });
            try {
                return future.get(seconds, TimeUnit.SECONDS);
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "ERROR", ex);
            } finally {
                es.shutdownNow();
                if (process != null && process.isAlive()) {
                    process.destroy();
                }
            }
        } catch (Exception ex) {
        	LOG.log(Level.SEVERE, "ERROR", ex);
        }
        return null;
    }

	private String execute(Process process, JsonObject... json) {
        String string = null;
        StringBuilder sb = new StringBuilder();
        try {
            BufferedWriter stdOutput = null;
            if (json.length > 0) {
                stdOutput = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
            }
            BufferedReader stdInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
            BufferedReader stdError = new BufferedReader(new InputStreamReader(process.getErrorStream()));

            if (json.length > 0 && stdOutput != null) {
                stdOutput.write(json[0].toString());
                stdOutput.flush();
            }

            while ((string = stdInput.readLine()) != null) {
                sb.append(string);
            }
            if (sb.length() > 0) {
                return sb.toString();
            }
            while ((string = stdError.readLine()) != null) {
                LOG.log(Level.SEVERE, "ERROR" + "{0}", string);
                return null;
            }
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "ERROR", e);
        }
        return null;
    }
}

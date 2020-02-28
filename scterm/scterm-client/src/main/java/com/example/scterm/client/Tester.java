package com.example.scterm.client;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Scanner;

public class Tester {
    private static Logger LOG = LoggerFactory.getLogger(Tester.class);

    private static final int NUM_CONNECTIONS = 50;

    private static long pid;

    private static String host;
    private static int port;

    public static void main(String[] args) throws IOException, InterruptedException {
        pid = ProcessHandle.current().pid();
        LOG.info("PID={}", pid);

        host = getEnv("SCTERM_HOST", "localhost");
        port = Integer.parseInt(getEnv("SCTERM_PORT", "7777"));

        if (args.length > 0) {
            long delay = Long.parseLong(args[0]);
            multiClients(delay);
        } else {
            prompt();
        }
    }

    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null ? defaultValue : value;
    }

    private static void multiClients(long delay) throws IOException, InterruptedException {
        SynchIsoClient[] clients = new SynchIsoClient[NUM_CONNECTIONS];

        for (int i = 0; i < NUM_CONNECTIONS; i++) {
            LOG.info("Connection #" + i);
            clients[i] = buildIsoClient();
            clients[i].connect();
        }
        LOG.info("All connected!");

        Thread.sleep(1000 * 2);

        LOG.info("Sending!");
        while (true) {
            for (int i = 0; i < NUM_CONNECTIONS; i++) {
                clients[i].sendAndWait(pid + "-" + i, "msg" + i);
                Thread.sleep(delay);
            }
        }
    }

    private static void prompt() throws InterruptedException, IOException {
        SynchIsoClient client = buildIsoClient();

        client.connect();

        Thread.sleep(500);

        readInput(client);

        client.close();
    }

    private static void readInput(SynchIsoClient client) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String value = scanner.nextLine();
            if ("q".equals(value)) {
                break;
            }
            client.sendAndWait(pid + "-0", value);
            Thread.sleep(100);
        }
    }

    private static SynchIsoClient buildIsoClient() throws IOException {
        SynchIsoClient client = new SynchIsoClient();
        client.setHostname(host);
        client.setPort(port);
        return client;
    }

}

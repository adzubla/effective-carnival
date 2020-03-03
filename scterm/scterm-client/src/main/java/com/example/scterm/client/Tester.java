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
            int numberClients = Integer.parseInt(args[0]);
            long delay = Long.parseLong(args[1]);
            multiClients(numberClients, delay);
        } else {
            prompt();
        }
    }

    private static String getEnv(String name, String defaultValue) {
        String value = System.getenv(name);
        return value == null ? defaultValue : value;
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

    private static void multiClients(int numberClients, long delay) throws InterruptedException {
        for (int i = 0; i < numberClients; i++) {
            Thread thread = new Thread(new Client(i, delay));
            thread.start();
        }

        LOG.info("All created!");
        Thread.sleep(1000 * 10000);
        LOG.info("Fim!");
    }

    public static class Client implements Runnable {
        private int num;
        private long delay;

        public Client(int i, long delay) {
            num = i;
            this.delay = delay;
        }

        @Override
        public void run() {
            LOG.info("Client #" + num);
            try {
                SynchIsoClient client = new SynchIsoClient();
                client.setHostname(host);
                client.setPort(port);
                client.connect();

                LOG.info("Sending!");
                while (true) {
                    client.sendAndWait(pid + "-" + num, "msg" + num);
                    Thread.sleep(delay);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

}

package com.example.scterm.client;

import com.github.kpavlov.jreactive8583.IsoMessageListener;
import com.solab.iso8583.IsoMessage;
import io.netty.channel.ChannelHandlerContext;
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
        IsoClient[] isoClients = new IsoClient[NUM_CONNECTIONS];

        for (int i = 0; i < NUM_CONNECTIONS; i++) {
            LOG.info("Connection #" + i);
            isoClients[i] = buildIsoClient();
            isoClients[i].connect();
        }
        LOG.info("All connected!");

        Thread.sleep(1000 * 2);

        LOG.info("Sending!");
        while (true) {
            for (int i = 0; i < NUM_CONNECTIONS; i++) {
                isoClients[i].sendMessage(pid + "-" + i, "msg" + i);
                Thread.sleep(delay);
            }
        }
    }

    private static void prompt() throws InterruptedException, IOException {
        IsoClient isoClient = buildIsoClient();

        isoClient.connect();

        Thread.sleep(500);

        readInput(isoClient);

        isoClient.close();
    }

    private static void readInput(IsoClient isoClient) throws InterruptedException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.print("> ");
            String value = scanner.nextLine();
            if ("q".equals(value)) {
                break;
            }
            isoClient.sendMessage(pid + "-0", value);
            Thread.sleep(100);
        }
    }

    private static IsoClient buildIsoClient() throws IOException {
        IsoClient isoClient = new IsoClient();
        isoClient.setHostname(host);
        isoClient.setPort(port);

        isoClient.setListener(new IsoMessageListener<>() {

            @Override
            public boolean applies(IsoMessage isoMessage) {
                LOG.debug("applies: {}", isoMessage);
                return isoMessage.getType() == 0x210;
            }

            @Override
            public boolean onMessage(ChannelHandlerContext channelHandlerContext, IsoMessage isoMessage) {
                LOG.debug("onMessage: {} {}", channelHandlerContext, isoMessage);
                LOG.info("id: {} request: {} response: {}", isoMessage.getField(41).getValue(),
                        isoMessage.getField(43).getValue(), isoMessage.getField(126).getValue());
                return false;
            }
        });
        return isoClient;
    }

}

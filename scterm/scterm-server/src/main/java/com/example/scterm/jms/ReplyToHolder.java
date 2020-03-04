package com.example.scterm.jms;

import org.springframework.stereotype.Component;

import javax.jms.Queue;

@Component
public class ReplyToHolder {

    private Queue replyToQueue;

    public Queue getReplyToQueue() {
        return replyToQueue;
    }

    public void setReplyToQueue(Queue replyToQueue) {
        this.replyToQueue = replyToQueue;
    }
}

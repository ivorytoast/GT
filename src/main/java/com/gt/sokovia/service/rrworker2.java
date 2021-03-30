package com.gt.sokovia.service;

import org.zeromq.SocketType;
import org.zeromq.ZContext;
import org.zeromq.ZMQ.Socket;

/*
You could use the "environments" section in the Dockerfile to assign the correct port...
 */

public class rrworker2 {
    public static void main(String[] args) throws Exception
    {
        try (ZContext context = new ZContext()) {
            //  Socket to talk to server
            Socket responder = context.createSocket(SocketType.REP);
            responder.connect("tcp://localhost:5560");

            while (!Thread.currentThread().isInterrupted()) {
                //  Wait for next request from client
                String string = responder.recvStr(0);
                System.out.printf("Received request: [%s]\n", string);

                //  Do some 'work'
//                Thread.sleep(1000);

                //  Send reply back to client
                responder.send("Cha queel: " + string);
            }
        }
    }
}

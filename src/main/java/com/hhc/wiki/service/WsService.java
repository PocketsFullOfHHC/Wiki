package com.hhc.wiki.service;

import com.hhc.wiki.websocket.WebSocketServer;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

@Service
public class WsService {

    @Resource
    public WebSocketServer webSocketServer;

    // 异步化的方法一定要单独写到一个类中
    @Async
    public void sendInfo(String message) {
        webSocketServer.sendInfo(message);
    }
}
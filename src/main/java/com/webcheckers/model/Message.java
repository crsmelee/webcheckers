package com.webcheckers.model;

/**
 * Created by Juna on 10/15/2017.
 */
public class Message {
    private String text;
    private enum type {INFO, ERROR}
    private type messageType;

    public Message(String messageText, type messageType){
        text = messageText;
        messageType = messageType;
    }
}
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.action.message.types;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class SMSMessage implements Message<String, String>, Serializable{
    private String refId;
    private String text;
    private String number;

    public SMSMessage() {
    }

    
    public SMSMessage(String refId, String text, String number) {
        this.refId = refId;
        this.text = text;
        this.number = number;
    }

    public String getNumber() {
        return number;
    }

    

    public String getText() {
        return text;
    }

    @Override
    public String toString() {
        return "SMSMessage{" + "refId=" + refId + ", text=" + text + ", number=" + number + '}';
    }

  

    public void setBody(String body) {
        this.text = body;
    }

    public void setHeader(String header) {
        this.number = header;
    }

    public String getBody() {
        return this.text;
    }

    public String getHeader() {
        return this.number;
    }

    public String getRefId() {
        return this.refId;
    }
    
    
    
    
}

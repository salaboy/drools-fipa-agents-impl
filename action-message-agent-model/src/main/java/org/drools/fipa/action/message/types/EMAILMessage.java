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
public class EMAILMessage implements Message<String, String>, Serializable {

    private String refId;
    private String body;
    private String subject;

    public EMAILMessage(String refId, String body, String subject) {
        this.refId = refId;
        this.body = body;
        this.subject = subject;
    }

    public EMAILMessage() {
    }

    
    
    public void setBody(String body) {
        this.body = body;
    }

    public void setHeader(String header) {
        this.subject = header;
    }

    public String getBody() {
        return this.body;
    }

    public String getHeader() {
        return this.subject;
    }

    public String getRefId() {
        return this.refId;
    }

    @Override
    public String toString() {
        return "EMAILMessage{" + "refId=" + refId + ", body=" + body + ", subject=" + subject + '}';
    }
    
    
}

/*
 * To change this template, choose Tools | Templates and open the template in
 * the editor.
 */
package org.drools.fipa.action.message.types;

/**
 *
 * @author salaboy
 */
public class AlertMessage implements Message{
    private String refId;
    private String header;
    private String body;

    public AlertMessage(String refId, String header, String body) {
        this.refId = refId;
        this.header = header;
        this.body = body;
    }
    

    public void setBody(Object body) {
        this.body = (String)body;
    }

    public void setHeader(Object header) {
        this.header = (String)header;
    }

    public Object getBody() {
        return this.body;
    }

    public Object getHeader() {
        return this.header;
    }

    public String getRefId() {
        return this.refId;
    }
    
}

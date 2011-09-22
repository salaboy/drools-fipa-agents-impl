/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.action.message;

import java.io.Serializable;

/**
 *
 * @author salaboy
 */
public class Channel implements Serializable {

    private String type;
    private String endpoint;

    public Channel() {
    }

    public Channel(String type) {
        this.type = type;
    }

    public Channel(String type, String endpoint) {
        this.type = type;
        this.endpoint = endpoint;
    }
    
   
    
    public String getEndpoint() {
        return endpoint;
    }

    public void setEndpoint(String endpoint) {
        this.endpoint = endpoint;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String toString() {
        return "Channel{" + "type=" + type + ", endpoint=" + endpoint + '}';
    }
    
    
}

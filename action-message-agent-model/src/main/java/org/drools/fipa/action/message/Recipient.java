/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.action.message;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author salaboy
 */
public class Recipient implements Serializable {
    
    private String recipientId;
    private String refId;
    private String externalReference;
    private List<String> channels;
    
    public Recipient() {
    }

    public Recipient(String refId, String id) {
        this.refId = refId;
        this.recipientId = id;
        this.channels = new ArrayList<String>();
    }

    public String getExternalReference() {
        return externalReference;
    }

    public void setExternalReference(String externalReference) {
        this.externalReference = externalReference;
    }

    public String getRecipientId() {
        return recipientId;
    }

    public void setRecipientId(String recipientId) {
        this.recipientId = recipientId;
    }

    public String getRefId() {
        return refId;
    }

    public void setRefId(String refId) {
        this.refId = refId;
    }

    public List<String> getChannels() {
        return channels;
    }

    public void setChannels(List<String> channels) {
        this.channels = channels;
    }
    
    public void addChannel(String channel){
        if(this.channels == null){
            this.channels = new ArrayList<String>();
        }
        this.channels.add(channel);
    }

    @Override
    public String toString() {
        return "Recipient{" + "recipientId=" + recipientId + ", refId=" + refId + ", externalReference=" + externalReference + ", channels=" + channels + '}';
    }

    
    
    

  
    
    
}

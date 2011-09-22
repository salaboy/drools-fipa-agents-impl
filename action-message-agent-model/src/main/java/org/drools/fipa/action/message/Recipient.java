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
public class Recipient implements Serializable {
    
    private String recipientId;
    private String refId;
    private String externalReference;

    public Recipient() {
    }

    public Recipient(String refId, String id) {
        this.refId = refId;
        this.recipientId = id;
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

    @Override
    public String toString() {
        return "Recipient{" + "recipientId=" + recipientId + ", refId=" + refId + ", externalReference=" + externalReference + '}';
    }
    
    
    

  
    
    
}

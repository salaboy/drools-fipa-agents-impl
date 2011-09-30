/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.action.message.invokers;

import java.io.Serializable;
import org.drools.fipa.action.message.types.EMAILMessage;

/**
 *
 * @author salaboy
 */
public class MockEMAILChannelInvoker implements ChannelInvoker<EMAILMessage>, Serializable {

    public String getType() {
        return "EMAIL";
    }

    public EMAILMessage invoke(String endpoint, EMAILMessage object) {
        System.out.println(" !!!! USING ENDPOINT  ="+endpoint);
        System.out.println(" !!!! SENDING EMAIL TO SUBJECT ="+object.getHeader());
        System.out.println(" !!!! TEXT  ="+object.getBody());
        
        return object;
    }
    
}

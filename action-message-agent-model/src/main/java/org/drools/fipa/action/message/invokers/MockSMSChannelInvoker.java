/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.action.message.invokers;

import java.io.Serializable;
import org.drools.fipa.action.message.types.SMSMessage;

/**
 *
 * @author salaboy
 */
public class MockSMSChannelInvoker implements ChannelInvoker<SMSMessage>, Serializable{

    public MockSMSChannelInvoker() {
    }

    
    public String getType() {
        return "SMS";
    }

    public SMSMessage invoke(String endpoint, SMSMessage object) {
        System.out.println(" !!!! USING ENDPOINT  ="+endpoint);
        System.out.println(" !!!! SENDING SMS TO ="+object.getNumber());
        System.out.println(" !!!! TEXT  ="+object.getBody());
        
        return object;
    }
    
}

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.action.message.invokers;

import java.io.Serializable;
import org.drools.fipa.action.message.Recipient;

/**
 *
 * @author salaboy
 */
public class MockIdentityChannelInvoker implements ChannelInvoker<Recipient>, Serializable{
    
    private String type;

    public MockIdentityChannelInvoker() {
        this.type = "IdentityChannelInvoker";
    }
    
    
    public Recipient invoke(String endpoint, Recipient recipient) {
        //DO a CALL TO THE REAL ENDPOINT 
        System.out.println("Invoking REAL ENDPOINT INSIDE THE Invoker -> "+endpoint);
        recipient.addChannel("EMAIL:salaboy@gmail.com");
        recipient.addChannel("SMS:5491148294893");
        return recipient;
    }

    public String getType() {
        return this.type;
    }
    
}

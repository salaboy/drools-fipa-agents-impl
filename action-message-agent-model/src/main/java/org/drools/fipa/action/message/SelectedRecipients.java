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
public class SelectedRecipients implements Serializable{    
    private List<Recipient> selectedRecipients;

    public SelectedRecipients() {
    }

    public List<Recipient> getSelectedRecipients() {
        return selectedRecipients;
    }

    public void setSelectedRecipients(List<Recipient> selectedRecipients) {
        this.selectedRecipients = selectedRecipients;
    }
    
    public void addRecipient(Recipient recipient){
        if(this.selectedRecipients == null){
            this.selectedRecipients = new ArrayList<Recipient>();
        }
        this.selectedRecipients.add(recipient);
    }
    
}

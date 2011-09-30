/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.fipa.action.message.types;

/**
 *
 * @author salaboy
 */
public interface Message<T, K> {
    public void setBody(T body);
    public void setHeader(K header);
    public T getBody();
    public K getHeader();
    public String getRefId();
}

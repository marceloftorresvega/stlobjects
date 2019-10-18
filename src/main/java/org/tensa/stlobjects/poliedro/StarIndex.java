/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tensa.stlobjects.poliedro;

import java.util.ArrayList;

/**
 *
 * @author mtorres
 */
public class StarIndex extends ArrayList<Integer> {
    private Integer centro;

    public StarIndex(Integer centro, int initialCapacity) {
        super(initialCapacity);
        this.centro = centro;
    }

    public Integer getCentro() {
        return centro;
    }
}

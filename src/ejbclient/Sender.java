/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ejbclient;

/**
 *
 * @author iskra
 */
public class Sender implements Runnable {

    private boolean stop = false;

    @Override
    public void run() {
        while (!stop) {
            long startTime = System.nanoTime();
            
            
            long estimatedTime = System.nanoTime() - startTime;

        }

    }

}

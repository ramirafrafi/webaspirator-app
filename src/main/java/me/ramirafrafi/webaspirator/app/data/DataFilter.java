/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.app.data;

import java.util.ArrayList;
import javafx.collections.ObservableList;
import me.ramirafrafi.dmanager.lib.State;
import me.ramirafrafi.webaspirator.app.model.Website;

/**
 *
 * @author Admin
 */
public class DataFilter {
    static public ArrayList<Website> loadAspiring(ObservableList<Website> allWebsites) {
        return loadDownloadsByStatus(allWebsites, State.DOWNLOADING, State.PENDING);
    }
    
    static public ArrayList<Website> loadFinished(ObservableList<Website> allWebsites) {
        return loadDownloadsByStatus(allWebsites, State.COMPLETE);
    }
    
    static public ArrayList<Website> loadUnfinished(ObservableList<Website> allWebsites) {
        return loadDownloadsByStatus(allWebsites, State.STOPPED, State.ERROR);
    }
    
    static public ArrayList<Website> loadDownloadsByStatus(ObservableList<Website> allWebsites, State... status) {
        ArrayList<Website> websites = new ArrayList<>();
        for(Website website: allWebsites) {
            for(State status_: status) {
                if(website.getWebsiteAspiration().getStatus() == status_) {
                    websites.add(website);
                    break;
                }
            }
        }
        
        return websites;
    }
}

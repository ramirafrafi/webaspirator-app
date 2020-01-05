/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.app.data;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import me.ramirafrafi.webaspirator.lib.WebsiteAspiration;
import me.ramirafrafi.webaspirator.app.WebAspiratorApp;
import me.ramirafrafi.webaspirator.app.model.Website;

/**
 *
 * @author Admin
 */
public class DataLoader {
    static public ArrayList<Website> loadWebsites() {
        String dirPath = 
                System.getenv("APPDATA") + File.separator + "WebAspirator";
        File dir = new File(dirPath);
        if(!dir.exists()) {
            dir.mkdir();
        }
        
        String filePath = dirPath + File.separator + "websites.txt";
        File file = new File(filePath);
        try {
            file.createNewFile();
        } catch (IOException ex) {
            WebAspiratorApp.alertError("Error writing to hard disk");
        }
        
        ArrayList<Website> websites = new ArrayList<>();
        String error = null;
        try {
            try (Scanner fileScanner = new Scanner(file)) {
                while(fileScanner.hasNext()) {
                    String line = fileScanner.nextLine();
                    String[] line_ = line.split(" ");
                    try {
                        WebsiteAspiration waspiration;
                        waspiration = new WebsiteAspiration(line_[0],
                                Integer.parseInt(line_[1]), WebAspiratorApp.DOWNLOAD_DIR);
                        Website website = new Website(waspiration);
                        if(line_.length > 2 && line_[2].equals("COMPLETE")) {
                            website.setFinished(true);
                        }
                        websites.add(website);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
        } catch (FileNotFoundException ex) {
            error = filePath + " not found";
        } finally {
            if(null != error) {
                WebAspiratorApp.alertError(error);
            }
        }
        
        return websites;
    }
}

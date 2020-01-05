/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.app.data;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import me.ramirafrafi.webaspirator.app.model.Website;

/**
 *
 * @author Admin
 */
public class DataManager {
    static public void saveWebsite(Website website) throws IOException {
        String dirPath = System.getenv("APPDATA") + File.separator + "WebAspirator";
        String filePath = dirPath + File.separator + "websites.txt";
        try (FileWriter writer = new FileWriter(filePath, true)) {
            String line = website.getUrl() + " " + website.getDepth() + "\n";
            writer.append(line);
        }
    }
    
    static public void deleteWebsite(Website website) throws FileNotFoundException, IOException {
        String dirPath = System.getenv("APPDATA") + File.separator + "WebAspirator";
        String filePath = dirPath + File.separator + "websites.txt";
        String lines;
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            lines = "";
            while(null != (line = reader.readLine())) {
                String[] line_ = line.split(" ");
                if(!line_[0].equals(website.getUrl())) {
                    lines += line + "\n";
                }
            }
        }
        
        try (FileWriter writer = new FileWriter(filePath)) {
            writer.write(lines);
        }
    }
}

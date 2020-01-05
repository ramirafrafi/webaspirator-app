/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package me.ramirafrafi.webaspirator.app.model;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import org.asynchttpclient.Response;
import me.ramirafrafi.dmanager.lib.FileDownload;
import me.ramirafrafi.dmanager.lib.State;
import me.ramirafrafi.webaspirator.lib.WebsiteAspiration;
import me.ramirafrafi.webaspirator.lib.WebAspirationListener;

/**
 *
 * @author Admin
 */
public class Website {
    private final SimpleStringProperty url = new SimpleStringProperty(null);
    private final SimpleIntegerProperty depth = new SimpleIntegerProperty(-1);
    private final SimpleStringProperty status = new SimpleStringProperty(null);
    private final SimpleBooleanProperty finished = new SimpleBooleanProperty(false);
    private final SimpleIntegerProperty submitted = new SimpleIntegerProperty(0);
    private final SimpleIntegerProperty downloaded = new SimpleIntegerProperty(0);
    private final SimpleStringProperty downloadedRate = new SimpleStringProperty(null);
    private final SimpleIntegerProperty errors = new SimpleIntegerProperty(0);
    
    private final WebsiteAspiration websiteAspiration;

    public Website(WebsiteAspiration aspiration) {
        websiteAspiration = aspiration;
        url.set(aspiration.getUrl().toString());
        depth.set(aspiration.getDepth());
        status.set(aspiration.getStatus().toString());
        downloadedRate.bind(Bindings.concat(downloaded, " /", submitted));
        
        websiteAspiration.setListener(new WebAspirationListener() {
            @Override
            public void onAspire(WebsiteAspiration waspiration) {
                status.set(waspiration.getStatus().toString());
            }

            @Override
            public void onCompleted(WebsiteAspiration waspiration, State status_) {
                status.set(status_.toString());
                if(status_ == State.COMPLETE) {
                    finished.set(true);
                }
            }

            @Override
            public void onError(WebsiteAspiration waspiration) {
                status.set(waspiration.getStatus().toString());
            }

            @Override
            public void onDownloadSubmitted(WebsiteAspiration waspiration, FileDownload download) {
                status.set(waspiration.getStatus().toString());
                submitted.set(submitted.get() + 1);
            }

            @Override
            public void onDownloadCompleted(WebsiteAspiration waspiration, FileDownload download, State status_, Response response) {
                status.set(waspiration.getStatus().toString());
                downloaded.set(downloaded.get() + 1);
            }

            @Override
            public void onDownloadError(WebsiteAspiration waspiration, FileDownload download) {
                status.set(waspiration.getStatus().toString());
                errors.set(errors.get() + 1);
            }

            @Override
            public void onHangon(WebsiteAspiration waspiration) {
                status.set(waspiration.getStatus().toString());
            }
        });
    }
    
    public WebsiteAspiration getWebsiteAspiration() {
        return websiteAspiration;
    }
    
    public String getUrl() {
        return url.get();
    }
    
    public int getDepth() {
        return depth.get();
    }

    public boolean isFinished() {
        return finished.get();
    }
    
    public void setFinished(boolean finished) {
        this.finished.set(finished);
    }
    
    public SimpleStringProperty statusProperty() {
        return status;
    }
    
    public SimpleBooleanProperty finishedProperty() {
        return finished;
    }
    
    public SimpleIntegerProperty submittedProperty() {
        return submitted;
    }
    
    public SimpleIntegerProperty downloadedProperty() {
        return downloaded;
    }
    
    public SimpleIntegerProperty errorsProperty() {
        return errors;
    }
    
    public SimpleStringProperty downloadedRateProperty() {
        return downloadedRate;
    }
}

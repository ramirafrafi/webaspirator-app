package me.ramirafrafi.webaspirator.app;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.concurrent.Worker;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.text.Text;
import org.apache.commons.validator.routines.UrlValidator;
import me.ramirafrafi.dmanager.lib.State;
import me.ramirafrafi.dmanager.lib.StatefulRunnable;
import me.ramirafrafi.webaspirator.lib.AspirationManager;
import me.ramirafrafi.webaspirator.lib.WebsiteAspiration;
import me.ramirafrafi.webaspirator.app.data.DataFilter;
import me.ramirafrafi.webaspirator.app.data.DataLoader;
import me.ramirafrafi.webaspirator.app.data.DataManager;
import me.ramirafrafi.webaspirator.app.model.Website;

public class FXMLController implements Initializable {
    static public AspirationManager aspirationManager = new AspirationManager();
    
    @FXML
    private Button aspiringBtn;
    
    @FXML
    private Button finishedBtn;
    
    @FXML
    private Button unfinishedBtn;
    
    @FXML
    private ProgressIndicator loadingIndicator;
    
    @FXML
    private TableView<Website> websitesTable;
    
    @FXML
    private Button resumeBtn;
    
    @FXML
    private Button stopBtn;
    
    @FXML
    private Button deleteBtn;
    
    @FXML
    private TextField urlTxt;
    
    @FXML
    private TextField depthTxt;
    
     @FXML
    private Text titleTxt;
    
    private Button active;
    private final SimpleBooleanProperty loading = new SimpleBooleanProperty(false);
    private ObservableList<Website> websites = FXCollections.observableArrayList();
    private final ObservableList<Website> allWebsites = FXCollections.observableArrayList();
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        active = aspiringBtn;
        loadingIndicator.visibleProperty().bind(loading);
        setupWebsitesTable();
        loadWebsites();
        loadRows();
    }
    
    @FXML
    public void topMenuBtnClicked(ActionEvent event) {
        Button source = (Button)event.getSource();
        if (active == source) {
            return;
        }
        
        active.getStyleClass().remove("active");
        source.getStyleClass().add("active");
        active = source;
        
        changeTitle();
        loadRows();
    }
    
    @FXML
    public void newWebsite(ActionEvent event) throws MalformedURLException, IOException {
        loading.set(true);
        
        if(!(new UrlValidator()).isValid(urlTxt.getText())) {
            WebAspiratorApp.alertError("Invalid or unsupported URL format");
        } else {
            int depth = -1;
            try {
                depth = Integer.parseInt(depthTxt.getText());
            } catch(NumberFormatException ex) {
                WebAspiratorApp.alertError("Aspiration depth must be a valid integer number");
            }
            
            final int depth_ = depth;
            if(depth_ > 0) {
                final Task<Website> task = new Task<Website>() {
                    @Override
                    protected Website call() throws Exception {
                        for(StatefulRunnable waspiration: aspirationManager.getAspirations()) {
                            if((new URL(urlTxt.getText())).equals(((WebsiteAspiration) waspiration).getUrl())) {
                                return null;
                            }
                        }

                        WebsiteAspiration waspiration = new WebsiteAspiration(urlTxt.getText(), 
                            depth_, WebAspiratorApp.DOWNLOAD_DIR);
                        Website website = new Website(waspiration);
                        DataManager.saveWebsite(website);
                        allWebsites.add(website);
                        return website;
                    }
                };
                task.setOnRunning((WorkerStateEvent e) -> {
                    loadRows();
                });
                task.setOnSucceeded((WorkerStateEvent event1) -> {
                    urlTxt.setText("");
                    depthTxt.setText("");
                    
                    Website website = task.getValue();
                    if(null == website) {
                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setTitle("Information");
                        alert.setContentText("Trying to aspire an already aspired URL?");
                        alert.setHeaderText("");
                        alert.show();
                    } else {
                        if(active == aspiringBtn) {
                            websites.add(website);
                        }
                        aspirationManager.newTask(website.getWebsiteAspiration(), true);
                    }
                    
                    loading.set(false);
                });

                Thread th = new Thread(task);
                th.setDaemon(true);
                th.start();
                loading.set(true);
            }
        }
        loading.set(false);
    }
    
    @FXML
    public void resumeAspiration(ActionEvent event) {
        Website website = (Website) websitesTable.getSelectionModel().getSelectedItem();
        websitesTable.getSelectionModel().clearSelection();
        
        aspirationManager.resumeTask(website.getWebsiteAspiration());
    }
    
    @FXML
    public void stopAspiration(ActionEvent event) {
        Website website = (Website) websitesTable.getSelectionModel().getSelectedItem();
        websitesTable.getSelectionModel().clearSelection();
        
        aspirationManager.stopTask(website.getWebsiteAspiration());
    }
    
    @FXML
    public void deleteWebsite(ActionEvent event) {
        final Website website = (Website) websitesTable.getSelectionModel().getSelectedItem();
        websitesTable.getSelectionModel().clearSelection();

        final Task<Object> task = new Task<Object>() {
            @Override
            protected Object call() throws Exception {
                aspirationManager.removeTask(website.getWebsiteAspiration());
                websites.remove(website);
                allWebsites.remove(website);
                DataManager.deleteWebsite(website);
                website.getWebsiteAspiration().deleteFiles();

                return null;
            }
        };
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void setupWebsitesTable() {
        TableColumn<Website, ?> nameColumn = websitesTable.getColumns().get(0);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("url"));
        
        TableColumn<Website, ?> statusColumn = websitesTable.getColumns().get(1);
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("downloadedRate"));
        
        TableColumn<Website, ?> speedColumn = websitesTable.getColumns().get(2);
        speedColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        websitesTable.getSelectionModel().selectedItemProperty().addListener((ObservableValue<? extends Website> observable, Website oldValue, Website newValue) -> {
            if(null == newValue) {
                resumeBtn.setDisable(true);
                stopBtn.setDisable(true);
                deleteBtn.setDisable(true);
                return;
            }
            
            Website website = (Website) newValue;
            State aspirationStatus = website.getWebsiteAspiration().getStatus();
            switch (aspirationStatus) {
                case STOPPED:
                case ERROR:
                    resumeBtn.setDisable(false);
                    stopBtn.setDisable(true);
                    deleteBtn.setDisable(false);
                    break;
                case PENDING:
                case DOWNLOADING:
                    resumeBtn.setDisable(true);
                    stopBtn.setDisable(false);
                    deleteBtn.setDisable(true);
                    break;
                case COMPLETE:
                    resumeBtn.setDisable(true);
                    stopBtn.setDisable(true);
                    deleteBtn.setDisable(false);
                    break;
            }
        });
    }

    private void loadWebsites() {
        loading.set(true);
        
        final Task<ArrayList<Website>> task = new Task<ArrayList<Website>>() {
            @Override
            protected ArrayList<Website> call() throws Exception {
                return DataLoader.loadWebsites();
            }
        };
        task.setOnSucceeded((WorkerStateEvent event) -> {
            websites = FXCollections.observableArrayList(task.getValue());
            for (Website website: websites) {
                allWebsites.add(website);
                aspirationManager.newTask(website.getWebsiteAspiration(), false);
            }
            loading.set(false);
        });
        task.setOnFailed((WorkerStateEvent event) -> {
            Logger.getLogger(FXMLController.class.getName()).log(Level.SEVERE, null, task.getException());
        });
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void loadRows() {
        loading.set(true);
        
        Task<ArrayList<Website>> task = new Task<ArrayList<Website>>() {
            @Override
            protected ArrayList<Website> call() throws Exception {
                return new ArrayList<>();
            }
        };
        if (active == aspiringBtn) {
            task = new Task<ArrayList<Website>>() {
                @Override
                protected ArrayList<Website> call() throws Exception {
                    return DataFilter.loadAspiring(allWebsites);
                }
            };
        } else if (active == finishedBtn) {
            task = new Task<ArrayList<Website>>() {
                @Override
                protected ArrayList<Website> call() throws Exception {
                    return DataFilter.loadFinished(allWebsites);
                }
            };
        } else if (active == unfinishedBtn) {
            task = new Task<ArrayList<Website>>() {
                @Override
                protected ArrayList<Website> call() throws Exception {
                    return DataFilter.loadUnfinished(allWebsites);
                }
            };
        }

        task.setOnSucceeded((WorkerStateEvent event) -> {
            @SuppressWarnings("unchecked")
            Worker<ArrayList<Website>> task1 = event.getSource();
            websites = FXCollections.observableArrayList(task1.getValue());
            websitesTable.setItems(websites);
            loading.set(false);
        });
        Thread th = new Thread(task);
        th.setDaemon(true);
        th.start();
    }

    private void changeTitle() {
        if (active == aspiringBtn) {
            titleTxt.setText("Currently aspiring");
        } else if (active == finishedBtn) {
            titleTxt.setText("Finished websites");
        } else if (active == unfinishedBtn) {
            titleTxt.setText("Unfinished websites");
        }
    }
}

package me.ramirafrafi.webaspirator.app;

import java.io.File;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.stage.Stage;


public class WebAspiratorApp extends Application {
    static final public String DOWNLOAD_DIR = 
            System.getProperty("user.home") + File.separator + "Downloads"
            + File.separator + "WebAspirator Websites";
    
    @Override
    public void start(Stage stage) throws Exception {
        setup();
        
        Parent root = FXMLLoader.load(getClass().getResource("/fxml/Scene.fxml"));
        
        Scene scene = new Scene(root);
        scene.getStylesheets().add("/styles/Styles.css");
        
        stage.setTitle("WebAspirator");
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void stop() throws Exception {
        FXMLController.aspirationManager.shutdown();
        super.stop();
    }
    
    

    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
    
    static public void alertError(final String text) {
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("");
            alert.setContentText(text);
            alert.show();
        });
    }

    private void setup() {
        File dir = new File(DOWNLOAD_DIR);
        if(!dir.exists()) {
            dir.mkdir();
        }
    }

}

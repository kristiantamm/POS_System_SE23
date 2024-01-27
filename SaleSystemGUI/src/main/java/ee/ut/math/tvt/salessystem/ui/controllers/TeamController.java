package ee.ut.math.tvt.salessystem.ui.controllers;
import ee.ut.math.tvt.salessystem.ui.SalesSystemUI;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class TeamController implements Initializable {
    private static final Logger log = LogManager.getLogger(SalesSystemUI.class);
    @FXML
    private Label teamNameLabel;

    @FXML
    private Label contactPersonLabel;

    @FXML
    private Label teamMembersLabel;

    @FXML
    private ImageView teamLogoImageView;

    public TeamController() {}

    private void refreshTeamTab(){
        Properties properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            properties.load(input);
        } catch (IOException e) {
            log.error("Error loading team information from properties file");
            e.printStackTrace();
        }

        teamNameLabel.setText(properties.getProperty("teamName"));
        contactPersonLabel.setText(properties.getProperty("teamContactPerson"));
        teamMembersLabel.setText(properties.getProperty("teamMembers"));

        String logoPath = properties.getProperty("teamLogo");
        Image logoImage = new Image(logoPath);
        teamLogoImageView.setImage(logoImage);
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        log.info("Team tab opened");
        refreshTeamTab();
    }
}

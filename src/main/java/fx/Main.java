package fx;

import fx.model.Person;
import fx.model.PersonListWrapper;
import fx.view.PersonEditDialogController;
import fx.view.PersonOverviewController;
import fx.view.RootLayoutController;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;
import java.util.prefs.Preferences;

public class Main extends Application {

    private Stage primaryStage;
    private BorderPane rootLayout;
    private ObservableList<Person> personData = FXCollections.observableArrayList();
    private static Properties properties = new Properties();

    /* Пути к страницам */
    private static final String ROOT_LAYOUT_PATH = "/RootLayout.fxml";
    private static final String PERSON_SCENE_PATH = "/PersonOverview.fxml";
    private static final String PERSON_EDIT_SCENE_PATH = "/PersonEditDialog.fxml";

    /* Url иконок stage */
    private static final String RESOURCES_PATH = "src/main/resources/";
    private static final String PRIMARY_STAGE_ICON_URL = "file:" + RESOURCES_PATH + "images/baseline_menu_book_black_18dp.png";
    private static final String EDIT_DIALOG_ICON_URL = "file:" + RESOURCES_PATH + "images/baseline_person_add_black_18dp.png";

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public BorderPane getRootLayout() {
        return rootLayout;
    }

    public ObservableList<Person> getPersonData() {
        return personData;
    }

    public static void main(String[] args) {
        try {
            InputStream inputStream = new FileInputStream("src/main/resources/config.properties");
            properties.load(inputStream);

            launch(args);
        } catch (Exception e) {
            System.err.println(">>> start exception");
            e.printStackTrace();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        this.primaryStage.setTitle("Persons App");
        /* Иконка приложения */
        this.primaryStage.getIcons().add(new Image(PRIMARY_STAGE_ICON_URL));

        initRootLayout();
        loadData();
        showPersonOverview();

        this.primaryStage.show();
    }

    private void initRootLayout() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(ROOT_LAYOUT_PATH));
            rootLayout = loader.load();

            Scene scene = new Scene(rootLayout);
            primaryStage.setScene(scene);

            RootLayoutController controller = loader.getController();
            controller.setMainApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadData() {
        if (Boolean.parseBoolean(properties.getProperty("data.random"))) {
            int number = Integer.parseInt(properties.getProperty("data.random.number"));
            personData.clear();
            personData.addAll(Person.generateRandomPersons(number));
        } else {
            // Try to load last opened person file.
            File file = getPersonFilePath();
            if (file != null) {
                loadPersonDataFromFile(file);
            } else {
                System.out.println("No persons data found, generated artificially");
                initPersonsData();
            }
        }
    }

    private void showPersonOverview() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(PERSON_SCENE_PATH));
            AnchorPane personOverview = loader.load();

            rootLayout.setCenter(personOverview);
            PersonOverviewController controller = loader.getController();
            controller.setMainApp(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initPersonsData() {
        personData.add(new Person("Hans", "Muster"));
        personData.add(new Person("Ruth", "Mueller"));
        personData.add(new Person("Heinz", "Kurz"));
        personData.add(new Person("Cornelia", "Meier"));
        personData.add(new Person("Werner", "Meyer"));
        personData.add(new Person("Lydia", "Kunz"));
        personData.add(new Person("Anna", "Best"));
        personData.add(new Person("Stefan", "Meier"));
        personData.add(new Person("Martin", "Mueller"));
    }

    /**
     * Opens a dialog to edit details for the specified person. If the user
     * clicks OK, the changes are saved into the provided person object and true
     * is returned.
     *
     * @param person the person object to be edited
     * @return true if the user clicked OK, false otherwise.
     */
    public boolean showPersonEditDialog(Person person) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(Main.class.getResource(PERSON_EDIT_SCENE_PATH));
            AnchorPane editDialog = loader.load();

            // Create the dialog Stage.
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Edit Person");
            dialogStage.getIcons().add(new Image(EDIT_DIALOG_ICON_URL));
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(primaryStage);
            Scene scene = new Scene(editDialog);
            dialogStage.setScene(scene);

            // Set the person into the controller.
            PersonEditDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setPerson(person);

            // Show the dialog and wait until the user closes it
            dialogStage.showAndWait();

            return controller.isOkClicked();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Returns the person file preference, i.e. the file that was last opened.
     * The preference is read from the OS specific registry. If no such
     * preference can be found, null is returned.
     */
    public File getPersonFilePath() {
        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        String filePath = preferences.get("filePath", null);
        if (filePath != null) {
            return new File(filePath);
        } else {
            return null;
        }
    }

    /**
     * Sets the file path of the currently loaded file. The path is persisted in the OS specific registry.
     *
     * @param file the file or null to remove the path
     */
    public void setPersonFilePath(File file) {
        Preferences preferences = Preferences.userNodeForPackage(Main.class);
        if (file != null) {
            preferences.put("filePath", file.getPath());
            primaryStage.setTitle("Persons App - " + file.getName());
        } else {
            preferences.remove("filePath");
            primaryStage.setTitle("Persons App");
        }
    }

    /**
     * Loads person data from the specified file. The current person data will be replaced.
     */
    public void loadPersonDataFromFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();

            // Reading XML from the file and unmarshalling.
            PersonListWrapper wrapper = (PersonListWrapper) unmarshaller.unmarshal(file);

            personData.clear();
            personData.addAll(wrapper.getPersons());

            // Save the file path to the registry.
            setPersonFilePath(file);
        } catch (Exception e) {
            showErrorDialog("Could not load data", "Could not load data from file:\n" + file.getPath());
        }
    }

    /**
     * Saves the current person data to the specified file.
     */
    public void savePersonDataToFile(File file) {
        try {
            JAXBContext context = JAXBContext.newInstance(PersonListWrapper.class);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

            // Wrapping our person data.
            PersonListWrapper wrapper = new PersonListWrapper();
            wrapper.setPersons(personData);

            // Marshalling and saving XML to the file.
            marshaller.marshal(wrapper, file);

            // Save the file path to the registry.
            setPersonFilePath(file);
        } catch (Exception e) {
            showErrorDialog("Could not save data", "Could not save data to file:\n" + file.getPath());
        }
    }

    private void showErrorDialog(String header, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(header);
        alert.setContentText(message);
        alert.showAndWait();
    }
}

package ntut.IR;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import ntut.IR.exception.NoThisDataSetNameException;
import ntut.IR.exception.NoThisMethodException;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.function.Consumer;

public class MainController extends Observable implements Observer{
    //GUI Constants
    private String TEXT_FILL_DEFAULT = "#F00";

    //Data
    private Stage mStage = null;
    private Model mModel = new Model();
    private List<String> mSupportingList = new ArrayList<>();

    //GUI Component
    @FXML
    private Label mDataSetDirectoryLabel;
    @FXML
    private ComboBox<String> mSupportingDataSetsComboBox;
    @FXML
    private VBox mDataSetSettingVBox;
    @FXML
    private ComboBox<String> mSupportClassificationMethodsComboBox;
    @FXML
    private Label mReportStoreLocationLabel;
    @FXML
    private Button mStartButton;

    //GUI Data
    private String mDataSetDirectoryLabelText = "請先選擇 Data Set 資料夾位置 (File > Select Data Set Folder)";
    private Paint mDataSetDirectoryLabelTextFill = Paint.valueOf(TEXT_FILL_DEFAULT);
    private String mReportStoreLocationLabelText = "請選擇一個資料夾來存放報告(此資料夾除了存放資料，亦會存放索引檔案)";
    private Node currentDataSetGUINode = null;

    @FXML
    private void ClickCloseMenuItem(){
        mStage.close();
    }

    @FXML
    private void ClickSelectDataSetLocationMenuItem(){
        File location = showDirectoryChooser();
        if(location != null) {
            String SUCCESS_COLOR = "#000";
            String AUTO_REPORT_SAVE_DIR = "Report";
            this.mModel.setDataSetLocation(location.getAbsolutePath());
            if(this.mModel.getReportStoreLocation().isEmpty()) {
                String autoGeneratedReportSaveDir = location.getAbsolutePath()+ File.separator + AUTO_REPORT_SAVE_DIR;
                this.mModel.setReportStoreLocation(autoGeneratedReportSaveDir);
                this.setReportStoreLocationLabelText(autoGeneratedReportSaveDir);
            }
            this.setDataSetDirectoryLabelText(this.mModel.getDataSetLocation());
            this.setDataSetDirectoryLabelTextFill(Paint.valueOf(SUCCESS_COLOR));
        }
    }

    private File showDirectoryChooser() {
        String DIRECTORY_CHOOSER_TITLE = "Choose a Directory";
        String PWD = ".";
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle(DIRECTORY_CHOOSER_TITLE);
        directoryChooser.setInitialDirectory(new File(PWD));
        return directoryChooser.showDialog(mStage);
    }

    @FXML
    private void ClickAboutMenuItem(){
        String HEADER_TEXT = "Information Retrieval : Classification";
        String TITLE = "About";
        String CONTENT = "Authors:\n\t 104598013 孫季加\n\t 104598050 黃仲毅\n";
        Alert aboutAlert = new Alert(Alert.AlertType.INFORMATION);
        aboutAlert.setHeaderText(HEADER_TEXT);
        aboutAlert.setTitle(TITLE);
        aboutAlert.setContentText(CONTENT);
        aboutAlert.show();
    }

    @FXML
    private void ChangeDataSetComboBoxOption() throws IOException{
        this.mModel.getSupportingDataSetList().forEach(new Consumer<String>() {
            @Override
            public void accept(String s) {
                if(s.equals(mSupportingDataSetsComboBox.getValue())){
                    String fxmlName = mModel.getFXMLName(s);
                    if(currentDataSetGUINode != null)
                        mDataSetSettingVBox.getChildren().remove(currentDataSetGUINode);
                    try {
                        FXMLLoader loader = new FXMLLoader(this.getClass().getClassLoader().getResource(fxmlName));
                        mDataSetSettingVBox.getChildren().add(loader.load());
                        mModel.setSelectDataSetName(s);
                    }catch (IOException|NoThisDataSetNameException exception){
                        showExceptionAlert(exception);
                    }
                }
            }
        });
        checkReady();
    }

    @FXML
    private void ClickBrowseStoreReportLocationButton(){
        File location = this.showDirectoryChooser();
        if(location != null){
            this.mModel.setReportStoreLocation(location.getAbsolutePath());
            this.setReportStoreLocationLabelText(this.mModel.getReportStoreLocation());
        }
    }

    @FXML
    private void ClickStartButton(){

    }

    @FXML
    private void HoverOnStartButton(){

    }

    private void makeChange() {
        this.setChanged();
        this.notifyObservers();
        this.checkReady();
    }

    private void checkReady(){
        if(this.mModel.isReady()){
            this.setStartButtonDisabled(false);
        }else {
            this.setStartButtonDisabled(true);
        }
    }

    public void setStartButtonDisabled(boolean mStartButtonDisabled) {
        this.mStartButton.setDisable(mStartButtonDisabled);
    }

    public void setThisStage(Stage stage){
        mStage = stage;
        mStage.setMaxHeight(mStage.getHeight());
        mStage.setMinHeight(mStage.getHeight());
        mStage.setMaxWidth(mStage.getWidth());
        mStage.setMinWidth(mStage.getWidth());
        mStage.setIconified(false);
    }

    public String getDataSetDirectoryLabelText() {
        return mDataSetDirectoryLabelText;
    }

    public void setDataSetDirectoryLabelText(String mDataSetDirectoryLabelText) {
        this.mDataSetDirectoryLabelText = mDataSetDirectoryLabelText;
        this.makeChange();
    }

    public Paint getDataSetDirectoryLabelTextFill() {
        return mDataSetDirectoryLabelTextFill;
    }

    public void setDataSetDirectoryLabelTextFill(Paint mDataSetDirectoryLabelTextFill) {
        this.mDataSetDirectoryLabelTextFill = mDataSetDirectoryLabelTextFill;
        this.makeChange();
    }

    public String getReportStoreLocationLabelText() {
        return mReportStoreLocationLabelText;
    }

    public void setReportStoreLocationLabelText(String mReportStoreLocationLabelText) {
        this.mReportStoreLocationLabelText = mReportStoreLocationLabelText;
        this.makeChange();
    }

    @Override
    public void update(Observable o, Object arg) {
        this.mDataSetDirectoryLabel.setText(this.getDataSetDirectoryLabelText());
        this.mDataSetDirectoryLabel.setTextFill(this.getDataSetDirectoryLabelTextFill());
        this.mReportStoreLocationLabel.setText(this.getReportStoreLocationLabelText());
    }

    public void initialize(){
        this.addObserver(this);
        try {
            this.mModel.init();
        }catch (IOException exception){
            showExceptionAlert(exception);
        }

        //Support Data Sets
        this.mSupportingDataSetsComboBox.setItems(FXCollections.observableList(this.mModel.getSupportingDataSetList()));
        //Support Classification Methods
        this.mSupportClassificationMethodsComboBox.setItems(FXCollections.observableList(this.mModel.getSupportClassificationMethodList()));
        this.mSupportClassificationMethodsComboBox.valueProperty().set(this.mModel.getSupportClassificationMethodList().get(0));
        try {
            this.mModel.setSelectedClassificationMethod(this.mModel.getSupportClassificationMethodList().get(0));
        }catch (NoThisMethodException exception){
            showExceptionAlert(exception);
        }

        this.mStartButton.setDisable(true);

        this.makeChange();
    }

    private void showExceptionAlert(Exception exception) {
        String ALERT_TITLE = "Error";
        String ALERT_HEADER = exception.getClass().getName();
        Alert ioErrorAlert = new Alert(Alert.AlertType.ERROR);
        ioErrorAlert.setTitle(ALERT_TITLE);
        ioErrorAlert.setHeaderText(ALERT_HEADER);
        ioErrorAlert.setContentText(exception.getLocalizedMessage());
    }
}

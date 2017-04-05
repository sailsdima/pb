package com.company.view;

import com.company.Main;
import com.company.SocketClient;
import com.company.listeners.SocketListener;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import com.company.model.Deposit;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

import static com.company.Constants.*;

public class Controller {

    @FXML
    TableView<Deposit> tableDepositInfo;

    @FXML
    TableColumn<Deposit, String> columnName;
    @FXML
    TableColumn<Deposit, String> columnCountry;
    @FXML
    TableColumn<Deposit, String> columnType;
    @FXML
    TableColumn<Deposit, String> columnDepositor;
    @FXML
    TableColumn<Deposit, String> columnAccountId;
    @FXML
    TableColumn<Deposit, String> columnAmountOnDeposit;
    @FXML
    TableColumn<Deposit, String> columnProfitability;
    @FXML
    TableColumn<Deposit, String> columnTimeConstraints;

    @FXML
    TextField textFieldBankName;
    @FXML
    TextField textFieldCountry;

    @FXML
    ComboBox<String> comboBoxType;

    @FXML
    TextField textFieldDepositor;
    @FXML
    TextField textFieldAccountId;
    @FXML
    TextField textFieldAmountOnDeposit;
    @FXML
    TextField textFieldProfitability;
    @FXML
    TextField textFieldTimeConstraints;
    @FXML
    Button buttonList;
    @FXML
    Button buttonSum;
    @FXML
    Button buttonCount;
    @FXML
    Button buttonInfoAccount;
    @FXML
    Button buttonInfoDepositor;
    @FXML
    Button buttonShowType;
    @FXML
    Button buttonShowBank;
    @FXML
    Button buttonAdd;
    @FXML
    Button buttonDelete;
    @FXML
    Button buttonSendRequest;
    @FXML
    Button buttonReconnect;
    @FXML
    TextField textFieldCommand;
    @FXML
    TextArea textAreaServerResponse;
    @FXML
    Label labelError;
    @FXML
    Label labelServerStatus;


    Main mainApp;
    SocketClient socket;

    public Controller() {
        System.out.println(this.getClass());
    }

    @FXML
    private void initialize() {

        initializeTableColumns();

        ObservableList<String> options =
                FXCollections.observableArrayList(
                        "demand",
                        "urgent",
                        "term",
                        "savings",
                        "metallic"
                );
        comboBoxType.getItems().addAll(options);

        textFieldCommand.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                onFocusCommandField();
            }
        });

        socket = new SocketClient(new FXSocketListener(), "localhost", 1555);
        socket.connect();
        System.out.println("socket initialized");

    }

    private void initializeTableColumns() {
        columnName.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getName()));

        columnCountry.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getCountry()));

        columnDepositor.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getDepositor()));

        columnType.setCellValueFactory(
                cellData -> new SimpleStringProperty(cellData.getValue().getType()));

        columnAccountId.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAccountId())));

        columnAmountOnDeposit.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getAmountOnDeposit())));

        columnProfitability.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getProfitability())));

        columnTimeConstraints.setCellValueFactory(
                cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getTimeConstraints())));
    }

    private void setTableData(List<Deposit> deposits) {
        ObservableList<Deposit> obDeposits = FXCollections.observableArrayList(deposits);
        tableDepositInfo.setItems(obDeposits);
    }

    private void setTableData(Deposit deposit) {
        List<Deposit> dd = new ArrayList<>();
        dd.add(deposit);
        setTableData(dd);
    }

    private void setMessage(String msg) {
        textAreaServerResponse.setText(msg);
    }

    public void setMainApp(Main mainApp) {
        this.mainApp = mainApp;
    }

    private Deposit createNewDeposit() {

        try {
            String name = textFieldBankName.getText();
            String type = comboBoxType.getSelectionModel().getSelectedItem();
            String depositor = textFieldDepositor.getText();
            String country = textFieldCountry.getText();

            if(name.length() < 1 || type.length() < 1 || depositor.length() < 1 || country.length() < 1){
                labelError.setText("Please, fill in all the fields.");
                return null;
            }

            long amountOnDeposit = Long.valueOf(textFieldAmountOnDeposit.getText());
            int id = Integer.valueOf(textFieldAccountId.getText());
            double profitability = Integer.valueOf(textFieldProfitability.getText());
            int timeConstraints = Integer.valueOf(textFieldTimeConstraints.getText());

            labelError.setText("");
            return new Deposit(name, country, type, depositor, id, amountOnDeposit, profitability, timeConstraints);
        } catch (NullPointerException e) {
            labelError.setText("Please, fill in all the fields.");
        } catch (NumberFormatException e){
            labelError.setText("Please, enter numbers correctly.");
        }
        return null;
    }

    private void onFocusCommandField() {
        String holder = textFieldCommand.getPromptText();

        if (!holder.equals(COMMAND)) {
            int lastPos = (holder.indexOf("<")) > 0 ? (holder.indexOf("<")) : holder.length();
            textFieldCommand.setText(holder.substring(0, lastPos));
        }

        textFieldCommand.setPromptText(COMMAND);
        textFieldCommand.positionCaret(textFieldCommand.getText().length());

    }

    private void setStatus(boolean isConnected) {
        if (isConnected) {
            labelServerStatus.setText("connected to server successfully");
            labelServerStatus.setTextFill(Color.GREEN);

        } else {
            labelServerStatus.setText("can not connect to server");
            labelServerStatus.setTextFill(Color.RED);
        }
        buttonReconnect.setVisible(!isConnected);
    }

    @FXML
    private void onButtonReconnectClick() {
        System.out.println("reconnect button");

        if (!socket.checkIsConnected())
            socket.connect();

    }

    @FXML
    private void onButtonSendRequestClick() {
        String command = textFieldCommand.getText().trim();

        System.out.println("COMMAND = " + command);
        if (command.equalsIgnoreCase(COMMAND_ADD)) {
            socket.sendRequest(command, createNewDeposit());
        } else {
            socket.sendRequest(textFieldCommand.getText());
        }
        textAreaServerResponse.setText("");
    }

    @FXML
    private void onButtonListClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_LIST + " ");
    }

    @FXML
    private void onButtonCountClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_COUNT + " ");
    }

    @FXML
    private void onButtonSumClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_SUM + " ");
    }

    @FXML
    private void onButtonShowTypeClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_SHOW_TYPE + " <deposit type>");
    }

    @FXML
    private void onButtonShowBankClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_SHOW_BANK + " <bank name>");
    }

    @FXML
    private void onButtonInfoAccountClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_INFO_ACCOUNT + " <account id>");
    }

    @FXML
    private void onButtonInfoDepositorClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_INFO_DEPOSITOR + " <depositor`s name>");
    }

    @FXML
    private void onButtonAddClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_ADD);
    }

    @FXML
    private void onButtonDeleteClick() {
        textFieldCommand.setText("");
        textFieldCommand.setPromptText(COMMAND_DELETE + " <account id>");
    }


    class FXSocketListener implements SocketListener {

        @Override
        public void onUpdateTable(List<Deposit> deposits) {
            setTableData(deposits);
        }

        @Override
        public void onUpdateTable(Deposit deposit) {
            setTableData(deposit);
        }

        @Override
        public void onMessageReceived(String msg) {
            setMessage(msg);
        }

        @Override
        public void connectionStatusChanged(boolean isConnected) {
            setStatus(isConnected);
        }
    }
}

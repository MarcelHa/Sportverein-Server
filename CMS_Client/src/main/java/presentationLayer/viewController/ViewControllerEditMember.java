package presentationLayer.viewController;

import applicationLayer.MemberHandler;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import presentationLayer.CareTaker;
import presentationLayer.SceneController;
import rmi.dto.PersonDTO;
import rmi.dto.RoleDTO;
import utilities.UtilDate;

import javax.management.relation.Role;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.sql.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.ResourceBundle;


public class ViewControllerEditMember extends SceneController implements Initializable {

    @FXML
    private TextField firstName, lastName, ssn;
    @FXML
    private DatePicker birthday;
    @FXML
    private TableView<RoleDTO> _availableRoles;
    @FXML
    private TableView<RoleDTO> _attachedRoles;

    private List<RoleDTO> _availableRolesList;
    private ObservableList<RoleDTO> _availableRolesObservableList;
    private ObservableList<RoleDTO> _attachedRolesObservableList;
    private List<RoleDTO> _attachedRolesList = new LinkedList<RoleDTO>();
    private MemberHandler _memberHandler = new MemberHandler();


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            _availableRolesList = _memberHandler.getAllRoles();
        } catch (RemoteException | NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        PersonDTO mementoDTO = CareTaker.getFirst();
        initTextField(mementoDTO);
        CareTaker.clear();

        try {
            mementoDTO.setRoleDTOList(_memberHandler.getRolesFromPersonDto(mementoDTO));
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NotBoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        _attachedRolesList = mementoDTO.getRoleDTOList();

        for (RoleDTO role:_attachedRolesList) {
            _availableRolesList.remove(role);
        }

        _availableRoles.refresh();
        _attachedRoles.refresh();


        for (RoleDTO roleDTO:_availableRolesList) {
            for (RoleDTO roleDTOMemento:mementoDTO.getRoleDTOList()) {
                if (roleDTOMemento.getRoleId() == roleDTO.getRoleId()) {
                    _availableRolesList.remove(roleDTO);
                }
            }
        }

        if (_attachedRolesList == null) {
            _attachedRolesList = new LinkedList<RoleDTO>();
        }

        _availableRolesObservableList = FXCollections.observableList(_availableRolesList);
        _attachedRolesObservableList = FXCollections.observableList(_attachedRolesList);
        _availableRoles.setItems(_availableRolesObservableList);
        _attachedRoles.setItems(_attachedRolesObservableList);
    }


    /*
    Simple Dashboard Navigation
     */
    @FXML
    public void switchToMember(ActionEvent actionEvent) throws IOException {
        super.switchScene(actionEvent, "member.fxml");
    }
    @FXML
    public void switchToNewMember(ActionEvent actionEvent) throws IOException {
        super.switchScene(actionEvent, "newMember.fxml");
    }
    @FXML
    public void switchToClub(ActionEvent actionEvent) throws IOException {
        super.switchScene(actionEvent, "club.fxml");
    }
    @FXML
    public void switchToEvent(ActionEvent actionEvent) throws IOException {
        super.switchScene(actionEvent, "event.fxml");
    }
    @FXML
    public void switchToHome(ActionEvent actionEvent) throws IOException {
        super.switchScene(actionEvent, "home.fxml");
    }
    @FXML
    public void switchToResult(ActionEvent actionEvent) throws IOException {
        super.switchScene(actionEvent, "result.fxml");
    }
    @FXML
    public void switchToMemberList(ActionEvent actionEvent) throws IOException {
        super.switchScene(actionEvent, "memberList.fxml");
    }


    /*
    Presentation Layer Logic
     */
    @FXML
    public void addRole() {
        if (_availableRolesList.remove(_availableRoles.getSelectionModel().getSelectedItem())) {
            _attachedRolesList.add(_availableRoles.getSelectionModel().getSelectedItem());
        }
        _availableRoles.refresh();
        _attachedRoles.refresh();
    }
    @FXML
    public void deleteRole() {
        if (_attachedRolesList.remove(_attachedRoles.getSelectionModel().getSelectedItem())) {
            _availableRolesList.add(_attachedRoles.getSelectionModel().getSelectedItem());
        }
        _availableRoles.refresh();
        _attachedRoles.refresh();
    }
    @FXML
    public void updateMember(ActionEvent actionEvent) throws IOException, NotBoundException {
        super.switchScene(actionEvent, "member.fxml");
        PersonDTO person = new PersonDTO();
        person.setFirstName(firstName.getText());
        person.setLastName(lastName.getText());
        person.setDateOfBirth(toDate(birthday));
        person.setSocialSecurityNumber(ssn.getText());
        person.setRoleDTOList(_attachedRolesList);
        _memberHandler.updateMember(person);
    }

    //Date Converter
    private Date toDate(DatePicker datePicker) {
        return UtilDate.convertToSQLDate(datePicker.getValue());
    }

    private void initTextField(PersonDTO mementoDTO){
        firstName.setText(mementoDTO.getFirstName());
        lastName.setText(mementoDTO.getLastName());
        //TODO
        birthday.setValue(UtilDate.convertToLocalDate(mementoDTO.getDateOfBirth()));
        //birthday.set(mementoDTO.getFirstName());
        ssn.setText(mementoDTO.getSocialSecurityNumber());
    }

}






package org.example;

import com.vaadin.flow.component.crud.BinderCrudEditor;
import com.vaadin.flow.component.crud.Crud;
import com.vaadin.flow.component.crud.CrudEditor;
import com.vaadin.flow.component.crud.CrudI18n;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.HeaderRow;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.converter.StringToIntegerConverter;
import com.vaadin.flow.router.Route;
import org.example.Entity.PersonInformations;
import org.example.Services.MyCrudService;

import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Route
public class MainView extends VerticalLayout {

    TextField name = new TextField("Name");
    TextField street = new TextField("Street");
    TextField city = new TextField("City");
    TextField country = new TextField("Country");
    TextField email = new TextField("E-Mail");
    TextField phone = new TextField("Phone");
    TextField id = new TextField("Phone");

    MyCrudService myCrudService = new MyCrudService();
    static ConcurrentHashMap<Integer, PersonInformations> data = new ConcurrentHashMap<>();
    static AtomicInteger atomicIntegerId = new AtomicInteger(0);

    Binder<PersonInformations> binder = new Binder<>(PersonInformations.class);
    Grid<PersonInformations> grid = new Grid<>();
    Crud<PersonInformations> crud = new Crud<>(PersonInformations.class, createGrid(grid), createCrudEditor());

    private static boolean changeFlag = true;

    public MainView() throws SQLException {

        // myCrudService.createTestData();

        if (changeFlag) {
            myCrudService.setMaxAtomicID(atomicIntegerId);
            myCrudService.fetchData(data);
            changeFlag = false;
        }

        CrudI18n customI18n = CrudI18n.createDefault();
        customI18n.setEditItem("Update Customer");
        customI18n.setNewItem("New Customer");

        binder.setValidatorsDisabled(false);

        //LISTENERS

        crud.addEditListener(personInformationsEditEvent -> binder.setValidatorsDisabled(true));

        crud.addNewListener(personInformationsNewEvent -> {
            binder.setValidatorsDisabled(false);
            binder.forField(phone).withValidator(v -> myCrudService.isPhoneUnique(data, v), "Phone Must Be Unique").bind("phone");
        });

        crud.addCancelListener(personInformationsCancelEvent -> binder.setValidatorsDisabled(true));

        crud.addSaveListener(saveEvent -> {
            binder.setValidatorsDisabled(true);
            PersonInformations toSave = saveEvent.getItem();
            // Save the item to memory
            myCrudService.addPerson(atomicIntegerId,toSave,data);

            grid.setItems(data.values());
        });

        crud.addDeleteListener(deleteEvent -> {
            // Delete the item in the database
            PersonInformations toDelete = deleteEvent.getItem();
            myCrudService.deletePerson(toDelete,data);
          //  data.remove(deleteEvent.getItem().getId());
            grid.setItems(data.values());
        });

        grid.setItems( data.values());
        add(crud);
    }

    private CrudEditor<PersonInformations> createCrudEditor() {

        name.setRequiredIndicatorVisible(true);
        phone.setRequiredIndicatorVisible(true);

        FormLayout form = new FormLayout(name, street, city, email, country, phone);

        binder.forField(id).withConverter(
                new StringToIntegerConverter(0, "integers only")).bind(PersonInformations::getId, PersonInformations::setId);
        binder.bind(name, PersonInformations::getName, PersonInformations::setName);
        binder.forField(name).asRequired("required!").bind("name");
        binder.bind(street, PersonInformations::getStreet, PersonInformations::setStreet);
        binder.bind(city, PersonInformations::getCity, PersonInformations::setCity);
        binder.bind(email, PersonInformations::getEmail, PersonInformations::setEmail);
        binder.bind(country, PersonInformations::getCountry, PersonInformations::setCountry);
        binder.bind(phone, PersonInformations::getPhone, PersonInformations::setPhone);
        binder.forField(phone).asRequired("required!").bind("phone");

        return new BinderCrudEditor<>(binder, form);
    }

    private Grid<PersonInformations> createGrid(Grid<PersonInformations> grid) {

        grid.addColumn(PersonInformations::getName).setHeader("Name").setKey("Name")
                .setWidth("160px").setComparator(PersonInformations::getName);
        grid.addColumn(PersonInformations::getPhone).setHeader("Phone").setKey("Phone");

        configureFilter(grid, data);

        Crud.addEditColumn(grid);

        return grid;
    }

    public void configureFilter(Grid<PersonInformations> grid, ConcurrentHashMap<Integer, PersonInformations> data) {

        HeaderRow filterRow = grid.appendHeaderRow();

        TextField nameFilter = new TextField();
        TextField phoneFilter = new TextField();

        //Name Filter

        nameFilter.setSizeFull();
        nameFilter.setPlaceholder("Filter");
        nameFilter.setClearButtonVisible(true);
        nameFilter.getElement().setAttribute("focus-target", "");

        //Phone Filter

        phoneFilter.setSizeFull();
        phoneFilter.setPlaceholder("Filter");
        phoneFilter.setClearButtonVisible(true);
        phoneFilter.getElement().setAttribute("focus-target", "");

        //Adding filters to grid
        filterRow.getCell(grid.getColumnByKey("Name")).setComponent(nameFilter);
        filterRow.getCell(grid.getColumnByKey("Phone")).setComponent(phoneFilter);

        nameFilter.addValueChangeListener(e -> filterColumns(grid, data, nameFilter, phoneFilter));
        phoneFilter.addValueChangeListener(e -> filterColumns(grid, data, nameFilter, phoneFilter));

    }

    private void filterColumns(Grid<PersonInformations> grid, ConcurrentHashMap<Integer, PersonInformations> data, TextField nameFilter, TextField phoneFilter) {

        String q1 = nameFilter.getValue();
        String q2 = phoneFilter.getValue();

        if (!q1.equals("") && q2.equals(""))
            grid.setItems(data.values().stream().filter(c -> c.getName().equalsIgnoreCase(nameFilter.getValue())));

        if (q1.equals("") && !q2.equals(""))
            grid.setItems(data.values().stream().filter(c -> c.getPhone().equalsIgnoreCase(phoneFilter.getValue())));

        if (!q1.equals("") && !q2.equals(""))
            grid.setItems(data.values().stream().filter(c -> c.getName().equalsIgnoreCase(nameFilter.getValue()) && c.getPhone().equalsIgnoreCase(phoneFilter.getValue())));

        if (q1.equals("") && q2.equals(""))
            grid.setItems(data.values());


    }



}



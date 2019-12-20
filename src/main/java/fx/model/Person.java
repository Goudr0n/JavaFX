package fx.model;

import javafx.beans.property.*;
import util.XmlDateAdapter;

import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

public class Person {

    private static final Random RANDOM = new Random();
    private static final String DATE_DELIMITER = ".";
    public static final SimpleDateFormat DATE_FORMATTER = new SimpleDateFormat(String.format("dd%1$sMM%1$syyyy", DATE_DELIMITER));

    private static int idCounter = 0;
    private final IntegerProperty id;
    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty street;
    private final IntegerProperty postalCode;
    private final StringProperty city;
    private final ObjectProperty<Date> birthday;

    public Person() {
        this(null, null);
    }

    public Person(String firstName, String lastName) {
        this.id = new SimpleIntegerProperty(idCounter++);
        this.firstName = new SimpleStringProperty(firstName);
        this.lastName = new SimpleStringProperty(lastName);

        // Какие-то фиктивные начальные данные для удобства тестирования.
        this.street = new SimpleStringProperty("какая-то улица");
        this.postalCode = new SimpleIntegerProperty(123456);
        this.city = new SimpleStringProperty("какой-то город");
        Date birthday = null;
        try {
            birthday = generateRandomBirthday();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        this.birthday = new SimpleObjectProperty<>(birthday);
    }

    private Date generateRandomBirthday() throws ParseException {
        int month = RANDOM.nextInt(12) + 1;
        String monthString = month < 10 ? "0" + month : String.valueOf(month);
        int day;
        /* Если февраль */
        if (month == 2) {
            day = RANDOM.nextInt(28) + 1;
        } else {
            day = RANDOM.nextInt(30) + 1;
        }
        String dayString = day < 10 ? "0" + day : String.valueOf(day);
        int year = 1950 + RANDOM.nextInt(70);
        return DATE_FORMATTER.parse(dayString + DATE_DELIMITER + monthString + DATE_DELIMITER + year);
    }

    public int getId() {
        return id.get();
    }

    public IntegerProperty idProperty() {
        return id;
    }

    public void setId(int id) {
        this.id.set(id);
    }

    public String getFirstName() {
        return firstName.get();
    }

    public StringProperty firstNameProperty() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName.set(firstName);
    }

    public String getLastName() {
        return lastName.get();
    }

    public StringProperty lastNameProperty() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName.set(lastName);
    }

    public String getStreet() {
        return street.get();
    }

    public StringProperty streetProperty() {
        return street;
    }

    public void setStreet(String street) {
        this.street.set(street);
    }

    public int getPostalCode() {
        return postalCode.get();
    }

    public IntegerProperty postalCodeProperty() {
        return postalCode;
    }

    public void setPostalCode(int postalCode) {
        this.postalCode.set(postalCode);
    }

    public String getCity() {
        return city.get();
    }

    public StringProperty cityProperty() {
        return city;
    }

    public void setCity(String city) {
        this.city.set(city);
    }

    @XmlJavaTypeAdapter(XmlDateAdapter.class)
    public Date getBirthday() {
        return birthday.get();
    }

    public ObjectProperty<Date> birthdayProperty() {
        return birthday;
    }

    public void setBirthday(Date birthday) {
        this.birthday.set(birthday);
    }

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id.get() +
                ", firstName=" + firstName.get() +
                ", lastName=" + lastName.get() +
                '}';
    }

}

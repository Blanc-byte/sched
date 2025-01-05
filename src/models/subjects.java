import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class subjects {
    private final StringProperty id;
    private final StringProperty code;
    private final StringProperty description;

    // Constructor
    public subjects(String id, String code, String description) {
        this.id = new SimpleStringProperty(id);
        this.code = new SimpleStringProperty(code);
        this.description = new SimpleStringProperty(description);
    }

    // Getter and Setter for ID
    public String getId() {
        return id.get();
    }

    public void setId(String id) {
        this.id.set(id);
    }

    public StringProperty idProperty() {
        return id;
    }

    // Getter and Setter for Code
    public String getCode() {
        return code.get();
    }

    public void setCode(String code) {
        this.code.set(code);
    }

    public StringProperty codeProperty() {
        return code;
    }

    // Getter and Setter for Description
    public String getDescription() {
        return description.get();
    }

    public void setDescription(String description) {
        this.description.set(description);
    }

    public StringProperty descriptionProperty() {
        return description;
    }
}

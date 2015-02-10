package no.difi.datahotel.model;

import javax.xml.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@XmlRootElement(name = "datasetFields")
@XmlAccessorType(XmlAccessType.NONE)
public class Fields {

    @XmlElementWrapper(name = "fields")
    @XmlElement(name = "field")
    private List<Field> fields = new ArrayList<Field>();

    public void setFields(List<Field> fields) {
        this.fields = fields;
    }

    public List<Field> getFields() {
        return fields;
    }
}
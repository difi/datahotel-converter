package no.difi.datahotel.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * The class creates a Field object that represent the fields in the dataset, in
 * XML format
 */
@XmlAccessorType(XmlAccessType.NONE)
@XmlRootElement
public class Field {
    @XmlElement
    private String name;

    @XmlElement
    private String shortName;

    @XmlElement
    private boolean groupable = false;

    @XmlElement
    private boolean searchable = false;

    @XmlElement
    private boolean indexPrimaryKey = false;

    @XmlElement
    private String content;

    private String defShort;

    public Field() {

    }

    public Field(String shortName, boolean searchable) {
        this.shortName = shortName;
        this.searchable = searchable;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean getGroupable() {
        return groupable;
    }

    public void setGroupable(boolean groupable) {
        this.groupable = groupable;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String metadata) {
        this.content = metadata;
    }

    public void setSearchable(boolean searchable) {
        this.searchable = searchable;
    }

    public boolean getSearchable() {
        return searchable;
    }

    public void setIndexPrimaryKey(boolean indexPrimaryKey) {
        this.indexPrimaryKey = indexPrimaryKey;
    }

    public boolean getIndexPrimaryKey() {
        return indexPrimaryKey;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getShortName() {
        return shortName;
    }
}
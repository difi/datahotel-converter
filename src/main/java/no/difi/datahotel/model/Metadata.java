package no.difi.datahotel.model;

import javax.xml.bind.annotation.XmlRootElement;

/**
 * Metadata is an unit in the "file system" available to users, and can be a
 * dataset or a folder.
 */
@XmlRootElement
public class Metadata {

    private boolean active = true;

    // Values for users
    private String name;
    private String description;
    private String url;
    private Long updated;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Long getUpdated() {
        return updated;
    }

    public void setUpdated(Long updated) {
        this.updated = updated;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }
}
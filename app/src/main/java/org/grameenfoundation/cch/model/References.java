package org.grameenfoundation.cch.model;

/**
 * Created by Software Developer on 26-May-16.
 */
public class References {
    private String id;
    private String reference_name;
    private String reference_size;
    private String reference_shortname;
    private String reference_url;

    public References(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getReference_name() {
        return reference_name;
    }

    public void setReference_name(String reference_name) {
        this.reference_name = reference_name;
    }

    public String getReference_size() {
        return reference_size;
    }

    public void setReference_size(String reference_size) {
        this.reference_size = reference_size;
    }

    public String getReference_shortname() {
        return reference_shortname;
    }

    public void setReference_shortname(String reference_shortname) {
        this.reference_shortname = reference_shortname;
    }

    public String getReference_url() {
        return reference_url;
    }

    public void setReference_url(String reference_url) {
        this.reference_url = reference_url;
    }
}

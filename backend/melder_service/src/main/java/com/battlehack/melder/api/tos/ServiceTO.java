package com.battlehack.melder.api.tos;

/**
 * Created by aakhmerov on 20.06.15.
 */
public class ServiceTO {

    private String name;
    private String id;
    private String href;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getHref() {
        return href;
    }
}

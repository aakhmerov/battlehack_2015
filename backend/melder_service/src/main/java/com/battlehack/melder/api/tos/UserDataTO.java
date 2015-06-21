package com.battlehack.melder.api.tos;

import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by aakhmerov on 20.06.15.
 *
 *   date: ["monday", "tuesday", "wednesday", "thursday", "friday", "saturday"],
 email: "hans@wurst.us",
 firstname: "Hans",
 lastname: "Wurst",
 phone: "0133713371337",
 time: "10,18,
 zipcodes: ["12098", "12239", "13337"]

 */
@XmlRootElement
public class UserDataTO {
    private Long id;
    private String email;
    private String lastName;
    private String firstName;
    private String phone;
    private String time;
    private List<String> zipCodes;
    private List <String> days;
    private String serviceId;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public List<String> getZipCodes() {
        return zipCodes;
    }

    public void setZipCodes(List<String> zipCodes) {
        this.zipCodes = zipCodes;
    }

    public List<String> getDays() {
        return days;
    }

    public void setDays(List<String> days) {
        this.days = days;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }
}

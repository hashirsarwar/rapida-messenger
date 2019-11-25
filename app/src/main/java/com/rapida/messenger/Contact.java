package com.rapida.messenger;

public class Contact implements Comparable<Contact> {

    private String name;
    private String phone;
    private boolean isChecked;

    public Contact(String name, String phone, boolean isChecked) {
        this.name = name;
        this.phone = phone;
        this.isChecked = isChecked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public boolean isChecked() {
        return isChecked;
    }

    public void setChecked(boolean checked) {
        isChecked = checked;
    }


    @Override
    public int compareTo(Contact o) {
        return this.getName().compareTo(o.getName());
    }
}

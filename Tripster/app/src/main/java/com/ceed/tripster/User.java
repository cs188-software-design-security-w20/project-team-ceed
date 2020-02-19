package com.ceed.tripster;

public class User {
    private String _display_name;
    private String _email;
    private String _profile_photo;


    public User(String display_name, String email, String profile_photo) {
        this._display_name = display_name;
        this._email = email;
        this._profile_photo = profile_photo;
    }

    public String get_display_name() {
        return _display_name;
    }

    public void set_display_name(String _display_name) {
        this._display_name = _display_name;
    }

    public String get_email() {
        return _email;
    }

    public void set_email(String _email) {
        this._email = _email;
    }

    public String get_profile_photo() {
        return _profile_photo;
    }

    public void set_profile_photo(String _profile_photo) {
        this._profile_photo = _profile_photo;
    }
}
